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


/**
 * Created by guobiao on 2018/11/15.
 * 测试蓝牙连接
 */


class BluetoothCarActivity : BaseActivity() {

    private var startTime: Long = 0L

    private var sb: StringBuffer? = null
    private var mac: String? = null

    private var clickAction: Int = 0
    private val CLICK_ADD: Int = 1
    private val CLICK_LESS: Int = 2

    private var resultLength: Int = 0

    private var macAddress: String = ""

    private var deviceName: String = ""

    private var deviceState: Boolean = false
    private val presenter by lazy {
        BluetoothTestCarImpl(this)
    }

    private val rxPermissions by lazy {
        RxPermissions(this)
    }
    private var configCoinCount: Int= 0

    private var mHandler:Handler = @SuppressLint("HandlerLeak")
    object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_car_connect)
        mac = intent.getStringExtra("mac")
        Logger.d("mac=${mac}")

        val connectCount = mEditText.text.toString().trim()


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
                if (configCoinCount>0){
                    //继续连接
                    presenter?.connect()
                }else{
                    toast("配置投币次数用完")
                    mTvMessage.text="配置投币次数用完"
                }

            }

            override fun connectOnSuccess() {
                mProgressBar.visibility = View.GONE
                val endTimeMillis = System.currentTimeMillis()
                val consumTime = endTimeMillis - startTime
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
                //自动投币
                presenter?.coin()
            }

            override fun onError(data: String) {
                Logger.d("onError:$data")
            }

            override fun onWriteFailure(msg: String) {
                Logger.d("onWriteFailure:$msg")
            }

            override fun onWriteSuccess(msg: String) {
                Logger.d("onWriteSuccess:$msg")
            }

            override fun requestOnSuccess(byteArrayToHexString: String?) {
                Logger.d("requestOnSuccess:$byteArrayToHexString")
            }

            override fun checkSeekOnSuccess(byteArrayToHexString: String?) {
                Logger.d("checkSeekOnSuccess:$byteArrayToHexString")
            }

            override fun getDeviceInfoOnSuccess(count: Int, bleCount: Int) {
                Logger.d("投币次数=$count , 蓝牙使用次数=$bleCount")
                tvDeviceCoinCount.text="投币次数=$count , 蓝牙使用次数=$bleCount"
            }

            override fun getDeviceInfoOnError(error: String) {
                mTvMessage.text = error
                toast(error)
            }

            override fun coinOnSuccess(byteArrayToHexString: String?) {
                toast("投币成功")
                var cacheCount = SpUtils.getLong(AppUtils.getContext(), Constant.COIN_COUNT)
                Logger.d("cacheCount=$cacheCount")
                cacheCount += 1
                SpUtils.put(AppUtils.getContext(), Constant.COIN_COUNT, cacheCount)
                tvCoinCount.text = "手机成功投币次数=$cacheCount"
                Logger.d("次数:$cacheCount")
                configCoinCount--
                mEditText.setText("${configCoinCount}")
            }

            override fun clearCountOnSuccess(byteArrayToHexString: String?) {
                Logger.d("clearCountOnSuccess:$byteArrayToHexString")
            }

            override fun clearConfigOnSuccess(byteArrayToHexString: String?) {
                Logger.d("clearConfigOnSuccess:$byteArrayToHexString")
            }
        })

        presenter.registerBroadcastReceiver()
        //连接
        mbtnConnect.onClick {
            val connectCount = mEditText.text.toString().trim()

            if (connectCount.isEmpty() ) {
                toast("请配置投币次数")
                return@onClick
            }
            //配置的投币次数
            configCoinCount = connectCount.toInt()
            if (configCoinCount < 1){
                toast("请配置投币次数，且须大于1次")
                return@onClick
            }

            if (!presenter?.getConnectState())
                mTvMessage?.text = ""
            mTvReciver?.text = ""
            mProgressBar.visibility = View.VISIBLE
            presenter?.connect()
            startTime = System.currentTimeMillis()
            Logger.d("startTime:$startTime")
        }

        //断开连接
        mBtnDisconnect.onClick {
            val connectCount = mEditText.text.toString().trim()
            if (connectCount.isEmpty() || connectCount.toInt() < 1) {
                toast("请配置投币次数")
                return@onClick
            }
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
            val connectCount = mEditText.text.toString().trim()
            if (connectCount.isEmpty() || connectCount.toInt() < 1) {
                toast("请配置投币次数")
                return@onClick
            }
            if (presenter?.getConnectState()) {
                mProgressBar.visibility = View.GONE
                mTvMessage?.text = ""
                mTvReciver?.text = ""
                sb = null
                sb = StringBuffer()
                mConsumTime.text = ""
                if (deviceState) {
                    presenter?.coin()
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
            if (presenter?.getConnectState()) {
                mProgressBar.visibility = View.GONE
                mTvMessage?.text = ""
                mTvReciver?.text = ""
                sb = null
                sb = StringBuffer()
                mConsumTime.text = ""
                presenter?.clearCount()
            } else {
                toast("蓝牙已断开")
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





