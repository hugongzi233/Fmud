package com.zwyouto.main;

import android.app.Application;
import android.util.Log;
import com.zwyouto.mud.BuildConfig;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class mudsocketd extends Application {
    private static PrintWriter out;
    private boolean action;
    private String addr;
    private boolean bbact;
    private String content;
    private String gameip;
    private String gamekey;
    private String gamepart;
    private String gameport;
    private boolean gxact;

    private BufferedReader f4in;
    private boolean ingame;
    private boolean iscon;
    private boolean jobact;
    private String jobs;
    private boolean lookact;
    private boolean mailact;
    private String mubiaoid;
    private String mubiaoimg;
    private String mubiaojinengs;
    private String mubiaoname;
    private String mubiaowupins;
    private String myimg;
    private String mylevel;
    private String myname;
    private String myshort;
    private int port;
    private String senceexit;
    private String sencenpc;
    private String senceshort;
    private static Socket socket = null;
    private static boolean cmdss = true;
    private String pindaoa = "世界";
    private String pindaob = "chat";
    private String myid = "null";
    private String sencelong = "无";
    private String mubiaoinfo = "无";
    private String zhuzaowu = "无";
    private String mails = "无";
    private String mailtexts = "无";
    private String guanxi = "无";
    private String ucenter_id = BuildConfig.FLAVOR;
    private String ucenter_pass = BuildConfig.FLAVOR;
    private String ucenter_phone = BuildConfig.FLAVOR;
    private String ucenter_email = BuildConfig.FLAVOR;
    private String ucenter_key = BuildConfig.FLAVOR;
    private boolean canpay = true;

    public static void mudout(final String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mudsocketd.socket.isClosed() || mudsocketd.socket.isInputShutdown()) {
                    return;
                }
                Log.d("socket", mudsocketd.socket.toString() + "run: " + str);
                if (mudsocketd.cmdss) {
                    mudsocketd.out.println(str);
                } else {
                    mudsocketd.out.println(str.replace("\n", ";"));
                }
            }
        }).start();
    }

    public boolean canpay() {
        return this.canpay;
    }

    public String getAddr() {
        return this.addr;
    }

    public int getPort() {
        return this.port;
    }

    public boolean get_cmdss() {
        return cmdss;
    }

    public String get_ucenter_email() {
        return this.ucenter_email;
    }

    public String get_ucenter_id() {
        return this.ucenter_id;
    }

    public String get_ucenter_key() {
        return this.ucenter_key;
    }

    public String get_ucenter_pass() {
        return this.ucenter_pass;
    }

    public String get_ucenter_phone() {
        return this.ucenter_phone;
    }

    public String getgameip() {
        return this.gameip;
    }

    public String getgamekey() {
        return this.gamekey;
    }

    public String getgamepart() {
        return this.gamepart;
    }

    public String getgameport() {
        return this.gameport;
    }

    public String getguanxi() {
        return this.guanxi;
    }

    public String getjobs() {
        return this.jobs;
    }

    public String getmails() {
        return this.mails;
    }

    public String getmailtexts() {
        return this.mailtexts;
    }

    public String getmubiaoid() {
        return this.mubiaoid;
    }

    public String getmubiaoimg() {
        return this.mubiaoimg;
    }

    public String getmubiaoinfo() {
        return this.mubiaoinfo;
    }

    public String getmubiaojinengs() {
        return this.mubiaojinengs;
    }

    public String getmubiaoname() {
        return this.mubiaoname;
    }

    public String getmubiaowupins() {
        return this.mubiaowupins;
    }

    public String getmyid() {
        return this.myid;
    }

    public String getmyimg() {
        return this.myimg;
    }

    public String getmylevel() {
        return this.mylevel;
    }

    public String getmyname() {
        return this.myname;
    }

    public String getmyshort() {
        return this.myshort;
    }

    public String getpindaoa() {
        return this.pindaoa;
    }

    public String getpindaob() {
        return this.pindaob;
    }

    public String getsenceexit() {
        return this.senceexit;
    }

    public String getsencelong() {
        return this.sencelong;
    }

    public String getsencenpc() {
        return this.sencenpc;
    }

    public String getsenceshort() {
        return this.senceshort;
    }

    public String getzhuzaowu() {
        return this.zhuzaowu;
    }

    public boolean isaction() {
        return this.action;
    }

    public boolean isbbact() {
        return this.bbact;
    }

    public boolean isconing() {
        return this.iscon;
    }

    public boolean isgxact() {
        return this.gxact;
    }

    public boolean isjobact() {
        return this.jobact;
    }

    public boolean islookact() {
        return this.lookact;
    }

    public boolean ismailact() {
        return this.mailact;
    }

    public String mudin() {
        if (this.iscon) {
            return null;
        }
        if (socket == null || socket.isClosed() || !socket.isConnected() || socket.isInputShutdown()) {
            this.content = "连接已断开！";
        } else {
            if (this.sencelong != "无") {
                return null;
            }
            try {
                this.content = this.f4in.readLine();
            } catch (IOException e) {
                this.content = "连接已断开！";
            }
        }
        return this.content;
    }

    public void new_sock(String str, int i) {
        this.addr = str;
        this.port = i;
        this.action = false;
        this.iscon = true;
        this.ingame = false;
        if (socket != null && !socket.isClosed() && socket.isConnected()) {
            Log.d("socket", "socket close\n");
            try {
                socket.close();
            } catch (IOException e) {
                Log.d("socket", e.toString());
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket unused = mudsocketd.socket = new Socket(mudsocketd.this.addr, mudsocketd.this.port);
                    mudsocketd.socket.setKeepAlive(true);
                    mudsocketd.this.f4in = new BufferedReader(new InputStreamReader(mudsocketd.socket.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter unused2 = mudsocketd.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mudsocketd.socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                    mudsocketd.this.action = true;
                    mudsocketd.this.iscon = false;
                    Log.d("socket", "newsock...\n");
                } catch (Exception e2) {
                    Log.d("socket", e2.toString());
                    mudsocketd.this.action = false;
                    mudsocketd.this.iscon = false;
                }
            }
        }).start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String recon() {
        this.iscon = true;
        new_sock(this.addr, this.port);
        return "连接错误，正在重新连接...";
    }

    public void setAddr(String str) {
        this.addr = str;
        this.gameip = str;
    }

    public void setPort(int i) {
        this.port = i;
        this.gameport = String.valueOf(i);
    }

    public void set_cmdss(Boolean bool) {
        cmdss = bool.booleanValue();
    }

    public void set_ucenter_email(String str) {
        this.ucenter_email = str;
    }

    public void set_ucenter_id(String str) {
        this.ucenter_id = str;
    }

    public void set_ucenter_key(String str) {
        this.ucenter_key = str;
    }

    public void set_ucenter_pass(String str) {
        this.ucenter_pass = str;
    }

    public void set_ucenter_phone(String str) {
        this.ucenter_phone = str;
    }

    public void setbbact(boolean z) {
        this.bbact = z;
    }

    public void setgameip(String str) {
        this.gameip = str;
    }

    public void setgamekey(String str) {
        this.gamekey = str;
    }

    public void setgamepart(String str) {
        this.gamepart = str;
    }

    public void setgameport(String str) {
        this.gameport = str;
    }

    public void setguanxi(String str) {
        this.guanxi = str;
    }

    public void setgxact(boolean z) {
        this.gxact = z;
    }

    public void setjobact(boolean z) {
        this.jobact = z;
    }

    public void setjobs(String str) {
        this.jobs = str;
    }

    public void setlookact(boolean z) {
        this.lookact = z;
    }

    public void setmailact(boolean z) {
        this.mailact = z;
    }

    public void setmails(String str) {
        this.mails = str;
    }

    public void setmailtexts(String str) {
        this.mailtexts = str;
    }

    public void setmubiaoid(String str) {
        this.mubiaoid = str;
    }

    public void setmubiaoimg(String str) {
        this.mubiaoimg = str;
    }

    public void setmubiaoinfo(String str) {
        this.mubiaoinfo = str;
    }

    public void setmubiaojinengs(String str) {
        this.mubiaojinengs = str;
    }

    public void setmubiaoname(String str) {
        this.mubiaoname = str;
    }

    public void setmubiaowupins(String str) {
        this.mubiaowupins = str;
    }

    public void setmyshort(String str) {
        this.myshort = str;
        this.myname = this.myshort.split("/")[0];
        this.myid = this.myshort.split("/")[1];
        this.mylevel = this.myshort.split("/")[2];
        this.myimg = this.myshort.split("/")[3];
    }

    public void setpay(boolean z) {
        this.canpay = z;
    }

    public void setpindaoa(String str) {
        this.pindaoa = str;
    }

    public void setpindaob(String str) {
        this.pindaob = str;
    }

    public void setsenceexit(String str) {
        this.senceexit = str;
    }

    public void setsencelong(String str) {
        this.sencelong = str;
    }

    public void setsencenpc(String str) {
        this.sencenpc = str;
    }

    public void setsenceshort(String str) {
        this.senceshort = str;
    }

    public void setzhuzaowu(String str) {
        this.zhuzaowu = str;
    }

    public void shut() {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            return;
        }
        socket = null;
        this.f4in = null;
        out = null;
    }
}

