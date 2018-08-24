package com.gb.sockt.blutoothcontrol.ble

import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControl
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BleMultiDataChangeListener

/**
 * Created by guobiao on 2018/8/22.
 */
 abstract class BLEControl {


   abstract fun unregisterBroadcastReceiver()

    abstract fun setConnectListener(bleConnectListener: BleConnectListener)

    /**
     * 蓝牙响应数据监听
     */
    abstract fun setResponeListener(bleDataChangeListener: BleMultiDataChangeListener?): BlueToothMultiControl
}