package com.gb.socket.mvp.presenter

import com.example.baselibrary.base.IPresenter
import com.gb.socket.mvp.view.MainView

/**
 * Created by guobiao on 2018/8/5.
 */
interface MainPresenter  {

    fun getBanner(deleteFlag: String, curPage: String, pageSize: String)

    fun getRecords(userId: String, appType: String)

    fun getDeviceInfo(macAddress: String?, deviceName: String?)

    fun uploadLocation(userId: String,
                       deviceId: String,
                       macAddress: String,
                       longitude: String,
                       latitude: String,
                       collectionType: String

    )


}