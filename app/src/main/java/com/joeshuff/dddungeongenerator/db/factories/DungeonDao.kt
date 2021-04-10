package com.joeshuff.dddungeongenerator.db.factories

import com.joeshuff.chatalyser.db.RealmLiveData
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import io.realm.Realm

class DungeonDao(val realm: Realm) {

//    fun getLiveMemory(): RealmLiveData<Dungeon> {
//        return realm.where(Dungeon::class.java)
//                .findAllAsync().asLiveData()
//    }
//
//    fun getDungeonById(id: Int): RealmLiveData<Dungeon> {
//        return realm.where(Dungeon::class.java)
//                .equalTo("id", id)
//                .findAllAsync().asLiveData()
//    }

}