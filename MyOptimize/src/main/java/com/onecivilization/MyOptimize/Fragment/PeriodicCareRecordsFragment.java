package com.onecivilization.MyOptimize.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onecivilization.MyOptimize.CustomView.RecordsCalendar;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.PeriodicCare;
import com.onecivilization.MyOptimize.Model.Record;
import com.onecivilization.MyOptimize.Model.RecordsSortHelper;
import com.onecivilization.MyOptimize.Model.SubPeriodicCare;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by CGZ on 2016/7/27.
 */
public class PeriodicCareRecordsFragment extends Fragment {

    private PeriodicCare care;
    private RecordsCalendar recordsCalendar;
    private RecordsSortHelper recordsSortHelper;
    private RecyclerView recordsList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        care = (PeriodicCare) DataManager.getInstance().getCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care_records, container, false);
        recordsCalendar = (RecordsCalendar) view.findViewById(R.id.records_calendar);
        if (care.getType() == Care.SUB_PERIODIC) {
            recordsCalendar.setRecords(((SubPeriodicCare) care).getSubRecords(), 0);
            recordsSortHelper = new RecordsSortHelper(((SubPeriodicCare) care).getSubRecords());
        } else {
            recordsCalendar.setRecords(care.getRecords(), care.getPunishment());
            recordsSortHelper = new RecordsSortHelper(care.getRecords());
        }
        recordsCalendar.setCreateTime(care.getCreateTime());
        recordsCalendar.setOnDateClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Button) v).getText().toString().equals("")) {
                    return;
                }
                final int date = Integer.parseInt(((Button) v).getText().toString());
                final int year = recordsCalendar.getCurrentYear(), month = recordsCalendar.getCurrentMonth();
                GregorianCalendar calendar = new GregorianCalendar(year, month, date + 1);
                if (calendar.getTimeInMillis() <= care.getCreateTime()) {
                    Toast.makeText(getActivity(), R.string.care_has_not_been_created, Toast.LENGTH_SHORT).show();
                    return;
                } else if (calendar.getTimeInMillis() - 86400000 > System.currentTimeMillis()) {
                    Toast.makeText(getActivity(), R.string.day_has_not_come_yet, Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Record> records = recordsSortHelper.getDayRecord(year, month, date);
                Bundle information = RecordsSortHelper.getInformation(records, care.getPunishment());
                int progress = information.getInt("progress");
                int succeeded = information.getInt("succeeded");
                int failed = information.getInt("failed");
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_history_records_details, null);
                TextView title = (TextView) dialogView.findViewById(R.id.title);
                TextView progressText = (TextView) dialogView.findViewById(R.id.progress);
                TextView succeededText = (TextView) dialogView.findViewById(R.id.succeeded);
                TextView failedText = (TextView) dialogView.findViewById(R.id.failed);
                recordsList = (RecyclerView) dialogView.findViewById(R.id.records_list);
                title.setText("   " + SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new GregorianCalendar(year, month, date).getTime()));
                if (progress > 0) {
                    progressText.setTextColor(0xFF31b314);
                } else if (progress < 0) {
                    progressText.setTextColor(Color.RED);
                }
                progressText.setText(progress + "    ");
                if (succeeded > 0) succeededText.setTextColor(0xFF31b314);
                succeededText.setText(succeeded + "    ");
                if (failed > 0) failedText.setTextColor(Color.RED);
                failedText.setText(failed + "   ");
                recordsList.setLayoutManager(new LinearLayoutManager(getActivity()));
                recordsList.setAdapter(new MyAdapter(records));
                new AlertDialog.Builder(getActivity())
                        .setView(dialogView).create().show();
            }
        });
        return view;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView recordTime;
        private TextView recordTag;

        public MyViewHolder(View itemView) {
            super(itemView);
            recordTime = (TextView) itemView.findViewById(R.id.record_time);
            recordTag = (TextView) itemView.findViewById(R.id.record_tag);
        }

        public void bindRecord(Record record) {
            recordTime.setText(SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM, AppManager.LOCALE).format(new Date(record.time)));
            if (record.tag) {
                recordTag.setTextColor(0xff31b314);
                recordTag.setText(getString(R.string.succeeded_));
            } else {
                recordTag.setTextColor(Color.RED);
                recordTag.setText(getString(R.string.failed_));
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private ArrayList<Record> records;

        public MyAdapter(ArrayList<Record> records) {
            this.records = records;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_record, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.bindRecord(records.get(position));
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }
}
