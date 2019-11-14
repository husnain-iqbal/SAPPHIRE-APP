package com.live.sapphire.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.LinkInfoContainer;
import com.live.sapphire.app.R;
import com.live.sapphire.app.Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebViewActivity extends AppCompatActivity {

    private Activity mActivity;
    private WebView mWebView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mActivity = WebViewActivity.this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar_web_view);
        AppConfigurations.setActionbarAndPrevButton(WebViewActivity.this, mToolbar);
        TextView tv = (TextView) findViewById(R.id.marquee_text_web_view);
        tv.setSelected(true);
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 16) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        Intent intent = getIntent();
        if (intent.hasExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE);
            LinkInfoContainer linkInfo = (LinkInfoContainer) bundle.getSerializable(LinksLoaderActivity.LINKS_LOADER_INFO_TEXT);
            if (linkInfo == null) {
                Utilities.showLongToast(WebViewActivity.this, "Video Link is empty");
            } else {
                tv.setText(linkInfo.getTitle());
                if (linkInfo.getSource().equalsIgnoreCase(Utilities.SOURCE_KARWAN_TEXT)) {
                    mWebView.loadUrl(linkInfo.getUrl() + "?enablejsapi=1&autoplay=1");
                } else if (linkInfo.getSource().equalsIgnoreCase(Utilities.SOURCE_FLUSSONIC_TEXT)) {
                    final String linkUrl = linkInfo.getUrl();
                    if (linkUrl != null) {
                        tv.setText(linkInfo.getTitle());
                        getPublicIpAndMakeUrl(linkUrl);
                    }
                }
            }
        } else if (intent.hasExtra(YoutubePlayerActivity.YOUTUBE_PLAYER_BUNDLE_TEXT)) {
            Bundle bundle = intent.getBundleExtra(YoutubePlayerActivity.YOUTUBE_PLAYER_BUNDLE_TEXT);
            LinkInfoContainer linkInfo = (LinkInfoContainer) bundle.getSerializable(YoutubePlayerActivity.YOUTUBE_PLAYER_INFO_TEXT);
            if (linkInfo == null) {
                Utilities.showLongToast(WebViewActivity.this, "Video Link is empty");
            } else {
                tv.setText(linkInfo.getTitle());
                String link = linkInfo.getUrl();
                String youtubeLinkPrefix = "https://www.youtube.com/watch?v=";
                final String youtubeEmbedPrefix = "https://www.youtube.com/embed/VIDEOID";
//                final String youtubeEmbedPrefix = "https://www.youtube.com/tv#/watch?v=VIDEOID";
//                final String youtubeEmbedPrefix = "https://www.youtube.com/tv#/watch/video/idle?v=VIDEOID&resume";
//                final String youtubeEmbedPrefix = "http://www.youtube.com/watch_popup?v=VIDEOID";
                if (link.contains(youtubeLinkPrefix)) {
                    link = link.replace(youtubeLinkPrefix, "") ;
                    link = youtubeEmbedPrefix.replace("VIDEOID", link);
                }
                link += "?enablejsapi=1&autoplay=1";
                mWebView.loadUrl(link);

//                final String mimeType = "text/html";
//                final String encoding = "UTF-8";
//                String htmlUrl = getUrlHTML(link.replace(youtubeLinkPrefix, ""));
////                mWebView.loadDataWithBaseURL("https://www.youtube.com", htmlUrl, mimeType, encoding, null);
////                mWebView.loadData(htmlUrl, mimeType, encoding);

            }
        } else {
            Utilities.showLongToast(WebViewActivity.this, "Video Link is empty");
        }
    }

    public String getUrlHTML(String videoId) {
//        String youtubeUrl = "https://www.youtube.com/embed/";
        String youtubeUrl = "https://www.youtube.com/tv#/watch/video/idle?v=";
        return "<iframe class=\"youtube-player\" style=\"border: 0; width: 100%; height: 95%; padding:0px; margin:0px\" id=\"ytplayer\" type=\"text/html\" src=\""+youtubeUrl
                + videoId + "&resume" /*"?enablejsapi=1&autoplay=1"*/
                + "&fs=0\" frameborder=\"0\">\n"
                + "</iframe>\n";
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
                        unixEpochTime += 14400;
                        String temp = ip + '-' + "token3" + '-' + unixEpochTime;
                        String videoLink = linkUrl + "?token=" + Base64.encodeToString(temp.getBytes(), Base64.DEFAULT);
                        final String link = videoLink.trim();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWebView.loadUrl(link + "?enablejsapi=1&autoplay=1");
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
            }
        });
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

    @Override
    public void onDestroy() {
        mWebView.loadUrl("about:blank");
        super.onDestroy();
    }
}
