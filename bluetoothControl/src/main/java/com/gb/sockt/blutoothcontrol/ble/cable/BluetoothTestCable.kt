package com.gb.sockt.blutoothcontrol.ble.cable

import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.listener.BaseBLEDataListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestCarListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestKeyListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestListener

/**
 * Created by guobiao on 2018/11/15.
 */
interface BluetoothTestCable : BaseBLEControl {

    fun setResponseListener(mBluetoothTestCarListener: BluetoothTestCarListener?)

}
