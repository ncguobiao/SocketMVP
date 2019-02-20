package com.gb.socket1.mvp.view

import com.example.baselibrary.base.IBaseView

/**
 * Created by guobiao on 2019/2/20.
 */
interface ScanQRCodeView: IBaseView{

    fun getCheckedDevice(b:Boolean)
    fun getCheckedDeviceError(msg:String?)
}