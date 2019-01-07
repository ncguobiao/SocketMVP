package com.gb.socket1.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.view.Gravity
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
import com.mob.tools.utils.UIHandler.sendEmptyMessageDelayed
import com.mylhyl.circledialog.CircleDialog
import com.orhanobut.logger.Logger

import kotlinx.android.synthetic.main.activity_cable_set_default_mac.*

import org.jetbrains.anko.toast

/**
 * Created by guobiao on 2019/1/3.
 */
class BleCableSetDefaultMACActivity : BaseActivity() {

    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                1 -> {
                    tvSend.text = ""
                    tvRecive.text = ""
                    if (mPresenter.getConnectState()) {
                        mPresenter.setCircleData(pwd)
//                        Logger.d("setCircleData pwd = $pwd")
                        sendEmptyMessageDelayed(1, 3000)

                    }

                }
//                2->{
//                    tvSend.text = ""
//                    tvRecive.text = ""
//                    if (mPresenter.getConnectState()) {
//                        mPresenter.setCircleData(oldPassword)
//                        sendEmptyMessageDelayed(2, 3000)
//
//                    }
//                }
            }
        }
    }

    private val mPresenter by lazy {
        BluetoothTestCableImpl(this)
    }

    private val oldPassword = "FFEECCDDAA998877"
    private val defaultMAC = "F2:35:0A:00:00:00"
    private var pwd: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cable_set_default_mac)
        var mac = intent.getStringExtra("mac")
        mac?.let {
            if (it.contains(":")) {
                val mac = it.replace(":", "")
                //获取新密码
                pwd = AesEntryDetry.getPassword("sensor668", mac)
                Logger.d("password:$pwd")

                //DC, 0E, C1, E9, 36, B9, 5E, 89
            }
        }
//        if (mac.isNullOrEmpty()){
//            mac = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_MAC)
//            pwd = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)
//        }


        mPresenter.setMAC(mac, object : BleConnectListener {
            override fun connectOnSuccess() {
                tvState.text = resources.getString(R.string.connected)
                mHandler.postDelayed({
                    tvSend.text = ""
                    tvRecive.text = ""
                    mHandler.sendEmptyMessageDelayed(1, 2000)
                }, 300)

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
            override fun onCircle() {
                tvMsg.text = "设备密码错误"
//                //获取新密码
//                SpUtils.put(AppUtils.getContext(), ConstantSP.ISSETDEFAULTPWDSUCCESS, true)
//                val pwd = AesEntryDetry.getPassword("sensor668", mac.replace(":", ""))
//                SpUtils.put(AppUtils.getContext(), ConstantSP.DEVICE_PWD, pwd)

            }

            override fun onRecived(data: String) {
                tvRecive.text = "接收数据=$data"
            }

            override fun onWriteSuccess(msg: String?, type: Int) {
                when (type) {
                    BluetoothTestCableImpl.TYPE_CIRCLE -> tvSend.text = "发送${msg}"
                    BluetoothTestCableImpl.TYPE_SETPWD -> tvSend.text = "发送${msg}"
                    BluetoothTestCableImpl.TYPE_SETPWD -> tvSend.text = "发送${msg}"
                    BluetoothTestCableImpl.TYPE_OPEN -> tvSend.text = "发送${msg}"

                }
            }

            override fun onWriteFailure(msg: String?, type: Int) {
                when (type) {
                    BluetoothTestCableImpl.TYPE_CIRCLE -> tvSend.text = "发送${msg}"
                    BluetoothTestCableImpl.TYPE_SETPWD -> tvSend.text = "发送${msg}"
                    BluetoothTestCableImpl.TYPE_SETPWD -> tvSend.text = "发送${msg}"
                    BluetoothTestCableImpl.TYPE_OPEN -> tvSend.text = "发送${msg}"

                }
            }

            override fun setPwdSuccess(password: String?) {
                toast("恢复默认密码成功")
                tvMsg.text ="恢复默认密码成功，PWD=$password"
                pwd = oldPassword
                Logger.d("setPwdSuccess:PWd=$password")
                updateDefaultMac()
//                mHandler.removeMessages(1)
//                mHandler.sendEmptyMessageDelayed(2,1000)

            }

            override fun onError(byteArrayToHexString: String) {
                Logger.d("onError:PWd=$byteArrayToHexString")
                tvRecive.text = "接收数据=$byteArrayToHexString"

            }

            override fun openSuccess() {

            }

        })

        mBtnSetMAC.onClick {
            updateDefaultMac()
        }
        mBtnClearPwd.onClick {
            CircleDialog.Builder()
                    .setTitle("恢复默认密码!")
                    .setCanceledOnTouchOutside(false)
                    .setTextColor(resources.getColor(com.example.baselibrary.R.color.red_normal))
                    .setText("是否恢复成默认密码！")
                    .setPositive("确定") {
                        //                        val issetdefaultpwdsuccess = SpUtils.getBoolean(AppUtils.getContext(),ConstantSP.ISSETDEFAULTPWDSUCCESS)
//                        if (!issetdefaultpwdsuccess){
//                            val pwd = AesEntryDetry.getPassword("sensor668", mac.replace(":", ""))
//                        }
                        pwd?.let {
                            mPresenter.setPWd(it, oldPassword)
                        }

                    }
                    .setNegative("取消", null)
                    .setCancelable(false)
                    .show(supportFragmentManager)
        }
        mBtnPwd.onClick {
            pwd?.let {
                CircleDialog.Builder()
                        .setTitle("设置新密码!")
                        .setCanceledOnTouchOutside(false)
                        .setTextColor(resources.getColor(com.example.baselibrary.R.color.red_normal))
                        .setText("是否设置新密码！")
                        .setPositive("确定") {
                            pwd?.let {
                                mPresenter.setPWd(oldPassword, it)
                            }

                        }
                        .setNegative("取消", null)
                        .setCancelable(false)
                        .show(supportFragmentManager)

            }
        }


    }

    private fun updateDefaultMac() {
        if (mPresenter.getConnectState())
            mPresenter.setDeviceMac(oldPassword, defaultMAC)
        else toast("蓝牙未连接无法恢复成默认MAC")
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        mPresenter.unregisterBroadcastReceiver()
        mPresenter.close()
    }
}