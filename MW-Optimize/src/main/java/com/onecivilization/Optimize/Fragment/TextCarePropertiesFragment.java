package com.onecivilization.Optimize.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.onecivilization.Optimize.CustomView.GridColorPickerView;
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
    private GridColorPickerView colorPicker;
    private TextCare textCare;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textCare = (TextCare) DataManager.getInstance(getActivity()).getCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_care_properties, container, false);
        createdTimeTextView = (TextView) view.findViewById(R.id.created_time);
        existedTimeTextView = (TextView) view.findViewById(R.id.existed_time);
        stateButton = (ToggleButton) view.findViewById(R.id.status_toggle_button);
        colorPicker = (GridColorPickerView) view.findViewById(R.id.color_picker);
        createdTimeTextView.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(textCare.getCreateTime())));
        Calendar calendar = Calendar.getInstance();
        int dayNow = calendar.get(Calendar.DAY_OF_YEAR);
        int yearCreated = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(textCare.getCreateTime());
        int yearNow = calendar.get(Calendar.YEAR);
        int dayCreated = calendar.get(Calendar.DAY_OF_YEAR);
        int days = (int) (new Date(yearNow, 0, 1).getTime() - new Date(yearCreated, 0, 1).getTime()) / 86400000;
        existedTimeTextView.setText(days + dayNow - dayCreated + "  " + getString(R.string.days));
        colorPicker.setSelectionByColor(textCare.getColor());
        stateButton.setChecked(textCare.isAchieved());
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (stateButton.isChecked() && textCare.getAchievedTime() == 0L) {
            textCare.setAchievedTime(System.currentTimeMillis());
        } else {
            textCare.setAchievedTime(0L);
        }
        textCare.setColor(colorPicker.getSelectedColor());
        DataManager.getInstance(getActivity()).updateCareItem(textCare);
    }
}
