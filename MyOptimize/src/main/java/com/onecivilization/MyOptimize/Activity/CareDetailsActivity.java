package com.onecivilization.MyOptimize.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.onecivilization.MyOptimize.CustomView.TipsDialog;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Fragment.DescriptionFragment;
import com.onecivilization.MyOptimize.Fragment.NonperiodicCareProgressFragment;
import com.onecivilization.MyOptimize.Fragment.NonperiodicCareRecordsFragment;
import com.onecivilization.MyOptimize.Fragment.PeriodicCareProgressFragment;
import com.onecivilization.MyOptimize.Fragment.PeriodicCareRecordsFragment;
import com.onecivilization.MyOptimize.Fragment.SubPeriodicCareProgressFragment;
import com.onecivilization.MyOptimize.Fragment.TextCarePropertiesFragment;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by CGZ on 2016/7/14.
 */
public class CareDetailsActivity extends BaseActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText careItemTitle;
    private Care care;

    private void findViews() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);
        careItemTitle = (EditText) findViewById(R.id.care_item_title);
    }

    public void refreshFragments(){
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_details);
        findViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        care = DataManager.getInstance().getCareList().get(getIntent().getIntExtra("careItemPosition", -1));
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
            DataManager.getInstance().updateCareItem(care);
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
            case R.id.action_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                Uri u = Uri.fromFile(new File(shotScreen()));
                intent.putExtra(Intent.EXTRA_STREAM, u);
                startActivity(Intent.createChooser(intent, null));
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_warning)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataManager.getInstance().deleteCareItem(getIntent().getIntExtra("careItemPosition", -1));
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
                                    DataManager.getInstance().archiveCareItem(getIntent().getIntExtra("careItemPosition", -1));
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
                                                    DataManager.getInstance().archiveCareItem(getIntent().getIntExtra("careItemPosition", -1));
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
                switch (care.getType()) {
                    case Care.TEXT:
                        new TipsDialog(this, R.string.tips_text_care).show();
                        break;
                    case Care.NONPERIODIC:
                        new TipsDialog(this, R.string.tips_nonperiodic_care).show();
                        break;
                    default:
                        new TipsDialog(this, R.string.tips_periodic_care).show();
                }
                return true;
        }
        return false;
    }

    public String shotScreen() {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height_1 = appBarLayout.getHeight();
        Bitmap b1 = Bitmap.createBitmap(width, height_1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b1);
        appBarLayout.draw(canvas);
        NestedScrollView nestedScrollView = (NestedScrollView) ((SectionsPagerAdapter) viewPager.getAdapter()).currentFragment.getView().findViewById(R.id.nested_scroll_view);
        int height_2 = nestedScrollView.getChildAt(0).getHeight();
        Bitmap b2 = Bitmap.createBitmap(width, height_2, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(b2);
        nestedScrollView.getChildAt(0).draw(canvas);

        Bitmap result = Bitmap.createBitmap(width, height_1 + height_2 + appBarLayout.getTop(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(result);
        canvas.drawBitmap(b1, 0, appBarLayout.getTop(), null);
        canvas.drawBitmap(b2, 0, height_1 + appBarLayout.getTop(), null);

        File screenShootDirectory = new File(DataManager.APP_DIRECTORY + "/Screenshots");
        if (!screenShootDirectory.exists()) {
            screenShootDirectory.mkdir();
        }
        GregorianCalendar calendar = new GregorianCalendar();
        String picturePath = screenShootDirectory.getAbsolutePath() + String.format("/%d%02d%02d%02d%02d%02d_", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)) + care.getTitle() + ".png";
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(picturePath);
            result.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
        }
        return picturePath;
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private Fragment currentFragment;

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
                            return new NonperiodicCareProgressFragment();
                        case 2:
                            return new NonperiodicCareRecordsFragment();
                    }
                    break;
                case Care.PERIODIC:
                case Care.TIMELIMITED_PERIODIC:
                    switch (position) {
                        case 0:
                            return new DescriptionFragment();
                        case 1:
                            return new PeriodicCareProgressFragment();
                        case 2:
                            return new PeriodicCareRecordsFragment();
                    }
                    break;
                case Care.SUB_PERIODIC:
                case Care.COMPLEX_PERIODIC:
                    switch (position) {
                        case 0:
                            return new DescriptionFragment();
                        case 1:
                            return new SubPeriodicCareProgressFragment();
                        case 2:
                            return new PeriodicCareRecordsFragment();
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
                            return getString(R.string.progress);
                        case 2:
                            return getString(R.string.records);
                    }
                    break;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            currentFragment = (Fragment) object;
        }
    }

}
