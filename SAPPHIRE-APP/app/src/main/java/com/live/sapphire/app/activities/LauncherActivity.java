package com.live.sapphire.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.live.sapphire.app.R;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        startActivity(new Intent(LauncherActivity.this, CategoriesActivity.class));
        finish();
    }
}
