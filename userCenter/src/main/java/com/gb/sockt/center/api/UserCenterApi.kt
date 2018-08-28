package com.gb.sockt.center.api

import com.example.baselibrary.api.UriConstant
import com.example.baselibrary.common.BaseResp
import com.gb.sockt.center.data.domain.DeleteRechargeRecordReq
import com.gb.sockt.center.data.domain.DeleteUserRecordReq
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

    /**
     * 删除使用记录
     */
//   @POST("DeviceService/DerviceReturn/deleteUseDevice")
    @POST(UriConstant.TEST_USER_DEVICE_URL+"DeviceService/DerviceReturn/deleteUseDevice")
    fun deleteUserRecord(@Body req: DeleteUserRecordReq): Observable<BaseResp>

    /**
     * 删除充值记录
     */
//    @POST("UserService/PayHistory/deletePayment")
    @POST(UriConstant.TEST_USER_URL+"UserService/PayHistory/deletePayment")
    fun deletePayMent(@Body req: DeleteRechargeRecordReq): Observable<BaseResp>

}