package com.gb.socket1.injection.module

import android.app.Activity
import com.example.baselibrary.lbs.GaodeLbsLayerImpl
import com.example.baselibrary.lbs.ILbsLayer
import com.gb.socket1.mvp.service.MainService
import com.gb.socket1.mvp.service.impl.MainServiceImpl
import dagger.Module
import dagger.Provides

/**
 * Created by guobiao on 2018/8/8.
 */

@Module
class MainModule constructor(private val context: Activity){

    @Provides
    fun providerService(service: MainServiceImpl): MainService {
        return service
    }

    @Provides
    fun providerSGaodeLbsLayer():ILbsLayer {
        return GaodeLbsLayerImpl(context)
    }



}