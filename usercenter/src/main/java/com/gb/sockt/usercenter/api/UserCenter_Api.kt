package com.gb.sockt.usercenter.api

import com.example.baselibrary.api.UriConstant
import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.Constant
import com.gb.sockt.usercenter.data.domain.*
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
//    @POST("UserService/User/Login")
    @POST(UriConstant.TEST_USER_URL+"UserService/User/Login")
    fun login(@Body req: LoginReq): Observable<BaseResp>


    /**
     * 微信登陆
     */
    @POST("UserService/User/wechatLogin")
//    @POST(Constant.USER_URL+"UserService/User/wechatLogin")
    fun weixinLogin(@Body req: WeiXinLoginReq): Observable<BaseResp>


    /**
     * 检验手机好是否注册
     */
    @POST("UserService/User/isNumberExist")
    fun checkPhoneISRegister(@Body req: LoginReq): Observable<BaseResp>

    /**
     * 获取短信验证码
     */
    @POST("CommonParameter/SendSms/sendSmsTask")
    fun getMsgCode(@Body req: GetCodeReq): Observable<BaseResp>

    /**
     * 用户注册
     */
    @POST("UserService/User/SignUp")
//    @POST(Constant.USER_COMMON_URL+"UserService/User/SignUp")
    fun register(@Body registerReq: RegisterReq): Observable<BaseResp>

    /**
     * 忘记密码
     */
    @POST("UserService/User/ForgetPassword")
    fun forgetPwd(@Body req: ForgetPwdBean): Observable<BaseResp>


    /**
     * 快捷登陆
     */
    @POST("UserService/User/fastLogin")
    fun fastLogin(@Body req: FastLoginReq): Observable<BaseResp>

//    /**
//     * 获取用户信息
//     */
//    @POST("UserService/rest/executeAll")
////    @POST(Constant.USER_URL+"UserService/rest/executeAll")
//    fun getUserInfo(@Body req: ReqPramas): Observable<BaseResp>
}