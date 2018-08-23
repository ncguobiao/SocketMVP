package com.gb.sockt.blutoothcontrol.ble

import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import java.util.*

/**
 * @date  创建时间 : 2018/8/22
 * @author  : guobiao
 * @Description  蓝牙参数配置项
 * @version
 */
object BluetoothConfig {

    val serviceUUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    val characteristicUUID1 = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
    val characteristicUUID2 = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb")
    val options = BleConnectOptions.Builder()
            .setConnectRetry(3)
            .setConnectTimeout(20000)
            .setServiceDiscoverRetry(3)
            .setServiceDiscoverTimeout(10000)
            .build()
}