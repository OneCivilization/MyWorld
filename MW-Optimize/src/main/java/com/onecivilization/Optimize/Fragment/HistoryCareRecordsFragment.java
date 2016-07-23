package com.onecivilization.Optimize.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onecivilization.Optimize.CustomView.RecordsCalendar;
import com.onecivilization.Optimize.Database.DataManager;
import com.onecivilization.Optimize.Model.NonperiodicCare;
import com.onecivilization.Optimize.R;

/**
 * Created by CGZ on 2016/7/23.
 */
public class HistoryCareRecordsFragment extends Fragment {

    private NonperiodicCare care;
    private RecordsCalendar recordsCalendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        care = (NonperiodicCare) DataManager.getInstance(getActivity()).getHistoryCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care_records, container, false);
        recordsCalendar = (RecordsCalendar) view.findViewById(R.id.records_calendar);
        recordsCalendar.setRecords(care.getRecords(), care.getPunishment());
        return view;
    }
}
