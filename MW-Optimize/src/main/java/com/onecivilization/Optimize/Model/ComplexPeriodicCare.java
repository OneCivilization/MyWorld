package com.onecivilization.Optimize.Model;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by CGZ on 2016/7/8.
 */
public class ComplexPeriodicCare extends SubPeriodicCare {

    protected ArrayList<TimePair> timePairs = new ArrayList<>();

    public ComplexPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int state, int order, String category, long createTime, long achievedTime, long archivedTime, int progress, int givenUp, int goal, ArrayList<Record> records, int periodUnit, int periodLength, int punishment, int subGoal, int subProgress, ArrayList<TimePair> timePairs) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, state, order, category, createTime, achievedTime, archivedTime, progress, givenUp, goal, records, periodUnit, periodLength, punishment, subGoal, subProgress);
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
