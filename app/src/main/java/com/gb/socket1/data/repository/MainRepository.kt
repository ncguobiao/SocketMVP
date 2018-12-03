package com.gb.socket1.data.repository

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.socket1.api.MainApi
import com.gb.socket1.data.domain.BannerReq
import com.gb.socket1.data.domain.DeviceInfoReq
import com.gb.socket1.data.domain.RecordReq
import com.gb.socket1.data.domain.UploadLocationReq
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/7.
 */
class MainRepository @Inject constructor() {


    /**
     * 获取主页banner
     */
    fun getBanner(deleteFlag: String, curPage: String, pageSize: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(MainApi::class.java).getBanner(BannerReq(deleteFlag, curPage, pageSize)).compose()
    }

    /**
     * 查询当前使用记录
     */
    fun getRecords(userId: String, appType: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(MainApi::class.java).getRecords(RecordReq(userId, appType)).compose()
    }

    /**
     * 查询2G设备使用记录
     */
    fun get2GRecords(userId: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(MainApi::class.java).get2GRecords(RecordReq(userId,"")).compose()
    }

    /**
     * 获取设备信息
     */
    fun getDeviceInfo(macAddress: String, deviceName: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(MainApi::class.java).getDeviceInfo(DeviceInfoReq(macAddress, deviceName)).compose()
    }


    /**
     * 上传位置信息
     */
    fun uploadLocation(userId: String, deviceId: String, macAddress: String, longitude: String, latitude: String, collectionType: String): Observable<BaseResp> {
        return RetrofitFactory.instance.create(MainApi::class.java)
                .uploadLocation(
                        UploadLocationReq(userId, deviceId, macAddress, longitude, latitude, collectionType)
                )
                .compose()

    }
}