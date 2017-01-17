package cn.sealiu.calendouer.until;

import android.provider.BaseColumns;

/**
 * Created by liuyang
 * on 2017/1/15.
 */

public final class MovieContract {
    private MovieContract() {
    }

    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_NAME_IMAGES = "images";
        public static final String COLUMN_NAME_AVERAGE = "average";
        public static final String COLUMN_NAME_STARS = "stars";
        public static final String COLUMN_NAME_ALT = "alt";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_SUMMARY = "summary";
    }
}
