package com.gb.sockt.blutoothcontrol.injection.module

import android.app.Activity
import com.gb.sockt.blutoothcontrol.ble.ce.BlueToothCEControl
import com.gb.sockt.blutoothcontrol.ble.ce.BlueToothCEControlImpl
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControl
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControlImpl
import com.gb.sockt.blutoothcontrol.mvp.service.BluetoothContrlService
import com.gb.sockt.blutoothcontrol.mvp.service.imp.BluetoothControlServiceImpl
import com.gb.sockt.blutoothcontrol.uitls.BlueToothSingleControl
import com.gb.sockt.blutoothcontrol.uitls.BlueToothSingleControlImpl
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by guobiao on 2018/8/9.
 */

@Module
class BluetoothModule constructor(private val deviceTag: String, val activity: Activity) {


    @Named("BluetoothCEControl")
    @Provides
    fun provideBluetoothCEControl(): BlueToothCEControl {
        return BlueToothCEControlImpl(deviceTag, activity)
    }

    @Named("BluetoothMultiControl")
    @Provides
    fun provideBluetoothMultiControl(): BlueToothMultiControl {
        return BlueToothMultiControlImpl(deviceTag, activity)
    }

    @Named("BlueToothSingleControl")
    @Provides
    fun provideBlueToothSingeControl(): BlueToothSingleControl {
        return BlueToothSingleControlImpl(activity)
    }

    @Provides
    fun provideBluetoothControlService(service: BluetoothControlServiceImpl): BluetoothContrlService {
        return service
    }
}