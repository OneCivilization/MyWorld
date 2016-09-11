package com.onecivilization.MyOptimize.Fragment;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.onecivilization.MyOptimize.Activity.CareDetailsActivity;
import com.onecivilization.MyOptimize.CustomView.RecordsCalendar;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.NonperiodicCare;
import com.onecivilization.MyOptimize.Model.Record;
import com.onecivilization.MyOptimize.Model.RecordsSortHelper;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by CGZ on 2016/7/23.
 */
public class NonperiodicCareRecordsFragment extends Fragment {

    private NonperiodicCare care;
    private RecordsCalendar recordsCalendar;
    private RecordsSortHelper recordsSortHelper;
    private TextView modifiedText;
    private RecyclerView recordsList;
    private ArrayList<Record> records;
    private int selectedRecordPosition = -1;
    private View selectedRecordItemView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        care = (NonperiodicCare) DataManager.getInstance().getCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recordsSortHelper = new RecordsSortHelper(care.getRecords());
        View view = inflater.inflate(R.layout.fragment_care_records, container, false);
        modifiedText = (TextView) view.findViewById(R.id.modified);
        if (care.getModified() != 0) {
            modifiedText.setText(getString(R.string.modified) + care.getModified());
        }
        recordsCalendar = (RecordsCalendar) view.findViewById(R.id.records_calendar);
        recordsCalendar.setRecords(care.getRecords(), care.getPunishment());
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
                records = recordsSortHelper.getDayRecord(year, month, date);
                Bundle information = RecordsSortHelper.getInformation(records, care.getPunishment());
                int progress = information.getInt("progress");
                int succeeded = information.getInt("succeeded");
                final int failed = information.getInt("failed");
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_records_details, null);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);
                final TextView progressText = (TextView) dialogView.findViewById(R.id.progress);
                final TextView succeededText = (TextView) dialogView.findViewById(R.id.succeeded);
                final TextView failedText = (TextView) dialogView.findViewById(R.id.failed);
                recordsList = (RecyclerView) dialogView.findViewById(R.id.records_list);
                ImageButton add = (ImageButton) dialogView.findViewById(R.id.add);
                ImageButton remove = (ImageButton) dialogView.findViewById(R.id.remove);
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
                recordsList.setItemAnimator(new DefaultItemAnimator());
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
                                final GregorianCalendar calendar = new GregorianCalendar(year, month, date, hourOfDay, minute, 0);
                                final View signInView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sign_in, null);
                                new AlertDialog.Builder(getActivity())
                                        .setTitle(SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM, AppManager.LOCALE).format(calendar.getTime()))
                                        .setView(signInView)
                                        .setNegativeButton(R.string.cancel, null)
                                        .setPositiveButton(R.string.confirm_, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Record record = null;
                                                RadioGroup recordTag = (RadioGroup) signInView.findViewById(R.id.record_tag);
                                                switch (recordTag.getCheckedRadioButtonId()) {
                                                    case R.id.succeeded:
                                                        care.insertRecord(calendar.getTimeInMillis(), true);
                                                        record = new Record(calendar.getTimeInMillis(), true);
                                                        break;
                                                    case R.id.failed:
                                                        care.insertRecord(calendar.getTimeInMillis(), false);
                                                        record = new Record(calendar.getTimeInMillis(), false);
                                                        break;
                                                }
                                                if (records.isEmpty() || record.time < records.get(0).time) {
                                                    records.add(0, record);
                                                    recordsList.getAdapter().notifyItemInserted(0);
                                                } else {
                                                    int i = records.size() - 1;
                                                    for (; i >= 0; i--) {
                                                        if (records.get(i).time < record.time) {
                                                            records.add(i + 1, record);
                                                            recordsList.getAdapter().notifyItemInserted(i + 1);
                                                            break;
                                                        }
                                                    }
                                                }
                                                Bundle information = RecordsSortHelper.getInformation(records, care.getPunishment());
                                                int progress = information.getInt("progress");
                                                int succeeded = information.getInt("succeeded");
                                                int failed = information.getInt("failed");
                                                if (progress > 0) {
                                                    progressText.setTextColor(0xFF31b314);
                                                } else if (progress < 0) {
                                                    progressText.setTextColor(Color.RED);
                                                }
                                                progressText.setText(progress + "    ");
                                                if (succeeded > 0)
                                                    succeededText.setTextColor(0xFF31b314);
                                                succeededText.setText(succeeded + "    ");
                                                if (failed > 0) failedText.setTextColor(Color.RED);
                                                failedText.setText(failed + "   ");
                                                selectedRecordPosition = -1;
                                                if (selectedRecordItemView != null) {
                                                    selectedRecordItemView.setBackgroundColor(Color.TRANSPARENT);
                                                    selectedRecordItemView = null;
                                                }
                                                ((CareDetailsActivity) getActivity()).refreshFragments();
                                            }
                                        }).create().show();
                            }
                        }, 0, 0, true) {
                            @Override
                            protected void onStop() {
                            }
                        }.show();
                    }
                });
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedRecordPosition != -1) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.delete_warning)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            care.deleteRecord(records.get(selectedRecordPosition).time);
                                            records.remove(selectedRecordPosition);
                                            recordsList.getAdapter().notifyItemRemoved(selectedRecordPosition);
                                            selectedRecordPosition = -1;
                                            Bundle information = RecordsSortHelper.getInformation(records, care.getPunishment());
                                            int progress = information.getInt("progress");
                                            int succeeded = information.getInt("succeeded");
                                            int failed = information.getInt("failed");
                                            if (progress > 0) {
                                                progressText.setTextColor(0xFF31b314);
                                            } else if (progress < 0) {
                                                progressText.setTextColor(Color.RED);
                                            }
                                            progressText.setText(progress + "    ");
                                            if (succeeded > 0)
                                                succeededText.setTextColor(0xFF31b314);
                                            succeededText.setText(succeeded + "    ");
                                            if (failed > 0) failedText.setTextColor(Color.RED);
                                            failedText.setText(failed + "   ");
                                            selectedRecordPosition = -1;
                                            if (selectedRecordItemView != null) {
                                                selectedRecordItemView.setBackgroundColor(Color.TRANSPARENT);
                                                selectedRecordItemView = null;
                                            }
                                            ((CareDetailsActivity) getActivity()).refreshFragments();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .create().show();
                        } else {
                            Toast.makeText(getActivity(), R.string.no_record_selected, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                new AlertDialog.Builder(getActivity())
                        .setView(dialogView)
                        .create().show();
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

        public void bindRecord(Record record, final int position) {
            recordTime.setText(SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM, AppManager.LOCALE).format(new Date(record.time)));
            if (record.tag) {
                recordTag.setTextColor(0xff31b314);
                recordTag.setText(getString(R.string.succeeded_));
            } else {
                recordTag.setTextColor(Color.RED);
                recordTag.setText(getString(R.string.failed_));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedRecordItemView != null) {
                        selectedRecordItemView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    v.setBackgroundColor(0x29888888);
                    selectedRecordItemView = v;
                    selectedRecordPosition = getAdapterPosition();
                }
            });
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
            holder.bindRecord(records.get(position), position);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }

}
