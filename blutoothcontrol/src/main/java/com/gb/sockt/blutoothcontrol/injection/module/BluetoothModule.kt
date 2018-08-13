package com.gb.sockt.blutoothcontrol.injection.module

import android.app.Activity
import com.gb.sockt.blutoothcontrol.ble.BlueToothControl
import com.gb.sockt.blutoothcontrol.ble.BlueToothMulitControlImpl
import com.gb.sockt.blutoothcontrol.mvp.service.BluetoothContrlService
import com.gb.sockt.blutoothcontrol.mvp.service.imp.BluetoothContrlServiceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by guobiao on 2018/8/9.
 */

@Module
class BluetoothModule constructor(private val deviceTag: String, val activity: Activity?) {


    @Named("BluetoothCEControll")
    @Provides
    fun provideBluetoothCEControll(): BlueToothControl {
        return BlueToothMulitControlImpl(deviceTag,activity)
    }
    @Named("BluetoothMulitControl")
    @Provides
    fun provideBluetoothMulitControl(): BlueToothControl {
        return BlueToothMulitControlImpl(deviceTag,activity)
    }

    @Provides
    fun provideBluetoothControlService(service: BluetoothContrlServiceImpl): BluetoothContrlService {
        return service
    }
}