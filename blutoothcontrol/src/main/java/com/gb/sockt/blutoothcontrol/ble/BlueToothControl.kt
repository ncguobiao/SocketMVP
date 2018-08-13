package com.gb.sockt.blutoothcontrol.ble

/**
 * Created by guobiao on 2018/8/9.
 */
interface BlueToothControl :BaseBLEControl{

    fun addTimeToBle(time: String?, deviceWay: String?, equipElectiic: String?)

    fun getBLEDeviceInfo(deviceWay:String?)



}