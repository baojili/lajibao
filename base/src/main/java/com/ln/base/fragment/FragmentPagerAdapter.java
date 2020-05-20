package com.ln.base.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class FragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    public FragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragmentList = new ArrayList<Fragment>();
    }

    public FragmentPagerAdapter(FragmentManager fragmentManager, Fragment... fragments) {
        this(fragmentManager);
        for (int i = 0; i < fragments.length; i++) {
            mFragmentList.add(fragments[i]);
        }
    }

    public void addFragments(Fragment... fragments) {
        for (int i = 0; i < fragments.length; i++) {
            mFragmentList.add(fragments[i]);
        }
        notifyDataSetChanged();
    }

    public void removeFragments(Fragment... fragments) {
        for (int i = 0; i < fragments.length; i++) {
            mFragmentList.remove(fragments[i]);
        }
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}