package com.gb.sockt.usercenter.data.repository

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.sockt.usercenter.api.UserCenter_Api
import com.gb.sockt.usercenter.data.domain.LoginReq
import com.gb.sockt.usercenter.data.domain.WeiXinLoginReq
import io.reactivex.Observable
import retrofit2.http.Body
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/5.
 */
class UserRepository @Inject constructor() {

    /**
     * 登陆
     */
    fun login(userName: String, pwd: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(UserCenter_Api::class.java).login(LoginReq(userName,pwd)).compose()
    }

    /**
     * 微信登陆
     */
    fun weixinLogin(openid:String,nickname:String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(UserCenter_Api::class.java).weixinLogin(WeiXinLoginReq(openid,nickname)).compose()
    }
}