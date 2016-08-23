package com.onecivilization.MyOptimize.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.R;

/**
 * Created by CGZ on 2016/8/16.
 */
public class ProblemContentFragment extends Fragment {

    private Problem problemItem;
    private View titleIndicator;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText analysisEditText;
    private EditText solutionEditText;
    private boolean isHistory = false;

    public ProblemContentFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public ProblemContentFragment(boolean isHistory) {
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
        View view = inflater.inflate(R.layout.fragment_problem_content, container, false);
        titleIndicator = view.findViewById(R.id.title_indicator);
        titleEditText = (EditText) view.findViewById(R.id.title_editText);
        descriptionEditText = (EditText) view.findViewById(R.id.description);
        analysisEditText = (EditText) view.findViewById(R.id.analysis);
        solutionEditText = (EditText) view.findViewById(R.id.solution);

        titleEditText.setText(problemItem.getTitle());
        descriptionEditText.setText(problemItem.getDescription());
        analysisEditText.setText(problemItem.getAnalysis());
        solutionEditText.setText(problemItem.getSolution());
        switch (problemItem.getRank()) {
            case Problem.LOW:
                titleIndicator.setBackgroundColor(getResources().getColor(R.color.state_true));
                break;
            case Problem.NORMAL:
                titleIndicator.setBackgroundColor(getResources().getColor(R.color.state_false));
                break;
            case Problem.HIGH:
                titleIndicator.setBackgroundColor(getResources().getColor(R.color.state_warning));
                break;
            case Problem.EXTRA_HIGH:
                titleIndicator.setBackgroundColor(getResources().getColor(R.color.state_extra_high));
                break;
        }
        if (isHistory) {
            setHistory();
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isHistory) {
            String title = titleEditText.getText().toString();
            problemItem.setTitle(title.equals("") ? problemItem.getTitle() : title);
            problemItem.setDescription(descriptionEditText.getText().toString());
            problemItem.setAnalysis(analysisEditText.getText().toString());
            problemItem.setSolution(solutionEditText.getText().toString());
        }

    }

    public void setHistory() {
        if (isHistory) {
            titleEditText.setEnabled(false);
            descriptionEditText.setEnabled(false);
            analysisEditText.setEnabled(false);
            solutionEditText.setEnabled(false);
        }
    }
}
