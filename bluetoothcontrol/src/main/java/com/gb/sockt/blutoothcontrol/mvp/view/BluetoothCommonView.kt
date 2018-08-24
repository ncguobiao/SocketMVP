package com.gb.sockt.blutoothcontrol.mvp.view

import com.example.baselibrary.base.IBaseView

/**
 * Created by guobiao on 2018/8/9.
 */
interface BluetoothCommonView:IBaseView {


    /**
     * 充钱
     */
    fun showFillMoneyDialog()

    fun openSuccess2ServiceOnSuccess(time :String)

    fun openSuccess2ServiceOnFailure(error:String)

    /**
     * 第一次使用
     */
    fun isFirstToUse()

}