package com.gb.sockt.center.ui.activity

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.provider.router.RouterPath
import com.gb.sockt.center.R
import com.gb.sockt.center.ui.fragment.CommFragment
import com.gb.sockt.center.ui.fragment.CommRecordsFragment
import com.gb.sockt.center.ui.fragment.RechargeRecordsFragment
import com.gb.sockt.center.ui.fragment.UseRecordsFragment
import kotlinx.android.synthetic.main.activity_comm_records.*
import kotlinx.android.synthetic.main.fragment_comm_records.*

/**
 * Created by guobiao on 2018/8/23.
 */
@Route(path = RouterPath.UserCenter.PATH_USER_CENTER)
class CommRecordsActivity : BaseActivity() {

    private  var commFragment: CommFragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comm_records)

        val recordType = intent.extras.getString(ConstantSP.RECORD_TYPE)

        when (recordType) {
            ConstantSP.RECHARGE_RECORDS -> {
                tvTitle.text = resources.getString(R.string.charg_detail)
                val commRecordsFragment = CommRecordsFragment.newInstance()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, commRecordsFragment).commit()
            }
            ConstantSP.USE_RECORDS -> {
                tvTitle.text = resources.getString(R.string.user_record)
                val commRecordsFragment = CommRecordsFragment.newInstance()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, commRecordsFragment).commit()
            }
            else -> {
                tvTitle.text = resources.getString(R.string.wechat)
                 commFragment = CommFragment.newInstance()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, commFragment).commit()
            }
        }


        iv_back.onClick { finish() }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        commFragment?.onActivityResult(requestCode,resultCode,data)
    }


}