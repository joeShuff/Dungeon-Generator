package com.joeshuff.dddungeongenerator.generator.features

import android.graphics.Point
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier
import com.joeshuff.dddungeongenerator.generator.dungeon.Room
import com.joeshuff.dddungeongenerator.generator.floors.Floor
import java.util.*

class StairsFeature(val seed: String, val modifier: Modifier, @Transient val myRoom: Room) : RoomFeature {

    companion object {
        @Transient
        var possibleConnections = Arrays.asList(
                Connection("There is a ladder passing up through a hole in the ceiling.", "There is a ladder heading down in the floor"),
                Connection("There is a huge staircase climbing to a new level.", "There is a huge staircase leading to a lower level"),
                Connection("There appears to be a slide coming from above", "There is a slide descending down into darkness."),
                Connection("There is a huge pile of rubble on the floor as a result of the ceiling collapsing", "There is a massive hole in the floor, this appears to have collapsed."),
                Connection("There is a small staircase waiting to be explored.", "There is a small staircase descending to the floor below"))
    }

    enum class DIRECTION(elevation: Int) {
        UP(1), DOWN(-1);

        var elevation = 0

        fun opposite(): DIRECTION {
            return if (elevation == 1) DOWN else UP
        }

        init {
            this.elevation = elevation
        }
    }

    @Transient
    var chosenType: Connection? = null

    @Transient
    var rnd: Random? = null

    @Transient
    private var floorTarget: Floor? = null

    @Transient
    private lateinit var connectedRoom: Room

    lateinit var direction: DIRECTION

    private lateinit var featureDescription: String
    var connectedRoomId = 0
    var connectedFloorId = 0

    fun generateType() {
        var newrnd = Random(Dungeon.stringToSeed(seed))
        rnd = newrnd

        chosenType = possibleConnections[newrnd.nextInt(possibleConnections.size)]

        if (newrnd.nextDouble() <= modifier.growthChanceUp) { //UP
            direction = DIRECTION.UP
            featureDescription = chosenType?.upDesc?: ""
        } else {
            direction = DIRECTION.DOWN
            featureDescription = chosenType?.downDesc?: ""
        }
    }

    constructor(seed: String, modifier: Modifier, myRoom: Room, direction: DIRECTION, description: String, roomId: Int, connectedFloorId: Int): this(seed, modifier, myRoom) {
        this.direction = direction
        featureDescription = description
        connectedRoomId = roomId
        this.connectedFloorId = connectedFloorId
    }

    fun setFloorTarget(floorTarget: Floor) {
        this.floorTarget = floorTarget
    }

    fun getConnectedRoom(): Room {
        if (!this::connectedRoom.isInitialized) {
            val myRoomCentre = Point(
                    myRoom.globalStartX + myRoom.width / 2,
                    myRoom.globalStartY + myRoom.height / 2
            )

            connectedRoom = floorTarget!!.getRoomClosestTo(myRoom)
            connectedFloorId = floorTarget!!.level
            connectedRoomId = connectedRoom.id

            var otherRoomDesc = ""

            if (direction == DIRECTION.UP) {
                otherRoomDesc = chosenType!!.downDesc
            } else if (direction == DIRECTION.DOWN) {
                otherRoomDesc = chosenType!!.upDesc
            }

            connectedRoom.connectRoomTo(myRoom, otherRoomDesc, direction)
        }

        return connectedRoom
    }

    class Connection(var upDesc: String, var downDesc: String)

    override fun getPageForMoreInfo() = 0

    override fun getFeatureName() = "Stairs Feature"

    override fun getFeatureDescription() = featureDescription
}