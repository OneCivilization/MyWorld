package com.onecivilization.Optimize.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onecivilization.Optimize.Database.DataManager;
import com.onecivilization.Optimize.Model.TextCare;
import com.onecivilization.Optimize.R;
import com.onecivilization.Optimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by CGZ on 2016/7/18.
 */
public class HistoryTextCarePropertiesFragment extends Fragment {

    private TextView createdTimeTextView;
    private TextView achievedTimeTextView;
    private TextView lastedTimeTextView;
    private TextView colorTextView;
    private TextView archivedTextView;
    private TextCare textCare;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textCare = (TextCare) DataManager.getInstance(getActivity()).getHistoryCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_text_care_properties, container, false);
        createdTimeTextView = (TextView) view.findViewById(R.id.created_time);
        achievedTimeTextView = (TextView) view.findViewById(R.id.achieved_time);
        lastedTimeTextView = (TextView) view.findViewById(R.id.lasted_time);
        colorTextView = (TextView) view.findViewById(R.id.color_text_view);
        archivedTextView = (TextView) view.findViewById(R.id.archived_time);
        archivedTextView.setText(getString(R.string.archived_at) + SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(textCare.getArchivedTime())));
        createdTimeTextView.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(textCare.getCreateTime())));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(textCare.getCreateTime());
        int yearCreated = calendar.get(Calendar.YEAR);
        int monthCreated = calendar.get(Calendar.MONTH);
        int dayCreated = calendar.get(Calendar.DAY_OF_YEAR);
        if (textCare.isAchieved()) {
            achievedTimeTextView.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(textCare.getAchievedTime())));
            calendar.setTimeInMillis(textCare.getAchievedTime());
            int dayAchieved = calendar.get(Calendar.DAY_OF_YEAR);
            int yearAchieved = calendar.get(Calendar.YEAR);
            int monthAchieved = calendar.get(Calendar.MONTH);
            int days = (int) (new Date(yearAchieved, monthAchieved, dayAchieved).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
            lastedTimeTextView.setText(days + "  " + getString(R.string.days));
        } else {
            achievedTimeTextView.setTextColor(getResources().getColor(R.color.state_warning));
            achievedTimeTextView.setText(getString(R.string.unachieved));
            calendar.setTimeInMillis(textCare.getArchivedTime());
            int dayArchived = calendar.get(Calendar.DAY_OF_YEAR);
            int yearArchived = calendar.get(Calendar.YEAR);
            int monthArchived = calendar.get(Calendar.MONTH);
            new Date().setDate(5);
            int days = (int) (new Date(yearArchived, monthArchived, dayArchived).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
            lastedTimeTextView.setText(days + "  " + getString(R.string.days));
        }
        colorTextView.setBackgroundColor(textCare.getColor());
        return view;
    }

}
