package com.zwyouto.myactivex;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zwyouto.R;

public class attrbar extends RelativeLayout {
    private TextView mA;
    private TextView mB;
    private TextView mC;
    private TextView mABC;

    public attrbar(Context context) {
        super(context);
        init(context);
    }

    public attrbar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public attrbar(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.attrbarx, this, true);
        this.mA = findViewById(R.id.TextViewA);
        this.mB = findViewById(R.id.TextViewB);
        this.mC = findViewById(R.id.TextViewC);
        this.mABC = findViewById(R.id.TextABC);
    }

    public void clearC() {
        if (this.mC != null) {
            this.mC.setBackgroundDrawable(null);
        }
    }

    public void setColorA(int color) {
        if (this.mA != null) {
            this.mA.setBackgroundColor(color);
        }
    }

    public void setColorB(int color) {
        if (this.mB != null) {
            this.mB.setBackgroundColor(color);
        }
    }

    public void setColorC(int color) {
        if (this.mC != null) {
            this.mC.setBackgroundColor(color);
        }
    }

    public void setTextss(CharSequence text) {
        if (this.mABC != null) {
            this.mABC.setText(text);
        }
    }

    public void setTextss(String text) {
        if (this.mABC != null) {
            this.mABC.setText(text);
        }
    }

    public void setWA(int width) {
        if (this.mA != null) {
            this.mA.setWidth(width);
        }
    }

    public void setWB(int width) {
        if (this.mB != null) {
            this.mB.setWidth(width);
        }
    }

    public void setsize(int size) {
        if (this.mABC != null) {
            this.mABC.setTextSize(0, size);
        }
    }

    public void setVal(int current, int max) {
        // Logic to visualize progress/value if needed, based on usage in longitemd
        // For now, we can just ensure it doesn't crash.
        // If this is a progress bar, we might want to adjust widths of A and B.
        // Assuming A is foreground/progress and B is background/total.
        // But without layout params logic, setWidth might not work as expected in RelativeLayout unless they are independent.
        // Given the decompiled code used setWidth, we'll stick to that or LayoutParams.

        // Implementation depends on layout.xml which we can't see, but assuming standard behavior:
        if (max <= 0) {
        }

        // This is a placeholder logic. Real logic depends on the total width available.
        // Since we don't have total width easily here without measurement,
        // we might just ignore or implement if we knew the parent width.
    }
}
