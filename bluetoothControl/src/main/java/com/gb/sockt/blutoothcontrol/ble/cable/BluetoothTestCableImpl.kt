package com.gb.sockt.blutoothcontrol.ble.cable

import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.BluetoothClientManager
import com.example.baselibrary.utils.SpUtils
import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.ble.BluetoothConfig
import com.gb.sockt.blutoothcontrol.listener.BleCableListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestCarListener
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.orhanobut.logger.Logger
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.xor


/**
 * Created by guobiao on 2018/11/15.
 */
class BluetoothTestCableImpl constructor(val context: Context?) : BluetoothTestCable {

    companion object {
        val WRITE_UUID: UUID = java.util.UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
        val NOTIFY_UUID: UUID = java.util.UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
        val UUID: UUID = java.util.UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    }

    private var mcontext: WeakReference<Context>? = null

    private var mClient: BluetoothClient? = null

    private var mBleConnectListener: BleConnectListener? = null
    private lateinit var msg: String
    private var macAddress: String? = null
    private var password: String? = null
    private var mBleCableListener: BleCableListener? = null
    private var mConnected: Boolean = false
    private lateinit var mConnectStatusListener: BleConnectStatusListener
    private var filter: IntentFilter? = null


    init {

        //获取蓝牙对象
        mClient = BluetoothClientManager.getClient()
        //连接状态变化监听
        mConnectStatusListener = object : BleConnectStatusListener() {
            override fun onConnectStatusChanged(mac: String?, status: Int) {
                when (status) {
                    Constants.STATUS_CONNECTED -> {
                        mConnected = true
                        mClient?.let {
                            it.notify(mac, UUID, NOTIFY_UUID, bleNotifyResponse)
                        }
                        mBleConnectListener?.let {
                            it.connectOnSuccess()
                        }

                    }
                    Constants.STATUS_DISCONNECTED -> {
                        mConnected = false
                        mClient?.let {
                            it.unnotify(mac, UUID, NOTIFY_UUID, bleUnNotifyResponse)
                        }
                        mBleConnectListener?.let {
                            it.connectOnFailure()
                        }
                    }
                }

            }

        }
    }

    /**
     * 心跳数据
     * 第一层加密：55，E9，CC，DD
     * 第二层：FF,70,44,AA
     *  0xAA070000
     * //加密
     *  第一个字节      xor   密码字节1 = 第一层加密数据1
    第二个字节      xor   密码字节2 = 第一层加密数据2
    第三个字节      xor   密码字节3 = 第一层加密数据3
    第四个字节      xor   密码字节4 = 第一层加密数据4
    第一层加密数据1 xor  密码字节5 = 第二层加密数据1
    第一层加密数据2 xor  密码字节6 = 第二层加密数据2
    第一层加密数据3 xor  密码字节7 = 第二层加密数据3
    第一层加密数据4 xor  密码字节8 = 第二层加密数据4
     */
    fun setCircle(password: String?) {
        if (password.isNullOrEmpty()) {
            context?.toast("加密MAC错误")
            return
        }
        val b1 = Integer.parseInt("AA", 16).toByte()
        val b2 = Integer.parseInt("07", 16).toByte()
        val b3 = Integer.parseInt("00", 16).toByte()
        val b4 = Integer.parseInt("00", 16).toByte()

        var sb = StringBuffer()
        password?.let {
            it.toCharArray().forEachIndexed { index, value ->
                sb.append(value)
                if (index % 2 == 1 && index < password.length - 1) {
                    sb.append("-")
                }
            }
            val list = sb.toString().split("-")
            val b5 = Integer.parseInt(list[0], 16).toByte()
            val b6 = Integer.parseInt(list[1], 16).toByte()
            val b7 = Integer.parseInt(list[2], 16).toByte()
            val b8 = Integer.parseInt(list[3], 16).toByte()

            val b9 = Integer.parseInt(list[4], 16).toByte()
            val b10 = Integer.parseInt(list[5], 16).toByte()
            val b11 = Integer.parseInt(list[6], 16).toByte()
            val b12 = Integer.parseInt(list[7], 16).toByte()
            val value1 = b1 xor b5 xor b9
            val value2 = b2 xor b6 xor b10
            val value3 = b3 xor b7 xor b11
            val value4 = b4 xor b8 xor b12
            val value = byteArrayOf(value1, value2, value3, value4)
            msg = "充电线心跳数据"
            write(value)
        }

    }

