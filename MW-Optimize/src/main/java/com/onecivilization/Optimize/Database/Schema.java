package com.onecivilization.Optimize.Database;

/**
 * Created by CGZ on 2016/7/10.
 */
public class Schema {

    public static final String INTEGER = " integer";
    public static final String REAL = " real";
    public static final String TEXT = " text";
    public static final String BLOB = " blob";
    public static final String CREATE_TABLE = "create table ";
    public static final String PRIMARY_KEY = " primary key";
    public static final String AUTOINCREMENT = " autoincrement";
    public static final String NOT_NULL = " not null";
    public static final String PRIMARY_KEY_ = "primary key";
    public static final String FOREIGN_KEY = "foreign key(";
    public static final String REFERENCES = ") references ";

    public static final class RecordTable {

        public static final String NAME = "records";
        public static final String HISTORY_NAME = "records_history";
        public static final String CREATE_STATEMENT = CREATE_TABLE + NAME + "(" +
                Cols.CARE_ITEM_ID + INTEGER + "," +
                Cols.TIME + INTEGER + "," +
                Cols.TAG + INTEGER + "," +
                PRIMARY_KEY_ + "(" + Cols.CARE_ITEM_ID + "," + Cols.TIME + "," + Cols.TAG + ")" + "," +
                FOREIGN_KEY + Cols.CARE_ITEM_ID + REFERENCES + CareItemTable.NAME + "(" + CareItemTable.Cols.CREATE_TIME + ")" +
                ")";
        public static final String HISTORY_CREATE_STATEMENT = CREATE_TABLE + HISTORY_NAME + "(" +
                Cols.CARE_ITEM_ID + INTEGER + "," +
                Cols.TIME + INTEGER + "," +
                Cols.TAG + INTEGER + "," +
                PRIMARY_KEY_ + "(" + Cols.CARE_ITEM_ID + "," + Cols.TIME + "," + Cols.TAG + ")" + "," +
                FOREIGN_KEY + Cols.CARE_ITEM_ID + REFERENCES + CareItemTable.HISTORY_NAME + "(" + CareItemTable.Cols.CREATE_TIME + ")" +
                ")";

        public static final class Cols {
            public static final String CARE_ITEM_ID = "careItemID";
            public static final String TIME = "time";
            public static final String TAG = "tag";
        }
    }

    public static final class TimePairTable {

        public static final String NAME = "timePairs";
        public static final String HISTORY_NAME = "timePairs_history";
        public static final String CREATE_STATEMENT = CREATE_TABLE + NAME + "(" +
                Cols.CARE_ITEM_ID + INTEGER + "," +
                Cols.START_MINUTES + INTEGER + "," +
                Cols.END_MINUTES + INTEGER + "," +
                PRIMARY_KEY_ + "(" + Cols.CARE_ITEM_ID + "," + Cols.START_MINUTES + "," + Cols.END_MINUTES + ")" + "," +
                FOREIGN_KEY + Cols.CARE_ITEM_ID + REFERENCES + CareItemTable.NAME + "(" + CareItemTable.Cols.CREATE_TIME + ")" +
                ")";
        public static final String HISTORY_CREATE_STATEMENT = CREATE_TABLE + HISTORY_NAME + "(" +
                Cols.CARE_ITEM_ID + INTEGER + "," +
                Cols.START_MINUTES + INTEGER + "," +
                Cols.END_MINUTES + INTEGER + "," +
                PRIMARY_KEY_ + "(" + Cols.CARE_ITEM_ID + "," + Cols.START_MINUTES + "," + Cols.END_MINUTES + ")" + "," +
                FOREIGN_KEY + Cols.CARE_ITEM_ID + REFERENCES + CareItemTable.HISTORY_NAME + "(" + CareItemTable.Cols.CREATE_TIME + ")" +
                ")";

        public static final class Cols {
            public static final String CARE_ITEM_ID = "careItemID";
            public static final String START_MINUTES = "startMinutes";
            public static final String END_MINUTES = "endMinutes";
        }
    }

    public static final class CareItemTable {

