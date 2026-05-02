package com.zwyouto.myactivex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.zwyouto.R;

import java.io.IOException;

public class liulanimg extends LinearLayout {
    private String dirs;
    private String[] imgfiles;
    private final LinearLayout liulanimgs;
    private final ImageView liulannow;
    private final Context mContext;

    public liulanimg(Context context) {
        this(context, null);
    }

    @SuppressLint({"NewApi"})
    public liulanimg(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflaterLayout(context);
        this.liulanimgs = findViewById(R.id.liulanimgs);
        this.liulannow = findViewById(R.id.liulannow);
        this.mContext = context;
    }

    public String getimg() {
        return this.liulannow.getTag().toString();
    }

    protected void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.liulanimg, this, true);
    }

    public void setdir(String str) {
        this.dirs = str;
        try {
            this.imgfiles = this.mContext.getResources().getAssets().list(str);
        } catch (IOException e) {
        }
        for (int i = 0; i < this.imgfiles.length; i++) {
            ImageView imageView = new ImageView(this.mContext);
            try {
                imageView.setImageBitmap(BitmapFactory.decodeStream(this.mContext.getResources().getAssets().open(this.dirs + "/" + this.imgfiles[i])));
            } catch (IOException e2) {
            }
            imageView.setTag(this.imgfiles[i]);
            imageView.setOnClickListener(new OnClickListener() {

                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    try {
                        liulanimg.this.liulannow.setImageBitmap(BitmapFactory.decodeStream(liulanimg.this.mContext.getResources().getAssets().open(liulanimg.this.dirs + "/" + view.getTag())));
                    } catch (IOException e3) {
                    }
                    liulanimg.this.liulannow.setTag(view.getTag());
                }
            });
            this.liulanimgs.addView(imageView);
        }
    }
}