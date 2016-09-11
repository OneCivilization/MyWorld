package com.onecivilization.MyOptimize.Fragment;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onecivilization.MyOptimize.Activity.CareDetailsActivity;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.ComplexPeriodicCare;
import com.onecivilization.MyOptimize.Model.NonperiodicCare;
import com.onecivilization.MyOptimize.Model.PeriodicCare;
import com.onecivilization.MyOptimize.Model.SubPeriodicCare;
import com.onecivilization.MyOptimize.Model.TextCare;
import com.onecivilization.MyOptimize.Model.TimeLimitedPeriodicCare;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by CGZ on 2016/7/10.
 */
public class CareListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cares, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.care_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter(DataManager.getInstance().getCareList());
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
                DataManager.getInstance().swapCareItem(fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return !PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).getBoolean("careItemAutoSort", true);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void notifyItemStateChanged(int careItemPosition, Care careItem) {
        if (PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).getBoolean("careItemAutoSort", true)) {
            ListIterator<Care> iterator = adapter.careList.listIterator();
            while (iterator.hasNext()) {
                if (iterator.next().getState() >= careItem.getState() && iterator.previousIndex() != careItemPosition) {
                    int toPosition = iterator.previousIndex();
                    Care care = iterator.previous();
                    while (care.getState() == careItem.getState() && care.getOrder() < careItem.getOrder()) {
                        toPosition = iterator.nextIndex();
                        care = iterator.next();
                    }
                    if (toPosition > careItemPosition) {
                        adapter.careList.add(toPosition, careItem);
                        adapter.careList.remove(careItemPosition);
                        adapter.notifyItemMoved(careItemPosition, toPosition - 1);
                    } else {
                        adapter.careList.add(toPosition, careItem);
                        adapter.careList.remove(careItemPosition + 1);
                        adapter.notifyItemMoved(careItemPosition, toPosition);
                    }
                    return;
                }
            }
            adapter.careList.remove(careItemPosition);
            adapter.careList.add(careItem);
            adapter.notifyItemMoved(careItemPosition, adapter.careList.size() - 1);
        }
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
                    statusTextView.setVisibility(View.GONE);
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
                            notifyItemStateChanged(getAdapterPosition(), care);
                        }
                    });
                    statusImageButton.setOnLongClickListener(null);
                    break;
                case Care.NONPERIODIC:
                    statusTextView.setVisibility(View.GONE);
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
                                            notifyItemStateChanged(getAdapterPosition(), care);
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }
                    });
                    statusImageButton.setOnLongClickListener(null);
                    break;
                case Care.PERIODIC:
                    statusTextView.setVisibility(View.GONE);
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
                            notifyItemStateChanged(getAdapterPosition(), care);
                        }
                    });
                    statusImageButton.setOnLongClickListener(null);
                    break;
                case Care.SUB_PERIODIC:
                    statusTextView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.VISIBLE);
                    goal.setVisibility(View.VISIBLE);
                    timeLimitation.setVisibility(View.VISIBLE);
                    statusTextView.setTextColor(0xffffffff);
                    final SubPeriodicCare careItem2 = (SubPeriodicCare) care;
                    goal.setText(getString(R.string.goal) + careItem2.getGoal());
                    timeLimitation.setText(careItem2.getPeriodText());
                    refreshCareState(care);
                    statusImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            careItem2.addSubRecord();
                            Toast.makeText(getActivity(), R.string.signed_in, Toast.LENGTH_SHORT).show();
                            refreshCareState(care);
                            notifyItemStateChanged(getAdapterPosition(), care);
                        }
                    });
                    statusImageButton.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (!careItem2.deleteSubRecord()) {
                                Toast.makeText(getActivity(), R.string.sub_progress_zero, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.signed_out, Toast.LENGTH_SHORT).show();
                                refreshCareState(care);
                                notifyItemStateChanged(getAdapterPosition(), care);
                            }
                            return true;
                        }
                    });
                    break;
                case Care.TIMELIMITED_PERIODIC:
                    statusTextView.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                    goal.setVisibility(View.VISIBLE);
                    timeLimitation.setVisibility(View.VISIBLE);
                    final TimeLimitedPeriodicCare careItem3 = (TimeLimitedPeriodicCare) care;
                    goal.setText(getString(R.string.goal) + careItem3.getGoal());
                    timeLimitation.setText(careItem3.getPeriodText());
                    refreshCareState(care);
                    statusImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (careItem3.isLocked()) {
                                Toast.makeText(getActivity(), R.string.current_time_out_of_limitation, Toast.LENGTH_SHORT).show();
                            } else {
                                if (careItem3.isSigned()) {
                                    careItem3.deleteRecord();
                                } else {
                                    careItem3.addRecord();
                                }
                                refreshCareState(care);
                                notifyItemStateChanged(getAdapterPosition(), care);
                            }
                        }
                    });
                    statusImageButton.setOnLongClickListener(null);
                    break;
                case Care.COMPLEX_PERIODIC:
                    statusTextView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.VISIBLE);
                    goal.setVisibility(View.VISIBLE);
                    timeLimitation.setVisibility(View.VISIBLE);
                    final ComplexPeriodicCare careItem4 = (ComplexPeriodicCare) care;
                    goal.setText(getString(R.string.goal) + careItem4.getGoal());
                    timeLimitation.setText(careItem4.getPeriodText());
                    refreshCareState(care);
                    statusImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (careItem4.isLocked()) {
                                Toast.makeText(getActivity(), R.string.current_time_out_of_limitation, Toast.LENGTH_SHORT).show();
                            } else {
                                if (careItem4.isSubSigned()) {
                                    careItem4.deleteSubRecord();
                                } else {
                                    careItem4.addSubRecord();
                                }
                                refreshCareState(care);
                                notifyItemStateChanged(getAdapterPosition(), care);
                            }
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
                            statusImageButton.setImageResource(R.drawable.state_unachieved);
                            container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                            break;
                    }
                    break;
                case Care.SUB_PERIODIC:
                    SubPeriodicCare careItem2 = (SubPeriodicCare) care;
                    statusTextView.setText(careItem2.getSubProgressText());
                    progress.setText(careItem2.getProgressText());
                    statusImageButton.setImageResource(0);
                    switch (careItem2.getState()) {
                        case Care.STATE_UNDONE:
                            container.setBackgroundColor(getResources().getColor(R.color.state_false));
                            break;
                        case Care.STATE_MINUS:
                            container.setBackgroundColor(getResources().getColor(R.color.state_warning));
                            break;
                        case Care.STATE_ACHIEVED:
                        case Care.STATE_ACHIEVED_UNDONE:
                            container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                            break;
                        case Care.STATE_DONE:
                            container.setBackgroundColor(getResources().getColor(R.color.state_true));
                            break;
                    }
                    break;
                case Care.TIMELIMITED_PERIODIC:
                    TimeLimitedPeriodicCare careItem3 = (TimeLimitedPeriodicCare) care;
                    progress.setText(careItem3.getProgressText());
                    switch (careItem3.getState()) {
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
                            statusImageButton.setImageResource(R.drawable.state_unachieved);
                            container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                            break;
                    }
                    if (careItem3.isLocked()) {
                        statusImageButton.setImageResource(R.drawable.state_locked);
                    }
                    break;
                case Care.COMPLEX_PERIODIC:
                    ComplexPeriodicCare careItem4 = (ComplexPeriodicCare) care;
                    statusTextView.setText(careItem4.getSubProgressText());
                    progress.setText(careItem4.getProgressText());
                    statusImageButton.setImageResource(0);
                    switch (careItem4.getState()) {
                        case Care.STATE_UNDONE:
                            container.setBackgroundColor(getResources().getColor(R.color.state_false));
                            break;
                        case Care.STATE_MINUS:
                            container.setBackgroundColor(getResources().getColor(R.color.state_warning));
                            break;
                        case Care.STATE_ACHIEVED:
                        case Care.STATE_ACHIEVED_UNDONE:
                            container.setBackgroundColor(getResources().getColor(R.color.state_achieved));
                            break;
                        case Care.STATE_DONE:
                            container.setBackgroundColor(getResources().getColor(R.color.state_true));
                            break;
                    }
                    if (careItem4.isLocked()) {
                        statusTextView.setTextColor(Color.GRAY);
                    } else {
                        statusTextView.setTextColor(0xffffffff);
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
