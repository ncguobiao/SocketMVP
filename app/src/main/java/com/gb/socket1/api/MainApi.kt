package com.gb.socket1.api

import com.example.baselibrary.api.UriConstant
import com.example.baselibrary.common.BaseResp
import com.gb.socket1.data.CheckedDeviceReq
import com.gb.socket1.data.domain.BannerReq
import com.gb.socket1.data.domain.DeviceInfoReq
import com.gb.socket1.data.domain.RecordReq
import com.gb.socket1.data.domain.UploadLocationReq
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by guobiao on 2018/8/7.
 */
interface MainApi {

    /**
     * 获取当前使用记录
     */
//    @POST("DeviceService/UseDevice/currentRecord")
    @POST(UriConstant.TEST_USER_DEVICE_URL + "DeviceService/UseDevice/currentRecord")
    fun getRecords(@Body req: RecordReq): Observable<BaseResp>


    //    @POST("DeviceService/UseGsmDevice/findOrder")
    @POST(UriConstant.TEST_USER_DEVICE_URL + "DeviceService/UseGsmDevice/findOrder")
    fun get2GRecords(@Body req: RecordReq): Observable<BaseResp>


    /**
     * 获取主页轮播图
     */
    @POST("CommonParameter/advertisement/managerAdvertisement")
    fun getBanner(@Body req: BannerReq): Observable<BaseResp>


    /**
     *
     * 上传缓存使用记录
     */
    @POST("DeviceService/UseDevice/bufferUseDevice")
    fun upLoadRecord(@Body req: RecordReq): Observable<BaseResp>

    /**
     * 查询设备信息
     */
//    @POST("DeviceService/Device/findDevice")
    @POST(UriConstant.TEST_USER_DEVICE_URL + "DeviceService/Device/findDevice")
    fun getDeviceInfo(@Body req: DeviceInfoReq): Observable<BaseResp>

    /**
     * 上传位置信息
     */
    @POST("DeviceService/Device/collectionAddress")
    fun uploadLocation(@Body req: UploadLocationReq): Observable<BaseResp>


    @POST("http://test04.sensor668.com:6080/DeviceCDXService/rest/executeAll")
    fun checkedDevice(@Body checkedDeviceReq: CheckedDeviceReq): Observable<BaseResp>


    @POST("http://test04.sensor668.com:6080/DeviceCDXService/rest/executeAll")
    fun checkedDevice2(@Body body: RequestBody): Observable<BaseResp>

}