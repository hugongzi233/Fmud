package com.zwyouto.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.*;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.*;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.zwyouto.R;
import com.zwyouto.look.duihua;
import com.zwyouto.mud.BuildConfig;
import com.zwyouto.myactivex.AudioRecorder;
import com.zwyouto.myactivex.attrbar;
import com.zwyouto.myactivex.longitemd;
import com.zwyouto.myactivex.text3d;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


import java.util.Objects;

@SuppressLint({"ShowToast"})
public class mudmaind extends Activity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, GestureDetector.OnGestureListener, View.OnTouchListener, Runnable {
    public static Handler mHandler1;
    protected static int progress;
    static RelativeLayout hudong_lay;
    static RelativeLayout maps_lay;
    static WebView my_web;
    private static final int MAX_TIME;
    private static final int MIX_TIME;
    private static final int RECODE_ED;
    private static int RECODE_STATE;
    private static final int RECORD_ING;
    private static final int RECORD_NO;
    private static BackgroundColorSpan bcolsp;
    private static StyleSpan bold;
    private static int bsyscolor;
    private static ForegroundColorSpan colsp;
    private static String date;
    private static int day;
    private static BackgroundColorSpan hbcolsp;
    private static ForegroundColorSpan hcolsp;
    private static SharedPreferences mdate;
    private static RelativeLayout mengban1;
    private static LinearLayout minimainmsg;
    private static mudsocketd mudsock;
    private static String night;
    private static boolean playState;
    private static Button player;
    private static TableLayout pop_menu;
    private static boolean quanjiao;
    private static float recodeTime;
    private static Button record;
    private static int scrw;
    private static AbsoluteSizeSpan sizesp;
    private static int syscolor;
    private static int syscolor2;
    private static URLSpan urlsp;
    private static MediaPlayer vmsg;
    private static double voiceValue;

