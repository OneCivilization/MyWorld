package com.onecivilization.Optimize.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.onecivilization.Optimize.Database.DataManager;
import com.onecivilization.Optimize.Model.Care;
import com.onecivilization.Optimize.R;
import com.onecivilization.Optimize.Util.AppManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CGZ on 2016/7/16.
 */
public class DescriptionFragment extends Fragment {

    private EditText descriptionContent;
    private TextView descriptionLastedEditedTime;
    private Care care;
    private String descriptionTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        care = DataManager.getInstance(getActivity()).getCareList().get(getActivity().getIntent().getIntExtra("careItemPosition", -1));
        descriptionTitle = care.getDescriptionTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        descriptionContent = (EditText) view.findViewById(R.id.description_content);
        descriptionLastedEditedTime = (TextView) view.findViewById(R.id.description_last_edited_time);
        descriptionContent.setText(care.getDescription());
        descriptionLastedEditedTime.setText(SimpleDateFormat.getDateInstance(DateFormat.LONG, AppManager.LOCALE).format(new Date(care.getDescriptionLastEditedTime())) + "  ");
        descriptionLastedEditedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText title = (EditText) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text, null);
                title.setText(care.getDescriptionTitle());
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.edit_description_title)
                        .setView(title)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                descriptionTitle = title.getText().toString();
                                care.setDescriptionTitle(descriptionTitle);
                                getActivity().recreate();
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        String description = descriptionContent.getText().toString();
        if (!description.equals(care.getDescription()) || !descriptionTitle.equals(care.getDescriptionTitle())) {
            care.setDescription(description);
            care.setDescriptionLastEditedTime(System.currentTimeMillis());
            DataManager.getInstance(getActivity()).updateCareItem(care);
        }
    }
}
