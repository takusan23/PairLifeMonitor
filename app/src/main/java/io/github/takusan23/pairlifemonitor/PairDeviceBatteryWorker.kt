package io.github.takusan23.pairlifemonitor

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
import io.github.takusan23.pairlifemonitor.db.BatteryDB
import io.github.takusan23.pairlifemonitor.db.BatteryEntity
import io.github.takusan23.pairlifemonitor.tool.BluetoothBatteryTool
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

/** WorkManagerを利用して接続済みBluetoothデバイスの電池残量を取得する */
class PairDeviceBatteryWorker(private val appContext: Context, workerParameters: WorkerParameters) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val dao = BatteryDB.getInstance(appContext).batteryDAO()
        // 取得する
        val deviceList = BluetoothBatteryTool.getBluetoothBatteryData(appContext)
        deviceList
            .map { BatteryEntity(deviceName = it.name, batteryLevel = it.level) }
            .forEach { dao.insert(it) }
        return Result.success()
    }

    companion object {

        /** WorkManagerに登録する際に付けてるタグ */
        private const val TAG = "io.github.takusan23.pairlifemonitor.pair_life_monitor_work"

        /**
         * 一回だけ転送タスクを実行する
         *
         * @param context Context
         * */
        fun oneShot(context: Context) {
            val transferWork = OneTimeWorkRequestBuilder<PairDeviceBatteryWorker>()
                .addTag(TAG)
                .build()
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(transferWork)
        }

        /**
         * 定期実行登録を行う
         *
         * @param context Context
         * @param intervalMinute 定期実行間隔。単位は分。最低値は15分以上である必要があります。
         * */
        fun registerRepeat(context: Context, intervalMinute: Long = 15) {
            val workManager = WorkManager.getInstance(context)
            // 既存の定期実行はキャンセル
            unRegisterRepeat(context)
            // 登録
            val transferWork = PeriodicWorkRequestBuilder<PairDeviceBatteryWorker>(intervalMinute, TimeUnit.MINUTES)
                .addTag(TAG)
                .build()
            workManager.enqueue(transferWork)
        }

        /**
         * 定期実行を解除する
         *
         * @param context Context
         * */
        fun unRegisterRepeat(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(TAG)
        }

        /**
         * WorkManagerのタスクの状態をFlowで取得する
         *
         * @param context [Context]
         */
        fun collectWorks(context: Context): Flow<MutableList<WorkInfo>> {
            val workManager = WorkManager.getInstance(context)
            val liveData = workManager.getWorkInfosByTagLiveData(TAG)
            return callbackFlow {
                val callback = Observer<MutableList<WorkInfo>> { trySend(it) }
                liveData.observeForever(callback)
                awaitClose { liveData.removeObserver(callback) }
            }
        }

    }

}