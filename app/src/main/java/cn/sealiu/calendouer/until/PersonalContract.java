package cn.sealiu.calendouer.until;

import android.provider.BaseColumns;

/**
 * Created by liuyang
 * on 2017/1/15.
 */

public final class PersonalContract {
    private PersonalContract() {
    }

    public static class PersonalEntry implements BaseColumns {
        public static final String TABLE_NAME = "personal";
        public static final String COLUMN_NAME_ID = "id"; // 由哪个影片添加的喜欢（影片id）
        public static final String COLUMN_NAME_KEY = "key"; // 类型：导演/演员celebrity/影片类型tag
        public static final String COLUMN_NAME_VALUE = "value"; // 影人名字/类型名称
        public static final String COLUMN_NAME_LEVEL = "level"; //<key-value>对应的值，表示程度
        public static final String COLUMN_NAME_DATETIME = "datetime"; // 添加时间
        public static final String COLUMN_NAME_ALT = "alt"; // 由哪个影片添加的喜欢（影片链接）
    }
}
