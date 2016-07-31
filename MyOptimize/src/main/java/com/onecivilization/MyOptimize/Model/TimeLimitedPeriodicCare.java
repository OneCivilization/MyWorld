package com.onecivilization.MyOptimize.Model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * Created by CGZ on 2016/7/8.
 */
public class TimeLimitedPeriodicCare extends PeriodicCare {

    protected TimePair timePair;

    {
        type = TIMELIMITED_PERIODIC;
    }

    public TimeLimitedPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int goal, int punishment, int periodUnit, int periodLength, TimePair timePair) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, goal, punishment, periodUnit, periodLength);
        this.timePair = timePair;
    }

    public TimeLimitedPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, int goal, int punishment, int modified, LinkedList<Record> records, int periodUnit, int periodLength, TimePair timePair) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, goal, punishment, modified, records, periodUnit, periodLength);
        this.timePair = timePair;
    }

    public TimeLimitedPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, long archivedTime, int goal, int punishment, int modified, LinkedList<Record> records, int periodUnit, int periodLength, TimePair timePair) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, archivedTime, goal, punishment, modified, records, periodUnit, periodLength);
        this.timePair = timePair;
    }

    public String getTimeLimitationText() {
        return String.format("%02d:%02d-%02d:%02d", timePair.getStartHour(), timePair.getStartMinutes(), timePair.getEndHour(), timePair.getEndMinutes());
    }

    @Override
    public String getPeriodText() {
        return super.getPeriodText() + "  " + getTimeLimitationText();
    }

    @Override
    public String getPeriodLengthText() {
        return super.getPeriodLengthText() + "  " + getTimeLimitationText();
    }

    public boolean isLocked() {
        boolean isLocked = true;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        int currentTime = (int) (System.currentTimeMillis() - calendar.getTimeInMillis()) / 60000;
        if (timePair.startMinutes <= currentTime && currentTime <= timePair.endMinutes) {
            isLocked = false;
        }
        return isLocked;
    }

    public TimePair getTimePair() {
        return timePair;
    }
}
