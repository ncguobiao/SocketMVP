package com.gb.sockt.center.mvp.view

import com.example.baselibrary.base.IBaseView
import com.gb.sockt.center.data.domain.RechargeRecordsBean
import com.gb.sockt.center.data.domain.UseRecordBean

/**
 * Created by guobiao on 2018/8/23.
 */
interface RecordsView :IBaseView {

    fun showError(msg: String,errorCode:Int)
    fun showRechargeRecords(dataList: List<RechargeRecordsBean>)

    fun showUseRecords(dataBean: UseRecordBean)

    fun deleteRechargeRecordOnSuccess()
}