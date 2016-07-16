package com.onecivilization.Optimize.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onecivilization.Optimize.Fragment.BlankFragment;
import com.onecivilization.Optimize.Fragment.CareListFragment;
import com.onecivilization.Optimize.R;
import com.onecivilization.Optimize.Util.AppManager;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private long lastBackPressedTime = 0L;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.setLanguage(PreferenceManager.getDefaultSharedPreferences(this).getInt("Language", AppManager.DEFAULT_LANGUAGE));
        setContentView(R.layout.activity_main);
        findViews();

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
                        Toast.makeText(MainActivity.this, R.string.problem, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                if (PreferenceManager.getDefaultSharedPreferences(this).getInt("Language", AppManager.CHINESE) == AppManager.CHINESE) {
                    AppManager.setLanguage(AppManager.ENGLISH);
                } else {
                    AppManager.setLanguage(AppManager.CHINESE);
                }
                AppManager.finishAllOtherActivities();
                recreate();
                return true;
            case R.id.action_tips:
                return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CareListFragment();
                case 1:
                    return new BlankFragment();
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
    }
}
