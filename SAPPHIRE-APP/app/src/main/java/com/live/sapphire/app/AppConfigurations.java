package com.live.sapphire.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;

import com.live.sapphire.app.Customized.ImageDimensions;
import com.live.sapphire.app.Customized.ScreenDimensions;

/**
 * Created by Husnain Iqnal on 04-Jun-17.
 */
public class AppConfigurations {
    public static void setActionbarAndPrevButton(AppCompatActivity compatActivity, Toolbar toolbar) {
        compatActivity.setSupportActionBar(toolbar);
        // add back arrow to toolbar
        ActionBar actionBar = compatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public static int getScreenOrientation(Activity activity) {
        return activity.getResources().getConfiguration().orientation;
    }

    public static void getScreenOrientationAndSetToolbarVisibility(Activity activity, android.support.v7.widget.Toolbar toolbar) {
        if (getScreenOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    public static void getScreenOrientationAndSetToolbarVisibility(Activity activity, android.widget.Toolbar toolbar) {
        if (getScreenOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    public static ScreenDimensions getScreenDimensions(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new ScreenDimensions(size.x, size.y);
    }

    public static ImageDimensions getImageDimensions(Context context) {
        Resources r = context.getResources();
        int imageWidth = (int) r.getDimension(R.dimen.customized_list_row_image_width);
        int imageHeight = (int) r.getDimension(R.dimen.customized_list_row_image_height);
        return new ImageDimensions(imageWidth, imageHeight);
    }
}
