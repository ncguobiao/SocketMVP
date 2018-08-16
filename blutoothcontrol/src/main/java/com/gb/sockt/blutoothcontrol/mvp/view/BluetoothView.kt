package com.gb.sockt.blutoothcontrol.mvp.view

import com.example.baselibrary.base.IBaseView

/**
 * Created by guobiao on 2018/8/9.
 */
interface BluetoothView:IBaseView {

    /**
     * 充钱
     */
    fun showFillMoneyDialog()

    /**
     * 加时弹窗
     */
    fun showAddTimeDialog(surplusTime: Long)

    /**
     * 第一次使用
     */
    fun isFirstToUse()

    fun openSuccess2ServiceOnSuceess(time :String)
    fun openSuccess2ServiceOnFailure(error:String)

    fun addTime2ServiceOnFailure(error:String)

    fun addTime2ServiceOnSuccess(addTime:String,reminTime:Long)

    fun showAddTimeCanUse()
}