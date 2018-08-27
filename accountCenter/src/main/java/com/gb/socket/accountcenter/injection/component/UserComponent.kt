package com.gb.socket.accountcenter.injection.component

import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.scope.PerComponentScope
import com.gb.socket.accountcenter.injection.module.UserModule
import com.gb.socket.accountcenter.ui.activity.InputPhoneActivity
import com.gb.socket.accountcenter.ui.activity.InputVerificationCodeActivity
import com.gb.socket.accountcenter.ui.activity.LoginActivity
import dagger.Component

/**
 * Created by guobiao on 2018/8/5.
 */

@PerComponentScope
@Component(dependencies = [(ActivityComponent::class)],
        modules = [(UserModule::class)])
interface UserComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: InputPhoneActivity)
    fun inject(activity: InputVerificationCodeActivity)
}