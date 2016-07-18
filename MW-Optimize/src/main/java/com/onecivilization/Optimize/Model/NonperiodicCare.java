package com.onecivilization.Optimize.Model;

import java.util.ArrayList;

/**
 * Created by CGZ on 2016/7/8.
 */
public class NonperiodicCare extends Care {

    private int goal = 1;
    private int progress = 0;
    private int givenUp = 0;
    private ArrayList<Record> records = new ArrayList<>();

    {
        type = NONPERIODIC;
    }

    public NonperiodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int state, int order, String category, long createTime, long achievedTime, long archivedTime, int goal, int progress, int givenUp, ArrayList<Record> records) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, archivedTime);
        this.goal = goal;
        this.progress = progress;
        this.givenUp = givenUp;
        this.records = records;
    }

    public float getPercentage() {
        return (float) progress / goal;
    }

    public void addProgress() {
        progress++;
        records.add(new Record(System.currentTimeMillis(), true));
        if (progress >= goal) {
            achievedTime = System.currentTimeMillis();
        }
    }

    public void addGivenUp() {
        givenUp++;
        records.add(new Record(System.currentTimeMillis(), false));
    }

    public int getGivenUp() {
        return givenUp;
    }

    public void setGivenUp(int givenUp) {
        this.givenUp = givenUp;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}
