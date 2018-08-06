package com.gb.sockt.usercenter.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.baselibrary.base.BaseMvpActivity
import com.gb.sockt.usercenter.R
import com.gb.sockt.usercenter.mvp.presenter.UserCenterPresenter

class InputPhoneActivity : BaseMvpActivity<UserCenterPresenter>(){

    override fun onError(error: String) {
    }

    override fun onDataIsNull() {
    }

    override fun layoutId(): Int = R.layout.activity_input_phone

    override fun initData() {
    }

    override fun initView() {
    }

    override fun initComponent() {

    }

    override fun start() {
    }
}
