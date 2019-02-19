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
    private var presenter: BluetoothTestCarImpl? = null
    private val rxPermissions by lazy {
        RxPermissions(this)
    }
    //配置连接次数
    private var configCoinCount: Int = 0
    private var tempCount: Int = 0
    //是否清除计数
    private var clearCoinState: Boolean = false
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                1 -> {
                    if (configCoinCount > 0) {
                        presenter?.coin()
                        configCoinCount--
                        tvTempCoinCount?.text = "剩余投币个数=$configCoinCount"
                        sendEmptyMessageDelayed(1, 2000)
                    }

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = BluetoothTestCarImpl(this)
        setContentView(R.layout.activity_bluetooth_car_connect)
        mac = intent.getStringExtra("mac")
        configCoinCount = intent.getIntExtra("configCoinCount", 0)
        Logger.d("mac=$mac")
        tvMac.text = "连接设备:mac=$mac"
        presenter?.setMAC(mac, object : BleConnectListener {
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
                            presenter?.connect()
                            startTime = System.currentTimeMillis()
                        }

                    } else {
//                            toast("配置投币次数用完")
                        mTvMessage.text = "配置投币次数用完"
                        mHandler.postDelayed({
                            setResult(1000)
                            toast("设备MAC= $mac \n已测试完成")
                            finish()
                        }, 5000)

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
                    presenter?.requestSeed()
                }

            }

        })
        //蓝牙响应监听
        presenter?.setResponseListener(object : BluetoothTestCarListener {
            override fun coinOnRetry(result: Byte) {
                toast("投币失败:${result}个,继续投币中...")
            }

            //设备状态正常
            override fun getDeviceInfoOnIdle() {
                deviceState = true
                if (!clearCoinState) {
                    //自动投币
//                    mHandler.sendEmptyMessageDelayed(1,300)
                    mHandler.postDelayed({
                        presenter?.coin()
                        configCoinCount--
                        tvTempCoinCount?.text = "剩余投币个数=$configCoinCount"
                    }, 300)

                } else {
                    presenter?.clearCount()
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
                tvDeviceCoinCount.text = "手动投币次数=$count , 蓝牙投币次数=$bleCount"
                tempCount = count
            }

            override fun getDeviceInfoOnError(error: String) {
                mTvMessage.text = error
                toast(error)
            }

            override fun coinOnSuccess(byteArrayToHexString: String?) {
                Logger.d("coinOnSuccess Data=$byteArrayToHexString")
                toast("投币成功")
                var cacheCount = SpUtils.getLong(AppUtils.getContext(), Constant.COIN_COUNT + mac)
//                Logger.d("cacheCount=$cacheCount")
                cacheCount += 1
                SpUtils.put(AppUtils.getContext(), Constant.COIN_COUNT + mac, cacheCount)
                tvCoinCount.text = "手机成功投币次数=$cacheCount，\n当前设备MAC=$mac"
//                Logger.d("次数:$cacheCount")
                tvTotalCoinCount.text = "投币总数:${tempCount + cacheCount}"
            }

            override fun clearCountOnSuccess(byteArrayToHexString: String?) {
                clearCoinState = false
                toast("清除计数成功")
//                SpUtils.put(AppUtils.getContext(), Constant.COIN_COUNT+mac, 0L)
                SpUtils.remove(Constant.COIN_COUNT + mac)
                tvCoinCount.text = "手机成功投币次数=0"

            }

            override fun clearConfigOnSuccess(byteArrayToHexString: String?) {
                toast("清除配置成功")
            }
        })

        presenter?.registerBroadcastReceiver()

        //连接自动连接蓝牙
        presenter?.connect()
        startTime = System.currentTimeMillis()
        //连接
        mbtnConnect.onClick {
            presenter?.let {
                if (!it.getConnectState()) {
                    mTvMessage?.text = ""
                    mTvReciver?.text = ""
                    mProgressBar.visibility = View.VISIBLE
                    it.connect()
                    startTime = System.currentTimeMillis()
                }

            }


        }

        //断开连接
        mBtnDisconnect.onClick {
            presenter?.let {
                if (it.getConnectState()) {
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
        }

        //开始投币
        mBtnCoin.onClick {
            presenter?.let {
                if (it.getConnectState()) {
                    mProgressBar.visibility = View.GONE
                    mTvMessage?.text = ""
                    mTvReciver?.text = ""
                    sb = null
                    sb = StringBuffer()
                    mConsumTime.text = ""
                    if (deviceState) {
                        it.coin()
                    } else {
                        toast("设备状态异常")
                    }
                } else {
                    toast("蓝牙已断开")
                }
            }
        }

        //  清除手机缓存的投币次数
        mBtnClearCacheCoinCount.onClick {
            SpUtils.put(AppUtils.getContext(), Constant.COIN_COUNT + mac, 0L)
            tvCoinCount.text = "手机成功投币次数=0"
            toast("清除手机缓存的投币次数成功")
        }
        //清除计数
        mBtnClearCoinCount.onClick {
            clearCoinState = true
            presenter?.let {
                if (it.getConnectState()) {
                    mProgressBar.visibility = View.GONE
                    mTvMessage?.text = ""
                    mTvReciver?.text = ""
                    sb = null
                    sb = StringBuffer()
                    mConsumTime.text = ""
                    it.clearCount()
                } else {
                    it.connect()
                    startTime = System.currentTimeMillis()
                }
            }

        }
        //清除配置
        mBtnClearConfig.onClick {
            presenter?.let {
                if (it.getConnectState()) {
                    mProgressBar.visibility = View.GONE
                    mTvMessage?.text = ""
                    mTvReciver?.text = ""
                    sb = null
                    sb = StringBuffer()
                    mConsumTime.text = ""
                    it.clearConfig()
                } else {
                    toast("蓝牙已断开")
                }
            }


        }
        var cacheCount = SpUtils.getLong(AppUtils.getContext(), Constant.COIN_COUNT + mac)
        tvCoinCount.text = "手机成功投币次数=$cacheCount"

    }


    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        presenter?.unregisterBroadcastReceiver()
        presenter?.close()
        presenter = null
        sb = null
    }

}





