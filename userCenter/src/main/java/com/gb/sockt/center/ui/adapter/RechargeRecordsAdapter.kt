package com.gb.sockt.center.ui.adapter

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.baselibrary.adapter.CommonAdapter
import com.example.baselibrary.adapter.viewHolder.ViewHolder
import com.example.baselibrary.onClick
import com.example.baselibrary.utils.AppUtils
import com.example.baselibrary.utils.DateUtils
import com.example.baselibrary.utils.databus.AmountUtils
import com.gb.sockt.center.R
import com.gb.sockt.center.data.domain.RechargeRecordsBean
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat

/**
 * Created by guobiao on 2018/8/14.
 */
class RechargeRecordsAdapter(context: Context, data: ArrayList<RechargeRecordsBean>, layoutId: Int) :
        CommonAdapter<RechargeRecordsBean>(context, data, layoutId) {

    private var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

//    private var onItemClickListener: OnRecyclerItemClickListener? = null

    override fun bindData(holder: ViewHolder, data: RechargeRecordsBean, position: Int) {
        setRecordItem(holder, data, position)
    }

    private fun setRecordItem(holder: ViewHolder, item: RechargeRecordsBean, position: Int) {
        holder.setText(R.id.creat_time, item.createDate ?: "")
        holder.setText(R.id.order_body, item.subject ?: "")
        val payType = item.payType
        if ("0" == payType) {
            holder.setText(R.id.tv_pay_type, "未支付")
            holder.getView<TextView>(R.id.tv_pay_type).setTextColor(AppUtils.getContext().resources.getColor(R.color.Orange))
        } else if ("1" == payType) {
            holder.setText(R.id.tv_pay_type, "已支付")
            holder.getView<TextView>(R.id.tv_pay_type).setTextColor(AppUtils.getContext().resources.getColor(R.color.tv_color))
        }
        var yuan = "0.00"
        try {
            yuan = AmountUtils.changeF2Y(item.menoy)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.setText(R.id.order_money, "${yuan}元")
    }

    fun addData(list: ArrayList<RechargeRecordsBean>?) {
        list?.let {
            this.mData.clear()
            this.mData.addAll(list)
        }

        this.notifyDataSetChanged()
    }

//    fun setOnItemClick(onItemClickListener: OnRecyclerItemClickListener) {
//        this.onItemClickListener = onItemClickListener
//    }

    fun  clearData(){
        mData.clear()
        notifyDataSetChanged()
    }
}