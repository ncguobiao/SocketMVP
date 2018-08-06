package com.gb.sockt.usercenter.ui.activity
import android.os.Bundle
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.utils.databus.RegisterBus
import com.example.baselibrary.utils.databus.RxBus
import com.gb.sockt.usercenter.R
import com.jakewharton.rxbinding2.view.RxView
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_regist.*
import kotlinx.android.synthetic.main.weixin_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

class RegistActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        initData()
        initView()
    }

    fun layoutId(): Int = R.layout.activity_login

    fun initData() {

        RxBus.getInstance().register(this)

    }

    fun initView() {
        //微信登陆
        RxView.clicks(bt_login)
                .throttleFirst(2, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribe({
                    Logger.d("微信登录")
                })
    }

    @RegisterBus
    fun onRecive(msg :String){
        toast(msg)
        Logger.e("msg:$msg")
    }


}
