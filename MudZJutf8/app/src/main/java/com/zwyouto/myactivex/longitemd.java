package com.zwyouto.myactivex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zwyouto.R;

public class longitemd extends LinearLayout {
    private final myimg img;
    private final attrbar qixue;
    private final TextView text1;
    private final TextView text2;
    private final LinearLayout txtbk;

    public longitemd(Context context) {
        this(context, null);
    }

    @SuppressLint({"NewApi"})
    public longitemd(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflaterLayout(context);
        this.txtbk = findViewById(R.id.txtbk);
        this.text1 = findViewById(R.id.itemtext1);
        this.text2 = findViewById(R.id.itemtext2);
        this.qixue = findViewById(R.id.hp_bar);
        this.qixue.setColorA(Color.parseColor("#99ff0000"));
        this.qixue.clearC();
        this.img = findViewById(R.id.itemimg);
        this.img.getLayoutParams().width = this.text1.getLayoutParams().height;
    }

    public CharSequence gettext1() {
        return this.text1.getText();
    }

    protected void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.longitem, this, true);
    }

    public void setGravity1(int i) {
        this.text1.setGravity(i);
    }

    public void setGravity2(int i) {
        this.text2.setGravity(i);
    }

    public void setimg(Bitmap bitmap, int i) {
        this.img.setVisibility(View.VISIBLE);
        if (bitmap != null) {
            this.img.setimgbmp(bitmap);
        } else {
            this.img.setimgres(R.drawable.icon);
        }
        if (i != 0) {
            this.img.getLayoutParams().width = i;
            this.img.getLayoutParams().height = i;
        }
    }

    public void setimgbk(String str, int i) {
        switch (Integer.parseInt(str)) {
            case 0:
                this.img.setbk2(R.drawable.cry);
                break;
            case 1:
                this.img.setbk2(R.drawable.wht);
                break;
            case 2:
                this.img.setbk2(R.drawable.grn);
                break;
            case 3:
                this.img.setbk2(R.drawable.blu);
                break;
            case 4:
                this.img.setbk2(R.drawable.zis);
                break;
            case 5:
                this.img.setbk2(R.drawable.god);
                break;
            case 6:
                this.img.setbk2(R.drawable.red);
                break;
            case 7:
                // Handle case 7 if needed
                break;
        }
    }

    public void setlpad(int i) {
        this.txtbk.setPadding(i, 0, 0, 0);
    }

    public void settext1(String str) {
        this.text1.setText(str);
    }

    public void settext1c(int i) {
        this.text1.setTextColor(i);
    }

    public void settext1col(int color) {
        this.text1.setTextColor(color);
    }

    public void settext1(CharSequence text) {
        this.text1.setText(text);
    }

    public void settext2(String str) {
        this.text2.setText(str);
    }

    public void settext2c(int i) {
        this.text2.setTextColor(i);
    }

    public void settext2col(int color) {
        this.text2.setTextColor(color);
    }

    public void settext2v(int i) {
        this.text2.setVisibility(i);
    }

    public void settextsize1(int i) {
        this.text1.setTextSize(0, i);
    }

    public void settextsize2(int i) {
        this.text2.setTextSize(0, i);
    }

    public void setval(int i, int i2) {
        this.qixue.setVal(i, i2);
    }

    public void settxtbk(int resId) {
        this.txtbk.setBackgroundResource(resId);
    }

    public void setqixue(String val) {
        try {
            if (val.contains("/")) {
                String[] parts = val.split("/");
                if (parts.length == 2) {
                    int current = Integer.parseInt(parts[0]);
                    int max = Integer.parseInt(parts[1]);
                    this.qixue.setVal(current, max);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
