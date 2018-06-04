package ch.bbcag.itudela.db;

import android.provider.BaseColumns;

public class HistoryContract {

    private HistoryContract(){}

    public static class HistoryEntry implements BaseColumns{
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_VIDEO_ID = "video_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

}
