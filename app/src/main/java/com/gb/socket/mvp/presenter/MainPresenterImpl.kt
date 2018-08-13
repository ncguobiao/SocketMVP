package com.gb.socket.mvp.presenter

import com.alibaba.fastjson.JSON
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.*
import com.example.baselibrary.compose
import com.example.baselibrary.utils.SpUtils
import com.gb.socket.data.domain.BannerBean
import com.gb.socket.data.domain.DeviceInfo
import com.gb.socket.mvp.service.MainService
import com.gb.socket.mvp.view.MainView
import com.gb.sockt.usercenter.data.domain.LoginBean
import com.gb.sockt.usercenter.mvp.service.UserService
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.functions.Function
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/7.
 */
class MainPresenterImpl @Inject constructor() : MainPresenter, BasePresenter<MainView>() {


    @Inject
    lateinit var service: MainService

    /**
     * 获取主页banner
     */
    override fun getBanner(deleteFlag: String, curPage: String, pageSize: String) {
        if (!preparReq(getView(), this)) return
        service.getBanner(deleteFlag, curPage, pageSize)
                .compose(lifecycleProvider.bindToLifecycle())
                .compose()
                .map(object : Function<BaseResp, List<String>?> {
                    override fun apply(t: BaseResp): List<String>?{
                        val jsonData = t.retnrnJson.toString().trim()
                        val data = JSON.parseObject(jsonData, BannerBean::class.java)
                        if (data != null) {
                            val list = ArrayList<String>()
                            val returnJson = data.returnJson
                            if (returnJson != null && returnJson.size > 0) {
                                for (i in returnJson) {
                                    list.add(i.path)
                                }
                                return list
                            }
                        } else {
                            getView()?.onDataIsNull()
                        }
                        return null
                    }

                }).subscribe {
                    if (it != null) {
                        getView()?.showBanner(it)
                    } else {
                        getView()?.onDataIsNull()
                    }
                }
    }

    /**
     * 查询当前使用记录
     */
    override fun getRecords(userId: String, appType: String) {
        if (!preparReq(getView(), this)) return
        if (userId.isEmpty()){
            getView()?.onError("userID is null")
            return
        }
        service.getRecords(userId, appType)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
//                            val jsonData = t.retnrnJson.toString().trim()
//                            val data = JSON.parseObject(jsonData, DeviceInfo::class.java)
//                            if (data!=null){
//                                getView()?.getDeviceInfo(data)
//                            }else{
//                                getView()?.onDataIsNull()
//                            }

                        } else {
                            getView()?.onDataIsNull()
                        }
                    }
                }, lifecycleProvider)
    }


    /**
     * 获取设备信息(可以获取设备信息后调用上传定位信息)
     */
    override fun getDeviceInfo(macAddress: String?, deviceName: String?) {
        if (!preparReq(getView(), this)) return
        if (macAddress==null || deviceName == null){
            getView()?.onError("macAddress is null or deviceName is null")
            return
        }
        this.service.getDeviceInfo(macAddress!!, deviceName!!)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            var jsonData = t.retnrnJson.toString().trim()
                            if (jsonData.contains("[")){
                                jsonData = jsonData.replace("[","")
                            }
                            if (jsonData.contains("]")){
                                jsonData = jsonData.replace("]","")
                            }
                            var data:DeviceInfo?=null
                            try {
                                 data = JSON.parseObject(jsonData, DeviceInfo::class.java)
                            }catch (e:Exception){
                                getView()?.onError("获取数据异常")
                            }
                            if (data!=null){
                                getView()?.getDeviceInfo(data)
                            }else{
                                getView()?.onDataIsNull()
                            }
                        } else {
                            getView()?.onDataIsNull()
                        }
                    }
                }, lifecycleProvider)

    }

    /**
     * 上传位置信息
     */
    override fun uploadLocation(userId: String, deviceId: String, macAddress: String, longitude: String, latitude: String, collectionType: String) {
        if (!preparReq(getView(), this)) return
        this.service.uploadLocation(userId, deviceId, macAddress, longitude, latitude, collectionType)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            getView()?.uploadLocationSuccess()
                        } else {
                            getView()?.onError("位置信息上传失败")
                        }
                    }
                }, lifecycleProvider)

    }

    /**
     * 合并发送请求
     */
    fun getMergeData(deleteFlag: String, curPage: String, pageSize: String,userId: String, appType: String){
        if (!preparReq(getView(), this)) return

        val banner = service.getBanner(deleteFlag, curPage, pageSize)
        val records = service.getRecords(userId, appType)
        val merge = Observable.merge(banner,records).subscribe(object :BaseSubscriber<BaseResp>(getView()!!){
            override fun onNext(t: BaseResp) {
//             if ("0000"==t.returnCode){
//                 val jsondata = t.retnrnJson
                //可以获取数据，此处转换问题
//                 if (jsondata is BannerBean){
//
//                 }else {
//                     getView()?.onDataIsNull()
//                 }
//
//             }else{
//                 getView()?.onDataIsNull()
//             }
            }
        })

    }




}