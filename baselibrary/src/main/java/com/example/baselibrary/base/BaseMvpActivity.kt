package com.example.baselibrary.base


import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.classic.common.MultipleStatusView
import com.example.baselibrary.common.BaseApplication
import com.example.baselibrary.common.ConstantSP
import com.example.baselibrary.injection.component.ActivityComponent
import com.example.baselibrary.injection.component.DaggerActivityComponent
import com.example.baselibrary.injection.module.ActivityMoudle
import com.example.baselibrary.injection.module.LifecycleProviderModule
import com.example.baselibrary.utils.SpUtils
import com.example.baselibrary.widght.ProgressLoading
import javax.inject.Inject


/**
 * BaseActivity基类
 */
abstract class BaseMvpActivity<T : BasePresenter<*>> : BaseActivity(), IBaseView {
    public val TAG: String = this.javaClass.simpleName
    val WEIXIN_MSG_ACTION_CALLBACK: Int = 6

    private lateinit var mLoadingDialog: ProgressLoading


    /**
     * 多种状态的 View 的切换
     */
    //Presenter泛型，Dagger注入
    @Inject
    lateinit var mPresenter: T

    lateinit var activityComponent: ActivityComponent

    protected var mLayoutStatusView: MultipleStatusView? = null

//    private lateinit var pDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        initActivityInjection()
        initComponent()
        initData()

        initView(savedInstanceState)
        initDialog()
        start()
        initListener()
    }




    private fun initDialog() {
        //初始加载框
        mLoadingDialog = ProgressLoading.create(this)
        //ARouter注册
        ARouter.getInstance().inject(this)
//        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
//        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
//        pDialog.titleText = "Loading"
//        pDialog.setCancelable(true)





    }


    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun layoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView(savedInstanceState: Bundle?)


    abstract fun initComponent()

    private fun initActivityInjection() {
        activityComponent = DaggerActivityComponent.builder()
                .appComponent((application as BaseApplication).appComponent)
                .lifecycleProviderModule(LifecycleProviderModule(this))
                .activityMoudle(ActivityMoudle(this))
                .build()
    }

    /**
     * 开始请求
     */
    abstract fun start()


    private fun initListener() {
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        start()
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
        BaseApplication.getRefWatcher(this)?.watch(this)
//        mLoadingDialog?.hideLoading()
//        pDialog?.cancel()
    }

    override fun showLoading() {

        mLayoutStatusView?.showLoading()
        mLoadingDialog.showLoading()
//        pDialog?.show()

    }

    override fun dismissLoading() {
        mLayoutStatusView?.showContent()
//        pDialog?.cancel()
        mLoadingDialog.hideLoading()
    }

    fun getUserID(): String {
        return SpUtils.getString(BaseApplication.getApplication(), ConstantSP.USER_ID)
    }


    override fun hideNetDialog() {
       dissmissNetDialog()
    }

    override fun showNetDialog() {
        showDialog()
    }


}