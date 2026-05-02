package com.zwyouto.myactivex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.zwyouto.R;
import com.zwyouto.main.mudsocketd;

import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ucenter extends Activity {
    private Button add_my_server;
    private Button add_server_ok;
    private Button bang_email;
    private Button buy_my_server;
    private Button gb_new_server;
    private Button gb_ucenter;
    private Button mod_pass;
    private Button mod_phone;
    private mudsocketd mudsock;
    private TextView my_server_ip;
    private TextView my_server_name;
    private TextView my_server_port;
    private LinearLayout new_server_lay;
    private TextView now_part_name;
    private TextView now_pay_part;
    private Button pay_moneybt;
    protected String result;
    private int scrw;
    private EditText server_ip_edit;
    private EditText server_name_edit;
    private EditText server_port_edit;
    private TextView ucenter_email;
    private TextView ucenter_id;
    private TextView ucenter_pass;
    private TextView ucenter_phone;
    private TextView ucenter_vip;
    private final DisplayMetrics pingmu = new DisplayMetrics();
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 2) {
                String[] strArrSplit = ucenter.this.result.substring(3).split("\\|");
                ucenter.this.ucenter_id.setText("账\u3000号：" + strArrSplit[0]);
                ucenter.this.ucenter_pass.setText("密\u3000码：" + strArrSplit[1]);
                ucenter.this.ucenter_phone.setText("手机号：" + strArrSplit[2]);
                ucenter.this.ucenter_email.setText("邮\u3000箱：" + strArrSplit[3]);
                if (strArrSplit[4].equals("null")) {
                    return;
                }
                String[] strArrSplit2 = strArrSplit[4].split("&");
                ucenter.this.my_server_name.setText("名\u3000称：" + strArrSplit2[0]);
                ucenter.this.my_server_ip.setText("地\u3000址：" + strArrSplit2[1]);
                ucenter.this.my_server_port.setText("端\u3000口：" + strArrSplit2[2]);
                return;
            }
            if (message.what == 3) {
                Toast.makeText(ucenter.this, ucenter.this.result, Toast.LENGTH_SHORT).show();
                return;
            }
            if (message.what == 4) {
                if (ucenter.this.result.indexOf("邮箱绑定成功") != 0) {
                    Toast.makeText(ucenter.this, ucenter.this.result, Toast.LENGTH_SHORT).show();
                    return;
                }
                ucenter.this.ucenter_email.setText("邮\u3000箱：" + ucenter.this.result.replace("邮箱绑定成功", ""));
                ucenter.this.mudsock.set_ucenter_email(ucenter.this.result.replace("邮箱绑定成功", ""));
                Toast.makeText(ucenter.this, "邮箱绑定成功！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (message.what == 5) {
                if (ucenter.this.result.indexOf("密码修改成功") != 0) {
                    Toast.makeText(ucenter.this, ucenter.this.result, Toast.LENGTH_SHORT).show();
                    return;
                }
                ucenter.this.ucenter_pass.setText("密\u3000码：" + ucenter.this.result.replace("密码修改成功", ""));
                ucenter.this.mudsock.set_ucenter_pass(ucenter.this.result.replace("密码修改成功", ""));
                Toast.makeText(ucenter.this, "密码修改成功！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (message.what == 6) {
                if (ucenter.this.result.indexOf("手机号绑定成功") != 0) {
                    Toast.makeText(ucenter.this, ucenter.this.result, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    ucenter.this.ucenter_phone.setText("手机号：" + ucenter.this.result.replace("手机号绑定成功", ""));
                    Toast.makeText(ucenter.this, "手机号绑定成功！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (message.what == 7) {
                if (ucenter.this.result.indexOf("测试服务器修改成功") != 0) {
                    Toast.makeText(ucenter.this, ucenter.this.result, Toast.LENGTH_SHORT).show();
                    return;
                }
                ucenter.this.result = ucenter.this.result.replace("测试服务器修改成功", "");
                Toast.makeText(ucenter.this, "测试服务器修改成功，请重新登录以更新服务器列表！", Toast.LENGTH_SHORT).show();
                String[] strArrSplit3 = ucenter.this.result.split("&");
                ucenter.this.my_server_name.setText("名\u3000称：" + strArrSplit3[0]);
                ucenter.this.my_server_ip.setText("地\u3000址：" + strArrSplit3[1]);
                ucenter.this.my_server_port.setText("端\u3000口：" + strArrSplit3[2]);
                ucenter.this.new_server_lay.setVisibility(View.GONE);
            }
        }
    };

    class ViewOnClickListener implements View.OnClickListener {
        ViewOnClickListener() {
        }

        @Override
        public void onClick(View view) {
            final EditText editText = new EditText(ucenter.this);
            switch (view.getId()) {
                case R.id.pay_moneybt:
                    ucenter.this.now_pay_part.setText("当前分区：" + ucenter.this.mudsock.getgamepart());
                    break;
                case R.id.mod_pass:
                    new AlertDialog.Builder(ucenter.this).setTitle("请输入新密码：").setCancelable(false).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (editText.getText().toString().equals("")) {
                                Toast.makeText(ucenter.this.getBaseContext(), "新密码不能为空！", Toast.LENGTH_SHORT).show();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            OkHttpClient client = new OkHttpClient();
                                            Request request = new Request.Builder()
                                                    .url("https://mud1.foxmoe.top/mobi/updateuser.php?id=" + ucenter.this.mudsock.get_ucenter_id() + "&pass=" + ucenter.this.mudsock.get_ucenter_pass() + "&newpwd=" + editText.getText().toString())
                                                    .build();
                                            Response response = client.newCall(request).execute();
                                            if (response.isSuccessful()) {
                                                ucenter.this.result = response.body().string();
                                                ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(5));
                                            } else {
                                                ucenter.this.result = "密码修改失败，服务器链接错误！";
                                                ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                                            }
                                        } catch (Exception e) {
                                            ucenter.this.result = "密码修改失败，请检查网络！";
                                            ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                                        }
                                    }
                                }).start();
                            }
                        }
                    }).setNegativeButton("取消", null).show();
                    break;
                case R.id.mod_phone:
                    editText.setInputType(3);
                    new AlertDialog.Builder(ucenter.this).setTitle("请输入新手机号：").setCancelable(false).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (editText.getText().toString().equals("")) {
                                Toast.makeText(ucenter.this.getBaseContext(), "新手机号不能为空！", Toast.LENGTH_SHORT).show();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            OkHttpClient client = new OkHttpClient();
                                            Request request = new Request.Builder()
                                                    .url("https://mud1.foxmoe.top/mobi/updateuser.php?id=" + ucenter.this.mudsock.get_ucenter_id() + "&pass=" + ucenter.this.mudsock.get_ucenter_pass() + "&phone=" + editText.getText().toString())
                                                    .build();
                                            Response response = client.newCall(request).execute();
                                            if (response.isSuccessful()) {
                                                ucenter.this.result = response.body().string();
                                                ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(6));
                                            } else {
                                                ucenter.this.result = "手机号修改失败，服务器链接错误！";
                                                ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                                            }
                                        } catch (Exception e) {
                                            ucenter.this.result = "手机号修改失败，请检查网络！";
                                            ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                                        }
                                    }
                                }).start();
                            }
                        }
                    }).setNegativeButton("取消", null).show();
                    break;
                case R.id.bang_email:
                    new AlertDialog.Builder(ucenter.this).setTitle("请输入你要绑定的email：").setCancelable(false).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (editText.getText().toString().equals("") || editText.getText().toString().indexOf("@") < 1 || editText.getText().toString().indexOf("@") > editText.getText().toString().length() - 2) {
                                Toast.makeText(ucenter.this.getBaseContext(), "请填写正确的email格式！", Toast.LENGTH_SHORT).show();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            OkHttpClient client = new OkHttpClient();
                                            Request request = new Request.Builder()
                                                    .url("https://mud1.foxmoe.top/mobi/updateuser.php?id=" + ucenter.this.mudsock.get_ucenter_id() + "&pass=" + ucenter.this.mudsock.get_ucenter_pass() + "&email=" + editText.getText().toString())
                                                    .build();
                                            Response response = client.newCall(request).execute();
                                            if (response.isSuccessful()) {
                                                ucenter.this.result = response.body().string();
                                                ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(4));
                                            } else {
                                                ucenter.this.result = "绑定失败，服务器链接错误！";
                                                ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                                            }
                                        } catch (Exception e) {
                                            ucenter.this.result = "绑定失败，请检查网络！";
                                            ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                                        }
                                    }
                                }).start();
                            }
                        }
                    }).setNegativeButton("取消", null).show();
                    break;
                case R.id.add_my_server:
                    ucenter.this.new_server_lay.setVisibility(View.VISIBLE);
                    ucenter.this.server_name_edit.setText(ucenter.this.my_server_name.getText().toString().replace("名\u3000称：", ""));
                    ucenter.this.server_ip_edit.setText(ucenter.this.my_server_ip.getText().toString().replace("地\u3000址：", ""));
                    ucenter.this.server_port_edit.setText(ucenter.this.my_server_port.getText().toString().replace("端\u3000口：", ""));
                    break;
                case R.id.gb_ucenter:
                    ucenter.this.finish();
                    break;
                case R.id.add_server_ok:
                    if (!ucenter.this.server_name_edit.getText().toString().equals("") && !ucenter.this.server_ip_edit.getText().toString().equals("") && !ucenter.this.server_port_edit.getText().toString().equals("")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url("https://mud1.foxmoe.top/mobi/updateuser.php?id=" + ucenter.this.mudsock.get_ucenter_id() + "&pass=" + ucenter.this.mudsock.get_ucenter_pass() + "&myserver=" + URLEncoder.encode(ucenter.this.server_name_edit.getText().toString() + "&" + ucenter.this.server_ip_edit.getText().toString() + "&" + ucenter.this.server_port_edit.getText().toString() + "&" + (Integer.parseInt(ucenter.this.server_port_edit.getText().toString()) + 1), "UTF-8"))
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    if (response.isSuccessful()) {
                                        ucenter.this.result = response.body().string();
                                        ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(7));
                                    } else {
                                        ucenter.this.result = "修改失败，服务器链接错误！";
                                        ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                                    }
                                } catch (Exception e) {
                                    ucenter.this.result = "修改失败，请检查网络！";
                                    ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                                }
                            }
                        }).start();
                        break;
                    } else {
                        Toast.makeText(ucenter.this.getBaseContext(), "请保证各项均不为空！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                case R.id.gb_new_server:
                    ucenter.this.new_server_lay.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.ucenterx);
        this.mudsock = (mudsocketd) getApplication();
        getWindowManager().getDefaultDisplay().getMetrics(this.pingmu);
        this.scrw = this.pingmu.widthPixels;
        if (Build.VERSION.SDK_INT >= 11) {
            setFinishOnTouchOutside(false);
        }
        this.new_server_lay = findViewById(R.id.new_server_lay);
        this.now_pay_part = findViewById(R.id.now_pay_part);
        this.now_part_name = findViewById(R.id.now_part_name);
        this.now_part_name.getLayoutParams().height = this.scrw / 10;
        this.my_server_ip = findViewById(R.id.my_server_ip);
        this.my_server_ip.getLayoutParams().height = this.scrw / 10;
        this.server_name_edit = findViewById(R.id.server_name_edit);
        this.server_ip_edit = findViewById(R.id.server_ip_edit);
        this.server_port_edit = findViewById(R.id.server_port_edit);
        this.my_server_port = findViewById(R.id.my_server_port);
        this.my_server_port.getLayoutParams().height = this.scrw / 10;
        this.my_server_name = findViewById(R.id.my_server_name);
        this.my_server_name.getLayoutParams().height = this.scrw / 10;
        this.ucenter_phone = findViewById(R.id.ucenter_phone);
        this.ucenter_phone.getLayoutParams().height = this.scrw / 10;
        this.ucenter_vip = findViewById(R.id.ucenter_server);
        this.ucenter_vip.getLayoutParams().height = this.scrw / 10;
        this.ucenter_email = findViewById(R.id.ucenter_email);
        this.ucenter_email.getLayoutParams().height = this.scrw / 10;
        this.ucenter_pass = findViewById(R.id.ucenter_pass);
        this.ucenter_pass.getLayoutParams().height = this.scrw / 10;
        this.ucenter_id = findViewById(R.id.ucenter_id);
        this.ucenter_id.getLayoutParams().height = this.scrw / 10;
        this.pay_moneybt = findViewById(R.id.pay_moneybt);
        this.pay_moneybt.getLayoutParams().height = this.scrw / 10;
        this.add_server_ok = findViewById(R.id.add_server_ok);
        this.add_server_ok.getLayoutParams().height = this.scrw / 9;
        this.gb_new_server = findViewById(R.id.gb_new_server);
        this.gb_new_server.getLayoutParams().height = this.scrw / 9;
        this.add_my_server = findViewById(R.id.add_my_server);
        this.add_my_server.getLayoutParams().height = this.scrw / 9;
        this.buy_my_server = findViewById(R.id.buy_my_server);
        this.buy_my_server.getLayoutParams().height = this.scrw / 10;
        this.mod_phone = findViewById(R.id.mod_phone);
        this.mod_phone.getLayoutParams().height = this.scrw / 10;
        this.mod_pass = findViewById(R.id.mod_pass);
        this.mod_pass.getLayoutParams().height = this.scrw / 10;
        this.bang_email = findViewById(R.id.bang_email);
        this.bang_email.getLayoutParams().height = this.scrw / 10;
        this.gb_ucenter = findViewById(R.id.gb_ucenter);
        this.gb_ucenter.getLayoutParams().height = this.scrw / 9;
        ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
        this.pay_moneybt.setOnClickListener(viewOnClickListener);
        this.gb_new_server.setOnClickListener(viewOnClickListener);
        this.add_my_server.setOnClickListener(viewOnClickListener);
        this.add_server_ok.setOnClickListener(viewOnClickListener);
        this.buy_my_server.setOnClickListener(viewOnClickListener);
        this.mod_phone.setOnClickListener(viewOnClickListener);
        this.mod_pass.setOnClickListener(viewOnClickListener);
        this.gb_ucenter.setOnClickListener(viewOnClickListener);
        this.bang_email.setOnClickListener(viewOnClickListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://mud1.foxmoe.top/mobi/userinfo.php?id=" + ucenter.this.mudsock.get_ucenter_id() + "&pass=" + ucenter.this.mudsock.get_ucenter_pass())
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        ucenter.this.result = response.body().string();
                        if (ucenter.this.result.indexOf("$i#") == 0) {
                            ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(2));
                        } else {
                            ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                        }
                    } else {
                        ucenter.this.result = "绑定失败，服务器链接错误！";
                        ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                    }
                } catch (Exception e) {
                    ucenter.this.result = "绑定失败，请检查网络！";
                    ucenter.this.mHandler.sendMessage(ucenter.this.mHandler.obtainMessage(3));
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        switch (i) {
            case 4:
                finish();
                break;
        }
        return false;
    }
}
