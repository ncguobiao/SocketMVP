package com.gb.socket1.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.Constant
import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.component.DaggerActivityComponent
import com.example.baselibrary.injection.module.ActivityMoudle
import com.example.baselibrary.injection.module.LifecycleProviderModule
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.ThreadPoolUtils
import com.example.baselibrary.zxing.app.CaptureActivity
import com.gb.socket1.R
import com.gb.socket1.injection.module.MainModule
import com.gb.sockt.blutoothcontrol.ble.key.BluetoothTestKeyImpl
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControl
import com.gb.sockt.blutoothcontrol.ble.test.BluetoothTest
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestKeyListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestListener
import com.gb.sockt.center.injection.component.DaggerMainComponent
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_bluetooth_key.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject
import javax.inject.Named


/**
 * Created by guobiao on 2018/11/15.
 * 测试蓝牙连接
 */


class BluetoothKeyActivity : BaseActivity() {

    private var startTime: Long = 0L

    private var sb: StringBuffer? = null
    private var mac: String? = null

    private var macAddress: String? = null
    private val rxPermissions by lazy {
        RxPermissions(this)
    }
    @Inject
    lateinit var mBluetoothTestKeyImpl: BluetoothTestKeyImpl

    private var resultLength: Int = 0
    private var deviceName: String? = null
    //
//    private val mBluetoothTestKeyImpl by lazy {
//        BluetoothTestKeyImpl(this)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_key)
        val mac = intent.getStringExtra("mac")
        if (mac.isNullOrEmpty()){
            toast("Mac错误")
            return
        }
        initComponent()

        initBluetooth(mac)

        mbtnConnect.onClick {
            if (!mBluetoothTestKeyImpl?.getConnectState())
                mTvMessage?.text = ""
            mTvReciver?.text = ""
            mProgressBar.visibility = View.VISIBLE
            mBluetoothTestKeyImpl?.connect()
            startTime = System.currentTimeMillis()
            Logger.d("startTime:$startTime")
        }

        mBtnDisconnect.onClick {
            if (mBluetoothTestKeyImpl?.getConnectState()) {
                mProgressBar.visibility = View.GONE
                mTvMessage?.text = ""
                mTvReciver?.text = ""
                sb = null
                sb = StringBuffer()
                mConsumTime.text = ""
                mBluetoothTestKeyImpl?.close()
            } else {
                toast("蓝牙已断开")
            }

        }
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val stringArray = resources.getStringArray(R.array.macs)
//                val mac = stringArray[position].toUpperCase()
////                val formatAddress = formatAddress(mac)
//                mEditText.setText(mac)
//                toast("已选择:${mac}")
//            }
//
//        }


