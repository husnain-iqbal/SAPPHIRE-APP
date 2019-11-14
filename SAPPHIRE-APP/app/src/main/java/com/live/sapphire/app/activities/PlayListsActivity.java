package com.live.sapphire.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.live.sapphire.app.Adapters.PlayListsAdapter;
import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.LinkInfoContainer;
import com.live.sapphire.app.Customized.PlayListsInfo;
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

public class PlayListsActivity extends AppCompatActivity implements PlayListsAdapter.SendData {

    private Activity mActivity;
    private ListView mListView;
    private PlayListsAdapter mPlayListsAdapter;
    public static final String PLAYLISTS_INFO_TEXT = "PlayListsInfoText";
    public static final String PLAYLISTS_LINKS_BUNDLE_TEXT = "PlayListsInfoBundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_lists);
        mActivity = PlayListsActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_playlists);
        AppConfigurations.setActionbarAndPrevButton(PlayListsActivity.this, toolbar);
        mListView = (ListView) findViewById(R.id.listViewPlayLists);
        Intent intent = getIntent();
        if (intent.hasExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE);
            LinkInfoContainer linkInfo = (LinkInfoContainer) bundle.getSerializable(LinksLoaderActivity.LINKS_LOADER_INFO_TEXT);
            if (linkInfo != null) {
                new DataLoaderAsyncTask().execute(Utilities.getChannelsPlaylistUrl(linkInfo.getId(), false));
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
    public void sendData2NewScreen(PlayListsInfo playListInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PLAYLISTS_INFO_TEXT, playListInfo);
        startActivity(new Intent(PlayListsActivity.this, VideosActivity.class).putExtra(PLAYLISTS_LINKS_BUNDLE_TEXT, bundle));
    }

    private class DataLoaderAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String playListUrl = Utilities.getChannelsPlaylistUrl(params[0], false);
            HttpPost httpPost = new HttpPost(playListUrl);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair(Utilities.POST_PARAMETER_NAME, Utilities.POST_PARAMETER_VALUE));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPost);
                String result = EntityUtils.toString(response.getEntity());

                if (result != null && !result.isEmpty()) {
                    JSONObject object = new JSONObject(result);
                    JSONObject channelInfoJsonObject = object.getJSONObject("channelinfo");
//                    String title = channelInfoJsonObject.getString("title");
//                    String url = channelInfoJsonObject.getString("url");
//                    String channelId = channelInfoJsonObject.getString("channelid");
//                    String description = channelInfoJsonObject.getString("description");
//                    String publishDate = channelInfoJsonObject.getString("publishedat");
//                    String totalResults = channelInfoJsonObject.getString("totalresults");
//                    String resultsPerPage = channelInfoJsonObject.getString("resultsperpage");
//                    String nextPageToken = channelInfoJsonObject.getString("nextpagetoken");
                    JSONArray playListJsonArray = channelInfoJsonObject.getJSONArray("playlists");

                    final ArrayList<PlayListsInfo> playListInfoList = new ArrayList<>();
                    for (int i = 0; i < playListJsonArray.length(); i++) {
                        JSONObject jsonObject = playListJsonArray.getJSONObject(i);
                        String playlistId = jsonObject.getString("playlistid");
                        String playListPublishDate = jsonObject.getString("publishedat");
                        String playlistTitle = jsonObject.getString("title");
                        String thumbnailUrl = jsonObject.getString("thumb");
                        playListInfoList.add(new PlayListsInfo(playlistTitle, playlistId, playListPublishDate, thumbnailUrl));
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPlayListsAdapter = new PlayListsAdapter(PlayListsActivity.this, mActivity, playListInfoList);
                            mListView.setAdapter(mPlayListsAdapter);
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
