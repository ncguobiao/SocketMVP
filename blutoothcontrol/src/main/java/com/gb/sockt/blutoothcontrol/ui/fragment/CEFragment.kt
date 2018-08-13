package com.gb.sockt.blutoothcontrol.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import com.example.baselibrary.base.BaseMvpFragment
import com.example.baselibrary.common.Constant
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.example.baselibrary.utils.databus.AmountUtils
import com.gb.sockt.blutoothcontrol.R
import com.gb.sockt.blutoothcontrol.injection.component.DaggerBluetoothComponent
import com.gb.sockt.blutoothcontrol.injection.module.BluetoothModule
import com.gb.sockt.blutoothcontrol.listener.BleConnectListener
import com.gb.sockt.blutoothcontrol.listener.BleDataChangeListener
import com.gb.sockt.blutoothcontrol.mvp.presenter.impl.BluetoothPresenterImpl
import com.gb.sockt.blutoothcontrol.mvp.view.BluetoothView
import com.gb.sockt.blutoothcontrol.ui.activity.BluetoothControlActivity
import com.gb.sockt.blutoothcontrol.ble.BlueToothControl
import com.inuker.bluetooth.library.BluetoothClient
import com.jakewharton.rxbinding2.view.RxView
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_ce.*
import org.jetbrains.anko.design.snackbar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

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

class CEFragment : BaseMvpFragment<BluetoothPresenterImpl>(), BluetoothView, View.OnClickListener {


    private lateinit var mContext: BluetoothControlActivity
    private lateinit var mClient: BluetoothClient
    private var hourArray: Array<out String>? = null
    private var selectHour: String? = null
    private val viewList = ArrayList<View>()
    @Inject
    @field:[Named("BluetoothCEControll")]
    lateinit var mBlueToothCEControlImpl: BlueToothControl

//
//    @Inject
//    @field:[Named("BluetoothMulitControl")]
//    lateinit var mBluetoothMulitControl: BlueToothControl

    companion object {
        fun newInstance(id: String): CEFragment {
            var args: Bundle = Bundle()
//            args.putString("todo_id_key", id)
            var editFragment: CEFragment = newInstance()
            editFragment.arguments = args
            return editFragment
        }

        fun newInstance(): CEFragment {
            return CEFragment()
        }

        private val mHandler: Handler = Handler()
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
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
//                .bluetoothModule(BluetoothModule(Constant.DEVICE_CE, activity))
                .bluetoothModule(BluetoothModule(Constant.DEVICE_CD, activity))
                .build()
                .inject(this)

        mPresenter.attachView(this)
    }


