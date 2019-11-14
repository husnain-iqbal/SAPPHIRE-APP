package com.live.sapphire.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.live.sapphire.app.Customized.FilmonInfo;
import com.live.sapphire.app.Customized.LinkInfoContainer;
import com.live.sapphire.app.Customized.StreamInfo;
import com.live.sapphire.app.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class FilmonActivity extends Activity {

    private Activity mActivity;
    public static final String FILMON_INFO_TEXT = "FilmonInfo";
    public static final String FILMON_BUNDLE = "FilmonBundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = FilmonActivity.this;
        Intent intent = getIntent();
        if (intent.hasExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(LinksLoaderActivity.LINKS_LOADER_INFO_BUNDLE);
            LinkInfoContainer linkInfoContainer = (LinkInfoContainer) bundle.getSerializable(LinksLoaderActivity.LINKS_LOADER_INFO_TEXT);
            if (linkInfoContainer != null) {
                new FilmonInfoLoaderAsyncTask().execute(linkInfoContainer.getUrl());
            }
        }
    }

    private void navigate2Player(final FilmonInfo info) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putSerializable(FILMON_INFO_TEXT, info);
                startActivity(new Intent(FilmonActivity.this, ExoPlayerActivity.class).putExtra(FILMON_BUNDLE, bundle));
//                startActivity(new Intent(FilmonActivity.this, BrightCoveExoPlayerActivity.class).putExtra(FILMON_BUNDLE, bundle));
//                startActivity(new Intent(FilmonActivity.this, AndroidNativePlayerActivity.class).putExtra(FILMON_BUNDLE, bundle));
                mActivity.finish();
            }
        });
    }

    private class FilmonInfoLoaderAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);
            try {
                HttpResponse response = httpclient.execute(httpGet);
                String result = EntityUtils.toString(response.getEntity());
                if (result != null && !result.isEmpty()) {
                    JSONObject object = new JSONObject(result);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    JSONObject dataJSONObject = object.getJSONObject("data");
                    String id = dataJSONObject.getString("id");
                    String title = dataJSONObject.getString("title");
                    String alias = dataJSONObject.getString("alias");
                    String description = dataJSONObject.getString("description");
                    String group = dataJSONObject.getString("group");
                    JSONArray streamsJSONArray = dataJSONObject.getJSONArray("streams");
                    HashMap<String, StreamInfo> streamInfoRecord = new HashMap<>();
                    for (int i = 0; i < streamsJSONArray.length(); i++) {
                        JSONObject jsonObject = streamsJSONArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        String quality = jsonObject.getString("quality");
                        String videoUrl = jsonObject.getString("url");
                        String timeOut = jsonObject.getString("watch-timeout");
                        quality = quality.toLowerCase();
                        streamInfoRecord.put(quality, new StreamInfo(name, quality, videoUrl, timeOut));
                    }
                    FilmonInfo filmonInfo = new FilmonInfo(code, message, id, title, alias, description, group, streamInfoRecord);
                    navigate2Player(filmonInfo);
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
