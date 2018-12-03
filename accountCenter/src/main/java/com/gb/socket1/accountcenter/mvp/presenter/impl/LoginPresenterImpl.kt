package com.gb.socket1.accountcenter.mvp.presenter.impl

import com.alibaba.fastjson.JSON
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.*
import com.example.baselibrary.utils.SpUtils
import com.gb.socket1.accountcenter.data.domain.LoginBean
import com.gb.socket1.accountcenter.data.domain.WeiXinLoginSuccessBean
import com.gb.socket1.accountcenter.mvp.presenter.LoginPresenter
import com.gb.socket1.accountcenter.mvp.service.UserService
import com.gb.socket1.accountcenter.mvp.view.LoginView
import com.orhanobut.logger.Logger
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/5.
 */
open class LoginPresenterImpl @Inject constructor() : LoginPresenter, BasePresenter<LoginView>() {
    @Inject
    lateinit var service: UserService

    /**
     * 登陆
     */
    override fun login(userName: String, pwd: String, operateType:String, pushId:String) {
        if (!preparReq(getView(), this)) return
        service.login(userName, pwd,operateType, pushId)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            val jsonData = t.retnrnJson.toString().trim()
//                            L.i("jsonData="+jsonData)
                            val data = JSON.parseObject(jsonData, LoginBean::class.java)
//                            val jsonReader = JsonReader(StringReader(jsonData.toString()))//其中jsonContext为String类型的Json数据
//                            jsonReader.isLenient = true
//                            val data = Gson().fromJson<LoginBean>(jsonReader, LoginBean::class.java)
                            if (data != null) {
                                //保存用户数据
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.USER_ID, data.id)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.MOBILE, data.mobile)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.AUTH_TOKEN, data.authToken)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.IS_LOGIN, true)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.USER_NAME, data.userName)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.LOGIN_TYPE, ConstantSP.USER_LOGIN_FOR_PWD)//普通登录
                                getView()?.loginSuccess()
                                Logger.e("账号密码登陆")
                            } else {
                                getView()?.onError("登陆失败,用户数据不存在")
                            }
                        } else {
                            getView()?.onError("登陆失败，原因:${t.returnMsg},错误码:${t.returnCode}")
                        }
                    }
                }, lifecycleProvider)
//                .subscribe {
//                        data->
//                    Log.e("userInfo", data.toString())
//        }
    }

    override fun weiXinLogin(openid: String?, nickname: String?, operateType:String, pushId:String) {
        if (!preparReq(getView(), this)) return
        if (openid.isNullOrEmpty() or nickname.isNullOrEmpty()) {
            Logger.e("微信登陆参数异常")
            return
        }
        service.weixinLogin(openid!!, nickname!!,operateType, pushId)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            val jsonData = t.retnrnJson.toString().trim()
//                            L.i("jsonData="+jsonData)
                            val data = JSON.parseObject(jsonData, WeiXinLoginSuccessBean::class.java)
//                            val jsonReader = JsonReader(StringReader(jsonData.toString()))//其中jsonContext为String类型的Json数据
//                            jsonReader.isLenient = true
//                            val data = Gson().fromJson<LoginBean>(jsonReader, LoginBean::class.java)
                            if (data != null) {
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.USER_ID, data.id)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.MOBILE, data.mobile)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.AUTH_TOKEN, data.authToken)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.IS_LOGIN, true)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.USER_NAME, data.userName)
                                SpUtils.put(BaseApplication.getApplication(), ConstantSP.LOGIN_TYPE, ConstantSP.USER_LOGIN_FOR_WEIXIN)//微信登陆
                                getView()?.loginSuccess()
                                Logger.e("微信登陆成功")
                            } else {
                                getView()?.onError("登陆失败,用户数据不存在")
                            }
                        } else {
                            getView()?.onError("登陆失败，原因:${t.returnMsg},错误码:${t.returnCode}")
                        }
                    }
                }, lifecycleProvider)
    }


    /**
     * 校验手机是否已注册
     */
    override fun checkPhoneISRegister(mobile: String, codeType: String?) {
        if (!preparReq(getView(), this)) return
        service.checkPhoneISRegister(mobile)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {

                        when (codeType) {
                            ConstantSP.USER_TYPE_REGIST -> when {
                                "0000" == t.returnCode -> getView()?.showSendCodeDialog(mobile)
                                "0001" == t.returnCode -> getView()?.showNotifyDialog("该手机号已存在")
                                "0002" == t.returnCode -> getView()?.showSendCodeDialog(mobile)
                                "0003" == t.returnCode -> getView()?.showNotifyDialog("参数为空")
                                "0004" == t.returnCode -> getView()?.showNotifyDialog("参数不是json")
                                "0004" == t.returnCode -> getView()?.showNotifyDialog("手机号为空")
                            }
                            ConstantSP.USER_TYPE_FORGET_PWD -> when {
                                "0000" == t.returnCode -> {
                                    //                            showSendCodeDialog(phone);
                                }
                                "0001" == t.returnCode -> getView()?.showSendCodeDialog(mobile)

                            //                            showNotifyDialog("该手机号已存在");
                                "0002" == t.returnCode -> getView()?.showNotifyDialog("该账号未注册")
                                "0003" == t.returnCode -> getView()?.showNotifyDialog("参数为空")
                                "0004" == t.returnCode -> getView()?.showNotifyDialog("参数不是json")
                                "0004" == t.returnCode -> getView()?.showNotifyDialog("手机号为空")
                            }
                        }

//                        if ("0000" == t.returnCode) {
//                            val jsonData = t.retnrnJson.toString().trim()
////                            L.i("jsonData="+jsonData)
//                            val data = JSON.parseObject(jsonData, LoginBean::class.java)
////                            val jsonReader = JsonReader(StringReader(jsonData.toString()))//其中jsonContext为String类型的Json数据
////                            jsonReader.isLenient = true
////                            val data = Gson().fromJson<LoginBean>(jsonReader, LoginBean::class.java)
//                            if (data != null) {
//
//
////                                getView()?.loginSuccess(data)
//                            } else {
//                                getView()?.onDataIsNull()
//                            }
//                        } else {
//                            getView()?.onDataIsNull()
//                        }
                    }
                }, lifecycleProvider)

    }

}