package com.gb.sockt.blutoothcontrol.ble.test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.BluetoothClientManager
import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.ble.BluetoothConfig
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestListener
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
class BluetoothTestImpl constructor( val context: Context?):BluetoothTest {

    private var address: String?=null
    private var mcontext: WeakReference<Context>? = null

    private var mClient: BluetoothClient? = null

    private var mBleConnectListener: BleConnectListener? = null
    private lateinit var msg: String
    private var macAddress: String? = null
    private var mBluetoothTestListener: BluetoothTestListener? = null
    private var mConnected: Boolean = false
    private lateinit var mConnectStatusListener: BleConnectStatusListener
    private var filter: IntentFilter? = null
    private lateinit var macBytes: ByteArray

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




    override fun sendAdd() {
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("01", 0x10).toByte()
        val b2 = Integer.parseInt("0C", 0x10).toByte()
        val b3 = Integer.parseInt("01", 0x10).toByte()
        val b4 = Integer.parseInt("02", 0x10).toByte()
        val b5 = Integer.parseInt("03", 0x10).toByte()
        val b6 = Integer.parseInt("04", 0x10).toByte()
        val b7 = Integer.parseInt("05", 0x10).toByte()
        val b8 = Integer.parseInt("06", 0x10).toByte()
        macBytes = BleUtils.getByteArrAddress(address)
        val b9 = macBytes[0]
        val b10 = macBytes[1]
        val b11 = macBytes[2]
        val b12 = macBytes[3]
        val b13 = macBytes[4]
        val b14 = macBytes[5]
        val b15 =Integer.parseInt("72", 0x10).toByte()
        val b16 = BleUtils.getCheckCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9,b10,b11,b12,b13,b14,b15))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10,b11,b12,b13,b14,b15,b16)
        msg = "发送Add"
        write(value)
    }


    override fun sendLess() {
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("02", 0x10).toByte()
        val b2 = Integer.parseInt("0C", 0x10).toByte()
        val b3 = Integer.parseInt("01", 0x10).toByte()
        val b4 = Integer.parseInt("02", 0x10).toByte()
        val b5 = Integer.parseInt("03", 0x10).toByte()
        val b6 = Integer.parseInt("04", 0x10).toByte()
        val b7 = Integer.parseInt("05", 0x10).toByte()
        val b8 = Integer.parseInt("06", 0x10).toByte()
        macBytes = BleUtils.getByteArrAddress(address)
        val b9 = macBytes[0]
        val b10 = macBytes[1]
        val b11 = macBytes[2]
        val b12 = macBytes[3]
        val b13 = macBytes[4]
        val b14 = macBytes[5]
        val b15 =Integer.parseInt("72", 0x10).toByte()
        val b16 = BleUtils.getCheckCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9,b10,b11,b12,b13,b14,b15))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10,b11,b12,b13,b14,b15,b16)
        msg = "发送Less"
        write(value)

    }

    //蓝牙数据写入方法
    private fun write(value: ByteArray) {
        if (mClient != null) {
            val writeData = BleUtils.byteArrayToHexString(value)
            mClient?.write(getMAC(), BluetoothConfig.serviceUUID, BluetoothConfig.characteristicUUID1, value, BleWriteResponse { code ->
                if (code == Constants.REQUEST_SUCCESS) {
                    mBluetoothTestListener?.onWriteSuccess("${msg}:${writeData}==成功")
                    Logger.w("${msg}:${writeData}==成功")
                } else {
                    mBluetoothTestListener?.onWriteFaile("${msg}:${writeData}==失败")
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
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_CHARACTER_CHANGED -> {
                    val receiveValue = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE)
                    receiveValue?.let {
                        Logger.w("接收蓝牙数据=${BleUtils.byteArrayToHexString(receiveValue)}")
                        when {
                            it.size > 2 && it[1].toInt() == 0x01
                                    && it[2].toInt() == 0x07 -> {
                                mBluetoothTestListener?.onAdd(BleUtils.byteArrayToHexString(receiveValue))

                            }
                            it.size > 2 && it[1].toInt() == 0x02
                                    && it[2].toInt() == 0x07 -> {
                                mBluetoothTestListener?.onLess(BleUtils.byteArrayToHexString(receiveValue))
                            }
                            else -> {
                                mBluetoothTestListener?.onError(BleUtils.byteArrayToHexString(receiveValue))
                            }
                        }

                    }
                }
            }
        }

    }


    override fun setMAC(mac: String?, bleConnectListener: BleConnectListener?): BaseBLEControl {
        if (mac.isNullOrEmpty()){
            throw IllegalArgumentException("MAC地址异常")
        }
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
            mClient!!.connect(getMAC(), BluetoothConfig.options) { code, data ->
                when (code) {
                //连接成功
                    Constants.REQUEST_SUCCESS -> {
                        mConnected = true
                        Logger.e("发起连接成功")
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

     fun getMAC(): String {
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


    override fun setResponseListener(mBluetoothTestListener: BluetoothTestListener) {
        this.mBluetoothTestListener = mBluetoothTestListener
    }

    override fun close() {
        Logger.d("APP主动断开蓝牙")
        mClient?.disconnect(macAddress)
    }

    override fun deviceIfNeeded() {
    }

    override fun getConnectState(): Boolean {
        return mConnected
    }

    fun setMACAddress(address: String) {
        this.address =address
    }
}