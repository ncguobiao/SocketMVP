package com.gb.sockt.blutoothcontrol.listener

/**
 * Created by guobiao on 2019/1/4.
 */
interface BleCableListener {

    fun setPwdSuccess(pwd:String?)

    fun onError()
    fun onWriteSuccess(msg:String?)

    fun onWriteFailure(msg:String?)
    fun openSuccess()
}