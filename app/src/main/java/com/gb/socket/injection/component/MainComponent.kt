package com.gb.sockt.usercenter.injection.component

import android.app.Activity
import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.scope.PerComponentScope
import com.gb.socket.injection.module.MainModule
import com.gb.socket.ui.activity.MainActivity
import com.gb.sockt.usercenter.injection.module.UserModule
import com.gb.sockt.usercenter.ui.activity.InputPhoneActivity
import com.gb.sockt.usercenter.ui.activity.InputVerificationCodeActivity
import com.gb.sockt.usercenter.ui.activity.LoginActivity
import dagger.Component

/**
 * Created by guobiao on 2018/8/5.
 */

@PerComponentScope
@Component(dependencies = [(ActivityComponent::class)],
        modules = [(MainModule::class)])
interface MainComponent {

    fun inject(activity: MainActivity)

}