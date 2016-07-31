package com.onecivilization.MyOptimize.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * Created by CGZ on 2016/7/8.
 */
public class ComplexPeriodicCare extends SubPeriodicCare {

    protected ArrayList<TimePair> timePairs = new ArrayList<>();
    protected long lastStartTime = 0L;

    {
        type = COMPLEX_PERIODIC;
    }

    public ComplexPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int goal, int punishment, int periodUnit, int periodLength, int subGoal, ArrayList<TimePair> timePairs) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, goal, punishment, periodUnit, periodLength, subGoal);
        this.timePairs = timePairs;
    }

    public ComplexPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, int goal, int punishment, int modified, LinkedList<Record> records, int periodUnit, int periodLength, int subGoal, LinkedList<Record> subRecords, ArrayList<TimePair> timePairs) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, goal, punishment, modified, records, periodUnit, periodLength, subGoal, subRecords);
        this.timePairs = timePairs;
    }

    public ComplexPeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, long archivedTime, int goal, int punishment, int modified, LinkedList<Record> records, int periodUnit, int periodLength, int subGoal, LinkedList<Record> subRecords, ArrayList<TimePair> timePairs) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, archivedTime, goal, punishment, modified, records, periodUnit, periodLength, subGoal, subRecords);
        this.timePairs = timePairs;
    }

    @Override
    public boolean isSigned() {
        isLocked();
        if (subRecords.isEmpty()) return false;
        return subRecords.getLast().time >= lastStartTime;
    }

    public boolean isLocked() {
        boolean isLocked = true;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        int currentTime = (int) (System.currentTimeMillis() - calendar.getTimeInMillis()) / 60000, i = 0;
        for (TimePair timePair : timePairs) {
            if (currentTime >= timePair.startMinutes && currentTime <= timePair.endMinutes) {
                isLocked = false;
                lastStartTime = calendar.getTimeInMillis() + timePair.startMinutes * 60000;
                break;
            }
        }
        return isLocked;
    }

    public String getTimeLimitationText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TimePair timePair : timePairs) {
            stringBuilder.append(String.format("%02d:%02d-%02d:%02d  ", timePair.getStartHour(), timePair.getStartMinutes(), timePair.getEndHour(), timePair.getEndMinutes()));
        }
        return stringBuilder.toString();
    }

    @Override
    public String getPeriodText() {
        return super.getPeriodText() + "  " + getTimeLimitationText();
    }

    @Override
    public String getPeriodLengthText() {
        return super.getPeriodLengthText() + "  " + getTimeLimitationText();
    }

    public ArrayList<TimePair> getTimePairs() {
        return timePairs;
    }
}
