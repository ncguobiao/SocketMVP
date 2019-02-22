package com.example.baselibrary.common

import java.util.*

/**
 * Created by Alienware on 2018/6/23.
 */
object Constant {
    const val SERVICE_ERROR: String = "服务器连接失败"
    //是否登陆
    const val IS_LOGIN: String = "is_login"
    //用户id
    const val USERID: String = "userID"
    const val USER_ID: Int = 107
    const val POWER_LEVEL: String = ":powerLevel"
    //设备信息
    const val DEVICE_NAME: String = "device_name"
    const val DEVICE_MAC: String = "device_mac"
    const val DEVICE_WAY: String = "device_way"
    const val DEVICE_TYPE: String = "device_typ"
    const val DEVICE_RATE: String = "rate"
    const val DEVICE_ID: String = "device_id"
    const val DEVICE_CE: String = "CE"
    const val DEVICE_CD: String = "CD"
    const val DEVICE_SINGLE: String = "single"

    //sp name
    const val SP_NAME: String = "MyAgent_save"

    //是否打印日志
    const val DEBUG: Boolean = true

    //fragment类型
    const val FRAGMENT_TAG: String = "fragment_tag"
    const val FRAGMENT_INCOME: Int = 0
    const val FRAGMENT_DEVICE_MANAGER: Int = 1
    const val FRAGMENT_CHARG_DETAIL: Int = 2
    const val FRAGMENT_TWITHDRAWALS_RECORD: Int = 3
    const val FRAGMENT_USER_INFO: Int = 4
    const val FRAGMENT_DEVICE_RECORDS: Int = 5
//  const val FRAGMENT_INCOME:Int = 0

    //测试服
    const val USER_URL: String = "http://test01.sensor668.com:5080/"
    const val USER_DEVICE_URL: String = "http://test01.sensor668.com:9080/"
    const val USER_COMMON_URL: String = "http://test01.sensor668.com:8080/"

    //蓝牙信息
    val SERVICEUUID: UUID? = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTICUUID2: UUID? = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTICUUID1: UUID? = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")

    const val QRCODE = 18888
    val COIN_COUNT: String? = "coin_count"

}