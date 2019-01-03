package com.gb.socket1.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.view.View
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.Constant
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.SpUtils
import com.gb.socket1.R
import com.gb.sockt.blutoothcontrol.ble.car.BluetoothTestCarImpl
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestCarListener
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_bluetooth_car_connect.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.util.ArrayList


/**
 * Created by guobiao on 2018/11/15.
 * 测试蓝牙连接
 */


class BluetoothCarActivity : BaseActivity() {

    private var startTime: Long = 0L
    private var sb: StringBuffer? = null
    private var mac: String? = null
    private var deviceState: Boolean = false
    private val presenter by lazy {
        BluetoothTestCarImpl(this)
    }
    private val rxPermissions by lazy {
        RxPermissions(this)
    }
    //配置连接次数
    private var configCoinCount: Int = 0


    //是否清除计数
    private var clearCoinState: Boolean = false
//    private var mHandler: Handler = @SuppressLint("HandlerLeak")
//    object : Handler() {
//        override fun handleMessage(msg: Message?) {
//            super.handleMessage(msg)
//        }
//    }

    private var tempCount: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_car_connect)
        mac = intent.getStringExtra("mac")
        configCoinCount = intent.getIntExtra("configCoinCount",0)
        Logger.d("mac=$mac")
        tvMac.text = "连接设备:mac=$mac"
        presenter.setMAC(mac, object : BleConnectListener {
            override fun connectOnError() {
                showToast("该设备不支持蓝牙")
                mTvState.text = "连接错误"
                mProgressBar.visibility = View.GONE
                mTvState.setTextColor(Color.RED)
            }

            override fun connectOnFailure() {
                mTvState.text = "连接失败"
                mTvState.setTextColor(Color.RED)
                mProgressBar.visibility = View.GONE
//
                if (clearCoinState) {
                    toast("正在清除手机计数")
                } else {
                    //配置的连接次数可用，需要继续连接
                    if (configCoinCount > 0) {
                        //继续连接
                        tvMac.text = mac
                        doAsync {
                            SystemClock.sleep(500)
                            presenter.connect()
                            startTime = System.currentTimeMillis()
                        }

                    } else {
//                            toast("配置投币次数用完")
                        mTvMessage.text = "配置投币次数用完"

                        doAsync {
                            setResult(1000)
                            runOnUiThread {
                                toast("设备MAC= $mac 已测试完成/r/n")
                            }
                            SystemClock.sleep(8000)
                            runOnUiThread {
                                finish()
                            }

                        }
                    }
                }
                //清除上一次连接的设备地址
            }

            override fun connectOnSuccess() {
                mProgressBar.visibility = View.GONE
                val consumTime = System.currentTimeMillis() - startTime
                mConsumTime.text = "连接耗时：${consumTime}ms"
                mTvState.text = "连接成功"
                mTvState.setTextColor(Color.GREEN)
                doAsync {
                    SystemClock.sleep(300)
                    presenter.requestSeed()
                }

            }

        })
        //蓝牙响应监听
        presenter.setResponseListener(object : BluetoothTestCarListener {
            //设备状态正常
            override fun getDeviceInfoOnIdle() {
                deviceState = true
                if (!clearCoinState) {
                    //自动投币
                    doAsync {
                        SystemClock.sleep(300)
                        presenter.coin()
                        configCoinCount--
//                        Logger.d("tempConfigCoinCount=$configCoinCount")
                        runOnUiThread {
                            tvTempCoinCount?.text = "剩余投币个数=$configCoinCount"
                        }

                    }
                } else {
                    presenter.clearCount()
                }

            }

            override fun onError(data: String) {
//                Logger.d("onError:$data")
            }

            override fun onWriteFailure(msg: String) {

            }

            override fun onWriteSuccess(msg: String) {

            }

            override fun requestOnSuccess(byteArrayToHexString: String?) {

            }

            override fun checkSeekOnSuccess(byteArrayToHexString: String?) {

            }

            override fun getDeviceInfoOnSuccess(count: Int, bleCount: Int) {
//                Logger.d("投币次数=$count , 蓝牙使用次数=$bleCount")
                tvDeviceCoinCount.text = "投币次数=$count , 蓝牙使用次数=$bleCount"
                val cacheCount = SpUtils.getLong(AppUtils.getContext(), Constant.COIN_COUNT)
//                tvTotalCoinCount.text = "投币总数:${count + cacheCount}"
                tempCount = count
            }

            override fun getDeviceInfoOnError(error: String) {
                mTvMessage.text = error
                toast(error)
            }

            override fun coinOnSuccess(byteArrayToHexString: String?) {
                Logger.d("coinOnSuccess Data=$byteArrayToHexString")
                toast("投币成功")
                var cacheCount = SpUtils.getLong(AppUtils.getContext(), Constant.COIN_COUNT)
//                Logger.d("cacheCount=$cacheCount")
                cacheCount += 1
                SpUtils.put(AppUtils.getContext(), Constant.COIN_COUNT, cacheCount)
                tvCoinCount.text = "手机成功投币次数=$cacheCount"
//                Logger.d("次数:$cacheCount")


                tvTotalCoinCount.text = "投币总数:${tempCount + cacheCount}"

            }

            override fun clearCountOnSuccess(byteArrayToHexString: String?) {
                clearCoinState = false
                toast("清除计数成功")
                SpUtils.put(AppUtils.getContext(), Constant.COIN_COUNT, 0L)
                tvCoinCount.text = "手机成功投币次数=0"

            }

            override fun clearConfigOnSuccess(byteArrayToHexString: String?) {
                toast("清除配置成功")
            }
        })

        presenter.registerBroadcastReceiver()

        //连接自动连接蓝牙
        presenter.connect()

        //连接
        mbtnConnect.onClick {
            if (!presenter?.getConnectState())
                mTvMessage?.text = ""
            mTvReciver?.text = ""
            mProgressBar.visibility = View.VISIBLE
            presenter.connect()
            startTime = System.currentTimeMillis()

        }

        //断开连接
        mBtnDisconnect.onClick {
            if (presenter?.getConnectState()) {
                mProgressBar.visibility = View.GONE
                mTvMessage?.text = ""
                mTvReciver?.text = ""
                sb = null
                sb = StringBuffer()
                mConsumTime.text = ""
                presenter?.close()
            } else {
                toast("蓝牙已断开")
            }

        }

        //开始投币
        mBtnCoin.onClick {

            if (presenter?.getConnectState()) {
                mProgressBar.visibility = View.GONE
                mTvMessage?.text = ""
                mTvReciver?.text = ""
                sb = null
                sb = StringBuffer()
                mConsumTime.text = ""
                if (deviceState) {
                    presenter.coin()
                } else {
                    toast("设备状态异常")
                }
            } else {
                toast("蓝牙已断开")
            }
        }
        //  清除手机缓存的投币次数
        mBtnClearCacheCoinCount.onClick {
            SpUtils.put(AppUtils.getContext(), Constant.COIN_COUNT, 0L)
            tvCoinCount.text = "手机成功投币次数=0"
            toast("清除手机缓存的投币次数成功")
        }
        //清除计数
        mBtnClearCoinCount.onClick {
            clearCoinState = true
            if (presenter?.getConnectState()) {
                mProgressBar.visibility = View.GONE
                mTvMessage?.text = ""
                mTvReciver?.text = ""
                sb = null
                sb = StringBuffer()
                mConsumTime.text = ""
                presenter?.clearCount()
            } else {
                presenter.connect()
                startTime = System.currentTimeMillis()
            }
        }
        //清除配置
        mBtnClearConfig.onClick {
            if (presenter?.getConnectState()) {
                mProgressBar.visibility = View.GONE
                mTvMessage?.text = ""
                mTvReciver?.text = ""
                sb = null
                sb = StringBuffer()
                mConsumTime.text = ""
                presenter?.clearConfig()
            } else {
                toast("蓝牙已断开")
            }
        }
        var cacheCount = SpUtils.getLong(AppUtils.getContext(), Constant.COIN_COUNT)
        tvCoinCount.text = "手机成功投币次数=$cacheCount"

    }


    override fun onDestroy() {
        super.onDestroy()
        presenter?.unregisterBroadcastReceiver()
        presenter?.close()
        sb = null
    }

}





