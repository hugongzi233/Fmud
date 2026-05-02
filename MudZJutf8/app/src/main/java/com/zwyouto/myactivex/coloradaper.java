package com.zwyouto.myactivex;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zwyouto.R;

public class coloradaper extends BaseAdapter {
    private final Context mContext;
    private final Integer[] mcolorids = new Integer[146];

    public coloradaper(Context context) {
        this.mContext = context;
        for (int i = 0; i < 146; i++) {
            this.mcolorids[i] = R.color.white + i;
        }
    }

    @Override
    public int getCount() {
        return this.mcolorids.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            textView = new TextView(this.mContext);
            textView.setLayoutParams(new AbsListView.LayoutParams(65, 65));
        } else {
            textView = (TextView) view;
        }
        textView.setBackgroundResource(this.mcolorids[i].intValue());
        textView.setTag(this.mcolorids[i]);
        return textView;
    }
}