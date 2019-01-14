package com.gb.socket1.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.Constant
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.zxing.app.CaptureActivity
import com.gb.socket1.R
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestListener
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_bluetooth_test_connect.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast


/**
 * Created by guobiao on 2018/11/15.
 * 一键启动
 */


class BluetoothTestConnectActivity : BaseActivity() {

    private  var startTime: Long =0L

    private var sb: StringBuffer? = null
    private var mac: String? = null

    private var clickAction: Int = 0
    private val CLICK_ADD: Int = 1
    private val CLICK_LESS: Int = 2
    private val CLICK_ADD_DEDICATED: Int = 3
    private val CLICK_ADD_COMMON: Int = 4
    private val FIND_SINGLE_MAC: Int = 5
    private val SET_MAC: Int = 6
    private var resultLength: Int = 0

    private var macAddress: String = ""

    private var deviceName: String = ""


    private lateinit var mCodes: ByteArray

    private val mBluetoothTestImpl by lazy {
        com.gb.sockt.blutoothcontrol.ble.test.BluetoothTestImpl(this)
    }

    private var currentTime: Long = 0L
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                1 -> {
                    val time = SystemClock.elapsedRealtime() - currentTime
                    if (time > 30 *60* 1000.toLong()) {
                        toast("请求码失效，请重新获取")
                        finish()
                    } else
                        sendEmptyMessageDelayed(1, 30 * 1000)
                }
            }
        }
    }
    private val rxPermissions by lazy {
        RxPermissions(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_test_connect)
        mac = intent.getStringExtra("mac")

        initView()

        mBluetoothTestImpl.setMAC(mac, object : BleConnectListener {
            //                    mBluetoothTestImpl.setMAC("00:00:A1:00:00:81", object : BleConnectListener {
//        mBluetoothTestImpl.setMAC("00:00:CD:00:00:01", object : BleConnectListener {
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
                mConsumTime.text = "连接耗时：${System.currentTimeMillis()-startTime}ms"
                mTvState.text = "连接成功"
                mTvState.setTextColor(Color.GREEN)
                doAsync {
                    SystemClock.sleep(200)
                    mHandler.sendEmptyMessage(1)
                    //请求code
                    mBluetoothTestImpl.requestCode()
                }
            }

        })

        mBluetoothTestImpl.setResponseListener(object : BluetoothTestListener {
            override fun onError(codes: ByteArray?, data: String) {
                codes?.let {
                    mCodes = codes
                    showToast(data)
                    mTvReciver?.text = data
                    Logger.d("失败后请求码：mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
            }

            override fun onOperation() {
                //获取当前时间
                currentTime = SystemClock.elapsedRealtime()
            }

            override fun onGetCodeSuccess(codes: ByteArray?) {
                codes?.let {
                    mCodes = codes
                    Logger.d("获取请求码成功：mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
                mTvReciver?.text = "获取请求码成功：${BleUtils.byteArrayToHexString(codes)}"

            }

            override fun onDelete(codes: ByteArray?, data: String) {
                codes?.let {
                    mCodes = codes
                    toast("删除MAC+${macAddress}成功")
                    Logger.d("删除设备成功：mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
                mTvReciver?.text = "删除设备成功：${BleUtils.byteArrayToHexString(codes)}"
            }

            override fun onAddMAC_1(codes: ByteArray?) {
                codes?.let {
                    mCodes = codes
                    Logger.d("添加MAC第一帧成功 ,mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
                mTvReciver?.text = "添加MAC第一帧成功：${BleUtils.byteArrayToHexString(codes)}"
            }

            override fun onAddMAC_2(codes: ByteArray?) {
                codes?.let {
                    mCodes = codes
                    Logger.d("添加MAC第二帧成功 ,mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
                mTvReciver?.text = "添加MAC第二帧成功：${BleUtils.byteArrayToHexString(codes)}"
            }

            override fun onAddMAC_3(codes: ByteArray?) {
                codes?.let {
                    mCodes = codes
                    Logger.d("添加MAC第三帧成功：mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
                toast("添加MAC = ${macAddress}成功")
                mTvReciver?.text = "添加MAC第三帧成功：${BleUtils.byteArrayToHexString(codes)}"
            }

            override fun onOpenOrClose(codes: ByteArray?) {
                codes?.let {
                    mCodes = codes
                    toast("开启关闭设备成功")
                    Logger.d("开启关闭设备成功 mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
                mTvReciver?.text = "开启关闭设备成功：${BleUtils.byteArrayToHexString(codes)}"
            }
            override fun onFindALLMAC(result: String) {

            }

            override fun onFindSingleMAC(codes: ByteArray?, result: String) {
                codes?.let {
                    mCodes = codes
                    Logger.d("查询单个MAC成功 mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
                mTvReciver?.text ="${result}=${macAddress}"
            }

            override fun onResetDevice(result: String) {
                mTvReciver?.text = result
            }

            override fun onSetDeciveMAC(result: String) {
                mTvReciver?.text = result

            }

            override fun onFindAllMAC(count: Int, map: MutableList<Byte>) {
//                sb?.append("  \r\n第${count}帧，MAC=${BleUtils.byteArrayToHexString(map.toByteArray())}\r\n")
                mTvReciver?.text = "查询MAC数据：${BleUtils.byteArrayToHexString(map.toByteArray())}"
            }

            override fun onResetDeviceSuccess(codes: ByteArray?, result: String) {
                codes?.let {
                    mCodes = codes
                    Logger.d("设备复位成功 mCodes = ${BleUtils.byteArrayToHexString(codes)}")
                }
                mTvReciver?.text =result
            }
            override fun onCloseDeviceBleSuccess(result: String) {
                mTvReciver?.text =result
            }
            override fun onWriteFailure(msg: String) {
                mTvMessage?.text = msg
            }

            override fun onWriteSuccess(msg: String) {
                mTvMessage?.text = msg
            }

            override fun onError(data: String) {
                showToast(data)
                mTvReciver?.text = data
            }
//           override fun onFindAllMAC(map: MutableMap<Int, MutableList<Byte>>) {
//
//                Logger.e("===========")
//                map.entries.forEach {
//                    sb?.append("MAC=${BleUtils.byteArrayToHexString(it.component2().toByteArray())}\r\n")
//                    Logger.e("当前帧=${it.component1()} 值=${it.component2()}")
//                }
//                mTvReciver?.text = "查询MAC数据：${sb.toString()}"
//            }

        })

        mBluetoothTestImpl.registerBroadcastReceiver()

        mBluetoothTestImpl.connect()
        startTime = System.currentTimeMillis()
        currentTime = SystemClock.elapsedRealtime()
        mTvState.text = "正在连接中..."
    }

    private fun initView() {

        mbtnConnect.onClick {
            if (!mBluetoothTestImpl.getConnectState())
                mTvMessage?.text = ""
            mTvReciver?.text = ""
            mProgressBar.visibility = View.VISIBLE
            mBluetoothTestImpl?.connect()
            startTime = System.currentTimeMillis()
            Logger.d("startTime:$startTime")
        }

        mBtnDisconnect.onClick {
            if (mBluetoothTestImpl?.getConnectState()) {
                mProgressBar.visibility = View.GONE
                mTvMessage?.text = ""
                mTvReciver?.text = ""
                sb = null
                sb = StringBuffer()
                mConsumTime.text = ""
                mBluetoothTestImpl?.close()
            } else {
                toast("蓝牙已断开")
            }

        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val stringArray = resources.getStringArray(R.array.macs)
                val mac = stringArray[position].toUpperCase()
//                val formatAddress = formatAddress(mac)
                mEditText.setText(mac)
                toast("已选择:${mac}")
            }

        }

        //获取操作码
        mBtnGetCode.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            //operationData:0-关，1-开
            mBluetoothTestImpl.requestCode()
        }
        //恢复出厂设置
        mBtnResetDevice.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            //operationData:0-关，1-开
            mBluetoothTestImpl.resetDevice(mac)
        }
        //MAC添加专用 0
        mBtnAddDedicatedMAC.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            clickAction = CLICK_ADD_DEDICATED
            if (checkCameraPermission()) return@onClick
        }
        //MAC添加通用 1
        mBtnAddCommonMAC.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            clickAction = CLICK_ADD_COMMON
            if (checkCameraPermission()) return@onClick
        }
        //删除MAC
        mBtnLess.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            clickAction = CLICK_LESS
            if (checkCameraPermission()) return@onClick
        }
        //查询所有MAC
        mBtnFindAll.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            mBluetoothTestImpl.findAllMAC()
        }
        //查询单个MAC
        mBtnFindSingleMAC.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            clickAction = FIND_SINGLE_MAC
            if (checkCameraPermission()) return@onClick
        }
        //修改单个MAC
        mBtnSetMAC.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            clickAction = SET_MAC
            if (checkCameraPermission()) return@onClick
        }
        //开设备
        mBtnOpenOperation.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            //operationData:0-关，1-开
            mBluetoothTestImpl.openOrClose(mCodes,1)
        }
        //关设备
        mBtnCloseOperation.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            //operationData:0-关，1-开
            mBluetoothTestImpl.openOrClose(mCodes,0)
        }
        //复位
        mBtnReset.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            //operationData:0-关，1-开
            mBluetoothTestImpl.resetDevice(mCodes)
        }

        //关闭设备蓝牙
        mBtnCLoseDevice.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            //operationData:0-关，1-开
            mBluetoothTestImpl.cLoseDeviceBle()
        }

        mEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text = mEditText.text.toString().trim().toUpperCase()
                checkMacAddress(text)

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constant.QRCODE ->//扫码逻辑
            {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data?.getStringExtra("SCAN_RESULT")
                    if (checkQRResult(result)) return
                    toast(macAddress ?: "错误mac")
                    if (!mBluetoothTestImpl?.getConnectState()) {
                        toast("蓝牙未连接")
                        return
                    }
                    when(clickAction){
                        CLICK_ADD_COMMON->{ //1
                            mBluetoothTestImpl?.sendAddMAC_1(mCodes,macAddress,1)
                        }
                        CLICK_ADD_DEDICATED->{//0
                            mBluetoothTestImpl?.sendAddMAC_1(mCodes,macAddress,0)
                        }
                        FIND_SINGLE_MAC->{
                            mBluetoothTestImpl.findSingleMAC(mCodes,macAddress)
                        }
                        SET_MAC->{
                            mBluetoothTestImpl.setDeviceMAC(macAddress)
                        }
                        else->{
                            mBluetoothTestImpl?.sendDeleteMAC(mCodes,macAddress)
                        }
                    }

                }
            }
        }
    }

    /**
     * 申请摄像头权限
     */
    private fun checkCameraPermission(): Boolean {
        if (!mBluetoothTestImpl?.getConnectState()) {
            toast("蓝牙未连接")
            return true
        }
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
        return false
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

    fun checkMacAddress(text: String) {
        val charArray = text.toCharArray()
        val size = charArray.size
        if (size == 12) {
            sb = StringBuffer()
            charArray.forEachIndexed { index, c ->
                sb?.append(c)
                if (index % 2 == 1 && index < size - 1) {
                    sb?.append(":")
                }
            }
            mTvMessage?.text = "输入MAC地址-${sb.toString()}"
            mDeviceName?.text = "连接设备MAC-${mac}"
            Logger.i(sb.toString())
            mBluetoothTestImpl?.setMACAddress(sb.toString())
        } else {
            mTvMessage?.text = "输入MAC地址有误"
        }
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

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        mBluetoothTestImpl?.unregisterBroadcastReceiver()
        mBluetoothTestImpl?.close()
        sb = null
    }

}





