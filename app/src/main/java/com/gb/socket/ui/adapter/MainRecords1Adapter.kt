//package com.gb.socket.ui.adapter
//
//import android.content.Context
//import android.view.View
//import android.widget.Button
//import com.example.baselibrary.adapter.CommonAdapter
//import com.example.baselibrary.adapter.viewHolder.MultipleType
//import com.example.baselibrary.adapter.viewHolder.ViewHolder
//import com.example.baselibrary.onClick
//import com.example.baselibrary.utils.DateUtils
//import com.gb.socket.R
//import com.gb.socket.data.domain.RecordsMergeBean
//import com.gb.socket.listener.OnRecyclerItemClickListener
//import com.orhanobut.logger.Logger
//import java.text.SimpleDateFormat
//
///**
// * Created by guobiao on 2018/8/14.
// */
//class MainRecords1Adapter(context: Context, data: ArrayList<RecordsMergeBean>) :
//        CommonAdapter<RecordsMergeBean>(context, data,object :MultipleType<RecordsMergeBean>{
//            override fun getLayoutId(item: RecordsMergeBean, position: Int): Int {
//                return when (position) {
//                    0 ->
//                        R.layout.item_first_header
//                    else -> R.layout.item__main_records_layout
//                }
//            }
//
//        }) {
//
//    private var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//
//    private var onItemClickListener: OnRecyclerItemClickListener? = null
//
//    override fun bindData(holder: ViewHolder, data: RecordsMergeBean, position: Int) {
//
//        Logger.d("bindData")
//        if (position == 0 )return
//        setRecordItem(holder, data, position)
//    }
//
//    private fun setRecordItem(holder: ViewHolder, data: RecordsMergeBean, position: Int) {
//
//        if (null != data.records2G) {
//            holder.setViewVisibility(R.id.btn_close, View.VISIBLE)
//            holder.setViewVisibility(R.id.tv_time_count_down, View.GONE)
//
//            val startTime = data?.records2G?.useTemporary?.startDate//开始使用时间
//            val useTime = System.currentTimeMillis() - format.parse(startTime).time//已经使用
//            val usedTime = DateUtils.getTimeFormat(useTime)
//            holder.setText(R.id.tv_time, "已经使用：$usedTime")
//
//            val deviceName = data?.records2G?.deviceCz?.deviceName
//            holder.setText(R.id.tv_device_num, deviceName?:"")
//            val useTemporaryId = data?.records2G?.deviceCz?.deviceGsmId
//
//            //关闭
//            holder.getView<Button>(R.id.btn_close).onClick {
//                onItemClickListener?.onClose(deviceName!!, useTemporaryId!!)
//            }
//            holder.itemView.onClick {
//                onItemClickListener?.onItemClick(position)
//            }
//        } else {
//            holder.setViewVisibility(R.id.btn_close, View.GONE)
//            holder.setViewVisibility(R.id.tv_time_count_down, View.VISIBLE)
//
//            val deviceId = data.recordsBean?.deviceId
//            holder.setText(R.id.tv_device_num, deviceId?:"")
//
//            val endTime = data.recordsBean?.endDate
//            //获取使用时间戳
//            val useTime = format.parse(endTime).time - format.parse(data.recordsBean?.startDate).time
//
//
//            holder.setText(R.id.tv_time, "充电时长：${ DateUtils.getHour(useTime/1000)}")
//            //剩余使用时间
//            val remainTime = format.parse(endTime).time - System.currentTimeMillis()//使用时间差
//            if (remainTime > 1000) {
//                val timeFormat = DateUtils.getTimeFormat(remainTime/1000)
//                holder.setText(R.id.tv_time_count_down, timeFormat)
//                holder.setViewVisibility(R.id.tv_remain_time,View.VISIBLE)
//            } else {
//                holder.setViewVisibility(R.id.tv_remain_time,View.INVISIBLE)
//                holder.setText(R.id.tv_time_count_down, "充电已完成")
//            }
//        }
//    }
//
//    /**
//     * 添加数据源
//     */
//    fun addData(list:ArrayList<RecordsMergeBean>?) {
//        list?.let {
//            this.mData.clear()
//            this.mData.addAll(list)
//        }
//
//        this.notifyDataSetChanged()
//       Logger.d(" itemCount: ${ itemCount}")
//    }
//
//}