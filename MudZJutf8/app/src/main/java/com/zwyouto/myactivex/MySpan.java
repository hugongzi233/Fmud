package com.zwyouto.myactivex;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import com.zwyouto.R;

public class MySpan {
    private static SpannableString addSpan(Context context, SpannableString spannableString, int i, int i2, int i3, int i4, String str) throws Resources.NotFoundException {
        switch (i3) {
            case 1:
                spannableString.setSpan(new URLSpan(str), i, i2, 33);
                return spannableString;
            case 2:
                spannableString.setSpan(new ForegroundColorSpan(i4), i, i2, 33);
                return spannableString;
            case 3:
                spannableString.setSpan(new BackgroundColorSpan(i4), i, i2, 33);
                return spannableString;
            case 4:
                spannableString.setSpan(new AbsoluteSizeSpan(i4), i, i2, 33);
                return spannableString;
            case 5:
                spannableString.setSpan(new StyleSpan(i4), i, i2, 33);
                return spannableString;
            case 6:
                spannableString.setSpan(new UnderlineSpan(), i, i2, 33);
                return spannableString;
            case 7:
                Drawable drawable = context.getResources().getDrawable(Integer.valueOf(str).intValue());
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                spannableString.setSpan(new ImageSpan(drawable, 1), i - 1, i, 33);
                return spannableString;
            default:
                return spannableString;
        }
    }

    public static SpannableString toSpan(Context context, String str) throws Resources.NotFoundException {
        String[] strArrSplit = str.split("#SP%");
        SpannableString spannableString = new SpannableString(strArrSplit[0]);
        if (strArrSplit.length != 1) {
            for (String str2 : strArrSplit[1].split("@sp&")) {
                String[] strArrSplit2 = str2.split(",");
                addSpan(context, spannableString, Integer.parseInt(strArrSplit2[0]), Integer.parseInt(strArrSplit2[1]), Integer.parseInt(strArrSplit2[2]), Integer.parseInt(strArrSplit2[3]), strArrSplit2[4]);
            }
        }
        return spannableString;
    }
}