package com.onecivilization.MyOptimize.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CGZ on 2016/7/18.
 */
public class HistoryDescriptionFragment extends Fragment {

    private EditText descriptionContent;
    private TextView descriptionLastedEditedTime;
    private Care care;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        care = DataManager.getInstance().getHistoryCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        descriptionContent = (EditText) view.findViewById(R.id.description_content);
        descriptionLastedEditedTime = (TextView) view.findViewById(R.id.description_last_edited_time);
        descriptionContent.setText(care.getDescription());
        descriptionContent.setEnabled(false);
        descriptionLastedEditedTime.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(care.getDescriptionLastEditedTime())) + "  ");
        return view;
    }

}
