package com.live.sapphire.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.FilmonInfo;
import com.live.sapphire.app.Customized.LinkInfoContainer;
import com.live.sapphire.app.Customized.StreamInfo;
import com.live.sapphire.app.Customized.VideoInfo;
import com.live.sapphire.app.R;
import com.live.sapphire.app.Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;

public class AndroidNativePlayerActivity extends AppCompatActivity {

    private Activity mActivity;
    private Context mContext;
    private VideoView mVideoView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_native_player);
        mActivity = AndroidNativePlayerActivity.this;
        mContext = AndroidNativePlayerActivity.this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar_native_player);
        AppConfigurations.setActionbarAndPrevButton(AndroidNativePlayerActivity.this, mToolbar);
        mVideoView = (VideoView) findViewById(R.id.video_view_native);
        TextView tv = (TextView) findViewById(R.id.marquee_text_native_player);
        tv.setSelected(true);
        Intent intent = getIntent();
        if (intent.hasExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE);
            LinkInfoContainer linkInfo = (LinkInfoContainer) bundle.getSerializable(LinksLoaderActivity.LINKS_LOADER_INFO_TEXT);
            if (linkInfo != null) {
                final String linkUrl = linkInfo.getUrl();
                if (linkUrl != null) {
                    tv.setText(linkInfo.getTitle());
                    getPublicIpAndMakeUrl(linkUrl);
                }
            }
        } else if (intent.hasExtra(FilmonActivity.FILMON_BUNDLE)) { //TODO: For OtherLinks -> Filmon videos
            Bundle bundle = intent.getBundleExtra(FilmonActivity.FILMON_BUNDLE);
            FilmonInfo filmonInfo = (FilmonInfo) bundle.getSerializable(FilmonActivity.FILMON_INFO_TEXT);
            if (filmonInfo != null) {
                HashMap<String, StreamInfo> streamRecords = filmonInfo.getStreamRecords();
                if (streamRecords != null) {
                    if (streamRecords.containsKey(Utilities.FILMON_VIDEO_QUALITY_LOW)) {
                        StreamInfo streamInfo = streamRecords.get(Utilities.FILMON_VIDEO_QUALITY_LOW);
                        String linkUrl = streamInfo.getUrl();
                        playVideo(linkUrl);
                    } else if (streamRecords.containsKey(Utilities.FILMON_VIDEO_QUALITY_HIGH)) {
                        StreamInfo streamInfo = streamRecords.get(Utilities.FILMON_VIDEO_QUALITY_HIGH);
                        String linkUrl = streamInfo.getUrl();
                        playVideo(linkUrl);
                    } else if (streamRecords.size() > 0) {
                        StreamInfo streamInfo = (StreamInfo) streamRecords.values().toArray()[0];
                        String linkUrl = streamInfo.getUrl();
                        playVideo(linkUrl);
                    }
                    tv.setText(filmonInfo.getTitle());
                }
            }
        } else if (intent.hasExtra(VideosActivity.VIDEOS_BUNDLE_TEXT)) { //TODO: For simple Videos
            Bundle bundle = intent.getBundleExtra(VideosActivity.VIDEOS_BUNDLE_TEXT);
            VideoInfo videoInfo = (VideoInfo) bundle.getSerializable(VideosActivity.VIDEOS_INFO_TEXT);
            if (videoInfo != null) {
                tv.setText(videoInfo.getTitle());
                String videoLink = Utilities.getVideoUrl(videoInfo.getVideoId());
                playVideo(videoLink);
            }
        }
    }


    public void getPublicIpAndMakeUrl(final String linkUrl) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("http://www.checkip.org").get();
                    String ip = doc.getElementById("yourip").select("h1").first().select("span").text();
                    if (ip != null && !ip.equals("")) {
                        long unixEpochTime = System.currentTimeMillis() / 1000;
                        String temp = ip + '-' + "token3" + '-' + unixEpochTime;
                        String videoLink = linkUrl + "?token=" + Base64.encodeToString(temp.getBytes(), Base64.DEFAULT);
                        final String video = videoLink.trim();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playVideo(video);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
            }
        });
    }

    private void playVideo(final String link) {
        AppConfigurations.getScreenOrientationAndSetToolbarVisibility(mActivity, mToolbar);
//        mVideoView.setVideoPath(link);
        mVideoView.setVideoURI(Uri.parse(link));
        mVideoView.requestFocus();
        MediaController mediaController = new MediaController(this);
        mVideoView.setMediaController(mediaController);
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                if (!mVideoView.isPlaying()) {
                    mVideoView.start();
                }
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("@AndroidPlayerActivity", "Type: " + what + ", Extra code is: " + extra);
//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        new AlertDialog.Builder(mActivity)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        AndroidNativePlayerActivity.this.finish();
//                                    }
//                                }).create().show();
//                    }
//                });
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mToolbar.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }
}
