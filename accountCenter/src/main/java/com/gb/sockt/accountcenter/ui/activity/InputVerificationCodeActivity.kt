package com.gb.sockt.accountcenter.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.SmsMessage
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.example.baselibrary.base.BaseMvpActivity
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.MD5Utils
import com.example.baselibrary.utils.SpUtils
import com.example.provider.router.RouterPath
import com.gb.sockt.accountcenter.injection.component.DaggerUserComponent
import com.gb.sockt.accountcenter.injection.module.UserModule
import com.gb.sockt.accountcenter.mvp.presenter.impl.RegistPresenterImpl
import com.gb.sockt.accountcenter.mvp.view.RegisterView
import com.gb.sockt.center.R
import com.jakewharton.rxbinding2.view.RxView
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_input_verification_code.*
import kotlinx.android.synthetic.main.reset_pwd.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Created by guobiao on 2018/8/6.
 * 短信验证码页面
 */
class InputVerificationCodeActivity : BaseMvpActivity<RegistPresenterImpl>(), RegisterView {




    private lateinit var mConsumerCountTime: Consumer<Long>
    private var etPwd: EditText? = null
    private var etRePwd: EditText? = null
    private var ivSee: ImageView? = null
    private var code: String? = null
    private var canSeePwd: Boolean = false
    private lateinit var smsBR: SmsBroadcastReceiver
    private lateinit var foregroundColorSpan: ForegroundColorSpan
    private var MAX_COUNT_TIME: Long = 61//倒计时时间

    private var mDisposable: Disposable? = null


    /**
     * 6个数字的正则表达式
     */
    private val patternCoder = "(?<!\\d)\\d{4}(?!\\d)"
    private var mobile: String? = null
    private var codeType: String? = null
    private var tag: String? = null
    private val SMS_CODE = "android.provider.Telephony.SMS_RECEIVED"

    private lateinit var spanableString: SpannableString

    override fun onError(error: String) {

    }

    override fun onDataIsNull() {

    }

    //登陆成功
    override fun loginResultSuc() {
        //发送时间清除栈内的activity

        toast(R.string.loginSuccess)
        ARouter.getInstance().build(RouterPath.Main.PATH_HOME)
                .withTransition(R.anim.anim_in, R.anim.anim_out)
                .navigation()
        AppUtils.exit()
    }

    override fun loginResultFail(error: String) {
        toast(error)
    }

    //注册成功
    override fun registSuc() {
        toast(R.string.register_success)
    }

    override fun registSucFail(error: String) {
        toast(error)
    }

    override fun getSmscodeResult(boolean: Boolean) {
        if (boolean) {
            toast("请注意查收短信验证码")
        } else {
            toast("获取短信失败,请稍后尝试")

        }

    }

