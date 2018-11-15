package com.gb.sockt.center.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baselibrary.base.BaseFragment
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.gb.sockt.center.R
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import kotlinx.android.synthetic.main.fragment_comm_records.*

/**
 * Created by guobiao on 2018/11/14.
 */
class CommRecordsFragment : BaseFragment() {


    companion object {

        fun newInstance(): CommRecordsFragment {
            return CommRecordsFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_comm_records, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val recordType = activity.intent.extras.getString(ConstantSP.RECORD_TYPE)

        if (ConstantSP.RECHARGE_RECORDS == recordType) {

            val rechargeRecordsFragment = RechargeRecordsFragment.newInstance()
            childFragmentManager.beginTransaction()
                    .replace(R.id.comm_container, rechargeRecordsFragment).commit()
        } else {

            val useRecordsFragment = UseRecordsFragment.newInstance()
            childFragmentManager.beginTransaction()
                    .replace(R.id.comm_container, useRecordsFragment).commit()
        }



    }

}