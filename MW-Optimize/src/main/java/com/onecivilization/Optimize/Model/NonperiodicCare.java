package com.onecivilization.Optimize.Model;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by CGZ on 2016/7/8.
 */
public class NonperiodicCare extends Care {

    public static final double MODIFY_RATE = 0.1;

    private int goal = 1;
    private int progress = 0;
    private int succeeded = 0;
    private int givenUp = 0;
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
                givenUp++;
                progress = progress - punishment;
            }
        }
    }

    public boolean isModifiable() {
        return (modified <= goal * MODIFY_RATE);
    }

    public void addRecord(boolean tag) {
        if (tag) {
            succeeded++;
            progress++;
        } else {
            givenUp++;
            progress = progress - punishment;
        }
        records.add(new Record(System.currentTimeMillis(), tag));
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
                givenUp--;
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
            return STATE_UNDONE;
        } else {
            Date date = new Date();
            if (records.getLast().time >= new Date(date.getYear(), date.getMonth(), date.getDay()).getTime()) {
                return STATE_DONE;
            } else if (progress < 0) {
                return STATE_MINUS;
            } else {
                return STATE_UNDONE;
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

    public int getGivenUp() {
        return givenUp;
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
