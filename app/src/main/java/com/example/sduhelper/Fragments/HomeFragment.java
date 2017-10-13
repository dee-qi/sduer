package com.example.sduhelper.Fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.sduhelper.Activities.CurriculumActivity;
import com.example.sduhelper.Activities.LibraryActivity;
import com.example.sduhelper.Activities.SchoolCardActivity;
import com.example.sduhelper.Items.ItemCurriculum;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * This is UITest2
 * Created by qidi on 2017/7/16.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class HomeFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "HomeFragment";

    //课表
    private LinearLayout curriculum;
    private TextView curriculumDate;
    private TextView curriculumName;
    private TextView curriculumOrder;
    private TextView curriculumLocation;
    private TextView curriculumShowAll;
    private ListView curriculumList;
    private TextView curriculumNone;

    //校园卡
    private LinearLayout schoolcard,schoolcardBalanceGroup;
    private TextView schoolcardBalance,schoolCardShowAll,schoolcardTips;
    private ImageView schoolcardRefresh;
    private static String balance = "";

    //图书借阅
    private LinearLayout library;
    private TextView libraryName,libraryReturnDate,libraryNone,libraryShowAll;
    private ListView libraryList;
    private List<Map<String,String>> libList;

    private final int LIB_SUCCEED = 0x111;
    private final int LIB_NOTHING = 0x112;
    private final int LIB_FAILED = 0x113;
    private final int SCH_SUCCEED = 0x114;
    private final int SCH_FAILED = 0x115;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == LIB_SUCCEED) {
                libraryNone.setVisibility(View.GONE);
                libraryList.setVisibility(View.VISIBLE);
                if (getContext() != null){
                    SimpleAdapter adapter = new SimpleAdapter(getContext(), libList, R.layout.item_home_lib,
                            new String[]{"name", "returnDate"}, new int[]{R.id.home_lib_name, R.id.home_lib_return});
                libraryList.setAdapter(adapter);
                }
            } else if(msg.what == LIB_NOTHING){
                libraryNone.setText("无借阅");
            } else if(msg.what == LIB_FAILED){
                libraryNone.setText("加载失败");
            } else if(msg.what == SCH_SUCCEED){
                schoolcardBalanceGroup.setVisibility(View.VISIBLE);
                schoolcardTips.setVisibility(View.GONE);
                schoolcardBalance.setText((String)msg.obj);
                Log.d("@schoolcard@", "handleMessage: handler SUCCEED");
                balance = (String)msg.obj;
            } else if(msg.what == SCH_FAILED){
                Log.d("@schoolcard@", "handleMessage: handler FAILED");
                schoolcardTips.setText("加载失败");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        Log.d(TAG, "loadCurriculumToday: 1223123123");
        //绑定布局文件（真TM操蛋）
        curriculum = (LinearLayout)v.findViewById(R.id.home_curriculum);
        curriculumDate = (TextView)v.findViewById(R.id.home_curriculum_date);
        curriculumShowAll = (TextView)v.findViewById(R.id.home_curriculum_show_all);
        curriculumList = (ListView) v.findViewById(R.id.home_curriculum_list);
        curriculumNone = (TextView)v.findViewById(R.id.home_curriculum_none);

        schoolcard = (LinearLayout)v.findViewById(R.id.home_schoolcard);

        schoolcardBalance = (TextView)v.findViewById(R.id.home_schoolcard_balance);
        schoolcardTips = (TextView)v.findViewById(R.id.home_schoolcard_tips);
        schoolcardBalanceGroup = (LinearLayout)v.findViewById(R.id.home_schoolcard_balance_group);
        schoolCardShowAll = (TextView)v.findViewById(R.id.home_schoolcard_show_all);
        schoolcardRefresh = (ImageView)v.findViewById(R.id.home_schoolcard_refresh);

        library = (LinearLayout)v.findViewById(R.id.home_library);
        libraryList = (ListView)v.findViewById(R.id.home_library_list);
        libraryNone = (TextView)v.findViewById(R.id.home_library_none);
        libraryShowAll = (TextView)v.findViewById(R.id.home_library_show_all);

        //设置ClickListener
        curriculumShowAll.setOnClickListener(this);
        libraryShowAll.setOnClickListener(this);
        schoolCardShowAll.setOnClickListener(this);
        schoolcardRefresh.setOnClickListener(this);

        loadCurriculumToday();
        loadSchoolCard();
        loadLib();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurriculumToday();
        loadSchoolCard();
        loadLib();
    }

    private void loadCurriculumToday(){
        if(Information.getCurrentWeekCount() == -1){
            curriculumDate.setText(Information.getCurrentDate()+"    "+"假期");
            curriculumList.setVisibility(View.GONE);
            curriculumNone.setVisibility(View.VISIBLE);
            curriculumNone.setText("当前是假期~课表君也要放假啦");
        } else {
            Log.d(TAG, "loadCurriculumToday: 非-1");
            curriculumDate.setText(Information.getCurrentDate() + "    " + "第" + Information.getCurrentWeekCount() + "周");
            if (Information.isOnTrial) {
                return;
            }

            ArrayList<ItemCurriculum> classList = (ArrayList<ItemCurriculum>) SharedPreferenceUtil.getObj(getContext(), "curriculum", "list");
            //获取到的list为空（一般是第一次加载）
            if (classList == null) {
                curriculumList.setVisibility(View.GONE);
                curriculumNone.setVisibility(View.VISIBLE);
                curriculumNone.setText("当前本地课表为空！请打开课表功能加载课程，或者检查教务系统！");
            } else {
                //正常情况
                curriculumList.setVisibility(View.VISIBLE);
                curriculumNone.setVisibility(View.GONE);
                int currentWeek = Information.getCurrentWeekCount();
                int weekday = Information.getCurrentWeekday();
                boolean hasClass = false;
                List<Map<String, Object>> mapList = new LinkedList<>();
                Log.d(TAG, "loadCurriculumToday: classList size is" + classList.size());
                for (int i = 0; i < classList.size(); i++) {
                    if (classList.get(i).isOnThisDay(currentWeek, weekday)) {
                        hasClass = true;
                        ItemCurriculum item = classList.get(i);
                        Map<String, Object> map = new HashMap<>();
                        map.put("order", "第" + item.getOrder() + "节");
                        map.put("name", item.getName());
                        map.put("location", item.getLocation());
                        mapList.add(map);
                        Log.d(TAG, "loadCurriculumToday: add " + item.getName());
                    }
                }

                SimpleAdapter adapter = new SimpleAdapter(getContext(), mapList, R.layout.item_home_curriculum,
                        new String[]{"order", "name", "location"},
                        new int[]{R.id.home_curriculum_order, R.id.home_curriculum_name, R.id.home_curriculum_location});
                curriculumList.setAdapter(adapter);
                setListViewHeightBasedOnItems(curriculumList);

                //今日无课的情况
                if (!hasClass) {
                    curriculumList.setVisibility(View.GONE);
                    curriculumNone.setVisibility(View.VISIBLE);
                    curriculumNone.setText("今日无课！快去刷图书馆吧！");
                }
            }
        }
    }

    private void loadSchoolCard(){

        schoolcardBalanceGroup.setVisibility(View.GONE);
        schoolcardTips.setVisibility(View.VISIBLE);
        schoolcardTips.setText("加载中");
        if(SharedPreferenceUtil.get(getContext(),"userInfo","cardNum")
                        .equals("")){
            schoolcardTips.setText("请先进入校园卡功能绑定卡号");
        } else if(balance.equals("")){
//            SmartToast.make(getContext(),"请求校园卡数据");
            String url = String.format(ApiUtil.getApi(getContext(), "api_school_card_getInfo"),
                    SharedPreferenceUtil.get(getContext(),"userInfo","cardNum"),
                    SharedPreferenceUtil.get(getContext(),"userInfo","pwd"));
//            String url = String.format(ApiUtil.getApi(getContext(),"api_school_card_getInfo"),
//                    "268210","@@@@@@");
            NetWorkUtil.get(url, new Callback() {
                Message msg = new Message();
                @Override
                public void onFailure(Call call, IOException e) {
                    msg.what = SCH_FAILED;
                    handler.sendMessage(msg);
                    Log.d("@schoolcard@", "onFailure: "+e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s = response.body().string();
                    if(s.contains("验证码")){
                        Log.d(TAG, "onResponse: return");
                        return;
                    }
                    try{
                        Log.d("@schoolcard@", "onResponse: schoolcard "+s);
                        JSONArray array = new JSONArray(s);
                        JSONObject obj = array.getJSONObject(2);
                        msg.obj = obj.get("value");
                        msg.what = SCH_SUCCEED;
                    } catch (JSONException e){
                        msg.what = SCH_FAILED;
                        Log.d("@schoolcard@", "onResponse: "+e.getMessage());
                        e.printStackTrace();
                    } finally {
                        handler.sendMessage(msg);
                    }
                }
            });
        } else if(!balance.equals("")){
            schoolcardBalanceGroup.setVisibility(View.VISIBLE);
            schoolcardTips.setVisibility(View.GONE);
            schoolcardBalance.setText(balance);
        }
    }

    private void loadLib(){
        libraryList.setVisibility(View.GONE);
        libraryNone.setVisibility(View.VISIBLE);
        if(SharedPreferenceUtil.get(getContext(),"userInfo","isLibraryBound").equals("false")){
            libraryNone.setText("请先进入图书馆功能绑定校园卡账号及密码");
        } else {
            libraryNone.setText("正在加载");
            String url = ApiUtil.getApi(getContext(),"api_lib_borrowed")
                    + SharedPreferenceUtil.get(getContext(), "userInfo", "id");
            String token = SharedPreferenceUtil.get(getContext(), "userInfo", "token");
            NetWorkUtil.get(url, token, new Callback() {
                Message msg = new Message();

                @Override
                public void onFailure(Call call, IOException e) {
                    msg.what = LIB_FAILED;
                    Log.d(TAG, "onFailure: a"+e.getMessage());
                    handler.sendMessage(msg);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s = response.body().string();
                    Log.d(TAG, "onResponse: " + s);
                    try {
                        JSONObject o = new JSONObject(s);
                        if (o.getInt("code") == 0) {
                            libList = new LinkedList<Map<String, String>>();
                            msg.what = LIB_SUCCEED;
                            JSONArray array = o.getJSONArray("obj");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject book = array.getJSONObject(i);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("name", book.getString("name"));
                                map.put("returnDate",
                                        "应还日期:"+
                                        Information.stamp2Date(Long.parseLong(book.getString("retDate"))));
                                libList.add(map);
                            }
                        } else if (o.getString("msg").contains("未借阅")) {
                            msg.what = LIB_NOTHING;
                        } else {
                            msg.what = LIB_FAILED;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        handler.sendMessage(msg);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.home_curriculum_show_all:
                if(Information.isOnTrial){
                    SmartToast.make(getContext(),"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(v.getContext(), CurriculumActivity.class);
                startActivity(intent);
                break;
            case R.id.home_library_show_all:
                if(Information.isOnTrial){
                    SmartToast.make(getContext(),"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(v.getContext(), LibraryActivity.class);
                startActivity(intent);
                break;
            case R.id.home_schoolcard_show_all:
                if(Information.isOnTrial){
                    SmartToast.make(getContext(),"试用模式无法使用该功能！");
                    break;
                }
                intent = new Intent(v.getContext(), SchoolCardActivity.class);
                startActivity(intent);
                break;
            case R.id.home_schoolcard_refresh:
                balance = "";
                loadSchoolCard();
                SmartToast.make(getContext(),"正在刷新\n请勿频繁刷新，两小时内超过5次访问会被拒绝");
            default:
                break;
        }
    }
    private void setListViewHeightBasedOnItems(ListView listView){
        ListAdapter adapter = listView.getAdapter();
        if(adapter.getCount() == 0)
            return;
        if(adapter != null){
            int total = adapter.getCount();
            View v = adapter.getView(0,null,listView);
            v.measure(0,View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = total*v.getMeasuredHeight();
            listView.setLayoutParams(params);
        }
    }
}
