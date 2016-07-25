package com.onecivilization.Optimize.Model;

import com.onecivilization.Optimize.Database.DataManager;

import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by CGZ on 2016/7/8.
 */
public class NonperiodicCare extends Care {

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

    public void addRecord(boolean tag) {
        if (tag) {
            succeeded++;
            progress++;
            if (progress == goal) {
                achievedTime = System.currentTimeMillis();
            }
        } else {
            failed++;
            progress = progress - punishment;
            if (progress < goal) {
                achievedTime = 0L;
            }
        }
        records.add(new Record(System.currentTimeMillis(), tag));
        DataManager.getInstance(null).addRecord(createTime, records.getLast(), false);
        DataManager.getInstance(null).updateCareItem(this);
    }

    public boolean insertRecord(long time, boolean tag) {
        Record record = new Record(time, tag);
        if (records.isEmpty() || time < records.getFirst().time) {
            records.add(0, record);
        } else {
            ListIterator<Record> iterator = records.listIterator(records.size());
            while (iterator.hasPrevious()) {
                if (iterator.previous().time < time) {
                    iterator.next();
                    iterator.add(record);
                    break;
                }
            }
        }
        if (record.tag) {
            succeeded++;
            progress++;
        } else {
            failed++;
            progress -= punishment;
        }
        if (progress == goal && record.tag) {
            achievedTime = System.currentTimeMillis();
        } else if (progress < goal) {
            achievedTime = 0L;
        }
        modified++;
        DataManager.getInstance(null).addRecord(createTime, record, false);
        DataManager.getInstance(null).updateCareItem(this);
        return true;
    }

    public boolean deleteRecord(long time) {
        ListIterator<Record> iterator = records.listIterator();
        Record record;
        int i = -1, position = -1;
        while (iterator.hasNext()) {
            record = iterator.next();
            i++;
            if (record.time == time) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            return deleteRecord(position);
        } else {
            return false;
        }
    }

    public boolean deleteRecord(int position) {
        Record record = records.get(position);
        if (record.tag) {
            succeeded--;
            progress--;
        } else {
            failed--;
            progress = progress + punishment;
        }
        if (progress == goal && !record.tag) {
            achievedTime = System.currentTimeMillis();
        } else if (progress < goal) {
            achievedTime = 0L;
        }
        modified++;
        DataManager.getInstance(null).deleteRecord(createTime, record, false);
        DataManager.getInstance(null).updateCareItem(this);
        records.remove(position);
        return true;
    }

    public float getPercentage() {
        return (float) progress / goal * 100;
    }

    public int getState() {
        if (achievedTime != 0L) return STATE_ACHIEVED;
        if (records.isEmpty()) {
            return STATE_NONE;
        } else {
            Date date = new Date();
            long today = new Date(date.getYear(), date.getMonth(), date.getDate()).getTime();
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
                if (progress < 0) return STATE_MINUS;
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
