package com.gb.socket1.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.onClick
import com.example.baselibrary.zxing.app.CaptureActivity
import com.gb.socket1.R
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_scan_qr_code.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Created by guobiao on 2018/12/3.
 */
class ScanQRCodeActivity : BaseActivity() {
    private var resultLength: Int = 0
    private var macAddress: String?= null
    private var deviceName: String?= null
    private lateinit var rxPermissions: RxPermissions
    private val QRCODE = 18666
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code)

        rxPermissions = RxPermissions(this)
        /*
        * 打开默认二维码扫描界面
        */
        mScan.onClick {
            rxPermissions
                    .request(Manifest.permission.CAMERA)
                    .subscribe {
                        if (it) {
//                //申请的权限全部允许
                            Logger.d("扫码权限通过")
                            startActivityForResult(
                                    Intent(BaseApplication.getApplication(), CaptureActivity::class.java),
//                                    Intent(BaseApplication.getApplication(), ActivityScanerCode::class.java),
                                    QRCODE
                            )
                        } else {
//                //只要有一个权限被拒绝，就会执行
                            requestPremissionSetting("相机")
                        }
                    }
        }

        button1.onClick {
            checkBLE()
        }


    }

    override fun doSomethingWithBluetoothOpened() {
        startActivity<BluetoothKeyActivity>()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * 处理二维码扫描结果
         */
        when (requestCode) {
            QRCODE ->//扫码逻辑
            {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data?.getStringExtra("SCAN_RESULT")
                    if (checkQRResult(result)) return
                    startActivity<BluetoothTestConnectActivity>("mac" to macAddress)

                }

            }
        }
    }


    /**
     * 二维码检查
     */
    private fun checkQRResult(result: String?): Boolean {
        var result1 = result
        if (result1.isNullOrEmpty()) {
            toast(R.string.qrcode_error)
            return true
        }
        if (!result1!!.contains("?")) {
            if (checkMac(result1)) return true
        } else {
            val str = result1.split("\\?".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (str.size != 2) {
                toast(R.string.qrcode_error)
                return true
            }
            result1 = str[1]
            if (checkMac(result1)) return true
        }
        return false
    }



    //检查二维码
    private fun checkMac(result: String): Boolean {
        if (result.contains("-")) {
            val strings = result.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            resultLength = strings.size
            if (resultLength == 2) {
//                LogUtil.e("checkMac two param")
                macAddress = strings[0]
                deviceName = strings[1]
                if (checkMac(macAddress, deviceName)) return true
            } else if (resultLength == 3) {
                macAddress = strings[0]
                deviceName = strings[1]
                val way = strings[2]
                if (checkMac(macAddress, deviceName, way)) return true
            } else {
                toast(R.string.qrcode_error)
                return true
            }
            return false
        } else {
            toast(R.string.qrcode_error)
            return true
        }

    }

    private fun checkMac(macAddress: String?, deviceName: String?): Boolean {
        if (macAddress.isNullOrEmpty() || deviceName.isNullOrEmpty()) {
            toast(R.string.qrcode_error)
            return true
        }
        if (!macAddress!!.contains(":")) {
            toast(R.string.qrcode_error)
            return true
        }
        if (macAddress.length != 17) {
            toast(R.string.qrcode_error)
            return true
        }
        val split = this.macAddress?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        if (split?.size != 6) {
            Logger.e("split:${split?.size}")
            toast(R.string.qrcode_error)
            return true
        }
        return false
    }

    private fun checkMac(macAddress: String?, deviceName: String?, way: String?): Boolean {
        if (macAddress.isNullOrEmpty() || deviceName.isNullOrEmpty() || way.isNullOrEmpty()) {
            toast(R.string.qrcode_error)
            return true
        }
        if (!macAddress!!.contains(":")) {
            toast(R.string.qrcode_error)
            return true
        }
        if (macAddress.length != 17) {
            toast(R.string.qrcode_error)
            return true
        }
        val split = this.macAddress?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        if (split?.size != 6) {
            toast(R.string.qrcode_error)
            return true
        }
        return false
    }

}