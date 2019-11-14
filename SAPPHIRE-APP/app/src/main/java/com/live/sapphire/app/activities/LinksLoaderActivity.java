package com.live.sapphire.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.live.sapphire.app.Adapters.LinksLoaderAdapter;
import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.CategoryInfo;
import com.live.sapphire.app.Customized.ChildCategoryInfo;
import com.live.sapphire.app.Customized.LinkInfoContainer;
import com.live.sapphire.app.Customized.SubcategoryInfo;
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

public class LinksLoaderActivity extends AppCompatActivity implements LinksLoaderAdapter.SendData {

    private Context mContext;
    private Activity mActivity;
    private ListView mListView;
    private ProgressDialog mProgressDialog;
    private LinksLoaderAdapter mInLinksAdapter;
    private ArrayList<LinkInfoContainer> mInLinkInfoList;
    public static final String LINKS_LOADER_INFO_TEXT = "InLinksInfoSerializableObject";
    public static final String LINKS_LOADER_INFO_BUNDLE = "InLinksInfoBundle";

    private void init() {
        mActivity = LinksLoaderActivity.this;
        mContext = mActivity.getApplicationContext();
        mInLinkInfoList = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.listViewIndividualLinks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_links_loader);
        AppConfigurations.setActionbarAndPrevButton(LinksLoaderActivity.this, toolbar);
        mProgressDialog = new ProgressDialog(LinksLoaderActivity.this);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Loading Data...");
        mProgressDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links_loader);
        init();
        try {
            String categoryId = null;
            String subcategoryId = null;
            String mainCategoryId = null;
            Intent intent = getIntent();
            if (intent.hasExtra(CategoriesActivity.CATEGORIES_BUNDLE_TEXT)) {
                Bundle bundle = intent.getBundleExtra(CategoriesActivity.CATEGORIES_BUNDLE_TEXT);
                CategoryInfo categoryInfo = (CategoryInfo) bundle.getSerializable(CategoriesActivity.CATEGORIES_SERIALIZABLE_OBJECT_TEXT);
                if (categoryInfo != null) {
                    categoryId = categoryInfo.getId();
                    subcategoryId = categoryInfo.getSubcategoryId();
                    mainCategoryId = categoryInfo.getMainCategoryId();
                }
            } else if (intent.hasExtra(SubcategoriesActivity.SUBCATEGORIES_BUNDLE_TEXT)) {
                Bundle bundle = intent.getBundleExtra(SubcategoriesActivity.SUBCATEGORIES_BUNDLE_TEXT);
                SubcategoryInfo subcategoryInfo = (SubcategoryInfo) bundle.getSerializable(SubcategoriesActivity.SUBCATEGORIES_SERIALIZABLE_OBJECT_TEXT);
                if (subcategoryInfo != null) {
                    categoryId = subcategoryInfo.getId();
                    subcategoryId = subcategoryInfo.getSubcategoryId();
                    mainCategoryId = subcategoryInfo.getMainCategoryId();
                }
            } else if (intent.hasExtra(ChildCategoriesActivity.CHILD_CATEGORIES_BUNDLE_TEXT)) {
                Bundle bundle = intent.getBundleExtra(ChildCategoriesActivity.CHILD_CATEGORIES_BUNDLE_TEXT);
                ChildCategoryInfo childCategoryInfo = (ChildCategoryInfo) bundle.getSerializable(ChildCategoriesActivity.CHILD_CATEGORIES_SERIALIZABLE_OBJECT_TEXT);
                if (childCategoryInfo != null) {
                    categoryId = childCategoryInfo.getCategoryId();
                    subcategoryId = childCategoryInfo.getSubcategoryId();
                    mainCategoryId = childCategoryInfo.getMainCategoryId();
                }
            }

            if (categoryId != null && subcategoryId != null && mainCategoryId != null) {
                String[] params = {categoryId, subcategoryId, mainCategoryId};
                new InLinksLoaderAsyncTask().execute(params);
                new OtherLinksLoaderAsyncTask().execute(params);
                new ChannelsLoaderAsyncTask().execute(params);
                new PlayListsLoaderAsyncTask().execute(params);
                mProgressDialog.show();
            }
        } catch (Exception e) {
            Log.e("@IndividualLinkActivity", e.getMessage());
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
    public void sendData2NewScreen(LinkInfoContainer info) {
        if (info != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(LINKS_LOADER_INFO_TEXT, info);
            String linkType = info.getLinkType();
            if (linkType.equals(Utilities.LINK_TYPE_INDIVIDUAL_LINKS)) {
                if (info.getSource().equalsIgnoreCase(Utilities.SOURCE_YOUTUBE_TEXT)) {
                    startActivity(new Intent(LinksLoaderActivity.this, YoutubePlayerActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
                } else if (info.getSource().equalsIgnoreCase(Utilities.SOURCE_FLUSSONIC_TEXT)) {
                    startActivity(new Intent(LinksLoaderActivity.this, FlussonicActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
                }
            } else if (linkType.equals(Utilities.LINK_TYPE_OTHER_LINKS)) {
                if (info.getSource().equalsIgnoreCase(Utilities.SOURCE_FILMON_TEXT)) {
                    startActivity(new Intent(LinksLoaderActivity.this, FilmonActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
                } else if (info.getSource().equalsIgnoreCase(Utilities.SOURCE_KARWAN_TEXT)) {
                    startActivity(new Intent(LinksLoaderActivity.this, WebViewActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
                }
            } else if (linkType.equals(Utilities.LINK_TYPE_CHANNELS)) {
                startActivity(new Intent(LinksLoaderActivity.this, PlayListsActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
            } else if (linkType.equals(Utilities.LINK_TYPE_PLAYLISTS)) {
                startActivity(new Intent(LinksLoaderActivity.this, VideosActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
            }
//                startActivity(new Intent(LinksLoaderActivity.this, YoutubePlayerActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
//                startActivity(new Intent(LinksLoaderActivity.this, AndroidNativePlayerActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
//                startActivity(new Intent(LinksLoaderActivity.this, ExoPlayerActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
//               startActivity(new Intent(LinksLoaderActivity.this, VitamioPlayerActivity.class).putExtra(LINKS_LOADER_INFO_BUNDLE, bundle));
        }
    }

    private void setAdapterToListViewAndCancelDialog() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInLinksAdapter = new LinksLoaderAdapter(LinksLoaderActivity.this, mActivity, mInLinkInfoList);
                mListView.setAdapter(mInLinksAdapter);
                if (mInLinkInfoList.isEmpty()) {
                    Utilities.showLongToast(mContext, "No data available");
                }
                mProgressDialog.cancel();
            }
        });
    }

    private class InLinksLoaderAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String url = Utilities.getIndividualLinksUrl(params[0], params[1], params[2]);
            HttpPost httpPost = new HttpPost(url);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair(Utilities.POST_PARAMETER_NAME, Utilities.POST_PARAMETER_VALUE));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPost);
                String result = EntityUtils.toString(response.getEntity());
                if (result != null && !result.isEmpty()) {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("individuallinks");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String videoUrl = jsonObject.getString("url");
                        String thumbnailUrl = jsonObject.getString("thumbnail");
                        String source = jsonObject.getString("source");
                        String ebound = jsonObject.getString("ebound");
                        String linkId = jsonObject.getString("linkid");
                        mInLinkInfoList.add(new LinkInfoContainer(title, videoUrl, thumbnailUrl, linkId, source, ebound, Utilities.LINK_TYPE_INDIVIDUAL_LINKS));
                    }
                } else {
                    Utilities.showLongToastInUiThread(mActivity, Utilities.SERVER_ERROR_TEXT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class OtherLinksLoaderAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String url = Utilities.getOtherLinksUrl(params[0], params[1], params[2]);
            HttpPost httpPost = new HttpPost(url);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair(Utilities.POST_PARAMETER_NAME, Utilities.POST_PARAMETER_VALUE));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPost);
                String result = EntityUtils.toString(response.getEntity());
                if (result != null && !result.isEmpty()) {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("channels");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String title = jsonObject.getString("title");
                        String videoUrl = jsonObject.getString("url");
                        String source = jsonObject.getString("source");
                        String thumbnailUrl = jsonObject.getString("thumbnail");
                        mInLinkInfoList.add(new LinkInfoContainer(id, title, videoUrl, thumbnailUrl, source, Utilities.LINK_TYPE_OTHER_LINKS));
                    }
                } else {
                    Utilities.showLongToastInUiThread(mActivity, Utilities.SERVER_ERROR_TEXT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ChannelsLoaderAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String url = Utilities.getChannelsUrl(params[0], params[1], params[2]);
            HttpPost httpPost = new HttpPost(url);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair(Utilities.POST_PARAMETER_NAME, Utilities.POST_PARAMETER_VALUE));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPost);
                String result = EntityUtils.toString(response.getEntity());
                if (result != null && !result.isEmpty()) {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("channels");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String title = jsonObject.getString("title");
                        String videoUrl = jsonObject.getString("url");
                        String playlistId = jsonObject.getString("channelid");
                        String description = jsonObject.getString("description");
                        String publishDate = jsonObject.getString("publishedat");
                        String thumbnailUrl = jsonObject.getString("thumbnail");
                        mInLinkInfoList.add(new LinkInfoContainer(id, title, videoUrl, thumbnailUrl, playlistId, description, publishDate, Utilities.LINK_TYPE_CHANNELS));
                    }
                } else {
                    Utilities.showLongToastInUiThread(mActivity, Utilities.SERVER_ERROR_TEXT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class PlayListsLoaderAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String url = Utilities.getPlayListsUrl(params[0], params[1], params[2]);
            HttpPost httpPost = new HttpPost(url);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair(Utilities.POST_PARAMETER_NAME, Utilities.POST_PARAMETER_VALUE));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPost);
                String result = EntityUtils.toString(response.getEntity());
                if (result != null && !result.isEmpty()) {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("playlists");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String videoUrl = jsonObject.getString("url");
                        String thumbnailUrl = jsonObject.getString("thumbnail");
                        String playlistId = jsonObject.getString("playlistid");
                        String description = jsonObject.getString("description");
                        String publishDate = jsonObject.getString("publishedat");
                        mInLinkInfoList.add(new LinkInfoContainer(null, title, videoUrl, thumbnailUrl, playlistId, description, publishDate, Utilities.LINK_TYPE_PLAYLISTS));
                    }
                } else {
                    Utilities.showLongToastInUiThread(mActivity, Utilities.SERVER_ERROR_TEXT);
                }
                setAdapterToListViewAndCancelDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
