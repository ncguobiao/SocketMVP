package com.gb.sockt.center.ui.adapter

import android.content.Context
import android.text.TextUtils
import com.example.baselibrary.adapter.CommonAdapter
import com.example.baselibrary.adapter.viewHolder.ViewHolder
import com.example.baselibrary.utils.databus.AmountUtils
import com.gb.sockt.center.R
import com.gb.sockt.center.data.domain.UseRecordBean
import java.text.SimpleDateFormat

/**
 * Created by guobiao on 2018/8/14.
 */
class UseRecordsAdapter(context: Context, data: ArrayList<UseRecordBean.UseCDZRecordsListBean>, layoutId: Int) :
        CommonAdapter<UseRecordBean.UseCDZRecordsListBean>(context, data, layoutId) {

    private var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

//    private var onItemClickListener: OnRecyclerItemClickListener? = null

    override fun bindData(holder: ViewHolder, data: UseRecordBean.UseCDZRecordsListBean, position: Int) {
        setRecordItem(holder, data, position)
    }

    private fun setRecordItem(holder: ViewHolder, item: UseRecordBean.UseCDZRecordsListBean, position: Int) {

        holder.setText(R.id.start_time, item.startDate)
        holder.setText(R.id.tv_usestime, "充电时间：${item.duration} 小时")
        holder.setText(R.id.tv_device_name, "设备名：${item.deviceName}")


        //显示金额
        val money = item.money
        if (!TextUtils.isEmpty(money)) {
            try {
                val yuan = AmountUtils.changeF2Y(money)
                holder.setText(R.id.tv_money, "${yuan}元")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun addData(list: ArrayList<UseRecordBean.UseCDZRecordsListBean>?) {
        list?.let {
            this.mData.addAll(list)
        }

        this.notifyDataSetChanged()
    }

    //    fun setOnItemClick(onItemClickListener: OnRecyclerItemClickListener) {
//        this.onItemClickListener = onItemClickListener
//    }
    fun clearData() {
        mData.clear()
        notifyDataSetChanged()
    }
}