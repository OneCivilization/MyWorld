package com.onecivilization.MyOptimize.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onecivilization.MyOptimize.Activity.HistoryCareDetailsActivity;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.NonperiodicCare;
import com.onecivilization.MyOptimize.Model.PeriodicCare;
import com.onecivilization.MyOptimize.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by CGZ on 2016/7/17.
 */
public class HistoryCareListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cares, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.care_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter(DataManager.getInstance().getHistoryCareList());
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
            goal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(care.getArchivedTime())));
            switch (care.getType()) {
                case Care.TEXT:
                    timeLimitation.setVisibility(View.GONE);
                    if (care.isAchieved()) {
                        progress.setText("100%");
                        container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                        statusImageButton.setImageResource(R.drawable.state_true);
                    } else {
                        progress.setText("0%");
                        container.setBackgroundColor(getResources().getColor(R.color.state_warning));
                        statusImageButton.setImageResource(R.drawable.state_false);
                    }
                    break;
                case Care.NONPERIODIC:
                    timeLimitation.setVisibility(View.GONE);
                    NonperiodicCare careItem = (NonperiodicCare) care;
                    int percentage = (int) careItem.getPercentage();
                    progress.setText(percentage + "%");
                    if (percentage <= 0) {
                        container.setBackgroundColor(getResources().getColor(R.color.state_warning));
                        statusImageButton.setImageResource(R.drawable.state_false);
                    } else if (percentage < 60) {
                        container.setBackgroundColor(getResources().getColor(R.color.state_false));
                        statusImageButton.setImageResource(R.drawable.state_unachieved);
                    } else if (percentage < 100) {
                        container.setBackgroundColor(getResources().getColor(R.color.state_true));
                        statusImageButton.setImageResource(R.drawable.state_unachieved);
                    } else {
                        container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                        statusImageButton.setImageResource(R.drawable.state_true);
                    }
                    break;
                case Care.PERIODIC:
                    PeriodicCare careItem1 = (PeriodicCare) care;
                    int percentage1 = (int) careItem1.getPercentage();
                    progress.setText(percentage1 + "%");
                    if (percentage1 <= 0) {
                        container.setBackgroundColor(getResources().getColor(R.color.state_warning));
                        statusImageButton.setImageResource(R.drawable.state_false);
                    } else if (percentage1 < 60) {
                        container.setBackgroundColor(getResources().getColor(R.color.state_false));
                        statusImageButton.setImageResource(R.drawable.state_unachieved);
                    } else if (percentage1 < 100) {
                        container.setBackgroundColor(getResources().getColor(R.color.state_true));
                        statusImageButton.setImageResource(R.drawable.state_unachieved);
                    } else {
                        container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                        statusImageButton.setImageResource(R.drawable.state_true);
                    }
                    timeLimitation.setVisibility(View.VISIBLE);
                    timeLimitation.setText(careItem1.getPeriodText());
                    break;
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), HistoryCareDetailsActivity.class).putExtra("careItemPosition", position));
                }
            });
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
