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
import com.mylhyl.circledialog.CircleDialog
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
                    tvSend.text=""
                    tvRecive.text = ""
                    mPresenter.closeDevice(0)
                }
            }
        }
    }

    private val mPresenter by lazy {
        BluetoothTestCableImpl(this)
    }


    private var pwd: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cable_new_mac)
        var mac= intent.getStringExtra("mac")
        mac?.let {
            if (it.contains(":")) {
                val mac = it.replace(":", "")
                //获取新密码
                pwd = AesEntryDetry.getPassword("sensor668", mac)
                Logger.d("password:$pwd")
                 SpUtils.put(AppUtils.getContext(), ConstantSP.DEVICE_PWD,pwd)
                //DC, 0E, C1, E9, 36, B9, 5E, 89
            }
        }
        if (mac.isNullOrEmpty()){
            mac = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_MAC)
            pwd = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)
        }

        Logger.d("mac= $mac   pwd=$pwd")
        tvMAC.text = "设备MAC=$mac   pwd=$pwd"

        mPresenter.setMAC(mac, object : BleConnectListener {
            override fun connectOnSuccess() {
                tvState.text = resources.getString(R.string.connected)
                doAsync {

                    SystemClock.sleep(200)
                    tvSend.text=""
                    tvRecive.text = ""
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
            override fun onRecived(data: String) {
                tvRecive.text ="接收数据=$data"
            }

            override fun onWriteSuccess(msg: String?, type: Int) {
                when(type){
                    BluetoothTestCableImpl.TYPE_CIRCLE->tvSend.text ="发送${msg}"
                    BluetoothTestCableImpl.TYPE_SETPWD->tvSend.text ="发送${msg}"
                    BluetoothTestCableImpl.TYPE_SETPWD->tvSend.text ="发送${msg}"
                    BluetoothTestCableImpl.TYPE_OPEN->tvSend.text ="发送${msg}"

                }
            }

            override fun onWriteFailure(msg: String?, type: Int) {
                when(type){
                    BluetoothTestCableImpl.TYPE_CIRCLE->tvSend.text ="发送${msg}"
                    BluetoothTestCableImpl.TYPE_SETPWD->tvSend.text ="发送${msg}"
                    BluetoothTestCableImpl.TYPE_SETPWD->tvSend.text ="发送${msg}"
                    BluetoothTestCableImpl.TYPE_OPEN->tvSend.text ="发送${msg}"

                }
            }

            override fun setPwdSuccess(pwd: String?) {
            }

            override fun onError(byteArrayToHexString: String) {
                tvRecive.text ="接收数据=$byteArrayToHexString"
            }


            override fun openSuccess() {
                tvMsg.visibility = View.VISIBLE
                toast("设备开启成功")
               var mdialog = CircleDialog.Builder()
                        .setTitle("是否清楚本次配置!")
                       .setCanceledOnTouchOutside(true)
                        .setTextColor(resources.getColor(com.example.baselibrary.R.color.red_normal))
                        .setText("清除本次配置后，\n方可进行新设备的配置！")
                        .setPositive("确定") {

                            SpUtils.put(AppUtils.getContext(),ConstantSP.DEVICE_PWD,"")
                            SpUtils.put(AppUtils.getContext(),ConstantSP.ISSETDEFAULTPWDSUCCESS,false)
                            SpUtils.remove(ConstantSP.DEVICE_PWD)
                            SpUtils.remove(ConstantSP.ISSETDEFAULTPWDSUCCESS)
                            finish()
                        }
                        .setNegative("取消",null)
                        .setCancelable(true)
                        .show(supportFragmentManager)


//                mHandler?.sendEmptyMessageDelayed(1,2000)

            }

        })

        mBtnClear.onClick {
            var mdialog = CircleDialog.Builder()
                    .setTitle("是否清楚本次配置!")
                    .setCanceledOnTouchOutside(true)
                    .setTextColor(resources.getColor(com.example.baselibrary.R.color.red_normal))
                    .setText("清除本次配置后，\n方可进行新设备的配置！")
                    .setPositive("确定") {
                        SpUtils.put(AppUtils.getContext(),ConstantSP.DEVICE_PWD,"")
                        SpUtils.put(AppUtils.getContext(),ConstantSP.ISSETDEFAULTPWDSUCCESS,false)
                        SpUtils.remove(ConstantSP.DEVICE_PWD)
                        SpUtils.remove(ConstantSP.ISSETDEFAULTPWDSUCCESS)
                        Logger.d("清除配置-pwd=${SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)} " +
                                " ISSETDEFAULTPWDSUCCESS=${ SpUtils.getBoolean(AppUtils.getContext(),ConstantSP.ISSETDEFAULTPWDSUCCESS)}")
                        finish()

                    }
                    .setNegative("取消",null)
                    .setCancelable(true)
                    .show(supportFragmentManager)
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        mPresenter.unregisterBroadcastReceiver()
        mPresenter.close()
    }
}