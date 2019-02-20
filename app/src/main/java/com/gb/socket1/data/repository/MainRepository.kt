package com.gb.socket1.data.repository

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.Constant
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.socket1.api.MainApi
import com.gb.socket1.data.CheckedDeviceBean
import com.gb.socket1.data.CheckedDeviceReq
import com.gb.socket1.data.CheckedDeviceParameter
import com.gb.socket1.data.domain.BannerReq
import com.gb.socket1.data.domain.DeviceInfoReq
import com.gb.socket1.data.domain.RecordReq
import com.gb.socket1.data.domain.UploadLocationReq
import com.gb.socket1.util.KeysUtil
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import okhttp3.MediaType
import javax.inject.Inject
import okhttp3.RequestBody



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

    fun checkedDevice(mac: String, deviceName: String): Observable<BaseResp> {
        val checkedDeviceParameter = CheckedDeviceParameter(deviceName, mac)
        val parameter = JSONObject()
//        parameter.put("mac", mac)
//        parameter.put("deviceName", deviceName)
        var js = com.alibaba.fastjson.JSONObject()
        js["className"] = "com.sensor.cdx.service.impl.DeviceServiceImpl.checkDeviceStorage"
        js["userId"] = Constant.USER_ID
        js["parameter"] = checkedDeviceParameter
        js = KeysUtil.encryption(js)
        Logger.d("checkedDevice js=${js.toString()}")
        val body = RequestBody.create(MediaType.parse("application/json"), js.toString())
//        return RetrofitFactory.instance.create(MainApi::class.java)
//                .checkedDevice(
//                        CheckedDeviceReq(CheckedDeviceBean(js.toString()) )
//                )
//                .compose()
        return RetrofitFactory.instance.create(MainApi::class.java)
                .checkedDevice2(
                      body
                )
                .compose()

    }
}