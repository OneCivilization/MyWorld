package com.onecivilization.MyOptimize.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.onecivilization.MyOptimize.CustomView.TipsDialog;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Fragment.ProblemContentFragment;
import com.onecivilization.MyOptimize.Fragment.ProblemPropertiesFragment;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by CGZ on 2016/8/15.
 */
public class ProblemDetailsActivity extends BaseActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Problem problem;

    private void findViews() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_details);
        findViews();
        problem = DataManager.getInstance().getProblemList().get(getIntent().getIntExtra("problemItemPosition", -1));
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
    protected void onDestroy() {
        super.onDestroy();
        DataManager.getInstance().updateProblemItem(problem);
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
                                DataManager.getInstance().deleteProblemItem(getIntent().getIntExtra("problemItemPosition", -1));
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create().show();
                return true;
            case R.id.action_archive:
                if (problem.isSolved()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.archive_confirm)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataManager.getInstance().archiveProblemItem(getIntent().getIntExtra("problemItemPosition", -1));
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).create().show();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.archive_warning_problem)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDialog.Builder(ProblemDetailsActivity.this)
                                            .setTitle(R.string.give_up_confirm)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DataManager.getInstance().archiveProblemItem(getIntent().getIntExtra("problemItemPosition", -1));
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
                new TipsDialog(this, R.string.tips_problem).show();
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
        ScrollView scrollView = (ScrollView) ((SectionsPagerAdapter) viewPager.getAdapter()).currentFragment.getView().findViewById(R.id.scroll_view);
        int height_2 = scrollView.getChildAt(0).getHeight();
        Bitmap b2 = Bitmap.createBitmap(width, height_2, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(b2);
        scrollView.getChildAt(0).draw(canvas);

        Bitmap result = Bitmap.createBitmap(width, height_1 + height_2, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(result);
        canvas.drawBitmap(b1, 0, 0, null);
        canvas.drawBitmap(b2, 0, height_1, null);

        File screenShootDirectory = new File(DataManager.APP_DIRECTORY + "/Screenshots");
        if (!screenShootDirectory.exists()) {
            screenShootDirectory.mkdir();
        }
        GregorianCalendar calendar = new GregorianCalendar();
        String picturePath = screenShootDirectory.getAbsolutePath() + String.format("/%d%02d%02d%02d%02d%02d_", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)) + problem.getTitle() + ".png";
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(picturePath);
            result.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return picturePath;
    }

    public void refreshFragments() {
        View titleIndicator = viewPager.findViewById(R.id.title_indicator);
        switch (problem.getRank()) {
            case Problem.LOW:
                titleIndicator.setBackgroundColor(getResources().getColor(R.color.state_true));
                break;
            case Problem.NORMAL:
                titleIndicator.setBackgroundColor(getResources().getColor(R.color.state_false));
                break;
            case Problem.HIGH:
                titleIndicator.setBackgroundColor(getResources().getColor(R.color.state_warning));
                break;
            case Problem.EXTRA_HIGH:
                titleIndicator.setBackgroundColor(getResources().getColor(R.color.state_extra_high));
                break;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment currentFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ProblemContentFragment();
                case 1:
                    return new ProblemPropertiesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            currentFragment = (Fragment) object;
        }
    }
}
