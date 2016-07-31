package com.onecivilization.MyOptimize.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.onecivilization.MyOptimize.R;

/**
 * Created by CGZ on 2016/7/31.
 */
public class TimeLimitationPicker extends LinearLayout {

    private TextView title;
    private NumberPicker startHourPicker;
    private NumberPicker startMinutePicker;
    private NumberPicker endHourPicker;
    private NumberPicker endMinutePicker;

    public TimeLimitationPicker(Context context) {
        this(context, null);
    }

    public TimeLimitationPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLimitationPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_time_limitation_picker, this);
        title = (TextView) findViewById(R.id.title);
        startHourPicker = (NumberPicker) findViewById(R.id.start_hour_picker);
        startMinutePicker = (NumberPicker) findViewById(R.id.start_minute_picker);
        endHourPicker = (NumberPicker) findViewById(R.id.end_hour_picker);
        endMinutePicker = (NumberPicker) findViewById(R.id.end_minute_picker);
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
    }

    public int getStartMinutes() {
        return startHourPicker.getValue() * 60 + startMinutePicker.getValue();
    }

    public int getEndMinutes() {
        return endHourPicker.getValue() * 60 + endMinutePicker.getValue();
    }

    public boolean isValid() {
        return getStartMinutes() < getEndMinutes();
    }

    public void setTitle(int position) {
        title.setText(getResources().getString(R.string.sign_time_limitation_) + " " + (position + 1) + "：");
    }

}
