package com.gb.sockt.center.injection.component

import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.scope.PerComponentScope
import com.gb.sockt.center.injection.module.UserCenterModule
import com.gb.sockt.center.ui.fragment.RechargeRecordsFragment
import com.gb.sockt.center.ui.fragment.UseRecordsFragment
import dagger.Component

/**
 * Created by guobiao on 2018/8/23.
 */
@PerComponentScope
@Component(dependencies = [(ActivityComponent::class)]
        , modules = [(UserCenterModule::class)]
)
interface UserCenterComponent {


    fun inject(fragment: UseRecordsFragment)

    fun inject(fragment: RechargeRecordsFragment)
}