//package com.gb.sockt.blutoothcontrol.uitls
//
//import android.app.Activity
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import com.example.baselibrary.common.Constant
//import com.example.baselibrary.common.ConstantSP
//import com.example.baselibrary.utils.BleUtils
//import com.example.baselibrary.utils.BluetoothClientManager
//import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
//import com.gb.sockt.blutoothcontrol.listener.BleDataChangeListener
//import com.inuker.bluetooth.library.BluetoothClient
//import com.inuker.bluetooth.library.Constants
//import com.inuker.bluetooth.library.Constants.*
//import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
//import com.inuker.bluetooth.library.connect.options.BleConnectOptions
//import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
//import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
//import com.inuker.bluetooth.library.connect.response.BleWriteResponse
//import com.inuker.bluetooth.library.utils.BluetoothUtils
//import com.inuker.bluetooth.library.utils.BluetoothUtils.registerReceiver
//import com.orhanobut.logger.Logger
//import java.lang.ref.WeakReference
//import java.util.*
//
///**
// * Created by guobiao on 2018/8/9.
// */
// open class BlueToothSingeControlImpl constructor(deviceTag: String, context: Context?) : BaseBLEControl {
//
//
//    private var mcontext: WeakReference<Context>? = null
//
//    private var mClient: BluetoothClient? = null
//
//    private var mBleConnectListener: BleConnectListener? = null
//    private var EQUIP_TYPE: Byte = 0
//    private lateinit var msg: String
//    private var macAddress: String? = null
//    private var mBleDataChangeListener: BleDataChangeListener?=null
//    private var mConnected: Boolean = false
//    private var mConnectStatusListener: BleConnectStatusListener
//    private var filter: IntentFilter? = null
//    override fun setMAC(mac: String?, bleConnectListener: BleConnectListener?): BlueToothControl {
//        this.macAddress = mac
//        this.mBleConnectListener = bleConnectListener
//        if (mClient != null) {
//            mClient?.registerConnectStatusListener(macAddress, mConnectStatusListener)
//        } else {
//            mBleConnectListener?.connectOnError()
//        }
//        return this
//    }
//
//    override fun setResponeListener(bleDataChangeListener: BleDataChangeListener?):BlueToothControl {
//
//        this.mBleDataChangeListener = bleDataChangeListener
//        return this
//    }
//
////    fun setMacAddress(macAddress: String?): BlueToothCEControlImpl {
////        this.macAddress = macAddress
////        return this
////    }
////
////    fun builder(): BlueToothCEControlImpl {
////        return this
////    }
//
//    companion object {
//        val serviceUUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
//        val characteristicUUID1 = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
//        val characteristicUUID2 = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb")
//        val options = BleConnectOptions.Builder()
//                .setConnectRetry(3)
//                .setConnectTimeout(20000)
//                .setServiceDiscoverRetry(3)
//                .setServiceDiscoverTimeout(10000)
//                .build()
//    }
//
//
//    init {
//        when (deviceTag) {
//            Constant.DEVICE_CE ->
//                EQUIP_TYPE = Integer.parseInt("D1", 16).toByte()
//            Constant.DEVICE_CD ->
//                EQUIP_TYPE = Integer.parseInt("B1", 16).toByte()
//            else -> Logger.d("单路设备")
//        }
//        //获取蓝牙对象
//        mClient = BluetoothClientManager.getClient()
//        //连接状态变化监听
//        mConnectStatusListener = object : BleConnectStatusListener() {
//            override fun onConnectStatusChanged(mac: String?, status: Int) {
//                when (status) {
//                    Constants.STATUS_CONNECTED -> {
//                        mConnected = true
//                        mClient?.let {
//                            it.notify(mac, serviceUUID, characteristicUUID2, bleNotifyResponse)
//                        }
//                        mBleConnectListener?.let {
//                            it.connectOnSuccess()
//                        }
//
//                    }
//                    Constants.STATUS_DISCONNECTED -> {
//                        mConnected = false
//                        mClient?.let {
//                            it.unnotify(mac, serviceUUID, characteristicUUID2, bleUnNotifyResponse)
//                        }
//                        mBleConnectListener?.let {
//                            it.connectOnFailure()
//                        }
//                    }
//                }
//
//            }
//
//        }
//    }
//
//    /**
//     * 注销广播
//     */
//    override fun unregisterBoradcastRecvier() {
//        filter = null
//        mcontext?.get()?.unregisterReceiver(mReceiver)
//    }
//
//    /**
//     * 注册广播
//     */
//    override fun registerBoradcastRecvier(context: Context?): BlueToothControl {
//        filter = IntentFilter()
//        filter?.addAction(ACTION_CHARACTER_CHANGED)
//        if (context != null) this.mcontext = WeakReference(context!!)
////        BluetoothUtils.registerReceiver(mReceiver, filter)
//        mcontext?.get()?.registerReceiver(mReceiver, filter)
//        return this
//    }
//
//
//    private val mReceiver = object : BroadcastReceiver() {
//        override fun onReceive(c: Context?, intent: Intent) {
//            when (intent.action) {
//                ACTION_CHARACTER_CHANGED -> {
//                    val receiveValue = intent.getByteArrayExtra(EXTRA_BYTE_VALUE)
//                    Logger.w("接收蓝牙数据=${BleUtils.byteArrayToHexString(receiveValue)}")
//                }
//            }
//        }
//
//    }
//
//    //开启通知回调
//    private val bleNotifyResponse = object : BleNotifyResponse {
//        override fun onResponse(code: Int) {
//            if (code == REQUEST_SUCCESS) {
//                Logger.e("开启通知成功")
//            } else {
//                Logger.e("开启通知失败")
//            }
//        }
//
//        override fun onNotify(service: UUID, character: UUID, value: ByteArray?) {
//            if (characteristicUUID1 === character) {
//                if (value != null) {
//                    val s = BleUtils.byteArrayToHexString(value)
//                    Logger.e("接收数据：$s")
//                }
//            }
//        }
//    }
//
//
//    //关闭通知回调
//    private val bleUnNotifyResponse = BleUnnotifyResponse { code ->
//        if (code == Constants.REQUEST_SUCCESS) {
//            Logger.e("关闭通知成功！")
//        }
//    }
//
//
//    private fun getMAC(): String {
//        if (null == macAddress) {
//            throw ExceptionInInitializerError("macAddress is not set")
//        }
//        if (macAddress.isNullOrEmpty()) {
//            throw ExceptionInInitializerError("macAddress is not set")
//        }
//        if (!macAddress!!.contains(":")) {
//            throw IllegalArgumentException("is not macAddress ")
//        }
//        return macAddress!!
//    }
//
//    /**
//     * 连接
//     */
//    override fun connect() {
//        connectDeviceIfNeeded()
//    }
//
//    override fun setConnectListener(bleConnectListener: BleConnectListener) {
//        this.mBleConnectListener = bleConnectListener
//    }
//
//    //校验密码
//    override fun sendAndCheckSeed(keys: ByteArray) {
//        val b0 = Integer.parseInt("27", 16).toByte()
//        val b1 = Integer.parseInt("02", 16).toByte()
//        val b3 = Integer.parseInt("04", 16).toByte()
//        val b4 = keys[0]
//        val b5 = keys[1]
//        val b6 = keys[2]
//        val b7 = keys[3]
//        val b8 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5, b6, b7))
//        val value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5, b6, b7, b8)
//        msg = "发送加密种子"
//        write(value)
//    }
//
//    /**
//     * 请求种子
//     */
//    override fun requestSeed() {
//        val b0 = Integer.parseInt("27", 16).toByte()
//        val b1 = Integer.parseInt("01", 16).toByte()
//        val b3 = Integer.parseInt("00", 16).toByte()
//        val b4 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3))
//        val value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4)
//        msg = "发送请求种子数据"
//        write(value)
//    }
//
//
//    /**
//     * 连接设备
//     */
//    fun connectDevice() {
//        mClient?.let {
//            mClient!!.connect(getMAC(), options) { code, data ->
//                when (code) {
//                //连接成功
//                    REQUEST_SUCCESS -> {
//                        mConnected = true
//                       Logger.e("发起连接成功")
//                    }
//                //连接失败
//                    REQUEST_FAILED -> {
//                        mConnected = false
//                        Logger.e("发起连接失败")
//                    }
//                    BLE_NOT_SUPPORTED -> {
//                        mConnected = false
//                        Logger.e("不支持蓝牙")
//                    }
//
//                }
//            }
//        }
//    }
//
//
//    //是否需要连接设备
//    private fun connectDeviceIfNeeded() {
//        if (!mConnected) {
//            connectDevice()
//        }
//    }
//
//    //蓝牙数据写入方法
//    private fun write(value: ByteArray) {
//        if (mClient != null) {
//            val writeData = BleUtils.byteArrayToHexString(value)
//            mClient?.write(getMAC(), serviceUUID, characteristicUUID1, value, BleWriteResponse { code ->
//                if (code == REQUEST_SUCCESS) {
//
//                    Logger.w("${msg}:${writeData}==成功")
//                } else {
//                    Logger.w("${msg}:${writeData}--失败")
//                }
//            })
//        }
//    }
//
//
//}
//
//
