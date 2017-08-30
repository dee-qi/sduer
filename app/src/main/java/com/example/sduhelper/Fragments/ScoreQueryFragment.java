package com.example.sduhelper.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.Information;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreQueryFragment extends Fragment {
    private static final String TAG = "ScoreQueryFragment";

    private Spinner spinner;//选择查询项目的Spinner
    private ListView listView;//展示成绩的ListView
    private TextView loading;//正在加载的View
    private View view;

    //Spinner的内容
    private final String CURRENT_TERM = "本学期成绩";
    private final String HISTORY = "历年成绩";
    private final String FAILED = "不及格成绩";
    private String[] queryMethod = new String[]{CURRENT_TERM, HISTORY, FAILED};

    //用于存放成绩Item的容器
    List<Map<String,Object>> itemList;

    private final int LOAD_FAILED = 0x111;
    private final int LOAD_SUCCEED = 0x112;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            listView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            switch (msg.what){
                case LOAD_SUCCEED:
                    SimpleAdapter listViewAdapter = new SimpleAdapter(view.getContext(),itemList,
                            R.layout.item_score,
                            new String[]{"courseName","xuefen","chengji","item21","item22","item31","item32"},
                            new int[]{R.id.score_course_name,R.id.score_xuefen,R.id.score_chengji,
                                    R.id.score_xkrenshu_or_wfzchengji,R.id.score_paiming_or_wfzjidian,
                                    R.id.score_highest_or_xueqi,R.id.score_lowest_or_ksshijian});
                    listView.setAdapter(listViewAdapter);
                    if(itemList.size()==0){
                        SmartToast.make(getContext(),"无当前项目成绩信息！");
                    }
                    break;
                case LOAD_FAILED:
                    SmartToast.make(getContext(),"获取成绩失败！");
                    break;
            }
        }
    };

    public ScoreQueryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_score_query, container, false);

        spinner = (Spinner) view.findViewById(R.id.score_spinner);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(view.getContext(),
                R.layout.support_simple_spinner_dropdown_item,queryMethod);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    loadData(CURRENT_TERM);
                } else if (position == 1){
                    loadData(HISTORY);
                } else  loadData(FAILED);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //似乎这里应该写点什么，但是我就是什么也不想写
            }
        });

        listView = (ListView)view.findViewById(R.id.score_list);
        loading = (TextView)view.findViewById(R.id.score_loading);

        return view;
    }

    private void loadData(final String selectedItem){
        //初始化布局和ItemList
        listView.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        itemList = new LinkedList<>();

        String url;
        if(selectedItem.equals(CURRENT_TERM)){
            url = ApiUtil.getApi(getContext(),"api_academic_curTerm") + SharedPreferenceUtil.get(getContext(), "userInfo", "id");
        } else if(selectedItem.equals(HISTORY)){
            url = ApiUtil.getApi(getContext(),"api_academic_allScore") + SharedPreferenceUtil.get(getContext(), "userInfo", "id");
        } else if(selectedItem.equals(FAILED)){
            url = ApiUtil.getApi(getContext(),"api_academic_failed") + SharedPreferenceUtil.get(getContext(), "userInfo", "id");
        } else url = "";
        String token = SharedPreferenceUtil.get(getContext(), "userInfo", "token");
        NetWorkUtil.get(url, token, new Callback() {
            Message msg;
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                msg.what = LOAD_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Log.d(TAG, "onResponse: Score is :"+s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if(obj.getInt("code") == 0){
                        JSONArray array = obj.getJSONArray("obj");
                        if(selectedItem.equals(CURRENT_TERM)){
                            loadCurrentTerm(array);
                        } else if(selectedItem.equals(HISTORY)){
                            loadHistoryOrFailed(array);
                        } else if(selectedItem.equals(FAILED)){
                            loadHistoryOrFailed(array);
                        }
                    }
                }catch (JSONException e){
                    Log.d(TAG, "onResponse: JsonException"+e.toString());
                    e.printStackTrace();
                }
            }

            //加载当前学期成绩
            private void loadCurrentTerm(JSONArray array) throws JSONException{
                msg = new Message();
                JSONObject mObj;
                String[] courseName = new String[array.length()];
                String[] xuefen = new String[array.length()];//英语不好只能用拼音
                String[] chengji = new String[array.length()];
                String[] item21 = new String[array.length()];//item21的意思是布局里第二行第一个项目
                String[] item22 = new String[array.length()];
                String[] item31 = new String[array.length()];
                String[] item32 = new String[array.length()];

                for(int i=0; i<array.length(); i++){
                    mObj = array.getJSONObject(i);
                    courseName[i] = mObj.getString("kcm");
                    xuefen[i] = mObj.getString("xf");
                    chengji[i] = mObj.getString("cj");
                    item21[i] = mObj.getString("xkrs");
                    item22[i] = mObj.getString("pm");
                    item31[i] = mObj.getString("zgf");
                    item32[i] = mObj.getString("zdf");
                }

                for(int i=0; i<array.length(); i++){
                    Map<String,Object> item = new HashMap<>();
                    item.put("courseName",courseName[i]);
                    item.put("xuefen","学分：" + xuefen[i]);
                    item.put("chengji","成绩：" + chengji[i]);
                    item.put("item21","选课人数：" + item21[i]);
                    item.put("item22","排名：" + item22[i]);
                    item.put("item31","最高分：" + item31[i]);
                    item.put("item32","最低分：" + item32[i]);
                    itemList.add(item);
                }

                msg.what = LOAD_SUCCEED;
                handler.sendMessage(msg);
            }

            //因为历年成绩和不及格成绩的格式相同，所以共用同一个加载方法
            private void loadHistoryOrFailed(JSONArray array) throws JSONException{
                msg = new Message();
                JSONObject mObj;
                String[] courseName = new String[array.length()];
                String[] xuefen = new String[array.length()];
                String[] chengji = new String[array.length()];
                String[] item21 = new String[array.length()];
                String[] item22 = new String[array.length()];
                String[] item31 = new String[array.length()];
                String[] item32 = new String[array.length()];

                for(int i=0; i<array.length(); i++){
                    mObj = array.getJSONObject(i);
                    courseName[i] = mObj.getString("kcm");
                    xuefen[i] = mObj.getString("xf");
                    chengji[i] = mObj.getString("kscjView");
                    item21[i] = mObj.getString("wfzdj");
                    item22[i] = mObj.getString("wfzjd");
                    item31[i] = mObj.getString("xnxq");
                    item32[i] = mObj.getString("kssj");
                }

                for(int i=0; i<array.length(); i++){
                    Map<String,Object> item = new HashMap<>();
                    item.put("courseName",courseName[i]);
                    item.put("xuefen","学分：" + xuefen[i]);
                    item.put("chengji","成绩：" + chengji[i]);
                    item.put("item21","五分制等级：" + item21[i]);
                    item.put("item22","五分制绩点：" + item22[i]);
                    item.put("item31","学期：" + item31[i]);
                    item.put("item32","考试时间：" + item32[i]);
                    itemList.add(item);
                }

                msg.what = LOAD_SUCCEED;
                handler.sendMessage(msg);
            }

        });
    }
}
