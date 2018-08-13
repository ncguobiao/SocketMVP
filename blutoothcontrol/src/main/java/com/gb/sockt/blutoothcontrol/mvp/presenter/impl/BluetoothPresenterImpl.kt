package com.gb.sockt.blutoothcontrol.mvp.presenter.impl

import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.preparReq
import com.example.baselibrary.compose
import com.gb.sockt.blutoothcontrol.mvp.presenter.BluetoothPresenter
import com.gb.sockt.blutoothcontrol.mvp.service.BluetoothContrlService
import com.gb.sockt.blutoothcontrol.mvp.service.imp.BluetoothContrlServiceImpl
import com.gb.sockt.blutoothcontrol.mvp.view.BluetoothView
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
                        getView()?.onError("未查询到设备")
                        return@map false
                    } else {
                        return@map false
                    }
                }.observeOn(Schedulers.io())
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
                    getView()?.onError(it?.message.toString())

                }
                )
    }


}




