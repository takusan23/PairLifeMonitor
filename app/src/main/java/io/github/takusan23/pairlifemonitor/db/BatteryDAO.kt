package io.github.takusan23.pairlifemonitor.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryDAO {

    /** すべて取得 */
    @Query("SELECT * FROM battery_table ORDER BY addTimeMs DESC")
    fun getAll(): List<BatteryEntity>

    /** すべて取得、Flow版 */
    @Query("SELECT * FROM battery_table ORDER BY addTimeMs DESC")
    fun collectAll(): Flow<List<BatteryEntity>>

    /** データ更新 */
    @Update
    fun update(batteryEntity: BatteryEntity)

    /** データ追加 */
    @Insert
    fun insert(batteryEntity: BatteryEntity)

    /** データ削除 */
    @Delete
    fun delete(batteryEntity: BatteryEntity)

}