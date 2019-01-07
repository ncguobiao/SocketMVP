package com.gb.sockt.blutoothcontrol.listener

/**
 * Created by guobiao on 2019/1/4.
 */
interface BleCableListener {

    fun setPwdSuccess(pwd:String?)

    fun onError(byteArrayToHexString: String)
    fun onWriteSuccess(msg:String?,type:Int)

    fun onWriteFailure(msg:String?,type:Int)
    fun openSuccess()
    fun onRecived(s: String)
    fun onCircle()
}