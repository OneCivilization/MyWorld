package com.onecivilization.MyOptimize.Model;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by CGZ on 2016/7/24.
 */
public class RecordsSortHelper {

    private LinkedList<Record> records;
    private LinkedList<DayRecords> dayRecords;

    public RecordsSortHelper(LinkedList<Record> records) {
        this.records = records;
        dayRecords = new LinkedList<>();
        refresh();
    }

    public static Bundle getInformation(ArrayList<Record> records, int punishment) {
        int progress = 0, succeeded = 0, failed = 0;
        for (Record record : records) {
            if (record.tag) {
                progress++;
                succeeded++;
            } else {
                failed++;
                progress -= punishment;
            }
        }
        Bundle result = new Bundle(3);
        result.putInt("progress", progress);
        result.putInt("succeeded", succeeded);
        result.putInt("failed", failed);
        return result;
    }

    public void refresh() {
        GregorianCalendar calendar = new GregorianCalendar();
        Record record;
        long end = 0L;
        ArrayList<Record> recordsToday = new ArrayList<>();
        ListIterator<Record> iterator = records.listIterator();
        while (iterator.hasNext()) {
            record = iterator.next();
            if (record.time >= end) {
                if (!recordsToday.isEmpty()) {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
                    dayRecords.add(new DayRecords(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), recordsToday));
                }
                calendar.setTimeInMillis(record.time);
                calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 1);
                end = calendar.getTimeInMillis();
                recordsToday = new ArrayList<>();
            }
            recordsToday.add(record);
        }
        if (!recordsToday.isEmpty()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
            dayRecords.add(new DayRecords(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), recordsToday));
        }
    }

    public LinkedList<DayRecords> getDayRecords() {
        return dayRecords;
    }

    public ArrayList<Record> getDayRecord(int year, int month, int day) {
        for (DayRecords dayRecord : dayRecords) {
            if (dayRecord.day == day && dayRecord.month == month && dayRecord.year == year) {
                return dayRecord.recordsToday;
            } else if (dayRecord.year > year || (dayRecord.year == year && dayRecord.month > month) || (dayRecord.day > day && dayRecord.month == month && dayRecord.year == year)) {
                break;
            }
        }
        return new ArrayList<>();
    }

    public class DayRecords {
        public int year;
        public int month;
        public int day;
        public ArrayList<Record> recordsToday;

        public DayRecords(int year, int month, int day, ArrayList<Record> recordsToday) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.recordsToday = recordsToday;
        }
    }
}
