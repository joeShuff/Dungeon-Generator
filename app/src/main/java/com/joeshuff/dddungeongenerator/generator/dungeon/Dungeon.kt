package com.joeshuff.dddungeongenerator.generator.dungeon

import android.graphics.Point
import com.joeshuff.dddungeongenerator.generator.dungeon.Environment.ENVIRONMENT_TYPE
import com.joeshuff.dddungeongenerator.generator.dungeon.History.HISTORY
import com.joeshuff.dddungeongenerator.generator.dungeon.Purpose.PURPOSE
import com.joeshuff.dddungeongenerator.generator.features.StairsFeature
import com.joeshuff.dddungeongenerator.generator.floors.DungeonSection
import com.joeshuff.dddungeongenerator.generator.floors.Floor
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree
import com.joeshuff.dddungeongenerator.generator.monsters.Bestiary
import com.joeshuff.dddungeongenerator.screens.create.GeneratingActivity
import com.joeshuff.dddungeongenerator.util.Logs
import java.util.*

class Dungeon {

    companion object {
        @Transient var MAP_SIZE = 800

        fun stringToSeed(s: String?): Long {
            if (s == null) {
                return 0
            }
            var hash: Long = 0
            for (c in s.toCharArray()) {
                hash = 31L * hash + c.toLong()
            }
            return hash
        }
    }

    var name: String = ""
        private set

    private var startX = 0
    private var startY = 0
    private var endX = 0
    private var endY = 0
    var width = 0
    var height = 0

    var selectedEnvironment: ENVIRONMENT_TYPE? = null
        private set

    var dungeonCreator: Creator? = null
        private set

    var dungeonPurpose: PURPOSE? = null
        private set

    var dungeonHistory: HISTORY? = null
        private set

    private var globalModifier = Modifier()
    private var userModifier = Modifier()

    @Transient
    lateinit var rnd: Random

    private var seed = ""
    private var dungeonFloors: ArrayList<Floor> = ArrayList()

    //Generation all complete
    @Transient
    var allCompleted = false

    //	HashMap<Integer, Giffer> floorGifs = new HashMap<>();
    //	Giffer fainGiffer = new Giffer(0);

    @Transient
    var activity: GeneratingActivity? = null

    constructor(): this(null, 0, 0, 0, 0, "")

    constructor(c: GeneratingActivity?, startX: Int, startY: Int, endX: Int, endY: Int, seed: String) {
        activity = c

        setSeed(seed)

        this.startX = startX
        this.startY = startY
        this.endX = endX
        this.endY = endY

        width = endX - startX
        height = endY - startY

        Bestiary.launchBestiary(c)
    }

    fun getDungeonDescription(): String {
        var description = ""

        selectedEnvironment?.let {
            description += "You find yourself in a dungeon ${it.description.toLowerCase()} .\n"
        }

        dungeonCreator?.let {creator ->
            if (creator.creatorType == Creator.CREATOR.NO_CREATOR) {
                description += "You discover that this dungeon is naturally formed."
            } else {
                dungeonPurpose?.let { purpose ->
                    description += "This dungeon seems to be created by ${creator.getDescription()} \n"
                    description += "They created this dungeon as a ${purpose.title.toLowerCase()}\n ${purpose.description} \n"
                }
            }
        }

        return description
    }

    fun setUserModifier(modifier: Modifier) {
        userModifier = modifier
    }

    fun setSeed(seed: String) {
        var numberseed = ""
        for (c in seed.toCharArray()) {
            val cVal = c.toInt()
            numberseed += cVal
        }
        this.seed = seed
        rnd = Random(stringToSeed(numberseed))
        Logs.i("Dungeon", "seed is : $seed", null)
    }

    fun setRoomSize(percentage: Int) {
        Room.changeRoomSize(percentage / 100f)
    }

    fun setLongCorridors(longCorridors: Boolean) {
        MinSpanningTree.setLongCorridors(longCorridors)
    }

    fun setLinearProgression(linearProgression: Boolean) {
        DungeonSection.linearProgression = linearProgression
    }

    fun getSeed(): String {
        return seed
    }

