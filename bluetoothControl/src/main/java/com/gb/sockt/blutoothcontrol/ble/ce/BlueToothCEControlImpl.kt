package com.gb.sockt.blutoothcontrol.ble.ce

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.ThreadPoolUtils
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControl
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControlImpl
import com.gb.sockt.blutoothcontrol.listener.BleCEDataChangeListener
import com.inuker.bluetooth.library.Constants
import com.orhanobut.logger.Logger

/**
 * Created by guobiao on 2018/8/9.
 * CE蓝牙协议操作
 */
class BlueToothCEControlImpl constructor(deviceTag: String, context: Context) :
        BlueToothMultiControlImpl(deviceTag, context), BlueToothCEControl {
    private var getElectricityCount: Int = 0
    private var count: Int = 0
    
    private var mBleCEDataChangeListener: BleCEDataChangeListener?=null
    /**
     * 连续获取节点电流
     */
    override fun getDeviceElectric(deviceWay: String) {
        var value = ByteArray(0)
        try {
            val b0 = Integer.parseInt("27", 16).toByte()
            val b1 = Integer.parseInt("04", 16).toByte()
            val b3 = Integer.parseInt("01", 16).toByte()
            val b4 = deviceWay.toByte()
            //            byte b5 = 40;//连接时间
            //            byte b6 = 20;//开设备时间
            //            byte b7 = BleUtils.getCheckCode(new byte[]{b0, b1, EQUIP_TYPE, b3, b4, b5, b6});
            //            value = new byte[]{b0, b1, EQUIP_TYPE, b3, b4, b5, b6, b7};
            val b5 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4))
            value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5)
        } catch (e: NumberFormatException) {
            Logger.e("getDeviceElectric 转换出现错误")
        }
        msg = "获取设备电流"
        write(value)
    }

    override fun openDevice(time: String?, deviceWay: String?, equipElectiic: String?) {
        var value = ByteArray(0)
        var equipElectiic = equipElectiic
        if (equipElectiic.isNullOrEmpty()) {
            equipElectiic = "2000"
        }
        try {
            val integerValue = equipElectiic!!.toInt()
            val b0 = Integer.parseInt("27", 16).toByte()
            val b1 = Integer.parseInt("10", 16).toByte()
            val b3 = Integer.parseInt("08", 16).toByte()
            if (deviceWay.isNullOrEmpty()) {
                return
            }
            val b4 = deviceWay?.toByte()//路数
//            byte b5 = 1;//开启
            val b5 = (time?.toLong()!! shr 8).toByte()//时间
            val b6 = time?.toByte()//时间
            val b7 = (integerValue shr 8).toByte()//电流
            val b8 = integerValue.toByte()//电流
            val b9 = (1000 shr 8).toByte()//过流
            val b10 = 1000.toByte()
            val b11 = Integer.parseInt("12", 16).toByte()
            val b12 = Integer.parseInt("34", 16).toByte()
            val b13 = Integer.parseInt("56", 16).toByte()
            val b14 = Integer.parseInt("78", 16).toByte()
            val b15 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4!!, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14))
            value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15)
        } catch (e: Exception) {
            Logger.e("打开操作继电器指令异常：" + e.toString())
        }
        msg = "发送开继电器指令"
        write(value)
    }

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
            val b3 = Integer.parseInt("03", 16).toByte()
            if (deviceWay.isNullOrEmpty()) {
                return
            }
            val b4 = deviceWay?.toByte()//路数
            val b5 = (time?.toLong()!! shr 8).toByte()//时间
            val b6 = time?.toByte()//时间
            val b7 = (integerValue shr 8).toByte()//电流
            val b8 = integerValue.toByte()//电流
            val b9 = (integerValue shr 8).toByte()//过流
            val b10 = integerValue.toByte()
            val b11 = Integer.parseInt("12", 16).toByte()
            val b12 = Integer.parseInt("34", 16).toByte()
            val b13 = Integer.parseInt("56", 16).toByte()
            val b14 = Integer.parseInt("78", 16).toByte()
            val b15 = BleUtils.getCheckCode(byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4!!, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14))
            value = byteArrayOf(b0, b1, EQUIP_TYPE, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15)
        } catch (e: Exception) {
            Logger.e("加时操作指令异常：" + e.toString())
        }
        msg = "加时操作"
        write(value)
    }

    override fun registerBroadcastReceiver(): BlueToothMultiControl {
        mReceiver = CEBroadcastReceiver()
        return super.registerBroadcastReceiver()
    }




    //蓝牙数据接收
    inner class CEBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_CHARACTER_CHANGED -> {
                    val receiveValue = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE)
                    Logger.w("接收蓝牙数据=${BleUtils.byteArrayToHexString(receiveValue)}")
                    receiveValue?.let {
                        when {
                        // 请求种子
                            it.size > 7 && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x01
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x04 -> {
                                mBleCEDataChangeListener?.requestSeedSuccess()
                                Logger.d("种子请求成功")
                                val seeds = byteArrayOf(it[4], it[5], it[6], it[7])
                                val keys = BleUtils.seedToKey(seeds, 2)
                                ThreadPoolUtils.execute {
                                    try {
                                        Thread.sleep(20)
                                        sendAndCheckSeed(keys)
                                    } catch (e: InterruptedException) {
                                        mBleCEDataChangeListener?.sendCheckSeedOnFailure()
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
                                            mBleCEDataChangeListener?.checkSeedSuccess()
                                        }
                                        it.toInt() == 0x01 -> {
                                            AppUtils.runOnUI(Runnable {
                                                mBleCEDataChangeListener?.checkSeedOnFailure("蓝牙通信失败，解密失败")
                                                Logger.d("解密失败")
                                            })

                                        }
                                        else -> {
                                        }
                                    }
                                }
                            }
                        // 获取设备节点信息
                            it.size > 7
                                    && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x03
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x04 -> {
                                it[4]?.apply {
                                    when {
                                        this.toInt() == 0x00 -> {
                                            it[5]?.let {
                                                //状态
                                                if (it.toInt() == 0x01) {
                                                    mBleCEDataChangeListener?.getDeviceInfoSuccess()
                                                }else{
                                                    mBleCEDataChangeListener?.getDeviceInfoFailure()
                                                }
                                            }
                                            //获取节点信息成功
                                            mBleCEDataChangeListener?.deviceCurrentState(false)
                                        }
                                        this.toInt() == 0x01 -> {
                                            //失败
                                            mBleCEDataChangeListener?.deviceCurrentState(true)

                                        }
                                    }
                                }
                                //上一次使用的电量
                                val totalElectricity = getMathElectricity(receiveValue[6], receiveValue[7])
                                Logger.d("上一次使用得电量${totalElectricity}wh")
                            }
                        //连续获取节点电流
                            it.size > 6
                                    && receiveValue[0].toInt() == 0x27
                                    && receiveValue[1].toInt() == 0x04
                                    && receiveValue[2] == EQUIP_TYPE
                                    && receiveValue[3].toInt() == 0x05->{
                                it[4]?.let {
                                    when{
                                        it.toInt()==0x00->{
                                            val totalVoltage = getMathElectricity(receiveValue[5], receiveValue[6])
                                            Logger.d("获取电压成功totalVoltage;${totalVoltage}V")
                                            val totalElectricity = getMathElectricity(receiveValue[7], receiveValue[8])
                                            Logger.d("设备电流:${totalElectricity}mA")
                                            mBleCEDataChangeListener?.showVoltageAndElectricity(totalVoltage,totalElectricity)

                                            when {
                                                totalElectricity < 10 -> {//电流小余10mA，没接负载
                                                    getElectricityCount++
                                                    when {
                                                        getElectricityCount >= 3 -> {
                                                            //获取电流三次小于阀值
                                                            getElectricityCount = 0
                                                            mBleCEDataChangeListener?.deviceisUse()

                                                        }
                                                        else -> {
                                                        }
                                                    }

                                                }
                                                else -> mBleCEDataChangeListener?.deviceisUse()
                                            }
                                        }
                                        else -> {
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
                                        this.toInt() == 0x00 ->
                                            mBleCEDataChangeListener?.openDeviceSuccess()

                                        this.toInt() == 0x01 ->
                                            mBleCEDataChangeListener?.openDeviceOnFailure()

                                    }
                                }

                            }
                        // 加时
                            it.size > 5
                                    && it[0].toInt() == 0x27
                                    && it[1].toInt() == 0x12
                                    && it[2] == EQUIP_TYPE
                                    && it[3].toInt() == 0x02 -> {
                                it[5]?.apply {
                                    when {
                                        this.toInt() == 0x00 -> if (mBleCEDataChangeListener != null) {
                                            mBleCEDataChangeListener?.addTimeSuccess()
                                        }
                                        this.toInt() == 0x01 -> if (mBleCEDataChangeListener != null) {
                                            mBleCEDataChangeListener?.addTimeOnFailure()
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

                                        mBleCEDataChangeListener?.deivceIsNotOnline("负载未接")
                                    }
                                } else {
//                                    //电压
//                                    val voltage = getMathVoltage(b1, b2)
//                                    //电流
//                                    val electricity = getMathCount(b3, b4)
//                                    mBleCEDataChangeListener?.showVoltageAndElectricity(voltage, electricity)

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

}


