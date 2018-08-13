package com.gb.sockt.usercenter.mvp.presenter

import com.example.baselibrary.base.IPresenter
import com.gb.sockt.usercenter.mvp.view.RegisterView
import retrofit2.http.POST

/**
 * Created by guobiao on 2018/8/5.
 */
interface RegistPresenter :IPresenter<RegisterView>{
    /**
     * 校验手机是否已注册
     */
    fun getMsgCode(mobile:String,tag:String)


    /**
     * 注册
     */
    fun register(mobile:String, username:String, pwd:String, code:String)


    /**
     * 忘记密码
     */
    fun forgetPwd(code: String, mobile: String, newPassword: String)


    /**
     * 快捷登陆
     */
    @POST("UserService/User/fastLogin")
    fun fastLogin( code:String,  mobile:String)

}