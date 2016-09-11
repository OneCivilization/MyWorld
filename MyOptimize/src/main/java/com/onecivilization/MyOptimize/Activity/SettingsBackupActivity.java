package com.onecivilization.MyOptimize.Activity;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.onecivilization.MyOptimize.CustomView.TipsDialog;
import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by CGZ on 2016/8/14.
 */
public class SettingsBackupActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_backup);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.backup_list);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new MyAdapter(DataManager.getInstance().getBackupList());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                Paint paint = new Paint();
                paint.setColor(getResources().getColor(R.color.divider_color));
                paint.setStrokeWidth(1);
                int count = parent.getChildCount();
                int left = parent.getPaddingLeft(), right = parent.getWidth() - parent.getPaddingRight(), y = 0;
                for (int i = 0; i < count; i++) {
                    y = parent.getChildAt(i).getBottom();
                    c.drawLine(left, y, right, y, paint);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_backup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_backup:
                GregorianCalendar calendar = new GregorianCalendar();
                final String fileName = String.format("%d%02d%02d%02d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                final EditText title = (EditText) LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
                title.setText(fileName);
                new AlertDialog.Builder(this).setTitle(R.string.backup_confirm)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.addBackup(DataManager.getInstance().backup(title.getText().toString()));
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .setView(title)
                        .create().show();
                return true;
            case R.id.action_tips:
                new TipsDialog(this, R.string.tips_settings_backup).show();
                return true;
        }
        return false;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView create_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            create_time = (TextView) itemView.findViewById(R.id.created_time);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private ArrayList<File> backups;

        public MyAdapter(ArrayList<File> backups) {
            this.backups = backups;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(SettingsBackupActivity.this).inflate(R.layout.item_backup, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.title.setText(backups.get(position).getName().split("[.]")[0]);
            holder.create_time.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, AppManager.LOCALE).format(new Date(backups.get(holder.getAdapterPosition()).lastModified())));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(SettingsBackupActivity.this)
                            .setMessage(getString(R.string.backed_up_at) + " " + holder.create_time.getText())
                            .setPositiveButton(R.string.recover, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GregorianCalendar calendar = new GregorianCalendar();
                                    final String fileName = String.format("auto_%d%02d%02d%02d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                                            calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                                    adapter.addBackup(DataManager.getInstance().backup(fileName));
                                    DataManager.getInstance().recover(backups.get(holder.getAdapterPosition()));
                                    DataManager.getInstance().loadCareList();
                                    DataManager.getInstance().loadHistoryCareList();
                                    DataManager.getInstance().loadProblemList();
                                    AppManager.recreateFirstActivity();
                                }
                            })
                            .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    backups.get(holder.getAdapterPosition()).delete();
                                    backups.remove(holder.getAdapterPosition());
                                    adapter.notifyItemRemoved(holder.getAdapterPosition());
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create().show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return backups.size();
        }

        public void addBackup(File backup) {
            backups.add(0, backup);
            notifyItemInserted(0);
        }
    }
}
