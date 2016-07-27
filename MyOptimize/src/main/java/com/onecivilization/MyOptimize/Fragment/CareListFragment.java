package com.onecivilization.MyOptimize.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onecivilization.MyOptimize.Activity.CareDetailsActivity;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.NonperiodicCare;
import com.onecivilization.MyOptimize.Model.PeriodicCare;
import com.onecivilization.MyOptimize.Model.TextCare;
import com.onecivilization.MyOptimize.R;

import java.util.List;

/**
 * Created by CGZ on 2016/7/10.
 */
public class CareListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cares, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.care_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter(DataManager.getInstance().getCareList());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageButton statusImageButton;
        public TextView statusTextView;
        public RelativeLayout container;
        public TextView title;
        public TextView timeLimitation;
        public TextView progress;
        public TextView goal;

        public MyViewHolder(View itemView) {
            super(itemView);
            statusImageButton = (ImageButton) itemView.findViewById(R.id.care_item_status_button);
            statusTextView = (TextView) itemView.findViewById(R.id.care_item_status_textview);
            container = (RelativeLayout) itemView.findViewById(R.id.care_item_container);
            title = (TextView) itemView.findViewById(R.id.care_item_title);
            timeLimitation = (TextView) itemView.findViewById(R.id.care_item_time);
            progress = (TextView) itemView.findViewById(R.id.care_item_title_progress);
            goal = (TextView) itemView.findViewById(R.id.care_item_goal);
        }

        public void bindCare(final Care care, final int position) {
            title.setText(care.getTitle());
            switch (care.getType()) {
                case Care.TEXT:
                    progress.setVisibility(View.GONE);
                    goal.setVisibility(View.GONE);
                    timeLimitation.setVisibility(View.GONE);
                    if (care.isAchieved()) {
                        container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                    } else {
                        container.setBackgroundColor(((TextCare) care).getColor());
                    }
                    if (care.isAchieved()) {
                        statusImageButton.setImageResource(R.drawable.state_true);
                    } else {
                        statusImageButton.setImageResource(0);
                    }
                    statusImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (care.isAchieved()) {
                                statusImageButton.setImageResource(0);
                                care.setAchievedTime(0L);
                                container.setBackgroundColor(((TextCare) care).getColor());
                            } else {
                                statusImageButton.setImageResource(R.drawable.state_true);
                                care.setAchievedTime(System.currentTimeMillis());
                                container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                            }
                            DataManager.getInstance().updateCareItem(care);
                        }
                    });
                    break;
                case Care.NONPERIODIC:
                    progress.setVisibility(View.VISIBLE);
                    goal.setVisibility(View.VISIBLE);
                    timeLimitation.setVisibility(View.GONE);
                    final NonperiodicCare careItem = (NonperiodicCare) care;
                    goal.setText(getString(R.string.goal) + careItem.getGoal());
                    refreshCareState(care);
                    statusImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final View signInView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sign_in, null);
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.sign_in)
                                    .setView(signInView)
                                    .setNegativeButton(R.string.cancel, null)
                                    .setPositiveButton(R.string.confirm_, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            RadioGroup recordTag = (RadioGroup) signInView.findViewById(R.id.record_tag);
                                            switch (recordTag.getCheckedRadioButtonId()) {
                                                case R.id.succeeded:
                                                    careItem.addRecord(true);
                                                    break;
                                                case R.id.failed:
                                                    careItem.addRecord(false);
                                                    break;
                                            }
                                            refreshCareState(careItem);
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }
                    });
                    break;
                case Care.PERIODIC:
                    progress.setVisibility(View.VISIBLE);
                    goal.setVisibility(View.VISIBLE);
                    timeLimitation.setVisibility(View.VISIBLE);
                    final PeriodicCare careItem1 = (PeriodicCare) care;
                    goal.setText(getString(R.string.goal) + careItem1.getGoal());
                    timeLimitation.setText(careItem1.getPeriodText());
                    refreshCareState(care);
                    statusImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (careItem1.isSigned()) {
                                careItem1.deleteRecord();
                            } else {
                                careItem1.addRecord();
                            }
                            refreshCareState(care);
                        }
                    });
                    break;
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), CareDetailsActivity.class).putExtra("careItemPosition", position));
                }
            });
        }

        private void refreshCareState(Care care) {
            switch (care.getType()) {
                case Care.NONPERIODIC:
                    NonperiodicCare careItem = (NonperiodicCare) care;
                    progress.setText(careItem.getProgressText());
                    switch (careItem.getState()) {
                        case Care.STATE_NONE:
                            statusImageButton.setImageResource(R.drawable.state_nonperiodic);
                            container.setBackgroundColor(getResources().getColor(R.color.state_false));
                            break;
                        case Care.STATE_UNDONE:
                            statusImageButton.setImageResource(R.drawable.state_false);
                            container.setBackgroundColor(getResources().getColor(R.color.state_false));
                            break;
                        case Care.STATE_DONE:
                            statusImageButton.setImageResource(R.drawable.state_true);
                            container.setBackgroundColor(getResources().getColor(R.color.state_true));
                            break;
                        case Care.STATE_MINUS:
                            statusImageButton.setImageResource(R.drawable.state_false);
                            container.setBackgroundColor(getResources().getColor(R.color.state_warning));
                            break;
                        case Care.STATE_ACHIEVED:
                            statusImageButton.setImageResource(R.drawable.state_true);
                            container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                            break;
                    }
                    break;
                case Care.PERIODIC:
                    PeriodicCare careItem1 = (PeriodicCare) care;
                    progress.setText(careItem1.getProgressText());
                    switch (careItem1.getState()) {
                        case Care.STATE_UNDONE:
                            statusImageButton.setImageResource(R.drawable.state_unachieved);
                            container.setBackgroundColor(getResources().getColor(R.color.state_false));
                            break;
                        case Care.STATE_DONE:
                            statusImageButton.setImageResource(R.drawable.state_true);
                            container.setBackgroundColor(getResources().getColor(R.color.state_true));
                            break;
                        case Care.STATE_MINUS:
                            statusImageButton.setImageResource(R.drawable.state_false);
                            container.setBackgroundColor(getResources().getColor(R.color.state_warning));
                            break;
                        case Care.STATE_ACHIEVED:
                            statusImageButton.setImageResource(R.drawable.state_true);
                            container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                            break;
                        case Care.STATE_ACHIEVED_UNDONE:
                            statusImageButton.setImageResource(R.drawable.state_false);
                            container.setBackgroundColor(getResources().getColor(R.color.state_true));
                            break;
                    }
                    break;
            }
        }

    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<Care> careList;

        public MyAdapter(List<Care> careList) {
            this.careList = careList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_care, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.bindCare(careList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return careList.size();
        }
    }

}
