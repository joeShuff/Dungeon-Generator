package com.joeshuff.dddungeongenerator.screens.home

class HomeSortable(
        val id: Int,
        val displayName: String,
        val sort: (ArrayList<DungeonHistoryItem>) -> ArrayList<DungeonHistoryItem>) {
}