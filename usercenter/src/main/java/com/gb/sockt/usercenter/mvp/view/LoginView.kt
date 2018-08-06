package com.gb.sockt.usercenter.mvp.view

import com.example.baselibrary.base.IBaseView
import com.gb.sockt.usercenter.data.domain.LoginBean
import com.gb.sockt.usercenter.data.domain.WeiXinLoginSuccessBean

/**
 * Created by guobiao on 2018/8/5.
 */
interface LoginView : IBaseView {

    /**
     * 设置空View
     */
    fun loginSuccess(data: LoginBean)

    fun weixinLoginSuccess(data: WeiXinLoginSuccessBean)

    fun loginFail(msg:String)

    fun showError(error: String,errorCode:Int)
}