package com.example.sduhelper.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * This is sduer
 * Created by qidi on 2017/7/23.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class ScorePagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList;

    public ScorePagerAdapter(FragmentManager fm, List<Fragment> mList) {
        super(fm);
        this.mList = mList;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "成绩查询";
        else return "绩点查询";
    }
}
