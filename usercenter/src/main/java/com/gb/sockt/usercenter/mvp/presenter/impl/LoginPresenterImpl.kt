package com.gb.sockt.usercenter.mvp.presenter.impl

import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.alibaba.fastjson.JSON
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.BaseSubscriber
import com.example.baselibrary.common.execute
import com.example.baselibrary.common.preparReq
import com.gb.sockt.usercenter.data.domain.LoginBean
import com.gb.sockt.usercenter.data.domain.WeiXinLoginSuccessBean
import com.gb.sockt.usercenter.mvp.presenter.LoginPresenter
import com.gb.sockt.usercenter.mvp.service.UserService
import com.gb.sockt.usercenter.mvp.view.LoginView
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/5.
 */
open class LoginPresenterImpl @Inject constructor():LoginPresenter ,BasePresenter<LoginView>(){
    /**
     * 正则表达式：验证手机号
     */
    val REGEX_MOBILE = "^1(3|4|5|7|8)\\d{9}$"
    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    val REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$"
    @Inject
    lateinit var service:UserService

    /**
     * 登陆
     */
    override fun login(userName: String, pwd: String) {

        preparReq(getView(),this)
        service.login(userName, pwd)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            val jsonData = t.retnrnJson.toString().trim()
//                            L.i("jsonData="+jsonData)
                            val data= JSON.parseObject(jsonData, LoginBean::class.java)
//                            val jsonReader = JsonReader(StringReader(jsonData.toString()))//其中jsonContext为String类型的Json数据
//                            jsonReader.isLenient = true
//                            val data = Gson().fromJson<LoginBean>(jsonReader, LoginBean::class.java)
                            if (data != null) {
                                getView()?.loginSuccess(data)
                            } else {
                                getView()?.onDataIsNull()
                            }
                        } else {
                            getView()?.onDataIsNull()
                        }
                    }
                }, lifecycleProvider)
//                .subscribe {
//                        data->
//                    Log.e("userInfo", data.toString())
//        }
    }

    override fun weiXinLogin(openid: String, nickname: String) {

        preparReq(getView(),this)
        service.weixinLogin(openid,nickname)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            val jsonData = t.retnrnJson.toString().trim()
//                            L.i("jsonData="+jsonData)
                            val data= JSON.parseObject(jsonData, WeiXinLoginSuccessBean::class.java)
//                            val jsonReader = JsonReader(StringReader(jsonData.toString()))//其中jsonContext为String类型的Json数据
//                            jsonReader.isLenient = true
//                            val data = Gson().fromJson<LoginBean>(jsonReader, LoginBean::class.java)
                            if (data != null) {
                                getView()?.weixinLoginSuccess(data)
                            } else {
                                getView()?.onDataIsNull()
                            }
                        } else {
                            getView()?.onDataIsNull()
                        }
                    }
                }, lifecycleProvider)
    }


}