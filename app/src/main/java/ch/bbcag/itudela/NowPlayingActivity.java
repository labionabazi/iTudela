package ch.bbcag.itudela;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import ch.bbcag.itudela.helper.YoutubeConnector;
import ch.bbcag.itudela.model.VideoItem;

public class NowPlayingActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private TextView mTextMessage;
    private YouTubePlayerView playerView;
    private String description, title, thumbnailURL;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent search = new Intent(getApplicationContext(), SearchActivity.class);
                    search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(search);
                    return true;
                case R.id.navigation_history:
                    Intent history = new Intent(getApplicationContext(), HistoryActivity.class);
                    history.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(history);
                    return true;
                case R.id.navigation_music:
                    Intent nowplaying = new Intent(getApplicationContext(), NowPlayingActivity.class);
                    nowplaying.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(nowplaying);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        mTextMessage = (TextView)findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_music);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        playerView = (YouTubePlayerView)findViewById(R.id.player_view);

        thumbnailURL = getIntent().getStringExtra("THUMBNAILURL");
        description = getIntent().getStringExtra("DESCRIPTION");
        title = getIntent().getStringExtra("VideoTitle");

        FrameLayout titelFrame = (FrameLayout) findViewById(R.id.titel);
        View to_add = getLayoutInflater().inflate(R.layout.video_item, titelFrame, false);

        ImageView videoThumbnail = (ImageView)to_add.findViewById(R.id.video_thumbnail);
        TextView videoTitle = (TextView)to_add.findViewById(R.id.video_title);
        TextView videoDescription = (TextView)to_add.findViewById(R.id.video_description);

        Picasso.with(getApplicationContext()).load(thumbnailURL).into(videoThumbnail);
        videoTitle.setText(title);
        videoDescription.setText(description);
        titelFrame.addView(to_add);

        playerView.initialize(YoutubeConnector.KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean restored) {
        if(!restored){
            player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
            player.loadVideo(getIntent().getStringExtra("VIDEO_ID"));

        }
    }
}
