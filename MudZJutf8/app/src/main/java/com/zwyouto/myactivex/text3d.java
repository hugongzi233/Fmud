package com.zwyouto.myactivex;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zwyouto.R;

public class text3d extends RelativeLayout {

    private final TextView textViewTop;
    private final TextView textViewCenter;
    private final TextView textViewBottom;

    public text3d(Context context) {
        this(context, null);
    }

    public text3d(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R.layout.text3d, this, true);
        this.textViewTop = findViewById(R.id.top3d);
        this.textViewCenter = findViewById(R.id.center3d);
        this.textViewBottom = findViewById(R.id.bottom3d);
    }

    public void set3dStep(int step) {
        this.textViewBottom.setTop(step * 2);
        this.textViewBottom.setLeft(step * 2);
        this.textViewCenter.setTop(step);
        this.textViewCenter.setLeft(step);
    }

    public void set3dcolor(int color) {
        this.textViewTop.setTextColor(color);
    }

    public void set3dtext(CharSequence text, int size) {
        this.textViewTop.setText(text);
        this.textViewCenter.setText(text);
        this.textViewBottom.setText(text);
        this.textViewTop.setTextSize(0, size);
        this.textViewCenter.setTextSize(0, size);
        this.textViewBottom.setTextSize(0, size);
    }
}