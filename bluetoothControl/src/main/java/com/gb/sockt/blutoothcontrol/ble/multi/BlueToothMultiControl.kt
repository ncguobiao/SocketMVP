package com.gb.sockt.blutoothcontrol.ble.multi

import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.listener.BaseBLEDataListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BleMultiDataChangeListener

/**
 * Created by guobiao on 2018/8/9.
 */
interface BlueToothMultiControl : BaseBLEControl {

    fun addTimeToBle(time: String?, deviceWay: String?, equipElectiic: String?)

    fun getBLEDeviceInfo(deviceWay:String?)

    //发送加密种子
    fun sendAndCheckSeed(keys: ByteArray)

    /**
     * 请求种子
     */
    fun requestSeed()
//    /**
//     * 蓝牙响应数据监听
//     */
//    fun setResponseListener(baseBLEDataListener: BaseBLEDataListener): BaseBLEControl

//    fun setResponseListener(bleDataChangeListener: BleMultiDataChangeListener?): BlueToothMultiControl
    /**
     * 开启设备
     */
    fun openDevice(time: String?, deviceWay: String?, equipElectiic: String? = "2000")





}