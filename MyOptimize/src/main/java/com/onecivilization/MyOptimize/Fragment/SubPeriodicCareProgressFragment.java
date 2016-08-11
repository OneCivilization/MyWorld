package com.onecivilization.MyOptimize.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.onecivilization.MyOptimize.Activity.CareDetailsActivity;
import com.onecivilization.MyOptimize.CustomView.HalfRingProgressBar;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.ComplexPeriodicCare;
import com.onecivilization.MyOptimize.Model.SubPeriodicCare;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by CGZ on 2016/7/28.
 */
public class SubPeriodicCareProgressFragment extends Fragment {

    private HalfRingProgressBar progressBar;
    private TextView existedTimeTextView;
    private TextView periodTextView;
    private TextView succeededTextView;
    private TextView failedTextView;
    private TextView punishmentTextView;
    private SubPeriodicCare care;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        care = (SubPeriodicCare) DataManager.getInstance().getCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care_progress, container, false);
        progressBar = (HalfRingProgressBar) view.findViewById(R.id.progress_bar);
        succeededTextView = (TextView) view.findViewById(R.id.succeeded);
        failedTextView = (TextView) view.findViewById(R.id.failed);
        existedTimeTextView = (TextView) view.findViewById(R.id.existed_time);
        periodTextView = (TextView) view.findViewById(R.id.period);
        progressBar.setProgressAndMax(care.getProgress(), care.getGoal());
        succeededTextView.setText(getString(R.string.succeeded) + care.getSucceeded());
        failedTextView.setText(getString(R.string.failed) + care.getFailed() + "   " + getString(R.string.sub_progress) + care.getSubProgressText());
        punishmentTextView = (TextView) view.findViewById(R.id.punishment);
        punishmentTextView.setText(getString(R.string.punishment) + care.getPunishment());
        final String createdTime = SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(care.getCreateTime()));
        Calendar calendar = Calendar.getInstance();
        int dayNow = calendar.get(Calendar.DAY_OF_YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int yearNow = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(care.getCreateTime());
        int yearCreated = calendar.get(Calendar.YEAR);
        int monthCreated = calendar.get(Calendar.MONTH);
        int dayCreated = calendar.get(Calendar.DAY_OF_YEAR);
        int days = (int) (new Date(yearNow, monthNow, dayNow).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
        final String existedTime;
        if (AppManager.LOCALE.equals(Locale.CHINESE)) {
            existedTime = "第 " + (days + 1) + " 天";
        } else {
            existedTime = "Day " + (days + 1);
        }
        final String period = care.getPeriodText();
        final String periodCount = care.getPeriodCountText();

        existedTimeTextView.setText(periodCount);
        existedTimeTextView.setOnClickListener(new View.OnClickListener() {
            private boolean isShowingPeriodCount = true;

            @Override
            public void onClick(View v) {
                if (isShowingPeriodCount) {
                    existedTimeTextView.setText(existedTime);
                    isShowingPeriodCount = false;
                } else {
                    existedTimeTextView.setText(periodCount);
                    isShowingPeriodCount = true;
                }
            }
        });
        periodTextView.setText(period);
        periodTextView.setOnClickListener(new View.OnClickListener() {
            private boolean isShowingCreatedTime = false;

            @Override
            public void onClick(View v) {
                if (isShowingCreatedTime) {
                    periodTextView.setText(period);
                    isShowingCreatedTime = false;
                } else {
                    periodTextView.setText(createdTime);
                    isShowingCreatedTime = true;
                }
            }
        });
        if (care.getType() == Care.SUB_PERIODIC) {
            progressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    care.addSubRecord();
                    Toast.makeText(getActivity(), R.string.signed_in, Toast.LENGTH_SHORT).show();
                    ((CareDetailsActivity) getActivity()).refreshFragments();
                }
            });
            progressBar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!care.deleteSubRecord()) {
                        Toast.makeText(getActivity(), R.string.sub_progress_zero, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.signed_out, Toast.LENGTH_SHORT).show();
                        ((CareDetailsActivity) getActivity()).refreshFragments();
                    }
                    return true;
                }
            });
        } else {
            progressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ComplexPeriodicCare) care).isLocked()) {
                        Toast.makeText(getActivity(), R.string.current_time_out_of_limitation, Toast.LENGTH_SHORT).show();
                    } else {
                        if (care.isSigned()) {
                            care.deleteSubRecord();
                        } else {
                            care.addSubRecord();
                        }
                        ((CareDetailsActivity) getActivity()).refreshFragments();
                    }
                }
            });
        }
        return view;
    }
}
