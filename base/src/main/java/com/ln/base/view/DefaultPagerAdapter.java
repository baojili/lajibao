package com.ln.base.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 *
 */
public abstract class DefaultPagerAdapter<T> extends PagerAdapter implements View.OnClickListener {
    private List<T> mListData;
    private Context mContext;
    private PagerIndicator pagerIndicator;

    public DefaultPagerAdapter(List<T> listData, Context context) {
        this.mListData = listData;
        this.mContext = context;
    }

    public DefaultPagerAdapter(List<T> listData, Context context, PagerIndicator pagerIndicator) {
        this(listData, context);
        if (pagerIndicator == null) {
            throw new IllegalArgumentException("pagerIndicator should not be null");
        }
        this.pagerIndicator = pagerIndicator;
        pagerIndicator.setPageCount(mListData.size());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        T data = mListData.get(position % mListData.size());
        View view = instantiateView(position, data);
        view.setTag(data);
        view.setOnClickListener(this);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (mListData == null) {
            return 0;
        }
        if (mListData.size() < 2) {
            if (pagerIndicator != null) {
                pagerIndicator.setVisibility(View.GONE);
            }
            return 1;
        } else {
            if (pagerIndicator != null) {
                pagerIndicator.setVisibility(View.VISIBLE);
            }
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void onClick(View v) {
        viewClick(v, (T) v.getTag());
    }


    public abstract View instantiateView(int position, T data);

    public abstract void viewClick(View v, T data);
}
