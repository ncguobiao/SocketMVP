package com.gb.socket1.mvp.presenter.impl

import android.widget.Toast
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.common.preparReq
import com.example.baselibrary.compose
import com.gb.socket1.mvp.presenter.ScanQRCodePresenter
import com.gb.socket1.mvp.service.MainService
import com.gb.socket1.mvp.service.impl.MainServiceImpl
import com.gb.socket1.mvp.view.MainView
import com.gb.socket1.mvp.view.ScanQRCodeView
import com.orhanobut.logger.Logger
import javax.inject.Inject

/**
 * Created by guobiao on 2019/2/20.
 */
class ScanQRCodePresenterImpl @Inject constructor() : ScanQRCodePresenter, BasePresenter<ScanQRCodeView>() {

    private val service by lazy {
        MainServiceImpl()
    }

    override fun checkedDevice(mac: String, deviceName: String) {
        if (!preparReq(getView(), this)) return
        Logger.d("ScanQRCodePresenterImpl_service+$service")
        service.checkedDevice(mac, deviceName)
//                .compose(lifecycleProvider.bindToLifecycle())
                .compose()
                .subscribe {it->
                    getView()?.dismissLoading()
                    if ("0000"==it.returnCode){
                        getView()?.getCheckedDevice(true)
                    }else if ("00002"==it.returnCode){
                        getView()?.getCheckedDevice(false)
                        getView()?.getCheckedDeviceError(it.returnMsg)
                    }else{
                        getView()?.getCheckedDevice(false)
                        getView()?.getCheckedDeviceError(it.returnMsg)
                    }
                    Logger.e("checkedDevice returnCode=${it.returnCode},returnMsg=${it.returnMsg},returnJson=${it.retnrnJson}")
                }

    }

}