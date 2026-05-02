package com.zwyouto.main;

import android.annotation.SuppressLint;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import com.zwyouto.mud.BuildConfig;

/* compiled from: mudmaind.java */
@SuppressLint({"ParcelCreator"})
/* loaded from: classes.dex */
class myUSpan extends URLSpan {

    public myUSpan(String str) {
        super(str);
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View view) throws NumberFormatException {
        if (getURL().indexOf("cmds:") == 0) {
            mudsocketd.mudout(getURL().replace("cmds:", BuildConfig.FLAVOR));
            mudmaind.hudong_lay.setVisibility(View.GONE);
        } else if (getURL().indexOf("pops:") == 0) {
            mudmaind.takepopmenu(getURL().replace("pops:", BuildConfig.FLAVOR));
        } else if (getURL().indexOf("voice:") == 0) {
            mudmaind.takevoice(getURL().replace("voice:", BuildConfig.FLAVOR));
        } else {
            super.onClick(view);
        }
    }

    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setUnderlineText(true);
    }
}

