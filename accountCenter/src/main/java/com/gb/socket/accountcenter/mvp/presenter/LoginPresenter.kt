package com.gb.socket.accountcenter.mvp.presenter

import com.example.baselibrary.base.IPresenter
import com.gb.socket.accountcenter.mvp.view.LoginView

/**
 * Created by guobiao on 2018/8/5.
 */
interface LoginPresenter :IPresenter<LoginView>{
    /**
     * 登陆
     */
    fun login(userName :String,pwd :String, operateType:String, pushId:String)

    /**
     * 微信登陆
     */
    fun weiXinLogin(openid: String?, nickname: String?, operateType:String, pushId:String)


    /**
     * 校验手机是否已注册
     */
    fun checkPhoneISRegister(mobile:String,codeType:String?)

}