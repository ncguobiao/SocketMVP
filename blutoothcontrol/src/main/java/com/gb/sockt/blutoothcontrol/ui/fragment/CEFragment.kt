package com.gb.sockt.blutoothcontrol.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.example.baselibrary.base.BaseMvpFragment
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.Constant
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.example.baselibrary.utils.databus.AmountUtils
import com.gb.sockt.blutoothcontrol.R
import com.gb.sockt.blutoothcontrol.injection.component.DaggerBluetoothComponent
import com.gb.sockt.blutoothcontrol.injection.module.BluetoothModule
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.mvp.presenter.impl.BluetoothPresenterImpl
import com.gb.sockt.blutoothcontrol.ui.activity.BluetoothControlActivity
import com.jakewharton.rxbinding2.view.RxView
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_ce.*
import org.jetbrains.anko.design.snackbar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import com.example.baselibrary.utils.DateUtils
import com.example.baselibrary.utils.SpUtils
import com.gb.sockt.blutoothcontrol.ble.ce.BlueToothCEControl
import com.gb.sockt.blutoothcontrol.listener.BleCEDataChangeListener
import com.gb.sockt.blutoothcontrol.mvp.view.BluetoothMultiView
import com.mylhyl.circledialog.CircleDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers


/**
 * Created by guobiao on 2018/8/9.
 * /**
 *                             _ooOoo_
 *                            o8888888o
 *                            88" . "88
 *                            (| -_- |)
 *                            O\  =  /O
 *                         ____/`---'\____
 *                       .'  \\|     |//  `.
 *                      /  \\|||  :  |||//  \
 *                     /  _||||| -:- |||||-  \
 *                     |   | \\\  -  /// |   |
 *                     | \_|  ''\---/''  |   |
 *                     \  .-\__  `-`  ___/-. /
 *                   ___`. .'  /--.--\  `. . __
 *                ."" '<  `.___\_<|>_/___.'  >'"".
 *               | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *               \  \ `-.   \_ __\ /__ _/   .-` /  /
 *          ======`-.____`-.___\_____/___.-`____.-'======
 *                             `=---='
 *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                     佛祖保佑        永无BUG
 *            佛曰:
 *                   写字楼里写字间，写字间里程序员；
 *                   程序人员写程序，又拿程序换酒钱。
 *                   酒醒只在网上坐，酒醉还来网下眠；
 *                   酒醉酒醒日复日，网上网下年复年。
 *                   但愿老死电脑间，不愿鞠躬老板前；
 *                   奔驰宝马贵者趣，公交自行程序员。
 *                   别人笑我忒疯癫，我笑自己命太贱；
 *                   不见满街漂亮妹，哪个归得程序员？
*/
 */

/**
 * 多路设备
 */
class CEFragment : BaseMvpFragment<BluetoothPresenterImpl>(), BluetoothMultiView, View.OnClickListener {

    private lateinit var mContext: BluetoothControlActivity
    private var hourArray: Array<out String>? = null
    private var selectHour: String? = null
    private val viewList = ArrayList<View>()
    @Inject
    @field:[Named("BluetoothCEControl")]
    lateinit var mBlueToothCEControlImpl: BlueToothCEControl

    private var deviceCurrentStatus: Boolean = false//设备当前状态
    //是否切换视图显示倒计时页面
    private var isSwitchView: Boolean = false

    private var addTimeToDeviceDialog: DialogFragment? = null

    private var remainHour: Int = 0//剩余可用时间

    private var isAddTimeAndDeviceIsNotBusy: Boolean = false

    private var isFirstToUse: Boolean = false

    private var currentPosition =-1

    private val circleGetElectricRunnable = object : Runnable {
        override fun run() {
            if (mHandler != null) {
                mBlueToothCEControlImpl.getDeviceElectric(mContext?.deviceWay!!)
                mHandler?.postDelayed(this, 2000)
            }
        }
    }


