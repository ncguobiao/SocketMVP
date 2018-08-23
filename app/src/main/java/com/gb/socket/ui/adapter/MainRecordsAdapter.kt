package com.gb.socket.ui.adapter

import android.content.Context
import android.view.View
import android.widget.Button
import com.example.baselibrary.adapter.CommonAdapter
import com.example.baselibrary.adapter.viewHolder.ViewHolder
import com.example.baselibrary.onClick
import com.example.baselibrary.utils.DateUtils
import com.gb.socket.R
import com.gb.socket.data.domain.RecordsMergeBean
import com.gb.socket.listener.OnRecyclerItemClickListener
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat

/**
 * Created by guobiao on 2018/8/14.
 */
class MainRecordsAdapter(context: Context, data: ArrayList<RecordsMergeBean>, layoutId: Int) :
        CommonAdapter<RecordsMergeBean>(context, data, layoutId) {

    private var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private var onItemClickListener: OnRecyclerItemClickListener? = null

    override fun bindData(holder: ViewHolder, data: RecordsMergeBean, position: Int) {
        setRecordItem(holder, data, position)
    }

    private fun setRecordItem(holder: ViewHolder, data: RecordsMergeBean, position: Int) {
        if (null != data.records2G) {//2G设备
            holder.setViewVisibility(R.id.btn_close, View.VISIBLE)
            holder.setViewVisibility(R.id.tv_time_count_down, View.GONE)
            holder.setViewVisibility(R.id.ll_container, View.VISIBLE)
            val startTime = data?.records2G?.useTemporary?.startDate//开始使用时间
            val useTime = System.currentTimeMillis() - format.parse(startTime).time//已经使用
            val usedTime = DateUtils.getTimeFormat(useTime / 1000)
            holder.setText(R.id.tv_time, "已经使用：$usedTime")

            val deviceName = data?.records2G?.deviceCz?.deviceName
            holder.setText(R.id.tv_device_num, deviceName ?: "")
            val useTemporaryId = data?.records2G?.deviceCz?.deviceGsmId

            val endDate = data?.records2G.useTemporary.endDate
            //显示关闭或支付按钮（endDate为空串就显示关闭按钮，否则显示支付按钮）
            if (endDate.isNullOrEmpty()){
                holder.setViewVisibility(R.id.btn_close, View.VISIBLE)
                holder.setViewVisibility(R.id.btn_open, View.GONE)
            }else{
                holder.setViewVisibility(R.id.btn_close, View.GONE)
                holder.setViewVisibility(R.id.btn_open, View.VISIBLE)

            }
            //关闭
            holder.getView<Button>(R.id.btn_close).onClick {
                onItemClickListener?.onClose(deviceName!!, useTemporaryId!!)
            }

            holder.getView<Button>(R.id.btn_open).onClick {
                onItemClickListener?.onOpen( useTemporaryId!!)
            }
            holder.itemView.onClick {
                onItemClickListener?.onItemClick(position)
            }
        } else {
            holder.setViewVisibility(R.id.btn_close, View.GONE)
            holder.setViewVisibility(R.id.tv_time_count_down, View.VISIBLE)
            holder.setViewVisibility(R.id.ll_container, View.GONE)
            val deviceId = data.recordsBean?.deviceId
            holder.setText(R.id.tv_device_num, deviceId ?: "")

            val endTime = data.recordsBean?.endDate
            //获取使用时间戳
            val useTime = format.parse(endTime).time - format.parse(data.recordsBean?.startDate).time

            holder.setText(R.id.tv_time, "充电时长：${DateUtils.getHour(useTime / 1000)}小时")
            //剩余使用时间
            val remainTime = format.parse(endTime).time - System.currentTimeMillis()//使用时间差
            if (remainTime > 1000) {
                val timeFormat = DateUtils.getTimeFormat(remainTime / 1000)
                holder.setText(R.id.tv_time_count_down, timeFormat)
                holder.setViewVisibility(R.id.tv_remain_time, View.VISIBLE)
            } else {
                holder.setViewVisibility(R.id.tv_remain_time, View.INVISIBLE)
                holder.setText(R.id.tv_time_count_down, "充电已完成")
            }
        }
    }

    fun addData(list: ArrayList<RecordsMergeBean>?) {
        list?.let {
            this.mData.clear()
            this.mData.addAll(list)
        }

        this.notifyDataSetChanged()
    }

    fun setOnItemClick(onItemClickListener: OnRecyclerItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

}