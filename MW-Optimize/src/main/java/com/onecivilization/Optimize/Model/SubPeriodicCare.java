package com.onecivilization.Optimize.Model;

import java.util.ArrayList;

/**
 * Created by CGZ on 2016/7/8.
 */
public class SubPeriodicCare extends PeriodicCare {

    protected int subGoal = 2;
    protected int subProgress = 0;

    public SubPeriodicCare(String title, String descriptionTitle, String description, int state, int order, String category, boolean isAchieved, boolean isArchived, long createTime, long achievedTime, long archivedTime, int progress, int givenUp, int goal, ArrayList<Record> records, int periodUnit, int periodLength, int punishment, int subGoal, int subProgress) {
        super(title, descriptionTitle, description, state, order, category, isAchieved, isArchived, createTime, achievedTime, archivedTime, progress, givenUp, goal, records, periodUnit, periodLength, punishment);
        this.subGoal = subGoal;
        this.subProgress = subProgress;
    }

    public int getSubGoal() {
        return subGoal;
    }

    public void setSubGoal(int subGoal) {
        this.subGoal = subGoal;
    }

    public int getSubProgress() {
        return subProgress;
    }

    public void setSubProgress(int subProgress) {
        this.subProgress = subProgress;
    }
}
