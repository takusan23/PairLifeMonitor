package io.github.takusan23.pairlifemonitor

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.work.WorkInfo
import io.github.takusan23.pairlifemonitor.db.BatteryDB
import io.github.takusan23.pairlifemonitor.tool.DateTool
import io.github.takusan23.pairlifemonitor.ui.theme.PairLifeMonitorTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 権限を貰う
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT), 0)
        }

        setContent {
            val context = LocalContext.current
            val dao = remember { BatteryDB.getInstance(context).batteryDAO() }
            val works = remember { PairDeviceBatteryWorker.collectWorks(context) }
            val workInfos = works.collectAsState(initial = emptyList())
            val batteryList = dao.collectAll().collectAsState(initial = emptyList())

            PairLifeMonitorTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Column {

                        // 実行予定のWorkManagerのタスクがあるかどうか
                        val statusText = if (workInfos.value.any { it.state == WorkInfo.State.ENQUEUED }) {
                            "定期実行予定のタスクがあります"
                        } else {
                            "WorkManagerのタスクはすべてキャンセル済みです"
                        }

                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = statusText
                        )

                        Row {
                            Button(
                                modifier = Modifier.padding(5.dp),
                                onClick = { PairDeviceBatteryWorker.registerRepeat(context) }
                            ) { Text(text = "WorkManager 開始") }
                            Button(
                                modifier = Modifier.padding(5.dp),
                                onClick = { PairDeviceBatteryWorker.unRegisterRepeat(context) }
                            ) { Text(text = "WorkManager 停止") }
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            batteryList.value
                                .groupBy { DateTool.formatDateFromUnixTimeMs(it.addTimeMs) }
                                .forEach { (date, batteryDeviceList) ->
                                    // スティッキーヘッダー、日付の部分
                                    stickyHeader {
                                        Surface {
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(5.dp),
                                                text = date
                                            )
                                        }
                                    }
                                    batteryDeviceList.forEach { battery ->
                                        item {
                                            Surface(color = Color.Transparent) {
                                                Row(
                                                    modifier = Modifier.padding(5.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        modifier = Modifier.weight(1f),
                                                        text = "${battery.batteryLevel} %",
                                                        fontSize = 25.sp
                                                    )
                                                    Column(horizontalAlignment = Alignment.End) {
                                                        Text(text = battery.deviceName)
                                                        Text(text = DateTool.formatFromUnixTimeMs(battery.addTimeMs))
                                                    }
                                                }
                                            }
                                        }
                                        item { Divider() }
                                    }
                                }
                        }
                    }
                }
            }

        }
    }
}