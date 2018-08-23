package com.gb.sockt.blutoothcontrol.listener

/**
 * Created by guobiao on 2018/8/22.
 */
interface BaseBLEDataListener {

    /**
     * 请求种子成功
     */
    fun requestSeedSuccess()

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
     * 发送加密种子是失败
     */
    fun sendCheckSeedOnFailure()

    /**
     * 开启设备成功
     */
    fun openDeviceSuccess()

    /**
     * 开启设备失败
     */
    fun openDeviceOnFailure()

    /**
     * 显示电流电压
     */
    fun showVoltageAndElectricity(voltage: Int, electricity: Int)

}