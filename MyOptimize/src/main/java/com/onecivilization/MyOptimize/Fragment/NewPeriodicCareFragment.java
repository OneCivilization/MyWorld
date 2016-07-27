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
 * Created by CGZ on 2016/7/26.
 */
public class NewPeriodicCareFragment extends Fragment implements NewCareFragment {

    private NumberPicker goalPicker;
    private NumberPicker punishmentPicker;
    private NumberPicker lengthPicker;
    private NumberPicker unitPicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_periodic_care, container, false);
        goalPicker = (NumberPicker) view.findViewById(R.id.goal_picker);
        punishmentPicker = (NumberPicker) view.findViewById(R.id.punishment_picker);
        lengthPicker = (NumberPicker) view.findViewById(R.id.length_picker);
        unitPicker = (NumberPicker) view.findViewById(R.id.unit_picker);
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
        return view;
    }

    @Override
    public Bundle getResult() {
        Bundle result = new Bundle();
        result.putInt("type", Care.PERIODIC);
        result.putInt("goal", goalPicker.getValue());
        result.putInt("punishment", punishmentPicker.getValue());
        result.putInt("periodUnit", 6 - unitPicker.getValue());
        result.putInt("periodLength", lengthPicker.getValue());
        return result;
    }
}
