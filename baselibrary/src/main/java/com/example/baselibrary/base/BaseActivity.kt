package com.example.baselibrary.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.example.baselibrary.utils.databus.RxBus
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

/**
 * Created by Alienware on 2018/5/31.
 */
open class BaseActivity : RxAppCompatActivity() {

    override fun onDestroy() {
        super.onDestroy()
        RxBus.getInstance().unRegister(this)
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






