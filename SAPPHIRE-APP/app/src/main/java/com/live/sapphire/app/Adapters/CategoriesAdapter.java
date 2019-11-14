package com.live.sapphire.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.CategoryInfo;
import com.live.sapphire.app.Customized.ScreenDimensions;
import com.live.sapphire.app.R;
import com.live.sapphire.app.Utilities;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by hp on 3/20/2017.
 */

public class CategoriesAdapter extends BaseAdapter {
    private Activity mActivity;
    private Context mContext;
    private SendData mSendData;
    private LayoutInflater mLayoutInflater;
    private ArrayList<CategoryInfo> mCategoryList;

    public CategoriesAdapter(SendData sendData, Activity activity, ArrayList<CategoryInfo> categoryList) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mSendData = sendData;
        mCategoryList = categoryList;
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mCategoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.listview_layout_new, parent, false);
            holder = new ViewHolder();
            holder.categoryButton = (ImageView) view.findViewById(R.id.listview_row);
            holder.textButton = (TextView) view.findViewById(R.id.imageView_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ScreenDimensions screenDimensions = AppConfigurations.getScreenDimensions(mActivity);
        int screenWidth = screenDimensions.getWidth();
        int screenHeight = screenDimensions.getHeight();
        int screenOrientation = AppConfigurations.getScreenOrientation(mActivity);
        int specifiedHeight = screenHeight / 3;
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            specifiedHeight = 2*screenHeight / 3;
        }
        holder.categoryButton.setMinimumHeight(specifiedHeight);
        holder.categoryButton.setMaxHeight(specifiedHeight);
        holder.categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendData.sendData2NewScreen(mCategoryList.get(position));
            }
        });
        holder.textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendData.sendData2NewScreen(mCategoryList.get(position));
            }
        });
        CategoryInfo info = mCategoryList.get(position);
        holder.textButton.setText(info.getCategoryName());
        String thumbnailUrl = info.getThumbnailUrl();
        if (thumbnailUrl != null && !thumbnailUrl.equals("")) {
            thumbnailUrl = Utilities.validateThumbnailUrl(thumbnailUrl);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.categoryButton.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    holder.categoryButton.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
//                    holder.categoryButton.setImageDrawable(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                    holder.categoryButton.setImageDrawable(placeHolderDrawable);
                }
            };
            Picasso.with(mContext)
                    .load(thumbnailUrl)
                    .error(R.drawable.list_image)
                    .placeholder(R.drawable.list_image)
                    .resize(screenWidth, specifiedHeight)
                    .into(target);
        }
        return view;
    }

    private static class ViewHolder {
        ImageView categoryButton;
        TextView textButton;
    }

    public interface SendData {
        void sendData2NewScreen(CategoryInfo categoryInfo);
    }
}
