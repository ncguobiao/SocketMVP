package com.gb.sockt.usercenter.mvp.service

import com.example.baselibrary.common.BaseResp
import io.reactivex.Observable

/**
 * Created by guobiao on 2018/8/5.
 */
interface UserService {

    /**
     * 登陆
     */
    fun login(mobile: String, pwd: String): Observable<BaseResp>


    /**
     * 微信登陆
     */
    fun weixinLogin(openid: String, nickname: String) : Observable<BaseResp>


    /**
     * 校验手机是否已注册
     */
    fun checkPhoneISRegister(mobile: String) : Observable<BaseResp>


    /**
     * 获取短信验证码
     */
    fun getMsgCode(mobile: String, tag: String): Observable<BaseResp>


    /**
     * 用户注册
     */
    fun register(mobile: String, username: String,pwd:String,code:String): Observable<BaseResp>

    /**
     * 忘记密码
     */
    fun forgetPwd(code: String, mobile: String, newPassword: String): Observable<BaseResp>

    /**
     * 快捷登陆
     */
    fun fastLogin(code: String, mobile: String): Observable<BaseResp>


}