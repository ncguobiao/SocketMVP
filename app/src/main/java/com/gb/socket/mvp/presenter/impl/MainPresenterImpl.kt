package com.gb.socket.mvp.presenter.impl

import com.alibaba.fastjson.JSON
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.*
import com.example.baselibrary.compose
import com.gb.socket.data.domain.*
import com.gb.socket.mvp.presenter.MainPresenter
import com.gb.socket.mvp.service.MainService
import com.gb.socket.mvp.view.MainView
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import java.io.IOException
import java.util.concurrent.TimeUnit
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

        val maxConnectCount = 10
        // 当前已重试次数
        var currentRetryCount = 0
        // 重试等待时间
        var waitRetryTime = 0

       service.getBanner(deleteFlag, curPage, pageSize)
                .retryWhen(object :Function<Observable<Throwable>, ObservableSource<Any>>{
                    override fun apply(t: Observable<Throwable>): ObservableSource<Any> {

                        return t.flatMap(object :Function<Throwable, ObservableSource<Int>>{
                            override fun apply(t: Throwable): ObservableSource<Int> {
                                // 输出异常信息
                                Logger.d(  "发生异常 = ${t.message.toString()}")
                                if (t is IOException){
                                    Logger.d(  "属于IO异常，需重试" )
                                    /**
                                     * 需求2：限制重试次数
                                     * 即，当已重试次数 < 设置的重试次数，才选择重试
                                     */
                                    if (currentRetryCount < maxConnectCount){
                                        // 记录重试次数
                                        currentRetryCount++;
                                        Logger.d( "重试次数 = " + currentRetryCount);
                                        /**
                                         * 需求2：实现重试
                                         * 通过返回的Observable发送的事件 = Next事件，从而使得retryWhen（）重订阅，最终实现重试功能
                                         *
                                         * 需求3：延迟1段时间再重试
                                         * 采用delay操作符 = 延迟一段时间发送，以实现重试间隔设置
                                         *
                                         * 需求4：遇到的异常越多，时间越长
                                         * 在delay操作符的等待时间内设置 = 每重试1次，增多延迟重试时间1s
                                         */
                                        // 设置等待时间
                                        waitRetryTime = 1000 + currentRetryCount* 1000;
                                        Logger.d( "等待时间 =" + waitRetryTime);

                                        val observable = Observable.just(1)
                                        return observable.delay(waitRetryTime.toLong(), TimeUnit.MILLISECONDS)

                                    }else{
                                        // 若重试次数已 > 设置重试次数，则不重试
                                        // 通过发送error来停止重试（可在观察者的onError（）中获取信息）
                                        return Observable.error(Throwable("重试次数已超过设置次数 = " +currentRetryCount  + "，即 不再重试"));
                                    }

                                }
                                // 若发生的异常不属于I/O异常，则不重试
                                // 通过返回的Observable发送的事件 = Error事件 实现（可在观察者的onError（）中获取信息）

                                else{
                                    return Observable.error( Throwable("发生了非网络异常（非I/O异常）"))
                                }
                            }

                        })
                    }

                })
               .compose(lifecycleProvider.bindToLifecycle())
                .compose()
                .map(object : Function<BaseResp, List<String>?> {
                    override fun apply(t: BaseResp): List<String>? {
                        getView()?.dismissLoading()
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

                }).subscribe({
                    getView()?.dismissLoading()
                    if (it != null) {
                        getView()?.showBanner(it)
                    } else {
                        getView()?.onDataIsNull()
                    }
                },{
                    getView()?.dismissLoading()
                    getView()?.onError("获取首页轮播图失败,原因:${it.message.toString()}")
                })

    }

    /**
     * 查询当前普通设备使用记录和2G设备使用记录
     */
    override fun getRecords(userId: String, appType: String) {
        if (!preparReq(getView(), this)) return
        if (userId.isEmpty()) {
            getView()?.onError("userID is null")
            return
        }
        //普通设备使用记录
        val records = service.getRecords(userId, appType)
        //2G设备使用记录
        val records2G = service.get2GRecords(userId)
        Observable.zip(records, records2G, object : BiFunction<BaseResp, BaseResp, ArrayList<RecordsMergeBean>> {
            override fun apply(t1: BaseResp, t2: BaseResp): ArrayList<RecordsMergeBean> {
                getView()?.dismissLoading()
                val list = ArrayList<RecordsMergeBean>()
                if ("0000" == t1.returnCode) {
                    val jsonData = t1.retnrnJson.toString().trim()
                    val data = JSON.parseArray(jsonData, RecordsBean::class.java)
                    data?.forEach {
//                        Logger.d("it${it}")
                        var databean = RecordsMergeBean (it, null)
                        list.add(databean)
                    }
//                    Logger.d("记录${data}")
                } else {
                    getView()?.onError("查询设备使用记录失败，错误吗：${t1.returnCode}")
                }
                if ("0000" == t2.returnCode) {
                    val jsonData = t2.retnrnJson.toString().trim()
                    val data = JSON.parseObject(jsonData, Records2G::class.java)
//                    Logger.d("2G记录${data}")
                    val databean = RecordsMergeBean(null, data)
                    list.add(0,databean)
                } else {
                    getView()?.onError("查询2G设备使用记录失败，错误吗：${t2.returnCode}")
                }
                return list
            }

        }).compose()
                .compose(lifecycleProvider.bindToLifecycle())
                .subscribe({
                    getView()?.dismissLoading()
                    getView()?.showRecords(it)
                },{
                    getView()?.dismissLoading()
                    getView()?.onError("查询设备使用记录失败,原因:${it.message.toString()}")
                })



//        service.getRecords(userId, appType)
//                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
//                    override fun onNext(t: BaseResp) {
//                        if ("0000" == t.returnCode) {
////                            val jsonData = t.retnrnJson.toString().trim()
////                            val data = JSON.parseObject(jsonData, DeviceInfo::class.java)
////                            if (data!=null){
////                                getView()?.getDeviceInfo(data)
////                            }else{
////                                getView()?.onDataIsNull()
////                            }
//
//                        } else {
//                            getView()?.onDataIsNull()
//                        }
//                    }
//                }, lifecycleProvider)
    }


    /**
     * 获取设备信息(可以获取设备信息后调用上传定位信息)
     */
    override fun getDeviceInfo(macAddress: String?, deviceName: String?) {
        if (!preparReq(getView(), this)) return
        if (macAddress == null || deviceName == null) {
            getView()?.onError("macAddress is null or deviceName is null")
            return
        }
        this.service.getDeviceInfo(macAddress!!, deviceName!!)
                .execute(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        if ("0000" == t.returnCode) {
                            var jsonData = t.retnrnJson.toString().trim()
                            if (jsonData.contains("[")) {
                                jsonData = jsonData.replace("[", "")
                            }
                            if (jsonData.contains("]")) {
                                jsonData = jsonData.replace("]", "")
                            }
                            var data: DeviceInfo? = null
                            try {
                                data = JSON.parseObject(jsonData, DeviceInfo::class.java)
                            } catch (e: Exception) {
                                getView()?.onError("获取数据异常")
                            }
                            if (data != null) {
                                getView()?.getDeviceInfo(data)
                            } else {
                                getView()?.onError("设备可能未入库")
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
    fun getMergeData(deleteFlag: String, curPage: String, pageSize: String, userId: String, appType: String) {
        if (!preparReq(getView(), this)) return

        val banner = service.getBanner(deleteFlag, curPage, pageSize)
        val records = service.getRecords(userId, appType)
        val merge = Observable.merge(banner, records).subscribe(object : BaseSubscriber<BaseResp>(getView()!!) {
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