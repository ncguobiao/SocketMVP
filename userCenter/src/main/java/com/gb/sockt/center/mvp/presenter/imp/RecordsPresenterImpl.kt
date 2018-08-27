package com.gb.sockt.center.mvp.presenter.imp

import com.alibaba.fastjson.JSON
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.BaseSubscriber
import com.example.baselibrary.common.preparReq
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.execption.ExceptionHandle
import com.gb.sockt.center.data.domain.RechargeRecordsBean
import com.gb.sockt.center.data.domain.UseRecordBean
import com.gb.sockt.center.mvp.presenter.RecordsPresenter
import com.gb.sockt.center.mvp.service.UserCenterService
import com.gb.sockt.center.mvp.view.RecordsView
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/23.
 */
class RecordsPresenterImpl @Inject constructor() : RecordsPresenter, BasePresenter<RecordsView>() {


    @Inject
    lateinit var service: UserCenterService

    override fun getUseRecords(className: String, userId: String, curPage: String) {
        if (!preparReq(getView(), this)) return
        service.getUseRecords(className, userId, curPage)
                .compose(lifecycleProvider.bindToLifecycle())
                .compose()
                .subscribe(object : Consumer<BaseResp> {
                    override fun accept(t: BaseResp?) {
                        getView()?.dismissLoading()
                        if ("0000"==t?.returnCode){
                            val jsonData = t.retnrnJson.toString().trim()
                            val data = JSON.parseObject(jsonData, UseRecordBean::class.java)

                            getView()?.showUseRecords(data)
                        }else{
                            getView()?.onError("获取使用记录失败,错误码${t?.returnCode}")
                        }
                    }

                }, Consumer {
                    getView()?.dismissLoading()
                    getView()?.showError(ExceptionHandle.handleException(it), ExceptionHandle.errorCode)

                })
    }

    override fun getRechargeRecords(userId: String) {
        if (!preparReq(getView(), this)) return
        service.getRechargeRecords(userId)
                .compose(lifecycleProvider.bindToLifecycle())
                .compose()
                .subscribe(object : Consumer<BaseResp> {
                    override fun accept(t: BaseResp?) {
                        getView()?.dismissLoading()
                        if ("0000"==t?.returnCode){
                            val jsonData = t.retnrnJson.toString().trim()
                            val parseArray = JSON.parseArray(jsonData, RechargeRecordsBean::class.java)
                            getView()?.showRechargeRecords(parseArray)
                        }else{
                            getView()?.onError("获取充值明细记录失败,错误码${t?.returnCode}")
                        }
                    }

                }, Consumer {
                    getView()?.dismissLoading()
                    getView()?.showError(ExceptionHandle.handleException(it), ExceptionHandle.errorCode)

                } )
    }


    override fun deleteUserRecord(appType: String, useDeviceId: String, userId: String) {
        if (!preparReq(getView(), this)) return
        service.getRechargeRecords(userId)
                .compose(lifecycleProvider.bindToLifecycle())
                .compose()
                .subscribe(object :BaseSubscriber<BaseResp>(getView()!!){
                    override fun onNext(t: BaseResp) {
                        if ("0000"==t.returnCode){
                            getView()?.deleteRechargeRecordOnSuccess()
                        }else{
                            getView()?.onError("删除失败，原因${t.returnCode}")
                        }

                    }
                })

    }

    override fun loadMoreData() {


    }

}