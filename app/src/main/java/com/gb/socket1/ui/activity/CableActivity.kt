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
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_cable.*
import kotlinx.android.synthetic.main.main_content_layout.view.*
import org.jetbrains.anko.doAsync

/**
 * Created by guobiao on 2019/1/3.
 */
class CableActivity : BaseActivity() {

    private val mPresenter by lazy {
        BluetoothTestCableImpl(this)
    }

    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    if (mPresenter.getConnectState()) {
                        mPresenter.setCicle(password)
                        sendEmptyMessageDelayed(1, 1000)
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

        initView()

        //加密mac
        mac?.let {
            if (it.contains(":")) {
                val mac = it.replace(":", "")
                //获取新密码
                password = AesEntryDetry.getPassword("sensor668", mac)
                SpUtils.put(AppUtils.getContext(),ConstantSP.DEVICE_PWD,password)
                Logger.d("password:$password")
            }
        }

        //password:9C4A816C3B02FF35-(F2:35:0A:00:00:55)
        //F2:35:0A:00:00:00  //F2:35:0A:00:00:55 //55:00:00:0A:35:F2
        mPresenter.setMAC("F2:35:0A:00:00:55", object : BleConnectListener {
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

        })
                .registerBroadcastReceiver()
                .connect()


    }

    private fun initView() {
        mBtnSetMAC.onClick {
            //修改mac
          val password=SpUtils.getString(AppUtils.getContext(),ConstantSP.DEVICE_PWD)
            mPresenter.setDeviceMac(password,mac)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        mPresenter.unregisterBroadcastReceiver()
        mPresenter.close()
    }
}