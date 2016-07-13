package com.onecivilization.Optimize.Model;

/**
 * Created by CGZ on 2016/7/8.
 */
public class TimePair {

    public int startMinutes = 0;
    public int endMinutes = 0;

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
