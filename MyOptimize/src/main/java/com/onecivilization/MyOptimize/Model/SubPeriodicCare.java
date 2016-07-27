package com.onecivilization.MyOptimize.Model;

/**
 * Created by CGZ on 2016/7/8.
 */
public class SubPeriodicCare extends PeriodicCare {

    protected int subGoal = 2;
    protected int subProgress = 0;

    public SubPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int goal, int punishment, int periodUnit, int periodLength, int subGoal, int subProgress) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, goal, punishment, periodUnit, periodLength);
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
