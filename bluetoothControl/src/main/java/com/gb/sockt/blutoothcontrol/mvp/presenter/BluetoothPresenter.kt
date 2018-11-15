package com.gb.sockt.blutoothcontrol.mvp.presenter

/**
 * Created by guobiao on 2018/8/9.
 */
interface BluetoothPresenter :BluetoothSinglePresenter{

    /**
     * 加时成功通知服务器
     */
     fun addTimeSuccess2Service(userId: String, deviceId: String, time: String)

    fun addTimeCanUse(userId: String, deviceId: String, time: String)
}