    //忘记密码成功
    override fun forgetPwdSuc() {
        //清空栈内所有Activity
        val intent = Intent(this@InputVerificationCodeActivity, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    override fun forgetPwdFail(error: String) {
        toast(error)
    }

    override fun showError(error: String, errorCode: Int) {
    }

    override fun layoutId(): Int = R.layout.activity_input_verification_code

    override fun initData() {
        mobile = intent.extras.getString(ConstantSP.MOBILE)
        codeType = intent.extras.getString(ConstantSP.LOGIN_TYPE)
        tag = intent.extras.getString(ConstantSP.CODE_TAG)
        initSmsReciver()


    }

    /**
     * 初始化短信接收广播
     */
    private fun initSmsReciver() {
        smsBR = SmsBroadcastReceiver()
        val intentFilter = IntentFilter(SMS_CODE)
        intentFilter.priority = 2147483647// 设置优先级
        registerReceiver(smsBR, intentFilter)
    }


    override fun initView(savedInstanceState: Bundle?) {
        when (codeType) {
            ConstantSP.USER_TYPE_REGIST//注册
            -> {
                tv_title.text = "注册账户"
                bt_next.text = "注 册"
            }
            ConstantSP.USER_TYPE_FORGET_PWD -> {
                tv_title.text = "找回登录密码"
                bt_next.text = "完 成"
            }
            ConstantSP.USER_LOGIN_FOR_FASTLOGIN -> {
                tv_title.text = "手机验证码登录"
                bt_next.setText(R.string.login)
                ll_input_pwd.visibility = View.GONE
            }
        }

        iv_back.onClick { finish() }

        tv_time_countdown.onClick {
            showSendCodeDialog(mobile, 1)
        }

        if (ll_input_pwd != null && ll_input_pwd.visibility == View.VISIBLE) {
            etPwd = findViewById<EditText>(R.id.et_pwd)
            etRePwd = findViewById<EditText>(R.id.et_re_pwd)
            ivSee = findViewById<ImageView>(R.id.iv_see)
        }
        if (etPwd != null && etRePwd != null && et_code != null) {
            //监听EditText
            etPwd?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    setNextButtonColor(etRePwd, et_code, s)
                }
            })

            etRePwd?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    setNextButtonColor(etPwd, et_code, s)
                }
            })
        }

        ivSee?.let {
            it.setOnClickListener({
                if (!canSeePwd) {
                    //选择状态 显示明文--设置为可见的密码
                    etRePwd?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    etPwd?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    canSeePwd = true
                    it?.setBackgroundResource(R.drawable.eye_see)
                } else {
                    canSeePwd = false
                    etRePwd?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    etPwd?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    it?.setBackgroundResource(R.drawable.eye_normal)
                }
            })
        }

        val spanableInfo = SpannableString("我们将要发送验证码到这个手机号:")
        foregroundColorSpan = ForegroundColorSpan(BaseApplication.getAppContext().resources.getColor(R.color.BLUE))
        spanableInfo.setSpan(foregroundColorSpan, 6, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        tv_content.text = spanableInfo
        mobile?.let {
            if (it.isNotEmpty()) {
                tv_phone.text = "+86 $it"
            }
        }

        et_code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                setNextButtonColor(etPwd, etRePwd, s)
            }
        })

        RxView.clicks(bt_next)
                .throttleFirst(2, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(Function<Any, ObservableSource<String>> {
                    mobile?.let {
                        val isMobile = Pattern.matches(mPresenter.REGEX_MOBILE, mobile)
                        if (!isMobile) {
                            toast("手机号码不符合规范")
                            return@Function Observable.empty()
                        }
                    }
                    code = et_code.text.toString().trim({ it <= ' ' })
                    if (code == null || code!!.isEmpty()) {
                        toast("请输入验证码")
                        return@Function Observable.empty()
                    }
                    if (codeType != ConstantSP.USER_LOGIN_FOR_FASTLOGIN) {
                        val pwd = etPwd?.text.toString().trim { it <= ' ' }
                        val rePwd = etRePwd?.text.toString().trim { it <= ' ' }
                        if (rePwd.isEmpty() || pwd.isEmpty()) {
                            toast(R.string.input_pwd)
                            return@Function Observable.empty()
                        }
                        val matches = Pattern.matches(mPresenter.REGEX_PASSWORD, pwd)
                        if (pwd != rePwd) {
                            toast(R.string.input_pwd_different)
                            return@Function Observable.empty()
                        }
                        if (!matches) {
                            toast(R.string.is_not_pwd)
                            return@Function Observable.empty()
                        }
                        return@Function Observable.just(MD5Utils.toMD5(pwd))
                    }
                    return@Function Observable.just(code)

                }).subscribe {
                    when (codeType) {
                        ConstantSP.USER_TYPE_REGIST//注册
                        -> {
                            if (mobile != null && code != null) {
                                mPresenter.register(mobile!!, mobile!!, it, code!!,"Android",getPushId())
                            }
                        }
                        ConstantSP.USER_TYPE_FORGET_PWD//忘记密码
                        -> {
                            if (mobile != null && code != null) {
                                mPresenter.forgetPwd(code!!, mobile!!, it)
                            }
                        }
                        ConstantSP.USER_LOGIN_FOR_FASTLOGIN -> if (mobile != null && code != null) {
                            mPresenter.fastLogin(mobile!!, it,"Android",getPushId())
                        }
                        else -> Logger.e("语音验证码类型错误")
                    }
                }

        //语音验证
        val mObservableCountTime = RxView.clicks(bt_voice_code)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(Function<Any, ObservableSource<Boolean>> {
                    val voiceMsgCount: Int = SpUtils.getInt(BaseApplication.getAppContext(), ConstantSP.VOICE_MSG_COUNT)
                    if (voiceMsgCount <= 0) {
                        toast(R.string.notify_msg_count)
                        return@Function Observable.empty()
                    }
                    mobile?.let {
                        val isMobile = Pattern.matches(mPresenter.REGEX_MOBILE, mobile)
                        if (!isMobile) {
                            toast("手机号码不符合规范")
                            return@Function Observable.empty()
                        }
                    }
                    Observable.just(true)
                }).doOnNext {
                    //                    Logger.e("doOnNext  codeType:$codeType")
                    //发送语音验证
                    when (codeType) {
                        ConstantSP.USER_TYPE_REGIST -> getCode(mobile, "8", false)
                        ConstantSP.USER_TYPE_FORGET_PWD -> getCode(mobile, "9", false)
                        ConstantSP.USER_LOGIN_FOR_FASTLOGIN -> getCode(mobile, "B", false)
                    }
                    showVoiceDialog()
                    var voiceMsgCount: Int = SpUtils.getInt(BaseApplication.getAppContext(), ConstantSP.VOICE_MSG_COUNT)
                    voiceMsgCount--
                    SpUtils.put(BaseApplication.getAppContext(), ConstantSP.VOICE_MSG_COUNT, voiceMsgCount)
                }
                .flatMap(countDown())

        //倒计时
        mConsumerCountTime = Consumer<Long> { aLong ->
            //当倒计时为 0 时，还原 btn 按钮
//            Logger.d("Observable thread is :${Thread.currentThread().name} ")
            if (aLong == 0L) {
//                RxView.enabled(tv_time_countdown).accept(true)
                bt_voice_code?.visibility = View.VISIBLE
//                RxView.enabled(bt_voice_code).accept(true)
//                RxTextView.text(bt_voice_code).accept("发送验证码")
                tv_time_countdown?.setTextColor(BaseApplication.getAppContext().resources.getColor(R.color.BLUE))
                tv_time_countdown?.setText(R.string.not_get_verification_code)
            } else {
//                RxTextView.text(bt_voice_code).accept("剩余 $aLong 秒")
//                Logger.d("剩余 $aLong 秒")
//                RxView.enabled(tv_time_countdown).accept(false)
                bt_voice_code?.visibility = View.INVISIBLE
                spanableString = SpannableString("接收验证码大约需要${aLong}秒")
                tv_time_countdown?.text = spanableString
            }
        }

        //订阅
        mDisposable = mObservableCountTime.subscribe(mConsumerCountTime)

        //语音验证

    }


    private fun countDown(): Function<Any, ObservableSource<Long>> {
        return object : Function<Any, ObservableSource<Long>> {
            override fun apply(t: Any): ObservableSource<Long>? {
                //更新发送按钮的状态并初始化显现倒计时文字
                tv_time_countdown?.setTextColor(BaseApplication.getAppContext().resources.getColor(R.color.text_light_color))
//                Logger.e("countDown")
                return Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                        .take(MAX_COUNT_TIME)
                        //将递增数字替换成递减的倒计时数字
                        .map(object : Function<Long, Long> {
                            override fun apply(aLong: Long): Long {
//                                Logger.d("aLong:$aLong")
//                                Logger.d("map thread is :${Thread.currentThread().getName()} ")
                                return MAX_COUNT_TIME - (aLong + 1)
                            }
                            //切换到 Android 的主线程。
                        }).observeOn(AndroidSchedulers.mainThread())

            }

        }
    }

    /**
     * 获取短信验证码
     */
    private fun getCode(mobile: String?, type: String?, isNormalCode: Boolean) {
        if (mobile != null && type != null) {
            mPresenter.getMsgCode(mobile!!, type!!)
            if (isNormalCode) {
                mDisposable = Observable.just(true)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnNext {

                            var msgCount = SpUtils.getInt(BaseApplication.getAppContext(), ConstantSP.MSG_COUNT)
                            msgCount--
                            SpUtils.put(BaseApplication.getAppContext(), ConstantSP.MSG_COUNT, msgCount)
                        }
                        .flatMap(countDown())
                        .subscribe(mConsumerCountTime)
            }
        }

    }

    private fun setNextButtonColor(etPwd: EditText?, RetPwd: EditText?, s: Editable) {
        val s = s.toString().trim { it <= ' ' }
        if (etPwd != null && RetPwd != null) {
            val pwd = etPwd.text.toString().trim { it <= ' ' }
            val code = RetPwd.text.toString().trim { it <= ' ' }
            if (s.isNotEmpty() && pwd.isNotEmpty() && code.isNotEmpty()) {
                bt_next.setBackgroundResource(R.drawable.press_button)
            } else {
                bt_next.setBackgroundResource(R.drawable.normal_button)
            }
        } else {
            if (s.isNotEmpty()) {
                bt_next.setBackgroundResource(R.drawable.press_button)
            } else {
                bt_next.setBackgroundResource(R.drawable.normal_button)
            }
        }
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
        getCode(mobile, tag, true)
    }

    //显示语音验证弹窗
    private fun showVoiceDialog() {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.voice_dialog,
                findViewById(R.id.dialog))
