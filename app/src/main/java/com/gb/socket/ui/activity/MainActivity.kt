package com.gb.socket.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.baselibrary.base.BaseActivity
import com.example.provider.router.RouterPath
import com.gb.socket.R
@Route(path = RouterPath.Main.PATH_HOME)
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        initData()
    }



     fun initData() {
    }

   fun layoutId(): Int =R.layout.activity_main

}
