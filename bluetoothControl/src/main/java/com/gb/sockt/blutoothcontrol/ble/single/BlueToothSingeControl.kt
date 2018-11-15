package com.gb.sockt.blutoothcontrol.uitls

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.baselibrary.common.Constant
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.BluetoothClientManager
import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.ble.multi.BlueToothMultiControl
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BleMultiDataChangeListener
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.Constants.*
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.inuker.bluetooth.library.utils.BluetoothUtils
import com.inuker.bluetooth.library.utils.BluetoothUtils.registerReceiver
import com.orhanobut.logger.Logger
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by guobiao on 2018/8/9.
 */
interface BlueToothSingeControl : BaseBLEControl {


    fun readVoltage()

    fun openDevice(keys: ByteArray)

    fun requestSeed(time: String)
}


