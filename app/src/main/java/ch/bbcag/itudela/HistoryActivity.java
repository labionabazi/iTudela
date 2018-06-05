package ch.bbcag.itudela;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.itudela.db.HistoryContract;
import ch.bbcag.itudela.db.HistoryDbHelper;
import ch.bbcag.itudela.model.VideoItem;

public class HistoryActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private Intent search, history, nowplaying;

    private ListView videosReload;

    private List<VideoItem> VideoList;

    private Handler handler;

    private HistoryDbHelper hDbHelper;

    private NavigationListener mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        hDbHelper = new HistoryDbHelper(getApplicationContext());

        mOnNavigationItemSelectedListener = new NavigationListener(search, history, nowplaying, getApplicationContext());

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_history);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //searchInput = (EditText)findViewById(R.id.search_input);
        videosReload = (ListView) findViewById(R.id.videos_reload);

        VideoList = getVideoFromDB();

        updateVideosFound();

//        loadYouTubeVideo();

        addClickListener();

        handler = new Handler();
    }

    @Override
    protected void onDestroy() {
        hDbHelper.close();
        super.onDestroy();
    }


    private void updateVideosFound() {
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, VideoList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                VideoItem searchResult = VideoList.get(position);

                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosReload.setAdapter(adapter);
    }

    private void loadYouTubeVideo() {
        new Thread() {
                public void run() {
                updateVideosFound();
            }
        }.start();
    }

    private void addClickListener() {
        videosReload.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent intent = new Intent(getApplicationContext(), NowPlayingActivity.class);
                intent.putExtra("VIDEO_ID", VideoList.get(pos).getId());
                intent.putExtra("DESCRIPTION", VideoList.get(pos).getDescription());
                intent.putExtra("THUMBNAILURL", VideoList.get(pos).getThumbnailURL());
                startActivity(intent);
            }

        });
    }

    public void setApplicationStatus(Intent history, Intent search, Intent nowplaying){
        this.history = history;
        this.search = search;
        this.nowplaying = nowplaying;
    }


    public List<VideoItem> getVideoFromDB() {

        SQLiteDatabase db = hDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                HistoryContract.HistoryEntry.COLUMN_NAME_VIDEO_ID,
                HistoryContract.HistoryEntry.COLUMN_NAME_URL,
                HistoryContract.HistoryEntry.COLUMN_NAME_DESCRIPTION,
                HistoryContract.HistoryEntry.COLUMN_NAME_TITLE
        };

        String sortOrder =
                BaseColumns._ID + " DESC";

        Cursor cursor = db.query(
                HistoryContract.HistoryEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<VideoItem> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            VideoItem videoItem = new VideoItem();
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

            items.add(videoItem);
        }
        cursor.close();

        return items;
    }
}