        scan.onClick {
            if (!mBluetoothTestKeyImpl?.getConnectState()) {
                toast("蓝牙未连接")
                return@onClick
            }
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            rxPermissions
                    .request(Manifest.permission.CAMERA)
                    .subscribe {
                        if (it) {
//                //申请的权限全部允许
                            Logger.d("扫码权限通过")
                            startActivityForResult(
                                    Intent(BaseApplication.getApplication(), CaptureActivity::class.java),
                                    Constant.QRCODE
                            )
                        } else {
//                //只要有一个权限被拒绝，就会执行
                            requestPremissionSetting("相机")
                        }
                    }
        }
        key.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            if (!mBluetoothTestKeyImpl?.getConnectState()) {
               toast("蓝牙未连接")
                return@onClick
            }
            val  oneKeyInputArray= getOneKeyInputArray()
            Logger.e("oneKeyInputArray:${oneKeyInputArray}")
            mBluetoothTestKeyImpl?.sendKeyOne( oneKeyInputArray)
        }

    }

    private fun getOneKeyInputArray(): ArrayList<Byte>? {
        val text = mEditText1.text.toString().trim().toUpperCase()
        return getInputTextArray(text)
    }

    private fun getOneTwoInputArray(): ArrayList<Byte>? {
        val text = mEditText2.text.toString().trim().toUpperCase()
        return getInputTextArray(text)
    }

    private fun getInputTextArray(text:String): ArrayList<Byte>? {
        if (16 != text?.length) {
            toast("输入长度有误")
            return null
        }
        val toCharArray = text.toCharArray().toMutableList()
        val array = arrayListOf<Byte>()
        var sb = StringBuffer()
        toCharArray.forEachIndexed { index, c ->
            sb.append(c)
            if (index % 2 == 1) {
                array.add(sb.toString().toByte())
                sb = StringBuffer()
            }
        }
        return array
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.QRCODE ->//扫码逻辑
            {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data?.getStringExtra("SCAN_RESULT")
                    if (checkQRResult(result)) return
                    toast(macAddress ?: "错误mac")
                    if (!mBluetoothTestKeyImpl?.getConnectState()) {
                       toast("蓝牙未连接")
                        return
                    }
                    mBluetoothTestKeyImpl?.configMAC(macAddress)
                }

            }
        }
    }

    /**
     * 二维码检查
     */
    private fun checkQRResult(result: String?): Boolean {
        var result1 = result
        if (result1.isNullOrEmpty()) {
            toast(R.string.qrcode_error)
            return true
        }
        if (!result1!!.contains("?")) {
            if (checkMac(result1)) return true
        } else {
            val str = result1.split("\\?".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (str.size != 2) {
                toast(R.string.qrcode_error)
                return true
            }
            result1 = str[1]
            if (checkMac(result1)) return true
        }
        return false
    }


    //检查二维码
    private fun checkMac(result: String): Boolean {
        if (result.contains("-")) {
            val strings = result.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            resultLength = strings.size
            if (resultLength == 2) {
//                LogUtil.e("checkMac two param")
                macAddress = strings[0]
                deviceName = strings[1]
                if (checkMac(macAddress, deviceName)) return true
            } else if (resultLength == 3) {
                macAddress = strings[0]
                deviceName = strings[1]
                val way = strings[2]
                if (checkMac(macAddress, deviceName, way)) return true
            } else {
                toast(R.string.qrcode_error)
                return true
            }
            return false
        } else {
            toast(R.string.qrcode_error)
            return true
        }

    }

    private fun checkMac(macAddress: String?, deviceName: String?): Boolean {
        if (macAddress.isNullOrEmpty() || deviceName.isNullOrEmpty()) {
            toast(R.string.qrcode_error)
            return true
        }
        if (!macAddress!!.contains(":")) {
            toast(R.string.qrcode_error)
            return true
        }
        if (macAddress.length != 17) {
            toast(R.string.qrcode_error)
            return true
        }
        val split = this.macAddress?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        if (split?.size != 6) {
            toast(R.string.qrcode_error)
            return true
        }
        return false
    }

    private fun checkMac(macAddress: String?, deviceName: String?, way: String?): Boolean {
        if (macAddress.isNullOrEmpty() || deviceName.isNullOrEmpty() || way.isNullOrEmpty()) {
            toast(R.string.qrcode_error)
            return true
        }
        if (!macAddress!!.contains(":")) {
            toast(R.string.qrcode_error)
            return true
        }
        if (macAddress.length != 17) {
            toast(R.string.qrcode_error)
            return true
        }
        val split = this.macAddress?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        if (split?.size != 6) {
            toast(R.string.qrcode_error)
            return true
        }
        return false
    }

    private fun initBluetooth(mac: String) {
        if (mac.isNullOrEmpty()) {
            toast("MAC未设置")
            return
        }
        mBluetoothTestKeyImpl.setMAC(mac, object : BleConnectListener {
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
            }

            override fun connectOnSuccess() {
                mProgressBar.visibility = View.GONE
                val endTimeMillis = System.currentTimeMillis()
                //                Logger.d("endTimeMillis:$endTimeMillis")
                val consumTime = endTimeMillis - startTime
                //                Logger.d("consumTime:$consumTime")
                mConsumTime.text = "连接耗时：${consumTime}ms"
                mTvState.text = "连接成功"
                mTvState.setTextColor(Color.GREEN)
            }

        })

        mBluetoothTestKeyImpl.setResponseListener(object : BluetoothTestKeyListener {
            override fun onConfigMAC(data: String) {
                mTvReciver?.text = "配置MAC接收数据：${data}"
            }

            override fun onSendkeyOne(data: String) {
                mTvReciver?.text = "配置KEY接收数据：${data}"
                mBluetoothTestKeyImpl?.sendKeyTwo(getOneTwoInputArray())
            }

            override fun onSendkeyTwo(data: String) {
                mTvReciver?.text = "配置KEY接收数据：${data}"
            }

            override fun onError(data: String) {
                mTvMessage?.text = "错误数据$data"
            }

            override fun onWriteFailure(msg: String) {
                mTvMessage?.text = msg
            }

            override fun onWriteSuccess(msg: String) {
                mTvMessage?.text = msg
            }
        })

        mBluetoothTestKeyImpl.registerBroadcastReceiver()
    }

    private fun initComponent() {
        val activityComponent = DaggerActivityComponent.builder()
                .appComponent((application as BaseApplication).appComponent)
                .lifecycleProviderModule(LifecycleProviderModule(this))
                .activityMoudle(ActivityMoudle(this))
                .build()

        DaggerMainComponent.builder()
                .activityComponent(activityComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        mBluetoothTestKeyImpl?.unregisterBroadcastReceiver()
        mBluetoothTestKeyImpl?.close()
        sb = null
    }

}





