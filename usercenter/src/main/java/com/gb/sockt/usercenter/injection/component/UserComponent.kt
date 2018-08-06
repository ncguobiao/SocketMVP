package com.gb.sockt.usercenter.injection.component

import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.scope.PerComponentScope
import com.gb.sockt.usercenter.injection.module.UserModule
import com.gb.sockt.usercenter.ui.activity.LoginActivity
import dagger.Component

/**
 * Created by guobiao on 2018/8/5.
 */

@PerComponentScope
@Component(dependencies = [(ActivityComponent::class)],
        modules = [(UserModule::class)])
interface UserComponent {
    fun inject(activity: LoginActivity)

}