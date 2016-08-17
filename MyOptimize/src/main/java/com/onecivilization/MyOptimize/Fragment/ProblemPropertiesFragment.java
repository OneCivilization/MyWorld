package com.onecivilization.MyOptimize.Fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by CGZ on 2016/8/16.
 */
public class ProblemPropertiesFragment extends Fragment {

    private Problem problemItem;
    private TextView existedTime;
    private Spinner rankChooser;
    private ToggleButton statusSwitcher;
    private TextView createdTime;
    private TableRow solvedTimeContainer;
    private TextView solvedTime;
    private TableRow archivedTimeContainer;
    private TextView archivedTime;
    private boolean isHistory = false;

    public ProblemPropertiesFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public ProblemPropertiesFragment(boolean isHistory) {
        super();
        this.isHistory = isHistory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isHistory) {
            problemItem = DataManager.getInstance().getHistoryProblemList().get(getActivity().getIntent().getIntExtra("historyProblemItemPosition", -1));
        } else {
            problemItem = DataManager.getInstance().getProblemList().get(getActivity().getIntent().getIntExtra("problemItemPosition", -1));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_problem_properties, container, false);
        existedTime = (TextView) view.findViewById(R.id.existed_time);
        rankChooser = (Spinner) view.findViewById(R.id.rank_chooser);
        statusSwitcher = (ToggleButton) view.findViewById(R.id.status_toggle_button);
        createdTime = (TextView) view.findViewById(R.id.created_time);
        solvedTimeContainer = (TableRow) view.findViewById(R.id.solved_time_row);
        solvedTime = (TextView) view.findViewById(R.id.solved_time);
        archivedTimeContainer = (TableRow) view.findViewById(R.id.archived_time_row);
        archivedTime = (TextView) view.findViewById(R.id.archived_time);

        if (AppManager.LOCALE.equals(Locale.CHINESE)) {
            existedTime.setText("第 " + problemItem.getExistedDays() + " 天");
        } else {
            existedTime.setText("Day " + problemItem.getExistedDays());
        }
        rankChooser.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_type, getResources().getStringArray(R.array.problem_item_ranks)));
        rankChooser.setSelection(problemItem.getRank() - 1);
        rankChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                problemItem.setRank(position + 1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                switch (position) {
                    case 0:
                        view.setBackgroundColor(getResources().getColor(R.color.state_true));
                        break;
                    case 1:
                        view.setBackgroundColor(getResources().getColor(R.color.state_false));
                        break;
                    case 2:
                        view.setBackgroundColor(getResources().getColor(R.color.state_warning));
                        break;
                    case 3:
                        view.setBackgroundColor(getResources().getColor(R.color.state_extra_high));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        statusSwitcher.setChecked(problemItem.isSolved());
        createdTime.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(problemItem.getCreateTime())));
        if (problemItem.isSolved()) {
            solvedTimeContainer.setVisibility(View.VISIBLE);
            solvedTime.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(problemItem.getSolvedTime())));
        }
        if (isHistory) {
            rankChooser.setEnabled(false);
            statusSwitcher.setEnabled(false);
            archivedTimeContainer.setVisibility(View.VISIBLE);
            archivedTime.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(problemItem.getArchivedTime())));
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isHistory) {
            if (!problemItem.isSolved()) {
                if (statusSwitcher.isChecked()) {
                    problemItem.setSolvedTime(System.currentTimeMillis());
                }
            } else if (!statusSwitcher.isChecked()) {
                problemItem.setSolvedTime(0L);
            }
        }

    }
}
