package com.ln.ljb.activity

import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ln.base.tool.DimenUtils
import com.ln.base.tool.Log
import com.ln.ljb.R
import com.ln.ljb.fragment.LoginFragment
import com.ln.ljb.fragment.RegisterFragment

class MobileLoginActivity : TabActivity() {

    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_login)
    }

    override fun onInit() {
        super.onInit()
        setTabAdapter(PageAdapter(this, supportFragmentManager))

        currentPage = intent.getIntExtra("page", 0)
        viewPager?.currentItem = currentPage
        viewPager?.offscreenPageLimit = 1


    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        super.onCheckedChanged(group, checkedId)
        if (checkedId == 0) {
            (group!![0] as RadioButton).textSize = DimenUtils.sp2pxOriginal(this, 10.0f)
            (group[1] as RadioButton).textSize = DimenUtils.sp2pxOriginal(this, 4.7f)
            titleBar.setTitle(getString(R.string.mobile_login))
        } else {
            (group!![1] as RadioButton).textSize = DimenUtils.sp2pxOriginal(this, 10.0f)
            (group[0] as RadioButton).textSize = DimenUtils.sp2pxOriginal(this, 4.7f)
            titleBar.setTitle(getString(R.string.mobile_register))
        }
    }

    private class PageAdapter : FragmentPagerAdapter {

        private var context: Context

        constructor(context: Context, fm: FragmentManager) : super(fm) {
            this.context = context
        }

        override fun getItem(position: Int): Fragment {
            return Fragment.instantiate(this.context, PageEnum.values()[position].aClass.name)
        }

        override fun getCount(): Int {
            return PageEnum.values().size
        }
    }

    private enum class PageEnum(var aClass: Class<*>) {
        LOGIN(LoginFragment::class.java), REGISTER(RegisterFragment::class.java);
    }

    fun showFragment(fragmentSimpleName: String) {
        Log.e(fragmentSimpleName)
        Log.e(RegisterFragment::class.java.simpleName)
        Log.e("result -> " + (RegisterFragment::class.java.simpleName == fragmentSimpleName))
        if (RegisterFragment::class.java.simpleName == fragmentSimpleName) {
            if (viewPager != null) viewPager?.setCurrentItem(PageEnum.REGISTER.ordinal, false)
        } else if (LoginFragment::class.java.simpleName == fragmentSimpleName) {
            if (viewPager != null) viewPager?.setCurrentItem(PageEnum.LOGIN.ordinal, false)
        }
    }

}
