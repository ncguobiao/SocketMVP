package com.gb.sockt.blutoothcontrol.mvp.presenter

import com.example.baselibrary.base.IPresenter
import com.gb.sockt.blutoothcontrol.mvp.view.BluetoothView

/**
 * Created by guobiao on 2018/8/9.
 */
interface BluetoothPresenter :IPresenter<BluetoothView>{

    /**
     * 请求是否可用
     */
    fun requestSurplusTime(userId:String,deviceId:String,time:String)

//    /**
//     * 获取可继续使用时间
//     */
//    fun requestSurplusTime(userId:String,deviceId:String)

}