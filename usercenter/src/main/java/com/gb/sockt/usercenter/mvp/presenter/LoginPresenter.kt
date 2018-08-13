package com.gb.sockt.usercenter.mvp.presenter

import com.example.baselibrary.base.IPresenter
import com.gb.sockt.usercenter.mvp.view.LoginView

/**
 * Created by guobiao on 2018/8/5.
 */
interface LoginPresenter :IPresenter<LoginView>{
    /**
     * 登陆
     */
    fun login(userName :String,pwd :String)

    /**
     * 微信登陆
     */
    fun weiXinLogin(openid: String?, nickname: String?)


    /**
     * 校验手机是否已注册
     */
    fun checkPhoneISRegister(mobile:String,codeType:String?)

}