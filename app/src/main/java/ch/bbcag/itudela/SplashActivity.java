package ch.bbcag.itudela;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                    Intent history = new Intent(SplashActivity.this, HistoryActivity.class);
                    history.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(history);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };
        logoTimer.start();
    }
}
