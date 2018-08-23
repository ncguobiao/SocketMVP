package com.gb.sockt.blutoothcontrol.ble.ce

import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControl

/**
 * Created by guobiao on 2018/8/9.
 */
interface BlueToothCEControl : BlueToothMultiControl {


    fun getDeviceElectric(deviceWay:String)



}