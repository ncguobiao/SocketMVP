package com.gb.sockt.blutoothcontrol.ble.multi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.baselibrary.common.Constant
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.BluetoothClientManager
import com.example.baselibrary.utils.ThreadPoolUtils
import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.ble.BluetoothConfig
import com.gb.sockt.blutoothcontrol.listener.BaseBLEDataListener
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BleMultiDataChangeListener
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.Constants.*
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.orhanobut.logger.Logger
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by guobiao on 2018/8/9.
 * 蓝牙多路协议控制
 */
open class BlueToothMultiControlImpl constructor(deviceTag: String, val context: Context) : BlueToothMultiControl {


    protected var mcontext: WeakReference<Context> = WeakReference(context)
    protected var mClient: BluetoothClient? = null
    protected var mBleConnectListener: BleConnectListener? = null
    protected var EQUIP_TYPE: Byte = 0
    protected lateinit var msg: String
    protected var macAddress: String? = null
    protected var mBleDataChangeListener: BleMultiDataChangeListener? = null
    protected var mConnected: Boolean = false
    protected var mConnectStatusListener: BleConnectStatusListener
    protected var filter: IntentFilter? = null
    protected var mReceiver: BroadcastReceiver?=null
    private var count = 0
    override fun setMAC(mac: String?, bleConnectListener: BleConnectListener?): BaseBLEControl {
        this.macAddress = mac
        this.mBleConnectListener = bleConnectListener
        if (mClient != null) {
            mClient?.registerConnectStatusListener(macAddress, mConnectStatusListener)
        } else {
            mBleConnectListener?.connectOnError()
        }
        return this
    }

    override fun setResponseListener(baseBLEDataListener: BaseBLEDataListener?): BaseBLEControl {
        if ( baseBLEDataListener is BleMultiDataChangeListener)
        this.mBleDataChangeListener = baseBLEDataListener
        return this
    }

//    override fun setResponseListener(bleDataChangeListener: BaseBLEDataListener?): BaseBLEControl {
//
//    }

//    companion object {
//        val options = BleConnectOptions.Builder()
//                .setConnectRetry(3)
//                .setConnectTimeout(20000)
//                .setServiceDiscoverRetry(3)
//                .setServiceDiscoverTimeout(10000)
//                .build()
//    }

