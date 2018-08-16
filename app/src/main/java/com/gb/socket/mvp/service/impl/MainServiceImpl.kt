package com.gb.socket.mvp.service.impl

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.socket.api.MainApi
import com.gb.socket.data.domain.RecordReq
import com.gb.socket.data.repository.MainRepository
import com.gb.socket.mvp.service.MainService
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/7.
 */
class MainServiceImpl @Inject constructor() : MainService {


    @Inject
    lateinit var repository: MainRepository

    /**
     * 获取主页banner
     */
    override fun getBanner(deleteFlag: String, curPage: String, pageSize: String): Observable<BaseResp> {

        return repository.getBanner(deleteFlag, curPage, pageSize)
    }

    /**
     * 获取当前使用记录
     */
    override fun getRecords(userId: String, appType: String): Observable<BaseResp> {
        return repository.getRecords(userId, appType)
    }

    /**
    * 获取设备信息
    */
    override fun getDeviceInfo(macAddress: String, deviceName: String): Observable<BaseResp> {
        return repository.getDeviceInfo(macAddress, deviceName)
    }


    /**
     * 上传位置信息
     */
    override fun uploadLocation(userId: String, deviceId: String, macAddress: String, longitude: String, latitude: String, collectionType: String): Observable<BaseResp> {
        return repository.uploadLocation(userId, deviceId, macAddress, longitude, latitude, collectionType)

    }


    override fun get2GRecords(userId: String): Observable<BaseResp> {
        return repository.get2GRecords(userId)
    }

}