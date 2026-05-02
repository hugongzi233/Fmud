package com.zwyouto.myactivex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class XRTextView extends androidx.appcompat.widget.AppCompatTextView {
    private final float lineSpacing;
    private final float spacing;
    private final float marginLeft;
    private final float marginRight;
    private final float paddingBottom;
    private final float paddingLeft;
    private final float paddingRight;
    private final float paddingTop;
    private final Paint paint;
    private String text;
    private final int textColor;
    private float textShowWidth;
    private final float textSize;

    public XRTextView(Context context, float textSize, int textColor, float paddingLeft, float paddingRight, float marginLeft, float marginRight) {
        super(context);
        this.paddingTop = 0.0f;
        this.paddingBottom = 5.0f;
        this.paint = new Paint();
        this.spacing = 3.0f;
        this.lineSpacing = 3.0f;
        this.textSize = textSize;
        this.textColor = textColor;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.paint.setTextSize(textSize);
        this.paint.setColor(textColor);
        this.paint.setAntiAlias(true);
    }

    public XRTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.paddingTop = 0.0f;
        this.paddingBottom = 5.0f;
        this.paint = new Paint();
        this.spacing = 3.0f;
        this.lineSpacing = 3.0f;
        this.text = attributeSet.getAttributeValue("com.zwyouto.myactivex.XRTextView", "text");
        this.textSize = attributeSet.getAttributeIntValue("com.zwyouto.myactivex.XRTextView", "textSize", 25);
        this.textColor = attributeSet.getAttributeIntValue("com.zwyouto.myactivex.XRTextView", "textColor", -16777216);
        this.paddingLeft = attributeSet.getAttributeIntValue("com.zwyouto.myactivex.XRTextView", "paddingLeft", 5);
        this.paddingRight = attributeSet.getAttributeIntValue("com.zwyouto.myactivex.XRTextView", "paddingRight", 5);
        this.marginLeft = attributeSet.getAttributeIntValue("com.zwyouto.myactivex.XRTextView", "marginLeft", 0);
        this.marginRight = attributeSet.getAttributeIntValue("com.zwyouto.myactivex.XRTextView", "marginRight", 0);
        this.paint.setTextSize(this.textSize);
        this.paint.setColor(this.textColor);
        this.paint.setAntiAlias(true);
    }

    public float getMYLineSpacing() {
        return this.lineSpacing;
    }

    public float getMYTextSize() {
        return this.textSize;
    }

    public float getSpacing() {
        return this.spacing;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        View view = (View) getParent();
        this.textShowWidth = (((((view.getMeasuredWidth() - view.getPaddingLeft()) - view.getPaddingRight()) - this.paddingLeft) - this.paddingRight) - this.marginLeft) - this.marginRight;
        int lineCount = 0;
        this.text = getText().toString();
        if (this.text == null) {
            return;
        }
        char[] charArray = this.text.toCharArray();
        float currentLineWidth = 0.0f;
        for (int i = 0; i < charArray.length; i++) {
            float charWidth = this.paint.measureText(charArray, i, 1);
            if (charArray[i] == '\n') {
                lineCount++;
                currentLineWidth = 0.0f;
            } else {
                if (this.textShowWidth - currentLineWidth < charWidth) {
                    lineCount++;
                    currentLineWidth = 0.0f;
                }
                canvas.drawText(charArray, i, 1, this.paddingLeft + currentLineWidth, this.paddingTop + ((lineCount + 1) * (this.textSize + this.lineSpacing)) + view.getPaddingTop(), this.paint);
                currentLineWidth += charWidth;
            }
        }
        setHeight((int) (((lineCount + 1) * (this.textSize + this.lineSpacing)) + view.getPaddingTop() + view.getPaddingBottom() + this.paddingTop + this.paddingBottom));
    }
}

