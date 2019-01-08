package com.gb.socket1.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.SpUtils
import com.gb.socket1.R
import com.gb.socket1.util.AesEntryDetry
import com.gb.sockt.blutoothcontrol.ble.cable.BluetoothTestCableImpl
import com.gb.sockt.blutoothcontrol.listener.BleCableListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_cable.*
import org.jetbrains.anko.toast

/**
 * Created by guobiao on 2019/1/3.
 */
class BleCableActivity : BaseActivity() {

    private val NEW_PWD = 1
    private val OLD_PWD = 2
    private var cllickType:Int = NEW_PWD

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
                        tvSend.text=""
                        tvRecive.text = ""
                        mPresenter.setCircle()
                        sendEmptyMessageDelayed(1, 3000)
                    }
                }
                2->{
                    if (!mPresenter.getConnectState()){
                        removeCallbacksAndMessages(null)
                        toast("请检验设备是否修改完成")
                        finish()

                        Logger.e("蓝牙断开，主动关闭当前页面")
                    }else{
                        tvSend.text=""
                        tvRecive.text = ""
                        mPresenter.setDeviceMac(password, mac)
                        sendEmptyMessageDelayed(2, 4000)
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
////        //获取新密码
//        val password = AesEntryDetry.getPassword("sensor668", mac?.replace(":", ""))
//        SpUtils.put(AppUtils.getContext(), ConstantSP.DEVICE_PWD, password)
//        SpUtils.put(AppUtils.getContext(),ConstantSP.ISSETDEFAULTPWDSUCCESS,true)
        initView()

        //password:9C4A816C3B02FF35-(F2:35:0A:00:00:55)
        //F2:35:0A:00:00:00  //F2:35:0A:00:00:55 //55:00:00:0A:35:F2
        //连接初始设备
        mPresenter.setMAC("F2:35:0A:00:00:00", object : BleConnectListener {
            override fun connectOnSuccess() {
                tvState.text = resources.getString(R.string.connected)
                    //心跳数据
                    mHandler.sendEmptyMessageDelayed(1, 3000)
                    toast("三秒后开始发送心跳包")

            }

            override fun connectOnFailure() {
                tvSend.text=""
                tvRecive.text = ""
                tvState.text = resources.getString(R.string.failure)
            }

            override fun connectOnError() {
                tvSend.text=""
                tvRecive.text = ""
                tvState.text = resources.getString(R.string.onError)
            }

        }).registerBroadcastReceiver()
        mPresenter.setResponseListener(object : BleCableListener {
            override fun onCircle() {
            }

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

            override fun openSuccess() {
            }

            override fun setPwdSuccess(pwd:String?) {
                when(cllickType){
                    NEW_PWD->{
                        //是否修改成功
                        SpUtils.put(AppUtils.getContext(),ConstantSP.ISSETDEFAULTPWDSUCCESS,true)
                        //存储新密码
                        val password = AesEntryDetry.getPassword("sensor668", mac?.replace(":", ""))
                        SpUtils.put(AppUtils.getContext(), ConstantSP.DEVICE_PWD, password)
                        tvMessage?.text = "设置密码成功：$pwd"
                    }
                    OLD_PWD->{
                        toast("恢复密码成功")
                        tvMessage.text = "恢复密码成功"
                    }
                }


            }

            override fun onError(byteArrayToHexString: String) {
                tvRecive.text ="接收数据=$byteArrayToHexString"

            }
        })
        //开启连接
        mPresenter.connect()

    }



    private fun initView() {
        mBtnSetMAC.onClick {
            if (mPresenter.getConnectState()){
                //修改密码成功
                val issetdefaultpwdsuccess = SpUtils.getBoolean(AppUtils.getContext(),ConstantSP.ISSETDEFAULTPWDSUCCESS)
                if (issetdefaultpwdsuccess){
                    //修改mac
                     password = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)
                    toast("正在设置新的MAC地址=$mac")
                    tvMessage?.text = "通过新密码修改MAC：$mac"
                    Logger.e("修改mac=$mac -- password=$password")
                    tvSend.text=""
                    tvRecive.text = ""
                    mPresenter.setDeviceMac(password, mac)
                    mHandler.sendEmptyMessageDelayed(2,2000)
                    SpUtils.put(AppUtils.getContext(), ConstantSP.DEVICE_MAC,mac)
                }else{
//                    password = "FFEECCDDAA998877"
//                    tvMessage?.text = "通过默认密码修改MAC：$mac"
                    toast("请先修改密码成功后，在修改MAC")
                }
            }else{
                toast("蓝牙版连接失败，无法修改密码")
            }

        }
        //修改密码
        mBtnSetPwd.onClick {
            cllickType =  NEW_PWD
           if (mPresenter.getConnectState()){
               val issetdefaultpwdsuccess = SpUtils.getBoolean(AppUtils.getContext(),ConstantSP.ISSETDEFAULTPWDSUCCESS)
               if (!issetdefaultpwdsuccess){
                   tvSend.text=""
                   tvRecive.text = ""
                   //加密mac
                   mac?.let {
                       if (it.contains(":")) {
                           val mac = it.replace(":", "")
                           //获取新密码
                           val password = AesEntryDetry.getPassword("sensor668", mac)
                           Logger.d("password:$password")
                           //DC, 0E, C1, E9, 36, B9, 5E, 89
                           //修改密码
                           mPresenter.setPWd("FFEECCDDAA998877",password)
                       }
                   }

               }else{
                   toast("密码已修改成功，无法再次修改")
               }

           }else{
               toast("蓝牙版连接失败，无法修改密码")
           }
        }

        mBtnSetDefaultPwd.onClick {
            if (mPresenter.getConnectState()){
                cllickType =  OLD_PWD
                val password = AesEntryDetry.getPassword("sensor668", mac?.replace(":", ""))
                mPresenter.setPWd(password,"FFEECCDDAA998877")
            }else{
                toast("蓝牙未连接")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()


        mHandler.removeCallbacksAndMessages(null)
        mPresenter.unregisterBroadcastReceiver()
        mPresenter.close()
    }
}