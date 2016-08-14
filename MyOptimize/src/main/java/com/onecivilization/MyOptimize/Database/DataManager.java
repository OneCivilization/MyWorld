package com.onecivilization.MyOptimize.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.onecivilization.MyOptimize.Database.Schema.CareItemTable;
import com.onecivilization.MyOptimize.Database.Schema.RecordTable;
import com.onecivilization.MyOptimize.Database.Schema.TimePairTable;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.ComplexPeriodicCare;
import com.onecivilization.MyOptimize.Model.NonperiodicCare;
import com.onecivilization.MyOptimize.Model.PeriodicCare;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.Model.Record;
import com.onecivilization.MyOptimize.Model.SubPeriodicCare;
import com.onecivilization.MyOptimize.Model.TextCare;
import com.onecivilization.MyOptimize.Model.TimeLimitedPeriodicCare;
import com.onecivilization.MyOptimize.Model.TimePair;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by CGZ on 2016/7/10.
 */
public class DataManager {

    private static DataManager dataManager;
    public final String APP_DIRECTORY = "/My Optimize";
    private SQLiteDatabase db;
    private List<Care> careList;
    private List<Care> historyCareList;
    private List<Problem> problemList;
    private List<Problem> historyProblemList;

    private DataManager(Context context) {
        db = new DatabaseOpenHelper(context).getWritableDatabase();
    }

