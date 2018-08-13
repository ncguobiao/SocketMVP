package com.gb.socket.api

import com.example.baselibrary.common.BaseResp
import com.gb.socket.data.domain.BannerReq
import com.gb.socket.data.domain.DeviceInfoReq
import com.gb.socket.data.domain.RecordReq
import com.gb.socket.data.domain.UploadLocationReq
import com.gb.sockt.usercenter.data.domain.LoginReq
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by guobiao on 2018/8/7.
 */
interface MainApi {

    /**
     * 获取当前使用记录
     */
    @POST("DeviceService/UseDevice/currentRecord")
    fun getRecords(@Body req: RecordReq): Observable<BaseResp>


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
    @POST("DeviceService/Device/findDevice")
    fun getDeviceInfo(@Body req: DeviceInfoReq): Observable<BaseResp>

    /**
     * 上传位置信息
     */
    @POST("DeviceService/Device/collectionAddress")
    fun uploadLocation(@Body req: UploadLocationReq): Observable<BaseResp>


}