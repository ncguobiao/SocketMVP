package com.gb.sockt.center.api

import com.example.baselibrary.api.UriConstant
import com.example.baselibrary.common.BaseResp
import com.gb.sockt.center.data.domain.RechargeRecordsReq
import com.gb.sockt.center.data.domain.UseRecordsReq
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by guobiao on 2018/8/23.
 */
interface UserCenterApi {

    /**
     * 获取用户使用记录
     */
//    @POST("DeviceService/rest/executeAll")
    @POST(UriConstant.TEST_USER_DEVICE_URL+"DeviceService/rest/executeAll")
    fun getUseRecords(@Body req: UseRecordsReq): Observable<BaseResp>

    /**
     * 充值记录
     */
//    @POST("UserService/HistoryMge/PayHistory")
    @POST(UriConstant.TEST_USER_URL+"UserService/HistoryMge/PayHistory")
    fun getRechargeRecords(@Body req: RechargeRecordsReq): Observable<BaseResp>

}