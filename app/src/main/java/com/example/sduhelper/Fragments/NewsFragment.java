package com.example.sduhelper.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sduhelper.Adapters.NewsPagerAdapter;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.NetWorkUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * This is UITest2
 * Created by qidi on 2017/7/16.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class NewsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private NewsPagerAdapter adapter;
    private List<Fragment> mList;

    private NewsListFrag online,under,youth,sduview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_news,container,false);
        tabLayout = (TabLayout) v.findViewById(R.id.news_tab_layout);
        viewPager = (ViewPager) v.findViewById(R.id.news_view_pager);

        online = new NewsListFrag();
        online.setApi(ApiUtil.getApi(getContext(),"api_news_online"));
        under = new NewsListFrag();
        under.setApi(ApiUtil.getApi(getContext(),"api_news_undergraduate"));
        youth = new NewsListFrag();
        youth.setApi(ApiUtil.getApi(getContext(),"api_news_youth"));
        sduview = new NewsListFrag();
        sduview.setApi(ApiUtil.getApi(getContext(),"api_news_sduView"));

        mList = new LinkedList<>();
        mList.add(online);
        mList.add(under);
        mList.add(youth);
        mList.add(sduview);

        adapter = new NewsPagerAdapter(getChildFragmentManager(), mList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

}
