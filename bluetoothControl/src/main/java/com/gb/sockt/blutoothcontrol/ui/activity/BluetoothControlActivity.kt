package com.gb.sockt.blutoothcontrol.ui.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.Constant
import com.example.provider.router.RouterPath
import com.gb.sockt.blutoothcontrol.R
import com.gb.sockt.blutoothcontrol.ui.fragment.MultiWayFragment
import com.example.baselibrary.utils.BluetoothClientManager
import com.example.baselibrary.utils.databus.AmountUtils
import com.gb.sockt.blutoothcontrol.ble.BaseBLEControl
import com.gb.sockt.blutoothcontrol.ui.fragment.CEFragment
import com.mylhyl.circledialog.CircleDialog
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.toast
import android.os.Build
import com.dpizarro.uipicker.library.picker.PickerUISettings
import com.gb.sockt.blutoothcontrol.ui.fragment.SingleFragment
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*


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

    private var deviceIsBusyDialog: DialogFragment? = null
    private var fillMoneyDialog: DialogFragment? = null

    private var bleConnectDialog: DialogFragment? = null
    var macAddress: String? = null
    var deviceName: String? = null
    var deviceWay: String? = null
    var device_rate: String? = null
    var rate_yuan: String? = null
    var deviceId: String? = null
    var deviceType: String? = null

    private lateinit var fragmentManager: FragmentManager

    private var noteStateNotSavedMethod: Method? = null
    private val activityClassName = arrayOf("Activity", "FragmentActivity")

    private lateinit var fragmentMgr: Any
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blue_tooth_control)
        initData()
        checkBLE()
    }

    private fun initFragment() {
        deviceType?.let {
            when (it) {
                Constant.DEVICE_CE -> {
                    val beginTransaction = supportFragmentManager.beginTransaction()
                    val ceFragment = CEFragment.newInstance()
                    beginTransaction.replace(R.id.content_main, ceFragment, ceFragment.javaClass.simpleName)
                            //                .addToBackStack(ceFragment.javaClass.simpleName)
                            //                .addToBackStack(null)
                            .commit()
                }
                Constant.DEVICE_CD -> {
                    val beginTransaction = supportFragmentManager.beginTransaction()
                    val multiWayFragment = MultiWayFragment.newInstance()
                    beginTransaction.replace(R.id.content_main, multiWayFragment, multiWayFragment.javaClass.simpleName)
                            .commit()
                }
                Constant.DEVICE_SINGLE -> {
                    val beginTransaction = supportFragmentManager.beginTransaction()
                    val singleFragment = SingleFragment.newInstance()
                    beginTransaction.replace(R.id.content_main, singleFragment, singleFragment.javaClass.simpleName)
                            .commit()
                }
                else -> {
                    toast("设备类型异常")
                }
            }
        }


    }

    private fun initData() {
        fragmentManager = supportFragmentManager
        macAddress = intent.getSerializableExtra(Constant.DEVICE_MAC) as String
        deviceName = intent.getSerializableExtra(Constant.DEVICE_NAME) as String
        val way = intent.getSerializableExtra(Constant.DEVICE_WAY)
        if (way != null) {
            deviceWay = way as String
        }
        device_rate = intent.getSerializableExtra(Constant.DEVICE_RATE) as String
        deviceType = intent.getSerializableExtra(Constant.DEVICE_TYPE) as String
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
                Logger.e("蓝牙开启")
                checkLocationPermissionAndNavigation()

            } else {
                Logger.e("蓝牙开启未开启")
                //开启蓝牙
                if (it.openBluetooth()) {
                    //蓝牙开启状态，检查位置信息
                    checkLocationPermissionAndNavigation()
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
    private fun checkLocationPermissionAndNavigation() {
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe {
                    if (it) {
//                //申请的权限全部允许
                        Logger.d("获取位置信息权限成功---")
                        initFragment()
                    } else {
//                //只要有一个权限被拒绝，就会执行
                        requestPremissionSetting("获取位置信息")
                    }
                }

    }


    fun showDeviceIsBusyDialog() {
        deviceIsBusyDialog = CircleDialog.Builder()
                .setTitle("设备正忙!")
                .setTextColor(resources.getColor(com.example.baselibrary.R.color.red_normal))
                .setText("设备可能被其他人占用，请稍后尝试")
                .setPositive("确定") {
                    val intent: Intent
                    deviceIsBusyDialog?.dismiss()
                }
                .setCancelable(true).show(fragmentManager)

    }


    fun showFillMoneyDialog() {
        fillMoneyDialog = CircleDialog.Builder()
                .setTitle("账户余额不足!")
                .setTextColor(resources.getColor(com.example.baselibrary.R.color.red_normal))
                .setText("账户金额不足，是否要充值后再使用？")
                .setNegative("取消") { fillMoneyDialog?.dismiss() }
                .setPositive("确定") {

                    fillMoneyDialog?.dismiss()


                }
                .setCancelable(true).show(fragmentManager)
    }

    fun showBleConnectDialog(mBLEControl: BaseBLEControl) {
        dismissAllDialog()
        bleConnectDialog = CircleDialog.Builder()
                .setTitle("连接失败!")
                .setTextColor(resources.getColor(com.example.baselibrary.R.color.red_normal))
                .setText("确认是否需要尝试继续连接？")
                .setNegative("取消") { bleConnectDialog?.dismiss() }
                .setPositive("确定") {

                    bleConnectDialog?.dismiss()
                    //蓝牙重连
                    mBLEControl.deviceIfNeeded()

                }
                .setCancelable(true).show(fragmentManager)


    }


    fun hideDeviceIsBusyDialog() {
        if (deviceIsBusyDialog != null && deviceIsBusyDialog!!.isVisible) {
            deviceIsBusyDialog?.dismissAllowingStateLoss()
        }
        deviceIsBusyDialog = null

    }

    fun hideBleConnectDialog() {
        if (bleConnectDialog != null && bleConnectDialog!!.isVisible) {
            bleConnectDialog?.dismissAllowingStateLoss()
        }
        bleConnectDialog = null
    }


    fun hideFillMomeyDialog() {
        if (fillMoneyDialog != null && fillMoneyDialog!!.isVisible) {
            fillMoneyDialog?.dismissAllowingStateLoss()
        }
        fillMoneyDialog = null
    }

    fun dismissAllDialog() {
        hideBleConnectDialog()
        hideDeviceIsBusyDialog()
        hideFillMomeyDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissAllDialog()

    }

    /**
     * pickerUI设置
     */
    fun pickerUISettings(): PickerUISettings? {
        val showTime = resources.getStringArray(R.array.show_time)?.toMutableList()
        return PickerUISettings.Builder().withItems(showTime)
                .withBackgroundColor(getRandomColor())
                .withAutoDismiss(true)
                .withItemsClickables(true)
                .withUseBlur(false)
                .build()
    }

    /**
     * 随机颜色
     */
    private fun getRandomColor(): Int {
        // generate the random integers for r, g and b value
        val rand = Random()
        val r = rand.nextInt(255)
        val g = rand.nextInt(255)
        val b = rand.nextInt(255)
        return Color.rgb(r, g, b)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        invokeFragmentManagerNoteStateNotSaved()
    }


    override fun onBackPressed() {
        if (!isFinishing)
            super.onBackPressed()
    }

    private fun invokeFragmentManagerNoteStateNotSaved() {
//java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return
        }
        try {
            if (noteStateNotSavedMethod != null && fragmentMgr != null) {
                noteStateNotSavedMethod!!.invoke(fragmentMgr)
                return
            }
            var cls: Class<*> = javaClass
            do {
                cls = cls.superclass
            } while (!(activityClassName[0].equals(cls.simpleName) || activityClassName[1].equals(cls.simpleName)))

            val fragmentMgrField = prepareField(cls, "mFragments")
            if (fragmentMgrField != null) {
                fragmentMgr = fragmentMgrField!!.get(this)
                noteStateNotSavedMethod = getDeclaredMethod(fragmentMgr, "noteStateNotSaved")
                noteStateNotSavedMethod?.invoke(fragmentMgr)
            }

        } catch (ex: Exception) {
        }
    }


    @Throws(NoSuchFieldException::class)
    private fun prepareField(c: Class<*>?, fieldName: String): Field {
        var c = c
        while (c != null) {
            try {
                val f = c.getDeclaredField(fieldName)
                f.isAccessible = true
                return f
            } finally {
                c = c.superclass
            }
        }
        throw NoSuchFieldException()
    }

    private fun getDeclaredMethod(`object`: Any, methodName: String, vararg parameterTypes: Class<*>): Method? {
        var method: Method? = null
        var clazz: Class<*> = `object`.javaClass
        while (clazz != Any::class.java) {
            try {
                method = clazz.getDeclaredMethod(methodName, *parameterTypes)
                return method
            } catch (e: Exception) {
            }

            clazz = clazz.superclass
        }
        return null
    }

}
