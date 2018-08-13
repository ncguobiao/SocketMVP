package com.gb.sockt.blutoothcontrol.listener

import android.media.MediaSync

/**
 * Created by guobiao on 2018/8/9.
 */
interface BleDataChangeListener {

    /**
     * 请求种子成功
     */
    fun seedSuccess()

    /**
     * 请求种子失败
     */
    fun seedOnFailure()

    /**
     * 蓝牙解密种子成功
     */
    fun checkSeedSuccess()

    /**
     * 蓝牙解密手机端发送的种子失败
     */
    fun checkSeedOnFailure(msg:String)

    /**
     * 发送加密种子是啊比
     */
    fun sendCheckSeedOnFailure()

    /**
     * 是返回设备当前状态
     */
    fun deviceCurrentState(isBusy:Boolean)

    /**
     * 开启设备成功
     */
    fun openDeviceSuccess()

    /**
     * 开启设备失败
     */
    fun openDeviceOnFailure()

    /**
     * 加时成功
     */
    fun addTimeSuccess()

    /**
     * 加时失败
     */
    fun addTimeOnFailure()

    /**
     * 未接负载
     */
    fun deivceIsNotOnline(error: String)

    /**
     * 显示电流电压
     */
    fun showVoltageAndElectricity(voltage: Int, electricity: Int)


}