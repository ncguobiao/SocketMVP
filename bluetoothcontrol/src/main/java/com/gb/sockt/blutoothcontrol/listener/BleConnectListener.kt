package com.gb.sockt.blutoothcontrol.listener

import android.media.MediaSync

/**
 * Created by guobiao on 2018/8/9.
 */
interface BleConnectListener {

    fun connectOnSuccess()

    fun connectOnFailure()

    fun connectOnError()
}