package com.gb.socket1.data

/**
 * Created by guobiao on 2019/2/20.
 */
data class CheckedDeviceReq(val checkedDeviceBean: CheckedDeviceBean)

data class CheckedDeviceBean(
        val json: String
)
data class CheckedDeviceParameter(
        val deviceName: String,
        val mac: String
)