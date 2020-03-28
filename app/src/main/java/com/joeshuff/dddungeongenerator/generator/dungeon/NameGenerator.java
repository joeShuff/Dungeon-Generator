package com.joeshuff.dddungeongenerator.generator.dungeon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NameGenerator {

    private static List<String> skeletons = Arrays.asList(
            "The {place_adj} {place}",
            "{loc} of the {liv_adj} {liv}"
    );

    private static List<String> baseSetPlaceAdj = Arrays.asList(
            "Dark",
            "Dangerous",
            "Never Ending",
            "Southern",
            "Northern",
            "Jagged",
            "Serene",
            "Bare",
            "Secret",
            "Bottomless",
            "Winding",
            "Gloomy",
            "Crumbling",
            "Overgrown",
            "Forbidden",
            "Abandoned",
            "Bloody",
            "Haunted",
            "Cursed",
            "Hidden",
            "Lost"
    );

    private static List<String> baseSetPlace = Arrays.asList(
            "Dungeon",
            "Cells",
            "Chambers",
            "Catacombs",
            "Tunnels",
            "Maze",
            "Caverns",
            "Stronghold",
            "Laboratories",
            "Observatory",
            "Asylum",
            "Grotto",
            "Mansion",
            "Ruins",
            "Library"
    );

    private static List<String> baseSetLoc = Arrays.asList(
            "Caves",
            "Vaults",
            "Cells",
            "Tombs",
            "Lair",
            "Crypt",
            "Palace",
            "Chambers",
            "Towers",
            "Library",
            "Shrine",
            "Hive",
            "Pit",
            "Cathedral",
            "Embassy",
            "Citadel",
            "Castle",
            "Treasury",
            "Fortress",
            "Apothecary",
            "Crater",
            "Catacombs",
            "Maze",
            "Mine",
            "Quarry",
            "Prison",
            "Asylum"
    );

    private static List<String> baseSetLivAdj = Arrays.asList(
            "Vanishing",
            "Rejected",
            "Mad",
            "Shunned",
            "Neglected",
            "Doomed",
            "Mythical",
            "Crying",
            "Chattering",
            "Impenetrable",
            "Lost",
            "Corrupted",
            "Walking",
            "Unquenchable",
            "Insatiable",
            "Devoured",
            "Gathering"
    );

    private static List<String> baseSetLiv = Arrays.asList(
            "Beasts",
            "Mysteries",
            "Companies",
            "Gates",
            "Lords",
            "Gods",
            "Pirates",
            "Abomination",
            "Paladins",
            "Skulls",
            "Souls",
            "Horrors"
    );

    public static String generateName(Dungeon dungeon) {
        Random rnd = dungeon.getRnd();

        ArrayList<String> live = new ArrayList<>();
        live.addAll(baseSetLiv);
        live.addAll(dungeon.getDungeonCreator().creatorType.livingNames);

        if (dungeon.getDungeonCreator().creatorType == Creator.CREATOR.HUMANS) {
            live.add(dungeon.getDungeonCreator().humanClass);
        }

        String skel = skeletons.get(rnd.nextInt(skeletons.size()));

        while (skel.contains("{place_adj}") || skel.contains("{place}") || skel.contains("{loc}") || skel.contains("{liv_adj}") || skel.contains("{liv}")) {
            if (skel.contains("{place_adj}")) {
                skel =skel.replaceFirst("\\{place_adj\\}", baseSetPlaceAdj.get(rnd.nextInt(baseSetPlaceAdj.size())));
            }

            if (skel.contains("{place}")) {
                skel =skel.replaceFirst("\\{place\\}", baseSetPlace.get(rnd.nextInt(baseSetPlace.size())));
            }

            if (skel.contains("{loc}")) {
                skel =skel.replaceFirst("\\{loc\\}", baseSetLoc.get(rnd.nextInt(baseSetLoc.size())));
            }

            if (skel.contains("{liv_adj}")) {
                skel =skel.replaceFirst("\\{liv_adj\\}", baseSetLivAdj.get(rnd.nextInt(baseSetLivAdj.size())));
            }

            if (skel.contains("{liv}")) {
                skel = skel.replaceFirst("\\{liv\\}", live.get(rnd.nextInt(live.size())));
            }
        }

        return skel;
    }

}
