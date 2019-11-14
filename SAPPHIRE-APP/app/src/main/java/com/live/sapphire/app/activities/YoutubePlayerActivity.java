package com.live.sapphire.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Config;
import com.live.sapphire.app.Customized.LinkInfoContainer;
import com.live.sapphire.app.R;
import com.live.sapphire.app.Utilities;

public class YoutubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private Toolbar mToolbar;
    private Activity mActivity;
    private LinkInfoContainer mLinkInfo;
    private YouTubePlayer mYouTubeVideoPlayer;
    private YouTubePlayerView mYouTubeVideoView;
    private static String mVideoLinkId;
    private static final int RECOVERY_REQUEST = 1;
    public static final String YOUTUBE_PLAYER_INFO_TEXT = "YoutubePlayerInfotext";
    public static final String YOUTUBE_PLAYER_BUNDLE_TEXT = "YoutubePlayerBundletext";

    private static String getVideoLinkId() {
        return mVideoLinkId;
    }

    private void setVideoLinkId(String videoId) {
        mVideoLinkId = videoId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        mActivity = YoutubePlayerActivity.this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar_youtube_player);
        setActionBar(mToolbar);
        mYouTubeVideoView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        TextView tv = (TextView) findViewById(R.id.marquee_text_youtube_player);
        tv.setSelected(true);
//        mYouTubeVideoView.initialize(Config.YOUTUBE_API_KEY, this); TODO: Uncomment for Youtube Player
        Intent intent = getIntent();
        if (intent.hasExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE);
            LinkInfoContainer info = (LinkInfoContainer) bundle.getSerializable(LinksLoaderActivity.LINKS_LOADER_INFO_TEXT);
            if (info == null) {
                Utilities.showLongToast(YoutubePlayerActivity.this, "Video Link is empty");
            } else {
                mLinkInfo = info;
                String link = info.getUrl();
                String prefixUrl = "https://www.youtube.com/watch?v=";
                if (link.contains(prefixUrl)) {
                    tv.setText(info.getTitle());
//                    setVideoLinkId(link.replace(prefixUrl, "")); TODO: Uncomment for Youtube Player

                    // TODO: delete it ... added only for testing
                    Bundle args = new Bundle();
                    args.putSerializable(YOUTUBE_PLAYER_INFO_TEXT, mLinkInfo);
                    startActivity(new Intent(YoutubePlayerActivity.this, WebViewActivity.class).putExtra(YOUTUBE_PLAYER_BUNDLE_TEXT, args));
                    finish();
                }
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player, boolean wasRestored) {
        mYouTubeVideoPlayer = player;
        if (mVideoLinkId != null && !mVideoLinkId.isEmpty()) {
//            player.cueVideo(getVideoLinkId());
            AppConfigurations.getScreenOrientationAndSetToolbarVisibility(mActivity, mToolbar);
            player.loadVideo(getVideoLinkId());
            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {
                }

                @Override
                public void onLoaded(String s) {
                    player.play();
                    Log.e("YoutubePlayerActivity", "video loaded");
                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {
                    Log.e("YoutubePlayerActivity", "video started");
                }

                @Override
                public void onVideoEnded() {
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {
                    Log.e("YoutubePlayerActivity", errorReason.name());
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
//            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
            Bundle bundle = new Bundle();
            bundle.putSerializable(YOUTUBE_PLAYER_INFO_TEXT, mLinkInfo);
            startActivity(new Intent(YoutubePlayerActivity.this, WebViewActivity.class).putExtra(YOUTUBE_PLAYER_BUNDLE_TEXT, bundle));
            finish();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return mYouTubeVideoView;
    }

    @Override
    protected void onDestroy() {
        if (mYouTubeVideoPlayer != null) {
            mYouTubeVideoPlayer.release();
        }
        super.onDestroy();
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