package com.onecivilization.MyOptimize.Model;

import com.onecivilization.MyOptimize.Database.DataManager;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by CGZ on 2016/7/8.
 */
public class SubPeriodicCare extends PeriodicCare {

    protected int subGoal = 2;
    protected int subProgress = 0;
    protected LinkedList<Record> subRecords;

    {
        type = SUB_PERIODIC;
    }

    public SubPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int goal, int punishment, int periodUnit, int periodLength, int subGoal) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, goal, punishment, periodUnit, periodLength);
        this.subGoal = subGoal;
        subRecords = new LinkedList<>();
    }

    public SubPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, int goal, int punishment, int modified, LinkedList<Record> records, int periodUnit, int periodLength, int subGoal, LinkedList<Record> subRecords) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, goal, punishment, modified, records, periodUnit, periodLength);
        this.subGoal = subGoal;
        this.subRecords = subRecords;
        initSubProgress();
    }

    public SubPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, long archivedTime, int goal, int punishment, int modified, LinkedList<Record> records, int periodUnit, int periodLength, int subGoal, LinkedList<Record> subRecords) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, archivedTime, goal, punishment, modified, records, periodUnit, periodLength);
        this.subGoal = subGoal;
        this.subRecords = subRecords;
        initSubProgress();
    }

    private void initSubProgress() {
        long start = periodStartTime + periodTime * getPeriodCount();
        ListIterator<Record> iterator = subRecords.listIterator(subRecords.size());
        Record subRecord;
        while (iterator.hasPrevious()) {
            subRecord = iterator.previous();
            if (subRecord.time < start) {
                break;
            }
            subProgress++;
        }
    }

    public void addSubRecord() {
        subProgress++;
        if (subProgress == subGoal) {
            addRecord();
        }
        subRecords.add(new Record(System.currentTimeMillis(), true));
        DataManager.getInstance().addSubRecord(createTime, subRecords.get(subRecords.size() - 1), false);
    }

    public boolean deleteSubRecord() {
        if (subProgress > 0) {
            if (subProgress == subGoal) {
                deleteRecord();
            }
            subProgress--;
            DataManager.getInstance().deleteSubRecord(createTime, subRecords.get(subRecords.size() - 1), false);
            subRecords.remove(subRecords.size() - 1);
            return true;
        } else {
            return false;
        }
    }

    public String getSubProgressText() {
        return subProgress + "/" + subGoal;
    }

    public int getSubGoal() {
        return subGoal;
    }

    public int getSubProgress() {
        return subProgress;
    }

    public LinkedList<Record> getSubRecords() {
        return subRecords;
    }
}
