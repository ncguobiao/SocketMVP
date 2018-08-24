package com.gb.sockt.accountcenter.api

import com.example.baselibrary.api.UriConstant
import com.example.baselibrary.common.BaseResp

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by guobiao on 2018/8/4.
 */
interface AccountCenterApi {

    /**
     * 登陆
     */
//    @POST("UserService/User/Login")
    @POST(UriConstant.TEST_USER_URL+"UserService/User/Login")
    fun login(@Body req: com.gb.sockt.accountcenter.data.domain.LoginReq): Observable<BaseResp>


    /**
     * 微信登陆
     */
    @POST("UserService/User/wechatLogin")
//    @POST(Constant.USER_URL+"UserService/User/wechatLogin")
    fun weixinLogin(@Body req: com.gb.sockt.accountcenter.data.domain.WeiXinLoginReq): Observable<BaseResp>


    /**
     * 检验手机好是否注册
     */
    @POST("UserService/User/isNumberExist")
    fun checkPhoneISRegister(@Body req: com.gb.sockt.accountcenter.data.domain.LoginReq): Observable<BaseResp>

    /**
     * 获取短信验证码
     */
    @POST("CommonParameter/SendSms/sendSmsTask")
    fun getMsgCode(@Body req: com.gb.sockt.accountcenter.data.domain.GetCodeReq): Observable<BaseResp>

    /**
     * 用户注册
     */
    @POST("UserService/User/SignUp")
//    @POST(Constant.USER_COMMON_URL+"UserService/User/SignUp")
    fun register(@Body registerReq: com.gb.sockt.accountcenter.data.domain.RegisterReq): Observable<BaseResp>

    /**
     * 忘记密码
     */
    @POST("UserService/User/ForgetPassword")
    fun forgetPwd(@Body req: com.gb.sockt.accountcenter.data.domain.ForgetPwdBean): Observable<BaseResp>


    /**
     * 快捷登陆
     */
    @POST("UserService/User/fastLogin")
    fun fastLogin(@Body req: com.gb.sockt.accountcenter.data.domain.FastLoginReq): Observable<BaseResp>

//    /**
//     * 获取用户信息
//     */
//    @POST("UserService/rest/executeAll")
////    @POST(Constant.USER_URL+"UserService/rest/executeAll")
//    fun getUserInfo(@Body req: ReqPramas): Observable<BaseResp>
}