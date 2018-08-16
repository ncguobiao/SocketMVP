package com.gb.socket.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.baselibrary.base.BaseMvpActivity
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.glide.GlideImageLoader
import com.example.baselibrary.lbs.ILbsLayer
import com.example.baselibrary.lbs.LocationInfo
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.example.baselibrary.utils.SpUtils
import com.example.provider.router.RouterPath
import com.gb.socket.App
import com.gb.socket.R
import com.gb.socket.injection.module.MainModule
import com.gb.socket.mvp.presenter.MainPresenterImpl
import com.gb.socket.mvp.view.MainView
import com.gb.sockt.usercenter.injection.component.DaggerMainComponent
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import javax.inject.Inject
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.Constant
import com.example.baselibrary.utils.BleUtils
import com.example.baselibrary.utils.BluetoothClientManager
import com.example.baselibrary.zxing.app.CaptureActivity
import com.gb.socket.data.domain.DeviceInfo
import com.gb.socket.data.domain.RecordsMergeBean
import com.gb.socket.listener.OnRecyclerItemClickListener
//import com.gb.socket.ui.adapter.MainRecords1Adapter
import com.gb.socket.ui.adapter.MainRecordsAdapter
import com.tbruyelle.rxpermissions2.RxPermissions
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.*
/**
 * @date 创建时间：2018/8/15
 * @author :guobiao
 * @Description
 * @version
 */
@Route(path = RouterPath.Main.PATH_HOME)
class MainActivity : BaseMvpActivity<MainPresenterImpl>(), MainView {


    private val QRCODE = 18666
    //地图管理
    @Inject
    lateinit var mLbsLayer: ILbsLayer

    private var mExitTime: Long = 0
    private var deviceName: String? = null
    private var macAddress: String? = null
    private var way: String? = null
    private var resultLength: Int = 0
    private lateinit var rxPermissions: RxPermissions
    private var latitude: String? = null
    private var longitude: String? = null
    private var rate: String? = null
    private var deviceId: String? = null

    private val mList = ArrayList<RecordsMergeBean>()


    private lateinit var cycleTask: Runnable

