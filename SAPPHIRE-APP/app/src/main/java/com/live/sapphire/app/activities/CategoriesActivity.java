package com.live.sapphire.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.live.sapphire.app.Adapters.CategoriesAdapter;
import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.CategoryInfo;
import com.live.sapphire.app.Customized.ChildCategoryInfo;
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

public class CategoriesActivity extends AppCompatActivity implements CategoriesAdapter.SendData {

    private Activity mActivity;
    private Context mContext;
    private ArrayList<CategoryInfo> mCategoriesList;
    //    private ListView mListView;
    private GridView mGridView;
    private Toolbar mToolbar;
    private CategoriesAdapter mCategoryAdapter;
    public static final String ACTIVITY1_SERIALIZABLE_LIST_TEXT = "serializable_list_activity1";
    public static final String CATEGORIES_SERIALIZABLE_OBJECT_TEXT = "serializable_object_activity1";
    public static final String CATEGORIES_BUNDLE_TEXT = "bundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = CategoriesActivity.this;
        mContext = mActivity.getApplicationContext();
        mCategoriesList = new ArrayList<>();
        setContentView(R.layout.activity_categories);
//        mListView = (ListView) findViewById(R.id.listView_categories);
        mGridView = (GridView) findViewById(R.id.gridView_categories);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_categories);
        mToolbar.setVisibility(View.GONE);
        AppConfigurations.setActionbarAndPrevButton(CategoriesActivity.this, mToolbar);
        checkNetworkAndLoadData(Utilities.CATEGORIES_URL);
    }

    protected void onRestart() {
        checkNetworkAndLoadData(Utilities.CATEGORIES_URL);
        super.onRestart();
    }

    private void checkNetworkAndLoadData(String url) {
        if (Utilities.isNetworkAvailable(mContext)) {
//        loadCategories2(url);
            new DataLoaderAsyncTask().execute(url);
        } else {
            alertUserAboutNoNetwork();
        }
    }

    private void alertUserAboutNoNetwork() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(CategoriesActivity.this, android.R.style.Theme_Dialog);
        } else {
            builder = new AlertDialog.Builder(CategoriesActivity.this);
        }
        builder.setTitle("No Network Available")
                .setMessage("No network found on your device. Please check your network settings")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkNetworkAndLoadData(Utilities.CATEGORIES_URL);
                    }
                })
                .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.finish();
                    }
                })
                .setNeutralButton("Network\nSettings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utilities.openNetworkSettings(mActivity);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendData2NewScreen(CategoryInfo info) {
        ArrayList<SubcategoryInfo> subcategoryList = info.getSubcategoriesList();
        if (subcategoryList != null && !subcategoryList.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ACTIVITY1_SERIALIZABLE_LIST_TEXT, subcategoryList);
            startActivity(new Intent(CategoriesActivity.this, SubcategoriesActivity.class).putExtra(CATEGORIES_BUNDLE_TEXT, bundle));
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CATEGORIES_SERIALIZABLE_OBJECT_TEXT, info);
            startActivity(new Intent(CategoriesActivity.this, LinksLoaderActivity.class).putExtra(CATEGORIES_BUNDLE_TEXT, bundle));
        }
    }

//    private void loadCategories(String url) {
//        JSONArray jsonArray = new JSONArray();
//        try {
//            jsonArray.put(1, "e");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonArrayRequest jsonObject = new JsonArrayRequest(Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject jsonCategoryObject = response.getJSONObject(i);
//                        String categoryName = jsonCategoryObject.getString("Main_Cat_name");
//                        String categoryThumbnail = jsonCategoryObject.getString("thumbnail");
//                        String categoryId = jsonCategoryObject.getString("id");
//                        String subcategoryId = jsonCategoryObject.getString("subcatid");
//                        String mainCategoryId = jsonCategoryObject.getString("maincatid");
//                        if (jsonCategoryObject.has("subcategories")) {
//                            JSONArray subcategoriesArray = jsonCategoryObject.getJSONArray("subcategories");
//                            for (int j = 0; j < subcategoriesArray.length(); j++) {
//                                JSONObject jsonSubcategoryObject = subcategoriesArray.getJSONObject(j);
//                                String subcategoryName = jsonSubcategoryObject.getString("Sub_Cat_name");
//                                String subcategoryThumbnail = jsonSubcategoryObject.getString("thumbnail");
//                                String id = jsonSubcategoryObject.getString("id");
//                                String subcatid = jsonSubcategoryObject.getString("subcatid");
//                                String maincatId = jsonSubcategoryObject.getString("maincatid");
//                            }
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.v("@MyWebService", error.getMessage());
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("token", "Z3YxdDllSHlMWVN3TW9kQUtDOGRjdHZrN25zcVIyOXk=");
//                return super.getParams();
//            }
//
//            @Override
//            protected Response<JSONArray> parseNetworkResponse(NetworkResponse networkResponse) {
//                try {
//                    String jsonString = new String(networkResponse.data,
//                            HttpHeaderParser
//                                    .parseCharset(networkResponse.headers));
//                    return Response.success(new JSONArray(jsonString),
//                            HttpHeaderParser
//                                    .parseCacheHeaders(networkResponse));
//                } catch (UnsupportedEncodingException e) {
//                    return Response.error(new ParseError(e));
//                } catch (JSONException je) {
//                    return Response.error(new ParseError(je));
//                }
//            }
//        };
//        AppController.getInstances().addToRequestQueue(jsonObject);
//    }

