package com.gb.sockt.center.mvp.presenter

/**
 * Created by guobiao on 2018/8/23.
 */
interface RecordsPresenter{

    fun getUseRecords(className:String,userId:String,curPage:String)

    fun getRechargeRecords(userId: String)
}