package cn.sealiu.calendouer.until;

import android.provider.BaseColumns;

/**
 * Created by liuyang
 * on 2017/1/15.
 */

public final class SimilarContract {
    private SimilarContract() {
    }

    public static class SimilarEntry implements BaseColumns {
        public static final String TABLE_NAME = "similar";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGES = "images";
        public static final String COLUMN_NAME_AVERAGE = "average";
        public static final String COLUMN_NAME_STARS = "stars";
        public static final String COLUMN_NAME_ALT = "alt";
        public static final String COLUMN_NAME_YEAR = "year";
    }
}
