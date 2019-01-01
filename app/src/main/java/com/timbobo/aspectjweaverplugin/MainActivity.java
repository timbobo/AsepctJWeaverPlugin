package com.timbobo.aspectjweaverplugin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doSomething();

    }

    private void doSomething() {
        int i = 0;
        int j = 2;
    }
}
