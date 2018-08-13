package com.gb.sockt.blutoothcontrol.ui.activity

import android.Manifest
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.Constant
import com.example.provider.router.RouterPath
import com.gb.sockt.blutoothcontrol.R
import com.gb.sockt.blutoothcontrol.ui.fragment.CEFragment
import com.example.baselibrary.utils.BluetoothClientManager
import com.example.baselibrary.utils.databus.AmountUtils
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.toast


// 在支持路由的页面上添加注解(必选)
// 这里的路径需要注意的是至少需要有两级，/xx/x
@Route(path = RouterPath.BLUETOOTH.PATH_BLUETOOTH_CONTROLL)
class BluetoothControlActivity : BaseActivity() {
//    val deviceName: String? by extraDelegate(extra = Constant.DEVICE_NAME)
//    val macAddress: String? by extraDelegate(extra = Constant.DEVICE_MAC)
//    private val mWay: String? by extraDelegate(extra = Constant.DEVICE_WAY)

//
//   @Autowired(name = Constant.DEVICE_MAC)
//    @JvmField
//    var macAddress: String ?=null


    var macAddress: String? = null
    var deviceName: String? = null
    var deviceWay: String? = null
    var device_rate: String? = null
    var rate_yuan: String? = null
    var deviceId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blue_tooth_control)
        initData()
        checkBLE()
        val ceFragment = CEFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_main, ceFragment, ceFragment.javaClass.simpleName)
                .addToBackStack(ceFragment.javaClass.simpleName)
                .commit()
    }


    private fun initData() {

        macAddress = intent.getSerializableExtra(Constant.DEVICE_MAC) as String
        deviceName = intent.getSerializableExtra(Constant.DEVICE_NAME) as String
        deviceWay = intent.getSerializableExtra(Constant.DEVICE_WAY) as String
        device_rate = intent.getSerializableExtra(Constant.DEVICE_RATE) as String
        deviceId = intent.getSerializableExtra(Constant.DEVICE_ID) as String
        try {
            rate_yuan = AmountUtils.changeF2Y(java.lang.Long.parseLong(device_rate))
        } catch (e: Exception) {
            toast("费率金额转换异常")
            Logger.d("金额转换异常")
        }


        Logger.e("deviceName=${deviceName},macAddress=${macAddress},deviceWay=${deviceWay}")
    }

    /**
     * 检查蓝牙
     */
    private fun checkBLE() {
        val mClient = BluetoothClientManager.getClient()
        mClient?.let {
            if (it.isBluetoothOpened) {
                //蓝牙开启状态，检查位置信息
                checkLocationPremissionAndNavigation()
            } else {
                //开启蓝牙
                if (it.openBluetooth()) {
                    //蓝牙开启状态，检查位置信息
                    checkLocationPremissionAndNavigation()
                } else {
//                    longSnackbar(bt_scan,"请先到手机设置页面，打开蓝牙" )
                    toast("请先到手机设置页面，打开蓝牙")
                    requestOpenBluetooth()
                }
            }
        }

    }

    /**
     * 校验位置信息权限
     */
    private fun checkLocationPremissionAndNavigation() {
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe {
                    if (it) {
//                //申请的权限全部允许
                        Logger.d("获取位置信息权限成功")
                    } else {
//                //只要有一个权限被拒绝，就会执行
                        requestPremissionSetting("获取位置信息")
                    }
                }

    }

}
