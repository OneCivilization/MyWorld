package com.onecivilization.Optimize.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.onecivilization.Optimize.Interface.NewCareFragment;
import com.onecivilization.Optimize.Model.Care;
import com.onecivilization.Optimize.R;

/**
 * Created by CGZ on 2016/7/19.
 */
public class NewNonperiodicCareFragment extends Fragment implements NewCareFragment {

    private NumberPicker goalPicker;
    private NumberPicker punishmentPicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_nonperiodic_care, container, false);
        goalPicker = (NumberPicker) view.findViewById(R.id.goal_picker);
        punishmentPicker = (NumberPicker) view.findViewById(R.id.punishment_picker);
        goalPicker.setMaxValue(Integer.MAX_VALUE);
        goalPicker.setMinValue(1);
        goalPicker.setWrapSelectorWheel(false);
        punishmentPicker.setMaxValue(Integer.MAX_VALUE);
        punishmentPicker.setMinValue(0);
        punishmentPicker.setValue(1);
        punishmentPicker.setWrapSelectorWheel(false);
        return view;
    }

    @Override
    public Bundle getResult() {
        Bundle result = new Bundle();
        result.putInt("type", Care.NONPERIODIC);
        result.putInt("goal", goalPicker.getValue());
        result.putInt("punishment", punishmentPicker.getValue());
        return result;
    }

}
