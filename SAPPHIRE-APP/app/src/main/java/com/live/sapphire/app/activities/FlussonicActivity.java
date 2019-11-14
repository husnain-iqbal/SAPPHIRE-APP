package com.live.sapphire.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.live.sapphire.app.Customized.LinkInfoContainer;
import com.live.sapphire.app.Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class FlussonicActivity extends Activity {

    public static final String VIDEO_URL_TEXT = "URL";
    public static final String VIDEO_TITLE_TEXT = "VideoTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE);
            LinkInfoContainer linkInfo = (LinkInfoContainer) bundle.getSerializable(LinksLoaderActivity.LINKS_LOADER_INFO_TEXT);
            if (linkInfo == null) {
                Utilities.showLongToast(FlussonicActivity.this, "Video Link is empty");
                return;
            }
            String linkUrl = linkInfo.getUrl();
            if (linkUrl == null) {
                Utilities.showLongToast(FlussonicActivity.this, "Video Link is empty");
                return;
            }
            getPublicIpAndMakeUrl(linkInfo.getTitle(), linkUrl);
        }
    }

    public void getPublicIpAndMakeUrl(final String videoTitle, final String linkUrl) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("http://www.checkip.org").get();
                    String ip = doc.getElementById("yourip").select("h1").first().select("span").text();
                    if (ip != null && !ip.equals("")) {
                        long unixEpochTime = System.currentTimeMillis() / 1000;
                        unixEpochTime += 14400;
                        String temp = ip + '-' + "token3" + '-' + unixEpochTime;
                        String videoLink = linkUrl + "?token=" + Base64.encodeToString(temp.getBytes(), Base64.DEFAULT);
                        videoLink = videoLink.trim();
                        navigate2Player(videoTitle, videoLink);
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
            }
        });
    }

    private void navigate2Player(final String videoTitle, final String videoUrl) {
        FlussonicActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(FlussonicActivity.this, BrightCoveExoPlayerActivity.class);
                Intent intent = new Intent(FlussonicActivity.this, ExoPlayerActivity.class);
                intent.putExtra(VIDEO_URL_TEXT, videoUrl);
                intent.putExtra(VIDEO_TITLE_TEXT, videoTitle);
                startActivity(intent);
                finish();
            }
        });
    }
}
