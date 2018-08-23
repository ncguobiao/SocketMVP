package com.gb.sockt.blutoothcontrol.listener

import android.media.MediaSync

/**
 * Created by guobiao on 2018/8/9.
 */
interface BleCEDataChangeListener :BleMultiDataChangeListener{

   fun deviceisUse()

   fun deviceisisNotUse()

    /**
     * 获取设备节点信息
     */
    fun getDeviceInfoSuccess()

    fun getDeviceInfoFailure()
}