package com.gb.socket.mvp.view

import com.example.baselibrary.base.IBaseView
import com.gb.socket.data.domain.DeviceInfo

/**
 * Created by guobiao on 2018/8/7.
 */
interface MainView:IBaseView {

    /**
     * 显示banner
     */
    fun showBanner(images:List<String>)

    /**
     * 获取设备信息
     */
    fun getDeviceInfo(data :DeviceInfo)

    fun uploadLocationSuccess()
}