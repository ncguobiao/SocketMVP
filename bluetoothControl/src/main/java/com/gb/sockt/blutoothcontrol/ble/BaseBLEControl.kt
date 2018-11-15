package com.gb.sockt.blutoothcontrol.ble

import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControl
import com.gb.sockt.blutoothcontrol.listener.BaseBLEDataListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BleMultiDataChangeListener
import com.gb.sockt.blutoothcontrol.uitls.BlueToothSingeControl

/**
 * Created by guobiao on 2018/8/9.
 */
interface BaseBLEControl {


    /**
     * 设置mac地址和连接状态监听
     */
    fun setMAC(mac: String?, bleConnectListener: BleConnectListener?): BaseBLEControl



    fun connect()

    fun registerBroadcastReceiver(): BaseBLEControl


    /**
     * 初始化蓝牙广播
     */

    fun unregisterBroadcastReceiver()

    fun setConnectListener(bleConnectListener: BleConnectListener)


    fun setResponseListener(baseBLEDataListener: BaseBLEDataListener?): BaseBLEControl

    /**
     * 关闭
     */
    fun close()

    /**
     * 设备重新连接
     */
    fun deviceIfNeeded()


    fun getConnectState(): Boolean

}