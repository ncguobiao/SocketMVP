package com.gb.sockt.blutoothcontrol.mvp.presenter.impl

import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.BaseSubscriber
import com.example.baselibrary.common.preparReq
import com.example.baselibrary.compose
import com.example.baselibrary.dao.GreenDaoHelper
import com.example.baselibrary.dao.model.DeviceUseRecords
import com.example.baselibrary.utils.DateUtils
import com.example.baselibrary.utils.SpUtils
import com.gb.sockt.blutoothcontrol.mvp.presenter.BluetoothPresenter
import com.gb.sockt.blutoothcontrol.mvp.service.BluetoothContrlService
import com.gb.sockt.blutoothcontrol.mvp.view.BluetoothMultiView
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeUnit

import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/9.
 */
class BluetoothPresenterImpl @Inject constructor() : BluetoothPresenter, BasePresenter<BluetoothMultiView>() {


    @Inject
    lateinit var service: BluetoothContrlService
    private val dao by lazy {
        GreenDaoHelper.getInstance().daoSession.deviceUseRecordsDao
    }

    /**
     * 获取剩余可使用时间
     */
    override fun requestSurplusTime(userId: String, deviceId: String, time: String) {

        if (!preparReq(getView(), this)) return
        service.requestCanUse(userId, deviceId, time)
                .compose()
                .compose(lifecycleProvider.bindToLifecycle())
                .map {
                    if ("0000" == it.returnCode) {
                        if ("true" == it.retnrnJson) {
                            return@map true
                        } else {
                            getView()?.showFillMoneyDialog()
                            return@map false
                        }
                    } else if ("0003" == it.returnCode) {
                        getView()?.onError("查询设备剩余使用时间失败，原因:未查询到设备")
                        return@map false
                    } else {
                        return@map false
                    }
                }.subscribeOn(Schedulers.io())
                .flatMap(object : Function<Boolean, ObservableSource<BaseResp>> {
                    override fun apply(t: Boolean): ObservableSource<BaseResp> {
                        return if (t)
                            service.requestSurplusTime(userId, deviceId)
                        else
                            Observable.empty()
                    }
                }).compose()
                .subscribe(
                        {
                            getView()?.dismissLoading()
                            if ("0000" == it.returnCode) {
                                if ("0" == it.retnrnJson) {//第一次使用
                                    getView()?.isFirstToUse()
                                } else {
                                    if (it.retnrnJson.toString().isNullOrEmpty()) {
                                        getView()?.onError("查询用户剩余使用时间失败")
                                        return@subscribe
                                    }
                                    //剩余可用时间
                                    val surplusTime = it.retnrnJson.toString().toLong()
                                    if (surplusTime > 0) {
                                        getView()?.showAddTimeDialog(surplusTime)
                                    } else {
                                        getView()?.showFillMoneyDialog()
                                    }

                                }

                            } else {
                                getView()?.onError("查询用户剩余使用时间失败，错误码${it.returnCode}")
                            }
                        }, {
                    getView()?.dismissLoading()
                    getView()?.onError("查询用户剩余使用时间失败，原因；${it?.message.toString()}")

                }, {
                    getView()?.dismissLoading()
                })
    }


