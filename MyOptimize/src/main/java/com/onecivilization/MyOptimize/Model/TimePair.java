package com.onecivilization.MyOptimize.Model;

/**
 * Created by CGZ on 2016/7/8.
 */
public class TimePair {

    public int startMinutes = 0;
    public int endMinutes = 0;

    public TimePair(int startMinutes, int endMinutes) {
        this.startMinutes = startMinutes;
        this.endMinutes = endMinutes;
    }

    public TimePair(int startHour, int startMinute, int endHour, int endMinute) {
        startMinutes = startHour * 60 + startMinute;
        endMinutes = endHour * 60 + endMinute;
    }

    public int getStartHour() {
        return startMinutes / 60;
    }

    public int getEndHour() {
        return endMinutes / 60;
    }

    public int getStartMinutes() {
        return startMinutes - startMinutes / 60 * 60;
    }

    public int getEndMinutes() {
        return endMinutes - endMinutes / 60 * 60;
    }

}
