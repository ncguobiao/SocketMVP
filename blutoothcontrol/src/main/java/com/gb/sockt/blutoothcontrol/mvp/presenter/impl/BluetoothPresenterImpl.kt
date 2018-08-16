package com.gb.sockt.blutoothcontrol.mvp.presenter.impl

import com.alibaba.fastjson.JSON
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.BaseSubscriber
import com.example.baselibrary.common.preparReq
import com.example.baselibrary.compose
import com.example.baselibrary.utils.SpUtils
import com.gb.sockt.blutoothcontrol.mvp.presenter.BluetoothPresenter
import com.gb.sockt.blutoothcontrol.mvp.service.BluetoothContrlService
import com.gb.sockt.blutoothcontrol.mvp.service.imp.BluetoothContrlServiceImpl
import com.gb.sockt.blutoothcontrol.mvp.view.BluetoothView
import com.orhanobut.logger.Logger
import de.greenrobot.event.EventBus
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/9.
 */
class BluetoothPresenterImpl @Inject constructor() : BluetoothPresenter, BasePresenter<BluetoothView>() {


    @Inject
    lateinit var service: BluetoothContrlService

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
//        getView()?.openSuccess2ServiceOnSuceess("1")
        if (!preparReq(getView(), this)) return
        service.openSuccess2Service(userId, deviceId, time)
                .compose()
                .compose(lifecycleProvider.bindToLifecycle())
                .subscribe(object : BaseSubscriber<BaseResp>(getView()!!) {
                    override fun onNext(t: BaseResp) {
                        super.onNext(t)
                        if ("0000" == t.returnCode) {
                            //缓存使用时间
                            SpUtils.put(BaseApplication.getAppContext(), userId + deviceId, time.toInt())
                            getView()?.openSuccess2ServiceOnSuceess(time)
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
        service.addTimeSuccess2Service(userId, deviceId, addTime)
                .compose()
                .compose(lifecycleProvider.bindToLifecycle())
                .flatMap(object : Function<BaseResp, Observable<BaseResp>> {
                    override fun apply(t: BaseResp): Observable<BaseResp> {
                        return if ("0000" == t.returnCode) {
                            val cacheTime = SpUtils.getInt(BaseApplication.getAppContext(), userId + deviceId)
                            SpUtils.put(BaseApplication.getAppContext(), userId + deviceId, addTime.toInt() + cacheTime)

                            Logger.e("加时成功:开始获取剩余时间")
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
                        },
                        {
                            getView()?.dismissLoading()
                            getView()?.onError("设备加时成功后通知服务器失败}")
                        })


    }


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




