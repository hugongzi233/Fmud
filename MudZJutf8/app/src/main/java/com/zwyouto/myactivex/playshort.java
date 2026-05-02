package com.zwyouto.myactivex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zwyouto.R;

import java.io.IOException;

public class playshort extends LinearLayout {
    private final LinearLayout fskills;
    private final TextView mname;
    private final attrbar neili;
    private final attrbar qixue;
    private final ImageView short_img;
    private final RelativeLayout short_img_zt;


    public playshort(Context context) {
        this(context, null);
    }

    @SuppressLint({"NewApi"})
    public playshort(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflaterLayout(context);
        this.mname = findViewById(R.id.mytextimgname);
        this.fskills = findViewById(R.id.fskills);
        this.qixue = findViewById(R.id.short_qi);
        this.neili = findViewById(R.id.short_nei);
        this.short_img_zt = findViewById(R.id.short_img_zt);
        this.short_img = findViewById(R.id.short_img);
        this.qixue.setColorA(-65536);
        this.neili.setColorA(-16776961);
    }

    public String getname() {
        return this.mname.getText().toString();
    }

    protected void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.playershortx, this, true);
    }

    public void set_img(String str) {
        try {
            this.short_img.setImageBitmap(BitmapFactory.decodeStream(getContext().getAssets().open("img/" + str + ".jpg")));
        } catch (IOException e) {
            try {
                this.short_img.setImageBitmap(BitmapFactory.decodeStream(getContext().getAssets().open("npc/" + str + ".png")));
            } catch (IOException e2) {
                this.short_img.setImageResource(R.drawable.icon);
            }
        }
    }

    public void set_img_zt_v(int i) {
        this.short_img_zt.setVisibility(i);
    }

    public void setbkcolor(String str) {
        setBackgroundColor(Color.parseColor(str));
    }

    public void setfskill(String str) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
        animationSet.addAnimation(new TranslateAnimation(0.0f, 0.0f, 0.0f, -15.0f));
        animationSet.setDuration(2000L);
        animationSet.setFillEnabled(true);
        animationSet.setFillAfter(true);
        TextView textView = new TextView(getContext());
        textView.setGravity(17);
        textView.setTextColor(-65536);
        textView.setTextSize(2, 11.0f);
        textView.setBackgroundColor(Color.parseColor("#99ffffff"));
        textView.setText(str);
        this.fskills.addView(textView);
        textView.startAnimation(animationSet);
        if (this.fskills.getChildCount() > 2) {
            this.fskills.removeViewAt(0);
        }
    }

    public void setname(String str) {
        this.mname.setText(str);
    }

    public void setnamesize(int i) {
        this.mname.setTextSize(0, i);
    }

    public void setneili(String str) throws NumberFormatException {
        String[] strArrSplit = str.split("：")[1].split("/");
        this.neili.setTextss(str);
        int i = Integer.parseInt(strArrSplit[0]);
        int i2 = Integer.parseInt(strArrSplit[1]);
        this.neili.setWB(this.neili.getWidth());
        this.neili.setWA((this.neili.getWidth() * i) / (i2 + 1));
    }

    public void setqineisize(int i) {
        this.qixue.setsize(i);
        this.neili.setsize(i);
    }

    public void setqixue(String str) throws NumberFormatException {
        String[] strArrSplit = str.split("：")[1].split("/");
        this.qixue.setTextss(str);
        int i = Integer.parseInt(strArrSplit[0]);
        int i2 = Integer.parseInt(strArrSplit[1]);
        int i3 = Integer.parseInt(strArrSplit[2]);
        this.qixue.setWB((this.qixue.getWidth() * i2) / i3);
        this.qixue.setWA((this.qixue.getWidth() * i) / i3);
    }

    public void setwh(int i, int i2) {
        getLayoutParams().width = i;
        getLayoutParams().height = i2;
    }
}