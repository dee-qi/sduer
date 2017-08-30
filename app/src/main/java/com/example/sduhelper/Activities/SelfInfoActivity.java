package com.example.sduhelper.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.sduhelper.LoginActivity;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelfInfoActivity extends BaseActivity implements View.OnClickListener{

    private ListView listView;
    private Button logout;
    private CardView e_card;
    Map<String, Object> item;
    List<Map<String,Object>> itemList;


    private int[] icon = new int[]{R.drawable.ic_about_black_24dp,
            R.drawable.ic_news_black_24dp,
            R.drawable.ic_news_black_24dp,
            R.drawable.ic_news_black_24dp};
    private String[] title = new String[]{"姓名", "学号", "学院", "专业"};

    //这个要读取用户数据
    private String[] info = new String[]{"齐迪", "201600301190","软件学院","软件工程"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);
        initToolbar("个人信息");

        if(!SharedPreferenceUtil.get(SelfInfoActivity.this,"userInfo","name").equals("")){
            info = new String[4];
            info[0] = SharedPreferenceUtil.get(SelfInfoActivity.this,"userInfo","name");
            info[1] = SharedPreferenceUtil.get(SelfInfoActivity.this,"userInfo","stuNum");
            info[2] = SharedPreferenceUtil.get(SelfInfoActivity.this,"userInfo","college");
            info[3] = SharedPreferenceUtil.get(SelfInfoActivity.this,"userInfo","major");
        }
        listView = (ListView)findViewById(R.id.selfinfo_list);
        logout = (Button)findViewById(R.id.selfinfo_logout);
        itemList = new ArrayList<Map<String, Object>>();
        for(int i=0; i<4; i++) {
            Map<String,Object> item1 = new HashMap<String, Object>();
            item1.put("icon", icon[i]);
            item1.put("title", title[i]);
            item1.put("info", info[i]);
            itemList.add(item1);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, itemList,R.layout.item_selfinfo,
                new String[]{"icon", "title", "info"},
                new int[] {R.id.selfinfo_item_icon, R.id.selfinfo_item_title,R.id.selfinfo_item_info});
        listView.setAdapter(adapter);
        setListViewHeightBasedOnItems(listView);

        e_card = (CardView)findViewById(R.id.selfinfo_ecard);
        e_card.setOnClickListener(this);

        logout = (Button)findViewById(R.id.selfinfo_logout);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selfinfo_ecard:
                SmartToast.make(this,"电子校园卡功能建设中！敬请期待！");
                break;
            case R.id.selfinfo_logout:
                //清除所有sharedPreference信息
                SharedPreferenceUtil.clear(SelfInfoActivity.this,"userInfo");
                SharedPreferenceUtil.clear(SelfInfoActivity.this,"curriculum");
//                SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.clear();
//                editor.commit();
                finishALl();
                Intent intent = new Intent(SelfInfoActivity.this, LoginActivity.class);
                startActivity(intent);
        }
    }

    private void setListViewHeightBasedOnItems(ListView listView){
        ListAdapter adapter = listView.getAdapter();
        if(adapter != null){
            int total = adapter.getCount();
            View v = adapter.getView(0,null,listView);
            v.measure(0,View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = total*v.getMeasuredHeight();
            listView.setLayoutParams(params);
        }
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
