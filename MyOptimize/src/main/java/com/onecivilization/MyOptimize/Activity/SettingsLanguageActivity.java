package com.onecivilization.MyOptimize.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.onecivilization.MyOptimize.CustomView.TipsDialog;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.util.Locale;

/**
 * Created by CGZ on 2016/8/13.
 */
public class SettingsLanguageActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView chineseSimplified;
    private TextView english_us;
    private int currentLanguage;

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        chineseSimplified = (TextView) findViewById(R.id.chinese_simplified);
        english_us = (TextView) findViewById(R.id.english_us);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_language);
        findViews();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (AppManager.LOCALE.equals(Locale.CHINESE)) {
            chineseSimplified.setCompoundDrawablesWithIntrinsicBounds(R.drawable.china, 0, R.drawable.check_laguage, 0);
            currentLanguage = AppManager.CHINESE;
        } else {
            english_us.setCompoundDrawablesWithIntrinsicBounds(R.drawable.us, 0, R.drawable.check_laguage, 0);
            currentLanguage = AppManager.ENGLISH;
        }
        chineseSimplified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLanguage != AppManager.CHINESE) {
                    AppManager.setLanguage(AppManager.CHINESE);
                    english_us.setCompoundDrawablesWithIntrinsicBounds(R.drawable.us, 0, 0, 0);
                    chineseSimplified.setCompoundDrawablesWithIntrinsicBounds(R.drawable.china, 0, R.drawable.check_laguage, 0);
                    currentLanguage = AppManager.CHINESE;
                }
            }
        });
        english_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLanguage != AppManager.ENGLISH) {
                    AppManager.setLanguage(AppManager.ENGLISH);
                    chineseSimplified.setCompoundDrawablesWithIntrinsicBounds(R.drawable.china, 0, 0, 0);
                    english_us.setCompoundDrawablesWithIntrinsicBounds(R.drawable.us, 0, R.drawable.check_laguage, 0);
                    currentLanguage = AppManager.ENGLISH;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new TipsDialog(this, R.string.tips_settings_language).show();
        return true;
    }

}