    companion object {
        fun newInstance(id: String): CEFragment {
            var args: Bundle = Bundle()
            var editFragment: CEFragment = newInstance()
            editFragment.arguments = args
            return editFragment
        }

        fun newInstance(): CEFragment {
            return CEFragment()
        }

        private val mHandler: Handler = Handler()
    }


    override fun onError(error: String) {
        showToast(error)
    }

    override fun onDataIsNull() {
    }

    override fun getLayoutId(): Int = R.layout.fragment_ce

    override fun injectComponent() {
        DaggerBluetoothComponent.builder()
                .activityComponent(mActivityComponent)
                .bluetoothModule(BluetoothModule(Constant.DEVICE_CE, activity))
                .build()
                .inject(this)

        mPresenter.attachView(this)
    }

    @SuppressLint("ResourceType")
    override fun lazyLoad() {
        //时间数组
        hourArray = resources.getStringArray(R.array.hours)
        Logger.d("mac=${mContext.macAddress}")

        //设置macAddress
        mBlueToothCEControlImpl.setMAC(mContext.macAddress, object : BleConnectListener {
            override fun connectOnSuccess() {
                tv_state?.text = resources.getText(R.string.connect_success)
                tv_state?.setTextColor(resources.getColor(R.color.tv_color))
                activity_ll_control?.apply {
                    snackbar(activity_ll_control, R.string.connect_success)
                }
                if (isSwitchView) {//充电倒计时显示的情况下
                    ll_time_group?.visibility = View.GONE//隐藏时间选择
                    bt_start?.visibility = View.GONE//隐藏开始充电按钮
                } else {
                    ll_time_group?.visibility = View.VISIBLE
                    bt_start?.visibility = View.VISIBLE
                }

                iv_gif?.setImageResource(R.drawable.success)

                //延时请求种子
                mHandler?.postDelayed({
                    mBlueToothCEControlImpl.requestSeed()
                }, 500)
            }

            //蓝牙连接失败
            override fun connectOnFailure() {
                activity_ll_control?.apply {
                    snackbar(this, R.string.connect_failure)
                }
                if (isSwitchView) {
                    iv_gif?.setImageResource(R.drawable.success)
                    tv_state?.text = " 正在充电中..."
                    tv_state?.setTextColor(resources.getColor(R.color.tv_color))
                } else {
                    iv_gif?.setImageResource(R.drawable.connect_fail)
                    tv_state?.text = " 连接失败！"
                    tv_state?.setTextColor(resources.getColor(R.color.connect_fail))
                    //显示重连蓝牙弹窗
                    mContext.showBleConnectDialog(mBlueToothCEControlImpl)
                }

            }

            //蓝牙连接成功
            override fun connectOnError() {
                showToast("该设备不支持蓝牙")
            }
            //注册广播
        }).registerBroadcastReceiver()
                //设置响应监听
                .setResponseListener(object : BleCEDataChangeListener {

                    override fun requestSeedSuccess() {
                        Logger.d("获取种子成功")
                    }

                    override fun seedOnFailure() {
                        Logger.d("获取种子失败")
                    }

                    override fun checkSeedSuccess() {
                        mBlueToothCEControlImpl?.getBLEDeviceInfo(deviceWay = mContext?.deviceWay)
                    }

                    override fun checkSeedOnFailure(msg: String) {
                        showToast(msg)
                    }

                    override fun sendCheckSeedOnFailure() {
                        showToast("蓝牙通讯失败")
                    }

                    override fun deviceCurrentState(isBusy: Boolean) {
                        deviceCurrentStatus = isBusy
                        if (isBusy) {

                            Logger.d("设备正在使用中")
                        } else {
                            Logger.d("设备空闲")
                        }
                    }

                    override fun showVoltageAndElectricity(voltage: Int, electricity: Int) {
                        tv_voltage?.text = "${voltage / 100.0}V"
                        tv_electricity?.text = "${electricity}mA"
                    }

                    override fun getDeviceInfoSuccess() {
                        //循环获取电流
                        mHandler?.postDelayed(circleGetElectricRunnable, 2000)
                        Logger.d("循环获取设备电流")
                    }

                    override fun getDeviceInfoFailure() {
                        showToast("获取蓝牙设备节点信息失败")
                    }

                    override fun deviceisUse() {
                        battery_one?.text = resources.getString(R.string.battery_state_no)
                        battery_one?.setTextColor(resources.getColor(R.color.red_normal))
                        battery_two?.text = resources.getString(R.string.battery_notify_no)
                    }

                    override fun deviceisisNotUse() {
                        battery_one.text = resources.getString(R.string.battery_state_yes)
                        battery_one.setTextColor(resources.getColor(R.color.tv_color))
                        battery_two.text = resources.getString(R.string.battery_notify_yes)
                    }

                    override fun openDeviceSuccess() {
                        if (isFirstToUse) {
                            Logger.e("第一次成功开启设备,通知服务器")
                            isFirstToUse = false
                            mPresenter.openSuccess2Service(getUserID(), deviceId = mContext?.deviceId!!, time = selectHour!!)
                        } else {
                            Logger.e("非第一次成功开启设备，无需通知服务器")
                            deviceCurrentStatus = true
                        }
                        if (isAddTimeAndDeviceIsNotBusy) {
                            Logger.e("设备断电，加时开启设备,通知服务器")
                            isAddTimeAndDeviceIsNotBusy = false
                            mPresenter.addTimeSuccess2Service(getUserID(), deviceId = mContext?.deviceId!!, addTime = selectHour!!)
                        }
                        Logger.d("蓝牙反馈开启设备成功")

                    }

                    override fun openDeviceOnFailure() {
                        showToast("蓝牙反馈开启设备失败")
                    }

                    override fun addTimeSuccess() {
                        mPresenter.addTimeSuccess2Service(getUserID(), deviceId = mContext?.deviceId!!, addTime = selectHour!!)
                        Logger.e("蓝牙反馈加时成功,通知服务器")
                    }

                    override fun addTimeOnFailure() {
                        showToast("蓝牙反馈加时失败")
                    }

                    override fun deivceIsNotOnline(msg: String) {
                        showToast(msg)
                    }
                })
                //连接蓝牙
                .connect()

    }

