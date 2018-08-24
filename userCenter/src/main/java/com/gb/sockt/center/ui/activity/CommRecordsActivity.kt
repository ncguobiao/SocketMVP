package com.gb.sockt.center.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.baselibrary.base.BaseActivity
import com.example.provider.router.RouterPath
import com.gb.sockt.center.R
import com.gb.sockt.center.ui.fragment.RechargeRecordsFragment
import com.gb.sockt.center.ui.fragment.UseRecordsFragment

/**
 * Created by guobiao on 2018/8/23.
 */
@Route(path = RouterPath.UserCenter.PATH_USER_CENTER)
class CommRecordsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comm_records)


        when{

        }
        val useRecordsFragment = UseRecordsFragment.newInstance()

        val rechargeRecordsFragment = RechargeRecordsFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.comm_container,useRecordsFragment).commit()
    }
}