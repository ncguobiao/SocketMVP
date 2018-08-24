package com.gb.sockt.center.injection.component

import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.scope.PerComponentScope
import com.gb.socket.injection.module.MainModule
import com.gb.socket.ui.activity.MainActivity
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