//        AlertDialog.Builder(this).setView(layout).show()
        alert {
            customView {
//                val layout = LayoutInflater.from(this@MainActivity)
//                        .inflate(com.gb.sockt.usercenter.R.layout.voice_dialog,
//                                findViewById(com.gb.sockt.usercenter.R.id.dialog))
                this.addView(layout,null)
            }
        }.show()

    }

    /**
     * 内部类 用于监听短信
     */
    private inner class SmsBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            val messages = bundle!!.get("pdus") as Array<Any> ?: return
            val smsMessage = arrayOfNulls<SmsMessage>(messages.size)
            for (n in messages.indices) {
                smsMessage[n] = SmsMessage.createFromPdu(messages[n] as ByteArray)
                val fromNumber = smsMessage[n]?.getOriginatingAddress()
                val content = smsMessage[n]?.getMessageBody()
                // 消息时间
                // String time = DateUtil.dateToStr(new Date(smsMessage[n].getTimestampMillis()));
                if (fromNumber == "10659885907900111851" || fromNumber == "1065905910861851858") {
                    // 如果是特定的电话号码的，则取出验证码
                    content?.let {
                        et_code?.setText(patternCode(content))
                    }

                } else {
//                    // 这里做实验，同样不管接到谁的短信，都取出连续六位数字
//                    content?.let {
//                        et_code.setText(patternCode(content))
//                    }
                }
            }
        }
    }

    /**
     * 短信验证匹配
     */
    fun patternCode(patternContent: String): String? {
        if (TextUtils.isEmpty(patternContent)) {
            return null
        }
        val p = Pattern.compile(patternCoder)
        val matcher = p.matcher(patternContent)
        return if (matcher.find()) {
            matcher.group()
        } else null
    }

    /**
     * 显示是否确认发送短信弹窗
     */
    private fun showSendCodeDialog(mobile: String?, type: Int) {
        val builder = android.support.v7.app.AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.dialog_queren_phone, null)
        val connectDialog = builder
                .setView(v).create()
        connectDialog.setCancelable(false)
        val window = connectDialog.window
        if (window != null) {
            val wlp = window.attributes
            wlp.gravity = Gravity.CENTER_HORIZONTAL
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = wlp
        }
        v.findViewById<TextView>(R.id.tv_dialog_title).text = "重新获取验证码"
        val spanableInfo = SpannableString(
                "我们将要发送验证码到这个手机号:")
        val foregroundColorSpan = ForegroundColorSpan(BaseApplication.getAppContext().resources.getColor(R.color.BLUE))
        spanableInfo.setSpan(foregroundColorSpan, 6, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        v.findViewById<TextView>(R.id.tv_dialog_message).text = spanableInfo
        mobile?.let {
            if (mobile.isNotEmpty()) {
                v.findViewById<TextView>(R.id.tv_phone).text = "+86 $mobile"
            }
        }
        //取消
        v.findViewById<TextView>(R.id.cancel).setOnClickListener {
            if (!isFinishing) {
                connectDialog.dismiss()
            }
            if (type == 1) {
                if (bt_voice_code?.visibility == View.INVISIBLE) {
                    bt_voice_code.visibility = View.VISIBLE
                }
            }
        }
        //好
        v.findViewById<TextView>(R.id.ok).setOnClickListener(View.OnClickListener {
            if (!isFinishing) {
                connectDialog.dismiss()
            }
            val msgCount = SpUtils.getInt(BaseApplication.getAppContext(), ConstantSP.MSG_COUNT)
            if (msgCount <= 0) {
                toast(R.string.notify_msg_count)
                return@OnClickListener
            }
            //发送短信
            when (codeType) {
            //注册
                ConstantSP.USER_TYPE_REGIST -> getCode(mobile, "2", true)
                ConstantSP.USER_TYPE_FORGET_PWD//忘记密码
                -> getCode(mobile, "3", true)
                ConstantSP.USER_LOGIN_FOR_FASTLOGIN//快捷登陆
                -> getCode(mobile, "A", true)
            }
            tv_time_countdown?.setTextColor(BaseApplication.getAppContext().resources.getColor(R.color.text_light_color))
            if (bt_voice_code?.visibility == View.VISIBLE) {
                bt_voice_code.visibility = View.INVISIBLE
            }
        }
        )
        if (!isFinishing) {
            connectDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBR)
        if (mDisposable != null) {
            mDisposable?.dispose()
            mDisposable = null
        }
    }
}