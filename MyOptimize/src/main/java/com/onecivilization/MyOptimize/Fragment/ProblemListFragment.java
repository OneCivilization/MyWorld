package com.onecivilization.MyOptimize.Fragment;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onecivilization.MyOptimize.Activity.ProblemDetailsActivity;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by CGZ on 2016/8/15.
 */
public class ProblemListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cares, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.care_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter(DataManager.getInstance().getProblemList());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(15);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                DataManager.getInstance().swapProblemItem(fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return !PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).getBoolean("problemItemAutoSort", true);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().sortProblemListByRank();
        adapter.notifyDataSetChanged();
    }

    public void notifyItemRankChanged(int problemItemPosition, Problem problemItem) {
        if (PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).getBoolean("problemItemAutoSort", true)) {
            ListIterator<Problem> iterator = adapter.problemList.listIterator();
            while (iterator.hasNext()) {
                if (iterator.next().getRank() <= problemItem.getRank() && iterator.previousIndex() != problemItemPosition) {
                    int toPosition = iterator.previousIndex();
                    if (toPosition > problemItemPosition) {
                        adapter.problemList.add(toPosition, problemItem);
                        adapter.problemList.remove(problemItemPosition);
                        adapter.notifyItemMoved(problemItemPosition, toPosition - 1);
                    } else {
                        adapter.problemList.add(toPosition, problemItem);
                        adapter.problemList.remove(problemItemPosition + 1);
                        adapter.notifyItemMoved(problemItemPosition, toPosition);
                    }
                    return;
                }
            }
            adapter.problemList.remove(problemItemPosition);
            adapter.problemList.add(problemItem);
            adapter.notifyItemMoved(problemItemPosition, adapter.problemList.size() - 1);
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView existedTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.problem_item_title);
            existedTime = (TextView) itemView.findViewById(R.id.problem_item_time);
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ProblemDetailsActivity.class).putExtra("problemItemPosition", position));
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
