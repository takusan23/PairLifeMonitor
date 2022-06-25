package io.github.takusan23.pairlifemonitor.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_table")
data class BatteryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deviceName: String,
    val batteryLevel: Int,
    val addTimeMs: Long = System.currentTimeMillis(),
)