package com.example.baselibrary.widght

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import com.example.baselibrary.R
import kotlinx.android.synthetic.main.layout_toolbar.view.*

/**
 * Created by Alienware on 2018/6/25.
 */
class CommonToolbar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    init {

        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.layout_toolbar, this)

    }

    fun setContentTitle(title: String) {
        toolbar_title.text = title
    }

    fun hideTitle(){
        toolbar_title.visibility = View.GONE
    }


}