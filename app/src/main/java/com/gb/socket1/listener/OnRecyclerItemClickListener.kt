package com.gb.socket1.listener

/**
 * Created by guobiao on 2018/8/14.
 */
interface OnRecyclerItemClickListener {
    abstract fun onItemClick(position: Int)

    abstract fun onClose(deviceName: String, useTemporaryId: String)

    abstract fun onOpen(useTemporaryId: String)
}