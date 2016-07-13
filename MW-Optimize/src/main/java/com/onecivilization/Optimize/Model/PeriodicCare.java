package com.onecivilization.Optimize.Model;

import java.util.ArrayList;

/**
 * Created by CGZ on 2016/7/8.
 */
public class PeriodicCare extends Care {

    public static final int YEAR = 6;
    public static final int MONTH = 5;
    public static final int WEEK = 4;
    public static final int DAY = 3;
    public static final int HOUR = 2;
    public static final int MINUTE = 1;

    protected int goal = 1;
    protected int progress = 0;
    protected int givenUp = 0;
    protected ArrayList<Record> records = new ArrayList<>();
    protected int periodUnit = 0;
    protected int periodLength = 1;
    protected int punishment = 1;

    public PeriodicCare(String title, String descriptionTitle, String description, int state, int order, String category, boolean isAchieved, boolean isArchived, long createTime, long achievedTime, long archivedTime, int progress, int givenUp, int goal, ArrayList<Record> records, int periodUnit, int periodLength, int punishment) {
        super(title, descriptionTitle, description, state, order, category, isAchieved, isArchived, createTime, achievedTime, archivedTime);
        this.progress = progress;
        this.givenUp = givenUp;
        this.goal = goal;
        this.records = records;
        this.periodUnit = periodUnit;
        this.periodLength = periodLength;
        this.punishment = punishment;
    }

    public float getPercentage() {
        return (float) progress / goal;
    }

    public void addProgress() {
        progress++;
        records.add(new Record(System.currentTimeMillis(), true));
        if (progress >= goal) {
            isAchieved = true;
        }
    }

    public void addGivenUp() {
        givenUp++;
        records.add(new Record(System.currentTimeMillis(), false));
        progress -= punishment;
    }

    public int getAccomplished() {
        return records.size() - givenUp;
    }

    public int getGivenUp() {
        return givenUp;
    }

    public void setGivenUp(int givenUp) {
        this.givenUp = givenUp;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(int periodUnit) {
        this.periodUnit = periodUnit;
    }

    public int getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(int periodLength) {
        this.periodLength = periodLength;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPunishment() {
        return punishment;
    }

    public void setPunishment(int punishment) {
        this.punishment = punishment;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

}
