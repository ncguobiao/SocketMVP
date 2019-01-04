package com.gb.socket1.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.SpUtils
import com.gb.socket1.R
import com.gb.socket1.util.AesEntryDetry
import com.gb.sockt.blutoothcontrol.ble.cable.BluetoothTestCable
import com.gb.sockt.blutoothcontrol.ble.cable.BluetoothTestCableImpl
import com.gb.sockt.blutoothcontrol.ble.car.BluetoothTestCarImpl
import com.gb.sockt.blutoothcontrol.listener.BleCableListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.orhanobut.logger.Logger

import kotlinx.android.synthetic.main.activity_cable_new_mac.*

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * Created by guobiao on 2019/1/3.
 */
class BleCableNewMACActivity : BaseActivity() {

    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when(msg?.what){
                1->{
                    mPresenter.closeDevice(0)
                }
            }
        }
    }

    private val mPresenter by lazy {
        BluetoothTestCableImpl(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cable_new_mac)

        val mac = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_MAC)
        val pwd = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)
        Logger.d("mac= $mac   pwd=$pwd")
        tvMAC.text = "设备MAC=$mac   pwd=$pwd"
        initView()


        mPresenter.setMAC(mac, object : BleConnectListener {
            override fun connectOnSuccess() {
                tvState.text = resources.getString(R.string.connected)
                doAsync {

                    SystemClock.sleep(200)

                    mPresenter.openDevice(1)
                }
            }

            override fun connectOnFailure() {
                tvState.text = resources.getString(R.string.failure)
            }

            override fun connectOnError() {
                tvState.text = resources.getString(R.string.onError)
            }

        })
                .registerBroadcastReceiver()
                .connect()
        mPresenter.setResponseListener(object : BleCableListener {
            override fun setPwdSuccess(pwd: String?) {
            }

            override fun onError() {
            }

            override fun onWriteSuccess(msg: String?) {
            }

            override fun onWriteFailure(msg: String?) {
            }

            override fun openSuccess() {
                tvMsg.text = "设置设备完成——OK"
                toast("设备开启成功")
//                mHandler?.sendEmptyMessageDelayed(1,2000)

            }

        })

    }

    private fun initView() {

//        mBtnSetPWd.onClick {
//            //修改密码
//            mPresenter.setPWd(password)
//        }
//        mBtnSetMAC.onClick {
//            //修改mac
//            val password = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)
//            Logger.e("修改mac=$mac -- password=$password")
//            SpUtils.put(AppUtils.getContext(), ConstantSP.DEVICE_MAC, mac)
//            mPresenter.setDeviceMac(password, mac)
//            doAsync {
//                mHandler?.removeCallbacksAndMessages(null)
//                SystemClock.sleep(2000)
//                runOnUiThread {
//                    setResult(ConstantSP.SET_MAC_SUCCESS)
//                    finish()
//
//
//                }
//            }
//
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        mPresenter.unregisterBroadcastReceiver()
        mPresenter.close()
    }
}