package com.onecivilization.MyOptimize.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.onecivilization.MyOptimize.R;

/**
 * Created by CGZ on 2016/8/17.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.contact_developer).setOnClickListener(this);
        findViewById(R.id.help_translate).setOnClickListener(this);
        findViewById(R.id.view_source_code).setOnClickListener(this);
        findViewById(R.id.support_developer).setOnClickListener(this);
        findViewById(R.id.rate_app).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.contact_developer:
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:1996cgz@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact：My Optimize");
                startActivity(intent);
                break;
            case R.id.help_translate:
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:1996cgz@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Translation：My Optimize");
                startActivity(intent);
                break;
            case R.id.view_source_code:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/OneCivilization/MyWorld"));
                startActivity(intent);
                break;
            case R.id.support_developer:
                break;
            case R.id.rate_app:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
                break;
        }
    }
}
