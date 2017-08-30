package com.example.sduhelper.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.BaseActivity;

public class WebViewBrowserActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_browser);

        //获取要打开的url
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        //设置webView属性
        webView = (WebView)findViewById(R.id.news_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);//手势缩放
        webView.getSettings().setBuiltInZoomControls(true);//手势缩放
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
