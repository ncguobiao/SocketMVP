package com.gb.sockt.blutoothcontrol.api

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.Constant
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
    @POST("/DeviceService/Device/requestUse")
//    @POST(Constant.USER_DEVICE_URL+"/DeviceService/Device/requestUse")
    fun requestCantUse(@Body req: RequestCantUseReq): Observable<BaseResp>


    /**
     * 查询设备剩余使用时间
     */
    @POST("/DeviceService/Device/surplusTime")
//    @POST(Constant.USER_DEVICE_URL+"/DeviceService/Device/surplusTime")
    fun requestSurplusTime(@Body req: RequestCantUseReq): Observable<BaseResp>
}