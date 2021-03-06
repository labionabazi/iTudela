package ch.bbcag.itudela;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import ch.bbcag.itudela.db.HistoryContract;
import ch.bbcag.itudela.db.HistoryDbHelper;
import ch.bbcag.itudela.helper.YoutubeConnector;
import ch.bbcag.itudela.model.VideoItem;

public class NowPlayingActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    VideoItem latestVideo;

    private TextView mTextMessage;
    private YouTubePlayerView playerView;
    private String description, title, thumbnailURL, id;
    private NavigationListener mOnNavigationItemSelectedListener;
    private HistoryDbHelper hDbHelper;

//    public static interface android.media.AudioManager.OnAudioFocusChangeListener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        mOnNavigationItemSelectedListener = NavigationListener.getInstance(getApplicationContext());

        try{
        mTextMessage = (TextView) findViewById(R.id.message);

        hDbHelper = new HistoryDbHelper(getApplicationContext());


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_music);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        playerView = (YouTubePlayerView) findViewById(R.id.player_view);

        latestVideo = new VideoItem();

        if (getIntent().getStringExtra("VIDEO_ID") != null) {

            latestVideo = new VideoItem();

            latestVideo.setTitle(getIntent().getStringExtra("TITLE"));
            latestVideo.setDescription(getIntent().getStringExtra("DESCRIPTION"));
            latestVideo.setThumbnailURL(getIntent().getStringExtra("THUMBNAILURL"));
            latestVideo.setId(getIntent().getStringExtra("VIDEO_ID"));

            description = getIntent().getStringExtra("DESCRIPTION");
            thumbnailURL = getIntent().getStringExtra("THUMBNAILURL");
            title = getIntent().getStringExtra("TITLE");
            id = getIntent().getStringExtra("VIDEO_ID");

            SharedPreferences preferences = getSharedPreferences("lastVideoWatched", MODE_PRIVATE);
            preferences.edit().putString("id", id).apply();
        }else {
            id = getSharedPreferences("lastVideoWatched", MODE_PRIVATE).getString("id", null);
            
            VideoItem latestvideo = getVideoByID(getApplicationContext() , id);
            description = latestvideo.getDescription();
            thumbnailURL = latestvideo.getThumbnailURL();
            title = latestvideo.getTitle();
        }

        insertIntoHistory(id, title, thumbnailURL, description);

        FrameLayout titelFrame = (FrameLayout) findViewById(R.id.titel);
        View to_add = getLayoutInflater().inflate(R.layout.video_item, titelFrame, false);

        ImageView videoThumbnail = (ImageView) to_add.findViewById(R.id.video_thumbnail);
        TextView videoTitle = (TextView) to_add.findViewById(R.id.video_title);
        TextView videoDescription = (TextView) to_add.findViewById(R.id.video_description);

        Picasso.with(getApplicationContext()).load(thumbnailURL).into(videoThumbnail);
        videoTitle.setText(title);
        videoDescription.setText(description);
        titelFrame.addView(to_add);

        playerView.initialize(YoutubeConnector.KEY, this);
    }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("Fehlermeldung");

            builder
                    .setMessage("Wähle bitte zuerst einen Titel aus!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
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

            if (getIntent().getStringExtra("VIDEO_ID") != null) {

                player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
                player.loadVideo(getIntent().getStringExtra("VIDEO_ID"));
            }
            else
            {
                player.cueVideo(id);
                player.loadVideo(id);
            }
        }
    }


    public VideoItem getVideoByID(Context context, String video_id){

        HistoryDbHelper hDbHelper = new HistoryDbHelper(context);

        SQLiteDatabase db = hDbHelper.getReadableDatabase();

        String[] projection = {
                HistoryContract.HistoryEntry._ID,
                HistoryContract.HistoryEntry.COLUMN_NAME_VIDEO_ID,
                HistoryContract.HistoryEntry.COLUMN_NAME_URL,
                HistoryContract.HistoryEntry.COLUMN_NAME_DESCRIPTION,
                HistoryContract.HistoryEntry.COLUMN_NAME_TITLE
        };

        String selection = HistoryContract.HistoryEntry.COLUMN_NAME_VIDEO_ID + " = ?";
        String[] selectionArgs = { video_id };


        String sortOrder =
                HistoryContract.HistoryEntry.COLUMN_NAME_DATE + " DESC";

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

            if(!videoItem.getId().equals(id)){
                return null;
            }else{
                return videoItem;
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void insertIntoHistory(String id, String title, String thumbnailURL, String description) {

        if (getVideoByID(getApplicationContext(), id) == null) {

            SQLiteDatabase db = hDbHelper.getWritableDatabase();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_URL, thumbnailURL);
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_VIDEO_ID, id);
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_DESCRIPTION, description);
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_TITLE, title);
            values.put(HistoryContract.HistoryEntry.COLUMN_NAME_DATE,dateFormat.format(date));

            long newRowId = db.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, values);
        }
    }
}
