package com.onecivilization.MyOptimize.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.onecivilization.MyOptimize.CustomView.TimeLimitationPicker;
import com.onecivilization.MyOptimize.Interface.NewCareFragment;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.PeriodicCare;
import com.onecivilization.MyOptimize.R;

import java.util.ArrayList;

/**
 * Created by CGZ on 2016/7/31.
 */
public class NewComplexPeriodicCareFragment extends Fragment implements NewCareFragment {

    private LinearLayout container;
    private NumberPicker goalPicker;
    private NumberPicker subGoalPicker;
    private NumberPicker punishmentPicker;
    private ArrayList<TimeLimitationPicker> timeLimitationPickers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_complex_periodic_care, container, false);
        this.container = (LinearLayout) view;
        goalPicker = (NumberPicker) view.findViewById(R.id.goal_picker);
        subGoalPicker = (NumberPicker) view.findViewById(R.id.sub_goal_picker);
        punishmentPicker = (NumberPicker) view.findViewById(R.id.punishment_picker);
        goalPicker.setMaxValue(Integer.MAX_VALUE);
        goalPicker.setMinValue(1);
        goalPicker.setWrapSelectorWheel(false);
        subGoalPicker.setMaxValue(Integer.MAX_VALUE);
        subGoalPicker.setMinValue(2);
        subGoalPicker.setWrapSelectorWheel(false);
        subGoalPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal - oldVal > 0) {
                    addTimeLimitationPicker();
                } else {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            removeTimeLimitationPicker();
                        }
                    });
                }
            }
        });
        punishmentPicker.setMaxValue(Integer.MAX_VALUE);
        punishmentPicker.setMinValue(0);
        punishmentPicker.setValue(1);
        punishmentPicker.setWrapSelectorWheel(false);
        timeLimitationPickers = new ArrayList<>();
        addTimeLimitationPicker();
        addTimeLimitationPicker();
        return view;
    }

    private void addTimeLimitationPicker() {
        TimeLimitationPicker timeLimitationPicker = new TimeLimitationPicker(getContext());
        timeLimitationPicker.setTitle(timeLimitationPickers.size());
        container.addView(timeLimitationPicker);
        timeLimitationPickers.add(timeLimitationPicker);
    }

    private void removeTimeLimitationPicker() {
        container.removeView(timeLimitationPickers.get(timeLimitationPickers.size() - 1));
        timeLimitationPickers.remove(timeLimitationPickers.size() - 1);
    }

    @Override
    public Bundle getResult() {
        Bundle result = new Bundle();
        result.putInt("type", Care.COMPLEX_PERIODIC);
        boolean isValid = true;
        for (int i = 0; i < timeLimitationPickers.size(); i++) {
            if (!timeLimitationPickers.get(i).isValid()) {
                result.putBoolean("isValid", false);
                result.putInt("invalidPosition", i + 1);
                return result;
            }
        }
        for (int i = 1; i <= timeLimitationPickers.size(); i++) {
            result.putInt("startMinutes" + i, timeLimitationPickers.get(i - 1).getStartMinutes());
            result.putInt("endMinutes" + i, timeLimitationPickers.get(i - 1).getEndMinutes());
        }
        result.putBoolean("isValid", true);
        result.putInt("goal", goalPicker.getValue());
        result.putInt("punishment", punishmentPicker.getValue());
        result.putInt("periodUnit", PeriodicCare.DAY);
        result.putInt("periodLength", 1);
        result.putInt("subGoal", subGoalPicker.getValue());
        return result;
    }
}