        public static final String NAME = "cares";
        public static final String HISTORY_NAME = "cares_history";
        public static final String CREATE_STATEMENT = CREATE_TABLE + NAME + "(" +
                Cols.CREATE_TIME + INTEGER + PRIMARY_KEY + "," +
                Cols.TYPE + INTEGER + NOT_NULL + "," +
                Cols.STATE + INTEGER + "," +
                Cols.ORDER + INTEGER + "," +
                Cols.TITLE + TEXT + NOT_NULL + "," +
                Cols.DESCRIPTION_TITLE + TEXT + "," +
                Cols.DESCRIPTION + TEXT + "," +
                Cols.DESCRIPTION_LAST_EDITED_TIME + INTEGER + "," +
                Cols.ACHIEVED_TIME + INTEGER + "," +
                Cols.COLOR + INTEGER + "," +
                Cols.GOAL + INTEGER + "," +
                Cols.PROGRESS + INTEGER + "," +
                Cols.GIVEN_UP + INTEGER + "," +
                Cols.PERIOD_UNIT + INTEGER + "," +
                Cols.PERIOD_LENGTH + INTEGER + "," +
                Cols.PUNISHMENT + INTEGER + "," +
                Cols.SUB_GOAL + INTEGER + "," +
                Cols.SUB_PROGRESS + INTEGER +
                ")";
        public static final String HISTORY_CREATE_STATEMENT = CREATE_TABLE + HISTORY_NAME + "(" +
                Cols.CREATE_TIME + INTEGER + PRIMARY_KEY + "," +
                Cols.TYPE + INTEGER + NOT_NULL + "," +
                Cols.TITLE + TEXT + NOT_NULL + "," +
                Cols.DESCRIPTION_TITLE + TEXT + "," +
                Cols.DESCRIPTION + TEXT + "," +
                Cols.DESCRIPTION_LAST_EDITED_TIME + INTEGER + "," +
                Cols.ACHIEVED_TIME + INTEGER + "," +
                Cols.ARCHIVED_TIME + INTEGER + "," +
                Cols.CATEGORY + TEXT + "," +
                Cols.COLOR + INTEGER + "," +
                Cols.GOAL + INTEGER + "," +
                Cols.PROGRESS + INTEGER + "," +
                Cols.GIVEN_UP + INTEGER + "," +
                Cols.PERIOD_UNIT + INTEGER + "," +
                Cols.PERIOD_LENGTH + INTEGER + "," +
                Cols.PUNISHMENT + INTEGER + "," +
                Cols.SUB_GOAL + INTEGER + "," +
                Cols.SUB_PROGRESS + INTEGER +
                ")";

        public static final class Cols {
            public static final String TITLE = "title";
            public static final String DESCRIPTION_TITLE = "descriptionTitle";
            public static final String DESCRIPTION = "description";
            public static final String DESCRIPTION_LAST_EDITED_TIME = "descriptionLastEditedTime";
            public static final String TYPE = "type";
            public static final String STATE = "state";
            public static final String ORDER = "'order'";
            public static final String ACHIEVED_TIME = "achievedTime";
            public static final String CREATE_TIME = "createTime";
            public static final String COLOR = "color";
            public static final String GOAL = "goal";
            public static final String PROGRESS = "progress";
            public static final String GIVEN_UP = "givenUp";
            public static final String PERIOD_UNIT = "periodUnit";
            public static final String PERIOD_LENGTH = "periodLength";
            public static final String PUNISHMENT = "punishment";
            public static final String SUB_GOAL = "subGoal";
            public static final String SUB_PROGRESS = "subProgress";

            public static final String CATEGORY = "category";
            public static final String ARCHIVED_TIME = "archivedTime";
        }
    }

    public static final class ProblemItemTable {

        public static final String NAME = "problems";
        public static final String HISTORY_NAME = "problems_history";
        public static final String CREATE_STATEMENT = CREATE_TABLE + NAME + "(" +
                Cols.CREATE_TIME + INTEGER + PRIMARY_KEY + "," +
                Cols.TITLE + TEXT + NOT_NULL + "," +
                Cols.DESCRIPTION + TEXT + "," +
                Cols.CAUSE + TEXT + "," +
                Cols.SOLUTION + TEXT + "," +
                Cols.PRIORITY + INTEGER + "," +
                Cols.ORDER + INTEGER + "," +
                Cols.SOLVED_TIME + INTEGER +
                ")";
        public static final String HISTORY_CREATE_STATEMENT = CREATE_TABLE + HISTORY_NAME + "(" +
                Cols.CREATE_TIME + INTEGER + PRIMARY_KEY + "," +
                Cols.TITLE + TEXT + NOT_NULL + "," +
                Cols.DESCRIPTION + TEXT + "," +
                Cols.CAUSE + TEXT + "," +
                Cols.SOLUTION + TEXT + "," +
                Cols.PRIORITY + INTEGER + "," +
                Cols.SOLVED_TIME + INTEGER + "," +
                Cols.ARCHIVED_TIME + INTEGER + "," +
                Cols.CATEGORY + TEXT +
                ")";

        public static final class Cols {
            public static final String TITLE = "title";
            public static final String DESCRIPTION = "description";
            public static final String CAUSE = "cause";
            public static final String SOLUTION = "solution";
            public static final String PRIORITY = "priority";
            public static final String ORDER = "'order'";
            public static final String CREATE_TIME = "createTime";
            public static final String SOLVED_TIME = "solvedTime";

            public static final String CATEGORY = "category";
            public static final String ARCHIVED_TIME = "archivedTime";
        }
    }

}
