package com.gb.sockt.center.mvp.presenter.imp

import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.common.BaseSubscriber
import com.example.baselibrary.common.preparReq
import com.example.baselibrary.compose
import com.gb.sockt.center.mvp.presenter.RecordsPresenter
import com.gb.sockt.center.mvp.service.UserCenterService
import com.gb.sockt.center.mvp.view.RecordsView
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/23.
 */
class RecordsPresenterImpl @Inject  constructor(): RecordsPresenter ,BasePresenter<RecordsView>(){

    @Inject
    lateinit var service: UserCenterService

    override fun getUseRecords(className:String,userId:String,curPage:String) {
        if (!preparReq(getView(), this)) return
        service.getUseRecords(className, userId, curPage)
                .compose(lifecycleProvider.bindToLifecycle())
                .compose()
                .subscribe(object :BaseSubscriber<BaseResp>(getView()!!){
                    override fun onNext(t: BaseResp) {


                    }
                })
    }

    override fun getRechargeRecords(userId: String) {
        if (!preparReq(getView(), this)) return
        service.getRechargeRecords(userId)
                .compose(lifecycleProvider.bindToLifecycle())
                .compose()
                .subscribe(object :BaseSubscriber<BaseResp>(getView()!!){
                    override fun onNext(t: BaseResp) {


                    }
                })
    }
}