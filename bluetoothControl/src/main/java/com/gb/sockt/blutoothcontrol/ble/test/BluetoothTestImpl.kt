package com.gb.sockt.blutoothcontrol.ble.test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.baselibrary.showToast
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
 * 一键启动
 */
class BluetoothTestImpl constructor(val context: Context?) : BluetoothTest {

    private var address: String? = null
    private var mcontext: WeakReference<Context>? = null

    private var mClient: BluetoothClient? = null

    private var mBleConnectListener: BleConnectListener? = null
    private lateinit var msg: String
    private var macAddress: String? = null
    private var mBluetoothTestListener: BluetoothTestListener? = null
    private var mConnected: Boolean = false
    private lateinit var mConnectStatusListener: BleConnectStatusListener
    private var filter: IntentFilter? = null
    private val flag by lazy {
        Integer.parseInt("72", 0x10).toByte()
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
     *向设备获取请求码
     */
    fun requestCode() {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("00", 0x10).toByte()
        val b2 = Integer.parseInt("00", 0x10).toByte()
        //异或校验位
        val b4 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, flag))
        val value = byteArrayOf(b0, b1, b2, flag, b4)
        msg = "发送requestCode"
        write(value)
    }


    /**
     * @time  创建时间 : 上午8:50
     * @author  : guobiao
     * @Description  添加MAC指令第一帧
     * @param  code:操作码  mac：MAC地址
     */
    open fun sendAddMAC_1(code: ByteArray, mac: String) {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("01", 0x10).toByte()
        val b2 = Integer.parseInt("0B", 0x10).toByte()

        //操作码
        val b3 = code[0]
        val b4 = code[1]
        val b5 = code[2]
        val b6 = code[3]

        //mac地址检测位(可配置，0 or 1)
        val macCheckFlag = 0.toByte()

        //MAC地址
        val macBytes = BleUtils.getByteArrAddress(mac)
        val b8 = macBytes[0]
        val b9 = macBytes[1]
        val b10 = macBytes[2]
        val b11 = macBytes[3]
        val b12 = macBytes[4]
        val b13 = macBytes[5]

        val b15 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, macCheckFlag, b8, b9, b10, b11, b12, b13, flag))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, macCheckFlag, b8, b9, b10, b11, b12, b13, flag, b15)
        msg = "sendAddMAC_1"
        write(value)
    }


    /**
     * int main()
        {
        //定义一个负数
        short a = -50;
        //定义两个数组
        unsigned char buf[2] = { 0 };
        //将a左移8个单位在和0xff与，取a的前8位
        buf[0] = (a >> 8) & 0XFF;
        //将a和0xff与，取a的后8位
        buf[1] = a & 0XFF;
        printf("%x\n", buf[0]);
        printf("%x\n", buf[1]);
        //将得到的数组合并成原始数
        a = buf[0] << 8 | buf[1];
        printf("%x\n", a);
        printf("%d\n", a);
        return 0;
        }
     */
    /**
     * @time  创建时间 : 上午8:56
     * @author  : guobiao
     * @Description  添加MAC指令第二帧
     * @param   code:操作码  key8Byte高8字节
     */
    open fun sendAddMAC_2(code: ByteArray, keyHigt8b: ByteArray) {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("02", 0x10).toByte()
        val b2 = Integer.parseInt("0C", 0x10).toByte()

        //操作码
        val b3 = code[0]
        val b4 = code[1]
        val b5 = code[2]
        val b6 = code[3]
        //钥匙8字节
        val b7 = keyHigt8b[0]
        val b8 = keyHigt8b[1]
        val b9 = keyHigt8b[2]
        val b10 = keyHigt8b[3]
        val b11 = keyHigt8b[4]
        val b12 = keyHigt8b[5]
        val b13 = keyHigt8b[6]
        val b14 = keyHigt8b[7]

        val b16 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, flag))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, flag, b16)
        msg = "sendAddMAC_2"
        write(value)
    }

    /**
     * @time  创建时间 : 上午8:56
     * @author  : guobiao
     * @Description  添加MAC指令第三帧
     * @param   code:操作码  key8Byte后字节
     */
    open fun sendAddMAC_3(code: ByteArray, keyLow8b: ByteArray) {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("03", 0x10).toByte()
        val b2 = Integer.parseInt("0C", 0x10).toByte()
        //操作码
        val b3 = code[0]
        val b4 = code[1]
        val b5 = code[2]
        val b6 = code[3]
        //钥匙8字节
        val b7 = keyLow8b[0]
        val b8 = keyLow8b[1]
        val b9 = keyLow8b[2]
        val b10 = keyLow8b[3]
        val b11 = keyLow8b[4]
        val b12 = keyLow8b[5]
        val b13 = keyLow8b[6]
        val b14 = keyLow8b[7]

        val b16 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, flag))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, flag, b16)
        msg = "sendAddMAC_3"
        write(value)
    }

    /**
     * @time  创建时间 : 上午9:08
     * @author  : guobiao
     * @Description  删除MAC指令
     * @param  code:操作码  mac：MAC地址
     */
    fun sendDeleteMAC(code: ByteArray, mac: String) {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("04", 0x10).toByte()
        val b2 = Integer.parseInt("0A", 0x10).toByte()
        //操作码
        val b3 = code[0]
        val b4 = code[1]
        val b5 = code[2]
        val b6 = code[3]
        //MAC地址
        val macBytes = BleUtils.getByteArrAddress(mac)
        val b9 = macBytes[0]
        val b10 = macBytes[1]
        val b11 = macBytes[2]
        val b12 = macBytes[3]
        val b13 = macBytes[4]
        val b14 = macBytes[5]

        val b16 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b9, b10, b11, b12, b13, b14, flag))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b9, b10, b11, b12, b13, b14, flag, b16)
        msg = "发送删除MAC指令"
        write(value)

    }

    /**
     * @time  创建时间 : 上午9:16
     * @author  : guobiao
     * @Description  开关指令
     * @param   code:操作码  operationData:0-关，1-开
     */
    fun openOrClose(code: ByteArray, operationData: Byte) {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("05", 0x10).toByte()
        val b2 = Integer.parseInt("05", 0x10).toByte()
        //操作码
        val b3 = code[0]
        val b4 = code[1]
        val b5 = code[2]
        val b6 = code[3]
        //0-关，1开
        val operationData = operationData

        val b9 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, operationData, flag))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, operationData, flag, b9)
        msg = "发送开关指令"
        write(value)

    }

    /**
     * @time  创建时间 : 上午9:21
     * @author  : guobiao
     * @Description
     * @param 查询单个MAC
     */
    fun findAllMAC() {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("06", 0x10).toByte()
        val b2 = Integer.parseInt("00", 0x10).toByte()
        val b4 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, flag))
        val value = byteArrayOf(b0, b1, b2, flag, b4)
        msg = "发送查询单个MAC"
        write(value)
    }

    /**
     * @time  创建时间 : 上午9:23
     * @author  : guobiao
     * @Description  查询单个MAC
     * @param
     */
    fun findSingleMAC(code: ByteArray, mac: String) {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("07", 0x10).toByte()
        val b2 = Integer.parseInt("0A", 0x10).toByte()
        //操作码
        val b3 = code[0]
        val b4 = code[1]
        val b5 = code[2]
        val b6 = code[3]
        //MAC地址
        val macBytes = BleUtils.getByteArrAddress(mac)
        val b9 = macBytes[0]
        val b10 = macBytes[1]
        val b11 = macBytes[2]
        val b12 = macBytes[3]
        val b13 = macBytes[4]
        val b14 = macBytes[5]

        val b16 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b9, b10, b11, b12, b13, b14, flag))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b9, b10, b11, b12, b13, b14, flag, b16)
        msg = "发送查询单个MAC"
        write(value)
    }

    /**
     * @time  创建时间 : 上午9:27
     * @author  : guobiao
     * @Description  恢复出厂设置
     * @param
     */
    fun resetDevice(mac: String) {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("FF", 0x10).toByte()
        val b2 = Integer.parseInt("08", 0x10).toByte()

        //密码8字节根据MAC计算
        val macBytes = BleUtils.getByteArrAddress(mac)
        val b3 = macBytes[0]
        val b4 = macBytes[1]
        val b5 = macBytes[2]
        val b6 = macBytes[3]
        val b7 = macBytes[4]
        val b8 = macBytes[5]
        val b9 = macBytes[6]
        val b10 = macBytes[7]

        val b12 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b9, b10, flag))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b9, b10, flag, b12)
        msg = "发送恢复出厂设置"
        write(value)
    }

    /**
     * @time  创建时间 : 上午9:30
     * @author  : guobiao
     * @Description 设置设备MAC
     * @param
     */
    fun setDeviceMAC(mac: String) {
        if (!getConnectState()){
            context?.showToast("蓝牙已断开")
        }
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("FE", 0x10).toByte()
        val b2 = Integer.parseInt("08", 0x10).toByte()

        //MAC地址4字节密文
        val macBytes = BleUtils.getByteArrAddress(mac)
        val b3 = macBytes[0]
        val b4 = macBytes[1]
        val b5 = macBytes[2]
        val b6 = macBytes[3]

        //加密结果高2位
        val b7 = macBytes[4]
        val b8 = macBytes[5]

        //厂家标记
        val b9 = macBytes[4]
        val b10 = macBytes[5]

        val b12 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b9, b10, flag))
        val value = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b9, b10, flag, b12)
        msg = "发送设置设备MAC"
        write(value)
    }


    fun open() {
        val b0 = Integer.parseInt("27", 0x10).toByte()
        val b1 = Integer.parseInt("04", 0x10).toByte()
        val b2 = Integer.parseInt("00", 0x10).toByte()
        val b3 = Integer.parseInt("72", 0x10).toByte()
        val b4 = BleUtils.checkSeekCode(byteArrayOf(b0, b1, b2, b3))
        val value = byteArrayOf(b0, b1, b2, b3, b4)
        msg = "钥匙使能"
        write(value)

    }

    //蓝牙数据写入方法
    private fun write(value: ByteArray) {
        //操作返回当前时间
        mBluetoothTestListener?.onOperation()
        if (mClient != null) {
            val writeData = BleUtils.byteArrayToHexString(value)
            mClient?.write(getMAC(), BluetoothConfig.serviceUUID, BluetoothConfig.characteristicUUID1, value, BleWriteResponse { code ->
                if (code == Constants.REQUEST_SUCCESS) {
                    mBluetoothTestListener?.onWriteSuccess("${msg}:${writeData}==成功")
                    Logger.w("${msg}:${writeData}==成功")
                } else {
                    mBluetoothTestListener?.onWriteFailure("${msg}:${writeData}==失败")
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

                            it.size > 6 && it[1].toInt() == 0x00
                            -> {
                                Logger.w("获取操作码成功")
                                val codes = getCodes(it)
                                mBluetoothTestListener?.onGetCodeSuccess(codes)
                            }
                            it.size > 7 && it[1].toInt() == 0x01
                            -> {
                                val codes = getCodes(it)
                                val result = it[7]
                                when (result) {
                                    0.toByte() -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第一帧:失败")
                                        Logger.e("添加MAC地址第一帧:失败")
                                    }
                                    1.toByte() -> {
                                        Logger.d("添加MAC地址第一帧:成功")
                                        mBluetoothTestListener?.onAddMAC_1(codes)
                                    }
                                    2.toByte() -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第一帧:已存在")
                                        Logger.w("添加MAC地址第一帧:已存在")
                                    }
                                    else -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第一帧:已满")
                                        Logger.w("添加MAC地址第一帧:已满")
                                    }
                                }
                            }
                            it.size > 7 && it[1].toInt() == 0x02
                            -> {
                                val codes = getCodes(it)
                                val result = it[7]
                                when (result) {
                                    0.toByte() -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第二帧:失败")
                                        Logger.e("添加MAC地址第二帧:失败")
                                    }
                                    1.toByte() -> {
                                        Logger.d("添加MAC地址第二帧:成功")
                                        mBluetoothTestListener?.onAddMAC_2(codes)
                                    }
                                    2.toByte() -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第二帧:已存在")
                                        Logger.w("添加MAC地址第二帧:已存在")
                                    }
                                    else -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第二帧:已满")
                                        Logger.w("添加MAC地址第二帧:已满")
                                    }
                                }
                            }
                            it.size > 7 && it[1].toInt() == 0x03
                            -> {
                                val codes = getCodes(it)
                                val result = it[7]
                                when (result) {
                                    0.toByte() -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第三帧:失败")
                                        Logger.e("添加MAC地址第三帧:失败")
                                    }
                                    1.toByte() -> {
                                        Logger.d("添加MAC地址第三帧:成功")
                                        mBluetoothTestListener?.onAddMAC_3(codes)
                                    }
                                    2.toByte() -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第三帧:已存在")
                                        Logger.w("添加MAC地址第三帧:已存在")
                                    }
                                    else -> {
                                        mBluetoothTestListener?.onError("添加MAC地址第三帧:已满")
                                        Logger.w("添加MAC地址第三帧:已满")
                                    }
                                }
                            }
                            it.size > 7 && it[1].toInt() == 0x04
                            -> {
                                val codes = getCodes(it)
                                val result = it[7]
                                when (result) {
                                    0.toByte() -> {
                                        mBluetoothTestListener?.onError("删除MAC地址:失败")
                                        Logger.e("删除MAC地址:失败")
                                    }
                                    1.toByte() -> {
                                        Logger.d("删除MAC地址:成功")
                                        mBluetoothTestListener?.onDelete(codes, "删除MAC地址:成功")
                                    }
                                    2.toByte() -> {
                                        mBluetoothTestListener?.onError("删除MAC地址:不存在")
                                        Logger.w("删除MAC地址:不存在")
                                    }
                                    else -> {
                                        mBluetoothTestListener?.onError("删除MAC地址:已满")
                                        Logger.w("删除MAC地址:已满")
                                    }
                                }
                            }
                            it.size > 7 && it[1].toInt() == 0x05
                            -> {
                                val codes = getCodes(it)
                                val result = it[7]
                                when (result) {
                                    0.toByte() -> {
                                        mBluetoothTestListener?.onError("控制开关:失败")
                                        Logger.e("控制开关:失败")
                                    }
                                    1.toByte() -> {
                                        Logger.d("控制开关:成功")
                                        mBluetoothTestListener?.onOpenOrClose(codes)
                                    }
                                    else -> {}
                                }
                            }
                            it.size > 4 && it[1].toInt() == 0x06
                            -> {
                                //数据长度可变 0x08/0x0E
                                val dataLength = it[2]
                                //当前帧
                                val currentPic = it[3]
                                //总帧
                                val totalPic = it[4]
                                Logger.w("查询MAC成功currentPic=$currentPic totalPic=$totalPic")
//                                mBluetoothTestListener?.onFindAllMAC(codes)

                            }
                            it.size > 7 && it[1].toInt() == 0x07
                            -> {
                                val codes = getCodes(it)
                                val result = it[7]
                                when (result) {
                                    0.toByte() -> {
                                        mBluetoothTestListener?.onError("查询单个MAC:失败")
                                        Logger.e("查询单个MAC:失败")
                                    }
                                    1.toByte() -> {
                                        Logger.d("查询单个MAC:成功")
                                        mBluetoothTestListener?.onFindSingleMAC(codes, "查询单个MAC:成功")
                                    }
                                    2.toByte() -> {
                                        mBluetoothTestListener?.onError("查询单个MAC:不存在")
                                        Logger.w("查询单个MAC:不存在")
                                    }
                                    else -> {}
                                }
                            }
                            //TODO
                        // 此处协议上位0x07
                            it.size > 3 && it[1].toInt() == 0xFF
                            -> {
                                val result=it[3]
                                when (result) {
                                    0.toByte() -> {
                                        mBluetoothTestListener?.onError("恢复出厂设置:失败")
                                        Logger.e("恢复出厂设置:失败")
                                    }
                                    1.toByte() -> {
                                        Logger.d("恢复出厂设置:成功")
                                        mBluetoothTestListener?.onResetDevice( "恢复出厂设置:成功")
                                    }
                                    2.toByte() -> {
                                        mBluetoothTestListener?.onError("恢复出厂设置:不存在")
                                        Logger.w("恢复出厂设置:不存在")
                                    }
                                    else -> {}
                                }
                            }
                            it.size > 3 && it[1].toInt() == 0xFE
                            -> {
                                val result=it[3]
                                when (result) {
                                    0.toByte() -> {
                                        mBluetoothTestListener?.onError("设置MAC地址:失败")
                                        Logger.e("设置MAC地址:失败")
                                    }
                                    1.toByte() -> {
                                        Logger.d("设置MAC地址:成功")
                                        mBluetoothTestListener?.onSetDeciveMAC( "设置MAC地址:成功")
                                    }
                                    2.toByte() -> {
                                        mBluetoothTestListener?.onError("设置MAC地址:不存在")
                                        Logger.w("设置MAC地址:不存在")
                                    }
                                    else -> {}
                                }
                            }
                            else -> {
                            }
                        }

