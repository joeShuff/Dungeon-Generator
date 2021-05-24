package com.joeshuff.dddungeongenerator.generator

import android.content.Context
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DungeonProcessor(
        val disposables: ArrayList<Disposable>?,
        val dungeon: Dungeon
) {

    fun beginProcessing(
            onNext: ((String) -> Unit)?,
            onDone: ((Dungeon) -> Unit)?,
            onError: ((Throwable) -> Unit)?) {

        disposables?.add(generateDungeon()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onNext?.invoke(it)
            }, {
                onError?.invoke(it)
            }, {
                onDone?.invoke(dungeon)
            })
        )
    }

    private fun generateDungeon(): Observable<String> {
        return Observable.create {
            dungeon.generate(it)
        }
    }

}