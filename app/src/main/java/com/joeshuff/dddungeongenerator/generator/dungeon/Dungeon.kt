package com.joeshuff.dddungeongenerator.generator.dungeon

import com.joeshuff.dddungeongenerator.db.models.Point
import com.joeshuff.dddungeongenerator.generator.dungeon.Environment.ENVIRONMENT_TYPE
import com.joeshuff.dddungeongenerator.generator.dungeon.History.HISTORY
import com.joeshuff.dddungeongenerator.generator.dungeon.Purpose.PURPOSE
import com.joeshuff.dddungeongenerator.generator.features.StairsFeature
import com.joeshuff.dddungeongenerator.generator.floors.DungeonSection
import com.joeshuff.dddungeongenerator.generator.floors.Floor
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration
import com.joeshuff.dddungeongenerator.screens.home.DungeonHistoryItem
import com.joeshuff.dddungeongenerator.util.Logs
import io.reactivex.ObservableEmitter
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

open class Dungeon(
        @PrimaryKey var id: Int
): RealmObject() {
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

    private var name: String = ""

    private var startX = 0
    private var startY = 0
    private var endX = 0
    private var endY = 0
    var width = 0
    var height = 0

    private var environment: String? = null
    var selectedEnvironment: ENVIRONMENT_TYPE?
        get() { return environment?.let { ENVIRONMENT_TYPE.valueOf(it) }?: null }
        set(new) { new?.let { environment = it.name } }

    var dungeonCreator: Creator? = null
        private set

    private var purpose: String? = null
    var dungeonPurpose: PURPOSE?
        get() { return purpose?.let { PURPOSE.valueOf(it) }?: null }
        set(new) { new?.let { purpose = it.name } }

    private var history: String? = null
    var dungeonHistory: HISTORY?
        get() { return history?.let { HISTORY.valueOf(it) }?: null }
        set(new) { new?.let { history = it.name } }


    private var globalModifier = Modifier()
    private var userModifier = Modifier()

    @Ignore
    lateinit var rnd: Random

    private var seed = ""

    private var dungeonFloors: RealmList<Floor> = RealmList()

    //Generation all complete
    var allCompleted = false

    constructor(): this((System.currentTimeMillis() / 1000).toInt(), 0, 0, 0, 0, "")

    constructor(startX: Int, startY: Int, endX: Int, endY: Int, seed: String): this((System.currentTimeMillis() / 1000).toInt(), startX, startY, endX, endY, seed)

    constructor(id: Int, startX: Int, startY: Int, endX: Int, endY: Int, seed: String): this(id) {
        setSeed(seed)

        this.startX = startX
        this.startY = startY
        this.endX = endX
        this.endY = endY

        width = endX - startX
        height = endY - startY
    }

    fun getName() = name

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

    fun setSeed(seed: String?) {
        var numberseed = ""
        for (c in (seed?: "").toCharArray()) {
            val cVal = c.toInt()
            numberseed += cVal
        }
        this.seed = seed?: ""
        rnd = Random(stringToSeed(numberseed))
        Logs.i("Dungeon", "seed is : ${this.seed}", null)
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

    override fun equals(other: Any?): Boolean {
        return if (other !is Dungeon) false else id == other.id
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

    fun generate(publishSubject: ObservableEmitter<String>) {
        generateAttributes()

        val firstFloor = Floor(this, rnd, 0)
        firstFloor.fillFloor()

        dungeonFloors.add(firstFloor)

        branchOut(publishSubject)

        calculateNearestPartner(publishSubject)

        calculateRejectedRooms()

        triangulate(publishSubject)

        minSpanningTree(publishSubject)

        combinePaths()

        clearUnnecessaryData()

        pathFind(publishSubject)

        finalise(publishSubject)

        complete()

        publishSubject.onComplete()
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

    private fun branchOut(publishSubject: ObservableEmitter<String>) {
        publishSubject.onNext("Generating Rooms...")
        var i: Int = 0

        while (i < getDungeonFloors().size) {
            val thisFloor = getDungeonFloors()[i]
            Logs.i("Dungeon", "generating rooms for floor ${thisFloor.level}", null)
            thisFloor.branchOut()
            i ++
        }
    }

    private fun calculateNearestPartner(publishSubject: ObservableEmitter<String>) {
        publishSubject.onNext("Spreading out Rooms...")
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

    private fun triangulate(publishSubject: ObservableEmitter<String>) {
        publishSubject.onNext("Triangulating Rooms...")
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.triangulate()
            }
        }
    }

    private fun minSpanningTree(publishSubject: ObservableEmitter<String>) {
        publishSubject.onNext("Finding best corridors...")
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

    private fun pathFind(publishSubject: ObservableEmitter<String>) {
        publishSubject.onNext("Connecting Rooms...")
        getDungeonFloors().forEach { floor ->
            floor.sectionList.forEach { section ->
                section.pathFind()
            }
        }
    }

    private fun finalise(publishSubject: ObservableEmitter<String>) {
        publishSubject.onNext("Generation Complete")
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

        stairsToDecide.forEach { it.getConnectedRoom() }

        getDungeonFloors().forEach { it.complete() }
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

    fun getMemoryItem(): DungeonHistoryItem {
        return DungeonHistoryItem(
                name,
                seed,
                id,
                id
        )
    }
}