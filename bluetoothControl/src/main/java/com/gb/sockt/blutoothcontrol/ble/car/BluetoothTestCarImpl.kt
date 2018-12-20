package com.gb.sockt.blutoothcontrol.ble.car

import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.BluetoothClientManager
import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.ble.BluetoothConfig
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestCarListener
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.orhanobut.logger.Logger
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by guobiao on 2018/11/15.
 */
class BluetoothTestCarImpl constructor(val context: Context?) : BluetoothTestCar {


    private var mcontext: WeakReference<Context>? = null

    private var mClient: BluetoothClient? = null

    private var mBleConnectListener: BleConnectListener? = null
    private lateinit var msg: String
    private var macAddress: String? = null
    private var mBluetoothTestCarListener: BluetoothTestCarListener? = null
    private var mConnected: Boolean = false
    private lateinit var mConnectStatusListener: BleConnectStatusListener
    private var filter: IntentFilter? = null

    private val deviceType by lazy {
        Integer.parseInt("A2", 16).toByte()
    }

    init {
//        when (deviceTag) {
//            Constant.DEVICE_CE ->
//                EQUIP_TYPE = Integer.parseInt("D1", 16).toByte()
//            Constant.DEVICE_CD ->
//                EQUIP_TYPE = Integer.parseInt("B1", 16).toByte()
//            else -> Logger.d("单路设备")
//        }

        //获取蓝牙对象
        mClient = BluetoothClientManager.getClient()
        //连接状态变化监听
        mConnectStatusListener = object : BleConnectStatusListener() {
            override fun onConnectStatusChanged(mac: String?, status: Int) {
                when (status) {
                    Constants.STATUS_CONNECTED -> {
                        mConnected = true
                        mClient?.let {
                            it.notify(mac, BluetoothConfig.serviceUUID, BluetoothConfig.characteristicUUID2, bleNotifyResponse)
                        }
                        mBleConnectListener?.let {
                            it.connectOnSuccess()
                        }

                    }
                    Constants.STATUS_DISCONNECTED -> {
                        mConnected = false
                        mClient?.let {
                            it.unnotify(mac, BluetoothConfig.serviceUUID, BluetoothConfig.characteristicUUID2, bleUnNotifyResponse)
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
     * 请求种子
     */
    fun requestSeed() {
        var value = ByteArray(0)
        try {
            val b0 = Integer.parseInt("27", 16).toByte()
            val b1 = Integer.parseInt("01", 16).toByte()
            val b2 = Integer.parseInt("00", 16).toByte()
//            val b3 = BleUtils.getCheckCode(byteArrayOf(b0, deviceType, b1, b2))
//            value = byteArrayOf(b0, deviceType, b1, b2, b3)
            val b3 = Integer.parseInt("84", 16).toByte()
            value = byteArrayOf(b0, deviceType, b1, b2, b3)
        } catch (e: Exception) {
            Logger.e("请求种子指令异常：" + e.toString())
        }
        msg = "摇摇车请求种子数据"
        write(value)
    }

    /**
     * 校验密码
     */
    fun sendAndCheckSeed(keys: ByteArray) {
        val b0 = Integer.parseInt("27", 16).toByte()
        val b1 = Integer.parseInt("02", 16).toByte()
        val b2 = Integer.parseInt("04", 16).toByte()
        val b3 = keys[0]
        val b4 = keys[1]
        val b5 = keys[2]
        val b6 = keys[3]
        val b7 = BleUtils.checkSeekCode(byteArrayOf(b0, deviceType, b1, b2, b3, b4, b5, b6))
        val value = byteArrayOf(b0, deviceType, b1, b2, b3, b4, b5, b6, b7)
        msg = "摇摇车发送加密种子"
        write(value)
    }

    /**
     * 投币
     */
    fun coin() {
        val b0 = Integer.parseInt("27", 16).toByte()
        val b1 = Integer.parseInt("10", 16).toByte()
        val b2 = Integer.parseInt("00", 16).toByte()
        val b3 = Integer.parseInt("95", 16).toByte()
//        val b3 = BleUtils.getCheckCode(byteArrayOf(b0, deviceType, b1, b2))
        val value = byteArrayOf(b0, deviceType, b1, b2, b3)
        msg = "摇摇车投币"
        write(value)
    }

    /**
     * 清楚计数
     */
    fun clearCount() {
        val b0 = Integer.parseInt("27", 16).toByte()
        val b1 = Integer.parseInt("20", 16).toByte()
        val b2 = Integer.parseInt("00", 16).toByte()
        val b3 = Integer.parseInt("A5", 16).toByte()
//        val b3 = BleUtils.getCheckCode(byteArrayOf(b0, deviceType, b1, b2))
        val value = byteArrayOf(b0, deviceType, b1, b2, b3)
        msg = "摇摇车清除计数"
        write(value)
    }


    fun clearConfig() {
        val b0 = Integer.parseInt("27", 16).toByte()
        val b1 = Integer.parseInt("21", 16).toByte()
        val b2 = Integer.parseInt("00", 16).toByte()
        val b3 = Integer.parseInt("A4", 16).toByte()
//        val b3 = BleUtils.getCheckCode(byteArrayOf(b0, deviceType, b1, b2))
        val value = byteArrayOf(b0, deviceType, b1, b2, b3)
        msg = "摇摇车清除配置"
        write(value)
    }
//    fun open() {
//        val b0 = Integer.parseInt("27", 0x10).toByte()
//        val b1 = Integer.parseInt("04", 0x10).toByte()
//        val b2 = Integer.parseInt("00", 0x10).toByte()
//        val b3 = Integer.parseInt("72", 0x10).toByte()
//        val b4 = BleUtils.getCheckCode(byteArrayOf(b0, b1, b2, b3))
//        val value = byteArrayOf(b0, b1, b2, b3, b4)
//        msg = "钥匙使能"
//        write(value)
//
//    }

    //蓝牙数据写入方法
    private fun write(value: ByteArray) {
        if (mClient != null) {
            val writeData = BleUtils.byteArrayToHexString(value)
            mClient?.write(getMAC(), BluetoothConfig.serviceUUID, BluetoothConfig.characteristicUUID1, value, BleWriteResponse { code ->
                if (code == Constants.REQUEST_SUCCESS) {
                    mBluetoothTestCarListener?.onWriteSuccess("${msg}:${writeData}==成功")
//                    Logger.w("${msg}:${writeData}==成功")
                } else {
                    mBluetoothTestCarListener?.onWriteFailure("${msg}:${writeData}==失败")
//                    Logger.w("${msg}:${writeData}--失败")
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
            if (BluetoothConfig.characteristicUUID1 === character) {
                if (value != null) {
                    val s = BleUtils.byteArrayToHexString(value)
                    Logger.e("接收数据：$s")
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
//                        Logger.w("接收蓝牙数据=${BleUtils.byteArrayToHexString(receiveValue)}")
                        when {
                            it.size > 7 && it[2].toInt() == 0x01 -> {
                                //获取随机种子
                                val seeds = byteArrayOf(it[4], it[5], it[6], it[7])
                                sendAndCheckSeed(BleUtils.seedToKey(seeds, 2))
                                mBluetoothTestCarListener?.requestOnSuccess(BleUtils.byteArrayToHexString(receiveValue))
                            }
                            it.size > 2 && it[2].toInt() == 0x02 -> {
                                mBluetoothTestCarListener?.checkSeekOnSuccess(BleUtils.byteArrayToHexString(receiveValue))
                            }
                            it.size > 8 && it[2].toInt() == 0x03 -> {

                                when (it[4].toInt()) {
                                    0 -> {
//                                        // 开始投币
//                                        coin()
                                        mBluetoothTestCarListener?.getDeviceInfoOnIdle()
                                    }
                                    1 -> mBluetoothTestCarListener?.getDeviceInfoOnError("投币机未校准")
                                    2 -> mBluetoothTestCarListener?.getDeviceInfoOnError("投币机故障")
                                    else -> mBluetoothTestCarListener?.getDeviceInfoOnError("投币机状态异常")
                                }
                                //投币累计次数
                                val coinCount = getMathCount(it[5], it[6])
                                //蓝牙使用次数
                                val bleCount = getMathCount(it[7], it[8])
                                mBluetoothTestCarListener?.getDeviceInfoOnSuccess(coinCount,bleCount)

                            }
                            it.size > 2 && it[2].toInt() == 0x10 -> {
                                mBluetoothTestCarListener?.coinOnSuccess(BleUtils.byteArrayToHexString(receiveValue))
                            }
                            it.size > 2 && it[2].toInt() == 0x20 -> {
                                mBluetoothTestCarListener?.clearCountOnSuccess(BleUtils.byteArrayToHexString(receiveValue))
                            }
                            it.size > 2 && it[2].toInt() == 0x21 -> {
                                mBluetoothTestCarListener?.clearConfigOnSuccess(BleUtils.byteArrayToHexString(receiveValue))
                            }
                            else -> {
                                mBluetoothTestCarListener?.onError(BleUtils.byteArrayToHexString(receiveValue))
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


    override fun setResponseListener(mBluetoothTestCarListener: BluetoothTestCarListener?) {
        this.mBluetoothTestCarListener = mBluetoothTestCarListener

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
    fun open(){
        mClient?.let {
          if (! it.isBluetoothOpened)it.openBluetooth()

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
    fun clearRequest(){
        mClient?.clearRequest(macAddress,0)
    }

}