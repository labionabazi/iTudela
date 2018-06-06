package ch.bbcag.itudela;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

        mOnNavigationItemSelectedListener = NavigationListener.getInstance(getApplicationContext());

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_history);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        videosReload = (ListView) findViewById(R.id.videos_reload);

        VideoList = getVideoFromDB();

        updateVideosFound();

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
                ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.video_title);
                TextView description = (TextView)convertView.findViewById(R.id.video_description);

                VideoItem searchResult = VideoList.get(position);

                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosReload.setAdapter(adapter);
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


    public List<VideoItem> getVideoFromDB() {

        SQLiteDatabase db = hDbHelper.getReadableDatabase();

        String queryString =
                "SELECT video_id,description,title,url  FROM history " +
                        "ORDER BY date desc";
        Cursor cursor = db.rawQuery(queryString,null);

        List<VideoItem> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            VideoItem videoItem = new VideoItem();
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