    public synchronized static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager(AppManager.getContext());
        }
        return dataManager;
    }

    public void addRecord(long careItemId, Record record, boolean isHistory) {
        ContentValues values = new ContentValues();
        String recordTableName = isHistory ? RecordTable.HISTORY_NAME : RecordTable.NAME;
        values.put(RecordTable.Cols.CARE_ITEM_ID, careItemId);
        values.put(RecordTable.Cols.TIME, record.time);
        values.put(RecordTable.Cols.TAG, record.tag);
        db.insert(recordTableName, null, values);
    }

    public void deleteRecord(long careItemId, Record record, boolean isHistory) {
        String recordTableName = isHistory ? RecordTable.HISTORY_NAME : RecordTable.NAME;
        db.delete(recordTableName, RecordTable.Cols.CARE_ITEM_ID + "=" + careItemId + " and " + RecordTable.Cols.TIME + "=" + record.time + " and " + RecordTable.Cols.TAG + "=" + (record.tag ? 1 : 0), null);
    }

    /*public void modifyRecord(long careItemId, Record record, boolean isHistory) {
        String recordTableName = isHistory ? RecordTable.HISTORY_NAME : RecordTable.NAME;
        ContentValues values = new ContentValues();
        values.put(RecordTable.Cols.TAG, record.tag);
        db.update(recordTableName, values, RecordTable.Cols.CARE_ITEM_ID + "=" + careItemId + " and " + RecordTable.Cols.TIME + "=" + record.time, null);
    }*/

    public void addRecords(long careItemId, List<Record> records, boolean isHistory) {
        ContentValues values = new ContentValues();
        String recordTableName = isHistory ? RecordTable.HISTORY_NAME : RecordTable.NAME;
        for (Record record : records) {
            values.put(RecordTable.Cols.CARE_ITEM_ID, careItemId);
            values.put(RecordTable.Cols.TIME, record.time);
            values.put(RecordTable.Cols.TAG, record.tag);
            db.insert(recordTableName, null, values);
            values.clear();
        }
    }

    public void deleteRecords(long careItemId, boolean isHistory) {
        String recordTableName = isHistory ? RecordTable.HISTORY_NAME : RecordTable.NAME;
        db.delete(recordTableName, RecordTable.Cols.CARE_ITEM_ID + "=" + careItemId, null);
    }

    public void addSubRecord(long careItemId, Record record, boolean isHistory) {
        ContentValues values = new ContentValues();
        String recordTableName = isHistory ? RecordTable.HISTORY_SUB_NAME : RecordTable.SUB_NAME;
        values.put(RecordTable.Cols.CARE_ITEM_ID, careItemId);
        values.put(RecordTable.Cols.TIME, record.time);
        values.put(RecordTable.Cols.TAG, record.tag);
        db.insert(recordTableName, null, values);
    }

    public void deleteSubRecord(long careItemId, Record record, boolean isHistory) {
        String recordTableName = isHistory ? RecordTable.HISTORY_SUB_NAME : RecordTable.SUB_NAME;
        db.delete(recordTableName, RecordTable.Cols.CARE_ITEM_ID + "=" + careItemId + " and " + RecordTable.Cols.TIME + "=" + record.time + " and " + RecordTable.Cols.TAG + "=" + (record.tag ? 1 : 0), null);
    }

    public void addSubRecords(long careItemId, List<Record> records, boolean isHistory) {
        ContentValues values = new ContentValues();
        String recordTableName = isHistory ? RecordTable.HISTORY_SUB_NAME : RecordTable.SUB_NAME;
        for (Record record : records) {
            values.put(RecordTable.Cols.CARE_ITEM_ID, careItemId);
            values.put(RecordTable.Cols.TIME, record.time);
            values.put(RecordTable.Cols.TAG, record.tag);
            db.insert(recordTableName, null, values);
            values.clear();
        }
    }

    public void deleteSubRecords(long careItemId, boolean isHistory) {
        String recordTableName = isHistory ? RecordTable.HISTORY_SUB_NAME : RecordTable.SUB_NAME;
        db.delete(recordTableName, RecordTable.Cols.CARE_ITEM_ID + "=" + careItemId, null);
    }

    public LinkedList<Record> getRecordList(long careItemId, boolean isHistory) {
        LinkedList<Record> records = new LinkedList<>();
        Cursor cursor;
        if (isHistory) {
            cursor = db.rawQuery("select * from " + RecordTable.HISTORY_NAME + " where "
                    + RecordTable.Cols.CARE_ITEM_ID + "=? order by " + RecordTable.Cols.TIME, new String[]{String.valueOf(careItemId)});
        } else {
            cursor = db.rawQuery("select * from " + RecordTable.NAME + " where "
                    + RecordTable.Cols.CARE_ITEM_ID + "=? order by " + RecordTable.Cols.TIME, new String[]{String.valueOf(careItemId)});
        }
        if (cursor.moveToFirst()) {
            do {
                long time = cursor.getLong(cursor.getColumnIndex(RecordTable.Cols.TIME));
                int tag = cursor.getInt(cursor.getColumnIndex(RecordTable.Cols.TAG));
                records.add(new Record(time, tag != 0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    public LinkedList<Record> getSubRecordList(long careItemId, boolean isHistory) {
        LinkedList<Record> records = new LinkedList<>();
        Cursor cursor;
        if (isHistory) {
            cursor = db.rawQuery("select * from " + RecordTable.HISTORY_SUB_NAME + " where "
                    + RecordTable.Cols.CARE_ITEM_ID + "=? order by " + RecordTable.Cols.TIME, new String[]{String.valueOf(careItemId)});
        } else {
            cursor = db.rawQuery("select * from " + RecordTable.SUB_NAME + " where "
                    + RecordTable.Cols.CARE_ITEM_ID + "=? order by " + RecordTable.Cols.TIME, new String[]{String.valueOf(careItemId)});
        }
        if (cursor.moveToFirst()) {
            do {
                long time = cursor.getLong(cursor.getColumnIndex(RecordTable.Cols.TIME));
                int tag = cursor.getInt(cursor.getColumnIndex(RecordTable.Cols.TAG));
                records.add(new Record(time, tag != 0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    public void addTimePair(long careItemId, TimePair timePair, boolean isHistory) {
        ContentValues values = new ContentValues();
        String timePairTableName = isHistory ? TimePairTable.HISTORY_NAME : TimePairTable.NAME;
        values.put(TimePairTable.Cols.CARE_ITEM_ID, careItemId);
        values.put(TimePairTable.Cols.START_MINUTES, timePair.startMinutes);
        values.put(TimePairTable.Cols.END_MINUTES, timePair.endMinutes);
        db.insert(timePairTableName, null, values);
    }

    public TimePair getTimePair(long careItemId, boolean isHistory) {
        String timePairTableName = isHistory ? TimePairTable.HISTORY_NAME : TimePairTable.NAME;
        Cursor cursor = db.rawQuery("select * from " + timePairTableName + " where "
                + TimePairTable.Cols.CARE_ITEM_ID + "=" + careItemId, null);
        if (cursor.moveToFirst()) {
            int startMinutes = cursor.getInt(cursor.getColumnIndex(TimePairTable.Cols.START_MINUTES));
            int endMinutes = cursor.getInt(cursor.getColumnIndex(TimePairTable.Cols.END_MINUTES));
            cursor.close();
            return new TimePair(startMinutes, endMinutes);
        }
        cursor.close();
        return null;
    }

    public void addTimePairs(long careItemId, ArrayList<TimePair> timePairs, boolean isHistory) {
        ContentValues values = new ContentValues();
        String timePairTableName = isHistory ? TimePairTable.HISTORY_NAME : TimePairTable.NAME;
        for (TimePair timePair : timePairs) {
            values.put(TimePairTable.Cols.CARE_ITEM_ID, careItemId);
            values.put(TimePairTable.Cols.START_MINUTES, timePair.startMinutes);
            values.put(TimePairTable.Cols.END_MINUTES, timePair.endMinutes);
            db.insert(timePairTableName, null, values);
            values.clear();
        }
    }

    public ArrayList<TimePair> getTimePairs(long careItemId, boolean isHistory) {
        ArrayList<TimePair> timePairs = new ArrayList<>();
        String timePairTableName = isHistory ? TimePairTable.HISTORY_NAME : TimePairTable.NAME;
        int startMinutes, endMinutes;
        Cursor cursor = db.rawQuery("select * from " + timePairTableName + " where "
                + TimePairTable.Cols.CARE_ITEM_ID + "=" + careItemId, null);
        if (cursor.moveToFirst()) {
            do {
                startMinutes = cursor.getInt(cursor.getColumnIndex(TimePairTable.Cols.START_MINUTES));
                endMinutes = cursor.getInt(cursor.getColumnIndex(TimePairTable.Cols.END_MINUTES));
                timePairs.add(new TimePair(startMinutes, endMinutes));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return timePairs;
    }

    public void deleteTimePairs(long careItemId, boolean isHistory) {
        String timePairTableName = isHistory ? TimePairTable.HISTORY_NAME : TimePairTable.NAME;
        db.delete(timePairTableName, TimePairTable.Cols.CARE_ITEM_ID + "=" + careItemId, null);
    }

    public void loadCareList() {
        careList = new LinkedList<>();
        Cursor cursor = db.rawQuery("select * from " + CareItemTable.NAME + " order by " + CareItemTable.Cols.ORDER, null);
        if (cursor.moveToFirst()) {
            do {
                long createTime = cursor.getLong(cursor.getColumnIndex(CareItemTable.Cols.CREATE_TIME));
                int order = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.ORDER));
                String title = cursor.getString(cursor.getColumnIndex(CareItemTable.Cols.TITLE));
                String descriptionTitle = cursor.getString(cursor.getColumnIndex(CareItemTable.Cols.DESCRIPTION_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(CareItemTable.Cols.DESCRIPTION));
                long descriptionLastEditedTime = cursor.getLong(cursor.getColumnIndex(CareItemTable.Cols.DESCRIPTION_LAST_EDITED_TIME));
                long achievedTime = cursor.getLong(cursor.getColumnIndex(CareItemTable.Cols.ACHIEVED_TIME));
                int type = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.TYPE));
                switch (type) {
                    case Care.TEXT:
                        int color = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.COLOR));
                        careList.add(new TextCare(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, color));
                        break;
                    case Care.NONPERIODIC:
                        int goal = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        LinkedList<Record> records = getRecordList(createTime, false);
                        careList.add(new NonperiodicCare(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime,
                                goal, punishment, modified, records));
                        break;
                    case Care.PERIODIC:
                        int goal1 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment1 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified1 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        int periodUnit = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_UNIT));
                        int periodLength = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_LENGTH));
                        LinkedList<Record> records1 = getRecordList(createTime, false);
                        careList.add(new PeriodicCare(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime,
                                goal1, punishment1, modified1, records1, periodUnit, periodLength));
                        break;
                    case Care.SUB_PERIODIC:
                        int goal2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        int periodUnit2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_UNIT));
                        int periodLength2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_LENGTH));
                        int subGoal = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.SUB_GOAL));
                        LinkedList<Record> records2 = getRecordList(createTime, false);
                        LinkedList<Record> subRecords = getSubRecordList(createTime, false);
                        careList.add(new SubPeriodicCare(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime,
                                goal2, punishment2, modified2, records2, periodUnit2, periodLength2, subGoal, subRecords));
                        break;
                    case Care.TIMELIMITED_PERIODIC:
                        int goal3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        int periodUnit3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_UNIT));
                        int periodLength3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_LENGTH));
                        LinkedList<Record> records3 = getRecordList(createTime, false);
                        TimePair timePair = getTimePair(createTime, false);
                        careList.add(new TimeLimitedPeriodicCare(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime,
                                goal3, punishment3, modified3, records3, periodUnit3, periodLength3, timePair));
                        break;
                    case Care.COMPLEX_PERIODIC:
                        int goal4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        int periodUnit4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_UNIT));
                        int periodLength4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_LENGTH));
                        int subGoal4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.SUB_GOAL));
                        LinkedList<Record> records4 = getRecordList(createTime, false);
                        LinkedList<Record> subRecords4 = getSubRecordList(createTime, false);
                        ArrayList<TimePair> timePairs = getTimePairs(createTime, false);
                        careList.add(new ComplexPeriodicCare(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime,
                                goal4, punishment4, modified4, records4, periodUnit4, periodLength4, subGoal4, subRecords4, timePairs));
                        break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).getBoolean("careItemAutoSort", true)) {
            sortCareList();
        }
    }

    public void sortCareList() {
        Collections.sort(careList, new Comparator<Care>() {
            @Override
            public int compare(Care lhs, Care rhs) {
                return lhs.getState() - rhs.getState();
            }
        });
    }

    public void loadHistoryCareList() {
        historyCareList = new LinkedList<>();
        Cursor cursor = db.rawQuery("select * from " + CareItemTable.HISTORY_NAME + " order by " + CareItemTable.Cols.ARCHIVED_TIME + " desc", null);
        if (cursor.moveToFirst()) {
            do {
                long createTime = cursor.getLong(cursor.getColumnIndex(CareItemTable.Cols.CREATE_TIME));
                int type = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.TYPE));
                String title = cursor.getString(cursor.getColumnIndex(CareItemTable.Cols.TITLE));
                String descriptionTitle = cursor.getString(cursor.getColumnIndex(CareItemTable.Cols.DESCRIPTION_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(CareItemTable.Cols.DESCRIPTION));
                long descriptionLastEditedTime = cursor.getLong(cursor.getColumnIndex(CareItemTable.Cols.DESCRIPTION_LAST_EDITED_TIME));
                long achievedTime = cursor.getLong(cursor.getColumnIndex(CareItemTable.Cols.ACHIEVED_TIME));
                long archivedTime = cursor.getLong(cursor.getColumnIndex(CareItemTable.Cols.ARCHIVED_TIME));
                switch (type) {
                    case Care.TEXT:
                        int color = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.COLOR));
                        historyCareList.add(new TextCare(title, descriptionTitle, description, descriptionLastEditedTime, 0, createTime, achievedTime, archivedTime, color));
                        break;
                    case Care.NONPERIODIC:
                        int goal = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        LinkedList<Record> records = getRecordList(createTime, true);
                        historyCareList.add(new NonperiodicCare(title, descriptionTitle, description, descriptionLastEditedTime, 0, createTime, achievedTime, archivedTime,
                                goal, punishment, modified, records));
                        break;
                    case Care.PERIODIC:
                        int goal1 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment1 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified1 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        int periodUnit = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_UNIT));
                        int periodLength = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_LENGTH));
                        LinkedList<Record> records1 = getRecordList(createTime, true);
                        historyCareList.add(new PeriodicCare(title, descriptionTitle, description, descriptionLastEditedTime, 0, createTime, achievedTime, archivedTime,
                                goal1, punishment1, modified1, records1, periodUnit, periodLength));
                        break;
                    case Care.SUB_PERIODIC:
                        int goal2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        int periodUnit2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_UNIT));
                        int periodLength2 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_LENGTH));
                        int subGoal = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.SUB_GOAL));
                        LinkedList<Record> records2 = getRecordList(createTime, true);
                        LinkedList<Record> subRecords = getSubRecordList(createTime, true);
                        historyCareList.add(new SubPeriodicCare(title, descriptionTitle, description, descriptionLastEditedTime, 0, createTime, achievedTime, archivedTime,
                                goal2, punishment2, modified2, records2, periodUnit2, periodLength2, subGoal, subRecords));
                        break;
                    case Care.TIMELIMITED_PERIODIC:
                        int goal3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        int periodUnit3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_UNIT));
                        int periodLength3 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_LENGTH));
                        LinkedList<Record> records3 = getRecordList(createTime, true);
                        TimePair timePair = getTimePair(createTime, true);
                        historyCareList.add(new TimeLimitedPeriodicCare(title, descriptionTitle, description, descriptionLastEditedTime, 0, createTime, achievedTime, archivedTime,
                                goal3, punishment3, modified3, records3, periodUnit3, periodLength3, timePair));
                        break;
                    case Care.COMPLEX_PERIODIC:
                        int goal4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.GOAL));
                        int punishment4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PUNISHMENT));
                        int modified4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.MODIFIED));
                        int periodUnit4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_UNIT));
                        int periodLength4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.PERIOD_LENGTH));
                        int subGoal4 = cursor.getInt(cursor.getColumnIndex(CareItemTable.Cols.SUB_GOAL));
                        LinkedList<Record> records4 = getRecordList(createTime, true);
                        LinkedList<Record> subRecords4 = getSubRecordList(createTime, true);
                        ArrayList<TimePair> timePairs = getTimePairs(createTime, true);
                        historyCareList.add(new ComplexPeriodicCare(title, descriptionTitle, description, descriptionLastEditedTime, 0, createTime, achievedTime, archivedTime,
                                goal4, punishment4, modified4, records4, periodUnit4, periodLength4, subGoal4, subRecords4, timePairs));
                        break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public List<Care> getCareList() {
        if (careList == null) {
            loadCareList();
        }
        return careList;
    }

    public List<Care> getHistoryCareList() {
        if (historyCareList == null) {
            loadHistoryCareList();
        }
        return historyCareList;
    }

    public int getMaxCareOrder() {
        return getCareList().size() == 0 ? 0 : ((LinkedList<Care>) careList).getLast().getOrder();
    }

    public boolean addCareItem(Care careItem) {
        try {
            ContentValues values = new ContentValues();
            values.put(CareItemTable.Cols.CREATE_TIME, careItem.getCreateTime());
            values.put(CareItemTable.Cols.TYPE, careItem.getType());
            values.put(CareItemTable.Cols.ORDER, careItem.getOrder());
            values.put(CareItemTable.Cols.TITLE, careItem.getTitle());
            values.put(CareItemTable.Cols.DESCRIPTION_TITLE, careItem.getDescriptionTitle());
            values.put(CareItemTable.Cols.DESCRIPTION, careItem.getDescription());
            values.put(CareItemTable.Cols.DESCRIPTION_LAST_EDITED_TIME, careItem.getDescriptionLastEditedTime());
            values.put(CareItemTable.Cols.ACHIEVED_TIME, careItem.getAchievedTime());
            switch (careItem.getType()) {
                case Care.TEXT:
                    values.put(CareItemTable.Cols.COLOR, ((TextCare) careItem).getColor());
                    break;
                case Care.NONPERIODIC:
                    NonperiodicCare care = (NonperiodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care.getModified());
                    addRecords(care.getCreateTime(), care.getRecords(), false);
                    break;
                case Care.PERIODIC:
                    PeriodicCare care1 = (PeriodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care1.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care1.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care1.getModified());
                    values.put(CareItemTable.Cols.PERIOD_UNIT, care1.getPeriodUnit());
                    values.put(CareItemTable.Cols.PERIOD_LENGTH, care1.getPeriodLength());
                    addRecords(care1.getCreateTime(), care1.getRecords(), false);
                    break;
                case Care.SUB_PERIODIC:
                    SubPeriodicCare care2 = (SubPeriodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care2.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care2.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care2.getModified());
                    values.put(CareItemTable.Cols.PERIOD_UNIT, care2.getPeriodUnit());
                    values.put(CareItemTable.Cols.PERIOD_LENGTH, care2.getPeriodLength());
                    values.put(CareItemTable.Cols.SUB_GOAL, care2.getSubGoal());
                    addRecords(care2.getCreateTime(), care2.getRecords(), false);
                    addSubRecords(care2.getCreateTime(), care2.getSubRecords(), false);
                    break;
                case Care.TIMELIMITED_PERIODIC:
                    TimeLimitedPeriodicCare care3 = (TimeLimitedPeriodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care3.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care3.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care3.getModified());
                    values.put(CareItemTable.Cols.PERIOD_UNIT, care3.getPeriodUnit());
                    values.put(CareItemTable.Cols.PERIOD_LENGTH, care3.getPeriodLength());
                    addRecords(care3.getCreateTime(), care3.getRecords(), false);
                    addTimePair(care3.getCreateTime(), care3.getTimePair(), false);
                    break;
                case Care.COMPLEX_PERIODIC:
                    ComplexPeriodicCare care4 = (ComplexPeriodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care4.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care4.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care4.getModified());
                    values.put(CareItemTable.Cols.PERIOD_UNIT, care4.getPeriodUnit());
                    values.put(CareItemTable.Cols.PERIOD_LENGTH, care4.getPeriodLength());
                    values.put(CareItemTable.Cols.SUB_GOAL, care4.getSubGoal());
                    addRecords(care4.getCreateTime(), care4.getRecords(), false);
                    addSubRecords(care4.getCreateTime(), care4.getSubRecords(), false);
                    addTimePairs(care4.getCreateTime(), care4.getTimePairs(), false);
                    break;
            }
            db.insert(CareItemTable.NAME, null, values);
            if (PreferenceManager.getDefaultSharedPreferences(AppManager.getContext()).getBoolean("careItemAutoSort", true)) {
                ListIterator<Care> iterator = careList.listIterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getState() >= careItem.getState()) {
                        iterator.previous();
                        iterator.add(careItem);
                        return true;
                    }
                }

            }
            careList.add(careItem);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean updateCareItem(Care careItem) {
        try {
            ContentValues values = new ContentValues();
            values.put(CareItemTable.Cols.ORDER, careItem.getOrder());
            values.put(CareItemTable.Cols.TITLE, careItem.getTitle());
            values.put(CareItemTable.Cols.DESCRIPTION_TITLE, careItem.getDescriptionTitle());
            values.put(CareItemTable.Cols.DESCRIPTION, careItem.getDescription());
            values.put(CareItemTable.Cols.DESCRIPTION_LAST_EDITED_TIME, careItem.getDescriptionLastEditedTime());
            values.put(CareItemTable.Cols.ACHIEVED_TIME, careItem.getAchievedTime());
            switch (careItem.getType()) {
                case Care.TEXT:
                    values.put(CareItemTable.Cols.COLOR, ((TextCare) careItem).getColor());
                    break;
                case Care.NONPERIODIC:
                case Care.PERIODIC:
                case Care.SUB_PERIODIC:
                case Care.TIMELIMITED_PERIODIC:
                case Care.COMPLEX_PERIODIC:
                    values.put(CareItemTable.Cols.MODIFIED, ((NonperiodicCare) careItem).getModified());
                    break;
            }
            db.update(CareItemTable.NAME, values, CareItemTable.Cols.CREATE_TIME + "=?", new String[]{String.valueOf(careItem.getCreateTime())});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteCareItem(int position) {
        try {
            Care care = getCareList().get(position);
            switch (care.getType()) {
                case Care.TEXT:
                    break;
                case Care.NONPERIODIC:
                    deleteRecords(care.getCreateTime(), false);
                    break;
                case Care.PERIODIC:
                    deleteRecords(care.getCreateTime(), false);
                    break;
                case Care.SUB_PERIODIC:
                    deleteRecords(care.getCreateTime(), false);
                    deleteSubRecords(care.getCreateTime(), false);
                    break;
                case Care.TIMELIMITED_PERIODIC:
                    deleteRecords(care.getCreateTime(), false);
                    deleteTimePairs(care.getCreateTime(), false);
                    break;
                case Care.COMPLEX_PERIODIC:
                    deleteRecords(care.getCreateTime(), false);
                    deleteSubRecords(care.getCreateTime(), false);
                    deleteTimePairs(care.getCreateTime(), false);
                    break;
            }
            db.delete(CareItemTable.NAME, CareItemTable.Cols.CREATE_TIME + "=" + care.getCreateTime(), null);
            careList.remove(position);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteHistoryCareItem(int position) {
        try {
            Care care = getHistoryCareList().get(position);
            switch (care.getType()) {
                case Care.TEXT:
                    break;
                case Care.NONPERIODIC:
                    deleteRecords(care.getCreateTime(), true);
                    break;
                case Care.PERIODIC:
                    deleteRecords(care.getCreateTime(), true);
                    break;
                case Care.SUB_PERIODIC:
                    deleteRecords(care.getCreateTime(), true);
                    deleteSubRecords(care.getCreateTime(), true);
                    break;
                case Care.TIMELIMITED_PERIODIC:
                    deleteRecords(care.getCreateTime(), true);
                    deleteTimePairs(care.getCreateTime(), true);
                    break;
            }
            db.delete(CareItemTable.HISTORY_NAME, CareItemTable.Cols.CREATE_TIME + "=" + care.getCreateTime(), null);
            historyCareList.remove(position);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean archiveCareItem(int position) {
        try {
            Care careItem = getCareList().get(position);
            careItem.setArchivedTime(System.currentTimeMillis());
            getHistoryCareList().add(0, careItem);
            ContentValues values = new ContentValues();
            values.put(CareItemTable.Cols.CREATE_TIME, careItem.getCreateTime());
            values.put(CareItemTable.Cols.TYPE, careItem.getType());
            values.put(CareItemTable.Cols.TITLE, careItem.getTitle());
            values.put(CareItemTable.Cols.DESCRIPTION_TITLE, careItem.getDescriptionTitle());
            values.put(CareItemTable.Cols.DESCRIPTION, careItem.getDescription());
            values.put(CareItemTable.Cols.DESCRIPTION_LAST_EDITED_TIME, careItem.getDescriptionLastEditedTime());
            values.put(CareItemTable.Cols.ACHIEVED_TIME, careItem.getAchievedTime());
            values.put(CareItemTable.Cols.ARCHIVED_TIME, careItem.getArchivedTime());
            switch (careItem.getType()) {
                case Care.TEXT:
                    values.put(CareItemTable.Cols.COLOR, ((TextCare) careItem).getColor());
                    break;
                case Care.NONPERIODIC:
                    NonperiodicCare care = (NonperiodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care.getModified());
                    addRecords(care.getCreateTime(), care.getRecords(), true);
                    deleteRecords(care.getCreateTime(), false);
                    break;
                case Care.PERIODIC:
                    PeriodicCare care1 = (PeriodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care1.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care1.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care1.getModified());
                    values.put(CareItemTable.Cols.PERIOD_UNIT, care1.getPeriodUnit());
                    values.put(CareItemTable.Cols.PERIOD_LENGTH, care1.getPeriodLength());
                    addRecords(care1.getCreateTime(), care1.getRecords(), true);
                    deleteRecords(care1.getCreateTime(), false);
                    break;
                case Care.SUB_PERIODIC:
                    SubPeriodicCare care2 = (SubPeriodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care2.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care2.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care2.getModified());
                    values.put(CareItemTable.Cols.PERIOD_UNIT, care2.getPeriodUnit());
                    values.put(CareItemTable.Cols.PERIOD_LENGTH, care2.getPeriodLength());
                    values.put(CareItemTable.Cols.SUB_GOAL, care2.getSubGoal());
                    addRecords(care2.getCreateTime(), care2.getRecords(), true);
                    addSubRecords(care2.getCreateTime(), care2.getSubRecords(), true);
                    deleteRecords(care2.getCreateTime(), false);
                    deleteSubRecords(care2.getCreateTime(), false);
                    break;
                case Care.TIMELIMITED_PERIODIC:
                    TimeLimitedPeriodicCare care3 = (TimeLimitedPeriodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care3.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care3.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care3.getModified());
                    values.put(CareItemTable.Cols.PERIOD_UNIT, care3.getPeriodUnit());
                    values.put(CareItemTable.Cols.PERIOD_LENGTH, care3.getPeriodLength());
                    addRecords(care3.getCreateTime(), care3.getRecords(), true);
                    deleteRecords(care3.getCreateTime(), false);
                    addTimePair(care3.getCreateTime(), care3.getTimePair(), true);
                    deleteTimePairs(care3.getCreateTime(), false);
                    break;
                case Care.COMPLEX_PERIODIC:
                    ComplexPeriodicCare care4 = (ComplexPeriodicCare) careItem;
                    values.put(CareItemTable.Cols.GOAL, care4.getGoal());
                    values.put(CareItemTable.Cols.PUNISHMENT, care4.getPunishment());
                    values.put(CareItemTable.Cols.MODIFIED, care4.getModified());
                    values.put(CareItemTable.Cols.PERIOD_UNIT, care4.getPeriodUnit());
                    values.put(CareItemTable.Cols.PERIOD_LENGTH, care4.getPeriodLength());
                    values.put(CareItemTable.Cols.SUB_GOAL, care4.getSubGoal());
                    addRecords(care4.getCreateTime(), care4.getRecords(), true);
                    addSubRecords(care4.getCreateTime(), care4.getSubRecords(), true);
                    addTimePairs(care4.getCreateTime(), care4.getTimePairs(), true);
                    deleteRecords(care4.getCreateTime(), false);
                    deleteSubRecords(care4.getCreateTime(), false);
                    deleteRecords(care4.getCreateTime(), false);
                    break;
            }
            db.insert(CareItemTable.HISTORY_NAME, null, values);
            deleteCareItem(position);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean unarchiveCareItem(int position) {
        try {
            Care careItem = getHistoryCareList().get(position);
            careItem.setArchivedTime(0L);
            careItem.setOrder(getMaxCareOrder() + 1);
            addCareItem(careItem);
            deleteHistoryCareItem(position);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void swapCareItem(int fromPosition, int toPosition) {
        Collections.swap(careList, fromPosition, toPosition);
        ListIterator<Care> iterator;
        int order;
        if (fromPosition < toPosition) {
            iterator = careList.listIterator(fromPosition);
            order = iterator.next().decreaseOrder();
            iterator.next().setOrder(order);
        } else {
            iterator = careList.listIterator(toPosition);
            order = iterator.next().decreaseOrder();
            iterator.next().setOrder(order);
        }
        updateCareItem(iterator.previous());
        updateCareItem(iterator.previous());
    }

    public long getNextRefreshTime() {
        GregorianCalendar calendar = new GregorianCalendar();
        long currentTime = calendar.getTimeInMillis();
        calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        long today = calendar.getTimeInMillis(), nextRefreshTime = today + 86400000, temp = 0;
        for (Care care : getCareList()) {
            if (care.getType() == Care.TIMELIMITED_PERIODIC) {
                temp = ((TimeLimitedPeriodicCare) care).getTimePair().startMinutes * 60000 + today;
                if (temp > currentTime && temp < nextRefreshTime) {
                    nextRefreshTime = temp;
                }
                temp = ((TimeLimitedPeriodicCare) care).getTimePair().endMinutes * 60000 + 60000 + today;
                if (temp > currentTime && temp < nextRefreshTime) {
                    nextRefreshTime = temp;
                }
            } else if (care.getType() == Care.COMPLEX_PERIODIC) {
                for (TimePair timePair : ((ComplexPeriodicCare) care).getTimePairs()) {
                    temp = timePair.startMinutes * 60000 + today;
                    if (temp > currentTime && temp < nextRefreshTime) {
                        nextRefreshTime = temp;
                    }
                    temp = timePair.endMinutes * 60000 + 60000 + today;
                    if (temp > currentTime && temp < nextRefreshTime) {
                        nextRefreshTime = temp;
                    }
                }
            }
        }
        return nextRefreshTime;
    }

    public void loadProblemList() {
    }

    public boolean addProblemItem(Problem problemItem) {
        return false;
    }

    public boolean deleteProblemItem(Problem problemItem) {
        return false;
    }

    public List<Problem> getProblemList() {
        return problemList;
    }

    public ArrayList<File> getBackupList() {
        File backupDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + APP_DIRECTORY + "/Backup");
        File[] files = backupDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".backup");
            }
        });
        ArrayList<File> backups = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                backups.add(file);
            }
            Collections.sort(backups, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return (rhs.lastModified() - lhs.lastModified()) > 0 ? 1 : -1;
                }
            });
        }
        return backups;
    }

    public File backup() {
        try {
            //if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) return false;
            File backupDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + APP_DIRECTORY + "/Backup");
            if (!backupDirectory.exists()) {
                backupDirectory.mkdir();
            }
            GregorianCalendar calendar = new GregorianCalendar();
            String fileName = backupDirectory.getAbsolutePath() + String.format("/%d%02d%02d%02d%02d%02d.backup", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            writeZipFile("/data/data/com.onecivilization.MyOptimize/databases/MyOptimize.db", zipOutputStream);
            writeZipFile("/data/data/com.onecivilization.MyOptimize/databases/MyOptimize.db-journal", zipOutputStream);
            writeZipFile("/data/data/com.onecivilization.MyOptimize/shared_prefs/com.onecivilization.MyOptimize_preferences.xml", zipOutputStream);
            zipOutputStream.close();
            return new File(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void recover(File backup) {
        try {
            BufferedOutputStream bufferedOutputStream;
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(backup)));
            ZipEntry entry;
            byte[] buffer = new byte[10240];
            int bufferLength;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().contains(".db")) {
                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("/data/data/com.onecivilization.MyOptimize/databases/" + entry.getName()));
                } else {
                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("/data/data/com.onecivilization.MyOptimize/shared_prefs/" + entry.getName()));
                }
                while ((bufferLength = zipInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, bufferLength);
                }
                bufferedOutputStream.close();
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeZipFile(String fileName, ZipOutputStream zipOutputStream) {
        try {
            String[] tokens = fileName.split("/");
            ZipEntry zipEntry = new ZipEntry(tokens[tokens.length - 1]);
            zipOutputStream.putNextEntry(zipEntry);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName));
            byte[] buffer = new byte[10240];
            int bufferLength = 0;
            while ((bufferLength = bufferedInputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, bufferLength);
            }
            zipOutputStream.closeEntry();
            bufferedInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
