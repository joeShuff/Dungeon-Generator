package com.joeshuff.dddungeongenerator.generator.dungeon

import java.util.*

object RoomDetail {
    private val roots = Arrays.asList(
            "There is a pile of {obj} in the corner of the room, and someone has scrawled \"{scrawl}\" in blood on the {dir} wall.",
            "Someone has scrawled \"{scrawl}\" on the {dir} wall, and the ceiling is covered with cobwebs.",
            "The {dir} walls are covered with veins of green crystal and the floor is pulsing.",
            "The ceiling is low in this room and there is a pile of {obj} in the centre of the room.",
            "There is a deep pit in the centre of the room and there is a dead {live} at the bottom.",
            "Someone or something has been in this room recently, there is a dead {live} in the centre of the room, freshly killed.",
            "The sound of dripping water can be heard in the distance, someone wrote \"{scrawl}\" in blood on the floor.",
            "There is a large mirror on the {dir} wall, when the mirror is touched, the mirror version of them climbs out of the mirror.",
            "A mural of geometric patterns covers the ceiling and there are hieroglyphs on the {dir} wall.",
            "Checkerboard pressure pads cover the floor of this room with some skeletons hanging from the {dir} wall. \"{scrawl}\" is etched into the ceiling.",
            "This room is pitch black, and you hear a pitter-patter all around the room."
    )
    private val objects = Arrays.asList(
            "bricks",
            "apples",
            "severed heads",
            "bones"
    )
    private val scrawls = Arrays.asList(
            "RUN AWAY",
            "LEAVE",
            "They come from darkness",
            "Don't BLINK",
            "They're coming",
            "Have a nice day"
    )
    private val live = Arrays.asList(
            "cat",
            "demon",
            "baby dragon",
            "dryad",
            "elf",
            "warrior",
            "giant spider"
    )

    private val dir = Arrays.asList("north", "east", "south", "west", "north-east", "south-east", "north-west", "south-west")

    private fun getCoolDetail(rnd: Random): String {
        var selected = roots[rnd.nextInt(roots.size)]
        while (selected.contains("{obj}") || selected.contains("{dir}") || selected.contains("{scrawl}") || selected.contains("{live}")) {
            if (selected.contains("{obj}")) {
                selected = selected.replaceFirst("\\{obj\\}".toRegex(), objects[rnd.nextInt(objects.size)])
            }
            if (selected.contains("{dir}")) {
                selected = selected.replaceFirst("\\{dir\\}".toRegex(), dir[rnd.nextInt(dir.size)])
            }
            if (selected.contains("{scrawl}")) {
                selected = selected.replaceFirst("\\{scrawl\\}".toRegex(), scrawls[rnd.nextInt(scrawls.size)])
            }
            if (selected.contains("{live}")) {
                selected = selected.replaceFirst("\\{live\\}".toRegex(), live[rnd.nextInt(live.size)])
            }
        }
        return selected
    }

    private fun getGenericDetail(rnd: Random): String {
        return "This room has no interesting traits"
    }

    private const val COOL_DETAIL_CHANCE = 0.4
    fun getDetail(rnd: Random): String {
        return if (rnd.nextDouble() < COOL_DETAIL_CHANCE) {
            getCoolDetail(rnd)
        } else {
            getGenericDetail(rnd)
        }
    }
}