    /**
     * 使用成功通知服务器
     */
    override fun openSuccess2Service(userId: String, deviceId: String, time: String) {

        val startTime = DateUtils.getTodayDateTime()
        val timeStamp = DateUtils.getTimeStamp(startTime)
        val endTimeStamp =  time?.toLong()!! * 3600 * 1000 + timeStamp
        val endTime = DateUtils.getDateToString(endTimeStamp, "yyyy-MM-dd HH:mm:ss")
        val deviceUseRecord = DeviceUseRecords(timeStamp, userId, deviceId!!, time!!, startTime, endTime)
        val insertSuccess = dao.insertOrReplace(deviceUseRecord)
        if (insertSuccess > 0) {
            Logger.d("缓存用户使用记录成功")
        }


//        getView()?.openSuccess2ServiceOnSuccess("1")
//        if (!preparReq(getView(), this)) return
//        service.openSuccess2Service(userId, deviceId, time)
//                .compose()
//                .compose(lifecycleProvider.bindToLifecycle())
//                .subscribe(object : BaseSubscriber<BaseResp>(getView()!!) {
//                    override fun onNext(t: BaseResp) {
//                        super.onNext(t)
//                        if ("0000" == t.returnCode) {
//                            //缓存使用时间
//                            SpUtils.put(BaseApplication.getAppContext(), userId + deviceId, time.toInt())
//                            getView()?.openSuccess2ServiceOnSuccess(time)
//                        } else {
//                            getView()?.openSuccess2ServiceOnFailure("开启设备后通知服务器扣费失败，错误码${t.returnCode}")
//
//                        }
//                    }
//                })


        val maxConnectCount = 10
        // 当前已重试次数
        var currentRetryCount = 0
        // 重试等待时间
        var waitRetryTime = 0

        service.openSuccess2Service(userId, deviceId, time)
                .retryWhen(object : Function<Observable<Throwable>, ObservableSource<Any>> {
                    override fun apply(t: Observable<Throwable>): ObservableSource<Any> {

                        return t.flatMap(object : Function<Throwable, ObservableSource<Int>> {
                            override fun apply(t: Throwable): ObservableSource<Int> {
                                // 输出异常信息
                                Logger.d("发生异常 = ${t.message.toString()}")
                                if (t is IOException) {
                                    Logger.d("属于IO异常，需重试")
                                    /**
                                     * 需求2：限制重试次数
                                     * 即，当已重试次数 < 设置的重试次数，才选择重试
                                     */
                                    if (currentRetryCount < maxConnectCount) {
                                        // 记录重试次数
                                        currentRetryCount++;
                                        Logger.d("重试次数 = " + currentRetryCount);
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
                                        waitRetryTime = 1000 + currentRetryCount * 1000;
                                        Logger.d("等待时间 =" + waitRetryTime);

                                        val observable = Observable.just(1)
                                        return observable.delay(waitRetryTime.toLong(), TimeUnit.MILLISECONDS)

                                    } else {
                                        // 若重试次数已 > 设置重试次数，则不重试
                                        // 通过发送error来停止重试（可在观察者的onError（）中获取信息）
                                        return Observable.error(Throwable("重试次数已超过设置次数 = " + currentRetryCount + "，即 不再重试"));
                                    }

                                }
                                // 若发生的异常不属于I/O异常，则不重试
                                // 通过返回的Observable发送的事件 = Error事件 实现（可在观察者的onError（）中获取信息）

                                else {
                                    return Observable.error(Throwable("发生了非网络异常（非I/O异常）"))
                                }
                            }

                        })
                    }

                }).compose()
                .compose(lifecycleProvider.bindToLifecycle())
                .subscribe(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        super.onNext(t)
                        if ("0000" == t.returnCode) {
                            //缓存使用时间
                            SpUtils.put(BaseApplication.getAppContext(), userId + deviceId, time.toInt())
                            getView()?.openSuccess2ServiceOnSuccess(time)
                            //成功，同步数据库
                            dao.deleteByKey(timeStamp)
                            Logger.d("删除缓存用户使用记录成功")
                        } else {
                            getView()?.openSuccess2ServiceOnFailure("开启设备后通知服务器扣费失败，错误码${t.returnCode}")

                        }
                    }
                })
    }


    /**
     * 加时成功通知服务器
     */
    override fun addTimeSuccess2Service(userId: String, deviceId: String, addTime: String) {
        if (!preparReq(getView(), this)) return
//        service.addTimeSuccess2Service(userId, deviceId, addTime)
//                .compose()
//                .compose(lifecycleProvider.bindToLifecycle())
//                .flatMap(object : Function<BaseResp, Observable<BaseResp>> {
//                    override fun apply(t: BaseResp): Observable<BaseResp> {
//                        return if ("0000" == t.returnCode) {
//                            val cacheTime = SpUtils.getInt(BaseApplication.getAppContext(), userId + deviceId)
//                            SpUtils.put(BaseApplication.getAppContext(), userId + deviceId, addTime.toInt() + cacheTime)
//
//                            Logger.e("加时成功:开始获取剩余时间")
//                            return service.requestSurplusTime(userId, deviceId)
//                        } else {
//                            getView()?.addTime2ServiceOnFailure("设备加时成功后通知服务器失败，错误码${t.returnCode}")
//                            return Observable.empty()
//                        }
//                    }
//                }).subscribe({
//                    getView()?.dismissLoading()
//                    if ("0000" == it.returnCode) {
//                        Logger.e("加时后获取时间:${it.retnrnJson}")
//                        val json = it.retnrnJson.toString().trim()
//                        if ("0" != json) {
//                            if (!json.isNullOrEmpty()) {
//                                val reminTIme = json.toLong()
//                                if (reminTIme > 0) {
//
//                                    getView()?.addTime2ServiceOnSuccess(addTime, reminTIme)
//                                } else {
//
//                                    getView()?.onError("加时成功，获取设备之前剩余时间错误")
//                                }
//                            } else {
//                                getView()?.onError("加时成功，获取设备之前剩余时间错误")
//                            }
//                        }
//
//                    } else {
//                        getView()?.dismissLoading()
//                        getView()?.onError("加时成功，获取设备之前剩余时间错误")
//                    }
//                },
//                        {
//                            getView()?.addTime2ServiceOnFailure("设备加时成功后通知服务器失败，错误码${it.message.toString()}")
//                        },
//                        {
//                            getView()?.dismissLoading()
//                            getView()?.onError("设备加时成功后通知服务器失败}")
//                        })

        val startTime = DateUtils.getTodayDateTime()
        val timeStamp = DateUtils.getTimeStamp(startTime)
        val endTimeStamp =  addTime?.toLong()!! * 3600 * 1000 + timeStamp
        val endTime = DateUtils.getDateToString(endTimeStamp, "yyyy-MM-dd HH:mm:ss")
        val deviceUseRecord = DeviceUseRecords(timeStamp, userId, deviceId!!, addTime!!, startTime, endTime)
        val insertSuccess = dao.insertOrReplace(deviceUseRecord)
        if (insertSuccess > 0) {
            Logger.d("缓存用户加时使用记录成功")
        }
        val maxConnectCount = 10
        // 当前已重试次数
        var currentRetryCount = 0
        // 重试等待时间
        var waitRetryTime = 0
        service.addTimeSuccess2Service(userId, deviceId, addTime)
                .retryWhen(object : Function<Observable<Throwable>, ObservableSource<Any>> {
                    override fun apply(t: Observable<Throwable>): ObservableSource<Any> {

                        return t.flatMap(object : Function<Throwable, ObservableSource<Int>> {
                            override fun apply(t: Throwable): ObservableSource<Int> {
                                // 输出异常信息
                                Logger.d("发生异常 = ${t.message.toString()}")
                                if (t is IOException) {
                                    Logger.d("属于IO异常，需重试")
                                    /**
                                     * 需求2：限制重试次数
                                     * 即，当已重试次数 < 设置的重试次数，才选择重试
                                     */
                                    if (currentRetryCount < maxConnectCount) {
                                        // 记录重试次数
                                        currentRetryCount++;
                                        Logger.d("重试次数 = " + currentRetryCount);
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
                                        waitRetryTime = 1000 + currentRetryCount * 1000
                                        Logger.d("等待时间 =" + waitRetryTime)

                                        val observable = Observable.just(1)
                                        return observable.delay(waitRetryTime.toLong(), TimeUnit.MILLISECONDS)

                                    } else {
                                        // 若重试次数已 > 设置重试次数，则不重试
                                        // 通过发送error来停止重试（可在观察者的onError（）中获取信息）
                                        return Observable.error(Throwable("重试次数已超过设置次数 =  ${currentRetryCount} ，即不再重试"));
                                    }

                                }
                                // 若发生的异常不属于I/O异常，则不重试
                                // 通过返回的Observable发送的事件 = Error事件 实现（可在观察者的onError（）中获取信息）
                                else {
                                    return Observable.error(Throwable("发生了非网络异常（非I/O异常）"))
                                }
                            }

                        })
                    }
                })
                .compose()
                .compose(lifecycleProvider.bindToLifecycle())
                .flatMap(object : Function<BaseResp, Observable<BaseResp>> {
                    override fun apply(t: BaseResp): Observable<BaseResp> {
                        return if ("0000" == t.returnCode) {
                            Logger.e("加时成功:开始获取剩余时间")
                            val cacheTime = SpUtils.getInt(BaseApplication.getAppContext(), userId + deviceId)
                            SpUtils.put(BaseApplication.getAppContext(), userId + deviceId, addTime.toInt() + cacheTime)
                            dao.deleteByKey(timeStamp)
                            Logger.d("删除缓存用户加时使用记录成功")
                            return service.requestSurplusTime(userId, deviceId)
                        } else {
                            getView()?.addTime2ServiceOnFailure("设备加时成功后通知服务器失败，错误码${t.returnCode}")
                            return Observable.empty()
                        }
                    }
                }).subscribe({
                    getView()?.dismissLoading()
                    if ("0000" == it.returnCode) {
                        Logger.e("加时后获取时间:${it.retnrnJson}")
                        val json = it.retnrnJson.toString().trim()
                        if ("0" != json) {
                            if (!json.isNullOrEmpty()) {
                                val reminTIme = json.toLong()
                                if (reminTIme > 0) {

                                    getView()?.addTime2ServiceOnSuccess(addTime, reminTIme)
                                } else {

                                    getView()?.onError("加时成功，获取设备之前剩余时间错误")
                                }
                            } else {
                                getView()?.onError("加时成功，获取设备之前剩余时间错误")
                            }
                        }

                    } else {
                        getView()?.dismissLoading()
                        getView()?.onError("加时成功，获取设备之前剩余时间错误")
                    }
                },
                        {
                            getView()?.addTime2ServiceOnFailure("设备加时成功后通知服务器失败，错误码${it.message.toString()}")
                        }
                       )
    }


    /**
     * 加时是否可用
     */
    override fun addTimeCanUse(userId: String, deviceId: String, time: String) {
        if (!preparReq(getView(), this)) return
        service.addTimeCanUse(userId, deviceId, time)
                .compose()
                .compose(lifecycleProvider.bindToLifecycle())
                .subscribe(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        super.onNext(t)
                        if ("0000" == t.returnCode) {
                            if ("true" != t.retnrnJson) {
                                //提示充值
                                getView()?.showFillMoneyDialog()
                            } else {
                                getView()?.showAddTimeCanUse()
                            }
                        } else {
                            getView()?.openSuccess2ServiceOnFailure("请求加时失败，错误码${t.returnCode}")
                        }
                    }
                })
    }


}






