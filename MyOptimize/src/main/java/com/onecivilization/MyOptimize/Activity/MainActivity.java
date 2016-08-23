package com.onecivilization.MyOptimize.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onecivilization.MyOptimize.CustomView.TipsDialog;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Fragment.CareListFragment;
import com.onecivilization.MyOptimize.Fragment.HistoryCareListFragment;
import com.onecivilization.MyOptimize.Fragment.HistoryProblemListFragment;
import com.onecivilization.MyOptimize.Fragment.ProblemListFragment;
import com.onecivilization.MyOptimize.R;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private long lastBackPressedTime = 0L;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int navigationPosition = 0;
    private AlarmManager alarmManager;
    private RefreshBroadcastReceiver refreshBroadcastReceiver;

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void addRefreshTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, DataManager.getInstance().getNextRefreshTime(),
                    PendingIntent.getBroadcast(this, 0, new Intent("com.onecivilization.myoptimize.refreshCareList"), 0));
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, DataManager.getInstance().getNextRefreshTime(),
                    PendingIntent.getBroadcast(this, 0, new Intent("com.onecivilization.myoptimize.refreshCareList"), 0));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        tabLayout.setupWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, NewCareItemActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, NewProblemItemActivity.class));
                        break;
                }
            }
        });

        refreshBroadcastReceiver = new RefreshBroadcastReceiver();
        registerReceiver(refreshBroadcastReceiver, new IntentFilter("com.onecivilization.myoptimize.refreshCareList"));
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addRefreshTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
        alarmManager.cancel(PendingIntent.getBroadcast(this, 0, new Intent("com.onecivilization.myoptimize.refreshCareList"), 0));
        System.gc();
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - lastBackPressedTime < 2000) {
            finish();
        } else {
            lastBackPressedTime = now;
            Toast.makeText(this, R.string.exit_warning, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refreshBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (navigationPosition) {
            case 0:
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        new TipsDialog(this, R.string.tips_main_1).show();
                        break;
                    case 1:
                        new TipsDialog(this, R.string.tips_main_2).show();
                        break;
                }
                break;
            case 1:
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        new TipsDialog(this, R.string.tips_main_3).show();
                        break;
                    case 1:
                        new TipsDialog(this, R.string.tips_main_4).show();
                        break;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_home:
                toolbar.setTitle(R.string.app_name);
                navigationPosition = 0;
                viewPager.getAdapter().notifyDataSetChanged();
                fab.setVisibility(View.VISIBLE);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_archived:
                toolbar.setTitle(R.string.archive);
                navigationPosition = 1;
                viewPager.getAdapter().notifyDataSetChanged();
                fab.setVisibility(View.GONE);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_share:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                startActivity(Intent.createChooser(intent, null));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_feedback:
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:1996cgz@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback：My Optimize");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.email_not_supported, Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }
        return false;
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (navigationPosition) {
                case 0:
                    switch (position) {
                        case 0:
                            return new CareListFragment();
                        case 1:
                            return new ProblemListFragment();
                    }
                    break;
                case 1:
                    switch (position) {
                        case 0:
                            return new HistoryCareListFragment();
                        case 1:
                            return new HistoryProblemListFragment();
                    }
                    break;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.care);
                case 1:
                    return getString(R.string.problem);
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public class RefreshBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            viewPager.getAdapter().notifyDataSetChanged();
            addRefreshTask();
        }
    }

}
