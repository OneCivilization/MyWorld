package com.onecivilization.MyOptimize.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.onecivilization.MyOptimize.CustomView.TipsDialog;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

/**
 * Created by CGZ on 2016/8/11.
 */
public class SettingsActivity extends BaseActivity {

    private Toolbar toolbar;
    private Switch careItemAutoSortSwitch;
    private Switch problemItemAutoSortSwitch;
    private TextView backupTextView;
    private TextView languageTextView;

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        careItemAutoSortSwitch = (Switch) findViewById(R.id.care_auto_sort_switch);
        problemItemAutoSortSwitch = (Switch) findViewById(R.id.problem_auto_sort_switch);
        backupTextView = (TextView) findViewById(R.id.backup);
        languageTextView = (TextView) findViewById(R.id.language);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViews();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        careItemAutoSortSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).getBoolean("careItemAutoSort", true));
        careItemAutoSortSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).edit().putBoolean("careItemAutoSort", isChecked).apply();
                if (isChecked) {
                    DataManager.getInstance().sortCareListByState();
                } else {
                    DataManager.getInstance().sortCareListByOrder();
                }
            }
        });
        problemItemAutoSortSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).getBoolean("problemItemAutoSort", true));
        problemItemAutoSortSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).edit().putBoolean("problemItemAutoSort", isChecked).apply();
                if (isChecked) {
                    DataManager.getInstance().sortProblemListByRank();
                } else {
                    DataManager.getInstance().sortProblemListByOrder();
                }
            }
        });
        backupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SettingsBackupActivity.class));
            }
        });
        languageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SettingsLanguageActivity.class));
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
        new TipsDialog(this, R.string.tips_settings).show();
        return true;
    }
}
