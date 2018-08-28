package com.gb.sockt.center.data.repository

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.sockt.center.api.UserCenterApi
import com.gb.sockt.center.data.domain.*
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/23.
 */
class UseCenterRepository @Inject constructor(){


    fun getUseRecords(className:String,userId:String,curPage:String): Observable<BaseResp>{

        return RetrofitFactory.instance.create(UserCenterApi::class.java).getUseRecords(
                UseRecordsReq(className,parameter = ParameterReq(
                        userName = "",
                        deviceName = "",
                        adminName = "",
                        userId = userId,
                        secendAdmin = "",
                        thirdAdmin = "",
                        fourthAdmin = "",
                        fifthAdmin = "",
                        startDateMax = "",
                        startDateMin = "",
                        endDateMax = "",
                        endDateMin = "",
                        deleteFlag = "",
                        managerId = "1",
                        powerLevel = "0",
                        curPage = curPage,
                        pageSize = "15"
                        ),powerLevel = "0",userId = userId)).compose()
    }


    fun getRechargeRecords(userId:String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(UserCenterApi::class.java)
                .getRechargeRecords(RechargeRecordsReq(userId)).compose()
    }

    fun deleteUserRecord(appType: String, useDeviceId: String, userId: String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(UserCenterApi::class.java)
                .deleteUserRecord(DeleteUserRecordReq(appType, useDeviceId, userId)).compose()
    }

     fun deletePayMent(appType: String, paymentId: String, userId: String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(UserCenterApi::class.java)
                .deletePayMent(DeleteRechargeRecordReq(appType, paymentId, userId)).compose()
    }
}