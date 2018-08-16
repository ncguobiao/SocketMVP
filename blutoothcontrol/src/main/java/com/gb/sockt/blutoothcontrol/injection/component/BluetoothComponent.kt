package com.gb.sockt.blutoothcontrol.injection.component

import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.scope.PerComponentScope
import com.gb.sockt.blutoothcontrol.injection.module.BluetoothModule
import com.gb.sockt.blutoothcontrol.ui.fragment.MulitWayFragment
import dagger.Component

/**
 * Created by Alienware on 2018/6/23.
 */
@PerComponentScope
@Component(dependencies = arrayOf(ActivityComponent::class),
        modules = arrayOf(BluetoothModule::class
        ))
interface BluetoothComponent {

    fun inject(fragment: MulitWayFragment)


}