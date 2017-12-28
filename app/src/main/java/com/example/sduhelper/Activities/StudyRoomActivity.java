package com.example.sduhelper.Activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.sduhelper.Adapters.BuildingListAdapter;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.NetWorkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class StudyRoomActivity extends BaseActivity implements View.OnClickListener{

    private String[] zx = {"中心董明珠楼","中心文史楼","中心电教北楼","中心公教楼","中心理综楼","中心知新楼B座"};
    private String[] hjl = {"洪楼3号楼","洪楼6号楼","洪楼物理楼","洪楼公教楼"};
    private String[] btq = {"趵突泉2号楼","趵突泉8号楼","趵突泉9号楼","趵突泉图东","趵突泉图西"};
    private String[] rjy = {"软件园1区","软件园4区","软件园5区"};
    private String[] xls = {"兴隆山群楼A座","兴隆山群楼B座","兴隆山群楼C座","兴隆山群楼D座","兴隆山群楼E座","兴隆山讲学堂"};
    private String[] qfs = {"千佛山1号楼","千佛山5号楼","千佛山9号楼","千佛山10号楼"};
    BuildingListAdapter adapter = new BuildingListAdapter("中心校区",zx);

    private View campus_zx;
    private View campus_btq;
    private View campus_hjl;
    private View campus_rjy;
    private View campus_xls;
    private View campus_qfs;
    private RecyclerView buildingList;
    private TextView tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        initToolbar("自习室查询");

        campus_zx = findViewById(R.id.studyroom_zx);
        campus_zx.setOnClickListener(this);
        campus_btq = findViewById(R.id.studyroom_btq);
        campus_btq.setOnClickListener(this);
        campus_hjl = findViewById(R.id.studyroom_hjl);
        campus_hjl.setOnClickListener(this);
        campus_rjy = findViewById(R.id.studyroom_rjy);
        campus_rjy.setOnClickListener(this);
        campus_xls = findViewById(R.id.studyroom_xls);
        campus_xls.setOnClickListener(this);
        campus_qfs = findViewById(R.id.studyroom_qfs);
        campus_qfs.setOnClickListener(this);
        buildingList = (RecyclerView)findViewById(R.id.studyroom_building_list);
        LinearLayoutManager manager = new LinearLayoutManager(StudyRoomActivity.this);
        buildingList.setLayoutManager(manager);
        buildingList.setAdapter(adapter);
        tips = (TextView)findViewById(R.id.studyroom_tips);


    }

    @Override
    public void onClick(View v) {
        tips.setVisibility(View.GONE);
        buildingList.setVisibility(View.VISIBLE);
        switch (v.getId()){
            case R.id.studyroom_zx:
                adapter.setCampus("中心校区",zx);
                break;
            case R.id.studyroom_btq:
                adapter.setCampus("趵突泉校区",btq);
                break;
            case R.id.studyroom_hjl:
                adapter.setCampus("洪家楼校区",hjl);
                break;
            case R.id.studyroom_xls:
                adapter.setCampus("兴隆山校区",xls);
                break;
            case R.id.studyroom_rjy:
                adapter.setCampus("软件园校区",rjy);
                break;
            case R.id.studyroom_qfs:
                adapter.setCampus("千佛山校区",qfs);
                break;
        }
        adapter.notifyDataSetChanged();
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
