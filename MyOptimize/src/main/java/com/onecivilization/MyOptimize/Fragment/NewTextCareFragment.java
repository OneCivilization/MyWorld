package com.onecivilization.MyOptimize.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onecivilization.MyOptimize.CustomView.GridColorPicker;
import com.onecivilization.MyOptimize.Interface.NewCareFragment;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.R;

public class NewTextCareFragment extends Fragment implements NewCareFragment {

    private GridColorPicker gridColorPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_text_care, container, false);
        gridColorPicker = (GridColorPicker) view.findViewById(R.id.color_picker);
        gridColorPicker.setSelection(6);
        return view;
    }

    @Override
    public Bundle getResult() {
        Bundle result = new Bundle();
        result.putInt("type", Care.TEXT);
        result.putInt("color", gridColorPicker.getSelectedColor());
        return result;
    }

}
