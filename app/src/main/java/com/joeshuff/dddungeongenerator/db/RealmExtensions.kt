package com.joeshuff.dddungeongenerator.db

import com.joeshuff.chatalyser.db.RealmLiveData
import com.joeshuff.dddungeongenerator.db.factories.DungeonDao
import io.reactivex.Completable
import io.realm.*

fun <T: RealmObject> T.copyFromRealm(): T {
    return this.realm.copyFromRealm(this)
}

fun Realm.clearCache(deleteRealm: Boolean = true) {
    if (deleteRealm) {
        this.executeTransactionAsync {
            it.deleteAll()
        }
    }
}

fun <F: RealmObject, T: RealmResults<F>> T.copyFromRealm(): MutableList<F>? {
    return this.realm.copyFromRealm(this)
}

fun <T: RealmModel> ArrayList<T>.toRealmList(): RealmList<T> {
    val list = RealmList<T>()
    this.forEach { list.add(it) }
    return list
}

fun <T: RealmModel> RealmList<T>.toArrayList(): ArrayList<T> {
    val list = arrayListOf<T>()
    this.forEach { list.add(it) }
    return list
}

fun Realm.clearAll(): Completable {
    return Completable.create {emitter ->
        Realm.getDefaultInstance().executeTransaction {
            it.deleteAll()
        }
        if (!emitter.isDisposed) emitter.onComplete()
    }
}

fun <T: RealmModel> RealmResults<T>.asLiveData() = RealmLiveData(this)
fun Realm.dungeonDao() = DungeonDao(this)