    init {
        when (deviceTag) {
            Constant.DEVICE_CE ->
//                EQUIP_TYPE = Integer.parseInt("D1", 16).toByte()
                EQUIP_TYPE = "D1".toInt(16).toByte()
            Constant.DEVICE_CD ->
//                EQUIP_TYPE = Integer.parseInt("B1", 16).toByte()
                EQUIP_TYPE = "B1".toInt(16).toByte()
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
                            it.notify(mac, Constant.SERVICEUUID, Constant.CHARACTERISTICUUID2, bleNotifyResponse)
                        }
                        mBleConnectListener?.let {
                            it.connectOnSuccess()
                        }
                    }
                    Constants.STATUS_DISCONNECTED -> {
                        mConnected = false
                        mClient?.let {
                            it.unnotify(mac, Constant.SERVICEUUID, Constant.CHARACTERISTICUUID2, bleUnNotifyResponse)
                        }
                        mBleConnectListener?.let {
                            it.connectOnFailure()
                        }
                    }
                }
            }
        }

        mReceiver =  MultiBroadcastReceiver()
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
    override fun registerBroadcastReceiver(): BlueToothMultiControl {
        filter = IntentFilter()
        filter?.addAction(ACTION_CHARACTER_CHANGED)

//        BluetoothUtils.registerReceiver(mReceiver, filter)
        mcontext?.get()?.registerReceiver(mReceiver, filter)
        return this
    }




    //蓝牙数据接收
    inner class MultiBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent) {
            when (intent.action) {
                ACTION_CHARACTER_CHANGED -> {
                    val receiveValue = intent.getByteArrayExtra(EXTRA_BYTE_VALUE)
                    Logger.w("接收蓝牙数据=${BleUtils.byteArrayToHexString(receiveValue)}")
                    receiveValue?.let {
                        when {
                            // 请求种子
                            it.size > 7 && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x01
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x04 -> {
                                if (mBleDataChangeListener != null) {
                                    mBleDataChangeListener?.requestSeedSuccess()
                                }
                                Logger.d("种子请求成功")
                                val seeds = byteArrayOf(it[4], it[5], it[6], it[7])
                                val keys = BleUtils.seedToKey(seeds, 2)
                                ThreadPoolUtils.execute {
                                    try {
                                        Thread.sleep(20)
                                        sendAndCheckSeed(keys)
                                    } catch (e: InterruptedException) {
                                        if (mBleDataChangeListener != null) {
                                            mBleDataChangeListener?.sendCheckSeedOnFailure()
                                        }
                                        Logger.d("发送加密种子失败")
                                        e.printStackTrace()
                                    }
                                }
                            }
                            // 验证加密
                            it.size > 4
                                    && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x02
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x01 -> {
                                it[4]?.let {
                                    when {
                                        it.toInt() == 0x00 -> {
                                            Logger.d("解密成功")
                                            if (mBleDataChangeListener != null) {
                                                mBleDataChangeListener?.checkSeedSuccess()
                                            }

                                        }
                                        it.toInt() == 0x01 -> {
                                            AppUtils.runOnUI(Runnable {
                                                if (mBleDataChangeListener != null) {
                                                    mBleDataChangeListener?.checkSeedOnFailure("蓝牙通信失败，解密失败")
                                                }
                                                Logger.d("解密失败")
                                            })

                                        }
                                    }
                                }
                            }
                           // 获取设备信息
                            it.size > 4
                                    && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x03
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x01 -> {
                                it[4]?.apply {
                                    when {
                                        this.toInt() == 0x00 -> if (mBleDataChangeListener != null) {//空闲
                                            mBleDataChangeListener?.deviceCurrentState(false)
                                        }
                                        this.toInt() == 0x01 -> if (mBleDataChangeListener != null) {//忙碌
                                            mBleDataChangeListener?.deviceCurrentState(true)
                                        }
                                    }
                                }

                            }
                            // 开启设备
                            it.size > 5
                                    && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x10
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x02 -> {
                                it[5]?.apply {
                                    when {
                                        this.toInt() == 0x00 -> if (mBleDataChangeListener != null) {
                                            mBleDataChangeListener?.openDeviceSuccess()
                                        }
                                        this.toInt() == 0x01 -> if (mBleDataChangeListener != null) {
                                            mBleDataChangeListener?.openDeviceOnFailure()
                                        }
                                    }
                                }

                            }
                            //加时
                            it.size > 5
                                    && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x12
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x02 -> {
                                it[5]?.apply {
                                    when {
                                        this.toInt() == 0x00 -> if (mBleDataChangeListener != null) {
                                            mBleDataChangeListener?.addTimeSuccess()
                                        }
                                        this.toInt() == 0x01 -> if (mBleDataChangeListener != null) {
                                            mBleDataChangeListener?.addTimeOnFailure()
                                        }
                                    }
                                }

                            }
                        //多路设备开启应答
                            it.size > 8
                                    && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x11
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x05 -> {
                                val backWay = receiveValue[4]//返回通道
                                val b1 = receiveValue[5]//电压
                                val b2 = receiveValue[6]//电压
                                val b3 = receiveValue[7]//电流
                                val b4 = receiveValue[8]//电流
                                if (b1.toInt() == 0 && b2.toInt() == 0) {
                                    count++
                                    if (count < 3) {
                                    } else {
                                        count = 0
                                        Logger.d("负载未接")
                                        if (mBleDataChangeListener != null) {
                                            mBleDataChangeListener?.deivceIsNotOnline("负载未接")
                                        }
                                        return
                                    }
                                } else {
                                    //电压
                                    val voltage = getMathVoltage(b1, b2)
                                    //电流
                                    val electricity = getMathElectricity(b3, b4)
                                    mBleDataChangeListener?.showVoltageAndElectricity(voltage, electricity)

                                }

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
    protected val bleNotifyResponse = object : BleNotifyResponse {
        override fun onResponse(code: Int) {
            if (code == REQUEST_SUCCESS) {
                Logger.e("开启通知成功")
            } else {
                Logger.e("开启通知失败")
            }
        }

        override fun onNotify(service: UUID, character: UUID, value: ByteArray?) {
            if (Constant.CHARACTERISTICUUID1 === character) {
                if (value != null) {
                    val s = BleUtils.byteArrayToHexString(value)
                    Logger.e("接收数据：$s")
                }
            }
        }
    }

    //关闭通知回调
    protected val bleUnNotifyResponse = BleUnnotifyResponse { code ->
        if (code == Constants.REQUEST_SUCCESS) {
            Logger.e("关闭通知成功！")
        }
    }


    /**
     * 重新连接
     */
    override fun deviceIfNeeded() {
        connectDeviceIfNeeded()
    }


    /**
     * 获取mac地址
     */
    protected fun getMAC(): String {
        if (null == macAddress) {
            throw ExceptionInInitializerError("macAddress is not set")
        }
        if (macAddress.isNullOrEmpty()) {
            throw ExceptionInInitializerError("macAddress is not set")
        }
        if (!macAddress!!.contains(":")) {
            throw IllegalArgumentException("this is not macAddress ")
        }
        return macAddress!!
    }

    /**
     * 连接
     */
    override fun connect() {
        connectDeviceIfNeeded()
    }

    /**
     * 获取蓝牙连接状态
     */
    override fun getConnectState():Boolean{
        return mConnected
    }

    override fun setConnectListener(bleConnectListener: BleConnectListener) {
        this.mBleConnectListener = bleConnectListener
    }

    /**
     * 请求种子
     */
    override fun requestSeed() {
        var value = ByteArray(0)
        try {
            val b0 = Integer.parseInt("27", 16).toByte()
            val b1 = Integer.parseInt("01", 16).toByte()
            val b3 = Integer.parseInt("00", 16).toByte()
            val b4 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3))
            value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4)
        } catch (e: Exception) {
            Logger.e("请求种子指令异常：" + e.toString())
        }
        msg = "发送请求种子数据"
        write(value)
    }

    /**
     * 校验密码
     */
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
     * 获取设备信息
     */
    override fun getBLEDeviceInfo(deviceWay: String?) {
        var value = ByteArray(0)
        try {
            val b0 = Integer.parseInt("27", 16).toByte()
            val b1 = Integer.parseInt("03", 16).toByte()
            val b3 = Integer.parseInt("03", 16).toByte()
            if (deviceWay == null || deviceWay?.isEmpty()) {
                mcontext?.get()?.toast("设备路数异常")
                return
            }
            val b4 = deviceWay.toByte()
            val b5: Byte = 40//连接时间
            val b6: Byte = 20//开设备后连接时间
            val b7 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5, b6))
            value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5, b6, b7)
        } catch (e: NumberFormatException) {
            Logger.e("获取设备信息指令异常")
        }
        msg = "获取设备信息"
        write(value)
    }

    /**
     * 加时操作
     */
    override fun addTimeToBle(time: String?, deviceWay: String?, equipElectiic: String?) {
        var value = ByteArray(0)
        var equipElectiic = equipElectiic
        if (equipElectiic.isNullOrEmpty()) {
            equipElectiic = "2000"
        }
        try {
            val integerValue = Integer.valueOf(equipElectiic)!!
            val b0 = Integer.parseInt("27", 16).toByte()
            val b1 = Integer.parseInt("12", 16).toByte()
            val b3 = Integer.parseInt("04", 16).toByte()
            if (deviceWay.isNullOrEmpty()) {
                mcontext?.get()?.toast("设备路数异常")
                return
            }
            val b4 = deviceWay?.toByte()//路数
            val b5 = Integer.parseInt(time).toByte()//时间
            val b6 = (integerValue!! shr 8).toByte()//电流
            val b7 = integerValue!!.toByte()//电流
            val b8 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4!!, b5, b6, b7))
            value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4!!, b5, b6, b7, b8)
        } catch (e: Exception) {
            Logger.e("加时操作指令异常：" + e.toString())
        }

        msg = "加时操作"
        write(value)
    }



    /**
     * 开启
     */
    override fun openDevice(time: String?, deviceWay: String?, equipElectiic: String?) {
        var value = ByteArray(0)
        var equipElectiic = equipElectiic
        if (equipElectiic.isNullOrEmpty()) {
            equipElectiic = "2000"
        }
        try {
            val integerValue = Integer.valueOf(equipElectiic)!!
            val b0 = Integer.parseInt("27", 16).toByte()
            val b1 = Integer.parseInt("10", 16).toByte()
            val b3 = Integer.parseInt("04", 16).toByte()
            if (deviceWay.isNullOrEmpty()) {
                mcontext?.get()?.toast("设备路数异常")
                return
            }
            val b4 = deviceWay?.toByte()//路数
            val b5 = time?.toByte()//时间
            val b6 = (integerValue!! shr 8).toByte()//电流
            val b7 = integerValue!!.toByte()//电流
            val b8 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4!!, b5!!, b6, b7))
            value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4!!, b5!!, b6, b7, b8)
        } catch (e: Exception) {
            Logger.e("打开操作继电器指令异常：" + e.toString())
        }
        msg = "发送开继电器指令"
        write(value)
    }

    /**
     * 连接设备
     */
    protected fun connectDevice() {
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
    protected fun connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice()
        }
    }

    //蓝牙数据写入方法
    protected fun write(value: ByteArray) {
        if (mClient != null) {
            val writeData = BleUtils.byteArrayToHexString(value)
            mClient?.write(getMAC(), Constant.SERVICEUUID, Constant.CHARACTERISTICUUID1, value, BleWriteResponse { code ->
                if (code == REQUEST_SUCCESS) {

                    Logger.w("${msg}:${writeData}==成功")
                } else {
                    Logger.w("${msg}:${writeData}--失败")
                }
            })
        }
    }


    override fun close() {
        mClient?.disconnect(macAddress)
        mClient = null
    }


    /**
     * 获取电流
     * @param b3
     * @param b4
     * @return
     */
    protected fun getMathElectricity(b3: Byte, b4: Byte): Int {
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

    /**
     * 获取电压
     *
     * @param b1
     * @param b2
     * @return
     */
    protected fun getMathVoltage(b1: Byte, b2: Byte): Int {
        var voltage1 = Integer.toHexString(b1.toInt() and 0XFF)
        var voltage2 = Integer.toHexString(b2.toInt() and 0XFF)
        if (voltage1.length == 1) {
            voltage1 = "0$voltage1"
        }
        if (voltage2.length == 1) {
            voltage2 = "0$voltage2"
        }
        return Integer.parseInt(
                voltage1 + voltage2, 16)
    }


}


