package com.onecivilization.MyOptimize.Model;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by CGZ on 2016/7/8.
 */
public class ComplexPeriodicCare extends SubPeriodicCare {

    protected ArrayList<TimePair> timePairs = new ArrayList<>();

    public ComplexPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int goal, int punishment, int periodUnit, int periodLength, int subGoal, ArrayList<TimePair> timePairs) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, goal, punishment, periodUnit, periodLength, subGoal);
        this.timePairs = timePairs;
    }

    public String getTimeLimitationString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TimePair timePair : timePairs) {
            stringBuilder.append(timePair.getStartHour() + ":" + timePair.getStartMinutes() + "-" + timePair.getEndHour() + ":" + timePair.getEndMinutes() + " ");
        }
        return stringBuilder.toString();
    }

    public boolean isLocked() {
        boolean isLocked = false;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        int currentTime = (int) (System.currentTimeMillis() - calendar.getTimeInMillis()) / 60000;
        for (TimePair timePair : timePairs) {
            if (currentTime <= timePair.startMinutes || currentTime >= timePair.endMinutes) {
                isLocked = true;
            }
        }
        return isLocked;
    }

}