    @SuppressLint("ResourceType")
    override fun lazyLoad() {
        //时间数组
        hourArray = resources.getStringArray(R.array.hour_display)
        Logger.d("mac=${mContext.macAddress}")

//        Logger.d("mBlueToothMulitControlImpl=${mBluetoothMulitControl}")

        //设置macAddress
        mBlueToothCEControlImpl.setMAC(mContext.macAddress, object : BleConnectListener {
            override fun connectOnSuccess() {
                tv_state?.text = resources.getText(R.string.connect_success)
                tv_state?.setTextColor(resources.getColor(R.color.tv_color))
                activity_ll_control?.apply {
                    snackbar(activity_ll_control, R.string.connect_success)
                }
                if (ll_time_count_down != null && ll_time_count_down.visibility == View.VISIBLE) {//充电倒计时显示的情况下
                    if (ll_time_group != null) {
                        ll_time_group.visibility = View.GONE//隐藏时间选择
                    }
                    if (bt_start != null) {
                        bt_start.visibility = View.GONE//隐藏开始充电按钮
                    }
                } else {
                    if (ll_time_group != null) {
                        ll_time_group.visibility = View.VISIBLE
                    }
                    if (bt_start != null) {
                        bt_start.visibility = View.VISIBLE
                    }
                }
                if (iv_gif != null) {
                    iv_gif.setImageResource(R.drawable.success)
                }

                mHandler.postDelayed({
                    mBlueToothCEControlImpl.requestSeed()
                }, 500)
            }

            //蓝牙连接失败
            override fun connectOnFailure() {

                activity_ll_control?.apply {
                    snackbar(this, R.string.connect_failure)
                }
                if (ll_time_count_down != null && ll_time_count_down.visibility == View.GONE) {
//                    if (!operationSuccess) {
//                        if (isShowConnectDialog) {//不显示连接是否连接dialog
//                            if (isReconnect) {
//                                ThreadPoolUtils.execute(Runnable {
//                                    try {
//                                        Thread.sleep(1000)
//                                    } catch (e: InterruptedException) {
//                                        e.printStackTrace()
//                                    }
//
//                                    mBlueToothCEControlImpl.connectDeviceIfNeeded()
//                                })
//
//                            } else {
//                                if (mHandler != null) {
//                                    val msg = Message.obtain()
//                                    msg.what = SHOW_CONNECT_DIALOG
//                                    mHandler.sendMessage(msg)
//                                }
//                            }
//                        }
//                    }
                    if (iv_gif != null) {
                        iv_gif.setImageResource(R.drawable.connect_fail)
                    }
                    if (tv_state != null) {
                        tv_state.text = " 连接失败！"
                        tv_state.setTextColor(resources.getColor(R.color.connect_fail))
                    }

                } else {
                    if (iv_gif != null) {
                        iv_gif.setImageResource(R.drawable.success)
                    }
                    if (tv_state != null) {
                        tv_state.text = " 正在充电中..."
                        tv_state.setTextColor(resources.getColor(R.color.tv_color))
                    }
                }

            }

            //蓝牙连接成功
            override fun connectOnError() {
                showToast("该设备不支持蓝牙")
            }
            //注册广播
        }).registerBoradcastRecvier()
                //设置响应监听
                .setResponeListener(object : BleDataChangeListener {
                    override fun showVoltageAndElectricity(voltage: Int, electricity: Int) {

                    }

                    override fun seedSuccess() {
                        Logger.d("获取种子成功")
                    }

                    override fun seedOnFailure() {
                        Logger.d("获取种子失败")
                    }

                    override fun checkSeedSuccess() {
                        Logger.d("校验种子成功")
                    }

                    override fun checkSeedOnFailure(msg: String) {
                        showToast(msg)
                    }

                    override fun sendCheckSeedOnFailure() {
                        showToast("蓝牙通讯失败")
                    }

                    override fun deviceCurrentState(isBusy: Boolean) {
                        if (isBusy) {
                            Logger.d("设备正在使用中")
                        } else {
                            Logger.d("设备空闲")
                        }

                    }

                    override fun openDeviceSuccess() {
                        showToast("开启设备成功")
                    }

                    override fun openDeviceOnFailure() {
                        showToast("开启设备失败")
                    }

                    override fun addTimeSuccess() {
                        showToast("加时成功")
                    }

                    override fun addTimeOnFailure() {
                        showToast("加时失败")
                    }

                    override fun deivceIsNotOnline(msg: String) {
                        showToast(msg)
                    }
                })
                //连接蓝牙
                .connect()


    }

    override fun isFirstToUse() {
        showToast("once  to use")
    }


    override fun showFillMoneyDialog() {
        showToast("提示充钱")
    }

    override fun showAddTimeDialog(surplusTime: Long) {
        showToast("提示加时")
    }


    override fun initView() {
        mContext = activity as BluetoothControlActivity

        tv_device_num.text = mContext?.deviceName ?: ""
        tv_device_rate.text = mContext?.device_rate ?: ""

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

        bt_edit.onClick {
            changeSelectTimeColor(v = this.bt_edit)
        }

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
//                    mPresenter.requestSurplusTime(getUserID(), "77801", selectHour!!)
                })

    }


    override fun onClick(v: View?) {
        v?.let {
            when (it) {
                bt_time1 ->
                    selectHour = hourArray!![1]
                bt_time2 ->
                    selectHour = hourArray!![2]
                bt_time3 ->
                    selectHour = hourArray!![3]
                bt_time4 ->
                    selectHour = hourArray!![4]
                bt_time5 ->
                    selectHour = hourArray!![5]
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
        mBlueToothCEControlImpl.unregisterBoradcastRecvier()
        mBlueToothCEControlImpl.close()
    }
}