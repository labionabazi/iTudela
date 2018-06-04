package ch.bbcag.itudela;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import ch.bbcag.itudela.db.HistoryContract;
import ch.bbcag.itudela.db.HistoryDbHelper;
import ch.bbcag.itudela.helper.YoutubeConnector;
import ch.bbcag.itudela.model.VideoItem;

import static java.security.AccessController.getContext;

public class NowPlayingActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    VideoItem latestVideo;

    private TextView mTextMessage;
    private YouTubePlayerView playerView;
    private String description, title, thumbnailURL, id;

    private Intent search, history, nowplaying;

    private NavigationListener mOnNavigationItemSelectedListener;

    private HistoryDbHelper hDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        mOnNavigationItemSelectedListener = new NavigationListener(search, history, nowplaying, getApplicationContext());

        hDbHelper = new HistoryDbHelper(getApplicationContext());

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_music);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        playerView = (YouTubePlayerView) findViewById(R.id.player_view);

        latestVideo = new VideoItem();
        latestVideo.setTitle(getIntent().getStringExtra("TITLE"));
        latestVideo.setDescription(getIntent().getStringExtra("DESCRIPTION"));
        latestVideo.setThumbnailURL(getIntent().getStringExtra("THUMBNAILURL"));
        latestVideo.setId(getIntent().getStringExtra("VIDEO_ID"));

        if(getIntent().getStringExtra("VIDEO_ID") != null) {

            latestVideo = new VideoItem();

            latestVideo.setTitle(getIntent().getStringExtra("TITLE"));
            latestVideo.setDescription(getIntent().getStringExtra("DESCRIPTION"));
            latestVideo.setThumbnailURL(getIntent().getStringExtra("THUMBNAILURL"));
            latestVideo.setId(getIntent().getStringExtra("VIDEO_ID"));

            description = getIntent().getStringExtra("DESCRIPTION");
            thumbnailURL = getIntent().getStringExtra("THUMBNAILURL");
            title = getIntent().getStringExtra("TITLE");
            id = getIntent().getStringExtra("VIDEO_ID");
        }else {

            description = latestVideo.getDescription();
            thumbnailURL = latestVideo.getThumbnailURL();
            title = latestVideo.getTitle();
            id = latestVideo.getId();


        }





        insertIntoHistory(id, title, thumbnailURL, description);

        playerView.initialize(YoutubeConnector.KEY, this);
    }

    @Override
    protected void onDestroy() {
        hDbHelper.close();
        super.onDestroy();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean restored) {
        if (!restored) {

            if(getIntent().getStringExtra("VIDEO_ID") != null) {

                player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
                player.loadVideo(getIntent().getStringExtra("VIDEO_ID"));
            }
            else
            {
                player.cueVideo(latestVideo.getId());
                player.loadVideo(latestVideo.getId());
            }
        }
    }

    public VideoItem getVideoByThumbURL(Context context, String url){
        HistoryDbHelper hDbHelper = new HistoryDbHelper(context);

        SQLiteDatabase db = hDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                HistoryContract.HistoryEntry.COLUMN_NAME_VIDEO_ID,
                HistoryContract.HistoryEntry.COLUMN_NAME_URL,
                HistoryContract.HistoryEntry.COLUMN_NAME_DESCRIPTION,
                HistoryContract.HistoryEntry.COLUMN_NAME_TITLE
        };

        String selection = HistoryContract.HistoryEntry.COLUMN_NAME_URL + " = ?";
        String[] selectionArgs = { url };

        String sortOrder =
                BaseColumns._ID + " DESC";

        Cursor cursor = db.query(
                HistoryContract.HistoryEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        try {
            VideoItem videoItem = new VideoItem();
            while (cursor.moveToNext()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(BaseColumns._ID));
                videoItem.setDb_id(itemId);
                String itemUrl = cursor.getString(
                        cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_NAME_URL));
                videoItem.setThumbnailURL(itemUrl);
                String itemVideoID = cursor.getString(
                        cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_NAME_VIDEO_ID));
                videoItem.setId(itemVideoID);
                String itemTitle = cursor.getString(
                        cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_NAME_TITLE));
                videoItem.setTitle(itemTitle);
                String itemDescription = cursor.getString(
                        cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_NAME_DESCRIPTION));
                videoItem.setDescription(itemDescription);

            }
            cursor.close();

            return videoItem;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void insertIntoHistory(String id, String title, String thumbnailURL, String description) {

        if (getVideoByThumbURL(getApplicationContext(), thumbnailURL) == null) {

            SQLiteDatabase db = hDbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_URL, thumbnailURL);
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_VIDEO_ID, id);
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_DESCRIPTION, description);
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_TITLE, title);


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, values);
        }
    }
}
