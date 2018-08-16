package com.gb.socket

import android.content.Context
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.utils.AppUtils
import com.mob.MobSDK
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.Logger.addLogAdapter
import kotlin.properties.Delegates


/**
 * Created by guobiao on 2018/8/4.
 */
class App: BaseApplication(){

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun getAppContext() : App {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        MobSDK.init(this)

        AppUtils.init(this)
//        RxTool.init(this);
//        ZXingLibrary.initDisplayOpinion(this)
    }
}
