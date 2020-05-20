package com.ln.ljb.activity

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ln.base.view.PagerIndicator
import com.ln.ljb.R

open class TabActivity : com.ln.ljb.activity.BaseActivity(),
    RadioGroup.OnCheckedChangeListener, OnPageChangeListener {

    private var tab: RadioGroup? = null
    var viewPager: ViewPager? = null
    private var pagerIndicator: PagerIndicator? = null

    override fun onInit() {
        super.onInit()
        tab = findViewById(R.id.tab)
        if (tab != null) {
            val count = tab!!.childCount
            for (i in 0 until count) {
                tab!!.getChildAt(i).id = i
            }
            tab!!.setOnCheckedChangeListener(this)
        }
        viewPager = findViewById(R.id.view_pager)
        if (viewPager != null) viewPager!!.setOnPageChangeListener(this)
        pagerIndicator = findViewById(R.id.pager_indicator)
    }

    fun setTabAdapter() {
        setTabAdapter(DefaultPagerAdapter())
        viewPager?.offscreenPageLimit = viewPager?.adapter?.count?.minus(1)!!
    }

    fun setTabAdapter(pagerAdapter: PagerAdapter) {
        setTabAdapter(pagerAdapter, 0)
    }

    fun setTabAdapter(initIndex: Int) {
        setTabAdapter(DefaultPagerAdapter(), initIndex)
    }

    fun setTabAdapter(pagerAdapter: PagerAdapter, initIndex: Int) {
        viewPager?.adapter = pagerAdapter
        if (pagerIndicator != null) {
            pagerIndicator!!.setPageCount(pagerAdapter.count)
        }
        if (initIndex > 0) {
            viewPager?.setCurrentItem(initIndex, false)
        } else {
            onPageSelected(initIndex)
        }
    }

    val tabIndex: Int
        get() = viewPager!!.currentItem

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (viewPager != null) viewPager!!.setCurrentItem(checkedId, false)
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(
        position: Int, positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        if (pagerIndicator != null) {
            pagerIndicator!!.onPageScrolled(
                position, positionOffset,
                positionOffsetPixels
            )
        }
    }

    override fun onPageSelected(position: Int) {
        if (tab != null) {
            (tab!!.getChildAt(position) as RadioButton).isChecked = true
        }
    }

    private inner class DefaultPagerAdapter : PagerAdapter() {
        override fun instantiateItem(
            collection: View,
            position: Int
        ): Any {
            return viewPager!!.getChildAt(position)
        }

        override fun getCount(): Int {
            return viewPager!!.childCount
        }

        override fun isViewFromObject(
            view: View,
            obj: Any
        ): Boolean {
            return view === obj as View
        }
    }
}
