package com.onecivilization.MyOptimize.Model;

import android.widget.Toast;

import com.onecivilization.MyOptimize.Database.DataManager;
import com.onecivilization.MyOptimize.R;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by CGZ on 2016/7/8.
 */
public class PeriodicCare extends NonperiodicCare {

    public static final int YEAR = 6;
    public static final int MONTH = 5;
    public static final int WEEK = 4;
    public static final int DAY = 3;

    protected int periodUnit = 0;
    protected int periodLength = 1;
    protected long periodStartTime = 0L;
    protected long periodTime = 0L;

    {
        type = PERIODIC;
    }

    public PeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int goal, int punishment, int periodUnit, int periodLength) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, goal, punishment);
        this.periodUnit = periodUnit;
        this.periodLength = periodLength;
        calculatePeriod();
        initRecord();
    }

    public PeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, int goal, int punishment, int modified, LinkedList<Record> records, int periodUnit, int periodLength) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, goal, punishment, modified, records);
        this.periodUnit = periodUnit;
        this.periodLength = periodLength;
        calculatePeriod();
        initRecord();
    }

    public PeriodicCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, long archivedTime, int goal, int punishment, int modified, LinkedList<Record> records, int periodUnit, int periodLength) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, archivedTime, goal, punishment, modified, records);
        this.periodUnit = periodUnit;
        this.periodLength = periodLength;
        calculatePeriod();
        initRecord();
    }

    private void calculatePeriod() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(createTime);
        switch (periodUnit) {
            case DAY:
                periodStartTime = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).getTimeInMillis();
                periodTime = periodLength * 86400000;
                break;
            case WEEK:
                if (AppManager.LOCALE.equals(Locale.CHINESE)) {
                    periodStartTime = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_WEEK) + 2).getTimeInMillis();
                } else {
                    periodStartTime = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_WEEK) + 1).getTimeInMillis();
                }
                periodTime = periodLength * 604800000;
                break;
            case MONTH:
                periodStartTime = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1).getTimeInMillis();
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                for (int i = 0; i < periodLength; i++) {
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                    periodTime += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                }
                periodTime *= 86400000;
                break;
            case YEAR:
                periodStartTime = new GregorianCalendar(calendar.get(Calendar.YEAR), 0, 1).getTimeInMillis();
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                for (int i = 0; i < periodLength; i++) {
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                    periodTime += calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
                }
                periodTime *= 86400000;
                break;
        }
    }

    public long getPeriodUnitTime() {
        switch (periodUnit) {
            case DAY:
                return 86400000L;
            case WEEK:
                return 604800000L;
            case MONTH:
                return 2419200000L;
            case YEAR:
                return 31536000000L;
            default:
                return 0L;
        }
    }

    private void initRecord() {
        if (archivedTime == 0) {
            long start = periodStartTime + periodTime * getPeriodCount();
            if (records.isEmpty()) {
                if (System.currentTimeMillis() >= createTime) {
                    addRecord(false);
                }
            } else if (records.getLast().time < start) {
                long lastRecordTime = periodStartTime + records.size() * periodTime;
                int lostPeriodCount = getPeriodCount() + 1 - records.size();
                for (int i = 0; i < lostPeriodCount; i++) {
                    addDefaultFalseRecord(lastRecordTime);
                    lastRecordTime += periodTime;
                }
                DataManager.getInstance().updateCareItem(this);
            }
        }
    }

    public void addDefaultFalseRecord(long time) {
        failed++;
        progress = progress - punishment;
        if (progress < goal) {
            achievedTime = 0L;
        }
        records.add(new Record(time, false));
        DataManager.getInstance().addRecord(createTime, records.getLast(), false);
    }

    public void addRecord() {
        if (records.isEmpty()) {
            Toast.makeText(AppManager.getContext(), R.string.attention_not_activated, Toast.LENGTH_SHORT).show();
            return;
        }
        modified--;
        deleteRecord(records.size() - 1);
        addRecord(true);
    }

    public void deleteRecord() {
        modified--;
        deleteRecord(records.size() - 1);
        addRecord(false);
    }

    /*public boolean modifyRecord(long time) {
        ListIterator<Record> iterator = records.listIterator(records.size());
        Record record;
        int i = records.size(), position = -1;
        while (iterator.hasPrevious()) {
            record = iterator.previous();
            i--;
            if (record.time == time) {
                position = i;
                break;
            } else if (record.time<time) {
                break;
            }
        }
        if (position != -1) {
            record = records.get(position);
            if (record.tag) {
                succeeded--;
                failed++;
                progress-=punishment+1;
            } else {
                succeeded++;
                failed--;
                progress += punishment+1;
            }
            if (progress == goal && !record.tag) {
                achievedTime = System.currentTimeMillis();
            } else if (progress < goal) {
                achievedTime = 0L;
            }
            modified++;
            record.tag = !record.tag;
            DataManager.getInstance().modifyRecord(createTime, record, false);
            DataManager.getInstance().updateCareItem(this);
            return true;
        } else {
            return false;
        }
    }*/

    @Override
    public int getState() {
        if (records.isEmpty()) {
            return STATE_UNDONE;
        }
        if (isSigned()) {
            if (achievedTime == 0L) {
                return STATE_DONE;
            } else {
                return STATE_ACHIEVED;
            }
        } else {
            if (achievedTime == 0L && progress + 1 != goal) {
                if (getProgress() < 0) {
                    return STATE_MINUS;
                } else {
                    return STATE_UNDONE;
                }
            } else {
                return STATE_ACHIEVED_UNDONE;
            }
        }
    }

    public boolean isSigned() {
        if (records.isEmpty()) return false;
        return records.getLast().tag;
    }

    public int getPeriodCount() {
        return (int) ((System.currentTimeMillis() - periodStartTime) / periodTime);
    }

    public int getPeriodUnit() {
        return periodUnit;
    }

    public int getPeriodLength() {
        return periodLength;
    }

    public String getPeriodText() {
        String[] periodUnits = AppManager.getContext().getResources().getStringArray(R.array.period_units);
        String periodText;
        if (periodLength == 1) {
            periodText = AppManager.getContext().getString(R.string.every) + periodUnits[6 - periodUnit];
        } else {
            int leftLength = (int) (((periodStartTime + (getPeriodCount() + 1) * periodTime) - System.currentTimeMillis()) / getPeriodUnitTime());
            if (AppManager.LOCALE.equals(Locale.CHINESE)) {
                periodText = AppManager.getContext().getString(R.string.every) + " " + periodLength + " " + periodUnits[6 - periodUnit] + "，剩余 " + leftLength + " " + periodUnits[6 - periodUnit];
            } else {
                periodText = AppManager.getContext().getString(R.string.every) + " " + periodLength + " " + periodUnits[6 - periodUnit] + "s，" + leftLength + " " + periodUnits[6 - periodUnit] + "s left";
            }

        }
        return periodText;
    }

    public String getPeriodLengthText() {
        String[] periodUnits = AppManager.getContext().getResources().getStringArray(R.array.period_units);
        String periodText;
        if (periodLength == 1) {
            periodText = AppManager.getContext().getString(R.string.every) + periodUnits[6 - periodUnit];
        } else {
            if (AppManager.LOCALE.equals(Locale.CHINESE)) {
                periodText = AppManager.getContext().getString(R.string.every) + " " + periodLength + " " + periodUnits[6 - periodUnit];
            } else {
                periodText = AppManager.getContext().getString(R.string.every) + " " + periodLength + " " + periodUnits[6 - periodUnit] + "s";
            }

        }
        return periodText;
    }

    public String getPeriodCountText() {
        String[] periodUnits = AppManager.getContext().getResources().getStringArray(R.array.period_units);
        String periodCountText;
        if (periodLength == 1) {
            if (AppManager.LOCALE.equals(Locale.CHINESE)) {
                periodCountText = "第 " + (getPeriodCount() + 1) + " " + periodUnits[6 - periodUnit];
            } else {
                periodCountText = periodUnits[6 - periodUnit] + " " + (getPeriodCount() + 1);
            }
        } else {
            if (AppManager.LOCALE.equals(Locale.CHINESE)) {
                periodCountText = "第 " + (getPeriodCount() + 1) + " 周期";
            } else {
                periodCountText = "Cycle " + (getPeriodCount() + 1);
            }
        }
        return periodCountText;
    }

    @Override
    public float getPercentage() {
        return (float) getProgress() / goal * 100;
    }

    @Override
    public int getProgress() {
        if (records.isEmpty()) return 0;
        if (isSigned()) {
            return progress;
        } else {
            return progress + punishment;
        }
    }

    @Override
    public String getProgressText() {
        String progressText;
        int progress = getProgress();
        if (progress > 0) {
            progressText = "+ " + progress;
        } else if (progress == 0) {
            progressText = String.valueOf(0);
        } else {
            progressText = "- " + (-progress);
        }
        return progressText;
    }

    @Override
    public int getFailed() {
        if (records.isEmpty()) return 0;
        if (isSigned()) {
            return failed;
        } else {
            return failed - 1;
        }
    }
}