    /**
     * 第一次使用设备
     */
    override fun isFirstToUse() {
        Logger.d("once  to use")
        if (deviceCurrentStatus) {
            //设备忙碌
            mContext?.showDeviceIsBusyDialog()
        } else {
            //设备空闲,开启设备(时间分钟)
            mBlueToothCEControlImpl?.openDevice((selectHour?.toInt()!! * 60).toString(), mContext?.deviceWay, equipElectiic = "")
        }

        isFirstToUse = true
    }

    override fun showFillMoneyDialog() {
        mContext?.showFillMoneyDialog()

    }

    /**
     * @time  创建时间 : 下午5:17
     * @author  : guobiao
     * @Description
     * @param 秒 剩余使用时间
     */
    override fun showAddTimeDialog(surplusTime: Long) {
        showAddTimeToDeviceDialog(surplusTime, selectHour!!)
        showToast("提示加时")

    }

    //加时可用
    override fun showAddTimeCanUse() {
        Logger.d("账户余额充足，可以加时,当前设备状态：${deviceCurrentStatus}")
        val selectMinute = selectHour?.toLong()!! * 60
        if (deviceCurrentStatus) {
            //忙碌（设备正在使用，传递本次加时时间）
            mBlueToothCEControlImpl.addTimeToBle(
                    selectMinute.toString(),
                    deviceWay = mContext?.deviceWay,
                    equipElectiic = ""
            )
        } else {
            //设备断电，开启设备传总时间（剩余时间+加时时间）
            val remainMinute = remainHour?.toLong() * 60 + selectMinute
            mBlueToothCEControlImpl.openDevice(
                    remainMinute.toString(),
                    deviceWay = mContext?.deviceWay,
                    equipElectiic = ""
            )
            isAddTimeAndDeviceIsNotBusy = true
        }

    }

    override fun openSuccess2ServiceOnFailure(error: String) {
        showToast(error)

    }

