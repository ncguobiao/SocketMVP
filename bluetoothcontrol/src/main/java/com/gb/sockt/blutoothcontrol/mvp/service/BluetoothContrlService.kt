package com.gb.sockt.blutoothcontrol.mvp.service

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.sockt.blutoothcontrol.api.BluetoothControlApi
import com.gb.sockt.blutoothcontrol.data.domain.RequestCantUseReq
import io.reactivex.Observable

/**
 * Created by guobiao on 2018/8/13.
 */
interface BluetoothContrlService {

   fun requestSurplusTime(userId: String, deviceId: String):Observable<BaseResp>


   fun requestCanUse(userId: String, deviceId: String,time:String):Observable<BaseResp>

   fun openSuccess2Service(userId: String, deviceId: String, time: String): Observable<BaseResp>

   /**
    * 加时成功通知服务器
    */
   fun addTimeSuccess2Service(userId: String, deviceId: String, time: String): Observable<BaseResp>

   fun addTimeCanUse(userId: String, deviceId: String, time: String): Observable<BaseResp>
}