package com.gb.sockt.accountcenter.mvp.view

import com.example.baselibrary.base.IBaseView
import com.gb.sockt.accountcenter.data.domain.LoginBean
import com.gb.sockt.accountcenter.data.domain.WeiXinLoginSuccessBean

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