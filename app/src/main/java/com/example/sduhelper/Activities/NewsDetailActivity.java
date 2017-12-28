package com.example.sduhelper.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewsDetailActivity extends AppCompatActivity {
    private String url;
    private int id;
    private String api;

    private TextView title;
    private TextView site;
    private TextView time;
    private TextView content;
//    private LinearLayout attachmentsLayout;
//    private JSONArray attachmentsList;

    private String attachName;
    private String attachUrl;

    private final int LOADSUCCEED = 0;
    private final int LOADFAILED = 1;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADSUCCEED:
                    JSONObject o = (JSONObject)msg.obj;
                    try {
                        title.setText(o.getString("title"));
                        site.setText(o.getString("site"));
                        time.setText(o.getString("date"));
                        content.setText(o.getString("content"));
                        /*这部分用来实现下载，然而有问题。罢了罢了，先注释了再说。*/
//                        for(int i = 0; i<attachmentsList.length(); i++){
//                            final JSONObject attach = (JSONObject)attachmentsList.get(i);
//                            TextView tv = new TextView(NewsDetailActivity.this);
//
//                            final String fileName = attach.getString("title");
//                            attachName = fileName;
//                            tv.setText(fileName);
//                            final String url = attach.getString("url");
//                            attachUrl = url;
//
//                            tv.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if(ContextCompat.checkSelfPermission(NewsDetailActivity.this,
//                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//                                        ActivityCompat.requestPermissions(NewsDetailActivity.this,
//                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                                1);
//                                    } else {
//                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//                                        request.setDestinationInExternalPublicDir("/download/",fileName);
//                                        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
//                                        manager.enqueue(request);
//                                    }
//
//                                }
//                            });
//
//                            attachmentsLayout.addView(tv);
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case LOADFAILED:
                    SmartToast.make(NewsDetailActivity.this,"加载失败");
                    finish();
            }
        }
    };

    private void downloadAttach(){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(attachUrl));
        request.setDestinationInExternalPublicDir("/download/",attachName);
        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.news_detail_toolbar);
        toolbar.setTitle("资讯浏览");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        title = (TextView)findViewById(R.id.news_detail_title);
        site = (TextView)findViewById(R.id.news_detail_site);
        time = (TextView)findViewById(R.id.news_detail_time);
        content = (TextView)findViewById(R.id.news_detail_content);
//        attachmentsLayout = (LinearLayout)findViewById(R.id.news_detail_attachments);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        id = intent.getIntExtra("id",-1);
        api = intent.getStringExtra("api");
//        SmartToast.make(NewsDetailActivity.this,"url："+url+"\n"+id);
//        url = "https://sduonline.cn/isdu/news/api/?site=sduOnline&page=1";
//        id = 1;

        loadData();

    }

    private void loadData(){

        String mUrl = api + "&id="+id+"&content";
//        String mUrl = "https://sduonline.cn/isdu/news/api/?site=sduOnline&page=1&id=1&content";
        NetWorkUtil.get(mUrl, new Callback() {
            Message msg;
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                msg.what = LOADFAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                msg = new Message();
                String s = response.body().string();
                Log.d("@hhh", "onResponse: "+s);
                try {
                    JSONObject obj = new JSONObject(s);
                    msg.what = LOADSUCCEED;
//                    attachmentsList = obj.getJSONArray("attachment");
                    msg.obj = obj;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    msg.what = LOADFAILED;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            downloadAttach();
        } else {
            SmartToast.make(NewsDetailActivity.this,"读写文件请求被拒绝！无法下载");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webviewbrowser_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.share:
                String text2share = "我在SDUHelper上发现了个有趣新闻，快来看看吧！链接："+url;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, text2share);
                share.setType("text/plain");
                startActivity(share);
                break;
            case R.id.copy:
                ClipboardManager cbm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                cbm.setText(url);
                SmartToast.make(NewsDetailActivity.this,"url已经成功复制到剪切板");
                break;
        }
        return true;
    }
}


