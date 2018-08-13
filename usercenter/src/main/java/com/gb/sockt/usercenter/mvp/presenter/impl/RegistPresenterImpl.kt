package com.gb.sockt.usercenter.mvp.presenter.impl

import com.alibaba.fastjson.JSON
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.*
import com.example.baselibrary.compose
import com.example.baselibrary.utils.SpUtils
import com.gb.sockt.usercenter.data.domain.LoginBean
import com.gb.sockt.usercenter.mvp.presenter.RegistPresenter
import com.gb.sockt.usercenter.mvp.service.UserService
import com.gb.sockt.usercenter.mvp.view.RegisterView
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/6.
 */
class RegistPresenterImpl @Inject constructor() : RegistPresenter, BasePresenter<RegisterView>() {


    @Inject
    lateinit var service: UserService

    /**
     * 获取短信验证码
     */
    override fun getMsgCode(mobile: String, tag: String) {
        if (!preparReq(getView(), this)) return
        service.getMsgCode(mobile, tag)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            getView()?.getSmscodeResult(true)
                        } else {
                            getView()?.getSmscodeResult(false)
                        }
                    }
                }, lifecycleProvider)
        getView()?.loginResultSuc()
    }


    /**
     * 用户注册后登陆
     */
    override fun register(mobile: String, username: String, pwd: String, code: String) {
        if (!preparReq(getView(), this)) return
        service.register(mobile, username, pwd, code)
                .compose()
                .compose(lifecycleProvider.bindToLifecycle())
//                .doOnNext(object :Consumer<BaseResp>{
//                    override fun accept(t: BaseResp?) {
//                        //先根据注册的响应结果去做一些操作
//                    }
//                })
                .map(object : Function<BaseResp, Boolean> {
                    //将注册结果转换成Boolean
                    override fun apply(t: BaseResp): Boolean {
                        if ("0000" == t.returnCode) {
                            getView()?.registSuc()
                            return true
                        }
                        getView()?.registSucFail(t.returnMsg)
                        return false
                    }

                })
                .observeOn(Schedulers.io())
                .flatMap(object : Function<Boolean, ObservableSource<BaseResp>> {
                    //登陆
                    override fun apply(t: Boolean): ObservableSource<BaseResp> {
                        if (t) {
                            return service.login(mobile, pwd)
                        }
                        return Observable.empty()
                    }
                })
//                .flatMap(object :Function<BaseResp,ObservableSource<BaseResp>>{
//                    override fun apply(t: BaseResp): ObservableSource<BaseResp> {
//                      if ("0000" == t.returnCode){
//                          return service.login(mobile,pwd)
//                      }
//                        return Observable.empty()
//                    }
//
//                })
                .compose()
                .subscribe(Consumer<BaseResp> { it ->

                    if ("0000" == it?.returnCode) {
                        val jsonData = it.retnrnJson.toString().trim()
//                            L.i("jsonData="+jsonData)
                        val data = JSON.parseObject(jsonData, LoginBean::class.java)
//                            val jsonReader = JsonReader(StringReader(jsonData.toString()))//其中jsonContext为String类型的Json数据
//                            jsonReader.isLenient = true
//                            val data = Gson().fromJson<LoginBean>(jsonReader, LoginBean::class.java)
                        if (data != null) {
                            //保存用户数据
                            successLoginAndSave(data, ConstantSP.USER_LOGIN_FOR_PWD)
                        } else {
                            getView()?.loginResultSuc()
                        }

                    } else {
                        it?.returnMsg?.let { it1 -> getView()?.loginResultFail(it1) }
                    }
                }, Consumer<Throwable> { t -> getView()?.loginResultFail(t.toString()) })

    }

    /**
     * 保存登陆信息
     */
    private fun successLoginAndSave(data: LoginBean, loginType: String) {
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_ID, data.id)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.MOBILE, data.mobile)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.AUTH_TOKEN, data.authToken)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.IS_LOGIN, true)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_NAME, data.userName)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.LOGIN_TYPE, loginType)//普通登录
        getView()?.loginResultSuc()
    }


    /**
     * 忘记密码
     */
    override fun forgetPwd(code: String, mobile: String, newPassword: String) {
        if (!preparReq(getView(), this)) return
        service.forgetPwd(code, mobile, newPassword)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            SpUtils.put(BaseApplication.getAppContext(), ConstantSP.IS_LOGIN, false)
                            SpUtils.remove(ConstantSP.USER_PWD)
                            getView()?.forgetPwdSuc()
                        } else {
                            getView()?.forgetPwdFail(t.returnMsg)
                        }
                    }
                }, lifecycleProvider)
    }

    /**
     * 快捷登陆
     */
    override fun fastLogin(mobile: String, code: String) {
        if (!preparReq(getView(), this)) return
        service.fastLogin(code, mobile)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            val jsonData = t.retnrnJson.toString().trim()
//                            L.i("jsonData="+jsonData)
                            val data = JSON.parseObject(jsonData, LoginBean::class.java)
//
                            if (data != null) {
                                successLoginAndSave(data, ConstantSP.USER_LOGIN_FOR_FASTLOGIN)
                            } else {
                                getView()?.loginResultFail(t.retnrnJson.toString())
                            }
                        } else if ("0003" == t.returnCode) {
                            getView()?.loginResultFail("验证码不匹配")
                        } else {
                            getView()?.loginResultFail(t.retnrnJson.toString())
                        }
                    }
                }, lifecycleProvider)
    }
    /**
     * 校验手机是否已注册
     */
//    override fun checkPhoneISRegister(mobile:String,loginType:String?) {
//        if (!preparReq(getView(), this)) return
//        service.checkPhoneISRegister(mobile)
//                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
//                    override fun onNext(t: BaseResp) {
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
//                    }
//                }, lifecycleProvider)
//
//    }
}