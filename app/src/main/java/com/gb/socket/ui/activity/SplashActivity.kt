package com.gb.socket.ui.activity

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.utils.SpUtils
import com.gb.socket.App
import com.gb.socket.R
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.*
import com.example.baselibrary.utils.BluetoothClientManager
import com.gb.sockt.usercenter.ui.activity.LoginActivity
import com.orhanobut.logger.Logger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


/**
 * Created by guobiao on 2018/8/4.
 * 启动页
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        initData()
    }


    fun initData() {
        init_msg_count()
        requestPermissions()
    }


    //初始化获取短信次数
    private fun init_msg_count() {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        Log.d("test", currentDate.toString() + "")
        val date = SpUtils.getInt(App.getAppContext(), ConstantSP.CURRENT_DATE)
        if (date != currentDate) {
            SpUtils.put(App.getAppContext(), ConstantSP.CURRENT_DATE, currentDate)
            SpUtils.put(App.getAppContext(), ConstantSP.MSG_COUNT, 5)
            SpUtils.put(App.getAppContext(), ConstantSP.VOICE_MSG_COUNT, 2)
        }
    }


    fun layoutId(): Int {
        return R.layout.activity_splash
    }

    private fun requestPermissions() {
        val rxPermission = RxPermissions(this)
        rxPermission
                .requestEach(
                        Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.CAMERA
                        , Manifest.permission.READ_SMS
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .subscribe { permission ->
                    permission?.let {
                        when {
                            it.granted -> {
                                // 用户已经同意该权限
                                Logger.d(it.name + " is granted.")
                                redirectTo()
//                                when {
//                                    it.name == Manifest.permission.ACCESS_FINE_LOCATION -> {
//                                        jumpToOtherActivity()
//                                    }
//                                    else->toast("这个应用程序需要相应的权限才能正常运行")
//                                }

                            }
                            it.shouldShowRequestPermissionRationale -> // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                Logger.d(it.name + " is denied. More info should be provided.")
                            else -> {
                                // 用户拒绝了该权限，并且选中『不再询问』
                                Logger.d(it.name + " is denied.")
                                toast("这个应用程序需要相应的权限才能正常运行！")
//                                finish()
                            }
                        }
                    }
                }

//        rxPermission.request(
//                Manifest.permission.ACCESS_FINE_LOCATION
//                , Manifest.permission.ACCESS_COARSE_LOCATION
//                , Manifest.permission.CAMERA
//                , Manifest.permission.READ_SMS
//                , Manifest.permission.WRITE_EXTERNAL_STORAGE
//                , Manifest.permission.READ_EXTERNAL_STORAGE
//        ).subscribe({
//            if (it){
//                //申请的权限全部允许
//                redirectTo()
//            }else{
//                //只要有一个权限被拒绝，就会执行
//            }
//        })
    }


    private fun redirectTo() {
        val bluetoothOpened = BluetoothClientManager.getClient().isBluetoothOpened
        if (!bluetoothOpened) {
            val b = BluetoothClientManager.getClient().openBluetooth()
        }
        val isLogin = SpUtils.getBoolean(App.getAppContext(), ConstantSP.IS_LOGIN)
        if (isLogin) {
            startActivity<MainActivity>()
            finish()
        } else {
            Handler().postDelayed({
                startActivity<LoginActivity>()
                finish()
            }, 1500)
        }


    }

}