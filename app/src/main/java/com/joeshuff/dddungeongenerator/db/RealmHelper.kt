package com.joeshuff.chatalyser.db

import android.content.Context
import com.joeshuff.dddungeongenerator.BuildConfig
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration

class RealmHelper {
    companion object {
        fun build(context: Context) {
            Realm.init(context)

            val config = RealmConfiguration.Builder()
                    .name("dungeongenerator.realm")
                    .schemaVersion(1) //SCHEMA VERSION SET 13/04/2021

            if (BuildConfig.DEV) {
                config.deleteRealmIfMigrationNeeded()
            } else {
                config.migration(MyRealmMigration())
            }

            val built = config.build()

            Realm.setDefaultConfiguration(built)
            Realm.getInstance(built)
        }
    }
}

class MyRealmMigration: RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {

    }

    override fun equals(other: Any?) = other is MyRealmMigration

    override fun hashCode() = 37
}