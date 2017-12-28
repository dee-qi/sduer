package com.example.sduhelper.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.sduhelper.Adapters.ScorePagerAdapter;
import com.example.sduhelper.Fragments.GPAQueryFragment;
import com.example.sduhelper.Fragments.ScoreQueryFragment;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.BaseActivity;

import java.util.LinkedList;
import java.util.List;

public class ScoreActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ScorePagerAdapter adapter;
    private List<Fragment> mList;

    private ScoreQueryFragment scoreFrag;
    private GPAQueryFragment gpaFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        initToolbar("成绩查询");

        tabLayout = (TabLayout) findViewById(R.id.score_tab_layout);
        viewPager = (ViewPager) findViewById(R.id.score_view_pager);

        scoreFrag = new ScoreQueryFragment();
        gpaFrag = new GPAQueryFragment();

        mList = new LinkedList<>();
        mList.add(scoreFrag);
        mList.add(gpaFrag);

        adapter = new ScorePagerAdapter(getSupportFragmentManager(), mList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void initToolbar(String title){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_back_white_36dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
