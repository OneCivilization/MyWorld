package com.onecivilization.MyOptimize.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onecivilization.MyOptimize.Database.Schema.CareItemTable;
import com.onecivilization.MyOptimize.Database.Schema.RecordTable;
import com.onecivilization.MyOptimize.Model.Care;
import com.onecivilization.MyOptimize.Model.NonperiodicCare;
import com.onecivilization.MyOptimize.Model.PeriodicCare;
import com.onecivilization.MyOptimize.Model.Problem;
import com.onecivilization.MyOptimize.Model.Record;
import com.onecivilization.MyOptimize.Model.SubPeriodicCare;
import com.onecivilization.MyOptimize.Model.TextCare;
import com.onecivilization.MyOptimize.Util.AppManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by CGZ on 2016/7/10.
 */
public class DataManager {

    private static DataManager dataManager;
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
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
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
            }
            db.insert(CareItemTable.NAME, null, values);
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

    public void setProblemList(List<Problem> ProblemList) {
        problemList = ProblemList;
    }
}
