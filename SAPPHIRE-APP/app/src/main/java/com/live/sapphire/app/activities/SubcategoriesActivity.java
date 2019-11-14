package com.live.sapphire.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.live.sapphire.app.Adapters.SubcategoriesAdapter;
import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.ChildCategoryInfo;
import com.live.sapphire.app.Customized.SubcategoryInfo;
import com.live.sapphire.app.R;

import java.util.ArrayList;

public class SubcategoriesActivity extends AppCompatActivity implements SubcategoriesAdapter.SendData {

    public static final String SUBCATEGORIES_SERIALIZABLE_OBJECT_TEXT = "activity 2 serializable text";
    public static final String SUBCATEGORIES_BUNDLE_TEXT = "activity 2 bundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subcategories);
        AppConfigurations.setActionbarAndPrevButton(SubcategoriesActivity.this, toolbar);
        Intent intent = getIntent();
        if (intent.hasExtra(CategoriesActivity.CATEGORIES_BUNDLE_TEXT)) {
            Bundle bundle = intent.getBundleExtra(CategoriesActivity.CATEGORIES_BUNDLE_TEXT);
            ArrayList<SubcategoryInfo> subcategoryList = (ArrayList<SubcategoryInfo>) bundle.getSerializable(CategoriesActivity.ACTIVITY1_SERIALIZABLE_LIST_TEXT);
            ListView listView = (ListView) findViewById(R.id.listView_subcategories);
            SubcategoriesAdapter categoryAdapter = new SubcategoriesAdapter(SubcategoriesActivity.this, SubcategoriesActivity.this, subcategoryList);
            listView.setAdapter(categoryAdapter);
        }
//        ArrayList<SubcategoryInfo> subcategoryList = (ArrayList<SubcategoryInfo>) getIntent().getBundleExtra(CategoriesActivity.CATEGORIES_BUNDLE_TEXT).getSerializable(CategoriesActivity.ACTIVITY1_SERIALIZABLE_LIST_TEXT);
//        ListView listView = (ListView) findViewById(R.id.listView_subcategories);
//        SubcategoriesAdapter categoryAdapter = new SubcategoriesAdapter(SubcategoriesActivity.this, SubcategoriesActivity.this, subcategoryList);
//        listView.setAdapter(categoryAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendData2NewScreen(SubcategoryInfo subcategoryInfo) {
        ArrayList<ChildCategoryInfo> childCategoryList = subcategoryInfo.getChildCategoryList();
        if (childCategoryList != null && !childCategoryList.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SUBCATEGORIES_SERIALIZABLE_OBJECT_TEXT, childCategoryList);
            startActivity(new Intent(SubcategoriesActivity.this, ChildCategoriesActivity.class).putExtra(SUBCATEGORIES_BUNDLE_TEXT, bundle));
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SUBCATEGORIES_SERIALIZABLE_OBJECT_TEXT, subcategoryInfo);
            startActivity(new Intent(SubcategoriesActivity.this, LinksLoaderActivity.class).putExtra(SUBCATEGORIES_BUNDLE_TEXT, bundle));
        }
    }
}
