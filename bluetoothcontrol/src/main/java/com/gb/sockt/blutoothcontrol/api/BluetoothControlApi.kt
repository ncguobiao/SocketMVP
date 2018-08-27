package com.gb.sockt.blutoothcontrol.api

import com.example.baselibrary.api.UriConstant
import com.example.baselibrary.common.BaseResp
import com.gb.sockt.blutoothcontrol.data.domain.RequestCantUseReq
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by guobiao on 2018/8/13.
 */
interface BluetoothControlApi {

    /**
     * 查询设备是否可用
     */
//    @POST("DeviceService/Device/requestUse")
    @POST(UriConstant.TEST_USER_DEVICE_URL+"DeviceService/Device/requestUse")
    fun requestCantUse(@Body req: RequestCantUseReq): Observable<BaseResp>


    /**
     * 查询设备剩余使用时间
     */
//    @POST("DeviceService/Device/surplusTime")
    @POST(UriConstant.TEST_USER_DEVICE_URL+"DeviceService/Device/surplusTime")
    fun requestSurplusTime(@Body req: RequestCantUseReq): Observable<BaseResp>


    /**
     * 使用设备成功通知服务器
     */
//    @POST("DeviceService/Device/useSuccess")
    @POST(UriConstant.TEST_USER_DEVICE_URL+"DeviceService/Device/useSuccess")
    fun openSuccess2Service(@Body req: RequestCantUseReq): Observable<BaseResp>

    /**
     * 加时成功通知服务器
     */
//    @POST("DeviceService/DeviceAddTime/addPowerTime")
    @POST(UriConstant.TEST_USER_DEVICE_URL+"DeviceService/DeviceAddTime/addPowerTime")
    fun addTimeSuccess2Service(@Body req: RequestCantUseReq): Observable<BaseResp>

//    @POST("/DeviceService/DeviceAddTime/requestAddTime")
    @POST(UriConstant.TEST_USER_DEVICE_URL+"DeviceService/DeviceAddTime/requestAddTime")
    fun addTimeCanUse(@Body req: RequestCantUseReq): Observable<BaseResp>
}