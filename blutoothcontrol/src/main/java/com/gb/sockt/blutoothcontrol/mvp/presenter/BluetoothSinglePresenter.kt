package com.gb.sockt.blutoothcontrol.mvp.presenter

/**
 * Created by guobiao on 2018/8/9.
 */
interface BluetoothSinglePresenter {

    /**
     * 请求是否可用
     */
    fun requestSurplusTime(userId:String,deviceId:String,time:String)

//    /**
//     * 获取可继续使用时间
//     */
//    fun requestSurplusTime(userId:String,deviceId:String)


     fun openSuccess2Service(userId: String, deviceId: String, time: String)


}