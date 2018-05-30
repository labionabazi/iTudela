package ch.bbcag.itudela;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    private TextView mTextMessage;

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
        setContentView(R.layout.activity_history);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_history);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}