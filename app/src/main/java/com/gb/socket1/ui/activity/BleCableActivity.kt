package com.gb.socket1.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.view.View
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.base.BaseMvpActivity
import com.example.baselibrary.common.Constant
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.SpUtils
import com.gb.socket1.R
import com.gb.socket1.injection.module.MainModule
import com.gb.socket1.mvp.presenter.impl.ScanQRCodePresenterImpl
import com.gb.socket1.mvp.view.ScanQRCodeView
import com.gb.socket1.util.AesEntryDetry
import com.gb.sockt.blutoothcontrol.ble.cable.BluetoothTestCableImpl
import com.gb.sockt.blutoothcontrol.listener.BleCableListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.center.injection.component.DaggerMainComponent
import com.mylhyl.circledialog.CircleDialog
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_cable.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * Created by guobiao on 2019/1/3.
 * 充电线设置密码和MAC地址
 */
class BleCableActivity : BaseMvpActivity<ScanQRCodePresenterImpl>(), ScanQRCodeView {


    private var mac: String? = null
    override fun onError(error: String) {
    }

    override fun getCheckedDevice(b: Boolean) {

        Logger.e("是否重复=$b")
        if (b) {
            Logger.d("设备正常")
            tvMSg.text = "设备状态：正常"
        } else {
            Logger.d("设备重复")
            tvMSg.text = "设备状态：重复入库，请联系管理员"
        }


    }

    override fun getCheckedDeviceError(msg: String?) {
        msg?.let {
            toast(it)
            Logger.d(msg)
            tvMSg.text = "设备状态：重复入库，请联系管理员"
        }
    }

    override fun onDataIsNull() {
    }

    override fun layoutId(): Int = R.layout.activity_cable

    override fun initData() {
    }

    private var flag: Boolean = false

    private var deviceName: String? = null



    override fun initView(savedInstanceState: Bundle?) {
        mac = intent.getStringExtra(Constant.DEVICE_MAC)
        deviceName = intent.getStringExtra(Constant.DEVICE_NAME)
        Logger.d("设备名：S$deviceName,MAC：$mac")
        checkedDevice(mac!!, deviceName!!)
        tvState.text = "设备名：S$deviceName\r\nMAC：$mac"
    }


    private fun checkedDevice(macAddress: String, deviceName: String) {
        if (mPresenter.getView() == null) {
            mPresenter.attachView(this)
        }
        mPresenter.checkedDevice(macAddress.replace(":", ""), "S$deviceName")
    }

    override fun initComponent() {
        DaggerMainComponent.builder()
                .activityComponent(activityComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)

        if (mPresenter.getView() == null) {
            mPresenter.attachView(this)
        }

    }

    override fun start() {
    }

    private val NEW_PWD = 1
    private val OLD_PWD = 2
    private var cllickType: Int = NEW_PWD



    override fun onDestroy() {
        super.onDestroy()
        if (mPresenter.getView() != null) {
          mPresenter.detachView()
        }

    }


}