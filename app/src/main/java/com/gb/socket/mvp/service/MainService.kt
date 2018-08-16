package com.gb.socket.mvp.service

import com.example.baselibrary.common.BaseResp
import io.reactivex.Observable

/**
 * Created by guobiao on 2018/8/7.
 */
interface MainService {

    fun getBanner(deleteFlag: String, curPage: String, pageSize: String): Observable<BaseResp>

    fun getRecords(userId: String, appType: String): Observable<BaseResp>

    fun getDeviceInfo(macAddress: String, deviceName: String): Observable<BaseResp>

    fun uploadLocation(
            userId: String
            , deviceId: String
            , macAddress: String
            , longitude: String,
            latitude: String,
            collectionType: String
    ): Observable<BaseResp>

    fun get2GRecords(userId: String): Observable<BaseResp>

}