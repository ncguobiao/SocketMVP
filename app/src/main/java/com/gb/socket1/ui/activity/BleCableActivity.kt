package com.gb.socket1.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.view.View
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.SpUtils
import com.gb.socket1.R
import com.gb.socket1.util.AesEntryDetry
import com.gb.sockt.blutoothcontrol.ble.cable.BluetoothTestCableImpl
import com.gb.sockt.blutoothcontrol.listener.BleCableListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_cable.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * Created by guobiao on 2019/1/3.
 */
class BleCableActivity : BaseActivity() {

    private val mPresenter by lazy {
        BluetoothTestCableImpl(this)
    }

    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                //循环发送心跳
                1 -> {
                    if (mPresenter.getConnectState()) {
                        val pwd = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)

                        mPresenter.setCircle(pwd)
                        sendEmptyMessageDelayed(1, 1000)
                    }
                }
                2->{
                    sendEmptyMessageDelayed(2, 1500)
                    if (!mPresenter.getConnectState()){
                        removeCallbacksAndMessages(null)
                        SpUtils.put(AppUtils.getContext(), ConstantSP.DEVICE_MAC, mac)
                        setResult(ConstantSP.SET_MAC_SUCCESS)
                        finish()
                        Logger.e("蓝牙断开，主动关闭当前页面")
                    }else{
                        mPresenter.setDeviceMac(password, mac)
                    }
                }
            }
        }
    }

    private var mac: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cable)

        mac = intent.getStringExtra("mac")
        Logger.d("mac:$mac")

        //加密mac
        mac?.let {
            if (it.contains(":")) {
                val mac = it.replace(":", "")
                //获取新密码
                password = AesEntryDetry.getPassword("sensor668", mac)
                Logger.d("password:$password")
                //DC, 0E, C1, E9, 36, B9, 5E, 89
            }
        }

        initView()


        //password:9C4A816C3B02FF35-(F2:35:0A:00:00:55)
        //F2:35:0A:00:00:00  //F2:35:0A:00:00:55 //55:00:00:0A:35:F2
        mPresenter.setMAC("F2:35:0A:00:00:00", object : BleConnectListener {
            override fun connectOnSuccess() {
                tvState.text = resources.getString(R.string.connected)
                doAsync {

                    SystemClock.sleep(300)
                    //修改密码
                    mPresenter.setPWd(password)
                    //心跳数据
                    mHandler.sendEmptyMessageDelayed(1, 300)

                }
            }

            override fun connectOnFailure() {
                tvState.text = resources.getString(R.string.failure)
            }

            override fun connectOnError() {
                tvState.text = resources.getString(R.string.onError)
            }

        }).registerBroadcastReceiver()
        mPresenter.setResponseListener(object : BleCableListener {
            override fun openSuccess() {
            }

            override fun setPwdSuccess(pwd:String?) {
                tvMessage?.text = "设置密码成功：$pwd"
                mBtnSetMAC.visibility = View.VISIBLE
            }

            override fun onError() {
            }

            override fun onWriteSuccess(msg: String?) {
            }

            override fun onWriteFailure(msg: String?) {

            }

        })
        //开启连接
        mPresenter.connect()

    }

    private fun initView() {
        mBtnSetMAC.onClick {
            toast("正在设置新的MAC地址=$mac")
            mBtnSetMAC.visibility = View.INVISIBLE
            //修改mac
            val password = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)
            Logger.e("修改mac=$mac -- password=$password")
            mPresenter.setDeviceMac(password, mac)
            mHandler.sendEmptyMessageDelayed(2,1500)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        mPresenter.unregisterBroadcastReceiver()
        mPresenter.close()
    }
}