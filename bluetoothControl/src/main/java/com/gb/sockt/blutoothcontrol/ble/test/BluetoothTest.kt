package com.gb.sockt.blutoothcontrol.ble.test

import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.listener.BaseBLEDataListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestListener

/**
 * Created by guobiao on 2018/11/15.
 */
interface BluetoothTest : BaseBLEControl {

    fun setResponseListener(mBluetoothTestListener: BluetoothTestListener)

}