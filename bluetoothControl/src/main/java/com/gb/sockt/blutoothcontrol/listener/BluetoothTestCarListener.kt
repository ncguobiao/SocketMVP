package com.gb.sockt.blutoothcontrol.listener

/**
 * Created by guobiao on 2018/11/15.
 */
interface BluetoothTestCarListener {





    fun onError(data:String)

    fun onWriteFailure(msg: String)

    fun onWriteSuccess(msg: String)
    fun requestOnSuccess(byteArrayToHexString: String?)
    fun checkSeekOnSuccess(byteArrayToHexString: String?)
    fun getDeviceInfoOnSuccess(count: Int, bleCount: Int)
    fun coinOnSuccess(byteArrayToHexString: String?)
    fun clearCountOnSuccess(byteArrayToHexString: String?)
    fun clearConfigOnSuccess(byteArrayToHexString: String?)
    fun getDeviceInfoOnError(error: String)
    fun getDeviceInfoOnIdle()


}