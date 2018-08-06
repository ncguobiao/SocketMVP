package com.example.baselibrary.utils

import com.example.baselibrary.common.BaseApplication
import com.inuker.bluetooth.library.BluetoothClient

/**
 * Created by guobiao on 2018/7/13.
 */
class ClientManager {

    companion object {

        private  var mClient:BluetoothClient? =null

        fun getClient(): BluetoothClient {
            if (mClient == null) {
                synchronized(ClientManager::class.java) {
                    if (mClient == null) {
                        mClient = BluetoothClient(BaseApplication.getAppContext())
                    }
                }
            }
            return mClient!!
        }

    }






}

