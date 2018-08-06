package com.gb.sockt.usercenter.api

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.Constant
import com.gb.sockt.usercenter.data.domain.LoginReq
import com.gb.sockt.usercenter.data.domain.WeiXinLoginReq
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by guobiao on 2018/8/4.
 */
interface UserCenter_Api {

    /**
     * 登陆
     */
    @POST("UserService/User/Login")
//    @POST(Constant.USER_COMMON_URL+"UserService/User/Login")
    fun login(@Body req: LoginReq): Observable<BaseResp>


    /**
     * 微信登陆
     */
    @POST("UserService/User/wechatLogin")
//    @POST(Constant.USER_URL+"UserService/User/wechatLogin")
    fun weixinLogin(@Body req: WeiXinLoginReq): Observable<BaseResp>

//    /**
//     * 获取用户信息
//     */
//    @POST("UserService/rest/executeAll")
////    @POST(Constant.USER_URL+"UserService/rest/executeAll")
//    fun getUserInfo(@Body req: ReqPramas): Observable<BaseResp>
}