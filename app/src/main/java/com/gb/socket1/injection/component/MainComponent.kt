package com.gb.sockt.center.injection.component

import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.scope.PerComponentScope
import com.gb.socket1.injection.module.MainModule
import com.gb.socket1.ui.activity.*
import dagger.Component

/**
 * Created by guobiao on 2018/8/5.
 */

@PerComponentScope
@Component(dependencies = [(ActivityComponent::class)],
        modules = [(MainModule::class)])
interface MainComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: BluetoothKeyActivity)
    fun inject(activity: BluetoothCarActivity)

    fun inject(activity: ScanQRCodeActivity)

    fun inject(activity: BleCableActivity)

}