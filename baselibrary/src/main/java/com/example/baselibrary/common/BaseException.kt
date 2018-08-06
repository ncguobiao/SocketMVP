package com.example.baselibrary.common

/*
    定义通用异常
 */
class BaseException(val returnCode:Int,val returnMsg:String) :Throwable()
