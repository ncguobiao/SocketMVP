package com.gb.sockt.blutoothcontrol.uitls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.baselibrary.common.Constant
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.BluetoothClientManager
import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.ble.BluetoothConfig
import com.gb.sockt.blutoothcontrol.listener.BaseBLEDataListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.Constants.*
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.orhanobut.logger.Logger
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by guobiao on 2018/8/9.
 */
open class BlueToothSingeControlImpl constructor(deviceTag: String, val context: Context?) : BlueToothSingeControl {


    private var mcontext: WeakReference<Context>? = null

    private var mClient: BluetoothClient? = null

    private var mBleConnectListener: BleConnectListener? = null
    private var EQUIP_TYPE: Byte = 0
    private lateinit var msg: String
    private var macAddress: String? = null
    private var mBleDataChangeListener: BaseBLEDataListener? = null
    private var mConnected: Boolean = false
    private var mConnectStatusListener: BleConnectStatusListener
    private var filter: IntentFilter? = null
    private lateinit var macBytes: ByteArray




    override fun setMAC(mac: String?, bleConnectListener: BleConnectListener?): BlueToothSingeControlImpl {
        this.macAddress = mac
        macBytes = BleUtils.getByteArrAddress(macAddress)
        this.mBleConnectListener = bleConnectListener
        if (mClient != null) {
            mClient?.registerConnectStatusListener(macAddress, mConnectStatusListener)
        } else {
            mBleConnectListener?.connectOnError()
        }
        return this
    }

    override fun setResponseListener(baseBLEDataListener: BaseBLEDataListener?): BaseBLEControl {
        this.mBleDataChangeListener = baseBLEDataListener
        return this

    }

    init {
        when (deviceTag) {
            Constant.DEVICE_CE ->
                EQUIP_TYPE = Integer.parseInt("D1", 16).toByte()
            Constant.DEVICE_CD ->
                EQUIP_TYPE = Integer.parseInt("B1", 16).toByte()
            else -> Logger.d("单路设备")
        }

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
     * 注销广播
     */
    override fun unregisterBroadcastReceiver() {
        filter = null
        mcontext?.get()?.unregisterReceiver(mReceiver)
    }

    /**
     * 注册广播
     */

    override fun registerBroadcastReceiver(): BaseBLEControl {
        filter = IntentFilter()
        filter?.addAction(ACTION_CHARACTER_CHANGED)
        if (context != null) this.mcontext = WeakReference(context!!)
//        BluetoothUtils.registerReceiver(mReceiver, filter)
        mcontext?.get()?.registerReceiver(mReceiver, filter)
        return this

    }

    override fun readVoltage() {
        val b0 = Integer.parseInt("0B", 0x10).toByte()
        macBytes = BleUtils.getByteArrAddress(macAddress)
        val b1 = macBytes[0]
        val b2 = macBytes[1]
        val b3 = macBytes[2]
        val b4 = macBytes[3]
        val b5 = macBytes[4]
        val b6 = macBytes[5]
        val b7 = Integer.parseInt("C1", 0x10).toByte()
        val b8 = Integer.parseInt("30", 0x10).toByte()
        val b9 = Integer.parseInt("01", 0x10).toByte()
        val b10 = BleUtils.getCheckCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10)
        msg = "获取电压"
        write(value)
    }




