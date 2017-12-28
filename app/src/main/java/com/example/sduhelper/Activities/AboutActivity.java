package com.example.sduhelper.Activities;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.BaseActivity;

public class AboutActivity extends BaseActivity implements View.OnClickListener{
    ImageView logo;
    int love;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initToolbar("关于");
        logo = (ImageView)findViewById(R.id.about_logo);
        logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //This is my love. Nobody touch this part.
        // 2017/09/01 in Shandong University
        love++;
        if(love == 7){
            new AlertDialog.Builder(this)
                    .setTitle("你永远不知道在哪里还藏着一句")
                    .setMessage("常心怡我爱你")
                    .show();
            love = 0;
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
