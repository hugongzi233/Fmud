package com.zwyouto.myactivex;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.io.IOException;

public class ImageAdapter extends BaseAdapter {
    private String dirs;
    private String[] imgfiles;
    private final Context mContext;

    private int w = 100;

    private int h = 100;

    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.imgfiles.length;
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return Integer.valueOf(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(this.mContext);
            imageView.setLayoutParams(new AbsListView.LayoutParams(this.w, this.h));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) view;
        }
        try {
            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(this.mContext.getAssets().open(this.dirs + "/" + this.imgfiles[i])), 180, 130));
        } catch (IOException e) {
        }
        imageView.setTag(this.imgfiles[i].substring(0, this.imgfiles[i].indexOf(".")));
        return imageView;
    }

    public String getdir() {
        return this.dirs;
    }

    public void setdir(String str) {
        this.dirs = str;
        try {
            this.imgfiles = this.mContext.getAssets().list(str);
        } catch (IOException e) {
        }
    }

    public void setwh(int i, int i2) {
        this.w = i;
        this.h = i2;
    }
}

