package io.github.takusan23.pairlifemonitor.tool

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import io.github.takusan23.pairlifemonitor.data.BluetoothDeviceBatteryData

object BluetoothBatteryTool {

    /**
     * Bluetoothペアリングデバイスの名前と電池残量を取得する
     *
     * @param context [Context]
     * @return [BluetoothDeviceBatteryData]の配列
     */
    fun getBluetoothBatteryData(context: Context): List<BluetoothDeviceBatteryData> {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        // 権限がない、そもそも無効時は何もやらない
        if (!bluetoothManager.adapter.isEnabled) {
            return emptyList()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }

        // 電池残量を取得するメソッドが @hide で隠されているので、リフレクションで呼び出す
        val getBatteryLevelMethod = BluetoothDevice::class.java
            .methods
            .find { it.name == "getBatteryLevel" }!!

        return bluetoothManager.adapter.bondedDevices
            .map { BluetoothDeviceBatteryData(it.name, getBatteryLevelMethod.invoke(it) as Int) }
            .filter { it.level > 0 }
    }

}