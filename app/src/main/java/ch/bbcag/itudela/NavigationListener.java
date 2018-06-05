package ch.bbcag.itudela;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

public class NavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Intent search, history, nowplaying;

    private Context context;

    private static NavigationListener instance;

    private NavigationListener(Context context) {
        this.context = context;
    }

    public static NavigationListener getInstance(Context context) {
        if(NavigationListener.instance == null)
        {
            NavigationListener.instance = new NavigationListener(context);
        }

        return NavigationListener.instance;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_search:
                if (search == null) {
                    search = new Intent(context, SearchActivity.class);
                }
                search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(search);
                return true;
            case R.id.navigation_history:
                if (history == null) {
                    history = new Intent(context, HistoryActivity.class);
                }
                history.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(history);
                return true;
            case R.id.navigation_music:
                if (nowplaying == null) {
                    nowplaying = new Intent(context, NowPlayingActivity.class);
                }
                nowplaying.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(nowplaying);
                return true;
        }
        return false;
    }

}
