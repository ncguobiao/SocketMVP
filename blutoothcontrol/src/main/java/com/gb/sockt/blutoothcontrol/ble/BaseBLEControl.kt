package com.gb.sockt.blutoothcontrol.ble

import android.content.Context
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BleDataChangeListener

/**
 * Created by guobiao on 2018/8/9.
 */
interface BaseBLEControl {

    /**
     * 请求种子
     */
    fun requestSeed()

    //发送加密种子
    fun sendAndCheckSeed(keys: ByteArray)

    /**
     * 获取设备信息
     */

    fun getBLEDeviceInfo()

    /**
     * 开启设备
     */
    fun openDeivce(time: String?, deviceWay: String?, equipElectiic: String?="2000")


    fun connect()

    fun setConnectListener(bleConnectListener: BleConnectListener)

    /**
     * 设置mac地址和连接状态监听
     */
    fun setMAC(mac: String?, bleConnectListener: BleConnectListener?): BlueToothControl

    /**
     * 初始化蓝牙广播
     */
    fun registerBoradcastRecvier(): BlueToothControl

    fun unregisterBoradcastRecvier()

    /**
     * 蓝牙响应数据监听
     */
    fun setResponeListener(bleDataChangeListener: BleDataChangeListener?): BlueToothControl

    /**
     * 关闭
     */
    fun close()

    /**
     * 设备重新连接
     */
    fun deviceIfNeeded()


     fun getConnectState():Boolean

}