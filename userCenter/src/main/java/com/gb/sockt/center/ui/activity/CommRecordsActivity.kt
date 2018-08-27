package com.gb.sockt.center.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.baselibrary.base.BaseActivity
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.onClick
import com.example.provider.router.RouterPath
import com.gb.sockt.center.R
import com.gb.sockt.center.ui.fragment.RechargeRecordsFragment
import com.gb.sockt.center.ui.fragment.UseRecordsFragment
import com.orhanobut.logger.Logger
import com.yanzhenjie.recyclerview.swipe.*
import kotlinx.android.synthetic.main.activity_comm_records.*

/**
 * Created by guobiao on 2018/8/23.
 */
@Route(path = RouterPath.UserCenter.PATH_USER_CENTER)
class CommRecordsActivity : BaseActivity() {
     val swipeMenuCreator = object : SwipeMenuCreator {
        override fun onCreateMenu(swipeLeftMenu: SwipeMenu, swipeRightMenu: SwipeMenu, viewType: Int) {
            val width = resources.getDimensionPixelSize(R.dimen.dp_70)
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(this@CommRecordsActivity)
                    .setBackground(R.drawable.selector_red)
                    .setImage(R.mipmap.ic_action_delete)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height)
            swipeRightMenu.addMenuItem(deleteItem)// 添加菜单到右侧。

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comm_records)

        val recordType = intent.extras.getString(ConstantSP.RECORD_TYPE)

        if (ConstantSP.RECHARGE_RECORDS == recordType) {
            tvTitle.text = resources.getString(R.string.charg_detail)
            val rechargeRecordsFragment = RechargeRecordsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.comm_container, rechargeRecordsFragment).commit()
        } else {
            tvTitle.text = resources.getString(R.string.user_record)
            val useRecordsFragment = UseRecordsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.comm_container, useRecordsFragment).commit()
        }

        iv_back.onClick { finish() }


    }


}