    private val linearLayoutManager by lazy {
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    private val mAdapter by lazy {
        MainRecordsAdapter(this, mList, R.layout.item__main_records_layout)
//        MainRecords1Adapter(this,mList)
    }

    private val mHandler by lazy {
        LoopHandler(this)
    }


    override fun layoutId(): Int = R.layout.activity_main

    override fun onError(error: String) {
        toast(error)
    }

    override fun onDataIsNull() {
        toast("数据获取失败")
    }


    override fun initView(savedInstanceState: Bundle?) {
        //定位
        mLbsLayer.onCreate(savedInstanceState)
        mLbsLayer.setLocationChangeListener(object : ILbsLayer.CommonLocationChangeListener {
            override fun onLocation(locationInfo: LocationInfo?) {
                Logger.d("定位：${mLbsLayer.city} 定位时间${locationInfo?.date}")

                latitude = locationInfo?.latitude.toString()
                longitude = locationInfo?.longitude.toString()
                Logger.d("经纬度=$latitude:$longitude")
                SpUtils.put(App.getAppContext(), ConstantSP.USER_ADDRESS, mLbsLayer.city)
            }

            override fun onLocationChanged(locationInfo: LocationInfo?) {
//                Logger.d("onLocationChanged${locationInfo.toString()}")
            }
        })

        //扫码
        bt_scan.onClick {
            rxPermissions
                    .request(Manifest.permission.CAMERA)
                    .subscribe {
                        if (it) {
//                //申请的权限全部允许
                            Logger.d("扫码权限通过")
                            startActivityForResult(
                                    Intent(BaseApplication.getAppContext(), CaptureActivity::class.java),
//                                    Intent(BaseApplication.getAppContext(), ActivityScanerCode::class.java),
                                    QRCODE
                            )
                        } else {
//                //只要有一个权限被拒绝，就会执行
                            requestPremissionSetting("相机")
                        }
                    }


        }
        // Must be done during an initialization phase like onCreate
//        RxView.clicks(bt_scan)
//                .compose(rxPermissions.ensure(Manifest.permission.CAMERA))
//                .subscribe {
//                    if (it) toast("扫码权限通过") else toast("没有扫码权限")
//                }

        initRecyclerView()

    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        mRecyclerView?.layoutManager = linearLayoutManager
        mRecyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView?.itemAnimator = DefaultItemAnimator()
        mRecyclerView?.adapter = mAdapter
        mAdapter.setOnItemClick(object : OnRecyclerItemClickListener {
            override fun onItemClick(position: Int) {

            }

            override fun onClose(deviceName: String, useTemporaryId: String) {
                toast("关闭")

            }

            override fun onOpen(useTemporaryId: String) {
                toast("关闭")
            }

        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            QRCODE ->//扫码逻辑
            {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data?.getStringExtra("SCAN_RESULT")
                    if (checkQRResult(result)) return
                    macAddress = macAddress?.replace(":".toRegex(), "")
                    when (resultLength) {
                        2 ->
                            mPresenter.getDeviceInfo(macAddress, deviceName)
                        3 ->
                            mPresenter.getDeviceInfo(macAddress, "${deviceName}-${way}")
                        else -> mPresenter.getDeviceInfo(macAddress, "")
                    }
                }

            }
        }
    }

    //获取设备信息
    override fun getDeviceInfo(data: DeviceInfo) {

        if (macAddress != data.macAddress) {
            toast("二维码未入库或错误")
            return
        }
        deviceId = data.id
        if (deviceId == null || deviceId!!.isEmpty()) {
            toast("二维码未入库,设备id为空")
            return
        }
        rate = data.rate
        if (rate == null || rate!!.isEmpty()) {
            toast("二维码未入库,设备费率为空")
            return
        }
        Logger.d(longitude)
        if (longitude != null && latitude != null && longitude != "0.00" && latitude != "0.00") {
            //上传定位信息
            mPresenter.uploadLocation(getUserID(), deviceId!!, data.macAddress, longitude!!, latitude!!, "Android")
        }
        //服务器返回的macAddress格式化添加":"
        macAddress = BleUtils.formatAddress(macAddress)
        checkBLE()
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
        rxPermissions
                .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe {
                    if (it) {
//                //申请的权限全部允许
                        Logger.d("获取位置信息权限成功")
                        switchTo()
                    } else {
//                //只要有一个权限被拒绝，就会执行
                        requestPremissionSetting("获取位置信息")
                    }
                }

    }


    private fun switchTo() {
        //根据设备类型跳转不同页面
        deviceName?.toUpperCase()?.let {
            when {
                it.startsWith("CE") -> {
                    redirectTo(Constant.DEVICE_CE)
                }
                it.startsWith("CD") -> redirectTo(Constant.DEVICE_CD)
                else -> toast("仅支持CD或CE设备")
            }
        }

    }

    /**
     *  跳转页面
     */
    private fun redirectTo(deviceType: String) {
//        startActivity<BluetoothControlActivity>(
//                Constant.FRAGMENT_TAG to Constant.FRAGMENT_DEVICE_RECORDS,
//                Constant.DEVICE_NAME to deviceName + "-" + way,
//                Constant.DEVICE_MAC to macAddress,
//                Constant.DEVICE_WAY to way
//
//        )

        ARouter.getInstance().build(RouterPath.BLUETOOTH.PATH_BLUETOOTH_CONTROLL)
                .withTransition(com.gb.sockt.usercenter.R.anim.anim_in, com.gb.sockt.usercenter.R.anim.anim_out)
                .withString(Constant.DEVICE_NAME, deviceName + "-" + way)
                .withString(Constant.DEVICE_MAC, macAddress)
                .withString(Constant.DEVICE_WAY, way)
                .withString(Constant.DEVICE_TYPE, Constant.DEVICE_CE)
                .withString(Constant.DEVICE_RATE, rate)
                .withString(Constant.DEVICE_ID, deviceId)
                .navigation()

    }


    override fun uploadLocationSuccess() {
        Logger.d("位置信息上传成功")
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
            if (ckeckMac(result1)) return true
        } else {
            val str = result1.split("\\?".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (str.size != 2) {
                toast(R.string.qrcode_error)
                return true
            }
            result1 = str[1]
            if (ckeckMac(result1)) return true
        }
        return false
    }

    //检查二维码
    private fun ckeckMac(result: String): Boolean {
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
                way = strings[2]
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

    //显示banner
    override fun showBanner(images: List<String>) {
        banner.setImages(images)
                .setImageLoader(GlideImageLoader())
                .start()
    }

    override fun initComponent() {
        DaggerMainComponent.builder()
                .activityComponent(activityComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
        mPresenter.attachView(this)

    }

    override fun start() {
        //获取banner
        mPresenter.getBanner("No", "", "")
        //获取当前使用记录

//        mPresenter.getMergeData("No","","",getUserID(),"CDZ")
    }


    override fun initData() {
//        bugly更新
//        Beta.checkUpgrade()

        rxPermissions = RxPermissions(this)

        val mClient = BluetoothClientManager.getClient()
        mClient?.let {
            if (it.isBluetoothOpened) {
                Logger.d("蓝牙开启")
                checkLocationPremission()
            } else {
                //开启蓝牙
                if (it.openBluetooth()) {
                    //蓝牙开启状态，检查位置信息
                    toast("蓝牙开启")
                    checkLocationPremission()
                } else {
//                    longSnackbar(bt_scan,"请先到手机设置页面，打开蓝牙" )
                    toast("请先到手机设置页面，打开蓝牙")
                    requestOpenBluetooth()
                }
            }
        }

        mHandler?.obtainMessage(0)?.sendToTarget()

        //检查位置信息
//        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION
//                , Manifest.permission.ACCESS_COARSE_LOCATION)
//                .subscribe {
//                    if (it) {
//                        Logger.e("定位权限获取成功")
//                    } else {
//                        Logger.e("定位权限获取失败")
//                    }
//                }

    }

    /**
     * 检查定位权限
     */
    private fun checkLocationPremission() {
        rxPermissions
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


    override fun showRecords(list: ArrayList<RecordsMergeBean>?) {


        list?.let {
            if (it.size>0){
                rl_main_image.visibility = View.GONE
                mRecyclerView?.visibility = View.VISIBLE
//            mList?.apply {
//                this.clear()
//                this.addAll(it)
//            }
                mAdapter?.addData(it)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        //开始轮播
        banner.startAutoPlay()
        //获取使用记录
        mPresenter.getRecords(getUserID(), "CDZ")
    }

    override fun onResume() {
        super.onResume()
        mLbsLayer.onResume()
    }

    override fun onStop() {
        super.onStop()
        //结束轮播
        banner.stopAutoPlay()
    }

    override fun onPause() {
        super.onPause()
        mLbsLayer.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLbsLayer.onDestroy()
        mHandler?.let {
            it.removeCallbacksAndMessages(null)
        }


    }


    //退出
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
                finish()
            } else {
                mExitTime = System.currentTimeMillis()
                showToast("再按一次退出程序")
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    class LoopHandler constructor(activity: MainActivity) : Handler() {
        private var mWeakReference: WeakReference<MainActivity>? = WeakReference(activity)


        override fun handleMessage(msg: Message?) {
            val mContext = mWeakReference?.get()
            if (msg?.what == 0) {
                mContext?.mAdapter?.notifyDataSetChanged()
            }
            this.sendEmptyMessageDelayed(0, 1000)

        }
    }
}
