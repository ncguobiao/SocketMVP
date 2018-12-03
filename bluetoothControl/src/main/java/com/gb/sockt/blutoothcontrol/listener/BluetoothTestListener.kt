package com.gb.sockt.blutoothcontrol.listener

/**
 * Created by guobiao on 2018/11/15.
 */
interface BluetoothTestListener {

    fun onAdd(data:String)

    fun onLess(data:String)

    fun onError(data:String)

    fun onWriteFaile(msg: String)

    fun onWriteSuccess(msg: String)

    fun onFindAllMAC(byteArrayToHexString: String?)
}