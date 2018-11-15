package com.gb.sockt.blutoothcontrol.listener

import android.media.MediaSync

/**
 * Created by guobiao on 2018/8/9.
 */
interface BleMultiDataChangeListener :BaseBLEDataListener{




    /**
     * 是返回设备当前状态
     */
    fun deviceCurrentState(isBusy:Boolean)


    /**
     * 加时成功
     */
    fun addTimeSuccess()

    /**
     * 加时失败
     */
    fun addTimeOnFailure()

    /**
     * 未接负载
     */
    fun deivceIsNotOnline(error: String)




}