package com.gb.sockt.usercenter.mvp.service.impl

import com.example.baselibrary.common.BaseResp
import com.gb.sockt.usercenter.data.repository.UserRepository
import com.gb.sockt.usercenter.mvp.service.UserService
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/5.
 */
class UserServiceImpl @Inject constructor():UserService{



    @Inject
    lateinit var repository: UserRepository

    /**
     * 登陆
     */
    override fun login(userName: String, pwd: String): Observable<BaseResp> {
        return repository.login(userName,pwd)
    }

    /**
     * 微信登陆
     */
    override fun weixinLogin(openid: String, nickname: String): Observable<BaseResp> {
        return repository.weixinLogin(openid,nickname)
    }

    /**
     * 校验手机是否已注册
     */
    override fun checkPhoneISRegister(mobile: String): Observable<BaseResp> {
        return repository.checkPhoneISRegister(mobile)

    }

    /**
     * 获取短信验证码
     */
    override fun getMsgCode(mobile: String, tag: String): Observable<BaseResp> {
        return repository.getMsgCode(mobile,tag)
    }

    /**
     * 用户注册
     */
    override fun register(mobile: String, username: String, pwd: String, code: String): Observable<BaseResp> {

        return repository.register(mobile,username,pwd, code)
    }

    /**
     * 忘记密码
     */
    override fun forgetPwd(code: String, mobile: String, newPassword: String): Observable<BaseResp> {
        return repository.forgetPwd(code,mobile,newPassword)
    }

    /**
     * 快捷登陆
     */
    override fun fastLogin(code: String, mobile: String): Observable<BaseResp> {
        return repository.fastLogin(code,mobile)
    }

}