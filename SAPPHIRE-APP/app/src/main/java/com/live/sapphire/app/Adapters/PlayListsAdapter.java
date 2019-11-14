package com.live.sapphire.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import com.live.sapphire.app.AppConfigurations;
import com.live.sapphire.app.Customized.ImageDimensions;
import com.live.sapphire.app.Customized.PlayListsInfo;
import com.live.sapphire.app.R;
import com.live.sapphire.app.Utilities;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by hp on 3/20/2017.
 */

public class PlayListsAdapter extends BaseAdapter {
    private Activity mActivity;
    private Context mContext;
    private SendData mSendData;
    private LayoutInflater mLayoutInflater;
    private ArrayList<PlayListsInfo> mPlaylistsList;

    public PlayListsAdapter(SendData sendData, Activity activity, ArrayList<PlayListsInfo> playlistsList) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mSendData = sendData;
        mPlaylistsList = playlistsList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mPlaylistsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlaylistsList.get(position);
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
            view = mLayoutInflater.inflate(R.layout.listview_layout_customized, parent, false);
            holder = new ViewHolder();
            holder.imageButton = (ImageButton) view.findViewById(R.id.listview_row_image);
            holder.listButton = (Button) view.findViewById(R.id.listview_row_customized);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        PlayListsInfo info = mPlaylistsList.get(position);
        holder.listButton.setText(info.getTitle());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendData.sendData2NewScreen(mPlaylistsList.get(position));
            }
        });
        holder.listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendData.sendData2NewScreen(mPlaylistsList.get(position));
            }
        });
        String thumbnailUrl = info.getThumbnailUrl();
        if (thumbnailUrl != null && !thumbnailUrl.equals("")) {
            thumbnailUrl = Utilities.validateThumbnailUrl(thumbnailUrl);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    holder.imageButton.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    holder.imageButton.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
//                    holder.imageButton.setImageDrawable(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                    holder.imageButton.setImageDrawable(placeHolderDrawable);
                }
            };
            ImageDimensions imageDimensions = AppConfigurations.getImageDimensions(mActivity.getApplicationContext());
            Picasso.with(mContext)
                    .load(thumbnailUrl)
                    .resize(imageDimensions.getWidth(), imageDimensions.getHeight())
                    .into(target);
        }
        return view;
    }

    private static class ViewHolder {
        ImageButton imageButton;
        Button listButton;
    }

    public interface SendData {
        void sendData2NewScreen(PlayListsInfo playListInfo);
    }
}
