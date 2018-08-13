package com.gb.sockt.usercenter.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.alibaba.android.arouter.launcher.ARouter
import com.example.baselibrary.base.BaseMvpActivity
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.baselibrary.utils.SpUtils
import com.example.baselibrary.utils.databus.RxBus
import com.example.provider.router.RouterPath
import com.gb.sockt.usercenter.R
import com.gb.sockt.usercenter.injection.component.DaggerUserComponent
import com.gb.sockt.usercenter.injection.module.UserModule
import com.gb.sockt.usercenter.mvp.presenter.impl.LoginPresenterImpl
import com.gb.sockt.usercenter.mvp.view.LoginView
import com.jakewharton.rxbinding2.view.RxView
import com.mob.tools.utils.UIHandler
import com.orhanobut.logger.Logger
import de.greenrobot.event.EventBus
import de.greenrobot.event.Subscribe
import de.greenrobot.event.ThreadMode
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_input_phone.*
import kotlinx.android.synthetic.main.center_layout.*
import kotlinx.android.synthetic.main.weixin_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.HashMap
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * 输入手机号页面
 */
class InputPhoneActivity : BaseMvpActivity<LoginPresenterImpl>(), LoginView, PlatformActionListener, Handler.Callback {



    private var codeType: String? = null

    private lateinit var mobile: String

    private lateinit var nickname: String
    private lateinit var openid: String
    private lateinit var appContext: Context

    override fun onError(p0: Platform?, action: Int, e: Throwable?) {
        val msg = Message()
        msg.what = WEIXIN_MSG_ACTION_CALLBACK
        msg.arg1 = 2
        msg.arg2 = action
        msg.obj = e
        UIHandler.sendMessage(msg, this)
    }

    /**
     * 该方法处于子线程
     */
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
                        RxBus.getInstance().chainProcess {
                            R.string.man
                        }

