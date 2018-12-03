package com.gb.socket1.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.gb.socket1.R
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BluetoothTestListener
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_bluetooth_test_connect.*
import org.jetbrains.anko.toast


/**
 * Created by guobiao on 2018/11/15.
 * 测试蓝牙连接
 */


class BluetoothTestConnectActivity : BaseActivity() {

    private var startTime: Long = 0L

    private var sb: StringBuffer? = null
    private var mac: String? = null

    private val mBluetoothTestImpl by lazy {
        com.gb.sockt.blutoothcontrol.ble.test.BluetoothTestImpl(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_test_connect)
        mac = intent.getStringExtra("mac")

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
                val endTimeMillis = System.currentTimeMillis()
//                Logger.d("endTimeMillis:$endTimeMillis")
                val consumTime = endTimeMillis - startTime
//                Logger.d("consumTime:$consumTime")
                mConsumTime.text = "连接耗时：${consumTime}ms"
                mTvState.text = "连接成功"
                mTvState.setTextColor(Color.GREEN)
            }

        })


        mBluetoothTestImpl.setResponseListener(object : BluetoothTestListener {
            override fun onWriteFaile(msg: String) {
                mTvMessage?.text = msg
            }

            override fun onWriteSuccess(msg: String) {
                mTvMessage?.text = msg
            }

            override fun onError(data: String) {
                showToast(data)
                mTvReciver?.text = "错误数据：$data"
            }

            override fun onAdd(data: String) {
                mTvReciver?.text = "接收Add数据：$data"
            }

            override fun onLess(data: String) {
                mTvReciver?.text = "接收Less数据：$data"
            }

            override fun onFindAllMAC(byteArrayToHexString: String?) {

            }

        })

        mBluetoothTestImpl.registerBroadcastReceiver()

        mbtnConnect.onClick {
            if (!mBluetoothTestImpl?.getConnectState())
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

        mAdd.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            if (mBluetoothTestImpl?.getConnectState())
                mBluetoothTestImpl?.sendAdd() else showToast("请先连接设备")
        }
        mLess.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            if (mBluetoothTestImpl?.getConnectState())
                mBluetoothTestImpl?.sendLess() else showToast("请先连接设备")
        }
        find.onClick {
            mTvMessage?.text = ""
            mTvReciver?.text = ""
            if (mBluetoothTestImpl?.getConnectState())
                mBluetoothTestImpl?.findAllMAC() else showToast("请先连接设备")
        }

        mEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text = mEditText.text.toString().trim().toUpperCase()
                checkMac(text)

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

    }

    private fun checkMac(text: String) {
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

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothTestImpl?.unregisterBroadcastReceiver()
        mBluetoothTestImpl?.close()
        sb = null
    }

}





