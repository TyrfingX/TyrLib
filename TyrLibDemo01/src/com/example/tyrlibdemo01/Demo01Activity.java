package com.example.tyrlibdemo01;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Demo01Activity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo01);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_demo01, menu);
        return true;
    }
}
