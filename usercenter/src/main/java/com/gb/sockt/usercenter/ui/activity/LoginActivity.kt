package com.gb.sockt.usercenter.ui.activity

import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.alibaba.android.arouter.launcher.ARouter
import com.example.baselibrary.base.BaseMvpActivity
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.Constant
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.data.net.execption.ErrorStatus
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.example.baselibrary.utils.MD5Utils
import com.example.baselibrary.utils.SpUtils
import com.example.baselibrary.utils.databus.RxBus
import com.example.provider.router.RouterPath
import com.gb.sockt.usercenter.R
import com.gb.sockt.usercenter.data.domain.LoginBean
import com.gb.sockt.usercenter.data.domain.WeiXinLoginSuccessBean
import com.gb.sockt.usercenter.injection.component.DaggerUserComponent
import com.gb.sockt.usercenter.injection.module.UserModule
import com.gb.sockt.usercenter.mvp.presenter.UserCenterPresenter
import com.gb.sockt.usercenter.mvp.presenter.impl.LoginPresenterImpl
import com.gb.sockt.usercenter.mvp.view.LoginView
import com.jakewharton.rxbinding2.view.RxView
import com.mob.tools.utils.UIHandler
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.weixin_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.HashMap
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Created by guobiao on 2018/8/5.
 * 注册页面
 */
