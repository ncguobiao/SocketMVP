package com.gb.sockt.center.data.domain

/**
 * Created by guobiao on 2018/8/23.
 */
data class UseRecordsReq(
        val className: String,
        val parameter: ParameterReq,
        val powerLevel: String,
        val userId: String
)

data class ParameterReq(
        val userName: String,
        val deviceName: String,
        val adminName: String,
        val userId: String ,
        val secendAdmin: String,
        val thirdAdmin: String,
        val fourthAdmin: String,
        val fifthAdmin: String,
        val startDateMax: String,
        val startDateMin: String,
        val endDateMax: String,
        val endDateMin: String,
        val deleteFlag: String,
        val managerId: String,
        val powerLevel: String,
        val curPage: String,
        val pageSize: String

)