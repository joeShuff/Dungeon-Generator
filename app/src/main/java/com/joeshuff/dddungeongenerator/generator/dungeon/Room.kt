package com.joeshuff.dddungeongenerator.generator.dungeon

import android.graphics.Point
import com.joeshuff.dddungeongenerator.generator.features.*
import com.joeshuff.dddungeongenerator.generator.features.StairsFeature.DIRECTION
import com.joeshuff.dddungeongenerator.generator.floors.DungeonSection
import com.joeshuff.dddungeongenerator.generator.models.Rectangle
import com.joeshuff.dddungeongenerator.util.Logs
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class Room(@Transient val thisSection: DungeonSection, id: Int, @Transient var rnd: Random, modifier: Modifier) {

    companion object {
        @Transient
        var featureIntros = Arrays.asList("You've heard rumors that ", "You've discovered that ", "Maps have told you that ", "It became apparent that ", "You know that ")

        @Transient
        private val MIN_MEAN_ROOM_SIZE = 45

        @Transient
        private val MAX_MEAN_ROOM_SIZE = 85

        @Transient
        private val MIN_STD_DEV = 15

        @Transient
        private val MAX_STD_DEV = 45

        @Transient
        private var MEAN_ROOM_SIZE = 65

        @Transient
        private var STD_DEV = 30

        @Transient
        var DEFAULT_MONSTER_CHANCE = 0.15

        @Transient
        var DEFAULT_TRAP_CHANCE = 0.2

        @Transient
        var DEFAULT_TREASURE_CHANCE = 0.25

        @Transient
        var DEFAULT_STAIRS_CHANCE = 0.05
        fun changeRoomSize(percentage: Float) {
            MEAN_ROOM_SIZE = (MIN_MEAN_ROOM_SIZE + (MAX_MEAN_ROOM_SIZE - MIN_MEAN_ROOM_SIZE) * percentage).toInt()
            STD_DEV = (MIN_STD_DEV + (MAX_STD_DEV - MIN_STD_DEV) * percentage).toInt()
        }
    }

    private var sectionStartX = 0
    private var sectionStartY = 0

    var id: Int

    val detail: String

    var startX: Int = 0
    var startY: Int = 0

    var isSelected = false

    var isRejected = false

    val width: Int
    val height: Int

    private var timesShifted = 0

    private var featureList: MutableList<RoomFeature> = ArrayList()

    @Transient
    var movementHistory: MutableList<Movement> = ArrayList()

    init {
        sectionStartX = thisSection.startPoint.x
        sectionStartY = thisSection.startPoint.y

        this.id = id

        val center = randomPointInCircle(thisSection.width / 4)

        height = (rnd.nextGaussian() * STD_DEV + MEAN_ROOM_SIZE).toInt()
        width = (rnd.nextGaussian() * STD_DEV + MEAN_ROOM_SIZE).toInt()

        detail = "This room is ${height}ft x ${width}ft\n${RoomDetail.getDetail(rnd)}"

        startX = thisSection.width / 2 + center.x - width / 2
        startY = thisSection.height / 2 + center.y - height / 2

        generateFeatures(modifier)
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
        val description: MutableList<String> = ArrayList()

        for (feature in getFeatureList()) {
            description.add(featureIntros[Random().nextInt(featureIntros.size)].toString() + feature.getFeatureDescription())
        }

        return description
    }

    fun connectRoomTo(room: Room, connection: String, direction: DIRECTION) {
        val newStairs = StairsFeature("", Modifier(), this, direction.opposite(), connection, room.id, room.thisSection.floor.level)
        featureList.add(newStairs)
    }

    private fun randomPointInCircle(radius: Int): Point {
        val t = 2.0f * Math.PI * rnd.nextDouble()
        val u = rnd.nextDouble() + rnd.nextDouble()
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
        val center = myCenter()
        val otherCenter = room.myCenter()

        if (timesShifted >= 1000) {
            isRejected = true
            return true
        }

        val diff = Point(center.x - otherCenter.x, center.y - otherCenter.y)

        val intersection = room.meWithBorder().intersection(meWithBorder())

        if (intersection.isEmpty) return false
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

    fun generateFeatures(modifier: Modifier) {
        if (willIBeRejected()) return

        //monster chance
        val monChance = rnd.nextDouble()
        if (monChance <= DEFAULT_MONSTER_CHANCE * modifier.monsterChanceModifier) {
            featureList.add(MonsterFeature(String.format("%.0f", monChance * 10.0.pow(16.0)), modifier))
        }
        val trapChance = rnd.nextDouble()
        //trap chance
        if (trapChance <= DEFAULT_TRAP_CHANCE * modifier.trapChanceModifier) {
            featureList.add(TrapFeature(String.format("%.0f", trapChance * 10.0.pow(16.0)), modifier))
        }

        //treasure chance
        val treasureChance = rnd.nextDouble()
        if (rnd.nextDouble() <= DEFAULT_TREASURE_CHANCE * modifier.treasureChanceModifier) {
            featureList.add(TreasureFeature(String.format("%.0f", treasureChance * 10.0.pow(16.0))))
        }

        //Stairs Chance
        //As you get further away from the ground floor, the chance halves every floor you are away, to stop infinite growing
        val stairModifier = modifier.stairChanceModifier
        val level = thisSection.floor.level

        val chanceOfStairFeature = DEFAULT_STAIRS_CHANCE * stairModifier / (1 + abs(level))
        val stairChance = rnd.nextDouble()

        if (stairChance <= chanceOfStairFeature) {
            val stairsFeature = StairsFeature(String.format("%.0f", stairChance * 10.0.pow(16.0)), modifier, this)
            stairsFeature.generateType()

            featureList.add(stairsFeature)
            stairsFeature.setFloorTarget(thisSection.floor.newFloor(stairsFeature.direction.elevation, this))
        }
    }

    fun containsPoint(p: Point): Boolean {
        return p.x > startX && p.x < startX + width && p.y > startY && p.y < startY + height
    }

    fun containsGlobalPoint(p: Point): Boolean {
        return p.x > globalStartX && p.x < globalStartX + width && p.y > globalStartY && p.y < globalStartY + height
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