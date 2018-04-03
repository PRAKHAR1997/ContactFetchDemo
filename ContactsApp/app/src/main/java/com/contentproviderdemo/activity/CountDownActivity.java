package com.contentproviderdemo.activity;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.contentproviderdemo.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CountDownActivity extends AppCompatActivity {

    private TextView mTvTimer;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);
        mTvTimer = findViewById(R.id.tv_timer);
        ((ImageView)findViewById(R.id.iv_toolbar)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_toolbar)).setText(R.string.s_count_down);
        mCountDownTimer = new CountDownTimer(60000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                mTvTimer.setText(getString(R.string.s_time_remain)+" "+timeChange(millisUntilFinished));
            }
            @Override
            public void onFinish() {
                mTvTimer.setText(R.string.s_done);
                ((ConstraintLayout)findViewById(R.id.cl_timer)).setBackgroundColor(Color.YELLOW);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountDownTimer.cancel();
    }

    private String timeChange(long millis){
        String hms = String.format(Locale.getDefault(),"%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }
}
