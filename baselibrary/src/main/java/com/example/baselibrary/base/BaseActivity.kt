package com.example.baselibrary.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.example.baselibrary.R
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.databus.RxBus
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

/**
 * Created by Alienware on 2018/5/31.
 */
open class BaseActivity : RxAppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        AppUtils.addActivity(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppUtils.removeActivity(this)
        RxBus.getInstance().unRegister(this)
    }


    /**
     * 去开启设置页面
     */
    protected fun requestPremissionSetting(msg:String) {
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






