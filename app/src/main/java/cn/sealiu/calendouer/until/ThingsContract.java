package cn.sealiu.calendouer.until;

import android.provider.BaseColumns;

/**
 * Created by liuyang
 * on 2017/1/15.
 */

public final class ThingsContract {
    private ThingsContract() {
    }

    public static class ThingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "things";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATETIME = "datetime";
        public static final String COLUMN_NAME_NOTIFICATION_DATETIME = "notification_datetime";
        public static final String COLUMN_NAME_TIME_ADVANCE = "time_advance";
        public static final String COLUMN_NAME_DONE = "done";
    }
}