    fun generateAttributes() {
        rnd?.let {randomness ->
            selectedEnvironment = Environment.generateEnvironmentType(randomness)
            dungeonCreator = Creator.generateCreator(randomness)

            val modifiers: ArrayList<Modifier> = ArrayList()
            modifiers.add(userModifier)

            selectedEnvironment?.modifier?.let { modifiers.add(it) }
            dungeonCreator?.creatorType?.modifier?.let { modifiers.add(it) }

            if (dungeonCreator?.creatorType != Creator.CREATOR.NO_CREATOR) {
                dungeonPurpose = Purpose.getPurpose(randomness)
                dungeonPurpose?.modifier?.let { modifiers.add(it) }
                dungeonHistory = History.getHistory(randomness)
                dungeonHistory?.modifier?.let { modifiers.add(it) }
            }

            globalModifier = Modifier.combineModifiers(modifiers, true)
        }

        name = NameGenerator.generateName(this)
    }

    fun generate() {
        generateAttributes()

        val firstFloor = Floor(this, rnd, 0)
        firstFloor.fillFloor()
//        firstFloor.splitFloor();
        dungeonFloors.add(firstFloor)

        branchOut()

        calculateNearestPartner()

        calculateRejectedRooms()

        triangulate()

        minSpanningTree()

        combinePaths()

        clearUnnecessaryData()

        pathFind()

        finalise()

        complete()
    }

    fun addFloorForLevel(level: Int): Floor {
        val theFloor = dungeonFloors.firstOrNull { floor: Floor -> floor.level == level }
        if (theFloor != null) return theFloor

        val newFloor = Floor(this, rnd, level)
        dungeonFloors.add(newFloor)
        return newFloor
    }

    val lowestDungeonFloor: Floor?
        get() {
            return getDungeonFloors().minBy { it.level }
        }

    val highestDungeonFloor: Floor?
        get() {
            return getDungeonFloors().maxBy { it.level }
        }

    fun getDungeonFloorAtLevel(level: Int): Floor? {
        return getDungeonFloors().firstOrNull { it.level == level }
    }

    private fun branchOut() {
        activity?.setProgressText("Generating Rooms...")
        getDungeonFloors().forEach {
            Logs.i("Dungeon", "generating rooms for floor ${it.level}", null)
            it.branchOut()
        }
    }

    private fun calculateNearestPartner() {
        activity?.setProgressText("Spreading out Rooms...")
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.calculateNearestPartner()
            }
        }
    }

    private fun calculateRejectedRooms() {
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.calculateRejectedRooms()
            }
        }
    }

    private fun triangulate() {
        activity?.setProgressText("Triangulating Rooms...")
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.triangulate()
            }
        }
    }

    private fun minSpanningTree() {
        activity?.setProgressText("Finding best corridors...")
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.minSpanningTree()
            }
        }
    }

    private fun combinePaths() {
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.combinePaths()
            }
        }
    }

    private fun pathFind() {
        activity?.setProgressText("Connecting Rooms...")
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.pathFind()
            }
        }
    }

    private fun finalise() {
        activity?.setProgressText("Generation Complete")
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.finalise()
            }

            //Renumber all the rooms so that they are actually sequentially numbered
            var i = 1
            for (r in floor.allRooms) {
                r.id = i
                i++
            }
        }
    }

    private fun clearUnnecessaryData() {
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.clearUnnecessaryData()
            }
        }
    }

    fun complete() {
        allCompleted = true
        val stairsToDecide: ArrayList<StairsFeature> = ArrayList()
        getDungeonFloors().forEach { floor ->
            floor.allRooms.forEach { room ->
                room.getFeatureList().forEach { feature ->
                    if (feature is StairsFeature) {
                        stairsToDecide.add(feature)
                    }
                }
            }
        }

        for (sf in stairsToDecide) sf.getConnectedRoom() //The get initialises the connection, not best practice but dismissable for now
        activity?.runOnUiThread { activity?.onCompleted() }
    }

    fun getRoomAt(floor: Int, x: Int, y: Int): Room? {
        getDungeonFloorAtLevel(floor)?.allRooms?.forEach {room ->
            if (room.containsGlobalPoint(Point(x, y))) return room
        }
        return null
    }

    fun getDungeonFloors(): List<Floor> {
        return dungeonFloors
    }

    fun getGlobalModifier() = globalModifier
}