package com.gb.socket

import cn.jpush.android.api.JPushInterface
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.dao.GreenDaoHelper
import com.example.baselibrary.utils.AppUtils
import com.facebook.stetho.Stetho
import com.mob.MobSDK


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

        //初始化数据库
        GreenDaoHelper.getInstance().init(this)

        initStetho()

        initJPush()

//        RxTool.init(this);
//        ZXingLibrary.initDisplayOpinion(this)
    }

    private fun initJPush() {

        //极光推送
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
    }

    fun initStetho() {
//        Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build()
        )

    }

}
