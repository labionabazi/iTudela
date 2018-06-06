package ch.bbcag.itudela.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "History.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HistoryContract.HistoryEntry.TABLE_NAME + " (" +
                    HistoryContract.HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    HistoryContract.HistoryEntry.COLUMN_NAME_VIDEO_ID + " TEXT," +
                    HistoryContract.HistoryEntry.COLUMN_NAME_TITLE + " TEXT," +
                    HistoryContract.HistoryEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    HistoryContract.HistoryEntry.COLUMN_NAME_DATE + " DATE," +
                    HistoryContract.HistoryEntry.COLUMN_NAME_URL + " TEXT)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HistoryContract.HistoryEntry.TABLE_NAME;

    public HistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }
}
