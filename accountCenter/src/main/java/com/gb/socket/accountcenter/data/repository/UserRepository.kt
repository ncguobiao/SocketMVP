package com.gb.socket.accountcenter.data.repository

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.socket.accountcenter.api.AccountCenterApi
import com.gb.socket.accountcenter.data.domain.*
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/5.
 */
class UserRepository @Inject constructor() {

    /**
     * 登陆
     */
    fun login(mobile: String, pwd: String,operateType:String, pushId:String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(AccountCenterApi::class.java).login(LoginReq(mobile, pwd, operateType, pushId)).compose()
    }
    /**
     * 快捷登陆
     */
    fun fastLogin(code: String, mobile: String, operateType:String, pushId:String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(AccountCenterApi::class.java).fastLogin(FastLoginReq(code, mobile, operateType, pushId)).compose()

    }

    /**
     * 微信登陆
     */
    fun weixinLogin(openid:String,nickname:String, operateType:String, pushId:String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(AccountCenterApi::class.java).weixinLogin(WeiXinLoginReq(openid, nickname, operateType, pushId)).compose()
    }

    /**
     * 检验手机好是否注册
     */
    fun checkPhoneISRegister(mobile: String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(AccountCenterApi::class.java).checkPhoneISRegister(LoginReq(mobile, "", "", "")).compose()
    }

    /**
     * 获取短信验证码
     */
    fun getMsgCode(mobile: String, tag: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(AccountCenterApi::class.java).getMsgCode(GetCodeReq(mobile, tag)).compose()
    }

    /**
     * 用户注册
     */
    fun register(mobile: String, username: String, pwd: String, code: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(AccountCenterApi::class.java).register(RegisterReq(mobile, username, pwd, code)).compose()
    }

    /**
     * 忘记密码
     */
    fun forgetPwd(code: String, mobile: String, newPassword: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(AccountCenterApi::class.java).forgetPwd(ForgetPwdBean(code, mobile, newPassword)).compose()
    }




}