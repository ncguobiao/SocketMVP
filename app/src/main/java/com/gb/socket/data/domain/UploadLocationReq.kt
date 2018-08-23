package com.gb.socket.data.domain

/**
 * Created by guobiao on 2018/8/8.
 */
data class UploadLocationReq (val userId: String,
                         val deviceId: String,
                         val macAddress: String,
                         val longitude: String,
                         val latitude: String,
                         val collectionType: String)