    override fun openSuccess2ServiceOnSuccess(usedTime: String) {
        showToast("设备已开启,可以开始充电")
        //切换视图
        switchView(usedTime.toLong() * 3600)

    }

    override fun addTime2ServiceOnFailure(error: String) {
        showToast(error)
    }

    override fun addTime2ServiceOnSuccess(addTime: String, reminTime: Long) {
        showToast("加时成功,可以开始充电")
        //切换视图
        switchView(reminTime)
    }

    /**
     * @time  创建时间 : 下午1:45
     * @author  : guobiao
     * @Description  加时弹窗
     * @param  remain 剩余时间
     */
    private fun showAddTimeToDeviceDialog(remain: Long, addTime: String) {
        val timeFormat = DateUtils.getTimeFormat(remain)
        addTimeToDeviceDialog = CircleDialog.Builder()
                .setTitle("加时操作!")
                .setTextColor(resources.getColor(com.example.baselibrary.R.color.Orange))
                .setText("还可继续使用:${timeFormat}\r\n确定加时:${addTime}小时？")
                .setNegative("继续使用") {
                    if (!deviceCurrentStatus) {
                        val remainMinute = DateUtils.getHour(remain)?.toLong()!! * 60
                        //设备断电下开启
                        mBlueToothCEControlImpl.openDevice(
                                remainMinute.toString(),
                                deviceWay = mContext?.deviceWay,
                                equipElectiic = ""
                        )
                    } else {
                        showToast("还有可用时间，可继续使用")
                    }
                    addTimeToDeviceDialog?.dismiss()
                }
                .setPositive("加时使用") {
                    addTimeToDeviceDialog?.dismiss()
                    if (mBlueToothCEControlImpl.getConnectState()) {
                        mPresenter.addTimeCanUse(getUserID(), deviceId = mContext?.deviceId!!, time = selectHour!!)
                    } else {
                        mContext?.showBleConnectDialog(mBlueToothCEControlImpl)
                    }

                }
                .setCancelable(true).show(fragmentManager)
    }

    /**
     * 操作蓝牙加时
     */
    private fun addTimeToDevice(addTime: String) {
        val addHour = addTime.toInt() * 60
        mBlueToothCEControlImpl?.addTimeToBle(
                addHour.toString()
                , deviceWay = mContext?.deviceWay,
                equipElectiic = ""
        )
    }


    private fun hideAddTimeToDeviceDialog() {
        addTimeToDeviceDialog?.dismiss()
        addTimeToDeviceDialog = null
    }

    /**
     * 显示隐藏相关视图
     */
    private fun switchView(usedTime: Long) {
        isSwitchView = true

        ll_time_group?.visibility = View.GONE
        iv_gif?.setImageResource(R.drawable.success)
        tv_state?.text = "正在充电中..."
        tv_state?.setTextColor(resources.getColor(R.color.tv_color))
        bt_start?.visibility = View.GONE
        ll_time_count_down?.visibility = View.VISIBLE

        val totalCacheTime = SpUtils.getInt(BaseApplication.getAppContext(), getUserID() + mContext?.deviceId)
        tv_time?.text = "本次充电时长${totalCacheTime}小时"
//        val options = RequestOptions()
//                .centerCrop()
//                //.placeholder(R.mipmap.ic_launcher_round)
//                .error(android.R.drawable.stat_notify_error)
//                .priority(Priority.HIGH)
//                //.skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

        iv_time_count_down?.let {
            Glide.with(activity)
                    .asGif()
                    .load("file:///android_asset/time.gif")
//                    .apply(options)
                    .into(iv_time_count_down)

        }
        //显示倒计时
        showTimeCountDown(usedTime)

    }


