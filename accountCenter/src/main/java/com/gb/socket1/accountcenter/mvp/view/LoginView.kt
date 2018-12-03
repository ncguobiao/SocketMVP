package com.gb.socket1.accountcenter.mvp.view

import com.example.baselibrary.base.IBaseView

/**
 * Created by guobiao on 2018/8/5.
 */
interface LoginView : IBaseView {

    /**
     * 设置空View
     */
    fun loginSuccess()

    fun loginFail(msg: String)

    fun showError(error: String, errorCode: Int)

    fun showNotifyDialog(title:String?)

    fun showSendCodeDialog(phone:String?)



}