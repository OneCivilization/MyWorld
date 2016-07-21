package com.onecivilization.Optimize.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onecivilization.Optimize.CustomView.HalfRingProgressBar;
import com.onecivilization.Optimize.Database.DataManager;
import com.onecivilization.Optimize.Model.NonperiodicCare;
import com.onecivilization.Optimize.R;
import com.onecivilization.Optimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by CGZ on 2016/7/20.
 */
public class CareProgressFragment extends Fragment {

    private HalfRingProgressBar progressBar;
    private TextView succeededTextView;
    private TextView failedTextView;
    private TextView createdTimeTextView;
    private NonperiodicCare care;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        care = (NonperiodicCare) DataManager.getInstance(getActivity()).getCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care_progress, container, false);
        progressBar = (HalfRingProgressBar) view.findViewById(R.id.progress_bar);
        succeededTextView = (TextView) view.findViewById(R.id.succeeded);
        failedTextView = (TextView) view.findViewById(R.id.failed);
        createdTimeTextView = (TextView) view.findViewById(R.id.created_time);
        progressBar.setProgressAndMax(care.getProgress(), care.getGoal());
        succeededTextView.setText(getString(R.string.succeeded) + care.getSucceeded());
        failedTextView.setText(getString(R.string.failed) + care.getFailed());
        final String createdTime = SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(care.getCreateTime()));
        createdTimeTextView.setText(createdTime);
        Calendar calendar = Calendar.getInstance();
        int dayNow = calendar.get(Calendar.DAY_OF_YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int yearNow = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(care.getCreateTime());
        int yearCreated = calendar.get(Calendar.YEAR);
        int monthCreated = calendar.get(Calendar.MONTH);
        int dayCreated = calendar.get(Calendar.DAY_OF_YEAR);
        int days = (int) (new Date(yearNow, monthNow, dayNow).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
        final String existedTime = days + "  " + getString(R.string.days);
        createdTimeTextView.setOnClickListener(new View.OnClickListener() {
            private boolean isShowingCreatedTime = true;

            @Override
            public void onClick(View v) {
                if (isShowingCreatedTime) {
                    createdTimeTextView.setText(existedTime);
                    isShowingCreatedTime = false;
                } else {
                    createdTimeTextView.setText(createdTime);
                    isShowingCreatedTime = true;
                }
            }
        });
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View signInView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sign_in, null);
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.sign_in)
                        .setView(signInView)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.confirm_, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RadioGroup recordTag = (RadioGroup) signInView.findViewById(R.id.record_tag);
                                switch (recordTag.getCheckedRadioButtonId()) {
                                    case R.id.succeeded:
                                        care.addRecord(true, getActivity());
                                        break;
                                    case R.id.failed:
                                        care.addRecord(false, getActivity());
                                        break;
                                }
                                progressBar.setProgress(care.getProgress());
                                succeededTextView.setText(getString(R.string.succeeded) + care.getSucceeded());
                                failedTextView.setText(getString(R.string.failed) + care.getFailed());
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
        return view;
    }
}
