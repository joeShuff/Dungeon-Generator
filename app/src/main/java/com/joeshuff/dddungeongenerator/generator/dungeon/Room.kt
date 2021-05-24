package com.joeshuff.dddungeongenerator.generator.dungeon

import com.google.gson.GsonBuilder
import com.joeshuff.dddungeongenerator.db.models.Point
import com.joeshuff.dddungeongenerator.generator.features.*
import com.joeshuff.dddungeongenerator.generator.features.StairsFeature.DIRECTION
import com.joeshuff.dddungeongenerator.generator.floors.DungeonSection
import com.joeshuff.dddungeongenerator.generator.models.Rectangle
import com.joeshuff.dddungeongenerator.db.RuntimeTypeAdapterFactory
import com.joeshuff.dddungeongenerator.util.Logs
import io.realm.RealmObject
import io.realm.annotations.Ignore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin


open class Room(
        var id: Int,
        @Ignore val thisSection: DungeonSection?,
        @Ignore var rnd: Random?,
        @Ignore var modifier: Modifier?): RealmObject() {

    constructor(): this(0, null, null, null)

    companion object {
        @Ignore
        var featureIntros = Arrays.asList("You've heard rumors that ", "You've discovered that ", "Maps have told you that ", "It became apparent that ", "You know that ")

        @Ignore
        private val MIN_MEAN_ROOM_SIZE = 45

        @Ignore
        private val MAX_MEAN_ROOM_SIZE = 85

        @Ignore
        private val MIN_STD_DEV = 15

        @Ignore
        private val MAX_STD_DEV = 45

        @Ignore
        private var MEAN_ROOM_SIZE = 65

        @Ignore
        private var STD_DEV = 30

        @Ignore
        var DEFAULT_MONSTER_CHANCE = 0.15

        @Ignore
        var DEFAULT_TRAP_CHANCE = 0.2

        @Ignore
        var DEFAULT_TREASURE_CHANCE = 0.25

        @Ignore
        var DEFAULT_STAIRS_CHANCE = 0.05
        fun changeRoomSize(percentage: Float) {
            MEAN_ROOM_SIZE = (MIN_MEAN_ROOM_SIZE + (MAX_MEAN_ROOM_SIZE - MIN_MEAN_ROOM_SIZE) * percentage).toInt()
            STD_DEV = (MIN_STD_DEV + (MAX_STD_DEV - MIN_STD_DEV) * percentage).toInt()
        }
    }

    private var sectionStartX = 0
    private var sectionStartY = 0

    var detail: String = ""

    var startX: Int = 0
    var startY: Int = 0

    @Ignore
    var isSelected = false

    @Ignore
    var isRejected = false

    var width: Int = 0
    var height: Int = 0

    @Ignore
    private var timesShifted = 0

    private var features: String = "{\"features\":[]}"

    fun getUIFeatures(): FeatureContainer {
        val gson = GsonBuilder().registerTypeAdapterFactory(RuntimeTypeAdapterFactory.getAdapter()).create()
        val result = gson.fromJson(features, FeatureContainer::class.java)
        return result
    }

    @Ignore
    private var featureList: ArrayList<RoomFeature> = arrayListOf()

    fun addFeature(roomFeature: RoomFeature) {
        featureList.add(roomFeature)
    }

    @Ignore
    var movementHistory: ArrayList<Movement> = ArrayList()

    init {
        thisSection?.let { section ->
            rnd?.let { random ->
                sectionStartX = section.startPoint?.x?: 0
                sectionStartY = section.startPoint?.y?: 0

                val center = randomPointInCircle(random, section.width / 4)

                height = (random.nextGaussian() * STD_DEV + MEAN_ROOM_SIZE).toInt()
                width = (random.nextGaussian() * STD_DEV + MEAN_ROOM_SIZE).toInt()

                detail = "This room is ${height}ft x ${width}ft\n${RoomDetail.getDetail(random)}"

                startX = (section.width / 2) + (center.x - width / 2)
                startY = (section.height / 2) + (center.y - height / 2)

                modifier?.let { generateFeatures(section, random, it) }
            }
        }
    }

    fun getFullRoomDescription(): String {
        val descParts = getRoomDescription()

        var res = ""

        for (d in descParts) {
            res += "$d\n"
        }

        return res
    }

    fun getRoomDescription(): List<String> {
        val description: ArrayList<String> = ArrayList()

        for (feature in getFeatureList()) {
            description.add(featureIntros[Random().nextInt(featureIntros.size)].toString() + feature.getFeatureDescription())
        }

        return description
    }

    fun connectRoomTo(room: Room, connection: String, direction: DIRECTION) {
        room.thisSection?.floor?.let {
            val newStairs = StairsFeature("", Modifier(), this, direction.opposite(), connection, room.id, it.level)
            addFeature(newStairs)
        }
    }

    private fun randomPointInCircle(random: Random, radius: Int): Point {
        val t = 2.0f * Math.PI * random.nextDouble()
        val u = random.nextDouble() + random.nextDouble()
        val r: Double

        r = if (u > 1) {
            2 - u
        } else {
            u
        }

        val p = Point()
        p.x = (radius * r * cos(t)).toInt()
        p.y = (radius * r * sin(t)).toInt()
        return p
    }

    fun moveAwayFrom(room: Room): Boolean {
        if (thisSection == null) return false

        val center = myCenter()
        val otherCenter = room.myCenter()

        if (timesShifted >= 1000) {
            isRejected = true
            return true
        }

        val diff = Point(center.x - otherCenter.x, center.y - otherCenter.y)

        val intersection = room.meWithBorder().intersection(meWithBorder())

        if (intersection.isEmpty()) return false
        if (diff.x == 0 && diff.y == 0) return false

        var xOverlap = intersection.width
        var yOverlap = intersection.height

        if (diff.x < 0) { //other is to my right
            xOverlap *= -1
        }

        if (diff.y < 0) { //other is below me
            yOverlap *= -1
        }

        val canMoveX = startX + xOverlap > 0 && startX + xOverlap + width < thisSection.width
        val canMoveY = startY + yOverlap > 0 && startY + yOverlap + height < thisSection.height

        val myCentreX = startX + width / 2
        val myCentreY = startY + height / 2

        val sectionCentreX = thisSection.width / 2
        val sectionCentreY = thisSection.height / 2

        val towardCentreX = myCentreX < sectionCentreX && xOverlap > 0 ||
                myCentreX > sectionCentreX && xOverlap < 0

        val towardCentreY = myCentreY < sectionCentreY && yOverlap > 0 ||
                myCentreY > sectionCentreY && yOverlap < 0

        if (canMoveX && canMoveY) {
            if (abs(xOverlap) > abs(yOverlap)) {
                yOverlap = 0
            } else {
                xOverlap = 0
            }
        } else if (canMoveX) {
            yOverlap = 0
        } else if (canMoveY) {
            xOverlap = 0
        }

        if (startX + xOverlap > 0 && startX + xOverlap + width < thisSection.width) {
            if (startY + yOverlap > 0 && startY + yOverlap + height < thisSection.height) {

                var thisMovement = Movement(Point(startX, startY), Point(startX + xOverlap, startY + yOverlap))

                if (movementHistory.contains(thisMovement)) {
                    movementHistory[movementHistory.indexOf(thisMovement)].increment()
                    thisMovement = movementHistory[movementHistory.indexOf(thisMovement)]
                } else {
                    movementHistory.add(thisMovement)
                }

                if (thisMovement.timesMade > 5) {
                    Logs.i("Room", "Rejecting $id because it's bouncing", null)
                    isRejected = true
                    return false
                }

                startX += xOverlap
                startY += yOverlap
                timesShifted++
                return true
            }
        }

        return false
    }

    fun myCenter(): Point {
        return Point(startX + width / 2, startY + height / 2)
    }

    fun myGlobalCenter(): Point {
        return Point(globalStartX + width / 2, globalStartY + height / 2)
    }

    fun calcHowMuchItFailedBy(): Double {
        val lowerBound = MEAN_ROOM_SIZE - STD_DEV.toDouble()
        val upperBound = MEAN_ROOM_SIZE + STD_DEV.toDouble()

        var widthDiff = 0.0
        var heightDiff = 0.0

        if (width > upperBound) widthDiff = Math.abs(width - upperBound) / upperBound
        if (width < lowerBound) widthDiff = Math.abs(lowerBound - width) / lowerBound

        if (height > upperBound) heightDiff = Math.abs(height - upperBound) / upperBound
        if (height < lowerBound) heightDiff = Math.abs(lowerBound - height) / lowerBound

        return (widthDiff + heightDiff) / 2
    }

    fun forceSelect() {
        isRejected = false
        isSelected = true
    }

    private fun willIBeRejected(): Boolean {
        val lowerBound = MEAN_ROOM_SIZE - STD_DEV
        val upperBound = MEAN_ROOM_SIZE + STD_DEV

        return !((width in (lowerBound + 1) until upperBound) && (height in (lowerBound + 1) until upperBound))
    }

    fun calculateRejected(rooms: List<Room>) {
        for (other in rooms) {
            if (this === other) continue
            if (other.isRejected) continue

            if (me().intersects(other.me())) {
                if (area > other.area) {
                    isRejected = true
                } else {
                    other.isRejected = true
                }
            }
        }

        isSelected = !willIBeRejected()
    }

    fun intersects(r: Room): Int {
        val totalCoverage = Math.min(r.area, area).toDouble()

        val me = Rectangle(startX - 3, startY - 3, width + 6, height + 6)
        val other = Rectangle(r.startX, r.startY, r.width, r.height)

        val intersection = me.intersection(other)

        if (intersection.width < 0 || intersection.height < 0) return 0

        val actualCoverage = me.intersection(other).height * me.intersection(other).width.toDouble()

        return (100 * (actualCoverage / totalCoverage)).toInt()
    }

    fun getFeatureList(): List<RoomFeature> {
        return featureList
    }

    fun generateFeatures(section: DungeonSection, random: Random, modifier: Modifier) {
        if (willIBeRejected()) return

        //monster chance
        val monChance = random.nextDouble()
        if (monChance <= DEFAULT_MONSTER_CHANCE * modifier.monsterChanceModifier) {
            addFeature(MonsterFeature(String.format("%.0f", monChance * 10.0.pow(16.0)), modifier))
        }
        val trapChance = random.nextDouble()
        //trap chance
        if (trapChance <= DEFAULT_TRAP_CHANCE * modifier.trapChanceModifier) {
            addFeature(TrapFeature(String.format("%.0f", trapChance * 10.0.pow(16.0)), modifier))
        }

        //treasure chance
        val treasureChance = random.nextDouble()
        if (random.nextDouble() <= DEFAULT_TREASURE_CHANCE * modifier.treasureChanceModifier) {
            addFeature(TreasureFeature(String.format("%.0f", treasureChance * 10.0.pow(16.0))))
        }

        //Stairs Chance
        //As you get further away from the ground floor, the chance halves every floor you are away, to stop infinite growing
        val stairModifier = modifier.stairChanceModifier
        val level = section.floor?.level?: 0

        val chanceOfStairFeature = DEFAULT_STAIRS_CHANCE * stairModifier / (1 + abs(level))
        val stairChance = random.nextDouble()

        if (stairChance <= chanceOfStairFeature) {
            val stairsFeature = StairsFeature(String.format("%.0f", stairChance * 10.0.pow(16.0)), modifier, this)

            section.floor?.newFloor(stairsFeature.direction.elevation, this)?.let {
                addFeature(stairsFeature)
                stairsFeature.setFloorTarget(it)
            }
        }
    }

    fun containsPoint(p: Point): Boolean {
        return p.x > startX && p.x < startX + width && p.y > startY && p.y < startY + height
    }

    fun containsGlobalPoint(p: Point): Boolean {
        return p.x > globalStartX && p.x < globalStartX + width && p.y > globalStartY && p.y < globalStartY + height
    }

    fun complete() {
        val gson = GsonBuilder().registerTypeAdapterFactory(RuntimeTypeAdapterFactory.getAdapter()).create()
        val result = gson.toJson(FeatureContainer(featureList))

        features = result
    }

    //=========================
    //=========================
    //===== GETTERS ===========
    //=========================
    //=========================

    val globalStartX: Int
        get() = startX + sectionStartX

    val globalStartY: Int
        get() = startY + sectionStartY

    val area: Int
        get() = width * height

    fun me(): Rectangle {
        return Rectangle(startX, startY, width, height)
    }

    fun meWithBorder(): Rectangle {
        return Rectangle(startX - 2, startY - 2, width + 4, height + 4)
    }

    fun topBorder(): Rectangle {
        return Rectangle(startX, startY - 4, width, 6)
    }

    fun rightBorder(): Rectangle {
        return Rectangle(startX + width - 2, startY, 6, height)
    }

    fun leftBorder(): Rectangle {
        return Rectangle(startX - 4, startY, 6, height)
    }

    fun bottomBorder(): Rectangle {
        return Rectangle(startX, startY + height - 2, width, 6)
    }
}