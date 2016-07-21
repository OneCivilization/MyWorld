package com.onecivilization.Optimize.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.onecivilization.Optimize.CustomView.GridColorPicker;
import com.onecivilization.Optimize.Database.DataManager;
import com.onecivilization.Optimize.Model.TextCare;
import com.onecivilization.Optimize.R;
import com.onecivilization.Optimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by CGZ on 2016/7/15.
 */
public class TextCarePropertiesFragment extends Fragment {

    private TextView createdTimeTextView;
    private TextView existedTimeTextView;
    private ToggleButton stateButton;
    private GridColorPicker colorPicker;
    private TextCare textCare;
    private long achievedTime = 0L;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textCare = (TextCare) DataManager.getInstance(getActivity()).getCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
        achievedTime = textCare.getAchievedTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_care_properties, container, false);
        createdTimeTextView = (TextView) view.findViewById(R.id.created_time);
        existedTimeTextView = (TextView) view.findViewById(R.id.existed_time);
        stateButton = (ToggleButton) view.findViewById(R.id.status_toggle_button);
        colorPicker = (GridColorPicker) view.findViewById(R.id.color_picker);
        createdTimeTextView.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(textCare.getCreateTime())));
        Calendar calendar = Calendar.getInstance();
        int dayNow = calendar.get(Calendar.DAY_OF_YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int yearNow = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(textCare.getCreateTime());
        int yearCreated = calendar.get(Calendar.YEAR);
        int monthCreated = calendar.get(Calendar.MONTH);
        int dayCreated = calendar.get(Calendar.DAY_OF_YEAR);
        int days = (int) (new Date(yearNow, monthNow, dayNow).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
        existedTimeTextView.setText(days + "  " + getString(R.string.days));
        if (textCare.isAchieved()) {
            colorPicker.setSelection(8);
        } else {
            colorPicker.setSelectionByColor(textCare.getColor());
        }
        colorPicker.setEnabled(!textCare.isAchieved());
        stateButton.setChecked(textCare.isAchieved());
        stateButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    colorPicker.setSelection(8);
                    colorPicker.setEnabled(false);
                    textCare.setAchievedTime(System.currentTimeMillis());
                } else {
                    colorPicker.setEnabled(true);
                    colorPicker.setSelectionByColor(textCare.getColor());
                    textCare.setAchievedTime(0L);
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (stateButton.isChecked()) {
            if (achievedTime == 0L) {
                textCare.setAchievedTime(System.currentTimeMillis());
            } else {
                textCare.setAchievedTime(achievedTime);
            }
        } else {
            textCare.setAchievedTime(0L);
            textCare.setColor(colorPicker.getSelectedColor());
        }
        DataManager.getInstance(getActivity()).updateCareItem(textCare);
    }
}
