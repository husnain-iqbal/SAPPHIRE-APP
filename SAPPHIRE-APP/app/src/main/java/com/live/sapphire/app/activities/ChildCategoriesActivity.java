package com.live.sapphire.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.live.sapphire.app.Adapters.ChildCategoriesAdapter;
import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.ChildCategoryInfo;
import com.live.sapphire.app.R;

import java.util.ArrayList;

public class ChildCategoriesActivity extends AppCompatActivity implements ChildCategoriesAdapter.SendData {

    public static final String CHILD_CATEGORIES_SERIALIZABLE_OBJECT_TEXT = "ChildCategoriesSerializableObject";
    public static final String CHILD_CATEGORIES_BUNDLE_TEXT = "ChildCategoriesBundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_child_categories);
        AppConfigurations.setActionbarAndPrevButton(ChildCategoriesActivity.this, toolbar);
        ListView listView = (ListView) findViewById(R.id.listView_child_categories);
        Intent intent = getIntent();
        if (intent.hasExtra(SubcategoriesActivity.SUBCATEGORIES_BUNDLE_TEXT)) {
            Bundle bundle = intent.getBundleExtra(SubcategoriesActivity.SUBCATEGORIES_BUNDLE_TEXT);
            ArrayList<ChildCategoryInfo> childCategoryInfoList = (ArrayList<ChildCategoryInfo>) bundle.getSerializable(SubcategoriesActivity.SUBCATEGORIES_SERIALIZABLE_OBJECT_TEXT);
            ChildCategoriesAdapter childCategoryAdapter = new ChildCategoriesAdapter(this, ChildCategoriesActivity.this, childCategoryInfoList);
            listView.setAdapter(childCategoryAdapter);

        }
//        ArrayList<ChildCategoryInfo> childCategoryInfoList = (ArrayList<ChildCategoryInfo>) getIntent().getBundleExtra(SubcategoriesActivity.SUBCATEGORIES_BUNDLE_TEXT).getSerializable(SubcategoriesActivity.SUBCATEGORIES_SERIALIZABLE_OBJECT_TEXT);
//        ChildCategoriesAdapter childCategoryAdapter = new ChildCategoriesAdapter(this, ChildCategoriesActivity.this, childCategoryInfoList);
//        listView.setAdapter(childCategoryAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle toolbar/actionbar arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendData2NewScreen(ChildCategoryInfo info) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CHILD_CATEGORIES_SERIALIZABLE_OBJECT_TEXT, info);
        startActivity(new Intent(ChildCategoriesActivity.this, LinksLoaderActivity.class).putExtra(CHILD_CATEGORIES_BUNDLE_TEXT, bundle));
    }
}
