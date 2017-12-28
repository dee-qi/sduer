package com.example.sduhelper.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FeedBackActivity extends BaseActivity implements View.OnClickListener{

    private EditText feedBackTopic;
    private EditText feedBackContent;
    private EditText feedBackEmail;
    private EditText feedBackWechat;
    private EditText feedBackQQ;
    private Button feedBackSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        initToolbar("问题反馈");

        feedBackTopic = (EditText)findViewById(R.id.feedback_topic);
        feedBackContent = (EditText)findViewById(R.id.feedback_content);
        feedBackEmail = (EditText)findViewById(R.id.feedback_email);
        feedBackWechat = (EditText)findViewById(R.id.feedback_wechat);
        feedBackQQ = (EditText)findViewById(R.id.feedback_qq);
        feedBackSubmit = (Button) findViewById(R.id.feedback_submit);

        feedBackSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(feedBackContent.getText().toString().length() >= 100){
            Toast.makeText(this, "反馈字数不能多于100字！", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断逻辑有问题！
        if(!feedBackEmail.getText().toString().equals("")){
            if(!feedBackEmail.getText().toString().contains("@")) {
                Toast.makeText(this, "请输入正确的邮箱地址！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
//        Intent sendMail=new Intent(Intent.ACTION_SENDTO);
//        sendMail.setData(Uri.parse("mailto:support@mail.sdu.edu.com"));
//        sendMail.putExtra(Intent.EXTRA_SUBJECT, feedBackTopic.getText().toString());
//        StringBuilder sb = new StringBuilder();
//        sb.append("content:"+feedBackContent.getText().toString()+"\n");
//        sb.append("qq:"+feedBackQQ.getText().toString()+"\n");
//        sb.append("wx:"+feedBackWechat.getText().toString()+"\n");
//        sb.append("email:"+feedBackEmail.getText().toString()+"\n");
//        sendMail.putExtra(Intent.EXTRA_TEXT, sb.toString());
//        startActivity(sendMail);
//        this.finish();

        Map<String, String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append("<"+getResources().getString(R.string.version_code)+">");
        sb.append("topic:"+feedBackTopic.getText().toString()+"###");
        sb.append("content:"+feedBackContent.getText().toString()+"###");
        sb.append("qq:"+feedBackQQ.getText().toString()+"###");
        sb.append("wx:"+feedBackWechat.getText().toString()+"###");
        sb.append("email:"+feedBackEmail.getText().toString()+"###");
        map.put("content",sb.toString());
        map.put("clientType","SDUHELPER");
        String token = SharedPreferenceUtil.get(FeedBackActivity.this,"userInfo","token");
        NetWorkUtil.post(ApiUtil.getApi(FeedBackActivity.this,"api_feed_back"), map,token, callback);
    }
    Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SmartToast.make(FeedBackActivity.this,"发送失败！请检查网络！");
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String s = response.body().string();
            Log.d("@feedback@", "onResponse: "+s);
            try{
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("code") == 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SmartToast.make(FeedBackActivity.this,"谢谢您的反馈！我们会尽快解决！");
                        }
                    });
                } else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SmartToast.make(FeedBackActivity.this,"Sorry，服务器又崩了TAT");
                        }
                    });
                }
            } catch (JSONException e){
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartToast.make(FeedBackActivity.this,"Sorry，服务器抽风了");
                    }
                });
            }

        }
    };


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
