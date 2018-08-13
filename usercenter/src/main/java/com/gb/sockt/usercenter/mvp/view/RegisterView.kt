package com.gb.sockt.usercenter.mvp.view

import com.example.baselibrary.base.IBaseView
import com.gb.sockt.usercenter.data.domain.LoginBean
import com.gb.sockt.usercenter.data.domain.WeiXinLoginSuccessBean

/**
 * Created by guobiao on 2018/8/5.
 */
interface RegisterView : IBaseView {

    fun getSmscodeResult(boolean: Boolean)

    fun registSuc()

    fun registSucFail(error: String)

    fun loginResultSuc()

    fun loginResultFail(error: String)

    fun forgetPwdSuc()

    fun forgetPwdFail(error: String)



    fun showError(error: String,errorCode:Int)
}