//                        when {
//                            it.size > 2 && it[1].toInt() == 0x01
//                            -> {
//                                mBluetoothTestListener?.onAddMAC(BleUtils.byteArrayToHexString(receiveValue))
//
//                            }
//                            it.size > 2 && it[1].toInt() == 0x02
//                            -> {
//                                mBluetoothTestListener?.onDelete(BleUtils.byteArrayToHexString(receiveValue))
//                            }
//                            it.size > 5 && it[1].toInt() == 0x03 -> {
//                                val dataLength = it[2]
//                                val currentCount = it[3]
//                                val totalCount = it[4]
//                                if (currentCount <= totalCount) {
//                                    if (dataLength >= 6) {
//                                        val i = dataLength / 6
//                                        for (j in 0 until i) {
////                                            Logger.e("j=${j}")
//                                            val dataArr = mutableListOf<Byte>()
//                                            it.forEachIndexed { index, _ ->
//                                                //                                                Logger.e("index${index}")
//                                                if (index < 6) {
////                                                    Logger.e("index${index} data=${it[index + 6 * j + 5]}")
//                                                    dataArr.add(it[index + 6 * j + 5])
//                                                }
//                                            }
////                                            Logger.e("j${j} data=${dataArr}")
//                                            mBluetoothTestListener?.onFindAllMAC(j + 1, dataArr)
//
//                                        }
//
//                                    } else {
//
//                                    }
//                                } else {
//                                }
//                            }
//                            it.size > 5 && it[1].toInt() == 0x04 -> {
//                                val dataLength = it[2]
//                                val currentCount = it[3]
//                                val totalCount = it[4]
//                                if (currentCount <= totalCount) {
//                                    if (dataLength >= 6) {
//                                        val i = dataLength / 6
//                                        for (j in 0 until i) {
//                                            Logger.e("j=${j}")
//                                            val dataArr = mutableListOf<Byte>()
//                                            it.forEachIndexed { index, _ ->
//                                                //                                                Logger.e("index${index}")
//                                                if (index < 6) {
////                                                    Logger.e("index${index} data=${it[index + 6 * j + 5]}")
//                                                    dataArr.add(it[index + 6 * j + 5])
//                                                }
//                                            }
//                                            Logger.e("j${j} data=${dataArr}")
//                                            mBluetoothTestListener?.onFindAllMAC(j + 1, dataArr)
//
//                                        }
//
//                                    } else {
//
//                                    }
//                                } else {
//
//                                }
//
//                            }
//                            else -> {
//                                mBluetoothTestListener?.onError(BleUtils.byteArrayToHexString(receiveValue))
//                            }
//                        }

                    }
                }
            }
        }

    }

    //获取操作码
    private fun getCodes(it: ByteArray): ByteArray? {
        return BleUtils.seedToKey(byteArrayOf(it[3], it[4], it[5], it[6]), 2)
    }


    override fun setMAC(mac: String?, bleConnectListener: BleConnectListener?): BaseBLEControl {
        if (mac.isNullOrEmpty()) {
            throw IllegalArgumentException("MAC地址异常")
        }
        this.macAddress = mac
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
        this.address = address
    }


}