class LoginActivity : BaseMvpActivity<UserCenterPresenter>(), LoginView
        , PlatformActionListener, Handler.Callback {


    private val WEIXIN_MSG_ACTION_CALLBACK: Int = 6
    private lateinit var nickname: String
    private lateinit var openid: String
    private lateinit var login_type: String
    private lateinit var mobile: String
    private lateinit var pwd: String
    private var canSeePwd: Boolean = false

    override fun onError(p0: Platform?, action: Int, e: Throwable?) {
        val msg = Message()
        msg.what = WEIXIN_MSG_ACTION_CALLBACK
        msg.arg1 = 2
        msg.arg2 = action
        msg.obj = e
        UIHandler.sendMessage(msg, this)
    }

    override fun onComplete(platform: Platform?, action: Int, res: HashMap<String, Any>?) {

        res?.let {
            if (res.containsKey("openid")) {
                openid = res["openid"] as String
                Logger.e("openid： $openid")
            }
            if (res.containsKey("nickname")) {
                nickname = res["nickname"] as String
                Logger.e("nickname： $nickname")
            }
            if (res.containsKey("headimgurl")) {
                val headimgurl = res.get("headimgurl") as String
                Logger.e("headimgurl： $headimgurl")
                SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_WEIXIN_PHOTO, headimgurl)
            }
            if (res.containsKey("sex")) {
                val sex = res["sex"] as Int
                Logger.e("sex： $sex")
                when (sex) {
                    1 ->
//                        EventBus.getDefault().post(GenderBean("男"))
                        RxBus.getInstance().chainProcess {
                            R.string.man
                        }

                    2 ->
                        RxBus.getInstance().chainProcess {
                            R.string.women
                        }
//                    EventBus.getDefault().post(GenderBean("女"))
                }
            }

            if (openid.trim().isNotEmpty() && nickname.trim().isNotEmpty()) {
                mPresenter.weiXinLogin(openid, nickname)
//                mPresenter.weiXinLogin("oE1qXwvLwjfVeTNjPFE_FiE-zVNQ", "\uF8FF just so so，吻猪")
            }
        }

    }

    override fun onCancel(platform: Platform?, action: Int) {
        val msg = Message()
        msg.what = WEIXIN_MSG_ACTION_CALLBACK
        msg.arg1 = 3
        msg.arg2 = action
        msg.obj = platform
        UIHandler.sendMessage(msg, this)
    }

    override fun handleMessage(msg: Message?): Boolean {
        when (msg?.arg1) {
            1 -> {
                // 成功
                toast("微信登陆成功")
            }
            2 -> {
                // 失败
                toast("微信登陆失败")
                val expName = msg?.obj.javaClass.simpleName
                if ("WechatClientNotExistException" == expName
                        || "WechatTimelineNotSupportedException" == expName
                        || "WechatFavoriteNotSupportedExceptin" == expName) {
                    toast("请安装微信客户端")
                }
            }
            3 -> {
                // 取消
                toast("取消···")
            }
        }
        return false
    }


    override fun onError(error: String) {
        toast(error)
    }

    override fun onDataIsNull() {
    }

    override fun loginSuccess(data: LoginBean) {
        toast("登陆成功")
        //保存用户数据
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_ID, data.id)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.MOBILE, data.mobile)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.AUTH_TOKEN, data.authToken)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.IS_LOGIN, true)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_NAME, data.userName)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_LOGIN_TYPE, ConstantSP.USER_LOGIN_FOR_PWD)//普通登录
        ARouter.getInstance().build(RouterPath.Main.PATH_HOME)
                .withTransition(R.anim.anim_in, R.anim.anim_out)
                .navigation()
        finish()
    }


    override fun weixinLoginSuccess(data: WeiXinLoginSuccessBean) {

        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_ID, data.id)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.MOBILE, data.mobile)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.AUTH_TOKEN, data.authToken)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.IS_LOGIN, true)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_NAME, data.userName)
        SpUtils.put(BaseApplication.getAppContext(), ConstantSP.USER_LOGIN_TYPE, ConstantSP.USER_LOGIN_FOR_WEIXIN)//普通登录
        ARouter.getInstance().build(RouterPath.Main.PATH_HOME)
                .withTransition(R.anim.anim_in, R.anim.anim_out)
                .navigation()
        finish()
    }

    override fun loginFail(msg: String) {
        toast(msg)
    }

    override fun showError(error: String, errorCode: Int) {
        showToast(error)
        if (errorCode == ErrorStatus.NETWORK_ERROR) {
            multipleStatusView.showNoNetwork()
        } else {
            multipleStatusView.showError()
        }
    }

    override fun layoutId(): Int = R.layout.activity_login

    override fun initData() {
    }

    override fun initView() {
        im_clear.onClick {
            et_phone.setText("")
        }
        iv_clear.onClick {
            et_pwd.setText("")
        }

        tv_regist.onClick {
            login_type = ConstantSP.USER_TYPE_REGIST
            jumpActivity(login_type)
        }
        tv_forget_pwd.onClick {
            login_type = ConstantSP.USER_TYPE_FORGET_PWD;
            jumpActivity(login_type);
        }
        tv_fast_login.onClick {
            login_type = ConstantSP.USER_LOGIN_FOR_FASTLOGIN
            jumpActivity(login_type)
        }

        iv_see.onClick {
            if (!canSeePwd) {
                //选择状态 显示明文--设置为可见的密码
                et_pwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                canSeePwd = true
                iv_see.setBackgroundResource(R.drawable.eye_see)
            } else {
                canSeePwd = false
                et_pwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_see.setBackgroundResource(R.drawable.eye_normal)
            }
        }

        RxView.clicks(bt_login)
                .throttleFirst(1, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(Function<Any, ObservableSource<Boolean>> {
                    mobile = et_phone.text.toString().trim()
                    if (mobile.isEmpty()) {
                        toast("手机号码不能为空")
                        return@Function Observable.empty()
                    }

                    pwd = et_pwd.text.toString().trim()
                    if (pwd.isEmpty()) {
                        toast(R.string.input_pwd)
                        return@Function Observable.empty()
                    }
                    val matches = Pattern.matches(mPresenter.REGEX_PASSWORD, pwd)
                    if (!matches) {
                        toast(R.string.is_not_pwd)
                        return@Function Observable.empty()
                    }
                    val isMobile = Pattern.matches(mPresenter.REGEX_MOBILE, mobile)
                    if (!isMobile) {
                        toast("手机号码不符合规范")
                        return@Function Observable.empty()
                    }
                    Observable.just(true)
                })
                .subscribe {
//                    Logger.d(it)
                    mPresenter.login(mobile, MD5Utils.toMD5(pwd))
                }

        //微信登陆
        RxView.clicks(iv_login_wx)
                .throttleFirst(2, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribe({
//                    Logger.d("微信登录")
                    weiXinLogin()
                    mPresenter.weiXinLogin("oE1qXwvLwjfVeTNjPFE_FiE-zVNQ", "\uF8FF just so so，吻猪")

                })

        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Logger.e(s.toString().trim())
                val s = s?.toString()!!.trim()
                val phone = et_phone.text?.toString()!!.trim()
                if (s.isNotEmpty() && phone.isNotEmpty()) {
                    bt_login.setBackgroundResource(R.drawable.press_button)
                } else {
                    bt_login.setBackgroundResource(R.drawable.normal_button)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Logger.e(s.toString().trim())
            }

        })

    }

    override fun initComponent() {
        DaggerUserComponent.builder()
                .activityComponent(activityComponent)
                .userModule(UserModule())
                .build()
                .inject(this)
        mPresenter.attachView(this)
    }

    override fun start() {
    }


    private fun weiXinLogin() {
        val weiXinPF = ShareSDK.getPlatform(Wechat.NAME)
        if (weiXinPF != null) {
            weiXinPF.platformActionListener = this
            if (weiXinPF.isAuthValid) {
                weiXinPF.removeAccount(true)
            }
            weiXinPF.showUser(null)
        }

    }

    private fun jumpActivity(login_type: String) {
        startActivity<InputPhoneActivity>(ConstantSP.LOGIN_TYPE to login_type)
        finish()
    }


}