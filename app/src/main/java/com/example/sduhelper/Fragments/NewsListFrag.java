package com.example.sduhelper.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.sduhelper.Adapters.NewsAdapter;
import com.example.sduhelper.Items.NewsItem;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This is UITest2
 * Created by qidi on 2017/7/16.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

//该Fragment用于展示四种新闻。通过setApi()方法来显示不同的新闻列表
public class NewsListFrag extends Fragment {

    private List<NewsItem> newsItemList = new ArrayList<>();
    private LinearLayout emptyView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private String api = "defalut api";

    private final int LOAD_SUCCEED = 0x123;
    private final int LOAD_FAILED = 0x124;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            refreshLayout.setRefreshing(false);
            if(msg.what == LOAD_FAILED){
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                if(getContext() != null) {
                    SmartToast.make(getContext(), "加载失败！");
                }
            } else if(msg.what == LOAD_SUCCEED){
                try{
                    String resp = (String) msg.obj;
                    JSONArray jsonArray = new JSONArray(resp);
                    for(int k = 0; k <jsonArray.length(); k++){
                        JSONObject object = jsonArray.getJSONObject(k);
                        newsItemList.add(new NewsItem(object.getString("title"),
                                object.getString("date"),
                                object.getString("block"),
                                object.getString("url"),k));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                NewsAdapter adapter = new NewsAdapter(newsItemList,api);
                recyclerView.setAdapter(adapter);
            }
            if(msg.arg1 == REFRESH){
//                SmartToast.make(getContext(),"刷新成功!");
            } else if(msg.arg1 == FIRST_LOAD){
//                SmartToast.make(getContext(),"加载成功!");
            }
        }
    };

    public void setApi(String api){
        this.api = api;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_list,container,false);
        emptyView = (LinearLayout)v.findViewById(R.id.empty_view);

        recyclerView = (RecyclerView)v.findViewById(R.id.news_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),1));
        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(v.getResources().getColor(R.color.themeColor));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                        initNews(REFRESH);
            }
        });
        initNews(FIRST_LOAD);
//        StaggeredGridLayoutManager mlayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        return v;
    }

    private int FIRST_LOAD = 0x225;//第一次加载
    private int REFRESH = 0x226;//刷新

    private void initNews(final int loadType){

        if(!newsItemList.isEmpty())
            newsItemList.clear();
        refreshLayout.setRefreshing(true);
        emptyView.setVisibility(View.INVISIBLE);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(api)
                .build();
        client.newCall(request).enqueue(new Callback() {

            Message msg ;

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("@hhh", "onFailure: ");
                msg = new Message();
                msg.what = LOAD_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("@hhh", "onFailure: nnn");
                msg = new Message();
                msg.what = LOAD_SUCCEED;
                msg.arg1 = loadType;
                msg.obj = response.body().string();
                handler.sendMessage(msg);
            }
        });

    }
}
