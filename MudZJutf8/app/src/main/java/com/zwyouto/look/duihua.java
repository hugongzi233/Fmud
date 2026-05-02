package com.zwyouto.look;
import com.zwyouto.R;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.zwyouto.main.mudmaind;
import com.zwyouto.main.mudsocketd;

import java.io.IOException;


public class duihua extends Activity {
    private String[] duihuass;
    private String[] duihuasss;
    private LinearLayout duihuatexts;
    private Button gbdh;
    private TextView give_exp;
    private LinearLayout give_items;
    private TextView give_money;
    private mudsocketd mudsock;
    private EditText number;
    private Button qxdh;
    private String okmudout = "";
    private String nomudout = "";
    private final DisplayMetrics pingmu = new DisplayMetrics();

    protected int makebklv(String str) {
        switch (Integer.parseInt(str)) {
            case 1:
                return R.drawable.wht;
            case 2:
                return R.drawable.grn;
            case 3:
                return R.drawable.blu;
            case 4:
                return R.drawable.zis;
            case 5:
                return R.drawable.god;
            case 6:
                return R.drawable.red;
            default:
                return R.drawable.cry;
        }
    }

    @Override
    protected void onCreate(Bundle bundle) throws NumberFormatException {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.duihuax);
        this.mudsock = (mudsocketd) getApplication();
        getWindowManager().getDefaultDisplay().getMetrics(this.pingmu);
        this.duihuatexts = findViewById(R.id.duihuatexts);
        this.give_items = findViewById(R.id.give_items);
        this.number = findViewById(R.id.numbers);
        this.number.getLayoutParams().height = this.pingmu.widthPixels / 10;
        this.number.setTextSize(0, this.pingmu.widthPixels / 26);
        this.give_exp = findViewById(R.id.give_exp);
        this.give_exp.setTextSize(0, this.pingmu.widthPixels / 26);
        this.give_money = findViewById(R.id.give_money);
        this.give_money.setTextSize(0, this.pingmu.widthPixels / 26);
        this.gbdh = findViewById(R.id.gbdh);
        this.gbdh.getLayoutParams().height = this.pingmu.widthPixels / 9;
        this.gbdh.getLayoutParams().width = this.pingmu.widthPixels / 4;
        this.gbdh.setTextSize(0, this.pingmu.widthPixels / 26);
        this.qxdh = findViewById(R.id.qxdh);
        this.qxdh.getLayoutParams().height = this.pingmu.widthPixels / 9;
        this.qxdh.getLayoutParams().width = this.pingmu.widthPixels / 4;
        this.qxdh.setTextSize(0, this.pingmu.widthPixels / 26);
        this.duihuass = this.mudsock.getsencelong().split("\\$dh#");
        for (int i = 0; i < this.duihuass.length; i++) {
            if (this.duihuass[i].length() > 5 && this.duihuass[i].startsWith("ok11.")) {
                this.okmudout += this.duihuass[i].substring(5) + "$sock#";
            } else if (this.duihuass[i].length() > 5 && this.duihuass[i].startsWith("no11.")) {
                this.nomudout = this.duihuass[i].substring(5);
                this.qxdh.setVisibility(View.VISIBLE);
            } else if (this.duihuass[i].length() <= 5 || !this.duihuass[i].startsWith("numb.")) {
                this.duihuasss = this.duihuass[i].split("\\$br#");
                for (int i2 = 0; i2 < this.duihuasss.length; i2++) {
                    if (this.duihuasss[i2].length() > 5 && this.duihuasss[i2].startsWith("$exp#")) {
                        this.give_exp.setVisibility(View.VISIBLE);
                        this.give_exp.setText(this.duihuasss[i2].substring(5));
                    } else if (this.duihuasss[i2].length() > 5 && this.duihuasss[i2].startsWith("$obj#")) {
                        ImageView imageView = new ImageView(this);
                        imageView.setTag(this.duihuasss[i2].substring(5).split(",")[0]);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        int i3 = this.pingmu.densityDpi / 160;
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
                        layoutParams.setMargins(i3, 0, i3 * 5, i3 * 2);
                        layoutParams.height = this.pingmu.widthPixels / 6;
                        layoutParams.width = this.pingmu.widthPixels / 6;
                        imageView.setOnClickListener(view -> {
                            if (view.getTag() != null) {
                                mudsocketd.mudout("litem " + view.getTag().toString());
                            }
                        });
                        imageView.setBackgroundResource(makebklv(this.duihuasss[i2].substring(5).split(",")[2]));
                        try {
                            imageView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("item/" + this.duihuasss[i2].substring(5).split(",")[1] + ".png")));
                        } catch (IOException e) {
                            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
                        }
                        this.give_items.setVisibility(View.VISIBLE);
                        this.give_items.addView(imageView, layoutParams);
                    } else if (this.duihuasss[i2].length() <= 5 || !this.duihuasss[i2].startsWith("$god#")) {
                        TextView textView = new TextView(this);
                        int i4 = (this.pingmu.densityDpi * 4) / 160;
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams2.setMargins(0, 0, 0, i4);
                        textView.setTextSize(0, this.pingmu.widthPixels / 26);
                        if (this.duihuasss[i2].length() > 1 && this.duihuasss[i2].charAt(0) == '#') {
                            textView.setTextColor(Color.parseColor(this.duihuasss[i2].substring(0, 7)));
                            this.duihuasss[i2] = this.duihuasss[i2].substring(7);
                        }
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        if (this.duihuasss[i2].contains("\u001b")) {
                            for (String str : this.duihuasss[i2].split("\u001b")) {
                                CharSequence charSequenceTakespan = mudmaind.takespan(str);
                                if (charSequenceTakespan != null) {
                                    spannableStringBuilder.append(charSequenceTakespan);
                                }
                            }
                        } else {
                            CharSequence charSequenceTakespan2 = mudmaind.takespan(this.duihuasss[i2]);
                            if (charSequenceTakespan2 != null) {
                                spannableStringBuilder.append(charSequenceTakespan2);
                            }
                        }
                        textView.setText(spannableStringBuilder);
                        this.duihuatexts.addView(textView, layoutParams2);
                    } else {
                        this.give_money.setVisibility(View.VISIBLE);
                        this.give_money.setText(this.duihuasss[i2].substring(5));
                    }
                }
            } else {
                this.number.setVisibility(View.VISIBLE);
            }
        }
        setFinishOnTouchOutside(false);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.gbdh) {
                    if (duihua.this.number != null && (!duihua.this.number.getText().toString().equals("") || duihua.this.number.getVisibility() != View.VISIBLE)) {
                        duihua.this.finish();
                        if (duihua.this.number.getVisibility() == View.VISIBLE && !duihua.this.number.getText().toString().equals("")) {
                            duihua.this.okmudout = duihua.this.okmudout.replace("$N", duihua.this.number.getText().toString());
                        }
                        if (!duihua.this.okmudout.isEmpty()) {
                            for (int i5 = 0; i5 < duihua.this.okmudout.split("\\$sock#").length; i5++) {
                                mudsocketd.mudout(duihua.this.okmudout.split("\\$sock#")[i5]);
                            }
                        }
                    } else {
                        Toast toastMakeText = Toast.makeText(duihua.this.getBaseContext(), "锟斤拷锟斤拷锟斤拷锟斤拷为锟秸ｏ拷", Toast.LENGTH_SHORT);
                        toastMakeText.setGravity(17, 0, 0);
                        toastMakeText.show();
                    }
                } else if (id == R.id.qxdh) {
                    if (!duihua.this.nomudout.isEmpty()) {
                        mudsocketd.mudout(duihua.this.nomudout);
                    }
                    duihua.this.finish();
                }
            }
        };
        this.gbdh.setOnClickListener(onClickListener);
        this.qxdh.setOnClickListener(onClickListener);
        this.mudsock.setsencelong("锟斤拷");
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }
}