package com.example.baselibrary.utils

import com.example.baselibrary.common.BaseApplication
import com.inuker.bluetooth.library.BluetoothClient

/**
 * Created by guobiao on 2018/7/13.
 */
class BluetoothClientManager {

    companion object {

        private  var mClient:BluetoothClient? =null

        fun getClient(): BluetoothClient {
            if (mClient == null) {
                synchronized(BluetoothClientManager::class.java) {
                    if (mClient == null) {
                        mClient = BluetoothClient(BaseApplication.getApplication())
                    }
                }
            }
            return mClient!!
        }

    }






}