    /**
     *  4.修改密码命令数据格式:0xaabbccddeeffgghh+0xAC05+MMyyzznnXXYYZZNN
    电子开关收到修改密码命令命令后，返回0xAA050000表示修改成功 。
    0xaabbccddeeffgghh：ffeeccddaa998877 旧密码
    0xMMyyzznnXXYYZZNN: 9C4A816C3B02FF35 新密码
     */
    fun setPWd(password: String?) {
        this.password = password
        if (password.isNullOrEmpty()) {
            context?.toast("加密MAC错误")
            return
        }
        val b0 = Integer.parseInt("FF", 16).toByte()
        val b1 = Integer.parseInt("EE", 16).toByte()
        val b2 = Integer.parseInt("CC", 16).toByte()
        val b3 = Integer.parseInt("DD", 16).toByte()
        val b4 = Integer.parseInt("AA", 16).toByte()
        val b5 = Integer.parseInt("99", 16).toByte()
        val b6 = Integer.parseInt("88", 16).toByte()
        val b7 = Integer.parseInt("77", 16).toByte()

        val b8 = Integer.parseInt("AC", 16).toByte()
        val b9 = Integer.parseInt("05", 16).toByte()

        password?.let {
            var sb = StringBuffer()
            it.toCharArray().forEachIndexed { index, value ->
                sb.append(value)
                if (index % 2 == 1 && index < password.length - 1) {
                    sb.append("-")
                }
            }
            val list = sb.toString().split("-")

            val b10 = Integer.parseInt(list[0], 16).toByte()
            val b11 = Integer.parseInt(list[1], 16).toByte()
            val b12 = Integer.parseInt(list[2], 16).toByte()
            val b13 = Integer.parseInt(list[3], 16).toByte()
            val b14 = Integer.parseInt(list[4], 16).toByte()
            val b15 = Integer.parseInt(list[5], 16).toByte()
            val b16 = Integer.parseInt(list[6], 16).toByte()
            val b17 = Integer.parseInt(list[7], 16).toByte()
            val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17)
            msg = "充电线修改密码"
            write(value)
        }

    }

    /**
     * 修改mac
     * 修改MAC命令数据格式:0xaabbccddeeffgghh+AC06+MAC地址
    电子开关收到修改MAC命令之后，设备复位不会返回数据。
    Aa：密码第一个字节
    Bb：
    Cc：
    Dd：
    Ee：
    Ff：
    Gg：
    Hh：密码第八个字节
    手机收到0xFF返回值后表示命令错误或者密码不对。
     */
    fun setDeviceMac(password: String?, mac: String?) {
        if (password.isNullOrEmpty()) {
            context?.toast("加密MAC错误")
            return
        }
        password?.let {
            var sb = StringBuffer()
            it.toCharArray().forEachIndexed { index, value ->
                sb.append(value)
                if (index % 2 == 1 && index < password.length - 1) {
                    sb.append("-")
                }
            }
            val list = sb.toString().split("-")

            val b0 = Integer.parseInt(list[0], 16).toByte()
            val b1 = Integer.parseInt(list[1], 16).toByte()
            val b2 = Integer.parseInt(list[2], 16).toByte()
            val b3 = Integer.parseInt(list[3], 16).toByte()
            val b4 = Integer.parseInt(list[4], 16).toByte()
            val b5 = Integer.parseInt(list[5], 16).toByte()
            val b6 = Integer.parseInt(list[6], 16).toByte()
            val b7 = Integer.parseInt(list[7], 16).toByte()

            val b8 = Integer.parseInt("AC", 16).toByte()
            val b9 = Integer.parseInt("06", 16).toByte()
            mac?.let {
                if (mac.contains(":")) {
                    var listMac = mac.split(":")
                    //反转mac
                    listMac = listMac.reversed()

                    val b10 = Integer.parseInt(listMac[0], 16).toByte()
                    val b11 = Integer.parseInt(listMac[1], 16).toByte()
                    val b12 = Integer.parseInt(listMac[2], 16).toByte()
                    val b13 = Integer.parseInt(listMac[3], 16).toByte()
                    val b14 = Integer.parseInt(listMac[4], 16).toByte()
                    val b15 = Integer.parseInt(listMac[5], 16).toByte()
                    val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15)
                    msg = "充电线修改Mac=$mac"
                    write(value)
                }
            }
        }

    }

    /**
     * 开指令
     */
    fun openDevice(integerValue: Int) {
        val b0 = Integer.parseInt("AA", 16).toByte()
        val b1 = Integer.parseInt("02", 16).toByte()
        // 时间
        val b2 = (integerValue shr 8).toByte()
        val b3 = integerValue.toByte()

        val password = SpUtils.getString(AppUtils.getContext(), ConstantSP.DEVICE_PWD)
        if (password.isNullOrEmpty()) {
            context?.toast("加密MAC错误")
            return
        }
        password?.let {
            var sb = StringBuffer()
            it.toCharArray().forEachIndexed { index, value ->
                sb.append(value)
                if (index % 2 == 1 && index < password.length - 1) {
                    sb.append("-")
                }
            }
            val list = sb.toString().split("-")

            val b10 = Integer.parseInt(list[0], 16).toByte()
            val b11 = Integer.parseInt(list[1], 16).toByte()
            val b12 = Integer.parseInt(list[2], 16).toByte()
            val b13 = Integer.parseInt(list[3], 16).toByte()
            val b14 = Integer.parseInt(list[4], 16).toByte()
            val b15 = Integer.parseInt(list[5], 16).toByte()
            val b16 = Integer.parseInt(list[6], 16).toByte()
            val b17 = Integer.parseInt(list[7], 16).toByte()

            val value1 = b0 xor b10 xor b14
            val value2 = b1 xor b11 xor b15
            val value3 = b2 xor b12 xor b16
            val value4 = b3 xor b13 xor b17
            val value = byteArrayOf(value1, value2, value3, value4)
            msg = "充电线开启=${integerValue}分钟"
            write(value)
        }
    }

    /**
     * 关闭
     */
    fun closeDevice(integerValue: Int) {
        val b0 = Integer.parseInt("AA", 16).toByte()
        val b1 = Integer.parseInt("02", 16).toByte()


        val b2 = (integerValue shr 8).toByte()// 时间
        val b3 = integerValue.toByte()//时间
        val value = byteArrayOf(b0, b1, b2, b3)
        msg = "充电线关闭"
        write(value)

    }

    //蓝牙数据写入方法
    private fun write(value: ByteArray) {
        if (mClient != null) {
            val writeData = BleUtils.byteArrayToHexString(value)
            mClient?.write(getMAC(), UUID, WRITE_UUID, value, BleWriteResponse { code ->
                if (code == Constants.REQUEST_SUCCESS) {
                    mBleCableListener?.onWriteSuccess("${msg}=${writeData}==成功")
                    Logger.w("${msg}=${writeData}==成功")
                } else {
                    mBleCableListener?.onWriteFailure("${msg}:${writeData}==失败")
                    Logger.w("${msg}:${writeData}--失败")
                }
            })
        }
    }


    //开启通知回调
    private val bleNotifyResponse = object : BleNotifyResponse {
        override fun onResponse(code: Int) {
            if (code == Constants.REQUEST_SUCCESS) {
                Logger.e("开启通知成功")
            } else {
//                Logger.e("开启通知失败")
            }
        }

        override fun onNotify(service: UUID, character: UUID, value: ByteArray?) {
            if (NOTIFY_UUID === character) {
                if (value != null) {
                    val s = BleUtils.byteArrayToHexString(value)
                    Logger.w("接收数据：$s")
                }
            }
        }
    }


    //关闭通知回调
    private val bleUnNotifyResponse = BleUnnotifyResponse { code ->
        if (code == Constants.REQUEST_SUCCESS) {
            Logger.e("关闭通知成功！")
        }
    }
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_CHARACTER_CHANGED -> {
                    val receiveValue = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE)
                    receiveValue?.let {
                        Logger.w("接收蓝牙数据=${BleUtils.byteArrayToHexString(receiveValue)}")
                        when {
                            "0xAA050000" == BleUtils.byteToHexString(it) -> {
                                Logger.d("修改密码成功")
                                SpUtils.put(AppUtils.getContext(), ConstantSP.DEVICE_PWD, password)
                                mBleCableListener?.setPwdSuccess(password)
                            }
                            BleUtils.byteToHexString(it).startsWith("0xAA02")->{
                                Logger.d("开启成功")
                                mBleCableListener?.openSuccess()
                            }
                            else -> {
                                mBleCableListener?.onError()
                            }
                        }

                    }
                }
            }
        }

    }

    protected fun getMathCount(b3: Byte, b4: Byte): Int {
        var electricity1 = Integer.toHexString(b3.toInt() and 0XFF)
        var electricity2 = Integer.toHexString(b4.toInt() and 0XFF)
        if (electricity1.length == 1) {
            electricity1 = "0$electricity1"
        }
        if (electricity2.length == 1) {
            electricity2 = "0$electricity2"
        }

//        return Integer.parseInt(
//                electricity1 + electricity2, 16)
        return (electricity1 + electricity2).toInt(16)

    }

    override fun setMAC(mac: String?, bleConnectListener: BleConnectListener?): BaseBLEControl {
        if (mac.isNullOrEmpty()) {
            throw IllegalArgumentException("MAC地址异常")
        }
        this.macAddress = mac
        val macBytes = BleUtils.getByteArrAddress(macAddress)
        this.mBleConnectListener = bleConnectListener
        if (mClient != null) {
            mClient?.registerConnectStatusListener(macAddress, mConnectStatusListener)
        } else {
            mBleConnectListener?.connectOnError()
        }
        return this
    }


    override fun connect() {
        connectDeviceIfNeeded()
    }

    //是否需要连接设备
    private fun connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice()
        }
    }

    /**
     * 连接设备
     */
    private fun connectDevice() {
        mClient?.let {
            Logger.e("连接MAC=${getMAC()}")
            mClient!!.connect(getMAC(), BluetoothConfig.options) { code, data ->
                when (code) {
                //连接成功
                    Constants.REQUEST_SUCCESS -> {
                        mConnected = true
//                        Logger.d("发起连接成功")
                    }
                //连接失败
                    Constants.REQUEST_FAILED -> {
                        mConnected = false
                        Logger.e("发起连接失败")
                    }
                    Constants.BLE_NOT_SUPPORTED -> {
                        mConnected = false
                        Logger.e("不支持蓝牙")
                    }

                }
            }
        }
    }

    private fun getMAC(): String {
        if (null == macAddress) {
            throw ExceptionInInitializerError("macAddress is not set")
        }
        if (macAddress.isNullOrEmpty()) {
            throw ExceptionInInitializerError("macAddress is not set")
        }
        if (!macAddress!!.contains(":")) {
            throw ExceptionInInitializerError("is not macAddress ")
        }
        return macAddress!!
    }

    override fun registerBroadcastReceiver(): BaseBLEControl {
        filter = IntentFilter()
        filter?.addAction(Constants.ACTION_CHARACTER_CHANGED)
        if (context != null) this.mcontext = WeakReference(context!!)
//        BluetoothUtils.registerReceiver(mReceiver, filter)
        mcontext?.get()?.registerReceiver(mReceiver, filter)
        return this
    }

    override fun unregisterBroadcastReceiver() {
        filter = null
        mcontext?.get()?.unregisterReceiver(mReceiver)
    }

    override fun setConnectListener(bleConnectListener: BleConnectListener) {
        this.mBleConnectListener = bleConnectListener
    }


    override fun setResponseListener(mBleCableListener: BleCableListener?) {
        this.mBleCableListener = mBleCableListener

    }


    override fun close() {
        Logger.d("APP主动断开蓝牙")
//        BluetoothGatt
////        val bluetoothGatt:Class<BluetoothGatt> = Class.forName("android.bluetooth.BluetoothGatt") as Class<BluetoothGatt>
//        val bluetoothGatt  = BluetoothGatt::class.java
//        Logger.d("bluetoothGatt=$bluetoothGatt")
//        val newInstance = bluetoothGatt?.newInstance() as BluetoothGatt
//        Logger.d("newInstance=$newInstance")
//        val disconnect = bluetoothGatt?.getDeclaredMethod("disconnect")
//        Logger.d("disconnect=$disconnect")
//        disconnect?.isAccessible=true
//        disconnect?.invoke(newInstance)
        mClient?.let {
            it.disconnect(macAddress)
//            it.closeBluetooth()
        }

    }

    fun open() {
        mClient?.let {
            if (!it.isBluetoothOpened) it.openBluetooth()

        }
    }

    override fun deviceIfNeeded() {
    }

    override fun getConnectState(): Boolean {
        return mConnected
    }

    fun setMACAddress(mac: String) {
        this.macAddress = mac
    }

    fun clearRequest() {
        mClient?.clearRequest(macAddress, 0)
    }



}