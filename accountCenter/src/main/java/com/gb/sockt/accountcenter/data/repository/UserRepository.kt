package com.gb.sockt.accountcenter.data.repository

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
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
        return RetrofitFactory.instance.create(com.gb.sockt.accountcenter.api.AccountCenterApi::class.java).login(com.gb.sockt.accountcenter.data.domain.LoginReq(mobile, pwd, operateType, pushId)).compose()
    }
    /**
     * 快捷登陆
     */
    fun fastLogin(code: String, mobile: String, operateType:String, pushId:String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(com.gb.sockt.accountcenter.api.AccountCenterApi::class.java).fastLogin(com.gb.sockt.accountcenter.data.domain.FastLoginReq(code, mobile, operateType, pushId)).compose()

    }

    /**
     * 微信登陆
     */
    fun weixinLogin(openid:String,nickname:String, operateType:String, pushId:String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(com.gb.sockt.accountcenter.api.AccountCenterApi::class.java).weixinLogin(com.gb.sockt.accountcenter.data.domain.WeiXinLoginReq(openid, nickname, operateType, pushId)).compose()
    }

    /**
     * 检验手机好是否注册
     */
    fun checkPhoneISRegister(mobile: String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(com.gb.sockt.accountcenter.api.AccountCenterApi::class.java).checkPhoneISRegister(com.gb.sockt.accountcenter.data.domain.LoginReq(mobile, "", "", "")).compose()
    }

    /**
     * 获取短信验证码
     */
    fun getMsgCode(mobile: String, tag: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(com.gb.sockt.accountcenter.api.AccountCenterApi::class.java).getMsgCode(com.gb.sockt.accountcenter.data.domain.GetCodeReq(mobile, tag)).compose()
    }

    /**
     * 用户注册
     */
    fun register(mobile: String, username: String, pwd: String, code: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(com.gb.sockt.accountcenter.api.AccountCenterApi::class.java).register(com.gb.sockt.accountcenter.data.domain.RegisterReq(mobile, username, pwd, code)).compose()
    }

    /**
     * 忘记密码
     */
    fun forgetPwd(code: String, mobile: String, newPassword: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(com.gb.sockt.accountcenter.api.AccountCenterApi::class.java).forgetPwd(com.gb.sockt.accountcenter.data.domain.ForgetPwdBean(code, mobile, newPassword)).compose()
    }




}