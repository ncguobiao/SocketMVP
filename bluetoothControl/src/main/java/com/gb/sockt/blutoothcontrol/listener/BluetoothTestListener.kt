package com.gb.sockt.blutoothcontrol.listener

/**
 * Created by guobiao on 2018/11/15.
 */
interface BluetoothTestListener {

    fun onGetCodeSuccess(code:ByteArray?)

    fun onOperation()

    fun onDelete(code:ByteArray?,data:String)

    fun onError(data:String)

    fun onWriteFailure(msg: String)

    fun onWriteSuccess(msg: String)

    fun onFindAllMAC(count: Int, map: MutableList<Byte>)

    fun onAddMAC_1(codes: ByteArray?)
    fun onAddMAC_2(codes: ByteArray?)
    fun onAddMAC_3(codes: ByteArray?)

    fun onOpenOrClose(codes: ByteArray?)

    fun onFindSingleMAC(codes: ByteArray?, s: String)

    fun onResetDevice(result: String)

    fun onSetDeciveMAC(result: String)
}