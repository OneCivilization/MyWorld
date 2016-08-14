package com.onecivilization.MyOptimize.Util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.onecivilization.MyOptimize.Activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by CGZ on 2016/7/11.
 */
public class AppManager extends Application {

    public static final int DEFAULT_LANGUAGE = 0;
    public static final int CHINESE = 1;
    public static final int ENGLISH = 2;
    public static Locale LOCALE;
    private static Context context;
    private static List<BaseActivity> activities = new ArrayList<>();

    public static Context getContext() {
        return context;
    }

    public static void addActivity(BaseActivity activity) {
        activities.add(activity);
    }

    public static void removeActivity(BaseActivity activity) {
        activities.remove(activity);
    }

    public static void finishAllActivities() {
        for (BaseActivity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void finishAllOtherActivities() {
        int length = activities.size();
        for (int i = 0; i < length - 1; i++) {
            activities.get(i).finish();
        }
    }

    public static void setLanguage(int language) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        switch (language) {
            case CHINESE:
                if (!config.locale.equals(Locale.CHINESE)) {
                    config.locale = Locale.CHINESE;
                    resources.updateConfiguration(config, resources.getDisplayMetrics());
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("Language", language).apply();
                }
                break;
            case ENGLISH:
                if (!config.locale.equals(Locale.ENGLISH)) {
                    config.locale = Locale.ENGLISH;
                    resources.updateConfiguration(config, resources.getDisplayMetrics());
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("Language", language).apply();
                }
                break;
            case DEFAULT_LANGUAGE:
                if (config.locale.getLanguage().equals("zh")) {
                    config.locale = Locale.CHINESE;
                    resources.updateConfiguration(config, resources.getDisplayMetrics());
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("Language", CHINESE).apply();
                } else {
                    config.locale = Locale.ENGLISH;
                    resources.updateConfiguration(config, resources.getDisplayMetrics());
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("Language", ENGLISH).apply();
                }
                break;
        }
        LOCALE = config.locale;
        for (Activity activity : activities) {
            activity.recreate();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setLanguage(PreferenceManager.getDefaultSharedPreferences(this).getInt("Language", AppManager.DEFAULT_LANGUAGE));
    }

}
