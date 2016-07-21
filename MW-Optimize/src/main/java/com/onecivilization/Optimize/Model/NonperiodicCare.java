package com.onecivilization.Optimize.Model;

import android.content.Context;

import com.onecivilization.Optimize.Database.DataManager;

import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by CGZ on 2016/7/8.
 */
public class NonperiodicCare extends Care {

    public static final double MODIFY_RATE = 0.1;

    private int goal = 1;
    private int progress = 0;
    private int succeeded = 0;
    private int failed = 0;
    private int punishment = 1;
    private int modified = 0;
    private LinkedList<Record> records;

    {
        type = NONPERIODIC;
    }

    public NonperiodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int goal, int punishment) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime);
        this.goal = goal;
        this.punishment = punishment;
        records = new LinkedList<>();
    }

    public NonperiodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, int goal, int punishment, int modified, LinkedList<Record> records) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime);
        this.goal = goal;
        this.punishment = punishment;
        this.modified = modified;
        this.records = records;
        initialize();
    }

    public NonperiodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, long archivedTime, int goal, int punishment, int modified, LinkedList<Record> records) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, archivedTime);
        this.goal = goal;
        this.punishment = punishment;
        this.modified = modified;
        this.records = records;
        initialize();
    }

    private void initialize() {
        for (Record record : records) {
            if (record.tag) {
                succeeded++;
                progress++;
            } else {
                failed++;
                progress = progress - punishment;
            }
        }
    }

    public boolean isModifiable() {
        return (modified <= goal * MODIFY_RATE);
    }

    public void addRecord(boolean tag, Context context) {
        if (tag) {
            succeeded++;
            progress++;
            if (progress == goal) {
                achievedTime = System.currentTimeMillis();
            }
        } else {
            failed++;
            progress = progress - punishment;
        }
        records.add(new Record(System.currentTimeMillis(), tag));
        DataManager.getInstance(context).addRecord(createTime, records.getLast(), false);
        DataManager.getInstance(context).updateCareItem(this);
    }

    public boolean insertRecord(long time, boolean tag) {
        if (isModifiable() && time <= System.currentTimeMillis() && time >= createTime) {
            for (int i = records.size() - 1; i >= 0; i--) {
                if (records.get(i).time < time) {
                    records.add(i + 1, new Record(time, tag));
                    break;
                }
            }
            modified++;
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteRecord(int position) {
        if (isModifiable()) {
            Record record = records.get(position);
            if (record.tag) {
                succeeded--;
                progress--;
            } else {
                failed--;
                progress = progress + punishment;
            }
            modified++;
            return true;
        } else {
            return false;
        }
    }

    public boolean changeRecord(int position, long time, boolean tag) {
        if (isModifiable() && time <= System.currentTimeMillis() && time >= createTime) {
            records.remove(position);
            for (int i = records.size() - 1; i >= 0; i--) {
                if (records.get(i).time < time) {
                    records.add(i + 1, new Record(time, tag));
                    break;
                }
            }
            modified++;
            return true;
        } else {
            return false;
        }
    }

    public double getPercentage() {
        return (double) progress / goal * 100;
    }

    public int getState() {
        if (records.isEmpty()) {
            return STATE_NONE;
        } else {
            Date date = new Date();
            long today = new Date(date.getYear(), date.getMonth(), date.getDay()).getTime();
            ListIterator<Record> iterator = records.listIterator(records.size());
            int progressToday = 0;
            Record record;
            while (iterator.hasPrevious()) {
                record = iterator.previous();
                if (record.time < today) {
                    break;
                } else {
                    progressToday = record.tag ? progressToday + 1 : progressToday - punishment;
                }
            }
            if (progressToday > 0) {
                return STATE_DONE;
            } else if (progressToday == 0) {
                return STATE_NONE;
            } else {
                if (progress < 0) {
                    return STATE_MINUS;
                } else {
                    return STATE_UNDONE;
                }
            }
        }
    }

    public int getGoal() {
        return goal;
    }

    public int getProgress() {
        return progress;
    }

    public int getSucceeded() {
        return succeeded;
    }

    public int getFailed() {
        return failed;
    }

    public int getPunishment() {
        return punishment;
    }

    public int getModified() {
        return modified;
    }

    public LinkedList<Record> getRecords() {
        return records;
    }
}
