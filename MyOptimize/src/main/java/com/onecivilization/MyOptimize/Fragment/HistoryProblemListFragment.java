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
import android.widget.TextView;

import com.onecivilization.MyOptimize.Activity.HistoryProblemDetailsActivity;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by CGZ on 2016/8/15.
 */
public class HistoryProblemListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cares, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.care_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter(DataManager.getInstance().getHistoryProblemList());
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

        public TextView title;
        public TextView existedTime;
        public TextView archivedTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.problem_item_title);
            existedTime = (TextView) itemView.findViewById(R.id.problem_item_time);
            archivedTime = (TextView) itemView.findViewById(R.id.problem_item_archived_time);
        }

    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<Problem> problemList;

        public MyAdapter(List<Problem> problemList) {
            this.problemList = problemList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_problem, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Problem problem = problemList.get(position);
            holder.title.setText(problem.getTitle());
            holder.existedTime.setText(String.valueOf(problem.getDays()));
            holder.archivedTime.setVisibility(View.VISIBLE);
            holder.archivedTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(problem.getArchivedTime())));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), HistoryProblemDetailsActivity.class).putExtra("historyProblemItemPosition", position));
                }
            });
            if (problem.isSolved()) {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                return;
            }
            switch (problem.getRank()) {
                case Problem.LOW:
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.state_true));
                    break;
                case Problem.NORMAL:
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.state_false));
                    break;
                case Problem.HIGH:
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.state_warning));
                    break;
                case Problem.EXTRA_HIGH:
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.state_extra_high));
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return problemList.size();
        }
    }
}
