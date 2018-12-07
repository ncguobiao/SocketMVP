package com.gb.sockt.blutoothcontrol.listener

/**
 * Created by guobiao on 2018/11/15.
 */
interface BluetoothTestKeyListener {

    fun onConfigMAC(data:String)

    fun onSendkeyOne(data:String)

    fun onSendkeyTwo(data:String)



    fun onError(data:String)

    fun onWriteFailure(msg: String)

    fun onWriteSuccess(msg: String)


}