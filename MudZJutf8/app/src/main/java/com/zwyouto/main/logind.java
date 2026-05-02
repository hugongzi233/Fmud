package com.zwyouto.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.zwyouto.R;
import com.zwyouto.mud.BuildConfig;
import com.zwyouto.myactivex.XRTextView;
import com.zwyouto.myactivex.longitemd;
import com.zwyouto.myactivex.ucenter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class logind extends Activity {
    private Button denglub;
    protected String email;
    private String key;
    private longitemd last_part;
    private Thread loginT;
    private RelativeLayout logind_lay;
    private SharedPreferences mdate;
    protected SharedPreferences.Editor mdateedit;
    private FrameLayout mengban1;
    private mudsocketd mudsock;
    private EditText nameedit;
    private longitemd new_part;
    private Button nicok;
    private Button nicqx;

    private int screenHeightAdjusted;
    private Button noenter;
    private Button noreg;
    private EditText passedit;
    private EditText regemail;
    private EditText regid;
    private RelativeLayout reglay;
    private RelativeLayout reglayx;
    private EditText regname;
    private Button regok;
    private EditText regpass;
    private EditText regpass2;
    private EditText regphone;
    private RadioGroup regsexs;
    private String result;
    private int scrh;
    private int scrw;
    private TableLayout server_ls;
    private RelativeLayout slist;
    private Button slistdown;
    private Button slistup;
    private String slserver;
    private Button tuichub;
    private Button user_center;
    private Button zhuceb;
    private String sex = "男性";
    private String content = "...";
    private boolean turn_page = true;
    private boolean loop = true;
    private int slistpage = 1;
    private final DisplayMetrics pingmu = new DisplayMetrics();
    protected boolean verify = false;
    protected String register = BuildConfig.FLAVOR;
    public Handler mHandler = new LoginHandler();

    class LoginHandler extends Handler {

        LoginHandler() {
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 2) {
                if (logind.this.result.equals("注册成功")) {
                    logind.this.nameedit.setText(logind.this.regid.getText().toString());
                    logind.this.passedit.setText(logind.this.regpass.getText().toString());
                    logind.this.reglay.setVisibility(View.GONE);
                    logind.this.mengban1.setVisibility(View.GONE);
                    return;
                }
                if (logind.this.result.equals("最后一页")) {
                    logind.this.slistpage--;
                    logind.this.mengban1.setVisibility(View.GONE);
                    Toast.makeText(logind.this.getBaseContext(), logind.this.result, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!logind.this.result.subSequence(0, 3).equals("$l#")) {
                    logind.this.mengban1.setVisibility(View.GONE);
                    Toast.makeText(logind.this.getBaseContext(), logind.this.result, Toast.LENGTH_SHORT).show();
                    return;
                }
                logind.this.slist.setVisibility(View.VISIBLE);
                logind.this.mengban1.setVisibility(View.GONE);
                logind.this.server_ls.removeAllViews();
                String[] strArrSplit = logind.this.result.substring(3).split("\\|");
                if (logind.this.slistpage == 1 && logind.this.mudsock.get_ucenter_id().equals(BuildConfig.FLAVOR)) {
                    logind.this.mdateedit.putString("name", logind.this.nameedit.getText().toString());
                    logind.this.mdateedit.putString("pass", logind.this.passedit.getText().toString());
                    logind.this.mdateedit.commit();
                    logind.this.mudsock.set_ucenter_id(logind.this.nameedit.getText().toString());
                    logind.this.mudsock.set_ucenter_pass(logind.this.passedit.getText().toString());
                    logind.this.email = strArrSplit[strArrSplit.length - 1];
                    logind.this.mudsock.set_ucenter_email(logind.this.email);
                    logind.this.new_part.settext1(strArrSplit[0].split("&")[0]);
                    logind.this.new_part.setTag(strArrSplit[0]);
                    logind.this.new_part.setOnClickListener(view -> {
                        if (view.getTag() == null) return;
                        logind.this.mengban1.setVisibility(View.VISIBLE);
                        logind.this.slserver = view.getTag().toString();
                        logind.this.key = logind.this.slserver.split("&")[5];
                        logind.this.mudsock.new_sock(logind.this.slserver.split("&")[1], Integer.valueOf(logind.this.slserver.split("&")[2]).intValue());
                        new Thread(() -> {
                            for (int i = 0; i < 30; i++) {
                                try {
                                    Thread.sleep(200L);
                                } catch (InterruptedException exception) {
                                }
                                if (logind.this.mudsock.isaction()) {
                                    break;
                                }
                            }
                            logind.this.mHandler.sendMessage(logind.this.mHandler.obtainMessage(3));
                        }).start();
                    });
                    String string = logind.this.mdate.getString("last_part", "无");
                    if (!string.equals("无")) {
                        logind.this.last_part.settext1(string.split("&")[0]);
                        logind.this.last_part.setTag(string);
                        logind.this.last_part.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (view.getTag() == null) return;
                                logind.this.mengban1.setVisibility(View.VISIBLE);
                                logind.this.slserver = view.getTag().toString();
                                logind.this.key = logind.this.slserver.split("&")[5];
                                logind.this.mudsock.new_sock(logind.this.slserver.split("&")[1], Integer.valueOf(logind.this.slserver.split("&")[2]).intValue());
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < 30; i++) {
                                            try {
                                                Thread.sleep(200L);
                                            } catch (InterruptedException exception) {
                                            }
                                            if (logind.this.mudsock.isaction()) {
                                                break;
                                            }
                                        }
                                        logind.this.mHandler.sendMessage(logind.this.mHandler.obtainMessage(3));
                                    }
                                }).start();
                            }
                        });
                    }
                }
                for (int i = 0; i < strArrSplit.length - 1; i++) {
                    longitemd longitemdVar = new longitemd(logind.this);
                    longitemdVar.settext1(strArrSplit[i].split("&")[0]);
                    longitemdVar.setTag(strArrSplit[i]);
                    longitemdVar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            logind.this.mengban1.setVisibility(View.VISIBLE);
                            logind.this.slserver = view.getTag().toString();
                            logind.this.key = logind.this.slserver.split("&")[5];
                            logind.this.mudsock.new_sock(logind.this.slserver.split("&")[1], Integer.valueOf(logind.this.slserver.split("&")[2]).intValue());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i2 = 0; i2 < 30; i2++) {
                                        try {
                                            Thread.sleep(200L);
                                        } catch (InterruptedException exception) {
                                        }
                                        if (logind.this.mudsock.isaction()) {
                                            break;
                                        }
                                    }
                                    logind.this.mHandler.sendMessage(logind.this.mHandler.obtainMessage(3));
                                }
                            }).start();
                        }
                    });
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
                    layoutParams.setMargins((logind.this.pingmu.densityDpi * 10) / 160, (logind.this.pingmu.densityDpi * 5) / 160, (logind.this.pingmu.densityDpi * 10) / 160, 0);
                    layoutParams.width = (logind.this.scrw - ((logind.this.pingmu.densityDpi * 62) / 160)) / 2;
                    longitemdVar.settextsize1(logind.this.scrw / 28);
                    longitemdVar.settext2v(8);
                    longitemdVar.setlpad((logind.this.pingmu.densityDpi * 5) / 160);
                    longitemdVar.setimg(BitmapFactory.decodeResource(logind.this.getResources(), R.drawable.licon), logind.this.screenHeightAdjusted / 10);
                    RelativeLayout relativeLayout = new RelativeLayout(logind.this);
                    relativeLayout.addView(longitemdVar, layoutParams);
                    relativeLayout.setGravity(17);
                    boolean z = false;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= logind.this.server_ls.getChildCount()) {
                            break;
                        }
                        TableRow tableRow = (TableRow) logind.this.server_ls.getChildAt(i2);
                        if (tableRow.getChildCount() < 2) {
                            tableRow.addView(relativeLayout);
                            z = true;
                            break;
                        }
                        i2++;
                    }
                    if (!z) {
                        TableRow tableRow2 = new TableRow(logind.this);
                        tableRow2.addView(relativeLayout);
                        logind.this.server_ls.addView(tableRow2);
                    }
                }
                return;
            }
            if (message.what == 3) {
                if (logind.this.mudsock.isaction()) {
                    logind.this.loginT.start();
                    return;
                } else {
                    new AlertDialog.Builder(logind.this).setTitle("连接超时！").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i3) {
                            logind.this.mengban1.setVisibility(View.GONE);
                        }
                    }).show();
                    return;
                }
            }
            if (message.what == 4) {
                mudsocketd.mudout(logind.this.result);
                return;
            }
            if (message.what == 5) {
                logind.this.finish();
                System.exit(0);
                return;
            }
            if (message.what == 8) {
                if (!logind.this.content.contains("|")) {
                    if (logind.this.content.equals(logind.this.getVersion())) {
                        logind.this.logind_lay.setVisibility(View.VISIBLE);
                        return;
                    }
                    XRTextView xRTextView = new XRTextView(logind.this, logind.this.scrw / 25, -16777216, 5.0f, 2.0f, 2.0f, 2.0f);
                    xRTextView.setText("您当前使用的客户端版本不是最新版本，如果现有版本不能正常进行游戏，请进入官方网站或者游戏群里下载最新版本！");
                    xRTextView.setBackgroundColor(-1);
                    new AlertDialog.Builder(logind.this).setTitle("版本更新提示：").setView(xRTextView).setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i3) {
                            logind.this.logind_lay.setVisibility(View.VISIBLE);
                        }
                    }).show();
                    return;
                }
                String[] strArrSplit2 = logind.this.content.split("\\|");
                TextView textView = new TextView(logind.this);
                textView.setPadding(15, 5, 15, 5);
                String str = strArrSplit2[1];
                if (strArrSplit2.length > 2) {
                    logind.this.mudsock.setpay(false);
                }
                if (!strArrSplit2[0].equals(logind.this.getVersion())) {
                    str = strArrSplit2[1] + "\n您当前使用的客户端版本不是最新版本，如果现有版本不能正常进行游戏，请进入官方网站或者游戏群里下载最新版本";
                }
                textView.setText(str + "！");
                textView.setBackgroundColor(-1);
                new AlertDialog.Builder(logind.this).setTitle("服务器提醒：").setView(textView).setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i3) {
                            logind.this.logind_lay.setVisibility(View.VISIBLE);
                        }
                }).show();
                return;
            }
            if (logind.this.content.equals("与服务器断开连接！")) {
                logind.this.loop = false;
                new AlertDialog.Builder(logind.this).setTitle("与服务器失去连接！").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i3) {
                        System.exit(0);
                    }
                }).show();
            }
            if (logind.this.content == null || logind.this.content.length() < 4) {
                return;
            }
            String strSubstring = logind.this.content.substring(1, 4);
            if (!strSubstring.equals("000")) {
                if (strSubstring.equals("015")) {
                    Toast toastMakeText = Toast.makeText(logind.this.getBaseContext(), logind.this.content.substring(4), Toast.LENGTH_SHORT);
                    toastMakeText.setGravity(17, 0, 0);
                    toastMakeText.show();
                    logind.this.mengban1.setVisibility(View.GONE);
                    return;
                }
                if (logind.this.content.startsWith("ver")) {
                    final String[] strArrSplit3 = logind.this.content.split(",");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String url = "https://mud1.foxmoe.top/mobi/check.php?ip=" + logind.this.slserver.split("&")[1] + "&port=" + logind.this.slserver.split("&")[2] + "&key=" + strArrSplit3[1] + "AP4s3dF5";
                                logind.this.result = logind.this.doGet(url);
                                if (logind.this.result.equals("Error")) {
                                    logind.this.result = "登陆失败，服务器链接错误！";
                                }
                            } catch (Exception exception) {
                                logind.this.result = exception + "登陆失败，请检查网络！";
                            }
                            logind.this.content = logind.this.result;
                            logind.this.mHandler.sendMessage(logind.this.mHandler.obtainMessage(4));
                        }
                    }).start();
                    return;
                } else if (logind.this.content.equals("版本验证成功")) {
                    logind.this.verify = true;
                    mudsocketd.mudout(logind.this.mudsock.get_ucenter_id() + "║" + logind.this.mudsock.get_ucenter_pass() + "║" + logind.this.key + "║" + logind.this.email);
                    return;
                } else {
                    if (logind.this.content.equals("客户端非法")) {
                        Toast.makeText(logind.this.getBaseContext(), logind.this.content, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    return;
                }
            }
            if (!logind.this.verify) {
                logind.this.loop = false;
                new AlertDialog.Builder(logind.this).setTitle("服务器验证失败！").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i3) {
                        System.exit(0);
                    }
                }).show();
                return;
            }
            if (logind.this.content.substring(4).equals("0003")) {
                mudsocketd.mudout(logind.this.passedit.getText().toString());
                return;
            }
            if (logind.this.content.substring(4).equals("0008")) {
                logind.this.reglayx.setVisibility(View.VISIBLE);
                logind.this.mengban1.setVisibility(View.GONE);
                return;
            }
            if (logind.this.content.substring(4).equals("0007")) {
                logind.this.mdateedit.putString("last_part", logind.this.slserver);
                logind.this.mdateedit.commit();
                logind.this.mudsock.setgamepart(logind.this.slserver.split("&")[0]);
                logind.this.mudsock.setgameip(logind.this.slserver.split("&")[1]);
                logind.this.mudsock.setgameport(logind.this.slserver.split("&")[2]);
                logind.this.mudsock.setgamekey(logind.this.key);
                logind.this.startActivity(new Intent(logind.this, mudmaind.class));
                logind.this.loop = false;
                logind.this.finish();
            }
        }
    }

    public String MD5(String str) throws NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            char[] charArray = str.toCharArray();
            byte[] bArr = new byte[charArray.length];
            for (int i = 0; i < charArray.length; i++) {
                bArr[i] = (byte) charArray[i];
            }
            byte[] bArrDigest = messageDigest.digest(bArr);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bArrDigest) {
                int i2 = b & 255;
                if (i2 < 16) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(Integer.toHexString(i2));
            }
            return stringBuffer.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }

    public String getDiskCacheDir() {
        return ("mounted".equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) ? getExternalCacheDir().getAbsolutePath() : getCacheDir().getAbsolutePath();
    }

    public String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    public String doGet(String urlStr) {
        try {
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
                return sb.toString();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "Error";
    }

    @Override
    @SuppressLint({"SetJavaScriptEnabled"})
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.loginx);
        getWindowManager().getDefaultDisplay().getMetrics(this.pingmu);
        this.scrw = this.pingmu.widthPixels;
        this.scrh = this.pingmu.heightPixels;
        this.reglay = findViewById(R.id.reg_id_lay);
        this.reglayx = findViewById(R.id.reglayx);
        this.slist = findViewById(R.id.slist);
        this.logind_lay = findViewById(R.id.logind_lay);
        this.server_ls = findViewById(R.id.server_ls);
        this.nameedit = findViewById(R.id.nameedit);
        this.nameedit.getLayoutParams().height = this.scrw / 11;
        this.passedit = findViewById(R.id.passedit);
        this.passedit.getLayoutParams().height = this.scrw / 11;
        this.regid = findViewById(R.id.new_id);
        this.regid.getLayoutParams().height = this.scrw / 11;
        this.regname = findViewById(R.id.newregname);
        this.regname.getLayoutParams().height = this.scrw / 11;
        this.regpass = findViewById(R.id.new_pass);
        this.regpass.getLayoutParams().height = this.scrw / 11;
        this.regpass2 = findViewById(R.id.new_pass2);
        this.regpass2.getLayoutParams().height = this.scrw / 11;
        this.regphone = findViewById(R.id.new_phone);
        this.regphone.getLayoutParams().height = this.scrw / 11;
        this.regemail = findViewById(R.id.new_email);
        this.regemail.getLayoutParams().height = this.scrw / 11;
        this.denglub = findViewById(R.id.denglubt);
        this.denglub.getLayoutParams().height = this.scrw / 10;
        this.tuichub = findViewById(R.id.tuichubt);
        this.tuichub.getLayoutParams().height = this.scrw / 10;
        this.zhuceb = findViewById(R.id.zhucebt);
        this.zhuceb.getLayoutParams().height = this.scrw / 10;
        this.regok = findViewById(R.id.reg_ok);
        this.regok.getLayoutParams().height = this.scrw / 10;
        this.noreg = findViewById(R.id.qx_reg);
        this.noreg.getLayoutParams().height = this.scrw / 10;
        this.screenHeightAdjusted = ((this.scrh - ((this.pingmu.densityDpi * 180) / 160)) - (this.scrw / 9)) - (this.scrw / 10);
        this.new_part = findViewById(R.id.new_part);
        this.new_part.setimg(BitmapFactory.decodeResource(getResources(), R.drawable.licon), this.screenHeightAdjusted / 10);
        this.new_part.settextsize1(this.scrw / 28);
        this.new_part.settext2v(8);
        this.new_part.setlpad((this.pingmu.densityDpi * 5) / 160);
        this.last_part = findViewById(R.id.last_part);
        this.last_part.setimg(BitmapFactory.decodeResource(getResources(), R.drawable.licon), this.screenHeightAdjusted / 10);
        this.last_part.settextsize1(this.scrw / 28);
        this.last_part.settext2v(8);
        this.last_part.setlpad((this.pingmu.densityDpi * 5) / 160);
        this.noenter = findViewById(R.id.noenter);
        this.noenter.getLayoutParams().height = this.scrw / 9;
        this.slistup = findViewById(R.id.slistup);
        this.slistup.getLayoutParams().height = this.scrw / 9;
        this.slistdown = findViewById(R.id.slistdown);
        this.slistdown.getLayoutParams().height = this.scrw / 9;
        this.user_center = findViewById(R.id.user_center);
        this.user_center.getLayoutParams().height = this.scrw / 9;
        this.nicok = findViewById(R.id.nicok);
        this.nicok.getLayoutParams().height = this.scrw / 9;
        this.nicqx = findViewById(R.id.nicqx);
        this.nicqx.getLayoutParams().height = this.scrw / 9;
        this.regsexs = findViewById(R.id.newregsex);
        this.mengban1 = findViewById(R.id.loginmengban);
        this.mudsock = (mudsocketd) getApplication();
        this.mdate = getSharedPreferences("zwyjh", 0);
        this.mdateedit = this.mdate.edit();
        this.nameedit.setText(this.mdate.getString("name", BuildConfig.FLAVOR));
        this.passedit.setText(this.mdate.getString("pass", BuildConfig.FLAVOR));
        this.regsexs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio0) {
                    logind.this.sex = "男性";
                } else {
                    logind.this.sex = "女性";
                }
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            private void checkreg() {
                if (logind.this.regid.getText().toString().equals(BuildConfig.FLAVOR) || logind.this.regemail.getText().toString().equals(BuildConfig.FLAVOR) || logind.this.regphone.getText().toString().equals(BuildConfig.FLAVOR) || logind.this.regpass.getText().toString().equals(BuildConfig.FLAVOR) || logind.this.regpass2.getText().toString().equals(BuildConfig.FLAVOR)) {
                    Toast toastMakeText = Toast.makeText(logind.this.getBaseContext(), "请确保各项都不为空！", Toast.LENGTH_SHORT);
                    toastMakeText.setGravity(17, 0, 0);
                    toastMakeText.show();
                } else if (logind.this.regpass.getText().toString().equals(logind.this.regpass2.getText().toString())) {
                    logind.this.mengban1.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String url = "https://mud1.foxmoe.top/mobi/reg.php?id=" + logind.this.regid.getText().toString() + "&pass=" + logind.this.regpass.getText().toString() + "&phone=" + logind.this.regphone.getText().toString() + "&email=" + logind.this.regemail.getText().toString() + "&key=" + logind.this.MD5(logind.this.regid.getText().toString() + logind.this.regpass.getText().toString() + logind.this.regphone.getText().toString() + "AP4s3dF5");
                                logind.this.result = logind.this.doGet(url);
                                if (logind.this.result.equals("Error")) {
                                    logind.this.result = "注册失败，服务器链接错误！";
                                }
                            } catch (Exception exception) {
                                logind.this.result = "注册失败，请检查网络！";
                            }
                            logind.this.content = logind.this.result;
                            logind.this.mHandler.sendMessage(logind.this.mHandler.obtainMessage(2));
                        }
                    }).start();
                } else {
                    Toast toastMakeText2 = Toast.makeText(logind.this.getBaseContext(), "两次输入密码不一致！", Toast.LENGTH_SHORT);
                    toastMakeText2.setGravity(17, 0, 0);
                    toastMakeText2.show();
                }
            }

            private void enter_list() {
                logind.this.mengban1.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    String string;
                    String string2;
                    try {
                        if (logind.this.turn_page) {
                            string = logind.this.nameedit.getText().toString();
                            string2 = logind.this.passedit.getText().toString();
                        } else {
                            string = logind.this.mudsock.get_ucenter_id();
                            string2 = logind.this.mudsock.get_ucenter_pass();
                        }
                        String url = "https://mud1.foxmoe.top/mobi/loginto.php?id=" + string + "&pass=" + string2 + "&key=" + logind.this.MD5(string + string2 + "AP4s3dF5") + "&page=" + logind.this.slistpage;
                        logind.this.result = logind.this.doGet(url);
                        if (logind.this.result.equals("Error")) {
                            logind.this.result = "登陆失败，服务器链接错误！";
                        }
                    } catch (Exception exception) {
                        logind.this.result = "登陆失败，请检查网络！";
                    }
                    logind.this.content = logind.this.result;
                    logind.this.mHandler.sendMessage(logind.this.mHandler.obtainMessage(2));
                }).start();
            }

            private void myinput(int i, String str) {
                final EditText editText = new EditText(logind.this);
                final EditText editText2 = logind.this.findViewById(i);
                if (editText2 == null) return;
                editText.setText(editText2.getText().toString());
                new AlertDialog.Builder(logind.this).setTitle(str).setCancelable(false).setIcon(R.drawable.chahao).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        editText2.setText(editText.getText().toString());
                    }
                }).setNegativeButton("取消", null).show();
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.nameedit:
                        myinput(R.id.nameedit, "请输入账号：");
                        break;
                    case R.id.passedit:
                        myinput(R.id.passedit, "请输入密码：");
                        break;
                    case R.id.denglubt:
                        logind.this.slistpage = 1;
                        logind.this.turn_page = true;
                        enter_list();
                        break;
                    case R.id.zhucebt:
                        logind.this.reglay.setVisibility(View.VISIBLE);
                        break;
                    case R.id.tuichubt:
                        System.exit(0);
                        break;
                    case R.id.slistup:
                        if (logind.this.slistpage > 1) {
                            logind.this.slistpage--;
                            logind.this.turn_page = false;
                            enter_list();
                            break;
                        }
                        break;
                    case R.id.slistdown:
                        logind.this.slistpage++;
                        logind.this.turn_page = false;
                        enter_list();
                        break;
                    case R.id.user_center:
                        logind.this.startActivity(new Intent(logind.this, ucenter.class));
                        break;
                    case R.id.noenter:
                        logind.this.slist.setVisibility(View.GONE);
                        logind.this.logind_lay.setVisibility(View.VISIBLE);
                        break;
                    case R.id.new_id:
                        myinput(R.id.new_id, "请输4-12位的ID(字母开头可包含数字)：");
                        break;
                    case R.id.new_pass:
                        myinput(R.id.new_pass, "请输入15位以内的密码：");
                        break;
                    case R.id.new_pass2:
                        myinput(R.id.new_pass2, "请再次输入密码：");
                        break;
                    case R.id.new_phone:
                        myinput(R.id.new_phone, "请输入11位数字的手机号：");
                        break;
                    case R.id.new_email:
                        myinput(R.id.new_email, "请输入你的邮箱号：");
                        break;
                    case R.id.reg_ok:
                        checkreg();
                        break;
                    case R.id.qx_reg:
                        logind.this.reglay.setVisibility(View.GONE);
                        logind.this.logind_lay.setVisibility(View.VISIBLE);
                        break;
                    case R.id.newregname:
                        myinput(R.id.newregname, "请输你游戏中要用的名字(中文)：");
                        break;
                    case R.id.nicok:
                        logind.this.mengban1.setVisibility(View.VISIBLE);
                        mudsocketd.mudout(logind.this.sex + "║║" + logind.this.regname.getText().toString());
                        break;
                    case R.id.nicqx:
                        System.exit(0);
                        break;
                }
            }
        };
        this.user_center.setOnClickListener(onClickListener);
        this.nameedit.setOnClickListener(onClickListener);
        this.passedit.setOnClickListener(onClickListener);
        this.regid.setOnClickListener(onClickListener);
        this.regpass.setOnClickListener(onClickListener);
        this.regpass2.setOnClickListener(onClickListener);
        this.regname.setOnClickListener(onClickListener);
        this.regphone.setOnClickListener(onClickListener);
        this.regemail.setOnClickListener(onClickListener);
        this.denglub.setOnClickListener(onClickListener);
        this.tuichub.setOnClickListener(onClickListener);
        this.zhuceb.setOnClickListener(onClickListener);
        this.regok.setOnClickListener(onClickListener);
        this.noreg.setOnClickListener(onClickListener);
        this.new_part.setOnClickListener(onClickListener);
        this.last_part.setOnClickListener(onClickListener);
        this.noenter.setOnClickListener(onClickListener);
        this.nicok.setOnClickListener(onClickListener);
        this.nicqx.setOnClickListener(onClickListener);
        this.slistup.setOnClickListener(onClickListener);
        this.slistdown.setOnClickListener(onClickListener);
        this.loginT = new Thread(() -> {
            while (logind.this.loop) {
                logind.this.content = logind.this.mudsock.mudin();
                if (logind.this.content != null && logind.this.content.length() > 3) {
                    logind.this.mHandler.sendMessage(logind.this.mHandler.obtainMessage(1));
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException exception) {
                    }
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException exception) {
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://mud1.foxmoe.top/mobi/vers.php";
                    logind.this.result = logind.this.doGet(url);
                    if (logind.this.result.equals("Error")) {
                        logind.this.result = "服务器链接错误！";
                    }
                } catch (Exception exception) {
                    logind.this.result = "服务器链接错误！";
                }
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException exception) {
                }
                logind.this.content = logind.this.result;
                logind.this.mHandler.sendMessage(logind.this.mHandler.obtainMessage(8));
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    public void unzipok() {
    }
}

