package com.onecivilization.MyOptimize.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.onecivilization.MyOptimize.Interface.NewCareFragment;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.R;

/**
 * Created by CGZ on 2016/7/29.
 */
public class NewTimeLimitedPeriodicCareFragment extends Fragment implements NewCareFragment {

    private NumberPicker goalPicker;
    private NumberPicker punishmentPicker;
    private NumberPicker lengthPicker;
    private NumberPicker unitPicker;
    private NumberPicker startHourPicker;
    private NumberPicker startMinutePicker;
    private NumberPicker endHourPicker;
    private NumberPicker endMinutePicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_time_limited_periodic_care, container, false);
        goalPicker = (NumberPicker) view.findViewById(R.id.goal_picker);
        punishmentPicker = (NumberPicker) view.findViewById(R.id.punishment_picker);
        lengthPicker = (NumberPicker) view.findViewById(R.id.length_picker);
        unitPicker = (NumberPicker) view.findViewById(R.id.unit_picker);
        startHourPicker = (NumberPicker) view.findViewById(R.id.start_hour_picker);
        startMinutePicker = (NumberPicker) view.findViewById(R.id.start_minute_picker);
        endHourPicker = (NumberPicker) view.findViewById(R.id.end_hour_picker);
        endMinutePicker = (NumberPicker) view.findViewById(R.id.end_minute_picker);
        goalPicker.setMaxValue(Integer.MAX_VALUE);
        goalPicker.setMinValue(1);
        goalPicker.setWrapSelectorWheel(false);
        punishmentPicker.setMaxValue(Integer.MAX_VALUE);
        punishmentPicker.setMinValue(0);
        punishmentPicker.setValue(1);
        punishmentPicker.setWrapSelectorWheel(false);
        lengthPicker.setMaxValue(Integer.MAX_VALUE);
        lengthPicker.setMinValue(1);
        lengthPicker.setWrapSelectorWheel(false);
        unitPicker.setDisplayedValues(getResources().getStringArray(R.array.period_units));
        unitPicker.setMaxValue(3);
        unitPicker.setMinValue(0);
        unitPicker.setValue(3);
        unitPicker.setWrapSelectorWheel(true);
        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        };
        startHourPicker.setMaxValue(23);
        startHourPicker.setMinValue(0);
        startHourPicker.setValue(7);
        startHourPicker.setFormatter(formatter);
        startHourPicker.setWrapSelectorWheel(true);
        startMinutePicker.setMaxValue(59);
        startMinutePicker.setMinValue(0);
        startMinutePicker.setValue(0);
        startMinutePicker.setFormatter(formatter);
        startMinutePicker.setWrapSelectorWheel(true);
        endHourPicker.setMaxValue(23);
        endHourPicker.setMinValue(0);
        endHourPicker.setValue(7);
        endHourPicker.setFormatter(formatter);
        endHourPicker.setWrapSelectorWheel(true);
        endMinutePicker.setMaxValue(59);
        endMinutePicker.setMinValue(0);
        endMinutePicker.setValue(0);
        endMinutePicker.setFormatter(formatter);
        endMinutePicker.setWrapSelectorWheel(true);
        return view;
    }

    @Override
    public Bundle getResult() {
        Bundle result = new Bundle();
        result.putInt("type", Care.TIMELIMITED_PERIODIC);
        result.putInt("goal", goalPicker.getValue());
        result.putInt("punishment", punishmentPicker.getValue());
        result.putInt("periodUnit", 6 - unitPicker.getValue());
        result.putInt("periodLength", lengthPicker.getValue());
        result.putInt("startHour", startHourPicker.getValue());
        result.putInt("startMinute", startMinutePicker.getValue());
        result.putInt("endHour", endHourPicker.getValue());
        result.putInt("endMinute", endMinutePicker.getValue());
        boolean isValid = (startHourPicker.getValue() * 60 + startMinutePicker.getValue()) < (endHourPicker.getValue() * 60 + endMinutePicker.getValue());
        result.putBoolean("isValid", isValid);
        return result;
    }
}
