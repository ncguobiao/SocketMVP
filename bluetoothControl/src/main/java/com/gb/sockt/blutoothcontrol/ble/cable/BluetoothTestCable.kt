package com.gb.sockt.blutoothcontrol.ble.cable

import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.listener.*

/**
 * Created by guobiao on 2018/11/15.
 */
interface BluetoothTestCable : BaseBLEControl {

    fun setResponseListener(m: BleCableListener?)

}
