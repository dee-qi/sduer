package com.example.sduhelper;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sduhelper.Activities.CardQueryActivity;
import com.example.sduhelper.Activities.SchoolCardActivity;
import com.example.sduhelper.Fragments.FunctionsFragment;
import com.example.sduhelper.Fragments.HomeFragment;
import com.example.sduhelper.Fragments.NewsFragment;
import com.example.sduhelper.Activities.AboutActivity;
import com.example.sduhelper.Activities.FeedBackActivity;
import com.example.sduhelper.Activities.SchoolBusActivity;
import com.example.sduhelper.Activities.SelfInfoActivity;
import com.example.sduhelper.Items.ItemCurriculum;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.Information;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import java.util.ArrayList;

//主活动，三个页面分别由3个fragment实现
public class MainActivity extends BaseActivity {

    private HomeFragment homeFragment;
    private FunctionsFragment functionsFragment;
    private NewsFragment newsFragment;

    private TextView nav_header_name;
    private TextView nav_header_major;

        //侧滑栏的点击监听器
    private NavigationView.OnNavigationItemSelectedListener drawerOnNavigationItemSelectedListener
            = new NavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case R.id.nav_selfInfo:
                if(Information.isOnTrial){
                    SmartToast.make(MainActivity.this,"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(MainActivity.this,SelfInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                intent = new Intent(MainActivity.this,FeedBackActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, "哇！SDUHelper真的超好用！一起来用吧！下载地址：暂无");
                share.setType("text/plain");
                startActivity(share);
                break;
            case R.id.nav_schoolbus:
                intent = new Intent(MainActivity.this,SchoolBusActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_schoolcard:
                if(Information.isOnTrial){
                    SmartToast.make(MainActivity.this,"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(MainActivity.this, SchoolCardActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(getApplicationContext(), "default", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    };
        //底部tab监听，在三个fragment中切换
    private BottomNavigationView.OnNavigationItemSelectedListener bottomOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(homeFragment == null) {
                        homeFragment = new HomeFragment();
                    }
                    replaceFragment(R.id.main_fragment_content,homeFragment);
                    return true;
                case R.id.navigation_funtions:
                    if(functionsFragment == null) {
                        functionsFragment = new FunctionsFragment();
                    }
                    replaceFragment(R.id.main_fragment_content,functionsFragment);
                    return true;
                case R.id.navigation_news:
                    if(newsFragment == null) {
                        newsFragment = new NewsFragment();
                    }
                    replaceFragment(R.id.main_fragment_content,newsFragment);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigation.setOnNavigationItemSelectedListener(bottomOnNavigationItemSelectedListener);
        homeFragment = new HomeFragment();
        replaceFragment(R.id.main_fragment_content,homeFragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(drawerOnNavigationItemSelectedListener);

        //获取侧滑栏的header部分，并且根据用户信息进行变动
        View header = navigationView.getHeaderView(0);
        nav_header_name = (TextView)header.findViewById(R.id.nav_header_name);
        nav_header_major = (TextView)header.findViewById(R.id.nav_header_major);
        if(!SharedPreferenceUtil.get(MainActivity.this,"userInfo","name").equals("")) {
            nav_header_name.setText(SharedPreferenceUtil.get(MainActivity.this,"userInfo","name"));
        }
        if(!SharedPreferenceUtil.get(MainActivity.this,"userInfo","college").equals("")) {
            nav_header_major.setText(SharedPreferenceUtil.get(MainActivity.this,"userInfo","college")
            +" "+SharedPreferenceUtil.get(MainActivity.this,"userInfo","major"));
        }
    }

    //双击返回键退出应用
    private long mLastBackPress;
    Toast mToast;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            mToast = Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT);
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - mLastBackPress) > 2000) {
                mToast.show();
                mLastBackPress = currentTime;
            } else {
                mToast.cancel();
                finish();
            }
        }
    }

    //切换Fragment的方法
    private void replaceFragment(int resourceId, Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(resourceId,fragment);
        transaction.commit();
    }

}
