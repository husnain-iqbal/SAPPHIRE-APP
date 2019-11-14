package com.live.sapphire.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.live.sapphire.app.Customized.FilmonInfo;
import com.live.sapphire.app.Customized.StreamInfo;
import com.live.sapphire.app.R;
import com.live.sapphire.app.Utilities;

import java.io.IOException;
import java.util.HashMap;

public class ExoPlayerActivity extends AppCompatActivity {

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        TextView videoInfoTextView = (TextView) findViewById(R.id.marquee_text_exo_player);
        mVideoView = (SimpleExoPlayerView) findViewById(R.id.video_view_exoplayer);

        Intent intent = getIntent();
        if (intent.hasExtra(FlussonicActivity.VIDEO_URL_TEXT) && intent.hasExtra(FlussonicActivity.VIDEO_TITLE_TEXT)) { //TODO: For InLinks -> Flussonic videos
            String videoLink = intent.getStringExtra(FlussonicActivity.VIDEO_URL_TEXT);
            String videoTitle = intent.getStringExtra(FlussonicActivity.VIDEO_TITLE_TEXT);
            videoInfoTextView.setText(videoTitle);
            playVideo(videoLink);
        } else if (intent.hasExtra(FilmonActivity.FILMON_BUNDLE)) { //TODO: For OtherLinks -> Filmon videos
            Bundle bundle = intent.getBundleExtra(FilmonActivity.FILMON_BUNDLE);
            FilmonInfo filmonInfo = (FilmonInfo) bundle.getSerializable(FilmonActivity.FILMON_INFO_TEXT);
            if (filmonInfo != null) {
                HashMap<String, StreamInfo> streamRecords = filmonInfo.getStreamRecords();
                if (streamRecords != null) {
                    StreamInfo streamInfo = new StreamInfo();
                    if (streamRecords.containsKey(Utilities.FILMON_VIDEO_QUALITY_LOW)) {
                        streamInfo = streamRecords.get(Utilities.FILMON_VIDEO_QUALITY_LOW);
                    } else if (streamRecords.containsKey(Utilities.FILMON_VIDEO_QUALITY_HIGH)) {
                        streamInfo = streamRecords.get(Utilities.FILMON_VIDEO_QUALITY_HIGH);
                    } else if (streamRecords.size() > 0) {
                        streamInfo = (StreamInfo) streamRecords.values().toArray()[0];
                    }

                    videoInfoTextView.setText(filmonInfo.getTitle());
                    playVideo(streamInfo.getUrl());
                }
            }
        }
//        else if (intent.hasExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE)) {//TODO: Other Links -> Karwan Videos "NOT WORKING..."
//            Bundle bundle = intent.getBundleExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE);
//            LinkInfoContainer linkInfo = (LinkInfoContainer) bundle.getSerializable(LinksLoaderActivity.LINKS_LOADER_INFO_TEXT);
//            if (linkInfo == null) {
//                Utilities.showLongToast(BrightCoveExoPlayerActivity.this, "Video Link is empty");
//            } else {
//                if (linkInfo.getSource().equalsIgnoreCase(Utilities.SOURCE_KARWAN_TEXT)) {
//                    videoInfoTextView.setText(linkInfo.getTitle());
//                    brightcoveVideoView.add(Video.createVideo(linkInfo.getUrl()));
//                }
//            }
//        } else if (intent.hasExtra(VideosActivity.VIDEOS_BUNDLE_TEXT)) { //TODO: For simple Videos "NOT WORKING..."
//            Bundle bundle = intent.getBundleExtra(VideosActivity.VIDEOS_BUNDLE_TEXT);
//            VideoInfo videoInfo = (VideoInfo) bundle.getSerializable(VideosActivity.VIDEOS_INFO_TEXT);
//            if (videoInfo != null) {
//                String videoLink = Utilities.getVideoUrl(videoInfo.getVideoId());
//                videoInfoTextView.setText(videoInfo.getTitle());
//                brightcoveVideoView.add(Video.createVideo(videoLink));
//            }
//        }

    }

    private void playVideo(String link) {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(ExoPlayerActivity.this, trackSelector);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(ExoPlayerActivity.this, Util.getUserAgent(ExoPlayerActivity.this, "Exo2"), defaultBandwidthMeter);
        final HlsMediaSource hlsMediaSource = new HlsMediaSource(Uri.parse(link), dataSourceFactory, mainHandler, new AdaptiveMediaSourceEventListener() {
            @Override
            public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
            }

            @Override
            public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            }

            @Override
            public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            }

            @Override
            public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
            }

            @Override
            public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
            }

            @Override
            public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
            }
        });
        mExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object o) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            }

            @Override
            public void onLoadingChanged(boolean b) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d("Tag", playbackState + "");
                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        mExoPlayer.prepare(hlsMediaSource);
                        mExoPlayer.setPlayWhenReady(true);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        break;
                    case ExoPlayer.STATE_READY:
                        break;
                    case ExoPlayer.STATE_ENDED:
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException e) {
                String alertTitle = "Stream Error";
                String errorMsg = e.getMessage();
                if (errorMsg != null && !errorMsg.isEmpty()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ExoPlayerActivity.this);
                    alertBuilder.setTitle(alertTitle);
                    alertBuilder.setMessage(errorMsg);
                    alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                            .create()
                            .show();
                }
            }

            @Override
            public void onPositionDiscontinuity() {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }
        });
        mExoPlayer.prepare(hlsMediaSource);
        mVideoView.setPlayer(mExoPlayer);
        mVideoView.requestFocus();
        mExoPlayer.setPlayWhenReady(true);
        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    public void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

}

