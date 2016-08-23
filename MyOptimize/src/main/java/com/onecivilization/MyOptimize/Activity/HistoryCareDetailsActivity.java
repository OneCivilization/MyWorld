package com.onecivilization.MyOptimize.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.onecivilization.MyOptimize.CustomView.TipsDialog;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Fragment.HistoryCareRecordsFragment;
import com.onecivilization.MyOptimize.Fragment.HistoryDescriptionFragment;
import com.onecivilization.MyOptimize.Fragment.HistoryNonperiodicCarePropertiesFragment;
import com.onecivilization.MyOptimize.Fragment.HistoryTextCarePropertiesFragment;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.R;

/**
 * Created by CGZ on 2016/7/18.
 */
public class HistoryCareDetailsActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText careItemTitle;
    private Care care;

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);
        careItemTitle = (EditText) findViewById(R.id.care_item_title);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_details);
        findViews();
        care = DataManager.getInstance().getHistoryCareList().get(getIntent().getIntExtra("careItemPosition", -1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        careItemTitle.setText(care.getTitle());
        careItemTitle.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history_care_item_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_warning)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataManager.getInstance().deleteHistoryCareItem(getIntent().getIntExtra("careItemPosition", -1));
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create().show();
                break;
            case R.id.action_unarchive:
                if (care.isAchieved()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.unarchive_warning)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataManager.getInstance().unarchiveCareItem(getIntent().getIntExtra("careItemPosition", -1));
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).create().show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.unarchive_tip)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataManager.getInstance().unarchiveCareItem(getIntent().getIntExtra("careItemPosition", -1));
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).create().show();
                }
                break;
            case R.id.action_tips:
                new TipsDialog(this, R.string.tips_history).show();
                break;
        }
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (care.getType()) {
                case Care.TEXT:
                    switch (position) {
                        case 0:
                            return new HistoryDescriptionFragment();
                        case 1:
                            return new HistoryTextCarePropertiesFragment();
                    }
                    break;
                case Care.NONPERIODIC:
                case Care.PERIODIC:
                case Care.SUB_PERIODIC:
                case Care.TIMELIMITED_PERIODIC:
                case Care.COMPLEX_PERIODIC:
                    switch (position) {
                        case 0:
                            return new HistoryDescriptionFragment();
                        case 1:
                            return new HistoryNonperiodicCarePropertiesFragment();
                        case 2:
                            return new HistoryCareRecordsFragment();
                    }
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            switch (care.getType()) {
                case Care.TEXT:
                    return 2;
                case Care.NONPERIODIC:
                case Care.PERIODIC:
                case Care.SUB_PERIODIC:
                case Care.TIMELIMITED_PERIODIC:
                case Care.COMPLEX_PERIODIC:
                    return 3;
            }
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (care.getType()) {
                case Care.TEXT:
                    switch (position) {
                        case 0:
                            return care.getDescriptionTitle();
                        case 1:
                            return getString(R.string.properties);
                    }
                    break;
                case Care.NONPERIODIC:
                case Care.PERIODIC:
                case Care.SUB_PERIODIC:
                case Care.TIMELIMITED_PERIODIC:
                case Care.COMPLEX_PERIODIC:
                    switch (position) {
                        case 0:
                            return care.getDescriptionTitle();
                        case 1:
                            return getString(R.string.properties);
                        case 2:
                            return getString(R.string.records);
                    }
                    break;
            }
            return null;
        }
    }

}
