package com.zwyouto.myactivex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zwyouto.R;

public class longitemd1 extends LinearLayout {
    private final myimg img;
    private final attrbar qixue;
    private final TextView text1;
    private final TextView text2;
    private final LinearLayout txtbk;

    public String gettext1() {
        return this.text1.getText().toString();
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
                this.img.setbk2(R.drawable.hes);
                break;
            default:
                this.img.setbk2(R.drawable.cry);
                break;
        }
        this.img.setPadding(i, i, i, i);
    }

    public void setimgbk2(String str) {
        switch (Integer.parseInt(str)) {
            case 0:
                this.img.setBackgroundResource(R.drawable.wht1);
                return;
            case 1:
                this.img.setBackgroundResource(R.drawable.wht1);
                return;
            case 2:
                this.img.setBackgroundResource(R.drawable.grn1);
                return;
            case 3:
                this.img.setBackgroundResource(R.drawable.blu1);
                return;
            case 4:
                this.img.setBackgroundResource(R.drawable.zis1);
                return;
            case 5:
                this.img.setBackgroundResource(R.drawable.god1);
                return;
            case 6:
                this.img.setBackgroundResource(R.drawable.red1);
                return;
            default:
                this.img.setBackgroundResource(R.drawable.hes);
        }
    }

    public void setlpad(int i) {
        this.text1.setPadding(i, 0, 0, 0);
        this.text2.setPadding(i, 0, 0, 0);
    }

    public void setqixue(String str) {
        String[] split = str.split(":");
        int parseInt = Integer.parseInt(split[0]);
        int parseInt2 = Integer.parseInt(split[1]);
        int parseInt3 = Integer.parseInt(split[2]);
        this.qixue.setVisibility(View.VISIBLE);
        this.qixue.setWB((this.qixue.getWidth() * parseInt2) / parseInt3);
        this.qixue.setWA((this.qixue.getWidth() * parseInt) / parseInt3);
    }

    public void settext1(SpannableStringBuilder spannableStringBuilder) {
        this.text1.setText(spannableStringBuilder);
    }

    public void settext1(String str) {
        this.text1.setText(str);
    }

    public void settext1col(int i) {
        this.text1.setTextColor(i);
    }

    public void settext2(String str) {
        this.text2.setVisibility(View.VISIBLE);
        this.text2.setText(str);
    }

    public void settext2col(int i) {
        this.text2.setTextColor(i);
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

    public void settxtbk(int i) {
        this.txtbk.setBackgroundResource(i);
    }

    public longitemd1(Context context) {
        this(context, null);
    }

    @SuppressLint({"NewApi"})
    public longitemd1(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflaterLayout(context);
        this.txtbk = findViewById(R.id.txtbk);
        this.text1 = findViewById(R.id.itemtext1);
        this.text2 = findViewById(R.id.itemtext2);
        this.qixue = findViewById(R.id.hp_bar);
        this.qixue.setColorA(Color.parseColor("#99ff4500"));
        this.qixue.clearC();
        this.img = findViewById(R.id.itemimg);
        this.img.getLayoutParams().width = this.text1.getLayoutParams().height;
    }
}