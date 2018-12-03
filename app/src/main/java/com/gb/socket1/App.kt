package com.gb.socket1

import android.app.Activity
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.core.AutowiredLifecycleCallback
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.dao.GreenDaoHelper
import com.example.baselibrary.utils.AppUtils
import com.facebook.stetho.Stetho
import com.mob.MobSDK
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.widght.RxDialogSureCancel


/**
 * Created by guobiao on 2018/8/4.
 */
class App : BaseApplication() {

    private var curActivity: BaseActivity? = null

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun getApplication(): App {
            return instance!!
        }
    }


    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : AutowiredLifecycleCallback() {


            override fun onActivityPaused(activity: Activity?) {
                curActivity = null
            }

            override fun onActivityResumed(activity: Activity?) {
                if (activity != null && activity is BaseActivity)
                    curActivity = activity as BaseActivity
            }

        })
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

    private fun initStetho() {
//        Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build()
        )

    }

    /**
     * 全局dialog
     */
    fun showDialog(title: String, message: String) {
        curActivity?.let {
            val rxDialogSureCancel = RxDialogSureCancel(it)
            rxDialogSureCancel
                    .setTitle(title)
                    .setContent(message)
                    .setCancelListener { rxDialogSureCancel.cancel() }
                    .setSureListener { rxDialogSureCancel.cancel() }
                    .setOnCancelable(false)
                    .setOnCanceledOnTouchOutside(false)
                    .build()
                    .show()

        }
    }

}
