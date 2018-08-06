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
}