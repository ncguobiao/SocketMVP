package com.gb.sockt.blutoothcontrol.mvp.view

/**
 * Created by guobiao on 2018/8/9.
 */
interface BluetoothMultiView:BluetoothCommonView {


    /**
     * 加时弹窗
     */
    fun showAddTimeDialog(surplusTime: Long)



    fun addTime2ServiceOnFailure(error:String)

    fun addTime2ServiceOnSuccess(addTime:String,reminTime:Long)

    fun showAddTimeCanUse()

}