package com.onecivilization.Optimize.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onecivilization.Optimize.Database.Schema.CareItemTable;
import com.onecivilization.Optimize.Database.Schema.RecordTable;
import com.onecivilization.Optimize.Model.Care;
import com.onecivilization.Optimize.Model.NonperiodicCare;
import com.onecivilization.Optimize.Model.Problem;
import com.onecivilization.Optimize.Model.Record;
import com.onecivilization.Optimize.Model.TextCare;

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

    public synchronized static DataManager getInstance(Context context) {
        if (dataManager == null) {
            dataManager = new DataManager(context);
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
                        historyCareList.add(new NonperiodicCare(title, descriptionTitle, description, descriptionLastEditedTime, 0, createTime, achievedTime,
                                goal, punishment, modified, records));
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
            ((LinkedList) getHistoryCareList()).add(0, careItem);
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