                    2 ->
                        RxBus.getInstance().chainProcess {
                            R.string.women
                        }
                }
            }
            runOnUiThread {
                mPresenter.weiXinLogin(openid, nickname)
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

    override fun showError(error: String, errorCode: Int) {
        toast(error)
    }

    override fun loginSuccess() {
        toast("登陆成功")
        ARouter.getInstance().build(RouterPath.Main.PATH_HOME)
                .withTransition(R.anim.anim_in, R.anim.anim_out)
                .navigation()
        finish()
    }

    override fun loginFail(msg: String) {
        toast(msg)
    }

    override fun onError(error: String) {
    }

    override fun onDataIsNull() {
    }

    override fun layoutId(): Int = R.layout.activity_input_phone


    override fun initData() {
        codeType = intent.extras.getString(ConstantSP.CODE_TYPE)
        appContext = BaseApplication.getAppContext()
        //注册销毁activity事件
        EventBus.getDefault().register(this)

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    fun onFinish(b: Boolean) {
        toast("finish")
        this@InputPhoneActivity.finish()
    }

    override fun initView(savedInstanceState: Bundle?) {
        when (codeType) {
            ConstantSP.USER_TYPE_REGIST//注册
            -> {
                tv_title.setText("注册账户")
                tv_fast_login_content.setText("账号密码登录")
                tv_forget_pwd_content.setText("手机验证码登录")
            }
            ConstantSP.USER_TYPE_FORGET_PWD -> {
                tv_title.setText("找回登录密码")
                tv_regist.setVisibility(View.VISIBLE)
                center_layout.setVisibility(View.GONE)
            }
            ConstantSP.USER_LOGIN_FOR_FASTLOGIN -> {
                tv_fast_login_content.setText("账号密码登录")
                tv_title.setText("手机验证码登录")
                tv_regist.setVisibility(View.VISIBLE)
            }
        }


        //监听EditText
        et_phone.run {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    val s = s.toString().trim { it <= ' ' }
                    if (s.isEmpty()) {
                        bt_next.setBackgroundResource(R.drawable.normal_button)
                    } else {
                        bt_next.setBackgroundResource(R.drawable.press_button)
                    }
                }
            })

            setOnEditorActionListener({ _, _, _ -> false })
        }


        //下一步
        RxView.clicks(bt_next)
                .throttleFirst(2, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(Function<Any, ObservableSource<Boolean>> {
                    mobile = et_phone.text.toString().trim()
                    if (mobile.isEmpty()) {
                        toast("手机号码不能为空")
                        return@Function Observable.empty()
                    }
                    val isMobile = Pattern.matches(mPresenter.REGEX_MOBILE, mobile)
                    if (!isMobile) {
                        toast("手机号码不符合规范")
                        return@Function Observable.empty()
                    }
                    return@Function Observable.just(true)
                })
                .subscribe {
                    when (codeType) {
                        ConstantSP.USER_TYPE_REGIST ->//注册
//                            mPresenter.checkPhoneISRegister(mobile, codeType)
                            //显示发送短信提示弹窗
                            showSendCodeDialog(mobile)
                        ConstantSP.USER_TYPE_FORGET_PWD ->
//                            mPresenter.checkPhoneISRegister(mobile, codeType)
                            showSendCodeDialog(mobile)
                        ConstantSP.USER_LOGIN_FOR_FASTLOGIN ->
                            //显示发送短信提示弹窗
                            showSendCodeDialog(mobile)
                    }
                }

        //右侧文字
        tv_forget_pwd.onClick {
            when (codeType) {
                ConstantSP.USER_TYPE_REGIST//注册
                -> {
                    val fastIntent = Intent(appContext, InputPhoneActivity::class.java)
                    fastIntent.putExtra(ConstantSP.CODE_TYPE, ConstantSP.USER_LOGIN_FOR_FASTLOGIN)
                    start2Activity(fastIntent)
                }
                ConstantSP.USER_TYPE_FORGET_PWD -> {
                    start2Activity(Intent(appContext, InputPhoneActivity::class.java))
                }
                ConstantSP.USER_LOGIN_FOR_FASTLOGIN -> {
                    val forgetIntent = Intent(appContext, InputPhoneActivity::class.java)
                    forgetIntent.putExtra(ConstantSP.CODE_TYPE, ConstantSP.USER_TYPE_FORGET_PWD)
                    start2Activity(forgetIntent)
                }
            }
        }
        //左侧文字
        tv_fast_login.onClick {
            when (codeType) {
                ConstantSP.USER_TYPE_REGIST//注册
                -> {
                    startActivity<LoginActivity>()
                    finish()
                }
                ConstantSP.USER_TYPE_FORGET_PWD -> {
                    val intent = Intent(appContext, InputPhoneActivity::class.java)
                    start2Activity(intent)
                }
                ConstantSP.USER_LOGIN_FOR_FASTLOGIN -> {
                    startActivity<LoginActivity>()
                    finish()
                }
            }
        }

        tv_regist.onClick {
            val intent = Intent(appContext, InputPhoneActivity::class.java)
            intent.putExtra(ConstantSP.CODE_TYPE, ConstantSP.USER_TYPE_REGIST)
            start2Activity(intent)
        }


        //微信登陆
        RxView.clicks(iv_login_wx)
                .throttleFirst(2, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribe({
                    weiXinLogin()
                })

    }

    private fun start2Activity(intent: Intent) {
        if (appContext !is Activity) {
            //调用方没有设置context或app间组件跳转，context为application
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        appContext.startActivity(intent)
        this@InputPhoneActivity.finish()
    }


    /**
     * 显示是否确认发送短信弹窗
     */
    override fun showSendCodeDialog(phoneNumber: String?) {
        val builder = AlertDialog.Builder(this@InputPhoneActivity)
        val v = layoutInflater.inflate(R.layout.dialog_queren_phone, null)
        val connectDialog = builder
                .setView(v).create()
        connectDialog.setCancelable(false)
        val window = connectDialog.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.CENTER_HORIZONTAL
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = wlp!!
        //        TextView title = (TextView) v.findViewById(R.id.tv_dialog_title)
        val cancel = v.findViewById(R.id.cancel) as TextView
        val ok = v.findViewById(R.id.ok) as TextView
        val spanableInfo = SpannableString(
                "我们将要发送验证码到这个手机号:")
        val foregroundColorSpan = ForegroundColorSpan(BaseApplication.getAppContext().resources.getColor(R.color.blue))
        spanableInfo.setSpan(foregroundColorSpan, 6, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        (v.findViewById<View>(R.id.tv_dialog_message) as TextView).text = spanableInfo
        phoneNumber?.let {
            (v.findViewById<View>(R.id.tv_phone) as TextView).text = "+86 $phoneNumber"
        }
        cancel.setOnClickListener {
            if (!isFinishing) {
                connectDialog.dismiss()
            }
            et_phone?.setText("")

        }
        ok.setOnClickListener({
            if (!isFinishing) {
                connectDialog.dismiss()
            }
            val msgCount = SpUtils.getInt(BaseApplication.getAppContext(), ConstantSP.MSG_COUNT)
            if (msgCount <= 0) {
                toast(R.string.notify_msg_count)
                return@setOnClickListener
            }
            codeType?.let {
                //发送短信
                when (it) {
                    ConstantSP.USER_TYPE_REGIST//注册
                    -> {
                        redirectTo(mobile, "2", it)
                    }
                    ConstantSP.USER_TYPE_FORGET_PWD//忘记密码
                    -> {
                        redirectTo(mobile, "3", it)
                    }
                    ConstantSP.USER_LOGIN_FOR_FASTLOGIN//快捷登陆
                    -> {
                        redirectTo(mobile, "A", it)
                    }
                }
            }
        }
        )
        if (!isFinishing) {
            connectDialog.show()
        }
    }

    private fun redirectTo(mobile: String, tag: String, codeType: String) {
        startActivity<InputVerificationCodeActivity>(
                ConstantSP.MOBILE to mobile,
                ConstantSP.LOGIN_TYPE to codeType,
                ConstantSP.CODE_TAG to tag
        )
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

    //微信登录
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

    override fun showNotifyDialog(title: String?) {
        val layout = layoutInflater.inflate(R.layout.voice_dialog, findViewById(R.id.dialog))
        val tvContent = layout.findViewById<View>(R.id.tvname) as TextView
        title?.let {
            tvContent?.text = it
        }
        AlertDialog.Builder(this).setView(layout).show()
    }

}
