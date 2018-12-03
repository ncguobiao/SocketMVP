package com.gb.socket1.mvp.view

import com.example.baselibrary.base.IBaseView
import com.gb.socket1.data.domain.DeviceInfo
import com.gb.socket1.data.domain.RecordsMergeBean

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

    fun showRecords(list:ArrayList<RecordsMergeBean>?)
}