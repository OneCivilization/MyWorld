package com.onecivilization.MyOptimize.Model;

import java.util.Calendar;

/**
 * Created by CGZ on 2016/7/8.
 */
public class TimeLimitedPeriodicCare extends PeriodicCare {

    protected TimePair timePair;

    public TimeLimitedPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int goal, int punishment, int periodUnit, int periodLength, TimePair timePair) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, goal, punishment, periodUnit, periodLength);
        this.timePair = timePair;
    }

    public String getTimeLimitationString() {
        return timePair.getStartHour() + ":" + timePair.getStartMinutes() + "-" + timePair.getEndHour() + ":" + timePair.getEndMinutes();
    }

    public boolean isLocked() {
        boolean isLocked = true;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        int currentTime = (int) (System.currentTimeMillis() - calendar.getTimeInMillis()) / 60000;
        if (timePair.startMinutes <= currentTime && currentTime <= timePair.endMinutes) {
            isLocked = false;
        }
        return isLocked;
    }

    public TimePair getTimePair() {
        return timePair;
    }

    public void setTimePair(TimePair timePair) {
        this.timePair = timePair;
    }
}
