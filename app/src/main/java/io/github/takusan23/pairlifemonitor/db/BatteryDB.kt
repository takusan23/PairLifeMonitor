package io.github.takusan23.pairlifemonitor.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * データベース
 */
@Database(entities = [BatteryEntity::class], version = 1, exportSchema = false)
abstract class BatteryDB : RoomDatabase() {
    abstract fun batteryDAO(): BatteryDAO

    companion object {

        private var batteryDB: BatteryDB? = null

        fun getInstance(context: Context): BatteryDB {
            if (batteryDB == null) {
                batteryDB = Room.databaseBuilder(context, BatteryDB::class.java, "battery.db").build()
            }
            return batteryDB!!
        }

    }

}