    static {
        syscolor = Color.parseColor("#ddbb99");
        syscolor2 = Color.parseColor("#ddbb99");
        bsyscolor = -16777216;
        night = "ff";
        quanjiao = false;
        MAX_TIME = 0;
        MIX_TIME = 1;
        RECORD_NO = 0;
        RECORD_ING = 1;
        RECODE_ED = 2;
        RECODE_STATE = 0;
        recodeTime = 0.0f;
        voiceValue = 0.0d;
        playState = false;
        date = BuildConfig.FLAVOR;
        mHandler1 = new Handler() {
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    mudmaind.player.setText("下载中。。");
                } else if (msg.what == 2) {
                    mudmaind.player.callOnClick();
                } else if (msg.what == 3) {
                    mudmaind.player.setText("下载错误");
                }
            }
        };
    }

    private final DisplayMetrics pingmu = new DisplayMetrics();
    protected InputMethodManager imm;
    protected SharedPreferences.Editor mdateedit;
    private ScrollView allchatlay;
    private LinearLayout allchatmsg;
    private Button bbsb;
    private RelativeLayout biaotilan;
    private LinearLayout bottom_cmds_lay;
    private LinearLayout ce_m11;
    private Button ch_hi;
    private Button ch_low;
    private TextView change_long;
    private RelativeLayout chatroom;
    private Button cmds_exit;
    private String content;
    private ProgressBar dengdaijindu1;
    private Button e_exit;
    private RelativeLayout exits_ck;
    private TextView fcline2;
    private LinearLayout fexit;
    private Button game_set_bt;
    private Button gb_hudong;
    private Button gb_maps;
    private Button gb_more_txt;
    private Button gb_webs;
    private Button gbchatroom;
    private Button help_bt;
    private LinearLayout input_lay;
    private Button input_ok;
    private GestureDetector mGestureDetector;
    private Thread mainThread;
    private RelativeLayout mainlay;
    private ImageView mainmenub;
    private LinearLayout mainmenulist;
    private TextView map_txt;
    private MediaPlayer mediaPlayer;
    private Button minglingb;
    private ImageView minichat;
    private LinearLayout minichatmsg;
    private ScrollView minichatview;
    private LinearLayout minifightmsg;
    private ScrollView minifightview;
    private ScrollView minimainview;
    private TextView more_txt;
    private RelativeLayout more_txt_lay;
    private AudioRecorder mr;
    private Button mud_bt;
    private Button my_cmds_1;
    private Button my_cmds_10;
    private Button my_cmds_11;
    private Button my_cmds_12;
    private Button my_cmds_13;
    private Button my_cmds_14;
    private Button my_cmds_15;
    private Button my_cmds_16;
    private Button my_cmds_17;
    private Button my_cmds_2;
    private Button my_cmds_3;
    private Button my_cmds_4;
    private Button my_cmds_5;
    private Button my_cmds_6;
    private Button my_cmds_7;
    private Button my_cmds_8;
    private Button my_cmds_9;
    private LinearLayout my_cmds_lay;
    private LinearLayout myinfolay;
    private Button n_exit;
    private Button n_more_txt;
    private Button ne_exit;
    private EditText num_edit;
    private Button nw_exit;
    private TableLayout ob_hudong2_list;
    private TableLayout ob_hudong_list;
    private TextView ob_long;
    private RelativeLayout pfm_lay;
    private Button q_more_txt;
    private Button quitb;
    private Thread recordThread;
    private LinearLayout recorder;
    private Button s_exit;
    private Button se_exit;
    private TextView senname;
    private TextView story_text;
    private Button sw_exit;
    private LinearLayout syschatmsg;
    private TabHost tabhost5;
    private TextView topmsg;
    private LinearLayout tt_btlay;
    private TextView tt_text;
    private Button ucenterbt;
    private ScrollView user_q_lay;
    private LinearLayout user_q_list;
    private Button w_exit;
    private RelativeLayout webs_ck;
    private attrbar yinliang;
    private final Runnable ImgThread = new Runnable() {
        @SuppressLint("HandlerLeak")
        final
        Handler imgHandle = new Handler() {


            @Override // android.os.Handler
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        if (mudmaind.RECODE_STATE == mudmaind.RECORD_ING) {
                            int unused = mudmaind.RECODE_STATE = mudmaind.RECODE_ED;
                            try {
                                mudmaind.this.mr.stop();
                                double unused2 = mudmaind.voiceValue = 0.0d;
                            } catch (IOException e) {
                            }
                            if (mudmaind.recodeTime >= 1.0d) {
                                mudmaind.record.setText("录音完成\n继续录音");
                                return;
                            }
                            mudmaind.record.setText("开始录音");
                            int unused3 = mudmaind.RECODE_STATE = mudmaind.RECORD_NO;
                            return;
                        }
                        return;
                    case 1:
                        mudmaind.this.setDialogImage();
                        return;
                    default:
                }
            }
        };

        @Override // java.lang.Runnable
        public void run() {
            float unused = mudmaind.recodeTime = 0.0f;
            while (mudmaind.RECODE_STATE == mudmaind.RECORD_ING) {
                if (mudmaind.recodeTime < mudmaind.MAX_TIME || mudmaind.MAX_TIME == 0) {
                    try {
                        Thread.sleep(200L);
                        float unused2 = mudmaind.recodeTime = (float) (mudmaind.recodeTime + 0.2d);
                        if (mudmaind.RECODE_STATE == mudmaind.RECORD_ING) {
                            double unused3 = mudmaind.voiceValue = mudmaind.this.mr.getAmplitude();
                            this.imgHandle.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    this.imgHandle.sendEmptyMessage(0);
                }
            }
        }
    };
    private boolean loop = true;
    private int sleep = 100;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        private String[] tells;

        private void take_tt(String str) {
            mudmaind.this.tt_btlay.removeAllViews();
            if (str.length() > 2) {
                String[] split = str.split("\\$zj#");
                for (String str2 : split) {
                    String[] split2 = str2.split(":");
                    String str3 = split2[0];
                    String str4 = split2[1];
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    spannableStringBuilder.append(mudmaind.spantxt(str3));
                    longitemd longitemdVar = new longitemd(mudmaind.this);
                    longitemdVar.setTag(str4);
                    if (mudmaind.mdate.getString("mode", null).equals("mud")) {
                        longitemdVar.settext1col(Color.parseColor("#aaaaaa"));
                    } else {
                        longitemdVar.settext1col(mudmaind.syscolor2);
                    }
                    longitemdVar.settext1(spannableStringBuilder);
                    longitemdVar.setGravity1(17);
                    longitemdVar.setPadding((mudmaind.this.pingmu.densityDpi) / 160, 0, (mudmaind.this.pingmu.densityDpi) / 160, 0);
                    longitemdVar.settxtbk(R.drawable.hes);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -1);
                    longitemdVar.settextsize1(mudmaind.scrw / 30);
                    longitemdVar.setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View view) {
                            mudmaind.hudong_lay.setVisibility(View.GONE);
                            mudsocketd.mudout(view.getTag().toString());
                        }
                    });
                    mudmaind.this.tt_btlay.addView(longitemdVar, layoutParams);
                }
            }
        }

        private void takeexit(String str) {
            if (str.length() > 2) {
                String[] split = str.split("\\$zj#");
                for (String str2 : split) {
                    String[] split2 = str2.split(":");
                    String str3 = split2[0];
                    String str4 = split2[1];
                    String str5 = split2.length > 2 ? split2[2] : str3;
                    CharSequence spantxt = mudmaind.spantxt(str4);
                    if (str3.equals("northwest")) {
                        mudmaind.this.nw_exit.setTextColor(mudmaind.syscolor2);
                        mudmaind.this.nw_exit.setVisibility(View.VISIBLE);
                        mudmaind.this.nw_exit.setText(spantxt);
                        mudmaind.this.nw_exit.setTag(str5);
                    } else if (str3.equals("northeast")) {
                        mudmaind.this.ne_exit.setTextColor(mudmaind.syscolor2);
                        mudmaind.this.ne_exit.setVisibility(View.VISIBLE);
                        mudmaind.this.ne_exit.setText(spantxt);
                        mudmaind.this.ne_exit.setTag(str5);
                    } else if (str3.equals("southeast")) {
                        mudmaind.this.se_exit.setTextColor(mudmaind.syscolor2);
                        mudmaind.this.se_exit.setVisibility(View.VISIBLE);
                        mudmaind.this.se_exit.setText(spantxt);
                        mudmaind.this.se_exit.setTag(str5);
                    } else if (str3.equals("southwest")) {
                        mudmaind.this.sw_exit.setTextColor(mudmaind.syscolor2);
                        mudmaind.this.sw_exit.setVisibility(View.VISIBLE);
                        mudmaind.this.sw_exit.setText(spantxt);
                        mudmaind.this.sw_exit.setTag(str5);
                    } else if (str3.equals("south") || str3.equals("southup") || str3.equals("southdown")) {
                        mudmaind.this.s_exit.setTextColor(mudmaind.syscolor2);
                        mudmaind.this.s_exit.setVisibility(View.VISIBLE);
                        mudmaind.this.s_exit.setText(spantxt);
                        mudmaind.this.s_exit.setTag(str5);
                    } else if (str3.equals("north") || str3.equals("northup") || str3.equals("northdown")) {
                        mudmaind.this.n_exit.setTextColor(mudmaind.syscolor2);
                        mudmaind.this.n_exit.setVisibility(View.VISIBLE);
                        mudmaind.this.n_exit.setText(spantxt);
                        mudmaind.this.n_exit.setTag(str5);
                    } else if (str3.equals("west") || str3.equals("westup") || str3.equals("westdown")) {
                        mudmaind.this.w_exit.setTextColor(mudmaind.syscolor2);
                        mudmaind.this.w_exit.setVisibility(View.VISIBLE);
                        mudmaind.this.w_exit.setText(spantxt);
                        mudmaind.this.w_exit.setTag(str5);
                    } else if (str3.equals("east") || str3.equals("eastup") || str3.equals("eastdown")) {
                        mudmaind.this.e_exit.setTextColor(mudmaind.syscolor2);
                        mudmaind.this.e_exit.setVisibility(View.VISIBLE);
                        mudmaind.this.e_exit.setText(spantxt);
                        mudmaind.this.e_exit.setTag(str5);
                    } else {
                        TextView textView = new TextView(mudmaind.this);
                        textView.setTag(str3);
                        String string = mudmaind.mdate.getString("mode", null);
                        if (string.equals("night")) {
                            textView.setTextColor(Color.parseColor("#ddbb99"));
                            textView.setBackgroundResource(R.drawable.exitx);
                        } else {
                            textView.setTextColor(Color.parseColor(string.equals("day") ? "#000000" : "#aaaaaa"));
                            textView.setBackgroundResource(R.drawable.hes);
                        }
                        textView.setText(spantxt);
                        textView.setGravity(17);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mudmaind.scrw / 7, mudmaind.scrw / 11);
                        layoutParams.setMargins(0, (mudmaind.this.pingmu.densityDpi * 3) / 160, 0, mudmaind.this.pingmu.densityDpi / 160);
                        textView.setTextSize(0, (float) mudmaind.scrw / 35);
                        textView.setLayoutParams(layoutParams);


                        textView.setOnClickListener(view -> {
                            mudmaind.hudong_lay.setVisibility(View.GONE);
                            mudsocketd.mudout(view.getTag().toString());
                        });
                        mudmaind.this.fexit.addView(textView);
                    }
                }
            }
        }

        private void takeobacts(String str, int i) {
            int i2 = 1;
            int i3 = 3;
            int i4 = 9;
            int i5 = 30;
            if (str.charAt(0) == '$') {
                String substring = str.substring(1, str.indexOf("#"));
                String[] split = substring.split(",");
                i2 = Integer.parseInt(split[0]);
                i3 = Integer.parseInt(split[1]);
                i4 = Integer.parseInt(split[2]);
                i5 = Integer.parseInt(split[3]);
                str = str.substring(substring.length() + 2);
            }
            String[] split2 = str.split("\\$zj#");
            int i6 = 0;
            while (i6 < (split2.length / i2) + 1) {
                TableRow tableRow = new TableRow(mudmaind.mudsock);
                for (int i7 = 0; i7 < i2 && (i6 * i2) + i7 <= split2.length - 1; i7++) {
                    String[] split3 = split2[(i6 * i2) + i7].split(":");
                    longitemd longitemdVar = new longitemd(mudmaind.this);
                    longitemdVar.settext1col(mudmaind.syscolor);
                    longitemdVar.settext2col(Color.parseColor("#999999"));
                    split3[0] = split3[0].replace("$br#", "\n");
                    String[] split4 = split3[0].split("\\|");
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    if (split4[0].contains("\u001b")) {
                        for (String str2 : split4[0].split("\u001b")) {
                            CharSequence takespan = mudmaind.takespan(str2);
                            if (takespan != null) {
                                spannableStringBuilder.append(takespan);
                            }
                        }
                    } else {
                        CharSequence takespan2 = mudmaind.takespan(split4[0]);
                        if (takespan2 != null) {
                            spannableStringBuilder.append(takespan2);
                        }
                    }
                    longitemdVar.settext1(spannableStringBuilder);
                    if (split4.length > 1) {
                        longitemdVar.settext2(split4[1]);
                    }
                    longitemdVar.setTag(split3[1]);
                    longitemdVar.settextsize1(mudmaind.scrw / i5);
                    longitemdVar.settextsize2(mudmaind.scrw / i5);
                    longitemdVar.settxtbk(R.drawable.hes2);
                    longitemdVar.setlpad((mudmaind.this.pingmu.densityDpi * 2) / 160);
                    if (longitemdVar.gettext1().equals("发送语音")) {


                        longitemdVar.setOnClickListener(view -> {
                            mudmaind.this.my_cmds_lay.setVisibility(View.GONE);
                            mudmaind.this.exits_ck.setVisibility(View.GONE);
                            mudmaind.this.recorder.setVisibility(View.VISIBLE);
                            if (!view.getTag().toString().contains("$txt#")) {
                                mudmaind.hudong_lay.setVisibility(View.GONE);
                                mudmaind.this.imm.hideSoftInputFromWindow(mudmaind.this.num_edit.getWindowToken(), 0);
                                mudmaind.this.num_edit.setText(BuildConfig.FLAVOR);
                            }
                        });
                    } else {


                        longitemdVar.setOnClickListener(view -> {
                            if (view.getTag() == null) return;
                            if (view.getTag().toString().contains("\u001b020")) {
                                mudmaind.takepopmenu(view.getTag().toString().substring(4));
                                return;
                            }
                            mudsocketd.mudout(view.getTag().toString());
                            if (!view.getTag().toString().contains("$txt#")) {
                                mudmaind.hudong_lay.setVisibility(View.GONE);
                                mudmaind.this.imm.hideSoftInputFromWindow(mudmaind.this.num_edit.getWindowToken(), 0);
                                mudmaind.this.num_edit.setText(BuildConfig.FLAVOR);
                            }
                        });
                    }
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
                    layoutParams.height = mudmaind.scrw / i4;
                    layoutParams.width = mudmaind.scrw / i3;
                    layoutParams.bottomMargin = (mudmaind.this.pingmu.densityDpi * 2) / 160;
                    layoutParams.weight = 1.0f;
                    tableRow.addView(longitemdVar, layoutParams);
                }
                RelativeLayout.LayoutParams layoutParams2 = i6 == split2.length / i2 ? new RelativeLayout.LayoutParams(((mudmaind.scrw - (mudmaind.scrw / 4)) * (split2.length - (i6 * i2))) / i3, -2) : new RelativeLayout.LayoutParams(((mudmaind.scrw - (mudmaind.scrw / 4)) * i2) / i3, -2);
                if (i == 1) {
                    mudmaind.this.ob_hudong_list.addView(tableRow, layoutParams2);
                } else if (i == 2) {
                    mudmaind.this.ob_hudong2_list.addView(tableRow, layoutParams2);
                }
                i6++;
            }
        }

        private void takeobj(String str) {
            mudmaind.this.user_q_lay.setVisibility(View.VISIBLE);
            mudmaind.this.fcline2.setVisibility(View.VISIBLE);
            if (str.length() > 4) {
                String[] split = str.split("\\$zj#");
                for (String str2 : split) {
                    String[] split2 = str2.split(":");
                    longitemd longitemdVar = new longitemd(mudmaind.this);
                    longitemdVar.setTag(split2[1]);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    spannableStringBuilder.append(mudmaind.spantxt(split2[0]));
                    String string = mudmaind.mdate.getString("mode", null);
                    if (string.equals("night")) {
                        longitemdVar.settext1col(Color.parseColor("#ddbb99"));
                    } else {
                        longitemdVar.settxtbk(R.drawable.hes);
                        longitemdVar.settext1col(Color.parseColor(string.equals("day") ? "#000000" : "#aaaaaa"));
                    }
                    longitemdVar.settext1(spannableStringBuilder);
                    longitemdVar.setGravity1(17);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mudmaind.scrw / 7, mudmaind.scrw / 10);
                    longitemdVar.settextsize1(mudmaind.scrw / 35);
                    longitemdVar.setLayoutParams(layoutParams);


                    longitemdVar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mudmaind.hudong_lay.setVisibility(View.GONE);
                            mudmaind.mudsock.setmubiaoid(view.getTag().toString());
                            mudmaind.mudsock.setmubiaoname(((longitemd) view).gettext1().toString());
                            mudsocketd.mudout(view.getTag().toString());
                        }
                    });
                    mudmaind.this.user_q_list.addView(longitemdVar);
                }
            }
        }

        private void taketitle(String str) {
            CharSequence spantxt = mudmaind.spantxt(str);
            mudmaind.this.cmds_exit.setText("自定");
            mudmaind.this.senname.setTextColor(mudmaind.syscolor2);
            mudmaind.this.senname.setText(spantxt);
            if (mudmaind.mdate.getString("mode", null).equals("mud")) {
                mudmaind.this.tt_text.setTextColor(Color.parseColor("#aaaaaa"));
            } else {
                mudmaind.this.tt_text.setTextColor(mudmaind.syscolor2);
            }
            mudmaind.this.tt_text.setText(spantxt);
            mudmaind.this.fexit.removeAllViews();
            mudmaind.this.user_q_list.removeAllViews();
            mudmaind.this.nw_exit.setVisibility(View.GONE);
            mudmaind.this.ne_exit.setVisibility(View.GONE);
            mudmaind.this.sw_exit.setVisibility(View.GONE);
            mudmaind.this.se_exit.setVisibility(View.GONE);
            mudmaind.this.n_exit.setVisibility(View.GONE);
            mudmaind.this.w_exit.setVisibility(View.GONE);
            mudmaind.this.s_exit.setVisibility(View.GONE);
            mudmaind.this.e_exit.setVisibility(View.GONE);
            mudmaind.this.minifightview.setVisibility(View.GONE);
            mudmaind.this.exits_ck.setVisibility(View.VISIBLE);
            mudmaind.this.my_cmds_lay.setVisibility(View.GONE);
            mudmaind.this.recorder.setVisibility(View.GONE);
            mudmaind.this.sleep = 20;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 4 && !mudmaind.this.content.equals("版本验证失败！")) {
                mudsocketd.mudout(mudmaind.this.content);
                mudmaind.this.content = null;
                return;
            }
            if (mudmaind.this.content.equals("客户端非法") || mudmaind.this.content.equals("版本验证失败！")) {
                mudmaind.this.content = "客户端验证失败，请关闭客户端重新登陆！";
            }
            if (mudmaind.this.content.length() < 4) {
                mudmaind.this.content = null;
            } else if (mudmaind.this.content.equals("版本验证成功")) {
                mudsocketd.mudout(mudmaind.mudsock.get_ucenter_id() + "║" + mudmaind.mudsock.get_ucenter_pass() + "║" + mudmaind.mudsock.getgamekey() + "║" + mudmaind.mudsock.get_ucenter_email());
                mudmaind.this.content = null;
            } else {
                if (mudmaind.this.content.equals("连接已断开！")) {
                    mudmaind.this.loop = false;
                    new AlertDialog.Builder(mudmaind.this).setTitle("与服务器失去连接！").setPositiveButton("确定", new DialogInterface.OnClickListener() {


                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    }).show();
                }
                if (mudmaind.this.content.indexOf("\u001b[2;37;0m") == 0) {
                    AbsoluteSizeSpan unused = mudmaind.sizesp = null;
                    ForegroundColorSpan unused2 = mudmaind.colsp = null;
                    ForegroundColorSpan unused3 = mudmaind.hcolsp = null;
                    BackgroundColorSpan unused4 = mudmaind.bcolsp = null;
                    BackgroundColorSpan unused5 = mudmaind.hbcolsp = null;
                    StyleSpan unused6 = mudmaind.bold = null;
                    URLSpan unused7 = mudmaind.urlsp = null;
                    boolean unused8 = mudmaind.quanjiao = false;
                    mudmaind.this.content = mudmaind.this.content.substring(9);
                }
                String substring = mudmaind.this.content.length() < 4 ? BuildConfig.FLAVOR : mudmaind.this.content.substring(0, 4);
                if (substring.equals("\u001b002")) {
                    taketitle(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b001")) {
                    mudmaind.mengban1.setVisibility(View.GONE);
                    mudmaind.this.dengdaijindu1.setVisibility(View.GONE);
                    mudmaind.this.mainmenulist.setVisibility(View.GONE);
                    this.tells = mudmaind.this.content.substring(4).split("\\$zj#");
                    mudmaind.this.ob_hudong_list.removeAllViews();
                    mudmaind.this.ob_hudong2_list.removeAllViews();
                    mudmaind.hudong_lay.setVisibility(View.VISIBLE);
                    mudmaind.this.input_lay.setVisibility(View.VISIBLE);
                    mudmaind.this.takeoblong(this.tells[0]);
                    mudmaind.this.input_ok.setTag(this.tells[1]);
                    mudmaind.this.num_edit.requestFocus();
                    mudmaind.this.imm.showSoftInput(mudmaind.this.num_edit, 2);
                } else if (substring.equals("\u001b004")) {
                    mudmaind.this.content = mudmaind.this.content.substring(4);
                    CharSequence spantxt = mudmaind.spantxt(mudmaind.this.content);
                    mudmaind.this.story_text.setTextColor(mudmaind.syscolor);
                    mudmaind.this.story_text.setText(spantxt);
                } else if (substring.equals("\u001b003")) {
                    takeexit(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b903")) {
                    mudmaind.this.delexit(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b913")) {
                    mudmaind.this.clearexit(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b005")) {
                    takeobj(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b905")) {
                    mudmaind.this.delobj(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b006")) {
                    mudmaind.this.takebutton(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b007")) {
                    mudmaind.mudsock.setmubiaoinfo(mudmaind.this.content.substring(4));
                    mudmaind.this.ob_long.setText(BuildConfig.FLAVOR);
                    mudmaind.this.ob_hudong_list.removeAllViews();
                    mudmaind.this.ob_hudong2_list.removeAllViews();
                    mudmaind.this.input_lay.setVisibility(View.GONE);
                    mudmaind.this.takeoblong(mudmaind.this.content.substring(4));
                    mudmaind.hudong_lay.setVisibility(View.VISIBLE);
                } else if (substring.equals("\u001b008")) {
                    mudmaind.this.ob_hudong_list.removeAllViews();
                    mudmaind.hudong_lay.setVisibility(View.VISIBLE);
                    takeobacts(mudmaind.this.content.substring(4), 1);
                } else if (substring.equals("\u001b009")) {
                    mudmaind.this.ob_hudong2_list.removeAllViews();
                    mudmaind.hudong_lay.setVisibility(View.VISIBLE);
                    takeobacts(mudmaind.this.content.substring(4), 2);
                } else if (substring.equals("\u001b010")) {
                    mudmaind.mudsock.setsencelong(mudmaind.this.content.substring(4));
                    mudmaind.this.startActivity(new Intent(mudmaind.this, duihua.class));
                    mudmaind.mengban1.setVisibility(View.GONE);
                    mudmaind.this.dengdaijindu1.setVisibility(View.GONE);
                    mudmaind.this.mainmenulist.setVisibility(View.GONE);
                } else if (substring.equals("\u001b011")) {
                    mudmaind.this.takemap(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b012")) {
                    String substring2 = mudmaind.this.content.substring(4);
                    mudmaind.this.myinfolay.setVisibility(View.VISIBLE);
                    mudmaind.this.myinfolay.removeAllViews();
                    int i = 0;
                    int i2 = 22;
                    int i3 = 35;
                    if (substring2.charAt(0) == '$') {
                        String substring3 = substring2.substring(1, substring2.indexOf("#"));
                        String[] split = substring3.split(",");
                        i = Integer.parseInt(split[0]);
                        i2 = Integer.parseInt(split[2]);
                        i3 = Integer.parseInt(split[3]);
                        substring2 = substring2.substring(substring3.length() + 2);
                    }
                    String[] split2 = substring2.split("║");
                    if (i < 1) {
                        i = split2.length / 2;
                    }
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
                    layoutParams.weight = 1.0f;
                    layoutParams.bottomMargin = mudmaind.this.pingmu.densityDpi / 160;
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -1);
                    layoutParams2.rightMargin = mudmaind.this.pingmu.densityDpi / 160;
                    layoutParams2.weight = 1.0f;
                    int i4 = 0;
                    while (i4 < split2.length) {
                        LinearLayout linearLayout = new LinearLayout(mudmaind.this);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        for (int i5 = 0; i5 < i; i5++) {
                            if (i4 < split2.length) {
                                attrbar attrbarVar = new attrbar(mudmaind.this);
                                final String[] split3 = split2[i4].split(":");
                                attrbarVar.setTextss(split3[0]);
                                attrbarVar.setColorA(Color.parseColor(split3[2]));
                                attrbarVar.setsize(mudmaind.scrw / i3);
                                String[] split4 = split3[1].split("/");
                                if (split4.length == 3 && Integer.parseInt(split4[2]) > 0) {
                                    int i6 = (mudmaind.scrw - ((mudmaind.this.pingmu.densityDpi * i) / 160)) / i;
                                    attrbarVar.setWA((Integer.parseInt(split4[0]) * i6) / Integer.parseInt(split4[2]));
                                    attrbarVar.setWB((Integer.parseInt(split4[1]) * i6) / Integer.parseInt(split4[2]));
                                } else if (split4.length == 2 && Integer.parseInt(split4[1]) > 0) {
                                    int i7 = (mudmaind.scrw - ((mudmaind.this.pingmu.densityDpi * i) / 160)) / i;
                                    attrbarVar.setWA((Integer.parseInt(split4[0]) * i7) / Integer.parseInt(split4[1]));
                                    attrbarVar.setWB(i7);
                                } else if (split4.length == 1) {
                                    attrbarVar.setWA((mudmaind.scrw - ((mudmaind.this.pingmu.densityDpi * i) / 160)) / i);
                                    attrbarVar.setTextss(split3[0] + ":" + split3[1]);
                                }
                                if (split3.length == 4) {
                                    attrbarVar.setOnClickListener(new View.OnClickListener() {


                                        @Override
                                        public void onClick(View view) {
                                            mudsocketd.mudout(split3[3]);
                                        }
                                    });
                                }
                                layoutParams.height = mudmaind.scrw / i2;
                                linearLayout.addView(attrbarVar, layoutParams);
                                i4++;
                            }
                        }
                        mudmaind.this.myinfolay.addView(linearLayout, layoutParams2);
                    }
                } else if (substring.equals("\u001b013")) {
                    mudmaind.this.more_txt.setText(mudmaind.spantxt(mudmaind.this.content.substring(4).replace("$br#", "\n")));
                    mudmaind.this.more_txt_lay.setVisibility(View.VISIBLE);
                } else if (substring.equals("\u001b014")) {
                    mudsocketd.mudout(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b015")) {
                    mudmaind.this.topmsg.setVisibility(View.VISIBLE);
                    mudmaind.this.content = mudmaind.this.content.substring(4);
                    AnimationSet animationSet = new AnimationSet(true);
                    animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
                    animationSet.setDuration(5000L);
                    animationSet.setFillEnabled(true);
                    animationSet.setFillAfter(true);
                    if (mudmaind.this.dengdaijindu1.getVisibility() == View.VISIBLE) {
                        mudmaind.mengban1.setVisibility(View.GONE);
                        mudmaind.this.dengdaijindu1.setVisibility(View.GONE);
                    }
                    mudmaind.this.topmsg.setText(mudmaind.spantxt(mudmaind.this.content));
                    mudmaind.this.topmsg.startAnimation(animationSet);
                    mudmaind.this.addamsg(mudmaind.this.syschatmsg);
                    if (mudmaind.this.syschatmsg.getChildCount() > 500) {
                        mudmaind.this.syschatmsg.removeViewAt(0);
                    }
                } else if (substring.equals("\u001b016")) {
                    mudmaind.this.content = mudmaind.this.content.substring(4);
                    mudmaind.this.minifightview.setVisibility(View.VISIBLE);
                    mudmaind.this.addamsg(mudmaind.this.minifightmsg);
                    if (mudmaind.this.minifightmsg.getChildCount() > 50) {
                        mudmaind.this.minifightmsg.removeViewAt(0);
                    }
                    mudmaind.this.minifightmsg.post(new Runnable() {


                        @Override // java.lang.Runnable
                        public void run() {
                            mudmaind.this.minifightview.fullScroll(130);
                        }
                    });
                    mudmaind.this.minichatmsg.post(new Runnable() {


                        @Override // java.lang.Runnable
                        public void run() {
                            mudmaind.this.minichatview.fullScroll(130);
                        }
                    });
                    mudmaind.this.addamsg(mudmaind.this.syschatmsg);
                    if (mudmaind.this.syschatmsg.getChildCount() > 500) {
                        mudmaind.this.syschatmsg.removeViewAt(0);
                    }
                } else if (substring.equals("\u001b017")) {
                    mudmaind.this.minifightview.setVisibility(View.GONE);
                    mudmaind.this.minifightmsg.removeAllViews();
                } else if (substring.equals("\u001b020")) {
                    mudmaind.takepopmenu(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b021")) {
                    take_tt(mudmaind.this.content.substring(4));
                } else if (substring.equals("\u001b022")) {
                    mudmaind.this.content = mudmaind.this.content.substring(4);
                    String[] split5 = mudmaind.this.content.split("\\$zj#");
                    for (int i8 = 0; i8 < mudmaind.this.user_q_list.getChildCount(); i8++) {
                        if (mudmaind.this.user_q_list.getChildAt(i8).getTag().toString().equals(split5[0])) {
                            ((longitemd) mudmaind.this.user_q_list.getChildAt(i8)).setqixue(split5[1]);
                        }
                    }
                } else if (substring.equals("\u001b023")) {
                    mudmaind.this.content = mudmaind.this.content.substring(4);
                    if (mudmaind.this.content.equals("屏蔽描述")) {
                        mudmaind.this.story_text.setVisibility(View.GONE);
                        mudmaind.this.change_long.setText("显示");
                    } else if (mudmaind.mdate.getString("long_text", "open").equals("open")) {
                        mudmaind.this.story_text.setVisibility(View.VISIBLE);
                        mudmaind.this.change_long.setText("隐藏");
                    }
                } else if (substring.equals("\u001b024")) {
                    mudmaind.this.content = mudmaind.this.content.substring(4);
                    text3d text3dVar = new text3d(mudmaind.this);
                    text3dVar.set3dcolor(-65536);
                    text3dVar.set3dtext(mudmaind.spantxt(mudmaind.this.content), mudmaind.scrw / 22);
                    RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-1, -2);
                    layoutParams3.topMargin = (mudmaind.this.pfm_lay.getHeight() - 100) - (mudmaind.scrw / 22);
                    layoutParams3.leftMargin = 10;
                    mudmaind.this.pfm_lay.addView(text3dVar, layoutParams3);
                    AnimationSet animationSet2 = new AnimationSet(true);
                    TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -100.0f);
                    translateAnimation.setDuration(100L);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                    alphaAnimation.setDuration(100L);
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 2.0f, 0.0f, 2.0f, 1, 0.0f, 1, 1.0f);
                    scaleAnimation.setDuration(100L);
                    AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation2.setStartOffset(300L);
                    alphaAnimation2.setDuration(1800L);
                    TranslateAnimation translateAnimation2 = new TranslateAnimation(0.0f, 0.0f, 0.0f, -1000.0f);
                    translateAnimation2.setStartOffset(100L);
                    translateAnimation2.setDuration(1800L);
                    animationSet2.setFillAfter(true);
                    animationSet2.addAnimation(translateAnimation);
                    animationSet2.addAnimation(scaleAnimation);
                    animationSet2.addAnimation(alphaAnimation);
                    animationSet2.addAnimation(alphaAnimation2);
                    animationSet2.addAnimation(translateAnimation2);
                    animationSet2.setAnimationListener(new Animation.AnimationListener() {


                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationEnd(Animation animation) {
                            mudmaind.this.pfm_lay.removeViewAt(0);
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    text3dVar.startAnimation(animationSet2);
                } else if (substring.equals("\u001b045")) {
                    mudmaind.my_web.loadUrl(mudmaind.this.content.substring(4));
                    mudmaind.this.webs_ck.setVisibility(View.VISIBLE);
                    mudmaind.mengban1.setVisibility(View.GONE);
                    mudmaind.this.dengdaijindu1.setVisibility(View.GONE);
                } else if (substring.equals("\u001b000")) {
                    if (mudmaind.this.content.substring(4).equals("重连完毕")) {
                        mudsocketd.mudout("l");
                    }
                } else if (substring.equals("\u001b900")) {
                    String[] split6 = mudmaind.this.content.substring(4).split(":");
                    mudmaind.mudsock.shut();
                    mudmaind.mudsock.setAddr(split6[0]);
                    mudmaind.mudsock.setPort(Integer.valueOf(split6[1]).intValue());
                    mudmaind.mudsock.new_sock(mudmaind.mudsock.getAddr(), mudmaind.mudsock.getPort());
                } else if (substring.equals("\u001b999")) {
                    System.exit(0);
                } else if (substring.equals("\u001b997")) {
                    mudmaind.mudsock.set_cmdss(false);
                } else if (substring.equals("\u001b998")) {
                    mudmaind.mudsock.set_cmdss(true);
                } else if (substring.equals("\u001b006")) {
                    String substring4 = mudmaind.this.content.substring(4);
                    for (int i9 = 0; i9 < mudmaind.this.user_q_list.getChildCount(); i9++) {
                        if (mudmaind.this.user_q_list.getChildAt(i9).getTag() != null && mudmaind.this.user_q_list.getChildAt(i9).getTag().toString().equals(substring4)) {
                            mudmaind.this.user_q_list.removeViewAt(i9);
                        }
                    }
                } else if (substring.equals("\u001b100")) {
                    mudmaind.this.content = mudmaind.this.content.substring(4);
                    mudmaind.this.minichatview.setVisibility(View.VISIBLE);
                    mudmaind.this.addamsg(mudmaind.this.minichatmsg);
                    if (mudmaind.this.minichatmsg.getChildCount() > 100) {
                        mudmaind.this.minichatmsg.removeViewAt(0);
                    }
                    mudmaind.this.minichatmsg.post(new Runnable() {


                        @Override // java.lang.Runnable
                        public void run() {
                            mudmaind.this.minichatview.fullScroll(130);
                        }
                    });
                    mudmaind.this.addamsg(mudmaind.this.allchatmsg);
                    if (mudmaind.this.allchatmsg.getChildCount() > 500) {
                        mudmaind.this.allchatmsg.removeViewAt(0);
                    }
                } else {
                    mudmaind.this.sleep = 20;
                    mudmaind.this.addamsg(mudmaind.minimainmsg);
                    if (mudmaind.minimainmsg.getChildCount() > 50) {
                        mudmaind.minimainmsg.removeViewAt(0);
                    }
                    mudmaind.minimainmsg.post(new Runnable() {
                        @Override // java.lang.Runnable
                        public void run() {
                            mudmaind.this.minimainview.fullScroll(130);
                        }
                    });
                    mudmaind.this.addamsg(mudmaind.this.syschatmsg);
                    if (mudmaind.this.syschatmsg.getChildCount() > 500) {
                        mudmaind.this.syschatmsg.removeViewAt(0);
                    }
                }
                mudmaind.this.content = null;
            }
        }
    };

    static String getAmrFilePath() {
        return getAmrPath();
    }

    public static String toQJ(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = 12288;
            } else if (c[i] > ' ' && c[i] < 127) {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    public static CharSequence spantxt(String oblong) {
        SpannableStringBuilder message = new SpannableStringBuilder();
        if (oblong.contains("\u001b")) {
            String[] messages = oblong.split("\u001b");
            for (String str : messages) {
                CharSequence sptxt = takespan(str);
                if (sptxt != null) {
                    message.append(sptxt);
                }
            }
        } else {
            CharSequence sptxt2 = takespan(oblong);
            if (sptxt2 != null) {
                message.append(sptxt2);
            }
        }
        return message;
    }

    public static CharSequence takespan(String txt) {
        SpannableString spstr = null;
        int havesp = 0;
        int size = 0;
        String sp = " ";
        String url = BuildConfig.FLAVOR;
        if (txt.indexOf("[u") == 0) {
            url = txt.substring(3, txt.indexOf("]"));
        } else if (txt.indexOf("[s") == 0) {
            size = Integer.parseInt(txt.substring(3, txt.indexOf("]")));
        } else if (txt.indexOf("[2J") == 0) {
            minimainmsg.removeAllViews();
            txt = txt.replace("[2J", BuildConfig.FLAVOR);
        } else if (txt.indexOf("[H") == 0) {
            txt = txt.replace("[H", BuildConfig.FLAVOR);
        }
        if (url.equals(BuildConfig.FLAVOR) && txt.contains("m")) {
            sp = txt.substring(0, txt.indexOf("m") + 1);
        }
        if (!url.equals(BuildConfig.FLAVOR)) {
            havesp = 1;
            urlsp = new myUSpan(url);
            txt = txt.replace(txt.substring(0, txt.indexOf("]") + 1), BuildConfig.FLAVOR);
        } else if (size > 0) {
            havesp = 1;
            sizesp = new AbsoluteSizeSpan(scrw / size);
            txt = txt.replace(txt.substring(0, txt.indexOf("]") + 1), BuildConfig.FLAVOR);
        } else if (sp.equals("[0m") || sp.contains(";0m")) {
            sizesp = null;
            colsp = null;
            hcolsp = null;
            bcolsp = null;
            hbcolsp = null;
            bold = null;
            urlsp = null;
            quanjiao = false;
        } else if (sp.equals("[1m")) {
            bold = new StyleSpan(1);
        } else if (sp.equals("[9m")) {
            quanjiao = true;
        } else if (sp.indexOf("[f#") == 0) {
            colsp = new ForegroundColorSpan(Color.parseColor(sp.substring(2, sp.indexOf("m"))));
        } else if (sp.indexOf("[b#") == 0) {
            bcolsp = new BackgroundColorSpan(Color.parseColor(sp.substring(2, sp.indexOf("m"))));
        } else {
            if (day != 0) {
                if (sp.equals("[32m") || sp.equals("[33m") || sp.equals("[36m") || sp.equals("[37m")) {
                    sp = "[34m";
                } else if (sp.equals("[1;32m") || sp.equals("[1;33m") || sp.equals("[1;36m") || sp.equals("[1;37m")) {
                    sp = "[1;34m";
                }
            }
            switch (sp) {
                case "[30m":
                    colsp = new ForegroundColorSpan(Color.parseColor("#" + night + "000000"));
                    break;
                case "[31m":
                    colsp = new ForegroundColorSpan(Color.parseColor("#" + night + "aa3300"));
                    break;
                case "[32m":
                    colsp = new ForegroundColorSpan(Color.parseColor("#" + night + "00bb00"));
                    break;
                case "[33m":
                    colsp = new ForegroundColorSpan(Color.parseColor("#" + night + "eeee00"));
                    break;
                case "[34m":
                    colsp = new ForegroundColorSpan(Color.parseColor("#" + night + "0000aa"));
                    break;
                case "[35m":
                    colsp = new ForegroundColorSpan(Color.parseColor("#" + night + "aa00aa"));
                    break;
                case "[36m":
                    colsp = new ForegroundColorSpan(Color.parseColor("#" + night + "00bbbb"));
                    break;
                case "[37m":
                    colsp = new ForegroundColorSpan(Color.parseColor("#" + night + "aaaaaa"));
                    break;
                case "[1;30m":
                    hcolsp = new ForegroundColorSpan(Color.parseColor("#" + night + "000000"));
                    break;
                case "[1;31m":
                    hcolsp = new ForegroundColorSpan(Color.parseColor("#" + night + "ff3300"));
                    break;
                case "[1;32m":
                    hcolsp = new ForegroundColorSpan(Color.parseColor("#" + night + "88ff00"));
                    break;
                case "[1;33m":
                    hcolsp = new ForegroundColorSpan(Color.parseColor("#" + night + "ffff00"));
                    break;
                case "[1;34m":
                    hcolsp = new ForegroundColorSpan(Color.parseColor("#" + night + "0000ff"));
                    break;
                case "[1;35m":
                    hcolsp = new ForegroundColorSpan(Color.parseColor("#" + night + "ff00ff"));
                    break;
                case "[1;36m":
                    hcolsp = new ForegroundColorSpan(Color.parseColor("#" + night + "88ffff"));
                    break;
                case "[1;37m":
                    hcolsp = new ForegroundColorSpan(Color.parseColor("#" + night + "ffffff"));
                    break;
                case "[40m":
                    hbcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "222222"));
                    break;
                case "[41m":
                    hbcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "aa0000"));
                    break;
                case "[42m":
                    hbcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "00aa00"));
                    break;
                case "[43m":
                    hbcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "aaaa00"));
                    break;
                case "[44m":
                    hbcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "0000ff"));
                    break;
                case "[45m":
                    hbcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "aa00aa"));
                    break;
                case "[46m":
                    hbcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "00aaaa"));
                    break;
                case "[47m":
                    hbcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "aaaaaa"));
                    break;
                case "[40;1m":
                    bcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "000000"));
                    break;
                case "[41;1m":
                    bcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "ff0000"));
                    break;
                case "[42;1m":
                    bcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "00ff00"));
                    break;
                case "[43;1m":
                    bcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "ffff00"));
                    break;
                case "[44;1m":
                    bcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "0000ff"));
                    break;
                case "[45;1m":
                    bcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "ff00ff"));
                    break;
                case "[46;1m":
                    bcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "00ffff"));
                    break;
                case "[47;1m":
                    bcolsp = new BackgroundColorSpan(Color.parseColor("#" + night + "ffffff"));
                    break;
                default:
                    havesp = 1;
                    break;
            }
        }
        if (txt.length() >= 1) {
            if (havesp == 0) {
                txt = txt.replace(txt.substring(0, txt.indexOf("m") + 1), BuildConfig.FLAVOR);
            }
            if (quanjiao) {
                txt = toQJ(txt);
            }
            spstr = new SpannableString(txt);
            if (colsp != null) {
                spstr.setSpan(colsp, 0, spstr.length(), 33);
            }
            if (hcolsp != null) {
                spstr.setSpan(hcolsp, 0, spstr.length(), 33);
            }
            if (bcolsp != null) {
                spstr.setSpan(bcolsp, 0, spstr.length(), 33);
            }
            if (hbcolsp != null) {
                spstr.setSpan(hbcolsp, 0, spstr.length(), 33);
            }
            if (bold != null) {
                spstr.setSpan(bold, 0, spstr.length(), 33);
            }
            if (urlsp != null) {
                spstr.setSpan(urlsp, 0, spstr.length(), 33);
            }
            if (sizesp != null) {
                spstr.setSpan(sizesp, 0, spstr.length(), 33);
            }
        }
        return spstr;
    }

    private static String getAmrPath() {
        File file = new File(Environment.getExternalStorageDirectory(), "my/" + date);
        return file.getAbsolutePath();
    }

    public static void takepopmenu(String strs) {
        RelativeLayout.LayoutParams a;
        int r = 1;
        int w = 2;
        int h = 8;
        int s = 25;
        if (strs.charAt(0) == '$') {
            String gs = strs.substring(1, strs.indexOf("#"));
            String[] gss = gs.split(",");
            r = Integer.parseInt(gss[0]);
            w = Integer.parseInt(gss[1]);
            h = Integer.parseInt(gss[2]);
            s = Integer.parseInt(gss[3]);
            strs = strs.substring(gs.length() + 2);
        }
        pop_menu.removeAllViews();
        pop_menu.setVisibility(View.VISIBLE);
        mengban1.setVisibility(View.VISIBLE);
        String[] menus = strs.split("\\$z2#");
        for (int i = 0; i < (menus.length / r) + 1; i++) {
            TableRow ra = new TableRow(mudsock);
            for (int j = 0; j < r && (i * r) + j <= menus.length - 1; j++) {
                String[] menu = menus[(i * r) + j].split("\\|");
                TextView txt = new TextView(mudsock);
                CharSequence message = spantxt(menu[0]);
                txt.setText(message);
                txt.setTag(menu[1]);
                txt.setHeight(scrw / h);
                txt.setTextSize(0, scrw / s);
                if (mdate.getString("mode", null).equals("mud")) {
                    txt.setBackgroundResource(R.drawable.hes);
                    txt.setTextColor(Color.parseColor("#aaaaaa"));
                } else {
                    txt.setTextColor(Color.parseColor("#502015"));
                    txt.setBackgroundResource(R.drawable.buttonx);
                }
                txt.setGravity(17);


                txt.setOnClickListener(view -> {
                    if (view.getTag() == null) return;
                    mudsocketd.mudout(view.getTag().toString());
                    mudmaind.hudong_lay.setVisibility(View.GONE);
                    mudmaind.pop_menu.setVisibility(View.GONE);
                    mudmaind.mengban1.setVisibility(View.GONE);
                });
                TableRow.LayoutParams a2 = new TableRow.LayoutParams();
                a2.weight = 1.0f;
                ra.addView(txt, a2);
            }
            if (i == menus.length / r) {
                a = new RelativeLayout.LayoutParams((scrw * (menus.length - (i * r))) / w, -2);
            } else {
                a = new RelativeLayout.LayoutParams((scrw * r) / w, -2);
            }
            pop_menu.addView(ra, a);
        }
    }

    public static void takevoice(final String amr) {
        date = amr;
        File file1 = new File(getAmrPath());
        if (file1.exists()) {
            player.callOnClick();
            return;
        }
        player.setText("准备下载");

// java.lang.Runnable
        new Thread(() -> {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://mud1.foxmoe.top/voice/" + amr).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                InputStream inputStream = httpURLConnection.getInputStream();
                int contentLength = httpURLConnection.getContentLength();
                File file = new File(mudmaind.getAmrFilePath());
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int i = 0;
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = inputStream.read(bArr);
                    i += read;
                    mudmaind.progress = (int) ((i / contentLength) * 100.0f);
                    mudmaind.mHandler1.sendMessage(mudmaind.mHandler1.obtainMessage(1));
                    if (read <= 0) {
                        mudmaind.mHandler1.sendMessage(mudmaind.mHandler1.obtainMessage(2));
                        fileOutputStream.close();
                        inputStream.close();
                        return;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
            } catch (IOException e2) {
                mudmaind.mHandler1.sendMessage(mudmaind.mHandler1.obtainMessage(3, e2));
            }
        }).start();
    }

    public static void takevoice2(String amr) {
        String path;
        if (!playState) {
            File amrfile = new File(Environment.getExternalStorageDirectory(), "my/" + amr);
            if (amrfile.length() > 0) {
                path = amrfile.getAbsolutePath();
            } else {
                path = "https://mud1.foxmoe.top/voice/" + amr;
            }
            try {
                vmsg.reset();
                vmsg.setDataSource(path);
            } catch (IOException | IllegalStateException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
            vmsg.prepareAsync();
            return;
        }
        if (vmsg.isPlaying()) {
            vmsg.stop();
            playState = false;
        } else {
            playState = false;
        }
        player.setText("播放录音");
        record.setEnabled(true);
    }


    public void addamsg(LinearLayout linearLayout) {
        String str = this.content;
        TextView textView = new TextView(this);
        if (str.contains("单行") || str.equals("line_msg")) {
            textView.setBackgroundColor(syscolor);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 1);
            layoutParams.setMargins((this.pingmu.densityDpi * 3) / 160, (this.pingmu.densityDpi * 10) / 160, 0, (this.pingmu.densityDpi * 10) / 160);
            linearLayout.addView(textView, layoutParams);
            return;
        }
        textView.setPadding((this.pingmu.densityDpi * 2) / 160, 0, 0, (this.pingmu.densityDpi * 2) / 160);
        textView.setTextSize(0, (float) scrw / 28);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextColor(syscolor);
        textView.setText(spantxt(str));
        linearLayout.addView(textView);
    }

    private void init_main_face() {
        getWindowManager().getDefaultDisplay().getMetrics(this.pingmu);
        this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        scrw = this.pingmu.widthPixels;
        this.cmds_exit = findViewById(R.id.cmds_exit);
        setwh(this.cmds_exit, 7, 13);
        this.cmds_exit.setTextSize(0, (float) scrw / 31);
        this.cmds_exit.setTextColor(syscolor);
        player = findViewById(R.id.player);
        record = findViewById(R.id.record);
        this.yinliang = findViewById(R.id.yinliang);
        this.minichat = findViewById(R.id.chatl);
        this.minichat.getLayoutParams().height = scrw / 8;
        this.mainmenub = findViewById(R.id.mainmenubt);
        this.mainmenub.getLayoutParams().height = scrw / 8;
        this.my_cmds_11 = findViewById(R.id.my_cmds_11);
        this.my_cmds_11.setText(mdate.getString("my_cmds_11a", "观察"));
        this.my_cmds_11.setTextSize(0, (float) scrw / 31);
        this.my_cmds_11.setTag(mdate.getString("my_cmds_11b", "look"));
        setwh(this.my_cmds_11, 7, 13);
        this.my_cmds_12 = findViewById(R.id.my_cmds_12);
        this.my_cmds_12.setText(mdate.getString("my_cmds_12a", "物品"));
        this.my_cmds_12.setTextSize(0, (float) scrw / 31);
        this.my_cmds_12.setTag(mdate.getString("my_cmds_12b", "i"));
        this.my_cmds_13 = findViewById(R.id.my_cmds_13);
        this.my_cmds_13.setText(mdate.getString("my_cmds_13a", "技能"));
        this.my_cmds_13.setTextSize(0, (float) scrw / 31);
        this.my_cmds_13.setTag(mdate.getString("my_cmds_13b", "skills"));
        this.my_cmds_14 = findViewById(R.id.my_cmds_14);
        this.my_cmds_14.setText(mdate.getString("my_cmds_14a", "属性"));
        this.my_cmds_14.setTextSize(0, (float) scrw / 31);
        this.my_cmds_14.setTag(mdate.getString("my_cmds_14b", "score"));
        this.my_cmds_15 = findViewById(R.id.my_cmds_15);
        this.my_cmds_15.setText(mdate.getString("my_cmds_15a", "状态"));
        this.my_cmds_15.setTextSize(0, (float) scrw / 31);
        this.my_cmds_15.setTag(mdate.getString("my_cmds_15b", "hp"));
        this.my_cmds_16 = findViewById(R.id.my_cmds_16);
        this.my_cmds_16.setText(mdate.getString("my_cmds_16a", "队伍"));
        this.my_cmds_16.setTextSize(0, (float) scrw / 31);
        this.my_cmds_16.setTag(mdate.getString("my_cmds_16b", null));
        this.my_cmds_17 = findViewById(R.id.my_cmds_17);
        this.my_cmds_17.setText(mdate.getString("my_cmds_17a", "任务"));
        this.my_cmds_17.setTextSize(0, (float) scrw / 31);
        this.my_cmds_17.setTag(mdate.getString("my_cmds_17b", "quest"));
        this.my_cmds_1 = findViewById(R.id.my_cmds_1);
        this.my_cmds_1.setText(mdate.getString("my_cmds_1a", "属性"));
        this.my_cmds_1.setTextSize(0, (float) scrw / 31);
        this.my_cmds_1.setTag(mdate.getString("my_cmds_1b", "score"));
        this.my_cmds_2 = findViewById(R.id.my_cmds_2);
        this.my_cmds_2.setText(mdate.getString("my_cmds_2a", "物品"));
        this.my_cmds_2.setTextSize(0, (float) scrw / 31);
        this.my_cmds_2.setTag(mdate.getString("my_cmds_2b", "i"));
        this.my_cmds_3 = findViewById(R.id.my_cmds_3);
        this.my_cmds_3.setText(mdate.getString("my_cmds_3a", "技能"));
        this.my_cmds_3.setTextSize(0, (float) scrw / 31);
        this.my_cmds_3.setTag(mdate.getString("my_cmds_3b", "skills"));
        this.my_cmds_4 = findViewById(R.id.my_cmds_4);
        this.my_cmds_4.setText(mdate.getString("my_cmds_4a", "状态"));
        this.my_cmds_4.setTextSize(0, (float) scrw / 31);
        this.my_cmds_4.setTag(mdate.getString("my_cmds_4b", "hp"));
        this.my_cmds_5 = findViewById(R.id.my_cmds_5);
        this.my_cmds_5.setText(mdate.getString("my_cmds_5a", "队伍"));
        this.my_cmds_5.setTextSize(0, (float) scrw / 31);
        this.my_cmds_5.setTag(mdate.getString("my_cmds_5b", "team"));
        this.my_cmds_6 = findViewById(R.id.my_cmds_6);
        this.my_cmds_6.setText(mdate.getString("my_cmds_6a", "任务"));
        this.my_cmds_6.setTextSize(0, (float) scrw / 31);
        this.my_cmds_6.setTag(mdate.getString("my_cmds_6b", "quest"));
        this.my_cmds_7 = findViewById(R.id.my_cmds_7);
        this.my_cmds_7.setText(mdate.getString("my_cmds_7a", "长按"));
        this.my_cmds_7.setTextSize(0, (float) scrw / 31);
        this.my_cmds_7.setTag(mdate.getString("my_cmds_7b", null));
        this.my_cmds_8 = findViewById(R.id.my_cmds_8);
        this.my_cmds_8.setText(mdate.getString("my_cmds_8a", "长按"));
        this.my_cmds_8.setTextSize(0, (float) scrw / 31);
        this.my_cmds_8.setTag(mdate.getString("my_cmds_8b", null));
        this.my_cmds_9 = findViewById(R.id.my_cmds_9);
        this.my_cmds_9.setText(mdate.getString("my_cmds_9a", "长按"));
        this.my_cmds_9.setTextSize(0, (float) scrw / 31);
        this.my_cmds_9.setTag(mdate.getString("my_cmds_9b", null));
        this.my_cmds_10 = findViewById(R.id.my_cmds_10);
        this.my_cmds_10.setText(mdate.getString("my_cmds_10a", "长按"));
        this.my_cmds_10.setTextSize(0, (float) scrw / 31);
        this.my_cmds_10.setTag(mdate.getString("my_cmds_10b", null));
        this.q_more_txt = findViewById(R.id.q_more_txt);
        this.q_more_txt.setTextSize(0, (float) scrw / 26);
        setwh(this.q_more_txt, 6, 10);
        this.n_more_txt = findViewById(R.id.n_more_txt);
        this.n_more_txt.setTextSize(0, (float) scrw / 26);
        setwh(this.n_more_txt, 6, 10);
        this.gb_more_txt = findViewById(R.id.gb_more_txt);
        this.gb_more_txt.setTextSize(0, (float) scrw / 26);
        setwh(this.gb_more_txt, 6, 10);
        this.gb_maps = findViewById(R.id.gb_maps);
        this.gb_maps.setTextSize(0, (float) scrw / 16);
        setwh(this.gb_maps, 12, 12);
        this.gb_hudong = findViewById(R.id.gb_hudong);
        this.gb_hudong.setTextSize(0, (float) scrw / 20);
        this.gb_hudong.getLayoutParams().width = scrw / 12;
        this.gb_hudong.getLayoutParams().height = scrw / 14;
        this.nw_exit = findViewById(R.id.nw_exit);
        this.nw_exit.setTextSize(0, (float) scrw / 33);
        setwh(this.nw_exit, 5, 12);
        this.ne_exit = findViewById(R.id.ne_exit);
        this.ne_exit.setTextSize(0, (float) scrw / 33);
        setwh(this.ne_exit, 5, 12);
        this.sw_exit = findViewById(R.id.sw_exit);
        this.sw_exit.setTextSize(0, (float) scrw / 33);
        setwh(this.sw_exit, 5, 12);
        this.se_exit = findViewById(R.id.se_exit);
        this.se_exit.setTextSize(0, (float) scrw / 33);
        setwh(this.se_exit, 5, 12);
        this.n_exit = findViewById(R.id.n_exit);
        this.n_exit.setTextSize(0, (float) scrw / 33);
        setwh(this.n_exit, 5, 12);
        this.s_exit = findViewById(R.id.s_exit);
        this.s_exit.setTextSize(0, (float) scrw / 33);
        setwh(this.s_exit, 5, 12);
        this.w_exit = findViewById(R.id.w_exit);
        this.w_exit.setTextSize(0, (float) scrw / 33);
        setwh(this.w_exit, 5, 12);
        this.e_exit = findViewById(R.id.e_exit);
        this.e_exit.setTextSize(0, (float) scrw / 33);
        setwh(this.e_exit, 5, 12);
        this.input_ok = findViewById(R.id.input_ok);
        this.help_bt = findViewById(R.id.help_bt);
        setwh(this.help_bt, 4, 9);
        this.help_bt.setTextColor(Color.parseColor("#F4A460"));
        this.help_bt.setTextSize(0, (float) scrw / 22);
        this.mud_bt = findViewById(R.id.mud_bt);
        this.mud_bt.setTextSize(0, (float) scrw / 22);
        setwh(this.mud_bt, 4, 9);
        this.mud_bt.setTextColor(Color.parseColor("#F4A460"));
        this.ch_low = findViewById(R.id.ch_low);
        this.ch_low.setTextSize(0, (float) scrw / 22);
        setwh(this.ch_low, 4, 9);
        this.ch_low.setTextColor(Color.parseColor("#F4A460"));
        this.ch_hi = findViewById(R.id.ch_hi);
        this.ch_hi.setTextSize(0, (float) scrw / 22);
        setwh(this.ch_hi, 4, 9);
        this.ch_hi.setTextColor(Color.parseColor("#F4A460"));
        this.game_set_bt = findViewById(R.id.game_set_bt);
        setwh(this.game_set_bt, 4, 9);
        this.game_set_bt.setTextColor(Color.parseColor("#F4A460"));
        this.game_set_bt.setTextSize(0, (float) scrw / 22);
        this.gbchatroom = findViewById(R.id.gbchatroom);
        setwh(this.gbchatroom, 0, 10);
        this.gbchatroom.setTextSize(0, (float) scrw / 20);
        this.quitb = findViewById(R.id.quitbt);
        setwh(this.quitb, 4, 9);
        this.quitb.setTextColor(Color.parseColor("#F4A460"));
        this.quitb.setTextSize(0, (float) scrw / 22);
        this.ucenterbt = findViewById(R.id.ucenterbt);
        setwh(this.ucenterbt, 4, 9);
        this.ucenterbt.setTextColor(Color.parseColor("#F4A460"));
        this.ucenterbt.setTextSize(0, (float) scrw / 22);
        this.minglingb = findViewById(R.id.minglingbt);
        setwh(this.minglingb, 4, 9);
        this.minglingb.setTextColor(Color.parseColor("#F4A460"));
        this.minglingb.setTextSize(0, (float) scrw / 22);
        this.bbsb = findViewById(R.id.bbsbt);
        setwh(this.bbsb, 4, 9);
        this.bbsb.setTextColor(Color.parseColor("#F4A460"));
        this.bbsb.setTextSize(0, (float) scrw / 22);
        this.tt_text = findViewById(R.id.tt_text);
        this.tt_text.setTextColor(syscolor);
        this.tt_text.setTextSize(0, (float) (scrw - ((this.pingmu.densityDpi * 14) / 160)) / 18);
        this.change_long = findViewById(R.id.change_long);
        this.change_long.setTextColor(syscolor);
        this.change_long.setTextSize(0, (float) (scrw - ((this.pingmu.densityDpi * 14) / 160)) / 25);
        this.story_text = findViewById(R.id.story_text);
        this.story_text.setTextColor(syscolor);
        this.story_text.setTextSize(0, (float) (scrw - ((this.pingmu.densityDpi * 14) / 160)) / 25);
        this.story_text.setMovementMethod(LinkMovementMethod.getInstance());
        this.fcline2 = findViewById(R.id.fcline2);
        this.ob_long = findViewById(R.id.ob_long);
        this.ob_long.setTextSize(0, (float) scrw / 30);
        this.ob_long.setMovementMethod(LinkMovementMethod.getInstance());
        this.topmsg = findViewById(R.id.topmsg);
        this.topmsg.setTextSize(0, (float) scrw / 26);
        this.senname = findViewById(R.id.herename);
        setwh(this.senname, 4, 15);
        this.senname.setTextSize(0, (float) scrw / 33);
        this.senname.setTag(mdate.getString("senbt", null));
        this.map_txt = findViewById(R.id.map_text);
        this.map_txt.setTextColor(-1);
        this.map_txt.setTextSize(0, (float) scrw / 32);
        this.map_txt.setMovementMethod(LinkMovementMethod.getInstance());
        this.more_txt = findViewById(R.id.more_txt);
        this.more_txt.setTextSize(0, (float) scrw / 32);
        this.more_txt.setMovementMethod(LinkMovementMethod.getInstance());
        this.num_edit = findViewById(R.id.num_edit);
        this.bottom_cmds_lay = findViewById(R.id.bottom_cmds_lay);
        this.allchatmsg = findViewById(R.id.allchatmsg);
        this.syschatmsg = findViewById(R.id.syschatmsg);
        this.biaotilan = findViewById(R.id.biaotilan);
        this.pfm_lay = findViewById(R.id.pfm_lay);
        this.more_txt_lay = findViewById(R.id.more_txt_lay);
        this.chatroom = findViewById(R.id.chatroom);
        this.ce_m11 = findViewById(R.id.ce_m11);
        this.ce_m11.getLayoutParams().height = (scrw * 3) / 11;
        this.recorder = findViewById(R.id.recorder);
        this.my_cmds_lay = findViewById(R.id.my_cmds_lay);
        this.my_cmds_lay.getLayoutParams().height = (scrw * 3) / 11;
        pop_menu = findViewById(R.id.pop_menu);
        this.ob_hudong_list = findViewById(R.id.ob_hudong_list);
        this.ob_hudong2_list = findViewById(R.id.ob_hudong2_list);
        mengban1 = findViewById(R.id.mengban1);
        this.dengdaijindu1 = findViewById(R.id.dengdaijindu1);
        this.input_lay = findViewById(R.id.input_lay);
        this.tt_btlay = findViewById(R.id.tt_btlay);
        this.fexit = findViewById(R.id.fexit);
        this.user_q_list = findViewById(R.id.user_q_list);
        this.user_q_list.getLayoutParams().width = (scrw / 7) + ((this.pingmu.densityDpi * 2) / 160);
        this.minichatmsg = findViewById(R.id.minichatmsg);
        this.myinfolay = findViewById(R.id.myinfolay);
        minimainmsg = findViewById(R.id.minimainmsg);
        this.minifightmsg = findViewById(R.id.minifightmsg);
        this.mainmenulist = findViewById(R.id.mainmenulist);
        this.user_q_lay = findViewById(R.id.user_q_lay);
        this.user_q_lay.getLayoutParams().width = (scrw / 7) + ((this.pingmu.densityDpi * 2) / 160);
        this.allchatlay = findViewById(R.id.allchatlay);
        this.minimainview = findViewById(R.id.minimainview);
        this.minichatview = findViewById(R.id.minichatview);
        this.minichatview.getLayoutParams().height = scrw / mdate.getInt("ch_hi", 5);
        this.minifightview = findViewById(R.id.minifightview);
        hudong_lay = findViewById(R.id.hudong_lay);
        this.mainlay = findViewById(R.id.mainlay);
        maps_lay = findViewById(R.id.maps_lay);
        this.exits_ck = findViewById(R.id.exits_ck);
        this.exits_ck.getLayoutParams().height = (scrw * 4) / 13;
        this.webs_ck = findViewById(R.id.web_ck);
        this.tabhost5 = findViewById(R.id.tabhost5);
        this.tabhost5.setup();
        this.tabhost5.addTab(this.tabhost5.newTabSpec("allchat").setIndicator("聊天").setContent(R.id.allchatlay));
        this.tabhost5.addTab(this.tabhost5.newTabSpec("syschat").setIndicator("信息").setContent(R.id.syschatlay));
        updateTab(this.tabhost5);
        this.tabhost5.setOnTabChangedListener(new OnTabChangedListener());
        mudsock = (mudsocketd) getApplication();
        this.ucenterbt.setTextColor(-65536);
        getWindowManager().getDefaultDisplay().getMetrics(this.pingmu);
        scrw = this.pingmu.widthPixels;
        vmsg = new MediaPlayer();
        vmsg.setOnCompletionListener(this);
        vmsg.setOnErrorListener(this);
        vmsg.setOnBufferingUpdateListener(this);
        vmsg.setOnPreparedListener(this);
        View.OnClickListener onClickListener = new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                InputStream open = null;
                BufferedReader bufferedReader = null;
                switch (view.getId()) {
                    case R.id.gbchatroom /* 2131230733 */:
                        mudmaind.this.chatroom.setVisibility(View.GONE);
                        mudmaind.this.minichatmsg.post(new Runnable() {


                            @Override // java.lang.Runnable
                            public void run() {
                                mudmaind.this.minichatview.fullScroll(130);
                            }
                        });
                        return;
                    case R.id.herename /* 2131230755 */:
                        if (mudmaind.this.senname.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.senname.getTag().toString());
                            return;
                        }
                        return;
                    case R.id.nw_exit /* 2131230756 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        if (mudmaind.this.nw_exit.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.nw_exit.getTag().toString());
                        }
                        return;
                    case R.id.ne_exit /* 2131230757 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        if (mudmaind.this.ne_exit.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.ne_exit.getTag().toString());
                        }
                        return;
                    case R.id.sw_exit /* 2131230758 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        if (mudmaind.this.sw_exit.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.sw_exit.getTag().toString());
                        }
                        return;
                    case R.id.se_exit /* 2131230759 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        if (mudmaind.this.se_exit.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.se_exit.getTag().toString());
                        }
                        return;
                    case R.id.n_exit /* 2131230760 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        if (mudmaind.this.n_exit.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.n_exit.getTag().toString());
                        }
                        return;
                    case R.id.s_exit /* 2131230761 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        if (mudmaind.this.s_exit.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.s_exit.getTag().toString());
                        }
                        return;
                    case R.id.w_exit /* 2131230762 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        if (mudmaind.this.w_exit.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.w_exit.getTag().toString());
                        }
                        return;
                    case R.id.e_exit /* 2131230763 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        if (mudmaind.this.e_exit.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.e_exit.getTag().toString());
                        }
                        return;
                    case R.id.cmds_exit /* 2131230764 */:
                        if (mudmaind.this.cmds_exit.getText().toString().equals("自定")) {
                            mudmaind.this.cmds_exit.setText("关闭");
                        } else {
                            mudmaind.this.cmds_exit.setText("自定");
                        }
                        mudmaind.this.my_cmds_11.setText(mudmaind.mdate.getString("my_cmds_11a", "观察"));
                        mudmaind.this.my_cmds_11.setTag(mudmaind.mdate.getString("my_cmds_11b", "look"));
                        mudmaind.this.my_cmds_1.setText(mudmaind.mdate.getString("my_cmds_1a", "长按"));
                        mudmaind.this.my_cmds_1.setTag(mudmaind.mdate.getString("my_cmds_1b", null));
                        mudmaind.this.my_cmds_2.setText(mudmaind.mdate.getString("my_cmds_2a", "长按"));
                        mudmaind.this.my_cmds_2.setTag(mudmaind.mdate.getString("my_cmds_2b", null));
                        mudmaind.this.my_cmds_3.setText(mudmaind.mdate.getString("my_cmds_3a", "长按"));
                        mudmaind.this.my_cmds_3.setTag(mudmaind.mdate.getString("my_cmds_3b", null));
                        mudmaind.this.my_cmds_4.setText(mudmaind.mdate.getString("my_cmds_4a", "长按"));
                        mudmaind.this.my_cmds_4.setTag(mudmaind.mdate.getString("my_cmds_4b", null));
                        mudmaind.this.my_cmds_5.setText(mudmaind.mdate.getString("my_cmds_5a", "长按"));
                        mudmaind.this.my_cmds_5.setTag(mudmaind.mdate.getString("my_cmds_5b", null));
                        mudmaind.this.my_cmds_6.setText(mudmaind.mdate.getString("my_cmds_6a", "长按"));
                        mudmaind.this.my_cmds_6.setTag(mudmaind.mdate.getString("my_cmds_6b", null));
                        mudmaind.this.my_cmds_7.setText(mudmaind.mdate.getString("my_cmds_7a", "长按"));
                        mudmaind.this.my_cmds_7.setTag(mudmaind.mdate.getString("my_cmds_7b", null));
                        mudmaind.this.my_cmds_8.setText(mudmaind.mdate.getString("my_cmds_8a", "长按"));
                        mudmaind.this.my_cmds_8.setTag(mudmaind.mdate.getString("my_cmds_8b", null));
                        mudmaind.this.my_cmds_9.setText(mudmaind.mdate.getString("my_cmds_9a", "长按"));
                        mudmaind.this.my_cmds_9.setTag(mudmaind.mdate.getString("my_cmds_9b", null));
                        mudmaind.this.my_cmds_10.setText(mudmaind.mdate.getString("my_cmds_10a", "长按"));
                        mudmaind.this.my_cmds_10.setTag(mudmaind.mdate.getString("my_cmds_10b", null));
                        if (mudmaind.this.my_cmds_lay.getVisibility() == View.VISIBLE || mudmaind.this.recorder.getVisibility() == View.VISIBLE) {
                            mudmaind.this.exits_ck.setVisibility(View.VISIBLE);
                            mudmaind.this.recorder.setVisibility(View.GONE);
                            mudmaind.this.my_cmds_lay.setVisibility(View.GONE);
                            return;
                        }
                        mudmaind.this.exits_ck.setVisibility(View.GONE);
                        mudmaind.this.recorder.setVisibility(View.GONE);
                        mudmaind.this.my_cmds_lay.setVisibility(View.VISIBLE);
                        return;
                    case R.id.my_cmds_11 /* 2131230765 */:
                        if (mudmaind.this.my_cmds_11.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_11.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_1 /* 2131230768 */:
                        if (mudmaind.this.my_cmds_1.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_1.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_2 /* 2131230769 */:
                        if (mudmaind.this.my_cmds_2.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_2.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_3 /* 2131230770 */:
                        if (mudmaind.this.my_cmds_3.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_3.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_4 /* 2131230771 */:
                        if (mudmaind.this.my_cmds_4.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_4.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_5 /* 2131230772 */:
                        if (mudmaind.this.my_cmds_5.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_5.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_6 /* 2131230774 */:
                        if (mudmaind.this.my_cmds_6.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_6.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_7 /* 2131230775 */:
                        if (mudmaind.this.my_cmds_7.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_7.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_8 /* 2131230776 */:
                        if (mudmaind.this.my_cmds_8.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_8.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_9 /* 2131230777 */:
                        if (mudmaind.this.my_cmds_9.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_9.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_10 /* 2131230778 */:
                        if (mudmaind.this.my_cmds_10.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_10.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.player /* 2131230781 */:
                        if (mudmaind.playState) {
                            if (mudmaind.this.mediaPlayer.isPlaying()) {
                                mudmaind.this.mediaPlayer.stop();
                                boolean unused = mudmaind.playState = false;
                            } else {
                                boolean unused2 = mudmaind.playState = false;
                            }
                            mudmaind.player.setText("播放录音");
                            mudmaind.record.setEnabled(true);
                            return;
                        }
                        mudmaind.this.mediaPlayer = new MediaPlayer();
                        try {
                            mudmaind.this.mediaPlayer.setDataSource(mudmaind.getAmrFilePath());
                            mudmaind.this.mediaPlayer.prepare();
                            mudmaind.this.mediaPlayer.start();
                            mudmaind.player.setText("正在播放");
                            mudmaind.record.setEnabled(false);
                            boolean unused3 = mudmaind.playState = true;
                            mudmaind.this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


                                @Override // android.media.MediaPlayer.OnCompletionListener
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    if (mudmaind.playState) {
                                        mudmaind.player.setText("播放声音");
                                        mudmaind.record.setEnabled(true);
                                        boolean unused4 = mudmaind.playState = false;
                                    }
                                }
                            });
                            return;
                        } catch (IOException e) {
                            return;
                        } catch (IllegalArgumentException e2) {
                            return;
                        } catch (IllegalStateException e3) {
                            return;
                        }
                    case R.id.record /* 2131230782 */:
                        if (mudmaind.RECODE_STATE != mudmaind.RECORD_ING) {
                            String unused4 = mudmaind.date = System.currentTimeMillis() + ".amr";
                            mudmaind.this.mr = new AudioRecorder(mudmaind.date);
                            int unused5 = mudmaind.RECODE_STATE = mudmaind.RECORD_ING;
                            try {
                                mudmaind.this.mr.start();
                            } catch (IOException e4) {
                            }
                            mudmaind.player.setEnabled(false);
                            mudmaind.player.setText("录音中");
                            mudmaind.record.setText("结束并发送");
                            mudmaind.this.mythread();
                            return;
                        }
                        int unused6 = mudmaind.RECODE_STATE = mudmaind.RECODE_ED;
                        try {
                            mudmaind.this.mr.stop();
                            double unused7 = mudmaind.voiceValue = 0.0d;
                            mudmaind.this.setDialogImage();
                        } catch (IOException e5) {
                        }
                        if (mudmaind.recodeTime >= mudmaind.MIX_TIME) {
                            mudmaind.player.setEnabled(true);
                            mudmaind.player.setText("录音结束");
                            mudmaind.record.setText("开始录音");
                            new FileUploadTask().execute();
                            return;
                        }
                        mudmaind.player.setEnabled(true);
                        mudmaind.player.setText("录音失败");
                        mudmaind.record.setText("开始录音");
                        int unused8 = mudmaind.RECODE_STATE = mudmaind.RECORD_NO;
                        new File(Environment.getExternalStorageDirectory(), "my/" + mudmaind.date).delete();
                        return;
                    case R.id.change_long /* 2131230861 */:
                        if (mudmaind.this.story_text.getVisibility() == View.VISIBLE) {
                            mudmaind.this.story_text.setVisibility(View.GONE);
                            mudmaind.this.change_long.setText("显示");
                            mudmaind.this.mdateedit.putString("long_text", "close");
                        } else {
                            mudmaind.this.story_text.setVisibility(View.VISIBLE);
                            mudmaind.this.change_long.setText("隐藏");
                            mudmaind.this.mdateedit.putString("long_text", "open");
                        }
                        mudmaind.this.mdateedit.commit();
                        return;
                    case R.id.gb_maps /* 2131230885 */:
                        mudmaind.maps_lay.setVisibility(View.GONE);
                        mudmaind.this.map_txt.setText(BuildConfig.FLAVOR);
                        return;
                    case R.id.gb_more_txt /* 2131230888 */:
                        mudmaind.this.more_txt_lay.setVisibility(View.GONE);
                        mudsocketd.mudout("q");
                        return;
                    case R.id.q_more_txt /* 2131230890 */:
                        mudsocketd.mudout("b");
                        return;
                    case R.id.n_more_txt /* 2131230891 */:
                        mudsocketd.mudout("n");
                        return;
                    case R.id.mengban1 /* 2131230892 */:
                        if (mudmaind.this.dengdaijindu1.getVisibility() == View.GONE) {
                            mudmaind.mengban1.setVisibility(View.GONE);
                            mudmaind.this.mainmenulist.setVisibility(View.GONE);
                            mudmaind.pop_menu.setVisibility(View.GONE);
                            return;
                        }
                        return;
                    case R.id.game_set_bt /* 2131230894 */:
                        int unused9 = mudmaind.syscolor = Color.parseColor("#502515");
                        int unused10 = mudmaind.syscolor2 = Color.parseColor("#502515");
                        String unused11 = mudmaind.night = "ff";
                        mudmaind.minimainmsg.removeAllViews();
                        mudmaind.this.minichatmsg.removeAllViews();
                        mudmaind.this.mainlay.setBackgroundResource(R.drawable.bk1);
                        mudmaind.hudong_lay.setBackgroundResource(R.drawable.bk1);
                        int unused12 = mudmaind.bsyscolor = Color.parseColor("#ffffff");
                        mudmaind.this.my_cmds_11.setTextColor(mudmaind.syscolor);
                        mudmaind.this.cmds_exit.setTextColor(mudmaind.syscolor);
                        mudmaind.this.input_ok.setTextColor(mudmaind.syscolor);
                        mudmaind.this.biaotilan.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.nw_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.ne_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.sw_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.se_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.n_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.s_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.w_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.e_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.senname.setBackgroundResource(R.drawable.hes);
                        mudsocketd.mudout("mycmds");
                        mudsocketd.mudout("look");
                        mudmaind.this.mdateedit.putString("mode", "day");
                        mudmaind.this.mdateedit.commit();
                        int unused13 = mudmaind.day = 1;
                        return;
                    case R.id.help_bt /* 2131230895 */:
                        int unused14 = mudmaind.syscolor = Color.parseColor("#ffffff");
                        int unused15 = mudmaind.syscolor2 = Color.parseColor("#ffffff");
                        String unused16 = mudmaind.night = "ff";
                        mudmaind.minimainmsg.removeAllViews();
                        mudmaind.this.minichatmsg.removeAllViews();
                        mudmaind.this.mainlay.setBackgroundResource(R.drawable.bk2);
                        mudmaind.hudong_lay.setBackgroundResource(R.drawable.bk2);
                        int unused17 = mudmaind.bsyscolor = -16777216;
                        mudmaind.this.my_cmds_11.setTextColor(mudmaind.syscolor);
                        mudmaind.this.cmds_exit.setTextColor(mudmaind.syscolor);
                        mudmaind.this.input_ok.setTextColor(mudmaind.syscolor);
                        mudmaind.this.biaotilan.setBackgroundResource(R.drawable.bar);
                        mudmaind.this.nw_exit.setBackgroundResource(R.drawable.nwx);
                        mudmaind.this.ne_exit.setBackgroundResource(R.drawable.nex);
                        mudmaind.this.sw_exit.setBackgroundResource(R.drawable.swx);
                        mudmaind.this.se_exit.setBackgroundResource(R.drawable.sex);
                        mudmaind.this.n_exit.setBackgroundResource(R.drawable.northx);
                        mudmaind.this.s_exit.setBackgroundResource(R.drawable.southx);
                        mudmaind.this.w_exit.setBackgroundResource(R.drawable.westx);
                        mudmaind.this.e_exit.setBackgroundResource(R.drawable.eastx);
                        mudmaind.this.senname.setBackgroundResource(R.drawable.exitx);
                        mudsocketd.mudout("mycmds");
                        mudsocketd.mudout("look");
                        mudmaind.this.mdateedit.putString("mode", "night");
                        mudmaind.this.mdateedit.commit();
                        int unused18 = mudmaind.day = 0;
                        return;
                    case R.id.mud_bt /* 2131230896 */:
                        int unused19 = mudmaind.syscolor = Color.parseColor("#aaaaaa");
                        int unused20 = mudmaind.syscolor2 = Color.parseColor("#aaaaaa");
                        String unused21 = mudmaind.night = "ff";
                        mudmaind.minimainmsg.removeAllViews();
                        mudmaind.this.minichatmsg.removeAllViews();
                        mudmaind.this.mainlay.setBackgroundResource(R.drawable.huashan);
                        mudmaind.hudong_lay.setBackgroundResource(R.drawable.bg2);
                        int unused22 = mudmaind.bsyscolor = -16777216;
                        mudmaind.this.my_cmds_11.setTextColor(mudmaind.syscolor);
                        mudmaind.this.cmds_exit.setTextColor(mudmaind.syscolor);
                        mudmaind.this.input_ok.setTextColor(mudmaind.syscolor);
                        mudmaind.this.biaotilan.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.nw_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.ne_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.sw_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.se_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.n_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.s_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.w_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.e_exit.setBackgroundResource(R.drawable.hes);
                        mudmaind.this.senname.setBackgroundResource(R.drawable.hes);
                        mudsocketd.mudout("mycmds");
                        mudsocketd.mudout("look");
                        mudmaind.this.mdateedit.putString("mode", "mud");
                        mudmaind.this.mdateedit.commit();
                        int unused23 = mudmaind.day = 0;
                        return;
                    case R.id.ch_low /* 2131230897 */:
                        mudmaind.this.minichatview.getLayoutParams().height = mudmaind.scrw / 8;
                        mudmaind.this.content = "切换至单行聊天拉";
                        mudmaind.this.addamsg(mudmaind.this.minichatmsg);
                        mudmaind.this.minichatmsg.post(new Runnable() {


                            @Override // java.lang.Runnable
                            public void run() {
                                mudmaind.this.minichatview.fullScroll(130);
                            }
                        });
                        mudmaind.this.mdateedit.putInt("ch_hi", 8);
                        mudmaind.this.mdateedit.commit();
                        return;
                    case R.id.ch_hi /* 2131230898 */:
                        mudmaind.this.minichatview.getLayoutParams().height = mudmaind.scrw / 3;
                        mudmaind.this.content = "切换至多行聊天拉~";
                        mudmaind.this.addamsg(mudmaind.this.minichatmsg);
                        mudmaind.this.minichatmsg.post(new Runnable() {


                            @Override // java.lang.Runnable
                            public void run() {
                                mudmaind.this.minichatview.fullScroll(130);
                            }
                        });
                        mudmaind.this.mdateedit.putInt("ch_hi", 3);
                        mudmaind.this.mdateedit.commit();
                        return;
                    case R.id.minglingbt /* 2131230899 */:
                        mudmaind.mengban1.setVisibility(View.GONE);
                        mudmaind.this.mainmenulist.setVisibility(View.GONE);
                        mudmaind.this.chatroom.setVisibility(View.VISIBLE);
                        mudmaind.this.allchatmsg.post(new Runnable() {


                            @Override // java.lang.Runnable
                            public void run() {
                                mudmaind.this.allchatlay.fullScroll(130);
                            }
                        });
                        return;
                    case R.id.ucenterbt /* 2131230901 */:
                        String str = BuildConfig.FLAVOR;
                        try {
                            open = mudmaind.this.getResources().getAssets().open("pay_html.txt");
                            bufferedReader = new BufferedReader(new InputStreamReader(open));
                        } catch (IOException e6) {
                        }
                        while (true) {
                            String readLine = null;
                            try {
                                readLine = bufferedReader.readLine();

                                if (readLine == null) {
                                    mudmaind.my_web.loadDataWithBaseURL(BuildConfig.FLAVOR, str.replace("GUID", mudmaind.mudsock.get_ucenter_id()).replace("GPART", mudmaind.mudsock.getgamepart()), "text/html", "utf-8", BuildConfig.FLAVOR);
                                    open.close();
                                    mudmaind.this.webs_ck.setVisibility(View.VISIBLE);
                                    mudmaind.this.mainmenulist.setVisibility(View.GONE);
                                    mudmaind.mengban1.setVisibility(View.GONE);
                                    return;
                                }
                                str = str + readLine + "\n";
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    case R.id.bbsbt /* 2131230902 */:
                        mudmaind.my_web.loadUrl("https://mud1.foxmoe.top");
                        mudmaind.this.mainmenulist.setVisibility(View.GONE);
                        mudmaind.mengban1.setVisibility(View.GONE);
                        mudmaind.this.webs_ck.setVisibility(View.VISIBLE);
                        return;
                    case R.id.quitbt /* 2131230903 */:
                        mudmaind.this.mainmenulist.setVisibility(View.GONE);
                        mudmaind.mengban1.setVisibility(View.GONE);
                        new AlertDialog.Builder(mudmaind.this).setTitle("确认要退出么？").setIcon(R.drawable.command).setPositiveButton("退出", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mudsocketd.mudout("quit");
                                System.exit(0);
                            }
                        }).setNegativeButton("留下来", null).show();
                        return;
                    case R.id.chatl /* 2131230908 */:
                        mudmaind.this.ob_hudong_list.removeAllViews();
                        mudmaind.this.ob_hudong2_list.removeAllViews();
                        mudmaind.hudong_lay.setVisibility(View.VISIBLE);
                        mudmaind.this.input_lay.setVisibility(View.VISIBLE);
                        mudmaind.this.takeoblong("请输入指令：");
                        mudmaind.this.input_ok.setTag("$txt#");
                        mudmaind.this.num_edit.requestFocus();
                        mudmaind.this.imm.showSoftInput(mudmaind.this.num_edit, 2);
                        return;
                    case R.id.my_cmds_12 /* 2131230909 */:
                        if (mudmaind.this.my_cmds_12.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_12.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_13 /* 2131230910 */:
                        if (mudmaind.this.my_cmds_13.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_13.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_14 /* 2131230911 */:
                        if (mudmaind.this.my_cmds_14.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_14.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_15 /* 2131230912 */:
                        if (mudmaind.this.my_cmds_15.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_15.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_16 /* 2131230913 */:
                        if (mudmaind.this.my_cmds_16.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_16.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.my_cmds_17 /* 2131230914 */:
                        if (mudmaind.this.my_cmds_17.getTag() != null) {
                            mudsocketd.mudout(mudmaind.this.my_cmds_17.getTag().toString());
                        }
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        return;
                    case R.id.mainmenubt /* 2131230915 */:
                        if (mudmaind.this.mainmenulist.getVisibility() == View.GONE) {
                            mudmaind.this.mainmenulist.setVisibility(View.VISIBLE);
                            mudmaind.mengban1.setVisibility(View.VISIBLE);
                            return;
                        }
                        mudmaind.mengban1.setVisibility(View.GONE);
                        mudmaind.this.mainmenulist.setVisibility(View.GONE);
                        return;
                    case R.id.input_ok /* 2131230934 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        mudmaind.this.input_lay.setVisibility(View.GONE);
                        if (mudmaind.this.input_ok.getTag() != null) {
                            if (mudmaind.this.input_ok.getTag().toString().contains("$txt#")) {
                                mudsocketd.mudout(mudmaind.this.input_ok.getTag().toString().replace("$txt#", mudmaind.this.num_edit.getText().toString()));
                            } else {
                                mudsocketd.mudout(mudmaind.this.input_ok.getTag().toString() + " " + mudmaind.this.num_edit.getText().toString());
                            }
                        }
                        mudmaind.this.imm.hideSoftInputFromWindow(mudmaind.this.num_edit.getWindowToken(), 0);
                        mudmaind.this.num_edit.setText(BuildConfig.FLAVOR);
                        return;
                    case R.id.gb_hudong /* 2131230940 */:
                        mudmaind.hudong_lay.setVisibility(View.GONE);
                        mudmaind.this.input_lay.setVisibility(View.GONE);
                        mudmaind.this.imm.hideSoftInputFromWindow(mudmaind.this.num_edit.getWindowToken(), 0);
                        mudmaind.this.num_edit.setText(BuildConfig.FLAVOR);
                        mudmaind.this.ob_long.setText(BuildConfig.FLAVOR);
                        mudmaind.this.ob_hudong_list.removeAllViews();
                        mudmaind.this.ob_hudong2_list.removeAllViews();
                        return;
                    default:
                }
            }
        };
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {


            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(final View view) {
                final EditText editText = new EditText(mudmaind.this);
                if (view.getTag() != null) {
                    editText.setText(((TextView) view).getText() + "," + view.getTag().toString());
                }
                switch (view.getId()) {
                    case R.id.herename /* 2131230755 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                view.setTag(editText.getText().toString());
                                mudmaind.this.mdateedit.putString("senbt", editText.getText().toString());
                                mudmaind.this.mdateedit.commit();
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_11 /* 2131230765 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().indexOf(",") != -1) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_11a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_11b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_1 /* 2131230768 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_1a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_1b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_2 /* 2131230769 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_2a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_2b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_3 /* 2131230770 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_3a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_3b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_4 /* 2131230771 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_4a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_4b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_5 /* 2131230772 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_5a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_5b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_6 /* 2131230774 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_6a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_6b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_7 /* 2131230775 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_7a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_7b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_8 /* 2131230776 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_8a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_8b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_9 /* 2131230777 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_9a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_9b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_10 /* 2131230778 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_10a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_10b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_12 /* 2131230909 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_12a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_12b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_13 /* 2131230910 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_13a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_13b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_14 /* 2131230911 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_14a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_14b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_15 /* 2131230912 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_15a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_15b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_16 /* 2131230913 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_16a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_16b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    case R.id.my_cmds_17 /* 2131230914 */:
                        new AlertDialog.Builder(mudmaind.this).setTitle("请输入快捷键名称+,+指令：").setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (editText.getText().toString().contains(",")) {
                                    ((TextView) view).setText(editText.getText().toString().split(",")[0]);
                                    view.setTag(editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.putString("my_cmds_17a", editText.getText().toString().split(",")[0]);
                                    mudmaind.this.mdateedit.putString("my_cmds_17b", editText.getText().toString().split(",")[1]);
                                    mudmaind.this.mdateedit.commit();
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                        return false;
                    default:
                        return false;
                }
            }
        };
        this.senname.setOnLongClickListener(onLongClickListener);
        this.my_cmds_11.setOnLongClickListener(onLongClickListener);
        this.my_cmds_12.setOnLongClickListener(onLongClickListener);
        this.my_cmds_13.setOnLongClickListener(onLongClickListener);
        this.my_cmds_14.setOnLongClickListener(onLongClickListener);
        this.my_cmds_15.setOnLongClickListener(onLongClickListener);
        this.my_cmds_16.setOnLongClickListener(onLongClickListener);
        this.my_cmds_17.setOnLongClickListener(onLongClickListener);
        this.my_cmds_1.setOnLongClickListener(onLongClickListener);
        this.my_cmds_2.setOnLongClickListener(onLongClickListener);
        this.my_cmds_3.setOnLongClickListener(onLongClickListener);
        this.my_cmds_4.setOnLongClickListener(onLongClickListener);
        this.my_cmds_5.setOnLongClickListener(onLongClickListener);
        this.my_cmds_6.setOnLongClickListener(onLongClickListener);
        this.my_cmds_7.setOnLongClickListener(onLongClickListener);
        this.my_cmds_8.setOnLongClickListener(onLongClickListener);
        this.my_cmds_9.setOnLongClickListener(onLongClickListener);
        this.my_cmds_10.setOnLongClickListener(onLongClickListener);
        player.setOnClickListener(onClickListener);
        record.setOnClickListener(onClickListener);
        this.input_ok.setOnClickListener(onClickListener);
        this.cmds_exit.setOnClickListener(onClickListener);
        this.my_cmds_11.setOnClickListener(onClickListener);
        this.my_cmds_12.setOnClickListener(onClickListener);
        this.my_cmds_13.setOnClickListener(onClickListener);
        this.my_cmds_14.setOnClickListener(onClickListener);
        this.my_cmds_15.setOnClickListener(onClickListener);
        this.my_cmds_16.setOnClickListener(onClickListener);
        this.my_cmds_17.setOnClickListener(onClickListener);
        this.my_cmds_1.setOnClickListener(onClickListener);
        this.my_cmds_2.setOnClickListener(onClickListener);
        this.my_cmds_3.setOnClickListener(onClickListener);
        this.my_cmds_4.setOnClickListener(onClickListener);
        this.my_cmds_5.setOnClickListener(onClickListener);
        this.my_cmds_6.setOnClickListener(onClickListener);
        this.my_cmds_7.setOnClickListener(onClickListener);
        this.my_cmds_8.setOnClickListener(onClickListener);
        this.my_cmds_9.setOnClickListener(onClickListener);
        this.my_cmds_10.setOnClickListener(onClickListener);
        this.q_more_txt.setOnClickListener(onClickListener);
        this.n_more_txt.setOnClickListener(onClickListener);
        this.gb_more_txt.setOnClickListener(onClickListener);
        this.gb_hudong.setOnClickListener(onClickListener);
        this.gb_maps.setOnClickListener(onClickListener);
        this.nw_exit.setOnClickListener(onClickListener);
        this.ne_exit.setOnClickListener(onClickListener);
        this.sw_exit.setOnClickListener(onClickListener);
        this.se_exit.setOnClickListener(onClickListener);
        this.n_exit.setOnClickListener(onClickListener);
        this.s_exit.setOnClickListener(onClickListener);
        this.w_exit.setOnClickListener(onClickListener);
        this.e_exit.setOnClickListener(onClickListener);
        mengban1.setOnClickListener(onClickListener);
        this.gbchatroom.setOnClickListener(onClickListener);
        this.minichat.setOnClickListener(onClickListener);
        this.ucenterbt.setOnClickListener(onClickListener);
        this.quitb.setOnClickListener(onClickListener);
        this.mainmenub.setOnClickListener(onClickListener);
        this.minglingb.setOnClickListener(onClickListener);
        this.bbsb.setOnClickListener(onClickListener);
        this.senname.setOnClickListener(onClickListener);
        this.help_bt.setOnClickListener(onClickListener);
        this.mud_bt.setOnClickListener(onClickListener);
        this.ch_hi.setOnClickListener(onClickListener);
        this.ch_low.setOnClickListener(onClickListener);
        this.game_set_bt.setOnClickListener(onClickListener);
        this.change_long.setOnClickListener(onClickListener);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void init_webs_face() {
        this.gb_webs = findViewById(R.id.gb_webs);
        my_web = findViewById(R.id.my_web);
        my_web.getSettings().setJavaScriptEnabled(true);
        my_web.setWebViewClient(new WebViewClient());
        my_web.removeJavascriptInterface("searchBoxJavaBredge_");
        this.gb_webs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.gb_webs) { /* 2131230987 */
                    mudmaind.this.webs_ck.setVisibility(View.GONE);
                    return;
                }
            }
        });
    }


    public void mythread() {
        this.recordThread = new Thread(this.ImgThread);
        this.recordThread.start();
    }


    public void setDialogImage() {
        if (voiceValue < 200.0d) {
            this.yinliang.setWA(20);
        } else if (voiceValue > 200.0d && voiceValue < 400.0d) {
            this.yinliang.setWA(40);
        } else if (voiceValue > 400.0d && voiceValue < 800.0d) {
            this.yinliang.setWA(60);
        } else if (voiceValue > 800.0d && voiceValue < 1600.0d) {
            this.yinliang.setWA(80);
        } else if (voiceValue > 1600.0d && voiceValue < 3200.0d) {
            this.yinliang.setWA(100);
        } else if (voiceValue > 3200.0d && voiceValue < 5000.0d) {
            this.yinliang.setWA(120);
        } else if (voiceValue > 5000.0d && voiceValue < 7000.0d) {
            this.yinliang.setWA(140);
        } else if (voiceValue > 7000.0d && voiceValue < 10000.0d) {
            this.yinliang.setWA(160);
        } else if (voiceValue > 10000.0d && voiceValue < 14000.0d) {
            this.yinliang.setWA(180);
        } else if (voiceValue > 14000.0d && voiceValue < 17000.0d) {
            this.yinliang.setWA(200);
        } else if (voiceValue > 17000.0d && voiceValue < 20000.0d) {
            this.yinliang.setWA(220);
        } else if (voiceValue > 20000.0d && voiceValue < 24000.0d) {
            this.yinliang.setWA(240);
        } else if (voiceValue > 24000.0d && voiceValue < 28000.0d) {
            this.yinliang.setWA(260);
        } else if (voiceValue > 28000.0d) {
            this.yinliang.setWA(280);
        }
    }

    private void setwh(View view, int i, int i2) {
        ((TextView) view).setTextColor(syscolor);
        if (i > 0) {
            view.getLayoutParams().width = scrw / i;
        }
        if (i2 > 0) {
            view.getLayoutParams().height = scrw / i2;
        }
    }


    public void takeoblong(String str) {
        CharSequence spantxt = spantxt(str.replace("$br#", "\n"));
        this.ob_long.setTextColor(syscolor);
        this.ob_long.setText(spantxt);
    }


    public void updateTab(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View childAt = tabHost.getTabWidget().getChildAt(i);
            childAt.getLayoutParams().height = scrw / 10;
            /*
            TextView textView = (TextView) childAt.findViewById(R.id.title);
            if (tabHost.getCurrentTab() == i) {
                childAt.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt1));
                textView.setTextSize(0, scrw / 18);
                textView.setTextColor(getResources().getColorStateList(R.color.darkseagreen));
            } else {
                childAt.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt2));
                textView.setTextSize(0, scrw / 22);
                textView.setTextColor(getResources().getColorStateList(R.color.darksalmon));
            }*/
        }
    }

    protected void clearexit(String str) {
        this.nw_exit.setVisibility(View.GONE);
        this.ne_exit.setVisibility(View.GONE);
        this.sw_exit.setVisibility(View.GONE);
        this.se_exit.setVisibility(View.GONE);
        this.w_exit.setVisibility(View.GONE);
        this.n_exit.setVisibility(View.GONE);
        this.e_exit.setVisibility(View.GONE);
        this.s_exit.setVisibility(View.GONE);
        this.fexit.removeAllViews();
    }

    protected void delexit(String str) {
        if (str.equals("northwest")) {
            this.nw_exit.setVisibility(View.GONE);
        } else if (str.equals("northeast")) {
            this.ne_exit.setVisibility(View.GONE);
        } else if (str.equals("southeast")) {
            this.se_exit.setVisibility(View.GONE);
        } else if (str.equals("southwest")) {
            this.sw_exit.setVisibility(View.GONE);
        } else if (str.equals("south") || str.equals("southup") || str.equals("southdown")) {
            this.s_exit.setVisibility(View.GONE);
        } else if (str.equals("north") || str.equals("northup") || str.equals("northdown")) {
            this.n_exit.setVisibility(View.GONE);
        } else if (str.equals("west") || str.equals("westup") || str.equals("westdown")) {
            this.w_exit.setVisibility(View.GONE);
        } else if (str.equals("east") || str.equals("eastup") || str.equals("eastdown")) {
            this.e_exit.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < this.fexit.getChildCount(); i++) {
                if (this.fexit.getChildAt(i).getTag() != null && this.fexit.getChildAt(i).getTag().toString().equals(str)) {
                    this.fexit.removeViewAt(i);
                }
            }
        }
    }

    protected void delobj(String str) {
        for (int i = 0; i < this.user_q_list.getChildCount(); i++) {
            if (this.user_q_list.getChildAt(i).getTag() != null && this.user_q_list.getChildAt(i).getTag().toString().equals(str)) {
                this.user_q_list.removeViewAt(i);
            }
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.mGestureDetector.onTouchEvent(motionEvent);
        return super.dispatchTouchEvent(motionEvent);
    }

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
            case 7:
                return R.drawable.hes;
            default:
                return R.drawable.cry;
        }
    }

    protected void myinput(int i, String str) {
        final EditText editText = new EditText(this);
        final EditText editText2 = findViewById(i);
        editText.setText(editText2.getText().toString());
        new AlertDialog.Builder(this).setTitle(str).setIcon(R.drawable.command).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {


            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                editText2.setText(editText.getText().toString());
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override // android.media.MediaPlayer.OnBufferingUpdateListener
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        player.setText("缓冲中...");
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (playState) {
            player.setText("播放声音");
            record.setEnabled(true);
            playState = false;
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mdate = getSharedPreferences("AP4s3dF5", 0);
        this.mdateedit = mdate.edit();
        setContentView(R.layout.mainx);
        getWindow().addFlags(128);
        init_main_face();
        init_webs_face();

        if (mdate.getString("mode", null) == null || mdate.getString("mode", null).equals("night")) {
            this.help_bt.callOnClick();
        } else if (mdate.getString("mode", null).equals("mud")) {
            this.mud_bt.callOnClick();
        } else if (mdate.getString("mode", null).equals("day")) {
            this.game_set_bt.callOnClick();
        }
        if (mdate.getString("long_text", "open").equals("close")) {
            this.change_long.setText("显示");
            this.story_text.setVisibility(View.GONE);
        }
        this.mGestureDetector = new GestureDetector(this);
        this.mainThread = new Thread(this);
        this.mainThread.start();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        player.setText("错误:" + i + "-" + i2);
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        switch (i) {
            case 4:
                if (mengban1.getVisibility() != View.VISIBLE) {
                    new AlertDialog.Builder(this).setTitle("确认要退出么？").setIcon(R.drawable.command).setPositiveButton("退出", new DialogInterface.OnClickListener() {

                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i2) {
                            mudsocketd.mudout("quit");
                            System.exit(0);
                        }
                    }).setNegativeButton("不，留下来", null).show();
                    return false;
                }
                this.mainmenulist.setVisibility(View.GONE);
                mengban1.setVisibility(View.GONE);
                return false;
            case 82:
            default:
                return false;
        }
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    public void onPrepared(MediaPlayer mediaPlayer) {
        player.setText("正在播放");
        record.setEnabled(false);
        playState = true;
        vmsg.start();
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override // java.lang.Runnable
    public void run() {
        while (this.loop) {
            if (this.content == null) {
                this.content = mudsock.mudin();
                if (mudsock.isconing()) {
                    this.content = null;
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (this.content == null) {
                    try {
                        Thread.sleep(300L);
                    } catch (InterruptedException e2) {
                        throw new RuntimeException(e2);
                    }
                } else if (this.content.equals("连接已断开！")) {
                    this.content = mudsock.recon();
                    this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e3) {
                        throw new RuntimeException(e3);
                    }
                } else if (this.content.length() <= 3 || !this.content.startsWith("ver")) {
                    this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
                    try {
                        Thread.sleep(this.sleep);
                    } catch (InterruptedException e4) {
                        throw new RuntimeException(e4);
                    }
                } else {
                    final String[] split = this.content.split(",");
                    // java.lang.Runnable
                    new Thread(() -> {
                        try {
                            String ip = mudmaind.mudsock.getgameip();
                            String port = mudmaind.mudsock.getgameport();
                            String key = split[1] + "AP4s3dF5";

                            String urlStr = "https://mud1.foxmoe.top/mobi/check.php?ip="
                                    + ip
                                    + "&port=" + port
                                    + "&key=" + key;

                            URL url = new URL(urlStr);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setConnectTimeout(5000);

                            if (conn.getResponseCode() == 200) {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                                reader.close();
                                mudmaind.this.content = sb.toString();
                            } else {
                                mudmaind.this.content = "版本验证失败！";
                            }
                        } catch (Exception e5) {
                            mudmaind.this.content = "版本验证失败！";
                        }
                        mudmaind.this.mHandler.sendMessage(mudmaind.this.mHandler.obtainMessage(4));
                    }).start();
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e5) {
                        throw new RuntimeException(e5);
                    }
                }
            }
        }
    }

    protected void takebutton(String str) {
        String[] split = str.replace("$br#", "\n").split("\\$zj#");
        this.cmds_exit.setText("关闭");
        for (String str2 : split) {
            String[] split2 = str2.split(":");
            CharSequence spantxt = spantxt(split2[1]);
            if (split2[0].equals("bs")) {
                this.senname.setTextColor(syscolor);
                this.senname.setTag(split2[2]);
            } else if (split2[0].equals("b1")) {
                this.my_cmds_1.setTextColor(syscolor);
                this.my_cmds_1.setText(spantxt);
                this.my_cmds_1.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b2")) {
                this.my_cmds_2.setTextColor(syscolor);
                this.my_cmds_2.setText(spantxt);
                this.my_cmds_2.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b3")) {
                this.my_cmds_3.setTextColor(syscolor);
                this.my_cmds_3.setText(spantxt);
                this.my_cmds_3.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b4")) {
                this.my_cmds_4.setTextColor(syscolor);
                this.my_cmds_4.setText(spantxt);
                this.my_cmds_4.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b5")) {
                this.my_cmds_5.setTextColor(syscolor);
                this.my_cmds_5.setText(spantxt);
                this.my_cmds_5.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b6")) {
                this.my_cmds_6.setTextColor(syscolor);
                this.my_cmds_6.setText(spantxt);
                this.my_cmds_6.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b7")) {
                this.my_cmds_7.setTextColor(syscolor);
                this.my_cmds_7.setText(spantxt);
                this.my_cmds_7.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b8")) {
                this.my_cmds_8.setTextColor(syscolor);
                this.my_cmds_8.setText(spantxt);
                this.my_cmds_8.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b9")) {
                this.my_cmds_9.setTextColor(syscolor);
                this.my_cmds_9.setText(spantxt);
                this.my_cmds_9.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b10")) {
                this.my_cmds_10.setTextColor(syscolor);
                this.my_cmds_10.setText(spantxt);
                this.my_cmds_10.setTag(split2[2]);
                this.exits_ck.setVisibility(View.GONE);
                this.my_cmds_lay.setVisibility(View.VISIBLE);
                this.recorder.setVisibility(View.GONE);
            } else if (split2[0].equals("b11")) {
                this.my_cmds_11.setTextColor(syscolor);
                this.my_cmds_11.setText(spantxt);
                this.my_cmds_11.setTag(split2[2]);
            } else if (split2[0].equals("b12")) {
                this.my_cmds_12.setTextColor(syscolor2);
                this.my_cmds_12.setText(spantxt);
                this.my_cmds_12.setTag(split2[2]);
                this.mdateedit.putString("my_cmds_12a", split2[1]);
                this.mdateedit.putString("my_cmds_12b", split2[2]);
            } else if (split2[0].equals("b13")) {
                this.my_cmds_13.setTextColor(syscolor2);
                this.my_cmds_13.setText(spantxt);
                this.my_cmds_13.setTag(split2[2]);
                this.mdateedit.putString("my_cmds_13a", split2[1]);
                this.mdateedit.putString("my_cmds_13b", split2[2]);
            } else if (split2[0].equals("b14")) {
                this.my_cmds_14.setTextColor(syscolor2);
                this.my_cmds_14.setText(spantxt);
                this.my_cmds_14.setTag(split2[2]);
                this.mdateedit.putString("my_cmds_14a", split2[1]);
                this.mdateedit.putString("my_cmds_14b", split2[2]);
            } else if (split2[0].equals("b15")) {
                this.my_cmds_15.setTextColor(syscolor2);
                this.my_cmds_15.setText(spantxt);
                this.my_cmds_15.setTag(split2[2]);
                this.mdateedit.putString("my_cmds_15a", split2[1]);
                this.mdateedit.putString("my_cmds_15b", split2[2]);
            } else if (split2[0].equals("b16")) {
                this.my_cmds_16.setTextColor(syscolor2);
                this.my_cmds_16.setText(spantxt);
                this.my_cmds_16.setTag(split2[2]);
                this.mdateedit.putString("my_cmds_16a", split2[1]);
                this.mdateedit.putString("my_cmds_16b", split2[2]);
            } else if (split2[0].equals("b17")) {
                this.my_cmds_17.setTextColor(syscolor2);
                this.my_cmds_17.setText(spantxt);
                this.my_cmds_17.setTag(split2[2]);
                this.mdateedit.putString("my_cmds_17a", split2[1]);
                this.mdateedit.putString("my_cmds_17b", split2[2]);
            }
        }
        this.mdateedit.commit();
    }

    protected void takemap(String str) {
        this.map_txt.setText(spantxt(str.replace("$br#", "\n")));
        maps_lay.setVisibility(View.VISIBLE);
    }


    public class OnTabChangedListener implements TabHost.OnTabChangeListener {


        OnTabChangedListener() {
        }

        @Override // android.widget.TabHost.OnTabChangeListener
        public void onTabChanged(String str) {
            mudmaind.this.updateTab(mudmaind.this.tabhost5);
        }
    }


    class FileUploadTask extends AsyncTask<Object, Integer, String> {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        File file = new File(mudmaind.getAmrFilePath());
        String urlServer = "https://mud1.foxmoe.top/upload.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        long totalSize = this.file.length();
        private ProgressDialog dialog = null;
        private FileInputStream fileInputStream = null;
        private InputStream is = null;


        FileUploadTask() {
        }


        @Override // android.os.AsyncTask
        public String doInBackground(Object... objArr) {
            long j = 0;
            try {
                try {
                    this.fileInputStream = new FileInputStream(this.file);
                    this.connection = (HttpURLConnection) new URL(this.urlServer).openConnection();
                    this.connection.setChunkedStreamingMode(262144);
                    this.connection.setConnectTimeout(6000);
                    this.connection.setReadTimeout(6000);
                    this.connection.setDoInput(true);
                    this.connection.setDoOutput(true);
                    this.connection.setUseCaches(false);
                    this.connection.setRequestMethod("POST");
                    this.connection.setRequestProperty("Connection", "Keep-Alive");
                    this.connection.setRequestProperty("Charset", "UTF-8");
                    this.connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);
                    this.outputStream = new DataOutputStream(this.connection.getOutputStream());
                    this.outputStream.writeBytes(this.twoHyphens + this.boundary + this.lineEnd);
                    this.outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\"" + this.file.getName() + "\"" + this.lineEnd);
                    this.outputStream.writeBytes(this.lineEnd);
                    int min = Math.min(this.fileInputStream.available(), 262144);
                    byte[] bArr = new byte[min];
                    int read = this.fileInputStream.read(bArr, 0, min);
                    while (read > 0) {
                        this.outputStream.write(bArr, 0, min);
                        j += min;
                        publishProgress(Integer.valueOf((int) ((100 * j) / this.totalSize)));
                        min = Math.min(this.fileInputStream.available(), 262144);
                        read = this.fileInputStream.read(bArr, 0, min);
                    }
                    this.outputStream.writeBytes(this.lineEnd);
                    this.outputStream.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.lineEnd);
                    publishProgress(100);
                    this.is = this.connection.getInputStream();
                    String readLine = new BufferedReader(new InputStreamReader(this.is, StandardCharsets.UTF_8)).readLine();
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                            return readLine;
                        }
                        return readLine;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return readLine;
                    }
                } catch (Throwable th) {
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    throw th;
                }
            } catch (FileNotFoundException e3) {
                if (e3.getMessage().contains("http")) {
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                            return "服务器连接错误";
                        }
                        return "服务器连接错误";
                    } catch (IOException e4) {
                        e4.printStackTrace();
                        return "服务器连接错误";
                    }
                } else if (!e3.getMessage().contains(this.file.getAbsolutePath())) {
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                        }
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                    return null;
                } else {
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                            return "音频文件查找失败";
                        }
                        return "音频文件查找失败";
                    } catch (IOException e6) {
                        e6.printStackTrace();
                        return "音频文件查找失败";
                    }
                }
            } catch (MalformedURLException e7) {
                try {
                    if (this.fileInputStream != null) {
                        this.fileInputStream.close();
                    }
                    if (this.fileInputStream != null) {
                        this.outputStream.flush();
                        this.outputStream.close();
                    }
                    if (this.is != null) {
                        this.is.close();
                        return "URL错误";
                    }
                    return "URL错误";
                } catch (IOException e8) {
                    e8.printStackTrace();
                    return "URL错误";
                }
            } catch (IOException e9) {
                try {
                    if (this.fileInputStream != null) {
                        this.fileInputStream.close();
                    }
                    if (this.fileInputStream != null) {
                        this.outputStream.flush();
                        this.outputStream.close();
                    }
                    if (this.is != null) {
                        this.is.close();
                        return "数据流错误";
                    }
                    return "数据流错误";
                } catch (IOException e10) {
                    e10.printStackTrace();
                    return "数据流错误";
                }
            }
        }


        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            try {
                this.dialog.dismiss();
                if (Boolean.parseBoolean(str)) {
                    Toast.makeText(mudmaind.this, "语音消息发送成功", Toast.LENGTH_SHORT).show();
                    mudsocketd.mudout("liaotian voice " + this.file.getName());
                    new File(mudmaind.getAmrFilePath()).delete();
                } else {
                    Toast.makeText(mudmaind.this, "发送失败，" + str, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            this.dialog = new ProgressDialog(mudmaind.this);
            this.dialog.setMessage("正在上传...");
            this.dialog.setIndeterminate(false);
            this.dialog.setProgressStyle(1);
            this.dialog.setProgress(0);
            this.dialog.show();
        }


        @Override // android.os.AsyncTask
        public void onProgressUpdate(Integer... numArr) {
            this.dialog.setProgress(numArr[0]);
        }
    }


    class FileDownloadTask extends AsyncTask<Object, Integer, String> {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        File file = new File(mudmaind.getAmrFilePath());
        String urlServer = "https://mud1.foxmoe.top/mud/upload.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        long totalSize = this.file.length();
        private ProgressDialog dialog = null;
        private FileInputStream fileInputStream = null;
        private InputStream is = null;


        @Override // android.os.AsyncTask
        public String doInBackground(Object... objArr) {
            long j = 0;
            try {
                try {
                    this.fileInputStream = new FileInputStream(this.file);
                    this.connection = (HttpURLConnection) new URL(this.urlServer).openConnection();
                    this.connection.setChunkedStreamingMode(262144);
                    this.connection.setConnectTimeout(6000);
                    this.connection.setReadTimeout(6000);
                    this.connection.setDoInput(true);
                    this.connection.setDoOutput(true);
                    this.connection.setUseCaches(false);
                    this.connection.setRequestMethod("POST");
                    this.connection.setRequestProperty("Connection", "Keep-Alive");
                    this.connection.setRequestProperty("Charset", "UTF-8");
                    this.connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);
                    this.outputStream = new DataOutputStream(this.connection.getOutputStream());
                    this.outputStream.writeBytes(this.twoHyphens + this.boundary + this.lineEnd);
                    this.outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\"" + this.file.getName() + "\"" + this.lineEnd);
                    this.outputStream.writeBytes(this.lineEnd);
                    int min = Math.min(this.fileInputStream.available(), 262144);
                    byte[] bArr = new byte[min];
                    int read = this.fileInputStream.read(bArr, 0, min);
                    while (read > 0) {
                        this.outputStream.write(bArr, 0, min);
                        j += min;
                        publishProgress((int) ((100 * j) / this.totalSize));
                        min = Math.min(this.fileInputStream.available(), 262144);
                        read = this.fileInputStream.read(bArr, 0, min);
                    }
                    this.outputStream.writeBytes(this.lineEnd);
                    this.outputStream.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.lineEnd);
                    publishProgress(100);
                    this.is = this.connection.getInputStream();
                    String readLine = new BufferedReader(new InputStreamReader(this.is, StandardCharsets.UTF_8)).readLine();
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                            return readLine;
                        }
                        return readLine;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return readLine;
                    }
                } catch (Throwable th) {
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    throw th;
                }
            } catch (FileNotFoundException e3) {
                if (Objects.requireNonNull(e3.getMessage()).contains("http")) {
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                            return "服务器连接错误";
                        }
                        return "服务器连接错误";
                    } catch (IOException e4) {
                        e4.printStackTrace();
                        return "服务器连接错误";
                    }
                } else if (!e3.getMessage().contains(this.file.getAbsolutePath())) {
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                        }
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                    return null;
                } else {
                    try {
                        if (this.fileInputStream != null) {
                            this.fileInputStream.close();
                        }
                        if (this.fileInputStream != null) {
                            this.outputStream.flush();
                            this.outputStream.close();
                        }
                        if (this.is != null) {
                            this.is.close();
                            return "音频文件查找失败";
                        }
                        return "音频文件查找失败";
                    } catch (IOException e6) {
                        e6.printStackTrace();
                        return "音频文件查找失败";
                    }
                }
            } catch (MalformedURLException e7) {
                try {
                    if (this.fileInputStream != null) {
                        this.fileInputStream.close();
                    }
                    if (this.fileInputStream != null) {
                        this.outputStream.flush();
                        this.outputStream.close();
                    }
                    if (this.is != null) {
                        this.is.close();
                        return "URL错误";
                    }
                    return "URL错误";
                } catch (IOException e8) {
                    e8.printStackTrace();
                    return "URL错误";
                }
            } catch (IOException e9) {
                try {
                    if (this.fileInputStream != null) {
                        this.fileInputStream.close();
                    }
                    if (this.fileInputStream != null) {
                        this.outputStream.flush();
                        this.outputStream.close();
                    }
                    if (this.is != null) {
                        this.is.close();
                        return "数据流错误";
                    }
                    return "数据流错误";
                } catch (IOException e10) {
                    e10.printStackTrace();
                    return "数据流错误";
                }
            }
        }


        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            try {
                this.dialog.dismiss();
                if (Boolean.parseBoolean(str)) {
                    Toast.makeText(mudmaind.this, "语音消息发送成功", Toast.LENGTH_SHORT).show();
                    mudsocketd.mudout("liaotian voice " + this.file.getName());
                    this.file.delete();
                } else {
                    Toast.makeText(mudmaind.this, "发送失败，" + str, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            this.dialog = new ProgressDialog(mudmaind.this);
            this.dialog.setMessage("正在上传...");
            this.dialog.setIndeterminate(false);
            this.dialog.setProgressStyle(1);
            this.dialog.setProgress(0);
            this.dialog.show();
        }


        @Override // android.os.AsyncTask
        public void onProgressUpdate(Integer... numArr) {
            this.dialog.setProgress(numArr[0]);
        }

    }
}