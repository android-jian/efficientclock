package com.jian.android.efficientclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private EfficientClock mEfficientClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEfficientClock = (EfficientClock) findViewById(R.id.mEfficientClock);
        mEfficientClock.setCurrentData(90);
    }
}