    override fun openDevice(keys: ByteArray) {
        val b0 = "10".toByte(16)
        val b1 = macBytes!![0]
        val b2 = macBytes!![1]
        val b3 = macBytes!![2]
        val b4 = macBytes!![3]
        val b5 = macBytes[4]
        val b6 = macBytes[5]
        val b7 = Integer.parseInt("C1", 0x10).toByte()
        val b8 =Integer.parseInt("27", 0x10).toByte()
        val b9 = Integer.parseInt("02", 0x10).toByte()
        val b10 = keys[0]
        val b11 = keys[1]
        val b12 = keys[2]
        val b13 = keys[3]
        var b14 = Integer.parseInt("02", 0x10).toByte()

        val b15 = BleUtils.getCheckCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15)
        msg = "开启设备指令"
        write(value)
    }

    override fun close() {
        mClient?.disconnect(macAddress)
        mClient = null
    }

    override fun deviceIfNeeded() {
        connectDeviceIfNeeded()
    }

    override fun getConnectState(): Boolean {
        return mConnected
    }


    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent) {
            when (intent.action) {
                ACTION_CHARACTER_CHANGED -> {
                    val receiveValue = intent.getByteArrayExtra(EXTRA_BYTE_VALUE)
                    receiveValue?.let {
                        Logger.w("接收蓝牙数据=${BleUtils.byteArrayToHexString(receiveValue)}")
                        when {
                            it.size > 13 && it[8].toInt() == 103
                                    && it[9].toInt() == 0x01 -> {
                                mBleDataChangeListener?.requestSeedSuccess()
                                val seeds = byteArrayOf(it[10], it[11], it[12], it[13])
                                val keys = BleUtils.seedToKey(seeds, 2)
                                openDevice(keys)
                            }
                            it.size > 9 && it[8].toInt() == 0x67
                                    && it[9].toInt() ==2 -> {
                                mBleDataChangeListener?.openDeviceSuccess()
                            }
                            it.size > 11 && it[8].toInt() == 0x70
                                    && it[9].toInt() ==0x01 -> {
                                val voltage = (receiveValue[10].toInt() and 0xff) * 0x100
                                + (receiveValue[11].toInt() and 0xff)
                                mBleDataChangeListener?.showVoltageAndElectricity(voltage,0)
                            }
                            else -> {
                            }
                        }

                    }
                }
            }
        }

    }

    //开启通知回调
    private val bleNotifyResponse = object : BleNotifyResponse {
        override fun onResponse(code: Int) {
            if (code == REQUEST_SUCCESS) {
                Logger.e("开启通知成功")
            } else {
                Logger.e("开启通知失败")
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


    private fun getMAC(): String {
        if (null == macAddress) {
            throw ExceptionInInitializerError("macAddress is not set")
        }
        if (macAddress.isNullOrEmpty()) {
            throw ExceptionInInitializerError("macAddress is not set")
        }
        if (!macAddress!!.contains(":")) {
            throw IllegalArgumentException("is not macAddress ")
        }
        return macAddress!!
    }

    /**
     * 连接
     */
    override fun connect() {
        connectDeviceIfNeeded()
    }

    override fun setConnectListener(bleConnectListener: BleConnectListener) {
        this.mBleConnectListener = bleConnectListener
    }

    //校验密码
    override fun sendAndCheckSeed(keys: ByteArray) {
        val b0 = Integer.parseInt("27", 16).toByte()
        val b1 = Integer.parseInt("02", 16).toByte()
        val b3 = Integer.parseInt("04", 16).toByte()
        val b4 = keys[0]
        val b5 = keys[1]
        val b6 = keys[2]
        val b7 = keys[3]
        val b8 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5, b6, b7))
        val value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5, b6, b7, b8)
        msg = "发送加密种子"
        write(value)
    }

    /**
     * 请求种子
     */
    override fun requestSeed(time: String) {
        val b0 = Integer.parseInt("0C", 16).toByte()
        macBytes = BleUtils.getByteArrAddress(macAddress)
        val b1 = macBytes[0]
        val b2 = macBytes[1]
        val b3 = macBytes[2]
        val b4 = macBytes[3]
        val b5 = macBytes[4]
        val b6 = macBytes[5]
        val b7 = Integer.parseInt("C1", 16).toByte()

        val b8 = Integer.parseInt("27", 16).toByte()
        val b9 = Integer.parseInt("01", 16).toByte()
        val b10 = time.toByte()
        val b11 = BleUtils.getCheckCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11)
        msg = "请求种子指令"
        write(value)
    }


    /**
     * 连接设备
     */
    fun connectDevice() {
        mClient?.let {
            mClient!!.connect(getMAC(), BluetoothConfig.options) { code, data ->
                when (code) {
                //连接成功
                    REQUEST_SUCCESS -> {
                        mConnected = true
                        Logger.e("发起连接成功")
                    }
                //连接失败
                    REQUEST_FAILED -> {
                        mConnected = false
                        Logger.e("发起连接失败")
                    }
                    BLE_NOT_SUPPORTED -> {
                        mConnected = false
                        Logger.e("不支持蓝牙")
                    }

                }
            }
        }
    }


    //是否需要连接设备
    private fun connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice()
        }
    }

    //蓝牙数据写入方法
    private fun write(value: ByteArray) {
        if (mClient != null) {
            val writeData = BleUtils.byteArrayToHexString(value)
            mClient?.write(getMAC(), BluetoothConfig.serviceUUID, BluetoothConfig.characteristicUUID1, value, BleWriteResponse { code ->
                if (code == REQUEST_SUCCESS) {

                    Logger.w("${msg}:${writeData}==成功")
                } else {
                    Logger.w("${msg}:${writeData}--失败")
                }
            })
        }
    }


}


