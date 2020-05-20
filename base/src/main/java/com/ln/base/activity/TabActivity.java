package com.ln.base.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ln.base.R;
import com.ln.base.view.PagerIndicator;

public class TabActivity extends BaseActivity implements
        OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    protected RadioGroup tab;
    protected ViewPager viewPager;
    protected PagerIndicator pagerIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onInit() {
        super.onInit();
        tab = findViewById(R.id.tab);
        if (tab != null) {
            final int count = tab.getChildCount();
            for (int i = 0; i < count; i++) {
                tab.getChildAt(i).setId(i);
            }
            tab.setOnCheckedChangeListener(this);
        }

        viewPager = findViewById(R.id.view_pager);
        if (viewPager != null) viewPager.setOnPageChangeListener(this);

        pagerIndicator = findViewById(R.id.pager_indicator);
    }

    protected void setTabAdapter() {
        setTabAdapter(new DefaultPagerAdapter());
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount() - 1);
    }

    protected void setTabAdapter(PagerAdapter pagerAdapter) {
        setTabAdapter(pagerAdapter, 0);
    }

    protected void setTabAdapter(int initIndex) {
        setTabAdapter(new DefaultPagerAdapter(), initIndex);
    }

    protected void setTabAdapter(PagerAdapter pagerAdapter, int initIndex) {
        viewPager.setAdapter(pagerAdapter);
        if (pagerIndicator != null) {
            pagerIndicator.setPageCount(pagerAdapter.getCount());
        }
        if (initIndex > 0) {
            viewPager.setCurrentItem(initIndex, false);
        } else {
            onPageSelected(initIndex);
        }
    }

    public int getTabIndex() {
        return viewPager.getCurrentItem();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (viewPager != null) viewPager.setCurrentItem(checkedId, false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (pagerIndicator != null) {
            pagerIndicator.onPageScrolled(position, positionOffset,
                    positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (tab != null) {
            ((RadioButton) tab.getChildAt(position)).setChecked(true);
        }
    }

    private final class DefaultPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(View collection, int position) {
            return viewPager.getChildAt(position);
        }

        @Override
        public int getCount() {
            return viewPager.getChildCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

    }
}