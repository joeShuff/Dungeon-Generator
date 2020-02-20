package com.joeshuff.dddungeongenerator.generator.dungeon;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RoomDetail {

    private static List<String> roots = Arrays.asList(
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
    );

    private static List<String> objects = Arrays.asList(
        "bricks",
        "apples",
        "severed heads",
        "bones"
    );

    private static List<String> scrawls = Arrays.asList(
            "RUN AWAY",
            "LEAVE",
            "They come from darkness",
            "Don't BLINK",
            "They're coming",
            "Have a nice day"
    );

    private static List<String> live = Arrays.asList(
            "cat",
            "demon",
            "baby dragon",
            "dryad",
            "elf",
            "warrior",
            "giant spider"
    );

    private static List<String> dir = Arrays.asList("north", "east", "south", "west", "north-east", "south-east", "north-west", "south-west");

    private static String getCoolDetail(Random rnd) {
        String selected = roots.get(rnd.nextInt(roots.size()));

        while (selected.contains("{obj}") || selected.contains("{dir}") || selected.contains("{scrawl}") || selected.contains("{live}")) {
            if (selected.contains("{obj}")) {
                selected =selected.replaceFirst("\\{obj\\}", objects.get(rnd.nextInt(objects.size())));
            }

            if (selected.contains("{dir}")) {
                selected =selected.replaceFirst("\\{dir\\}", dir.get(rnd.nextInt(dir.size())));
            }

            if (selected.contains("{scrawl}")) {
                selected =selected.replaceFirst("\\{scrawl\\}", scrawls.get(rnd.nextInt(scrawls.size())));
            }

            if (selected.contains("{live}")) {
                selected =selected.replaceFirst("\\{live\\}", live.get(rnd.nextInt(live.size())));
            }
        }

        return selected;
    }

    private static String getGenericDetail(Random rnd) {
        return "This room has no interesting traits";
    }

    private static double COOL_DETAIL_CHANCE = 0.4d;

    public static String getDetail(Random rnd) { 
        if (rnd.nextDouble() < COOL_DETAIL_CHANCE) {
            return getCoolDetail(rnd);
        } else {
            return getGenericDetail(rnd);
        }
    }

}
