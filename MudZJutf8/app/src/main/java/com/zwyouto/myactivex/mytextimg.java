package com.zwyouto.myactivex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zwyouto.R;

public class mytextimg extends LinearLayout {
    private final ImageView mImg;
    private final TextView mname;
    private final FrameLayout mytttitle;
    private final attrbar npcqi;

    public mytextimg(Context context) {
        this(context, null);
    }

    @SuppressLint({"NewApi"})
    public mytextimg(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflaterLayout(context);
        this.mytttitle = findViewById(R.id.mytttitle);
        this.mImg = findViewById(R.id.mytextimgimg);
        this.mname = findViewById(R.id.mytextimgname);
        this.npcqi = findViewById(R.id.npcqi);
        this.mImg.setClickable(false);
    }

    public String getimgname() {
        return this.mname.getText().toString();
    }

    protected void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.mytextimgx, this, true);
    }

    public void setimgimg(Bitmap bitmap) {
        this.mImg.setImageBitmap(bitmap);
    }

    public void setimgname(String str) {
        this.mname.setText(str);
    }

    public void setmyttyy(int i) {
    }

    public void setqixue(String str) throws NumberFormatException {
        String[] strArrSplit = str.split("：")[1].split("/");
        this.npcqi.setVisibility(View.VISIBLE);
        this.npcqi.setColorA(-65536);
        int i = Integer.parseInt(strArrSplit[0]);
        int i2 = Integer.parseInt(strArrSplit[1]);
        int i3 = Integer.parseInt(strArrSplit[2]);
        this.npcqi.setWB((this.npcqi.getWidth() * i2) / i3);
        this.npcqi.setWA((this.npcqi.getWidth() * i) / i3);
    }

    public void settextcolor(int i) {
        this.mytttitle.setBackgroundColor(i);
    }

    public void settextsize(int i) {
        this.mname.setTextSize(0, i);
    }

    public void settouxiang(int i) {
        this.mImg.setImageResource(i);
    }
}