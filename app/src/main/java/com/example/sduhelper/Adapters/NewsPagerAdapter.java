package com.example.sduhelper.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * This is sduer
 * Created by qidi on 2017/7/23.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class NewsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList;

    public NewsPagerAdapter(FragmentManager fm, List<Fragment> mList) {
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
        if(position == 0) {
            return "学生在线";
        } else if(position == 1){
            return "本科生院";
        } else if(position == 2) {
            return "青春山大";
        } else
            return "山大视点";
    }
}
