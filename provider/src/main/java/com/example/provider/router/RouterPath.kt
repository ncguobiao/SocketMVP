package com.example.provider.router

/**
 * 模块路由 路径定义
 */
class RouterPath {
    //搜索
    class Search {
        companion object {
            const val PATH_SEARCH = "/searchCenter/search"
        }
    }

    //播放模块
    class Video {
        companion object {
            const val PATH_VIDEO = "/Video/play"
        }
    }

    //用户模块
    class UserCenter {
        companion object {
            //登陆页面
            const val PATH_LOGIN = "/usercenter/login"
        }
    }

    //app主模块
    class Main{
        companion object {
            //主页
            const val PATH_HOME = "/Main/home"
        }
    }

    //蓝牙
    class BLUETOOTH{
        companion object {
            //BluetoothControllActivity
            const val PATH_BLUETOOTH_CONTROLL = "/bluetooth/container"

//
        }
    }
}