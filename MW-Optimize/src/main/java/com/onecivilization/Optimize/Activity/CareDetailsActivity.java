package com.onecivilization.Optimize.Activity;

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

import com.onecivilization.Optimize.Database.DataManager;
import com.onecivilization.Optimize.Fragment.BlankFragment;
import com.onecivilization.Optimize.Fragment.CareProgressFragment;
import com.onecivilization.Optimize.Fragment.DescriptionFragment;
import com.onecivilization.Optimize.Fragment.TextCarePropertiesFragment;
import com.onecivilization.Optimize.Model.Care;
import com.onecivilization.Optimize.R;

/**
 * Created by CGZ on 2016/7/14.
 */
public class CareDetailsActivity extends BaseActivity {

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
        care = DataManager.getInstance(this).getCareList().get(getIntent().getIntExtra("careItemPosition", -1));
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
        if (care.getType() != Care.TEXT) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String title = careItemTitle.getText().toString();
        if (!title.equals(care.getTitle()) && !title.equals("")) {
            care.setTitle(title);
            DataManager.getInstance(this).updateCareItem(care);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_care_item_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_warning)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataManager.getInstance(CareDetailsActivity.this).deleteCareItem(getIntent().getIntExtra("careItemPosition", -1));
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create().show();
                return true;
            case R.id.action_archive:

                if (care.isAchieved()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.archive_confirm)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataManager.getInstance(CareDetailsActivity.this).archiveCareItem(getIntent().getIntExtra("careItemPosition", -1));
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).create().show();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.archive_warning)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDialog.Builder(CareDetailsActivity.this)
                                            .setTitle(R.string.give_up_confirm)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DataManager.getInstance(CareDetailsActivity.this).archiveCareItem(getIntent().getIntExtra("careItemPosition", -1));
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, null).create().show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).create().show();
                }
                return true;
            case R.id.action_tips:
                return true;
        }
        return false;
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
                            return new DescriptionFragment();
                        case 1:
                            return new TextCarePropertiesFragment();
                    }
                    break;
                case Care.NONPERIODIC:
                    switch (position) {
                        case 0:
                            return new DescriptionFragment();
                        case 1:
                            return new CareProgressFragment();
                        case 2:
                            return new BlankFragment();
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
                    switch (position) {
                        case 0:
                            return care.getDescriptionTitle();
                        case 1:
                            return getString(R.string.progress);
                        case 2:
                            return getString(R.string.records);
                    }
                    break;
            }
            return null;
        }
    }

}
