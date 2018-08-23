package com.example.baselibrary.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.DialogFragment
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import cn.jpush.android.api.JPushInterface.getRegistrationID
import com.example.baselibrary.R
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.databus.RxBus
import com.mylhyl.circledialog.CircleDialog
import com.mylhyl.circledialog.CircleParams
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * Created by Alienware on 2018/5/31.
 */
open class BaseActivity : RxAppCompatActivity() {

    private var mNetDialog: DialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppUtils.addActivity(this)
        super.onCreate(savedInstanceState)
        initNetBroadcastReceiver()

    }

    private lateinit var netBroadcastReceiver: NetBroadcastRecciver

    private fun initNetBroadcastReceiver() {

        val intentFilter = IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
         netBroadcastReceiver = NetBroadcastRecciver()
        registerReceiver(netBroadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        mNetDialog?.dismiss()
        mNetDialog=null
        unregisterReceiver(netBroadcastReceiver)
        RxBus.getInstance().unRegister(this)
        AppUtils.removeActivity(this)
    }


    /**
     * 去开启设置页面
     */
    protected fun requestPremissionSetting(msg: String) {
        alert("未获取${msg}权限，请到\"系统权限\"-\"应用管理中\"开启${msg}权限，方可正常使用", BaseApplication.getAppContext().getString(R.string.app_name)) {
            yesButton {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
            noButton {}
        }.show()
    }

    /**
     * 打开蓝牙设置
     */
    protected fun requestOpenBluetooth() {
        alert("蓝牙未打开，请开启蓝牙，方可正常使用", BaseApplication.getAppContext().getString(R.string.app_name)) {
            yesButton {
                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            }
            noButton {}
        }.show()
    }


    /**
     * 网络监听
     */
    inner class NetBroadcastRecciver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val connectMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager ?: return
            val mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (mobNetInfo == null || wifiNetInfo == null) {
                return
            }
            if (!mobNetInfo.isConnected && !wifiNetInfo.isConnected) {
                toast("网络连接不可用")
                showDialog()
                // unconnect network
            } else {
                dissmissNetDialog()
                // connect network
            }
        }



    }

    protected fun showDialog() {
            mNetDialog = CircleDialog.Builder()
                    .setTitle("网络未连接!")
                    .setTextColor(resources.getColor(R.color.red_normal))
                    .setText("请检查网络状态，或者手动开启网络")
                    .setNegative("取消") { mNetDialog?.dismiss() }
                    .setPositive("确定") {
                        val intent: Intent
                        mNetDialog?.dismiss()
                        if (android.os.Build.VERSION.SDK_INT > 10) {  // 3.0以上
                            intent = Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                        } else {
                            intent = Intent(Intent.ACTION_MAIN)
                            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting")
                        }
                        startActivity(intent)
                    }
                    .setCancelable(false).show(supportFragmentManager)

    }

    protected fun dissmissNetDialog() {
        mNetDialog?.dismiss()
    }

    protected fun getPushId():String{
        return getRegistrationID(this)
    }
}


/**
 * 打卡软键盘
 */
fun openKeyBord(mEditText: EditText, mContext: Context) {
    val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

/**
 * 关闭软键盘
 */
fun closeKeyBord(mEditText: EditText, mContext: Context) {
    val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
}






