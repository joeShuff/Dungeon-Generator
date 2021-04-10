package com.joeshuff.dddungeongenerator.generator.dungeon

import java.util.*

object NameGenerator {

    private val skeletons = listOf(
            "The {place_adj} {place}",
            "{loc} of the {liv_adj} {liv}"
    )
    private val baseSetPlaceAdj = listOf(
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
    )
    private val baseSetPlace = listOf(
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
    )
    private val baseSetLoc = listOf(
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
    )
    private val baseSetLivAdj = listOf(
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
    )
    private val baseSetLiv = listOf(
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
    )

    fun generateName(dungeon: Dungeon): String {
        val rnd = dungeon.rnd?: Random()

        val live = ArrayList<String>()
        live.addAll(baseSetLiv)

        dungeon.dungeonCreator?.let {
            it.creatorType?.livingNames?.let { live.addAll(it) }

            if (it.creatorType === Creator.CREATOR.HUMANS) {
                live.add(it.humanClass)
            }
        }

        var skel = skeletons[rnd.nextInt(skeletons.size)]
        while (skel.contains("{place_adj}") || skel.contains("{place}") || skel.contains("{loc}") || skel.contains("{liv_adj}") || skel.contains("{liv}")) {
            if (skel.contains("{place_adj}")) {
                skel = skel.replaceFirst("\\{place_adj\\}".toRegex(), baseSetPlaceAdj[rnd.nextInt(baseSetPlaceAdj.size)])
            }
            if (skel.contains("{place}")) {
                skel = skel.replaceFirst("\\{place\\}".toRegex(), baseSetPlace[rnd.nextInt(baseSetPlace.size)])
            }
            if (skel.contains("{loc}")) {
                skel = skel.replaceFirst("\\{loc\\}".toRegex(), baseSetLoc[rnd.nextInt(baseSetLoc.size)])
            }
            if (skel.contains("{liv_adj}")) {
                skel = skel.replaceFirst("\\{liv_adj\\}".toRegex(), baseSetLivAdj[rnd.nextInt(baseSetLivAdj.size)])
            }
            if (skel.contains("{liv}")) {
                skel = skel.replaceFirst("\\{liv\\}".toRegex(), live[rnd.nextInt(live.size)])
            }
        }
        return skel
    }
}