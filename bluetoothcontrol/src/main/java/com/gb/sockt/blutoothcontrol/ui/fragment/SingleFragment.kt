package com.gb.sockt.blutoothcontrol.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dpizarro.uipicker.library.picker.PickerUI
import com.dpizarro.uipicker.library.picker.PickerUISettings
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
import com.example.baselibrary.utils.ThreadPoolUtils
import com.gb.sockt.blutoothcontrol.ble.BluetoothConfig.options
import com.gb.sockt.blutoothcontrol.listener.BaseBLEDataListener
import com.gb.sockt.blutoothcontrol.mvp.presenter.impl.BluetoothSinglePresenterImpl
import com.gb.sockt.blutoothcontrol.mvp.view.BluetoothCommonView
import com.gb.sockt.blutoothcontrol.mvp.view.BluetoothSingleView
import com.gb.sockt.blutoothcontrol.uitls.BlueToothSingeControl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


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
class SingleFragment : BaseMvpFragment<BluetoothSinglePresenterImpl>(), BluetoothSingleView, View.OnClickListener {

    private lateinit var mContext: BluetoothControlActivity
    private var hourArray: Array<out String>? = null
    private var selectHour: String? = null
    private val viewList = ArrayList<View>()
    @Inject
    @field:[Named("BlueToothSingeControl")]
    lateinit var mBlueToothSingeControlImpl: BlueToothSingeControl

    private var deviceCurrentStatus: Boolean = false//设备当前状态
    //是否切换视图显示倒计时页面
    private var isSwitchView: Boolean = false

    private var isFirstToUse: Boolean = false

    private var currentPosition = -1

    private var remingTime: Long = 0L

    companion object {
        fun newInstance(id: String): SingleFragment {
            var args: Bundle = Bundle()
//            args.putString("todo_id_key", id)
            var editFragment: SingleFragment = newInstance()
            editFragment.arguments = args
            return editFragment
        }

        fun newInstance(): SingleFragment {
            return SingleFragment()
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
        mBlueToothSingeControlImpl.setMAC(mContext.macAddress, object : BleConnectListener {
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
                    mBlueToothSingeControlImpl?.readVoltage()
                }, 1500)
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
                    mContext.showBleConnectDialog(mBlueToothSingeControlImpl)
                }

            }

            //蓝牙连接成功
            override fun connectOnError() {
                showToast("该设备不支持蓝牙")
            }
            //注册广播
        }).registerBroadcastReceiver()
                //设置响应监听
                .setResponseListener(object : BaseBLEDataListener {
                    override fun showVoltageAndElectricity(voltage: Int, electricity: Int) {
                        tv_voltage.text = "${voltage}V"

                    }

                    override fun requestSeedSuccess() {
                        Logger.d("获取种子成功")
                    }

                    override fun seedOnFailure() {
                        Logger.d("获取种子失败")
                    }

                    override fun checkSeedSuccess() {
                    }

                    override fun checkSeedOnFailure(msg: String) {
                        showToast(msg)
                    }

                    override fun sendCheckSeedOnFailure() {
                        showToast("蓝牙通讯失败")
                    }

                    override fun openDeviceSuccess() {
                        Logger.d("蓝牙反馈开启设备成功")
                        if (isFirstToUse) {
                            Logger.e("第一次成功开启设备,通知服务器")
                            isFirstToUse = false
                            mPresenter.openSuccess2Service(getUserID(), deviceId = mContext?.deviceId!!, time = selectHour!!)
                        } else {
                            switchView(remingTime)
                        }
                        mHandler?.postDelayed({ mBlueToothSingeControlImpl?.close() }, 5000)

                    }

                    override fun openDeviceOnFailure() {
                        showToast("蓝牙反馈开启设备失败")
                    }


                })
                //连接蓝牙
                .connect()
        tv_state?.text = " 正在连接中..."
    }

    /**
     * 第一次使用设备
     */
    override fun isFirstToUse() {
        Logger.d("once  to use")
        mBlueToothSingeControlImpl?.requestSeed(selectHour!!)

        isFirstToUse = true
    }

    override fun openDeviceANDSwitchView(remingTime: Long) {
        this.remingTime = remingTime
        isFirstToUse = false
        mBlueToothSingeControlImpl?.requestSeed(selectHour!!)
    }


    override fun showFillMoneyDialog() {
        mContext?.showFillMoneyDialog()
    }


    override fun openSuccess2ServiceOnFailure(error: String) {
        showToast(error)

    }

    override fun openSuccess2ServiceOnSuccess(usedTime: String) {
        showToast("设备已开启,可以开始充电")
        //切换视图
        switchView(usedTime.toLong() * 3600)

    }

    /**
     * 显示隐藏相关视图
     * param 秒
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
        mPickerUI.visibility = View.GONE

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
        //自定义选择时间
        initSelectHourView()

        RxView.clicks(bt_start)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe({
                    if (selectHour.isNullOrEmpty()) {
                        showToast("请选择充电时间")
                        return@subscribe
                    }
                    if (!mBlueToothSingeControlImpl.getConnectState()) {
                        showToast("请先连接蓝牙")
                        return@subscribe
                    }
                    mPresenter.requestSurplusTime(getUserID(), mContext.deviceId!!, selectHour!!)
                })


    }

    /**
     * 自定义选择时间
     */
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
    private fun changeSelectTimeColor(v: View?) {
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
        mBlueToothSingeControlImpl.unregisterBroadcastReceiver()
        mBlueToothSingeControlImpl.close()
        mHandler?.removeCallbacksAndMessages(null)
    }
}