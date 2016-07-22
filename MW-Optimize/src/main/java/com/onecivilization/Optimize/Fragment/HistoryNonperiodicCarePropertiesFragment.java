package com.onecivilization.Optimize.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onecivilization.Optimize.Database.DataManager;
import com.onecivilization.Optimize.Model.NonperiodicCare;
import com.onecivilization.Optimize.R;
import com.onecivilization.Optimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by CGZ on 2016/7/22.
 */
public class HistoryNonperiodicCarePropertiesFragment extends Fragment {

    private TextView createdTimeTextView;
    private TextView percentageTextView;
    private TextView progressTextView;
    private TextView succeededTextView;
    private TextView failedTextView;
    private TextView punishmentTextView;
    private TextView achievedTimeTextView;
    private TextView archivedTimeTextView;
    private TextView lastedTimeTextView;
    private NonperiodicCare care;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        care = (NonperiodicCare) DataManager.getInstance(getActivity()).getHistoryCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_nonperiodic_care_properties, container, false);
        createdTimeTextView = (TextView) view.findViewById(R.id.created_time);
        percentageTextView = (TextView) view.findViewById(R.id.percentage);
        progressTextView = (TextView) view.findViewById(R.id.progress);
        succeededTextView = (TextView) view.findViewById(R.id.succeeded);
        failedTextView = (TextView) view.findViewById(R.id.failed);
        punishmentTextView = (TextView) view.findViewById(R.id.punishment);
        achievedTimeTextView = (TextView) view.findViewById(R.id.achieved_time);
        archivedTimeTextView = (TextView) view.findViewById(R.id.archived_time);
        lastedTimeTextView = (TextView) view.findViewById(R.id.lasted_time);
        createdTimeTextView.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(care.getCreateTime())));
        if (care.getProgress() > 0) {
            progressTextView.setTextColor(0xff43ba08);
            percentageTextView.setTextColor(0xff43ba08);
        } else if (care.getProgress() < 0) {
            progressTextView.setTextColor(Color.RED);
            percentageTextView.setTextColor(Color.RED);
        }
        percentageTextView.setText(String.format("%.2f%%", care.getPercentage()));
        progressTextView.setText(care.getProgress() + " / " + care.getGoal());
        if (care.getSucceeded() > 0) {
            succeededTextView.setTextColor(0xff43ba08);
        }
        succeededTextView.setText(String.valueOf(care.getSucceeded()));
        if (care.getFailed() > 0) {
            failedTextView.setTextColor(Color.RED);
        }
        failedTextView.setText(String.valueOf(care.getFailed()));
        if (care.getPunishment() > 0) {
            punishmentTextView.setTextColor(Color.BLACK);
        }
        punishmentTextView.setText(String.valueOf(care.getPunishment()));
        archivedTimeTextView.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(care.getArchivedTime())));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(care.getCreateTime());
        int yearCreated = calendar.get(Calendar.YEAR);
        int monthCreated = calendar.get(Calendar.MONTH);
        int dayCreated = calendar.get(Calendar.DAY_OF_YEAR);
        if (care.isAchieved()) {
            achievedTimeTextView.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(care.getAchievedTime())));
            calendar.setTimeInMillis(care.getAchievedTime());
            int dayAchieved = calendar.get(Calendar.DAY_OF_YEAR);
            int yearAchieved = calendar.get(Calendar.YEAR);
            int monthAchieved = calendar.get(Calendar.MONTH);
            int days = (int) (new Date(yearAchieved, monthAchieved, dayAchieved).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
            lastedTimeTextView.setText(days + "  " + getString(R.string.days));
        } else {
            achievedTimeTextView.setTextColor(Color.RED);
            achievedTimeTextView.setText(getString(R.string.unachieved));
            calendar.setTimeInMillis(care.getArchivedTime());
            int dayArchived = calendar.get(Calendar.DAY_OF_YEAR);
            int yearArchived = calendar.get(Calendar.YEAR);
            int monthArchived = calendar.get(Calendar.MONTH);
            new Date().setDate(5);
            int days = (int) (new Date(yearArchived, monthArchived, dayArchived).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
            lastedTimeTextView.setText(days + "  " + getString(R.string.days));
        }
        return view;
    }
}
