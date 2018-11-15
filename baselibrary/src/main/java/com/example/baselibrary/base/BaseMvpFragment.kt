package com.example.baselibrary.base

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.classic.common.MultipleStatusView
import com.example.baselibrary.R
import com.example.baselibrary.act
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.Constant
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.component.DaggerActivityComponent
import com.example.baselibrary.injection.module.ActivityMoudle
import com.example.baselibrary.injection.module.LifecycleProviderModule
import com.example.baselibrary.utils.SpUtils
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import javax.inject.Inject

/**
 * Created by Alienware 2018/5/31.
 *  Fragment基类，业务相关
 */
abstract class BaseMvpFragment<T : BasePresenter<*>> : BaseFragment(), IBaseView {
    val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
        val width = resources.getDimensionPixelSize(R.dimen.dp_70)
        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        val deleteItem = SwipeMenuItem(activity)
                .setBackground(R.drawable.selector_red)
                .setImage(R.mipmap.ic_action_delete)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
        swipeRightMenu.addMenuItem(deleteItem)// 添加菜单到右侧。
    }
    @Inject
    lateinit var mPresenter: T

    lateinit var mActivityComponent: ActivityComponent
    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false
    /**
     * 多种状态的 View 的切换
     */
    protected var mLayoutStatusView: MultipleStatusView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(getLayoutId(), null)
    }

    /**
     *  加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActivityInjection()
        injectComponent()

        isViewPrepare = true
        initView()

        lazyLoadDataIfPrepared()
        //多种状态切换的view 重试点击事件
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    /*
    Dagger注册
 */
    protected abstract fun injectComponent()

    /*
       初始化Activity级别Component
    */
    private fun initActivityInjection() {
        mActivityComponent = DaggerActivityComponent.builder()
                .appComponent((activity?.application as BaseApplication).appComponent)
                .activityMoudle(ActivityMoudle(act!!))
                .lifecycleProviderModule(LifecycleProviderModule(this))
                .build()
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    /**
     * 当fragment被用户可见时，setUserVisibleHint()会调用且传入true值
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }


    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        lazyLoad()
    }

    /**
     * 懒加载
     */
    abstract fun lazyLoad()

    /**
     * 初始化View
     */
    abstract fun initView()

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
        BaseApplication.getRefWatcher(activity!!)?.watch(activity)
    }

    fun getUserID(): String {
        return SpUtils.getString(activity, ConstantSP.USER_ID)
    }

    override fun showLoading() {
        if (activity is BaseMvpActivity<*>) {
            (activity as BaseMvpActivity<*>).showLoading()
        }
    }


    override fun dismissLoading() {
        if (activity is BaseMvpActivity<*>) {
            (activity as BaseMvpActivity<*>).dismissLoading()
        }

    }

    override fun showNetDialog() {
        if (activity is BaseMvpActivity<*>) {
            (activity as BaseMvpActivity<*>).showNetDialog()
        }
    }

    override fun hideNetDialog() {
        if (activity is BaseMvpActivity<*>) {
            (activity as BaseMvpActivity<*>).hideNetDialog()
        }
    }


}