package com.joeshuff.dddungeongenerator.db.factories

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.joeshuff.chatalyser.db.RealmLiveData
import com.joeshuff.dddungeongenerator.db.asLiveData
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import io.reactivex.Completable
import io.realm.Realm
import io.realm.RealmResults

class DungeonDao(val realm: Realm) {

    fun addDungeon(dungeon: Dungeon): Completable {
        return Completable.create { emitter ->
            realm.executeTransactionAsync({
                it.insert(dungeon)
            }, {
                emitter.onComplete()
            }, {
                emitter.onError(it)
            })
        }
    }

    fun deleteDungeonById(id: Int): Completable {
        return Completable.create {emitter ->
            realm.executeTransactionAsync({
                it.where(Dungeon::class.java)
                        .equalTo("id", id)
                        .findAll().deleteAllFromRealm()
            }, {
                emitter.onComplete()
            }, {
                emitter.onError(it)
            })
        }
    }

    fun getMemory(): RealmResults<Dungeon> {
        return realm.where(Dungeon::class.java)
                .findAll()
    }

    fun getLiveMemory(): RealmLiveData<Dungeon> {
        return realm.where(Dungeon::class.java)
                .findAllAsync().asLiveData()
    }

    fun getDungeonById(id: Int): RealmLiveData<Dungeon> {
        return realm.where(Dungeon::class.java)
                .equalTo("id", id)
                .findAllAsync().asLiveData()
    }

    fun loadDungeonOnce(id: Int, lifecycleOwner: LifecycleOwner, onGet: (Dungeon?) -> Unit) {
        val request = getDungeonById(id)
        request.observe(lifecycleOwner, Observer { results ->
            request.removeObservers(lifecycleOwner)
            onGet.invoke(results.firstOrNull())
        })
    }
}