//    private void loadCategories2(String url) {
//        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST,
//                url, null,
//                new Response.Listener<JSONArray>() {
//
//                    @Override
//                    public void onResponse(JSONArray response) {
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("token", "Z3YxdDllSHlMWVN3TW9kQUtDOGRjdHZrN25zcVIyOXk=");
//                return headers;
//            }
//        };
//        AppController.getInstances().addToRequestQueue(jsonObjReq);
//    }

    private class DataLoaderAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            if (Utilities.isInternetAvailable()) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urls[0]);
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                    nameValuePairs.add(new BasicNameValuePair(Utilities.POST_PARAMETER_NAME, Utilities.POST_PARAMETER_VALUE));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    String result = EntityUtils.toString(response.getEntity());
                    if (result != null && !result.isEmpty()) {
                        JSONObject object = new JSONObject(result);
                        JSONArray jsonCategoryArray = object.getJSONArray("categories");
                        for (int i = 0; i < jsonCategoryArray.length(); i++) {
                            JSONObject jsonCategoryObject = jsonCategoryArray.getJSONObject(i);
                            String categoryId = jsonCategoryObject.getString("id");
                            String categoryName = jsonCategoryObject.getString("Main_Cat_name");
                            String categoryThumbnailUrl = jsonCategoryObject.getString("thumbnail");
                            String subcategoryId = jsonCategoryObject.getString("subcatid");
                            String mainCategoryId = jsonCategoryObject.getString("maincatid");

                            ArrayList<SubcategoryInfo> subcategoriesList = new ArrayList<>();
                            if (jsonCategoryObject.has("subcategories")) {
                                JSONArray subcategoriesArray = jsonCategoryObject.getJSONArray("subcategories");
                                for (int j = 0; j < subcategoriesArray.length(); j++) {
                                    JSONObject jsonSubcategoryObject = subcategoriesArray.getJSONObject(j);
                                    String sub_categoryId = jsonSubcategoryObject.getString("id");
                                    String sub_categoryName = jsonSubcategoryObject.getString("Sub_Cat_name");
                                    String sub_categoryThumbnailUrl = jsonSubcategoryObject.getString("thumbnail");
                                    String sub_subcategoryId = jsonSubcategoryObject.getString("subcatid");
                                    String sub_mainCategoryId = jsonSubcategoryObject.getString("maincatid");

                                    ArrayList<ChildCategoryInfo> childCategoriesList = new ArrayList<>();
                                    if (jsonSubcategoryObject.has("childcategories")) {
                                        JSONArray childCategoriesArray = jsonSubcategoryObject.getJSONArray("childcategories");
                                        for (int k = 0; k < childCategoriesArray.length(); k++) {
                                            JSONObject jsonMainCategoryObject = childCategoriesArray.getJSONObject(k);
                                            String child_categoryName = jsonMainCategoryObject.getString("Child_Cat_name");
                                            String child_categoryId = jsonMainCategoryObject.getString("catid");
                                            String child_subcategoryId = jsonMainCategoryObject.getString("subcatid");
                                            String child_mainCategoryId = jsonMainCategoryObject.getString("maincatid");
                                            String child_thumbnailUrl = jsonMainCategoryObject.getString("thumbnail");
                                            childCategoriesList.add(new ChildCategoryInfo(child_categoryName, child_categoryId, child_subcategoryId, child_mainCategoryId, child_thumbnailUrl));
                                        }
                                    }
                                    subcategoriesList.add(new SubcategoryInfo(sub_categoryId, sub_categoryName, sub_subcategoryId, sub_mainCategoryId, sub_categoryThumbnailUrl, childCategoriesList));
                                }
                            }
                            mCategoriesList.add(new CategoryInfo(categoryId, categoryName, subcategoryId, mainCategoryId, categoryThumbnailUrl, subcategoriesList));
                        }
                        displayDataOnScreen();
                    } else {
                        Utilities.showLongToastInUiThread(mActivity, Utilities.SERVER_ERROR_TEXT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utilities.showLongToastInUiThread(mActivity, Utilities.NO_INTERNET_AVAILABILITY_TEXT);
            }
            return null;
        }
    }

    private void displayDataOnScreen() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCategoryAdapter = new CategoriesAdapter(CategoriesActivity.this, mActivity, mCategoriesList);
//                                mListView.setAdapter(mCategoryAdapter);
                mGridView.setAdapter(mCategoryAdapter);
                mToolbar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridView.setNumColumns(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridView.setNumColumns(2);
        }
    }
}
