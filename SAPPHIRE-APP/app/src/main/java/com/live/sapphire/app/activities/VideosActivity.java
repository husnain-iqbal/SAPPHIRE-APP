package com.live.sapphire.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.live.sapphire.app.Adapters.VideosAdapter;
import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.LinkInfoContainer;
import com.live.sapphire.app.Customized.PlayListsInfo;
import com.live.sapphire.app.Customized.VideoInfo;
import com.live.sapphire.app.R;
import com.live.sapphire.app.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class VideosActivity extends AppCompatActivity implements VideosAdapter.SendData {

    private Activity mActivity;
    private ListView mListView;
    private VideosAdapter mVideosAdapter;
    public static final String VIDEOS_INFO_TEXT = "VideosInfoText";
    public static final String VIDEOS_BUNDLE_TEXT = "VideosBundleText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        mActivity = VideosActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_videos);
        AppConfigurations.setActionbarAndPrevButton(VideosActivity.this, toolbar);
        mListView = (ListView) findViewById(R.id.listViewVideos);
        Intent intent = getIntent();
        if (intent.hasExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE);
            LinkInfoContainer playListLinkInfo = (LinkInfoContainer) bundle.getSerializable(LinksLoaderActivity.LINKS_LOADER_INFO_TEXT);
            if (playListLinkInfo != null) {
                String videoLink = Utilities.getPlaylistVideos(playListLinkInfo.getChannelOrPlaylistId(), "");
                new PlayListVideosLoaderAsyncTask().execute(videoLink);
            }
        } else if (intent.hasExtra(PlayListsActivity.PLAYLISTS_LINKS_BUNDLE_TEXT)) {
            Bundle bundle = intent.getBundleExtra(PlayListsActivity.PLAYLISTS_LINKS_BUNDLE_TEXT);
            PlayListsInfo playListInfo = (PlayListsInfo) bundle.getSerializable(PlayListsActivity.PLAYLISTS_INFO_TEXT);
            if (playListInfo != null) {
                String url = Utilities.getPlaylistVideos(playListInfo.getPlayListId(), "");
                new PlayListVideosLoaderAsyncTask().execute(url);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendData2NewScreen(VideoInfo videoInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEOS_INFO_TEXT, videoInfo);
        startActivity(new Intent(VideosActivity.this, AndroidNativePlayerActivity.class).putExtra(VIDEOS_BUNDLE_TEXT, bundle));
//        startActivity(new Intent(VideosActivity.this, BrightCoveExoPlayerActivity.class).putExtra(VIDEOS_BUNDLE_TEXT, bundle));
    }

    private class PlayListVideosLoaderAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params[0]);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair(Utilities.POST_PARAMETER_NAME, Utilities.POST_PARAMETER_VALUE));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPost);
                String result = EntityUtils.toString(response.getEntity());

                if (result != null && !result.isEmpty()) {
                    JSONObject object = new JSONObject(result);

                    JSONObject playListsVideosJsonObject = object.getJSONObject("playlistvideos");
//                    String totalResults = playListsVideosJsonObject.getString("totalresults");
//                    String resultsPerPage = playListsVideosJsonObject.getString("resultsperpage");
//                    String nextPageToken = playListsVideosJsonObject.getString("nextpagetoken");
                    JSONArray videosJSONArray = playListsVideosJsonObject.getJSONArray("videos");
                    final ArrayList<VideoInfo> videoList = new ArrayList<>();
                    for (int i = 0; i < videosJSONArray.length(); i++) {
                        JSONObject jsonObject = videosJSONArray.getJSONObject(i);
                        String videoId = jsonObject.getString("videoid");
                        String title = jsonObject.getString("title");
                        String thumbnailUrl = jsonObject.getString("thumb");
                        videoList.add(new VideoInfo(videoId, title, thumbnailUrl));
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideosAdapter = new VideosAdapter(VideosActivity.this, mActivity, videoList);
                            mListView.setAdapter(mVideosAdapter);
                        }
                    });
                } else {
                    Utilities.showLongToastInUiThread(mActivity, Utilities.SERVER_ERROR_TEXT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
