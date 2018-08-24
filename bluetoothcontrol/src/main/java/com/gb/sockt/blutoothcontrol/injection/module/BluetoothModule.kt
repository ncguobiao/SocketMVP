package com.gb.sockt.blutoothcontrol.injection.module

import android.app.Activity
import com.gb.sockt.blutoothcontrol.ble.ce.BlueToothCEControl
import com.gb.sockt.blutoothcontrol.ble.ce.BlueToothCEControlImpl
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControl
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControlImpl
import com.gb.sockt.blutoothcontrol.mvp.service.BluetoothContrlService
import com.gb.sockt.blutoothcontrol.mvp.service.imp.BluetoothContrlServiceImpl
import com.gb.sockt.blutoothcontrol.uitls.BlueToothSingeControl
import com.gb.sockt.blutoothcontrol.uitls.BlueToothSingeControlImpl
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

    @Named("BlueToothSingeControl")
    @Provides
    fun providelueToothSingeControl(): BlueToothSingeControl {
        return BlueToothSingeControlImpl(deviceTag, activity)
    }

    @Provides
    fun provideBluetoothControlService(service: BluetoothContrlServiceImpl): BluetoothContrlService {
        return service
    }
}