    /**
     * 显示倒计时
     * param 截止时间
    deadlineTime
     */
    private fun showTimeCountDown(deadlineTime: Long) {
        Observable.just(true)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    //此处可以更新ui

                    Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                            .take(deadlineTime)
                            //将递增数字替换成递减的倒计时数字
                            .map(object : Function<Long, Long> {
                                override fun apply(aLong: Long): Long {
                                    //                                Logger.d("aLong:$aLong")
                                    //                                Logger.d("map thread is :${Thread.currentThread().getName()} ")
                                    return deadlineTime - (aLong + 1)
                                }
                                //切换到 Android 的主线程。.observeOn(AndroidSchedulers.mainThread())
                            }).observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe { it ->
                    if (it == 0L) {
                        tv_time_count_down?.text = "已完成使用"
                    } else {
                        tv_time_count_down?.text = DateUtils.getTimeFormat(it)
                    }
                }
    }


    override fun initView() {
        mContext = activity as BluetoothControlActivity

        tv_device_num.text = mContext?.deviceName ?: ""
        tv_device_rate.text = "${mContext?.rate_yuan ?: ""}元/小时"

        bt_time1.onClick(this)
        bt_time2.onClick(this)
        bt_time3.onClick(this)
        bt_time4.onClick(this)
        bt_time5.onClick(this)
        viewList.add(bt_time1)
        viewList.add(bt_time2)
        viewList.add(bt_time3)
        viewList.add(bt_time4)
        viewList.add(bt_time5)
        viewList.add(bt_edit)

        initSelectHourView()

        RxView.clicks(bt_start)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe({
                    if (selectHour.isNullOrEmpty()) {
                        showToast("请选择充电时间")
                        return@subscribe
                    }
                    if (!mBlueToothCEControlImpl.getConnectState()) {
                        showToast("请先连接蓝牙")
                        return@subscribe
                    }
                    mPresenter.requestSurplusTime(getUserID(), mContext.deviceId!!, selectHour!!)
                })

    }

    private fun initSelectHourView() {
        val pickerUISettings = mContext?.pickerUISettings()
        mPickerUI.setOnClickItemPickerUIListener { _, position, valueResult ->
            currentPosition = position
            Logger.d("选择了position:$position")
            val hour = hourArray!![position]
            selectHour = hour
            //计算此次花费金额
            mathMoney(selectHour!!)
            showToast("选择了:${hour}小时")
        }
        bt_edit.onClick {
            mPickerUI.visibility = View.VISIBLE
            mPickerUI.setSettings(pickerUISettings)
            if (currentPosition == -1) {
                mPickerUI.slide()
            } else {
                mPickerUI.slide(currentPosition)
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it) {
                bt_time1 ->
                    selectHour = hourArray!![0]
                bt_time2 ->
                    selectHour = hourArray!![1]
                bt_time3 ->
                    selectHour = hourArray!![2]
                bt_time4 ->
                    selectHour = hourArray!![3]
                bt_time5 ->
                    selectHour = hourArray!![4]
            }
            Logger.d("选择时间:${selectHour}小时")
            changeSelectTimeColor(v)

        }
        //计算此次花费金额
        mathMoney(selectHour!!)
    }

    /**
     * 修改选择时间按钮颜色
     */
    private fun changeSelectTimeColor(v: View) {
        viewList.forEach {
            if (v == it) {
                v.background = resources.getDrawable(R.drawable.circle_btn_pressed_bg)
            } else {
                it.background = resources.getDrawable(R.drawable.circle_btn_normal_bg)
            }
        }

    }

    /**
     * 计算使用金额
     */
    private fun mathMoney(selectHour: String) {
        if (!TextUtils.isEmpty(mContext?.rate_yuan) && !TextUtils.isEmpty(selectHour)) {//判断时间和钱都不为空
            var multiply: String = "0.00"
            try {
                //计算金额
                multiply = AmountUtils.mathMoney(selectHour, mContext?.rate_yuan!!)
            } catch (e: Exception) {
                Logger.e("计算金额失败")
            }

            bt_start.text = "合计  ${multiply ?: "0.0"}  元 开始充电"
            bt_start.background = resources.getDrawable(R.drawable.bg_btn_pressed)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        hideAddTimeToDeviceDialog()
        mBlueToothCEControlImpl.unregisterBroadcastReceiver()
        mBlueToothCEControlImpl.close()
        mHandler?.removeCallbacksAndMessages(null)
    }
}