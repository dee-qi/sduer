package com.example.sduhelper.Activities;

import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.SmartToast;

public class WebViewBrowserActivity extends BaseActivity {
    String url;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_browser);
        Toolbar toolbar = (Toolbar)findViewById(R.id.webview_toolbar);
        toolbar.setTitle("资讯浏览");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //获取要打开的url
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        //设置webView属性
        webView = (WebView)findViewById(R.id.news_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);//手势缩放
        webView.getSettings().setBuiltInZoomControls(true);//手势缩放
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
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
                SmartToast.make(WebViewBrowserActivity.this,"url已经成功复制到剪切板");
                break;
        }
        return true;
    }
}
