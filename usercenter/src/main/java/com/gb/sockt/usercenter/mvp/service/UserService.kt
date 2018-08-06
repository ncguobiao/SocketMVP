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
    fun login(userName: String, pwd: String): Observable<BaseResp>


    /**
     * 微信登陆
     */
    fun weixinLogin(openid: String, nickname: String) : Observable<BaseResp>
}