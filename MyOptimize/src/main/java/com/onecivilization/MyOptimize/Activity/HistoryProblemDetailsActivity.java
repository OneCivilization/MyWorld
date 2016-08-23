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
import android.view.WindowManager;

import com.onecivilization.MyOptimize.CustomView.TipsDialog;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Fragment.ProblemContentFragment;
import com.onecivilization.MyOptimize.Fragment.ProblemPropertiesFragment;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.R;

/**
 * Created by CGZ on 2016/8/15.
 */
public class HistoryProblemDetailsActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Problem problem;

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_details);
        findViews();
        problem = DataManager.getInstance().getHistoryProblemList().get(getIntent().getIntExtra("historyProblemItemPosition", -1));
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                                DataManager.getInstance().deleteHistoryProblemItem(getIntent().getIntExtra("historyProblemItemPosition", -1));
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create().show();
                break;
            case R.id.action_unarchive:

                if (problem.isSolved()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.unarchive_warning)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataManager.getInstance().unarchiveProblemItem(getIntent().getIntExtra("historyProblemItemPosition", -1));
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
                                    DataManager.getInstance().unarchiveProblemItem(getIntent().getIntExtra("historyProblemItemPosition", -1));
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
            switch (position) {
                case 0:
                    return new ProblemContentFragment(true);
                case 1:
                    return new ProblemPropertiesFragment(true);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
