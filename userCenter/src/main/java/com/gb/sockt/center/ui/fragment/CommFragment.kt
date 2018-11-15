package com.gb.sockt.center.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baselibrary.act
import com.example.baselibrary.base.BaseFragment
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.baselibrary.showToast
import com.example.baselibrary.utils.pay.PayPalHelper
import com.gb.sockt.center.R
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import kotlinx.android.synthetic.main.fragment_comm.*
import kotlinx.android.synthetic.main.fragment_comm_records.*

/**
 * Created by guobiao on 2018/11/14.
 */
class CommFragment : BaseFragment() {


    companion object {
        fun newInstance(): CommFragment {
            return CommFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_comm, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        PayPalHelper.getInstance().startPayPalService(activity)
        val recordType = activity.intent.extras.getString(ConstantSP.RECORD_TYPE)

        btn_pay.onClick {
            PayPalHelper.getInstance().doPayPalPay(activity)
        }
    }

    override fun onDestroy() {
        PayPalHelper.getInstance().stopPayPalService(activity)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PayPalHelper.getInstance().confirmPayResult(context, requestCode, resultCode, data, object :PayPalHelper.DoResult {
            override fun confirmSuccess(id: String?) {
                showToast("支付成功" + id);
            }

            override fun confirmNetWorkError() {
                showToast("支付失败")
            }

            override fun customerCanceled() {
                showToast("支付取消")
            }

            override fun confirmFuturePayment() {
            }

            override fun invalidPaymentConfiguration() {
            }


        })



    }

}