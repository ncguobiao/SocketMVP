package com.gb.sockt.center.mvp.service

import com.example.baselibrary.common.BaseResp
import io.reactivex.Observable

/**
 * Created by guobiao on 2018/8/23.
 */
interface UserCenterService {

    fun getUseRecords(className:String,userId:String,curPage:String): Observable<BaseResp>


    fun getRechargeRecords(userId:String): Observable<BaseResp>


    fun deleteUserRecord(appType: String, useDeviceId: String, userId: String): Observable<BaseResp>

     fun deletePayMent(appType: String, paymentId: String, userId: String): Observable<BaseResp>
}