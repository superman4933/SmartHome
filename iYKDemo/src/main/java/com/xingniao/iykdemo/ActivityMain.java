package com.xingniao.iykdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityMain extends Activity {
    public static ConnetListening mConnetListening = null;
    public static byte[] shortredcodeA = new byte[512], shortredcodeB = new byte[512];
    public static byte[] longredcodeA = new byte[1024], longredcodeB = new byte[1024];
    public static byte[] m315codeA = new byte[12], m315codeB = new byte[12],
            m315codeC = new byte[12], m315codeD = new byte[12];
    private byte[] zuheredcodeA = new byte[512], zuheredcodeB = new byte[512], zuheredcodeC = new byte[512], zuheredcodeD = new byte[512];

    private String ip;
    private List<ScanResult> mWifiList;
    private static Handler mmHandler = null;
    private boolean bshortredflagA = false, bshortredflagB = false, blongredflagA = false, blongredflagB = false;
    private boolean b315flagA = false, b315flagB = false, b315flagC = false, b315flagD = false;
    private boolean bzuheflag = false;
    private int b315id, bshortredid, blongredid;
    private final CharSequence[] items = {"学习315M射频码", "创建315M射频编码", "取消"};
    private String helpstudy = "请将遥控器对准设备，并按下要学习的按键！";
    private boolean StudyCodeCheckwork = false;
    private boolean studybuttonsflag = false;
    private int studybuttonCount = 0;
    private SendCodeMoreButtonThred mSendCodeMoreButtonThred;
    private StudyCodeCheckThred mStudyCodeCheckThred;
    private StudyCodeMoreButtonThred mStudyCodeMoreButtonThred;
    private CmdCheck mCmdCheck;
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConnetListening = new ConnetListening(ActivityMain.this, mHandler);
        mConnetListening.StartListen();
        mConnetListening.ConnectWifiScan();
        buttononclick();

    }

    private void buttononclick() {
        Button b315a = (Button) findViewById(R.id.button_315a);
        b315a.setOnClickListener(new m315onclick());

        Button b315b = (Button) findViewById(R.id.button_315b);
        b315b.setOnClickListener(new m315onclick());

        Button b315c = (Button) findViewById(R.id.button_315c);
        b315c.setOnClickListener(new m315onclick());

        Button b315d = (Button) findViewById(R.id.button_315d);
        b315d.setOnClickListener(new m315onclick());

        Button bshortredA = (Button) findViewById(R.id.button_shortredA);
        bshortredA.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                bshortredid = v.getId();
                if (bshortredflagA) {
                    mConnetListening.SendCmdCode(0x32, shortredcodeA, shortredcodeA.length, ip);
                } else {
                    final byte[] buf = null;
                    if (ip == null || ip.equals("")) return;
                    mConnetListening.SendCmdCode(0x30, buf, 0, ip);
                }
            }
        });

        Button bshortredB = (Button) findViewById(R.id.button_shortredB);
        bshortredB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                bshortredid = v.getId();
                if (bshortredflagB) {
                    mConnetListening.SendCmdCode(0x32, shortredcodeB, shortredcodeB.length, ip);
                } else {
                    final byte[] buf = null;
                    if (ip == null || ip.equals("")) return;
                    mConnetListening.SendCmdCode(0x30, buf, 0, ip);
                }
            }
        });

        Button blongredA = (Button) findViewById(R.id.button_longredA);
        blongredA.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                blongredid = v.getId();
                if (blongredflagA) {
                    mConnetListening.SendCmdCode(0x09, longredcodeA, longredcodeA.length, ip);
                } else {
                    final byte[] buf = null;
                    if (ip == null || ip.equals("")) return;
                    mConnetListening.SendCmdCode(0x06, buf, 0, ip);
                }
            }
        });

        Button blongredB = (Button) findViewById(R.id.button_longredB);
        blongredB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                blongredid = v.getId();
                if (blongredflagB) {
                    mConnetListening.SendCmdCode(0x09, longredcodeB, longredcodeB.length, ip);
                } else {
                    final byte[] buf = null;
                    if (ip == null || ip.equals("")) return;
                    mConnetListening.SendCmdCode(0x06, buf, 0, ip);
                }
            }
        });

        Button bzuhered = (Button) findViewById(R.id.button_zuhered);
        bzuhered.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (bzuheflag) {

                    mSendCodeMoreButtonThred = new SendCodeMoreButtonThred();
                    mSendCodeMoreButtonThred.start();
                } else {
                    final byte[] buf = null;
                    if (ip == null || ip.equals("")) return;
                    studybuttonsflag = true;
                    studybuttonCount = 0;
                    mConnetListening.SendCmdCode(0x30, buf, 0, ip);
                }
            }
        });

        Button bgetip = (Button) findViewById(R.id.button_getip);
        bgetip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DeviceList();

            }
        });


        Button bnetset = (Button) findViewById(R.id.button_netset);
        bnetset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(ActivityMain.this, ActivityNet.class);
                startActivity(intent);
            }
        });
    }

    private class m315onclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            b315id = v.getId();
            switch (v.getId()) {
                case R.id.button_315a:
                    if (b315flagA) {
                        mConnetListening.SendCmdCode(0x0D, m315codeA, m315codeA.length, ip);
                    } else {
                        ButtonAlertDialog315();
                    }
                    break;
                case R.id.button_315b:
                    if (b315flagB) {
                        mConnetListening.SendCmdCode(0x0D, m315codeB, m315codeB.length, ip);
                    } else {
                        ButtonAlertDialog315();
                    }
                    break;
                case R.id.button_315c:
                    if (b315flagC) {
                        mConnetListening.SendCmdCode(0x0D, m315codeC, m315codeC.length, ip);
                    } else {
                        ButtonAlertDialog315();
                    }
                    break;
                case R.id.button_315d:
                    if (b315flagD) {
                        mConnetListening.SendCmdCode(0x0D, m315codeD, m315codeD.length, ip);
                    } else {
                        ButtonAlertDialog315();
                    }
                    break;
            }
        }

    }

    private void ButtonAlertDialog315() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
        builder.setTitle("提示！");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        final byte[] buf = null;
                        if (ip == null || ip.equals("")) return;
                        mConnetListening.SendCmdCode(0x0A, buf, 0, ip);
                        break;
                    case 1:
                        CreateSendCode315M();
                        break;
                }
            }
        });
        AlertDialog mAlerDialog = builder.create();
        mAlerDialog.show();


    }

    private void CreateSendCode315M() {

        final Calendar c = Calendar.getInstance();
        byte[] sendcode = new byte[12];
        int second = c.get(Calendar.HOUR) * 3600 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);
        // 地址码高位0
        switch ((int) (second / 34992)) {
            case 0:
                sendcode[0] = 0;
                break;
            case 1:
                sendcode[0] = 1;
                break;
            case 2:
                sendcode[0] = 0x0f;
                break;
        }
        // 地址码高位1
        switch ((int) ((second % 34992) / 11664)) {
            case 0:
                sendcode[1] = 0;
                break;
            case 1:
                sendcode[1] = 1;
                break;
            case 2:
                sendcode[1] = 0x0f;
                break;
        }

        // 地址码高位2
        switch ((int) ((second % 11664) / 3888)) {
            case 0:
                sendcode[2] = 0;
                break;
            case 1:
                sendcode[2] = 1;
                break;
            case 2:
                sendcode[2] = 0x0f;
                break;
        }
        // 地址码高位3
        switch ((int) ((second % 3888) / 1296)) {
            case 0:
                sendcode[3] = 0;
                break;
            case 1:
                sendcode[3] = 1;
                break;
            case 2:
                sendcode[3] = 0x0f;
                break;
        }

        // 地址码高位4
        switch ((int) ((second % 1296) / 432)) {
            case 0:
                sendcode[4] = 0;
                break;
            case 1:
                sendcode[4] = 1;
                break;
            case 2:
                sendcode[4] = 0x0f;
                break;
        }
        // 地址码高位5
        switch ((int) ((second % 432) / 144)) {
            case 0:
                sendcode[5] = 0;
                break;
            case 1:
                sendcode[5] = 1;
                break;
            case 2:
                sendcode[5] = 0x0f;
                break;
        }

        // 地址码高位6
        switch ((int) ((second % 144) / 48)) {
            case 0:
                sendcode[6] = 0;
                break;
            case 1:
                sendcode[6] = 1;
                break;
            case 2:
                sendcode[6] = 0x0f;
                break;
        }
        // 地址码高位7
        switch ((int) ((second % 48) / 16)) {
            case 0:
                sendcode[7] = 0;
                break;
            case 1:
                sendcode[7] = 1;
                break;
            case 2:
                sendcode[7] = 0x0f;
                break;
        }
        // 地址码高位8
        switch ((int) ((second % 16) / 8)) {
            case 0:
                sendcode[8] = 0;
                break;
            case 1:
                sendcode[8] = 1;
                break;
        }
        // 地址码高位9
        switch ((int) ((second % 8) / 4)) {
            case 0:
                sendcode[9] = 0;
                break;
            case 1:
                sendcode[9] = 1;
                break;
        }
        // 地址码高位10
        switch ((int) ((second % 4) / 2)) {
            case 0:
                sendcode[10] = 0;
                break;
            case 1:
                sendcode[10] = 1;
                break;
        }
        // 地址码高位11
        switch ((int) (second % 2)) {
            case 0:
                sendcode[11] = 0;
                break;
            case 1:
                sendcode[11] = 1;
                break;
        }
        switch (b315id) {
            case R.id.button_315a:
                m315codeA = sendcode;
                b315flagA = true;
                break;
            case R.id.button_315b:
                m315codeB = sendcode;
                b315flagB = true;
                break;
            case R.id.button_315c:
                m315codeC = sendcode;
                b315flagC = true;
                break;
            case R.id.button_315d:
                m315codeD = sendcode;
                b315flagD = true;
                break;

        }
    }

    private class StudyCodeMoreButtonThred extends Thread {
        byte[] buf;

        public StudyCodeMoreButtonThred() {
        }

        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (studybuttonsflag) {
                mConnetListening.SendCmdCode(0x30, buf, 0, ip); // 0X30为短红外指令 0x06是长红外指令
            }
        }
    }

    private class SendCodeMoreButtonThred extends Thread {

        public void run() {
            for (int i = 0; i < studybuttonCount; i++) {
                //0X32短红外学习码 0x09为红外学习码
                switch (i) {
                    case 0:
                        mConnetListening.SendCmdCode(0x32, zuheredcodeA, zuheredcodeA.length, ip);
                        break;
                    case 1:
                        mConnetListening.SendCmdCode(0x32, zuheredcodeB, zuheredcodeB.length, ip);
                        break;
                    case 2:
                        mConnetListening.SendCmdCode(0x32, zuheredcodeC, zuheredcodeC.length, ip);
                        break;
                    case 3:
                        mConnetListening.SendCmdCode(0x32, zuheredcodeD, zuheredcodeD.length, ip);
                        break;
                }
                try {
                    Thread.sleep(1500);//1.5s
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    private class StudyCodeCheckThred extends Thread {
        private int checkcmd;
        private int stopcmd;
        byte[] buf;

        public StudyCodeCheckThred(int checkcmd, int stopcmd) {
            this.checkcmd = checkcmd;
            this.stopcmd = stopcmd;
        }

        public void run() {
            while (StudyCodeCheckwork) {
                try {
                    Thread.sleep(1500);//1.5s
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (StudyCodeCheckwork) {
                    mConnetListening.SendCmdCode(checkcmd, buf, 0, ip);//查询学习状态
                } else {
                    mConnetListening.SendCmdCode(stopcmd, buf, 0, ip);
                    break;
                }
            }
        }

        public void cancel() {
            StudyCodeCheckwork = false;
        }
    }

    private void CmdCheckState(final int cmd) {
        int code;
        Log.e("CmdCheckState", "code-->" + cmd);
        switch (cmd) {
            case (byte) 0x9C:
                Deviceinit(mCmdCheck.getCmdContentNoStateBuf());
                break;
            case (byte) 0x86://红外学习应答指令
            case (byte) 0xB0://短红外学习应答指令
                if (cmd == (byte) 0x86) {
                    code = 0x08;
                } else {
                    code = 0x31;
                }
                if (mStudyCodeCheckThred == null) {
                    mStudyCodeCheckThred = new StudyCodeCheckThred(code, 0x07);//开始查询学习状态；
                    StudyCodeCheckwork = true;
                    mStudyCodeCheckThred.start();
                } else {
                    mStudyCodeCheckThred.cancel();
                    mStudyCodeCheckThred = null;
                    mStudyCodeCheckThred = new StudyCodeCheckThred(code, 0x07);//开始查询学习状态；
                    StudyCodeCheckwork = true;
                    mStudyCodeCheckThred.start();
                }
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(ActivityMain.this, "提示！", "", true, true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    //mProgressDialog.setCancelable(false);
                }
                mProgressDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        if (mStudyCodeCheckThred != null) {
                            StudyCodeCheckwork = false;
                            mStudyCodeCheckThred.cancel();
                            mStudyCodeCheckThred = null;
                            mProgressDialog.dismiss();
                        }
                        studybuttonsflag = false;
                    }
                });
                mProgressDialog.setMessage("学习红外编码!" + "\n" + helpstudy);
                mProgressDialog.show();
                break;
            case (byte) 0x8A://315M学习应答指令
                Log.e("mCmdCheck-->", "case：0x8A");
                if (mStudyCodeCheckThred == null) {
                    mStudyCodeCheckThred = new StudyCodeCheckThred(0x0C, 0x0B);//开始查询学习状态；
                    StudyCodeCheckwork = true;
                    mStudyCodeCheckThred.start();
                } else {
                    mStudyCodeCheckThred.cancel();
                    mStudyCodeCheckThred = null;
                    mStudyCodeCheckThred = new StudyCodeCheckThred(0X0C, 0x0B);//开始查询学习状态；
                    StudyCodeCheckwork = true;
                    mStudyCodeCheckThred.start();
                }
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(ActivityMain.this, "提示！", "", true, true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                }
                mProgressDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        if (mStudyCodeCheckThred != null) {
                            StudyCodeCheckwork = false;
                            mStudyCodeCheckThred.cancel();
                            mStudyCodeCheckThred = null;
                            mProgressDialog.dismiss();
                        }
                    }
                });
                mProgressDialog.setMessage("学习315M射频编码!" + "\n" + helpstudy);
                mProgressDialog.show();
                break;
            case (byte) 0x88://红外查询应答指令
            case (byte) 0xB1://短红外查询应答指令
                if (mCmdCheck.getCmdSetState() != 1) break;
                StudyCodeCheckwork = false;
                mStudyCodeCheckThred.cancel();
                mStudyCodeCheckThred = null;
                byte[] buf = mCmdCheck.getCmdContentNoStateBuf();
                if (studybuttonsflag) {
                    switch (studybuttonCount) {
                        case 0:
                            zuheredcodeA = buf;
                            break;
                        case 1:
                            zuheredcodeB = buf;
                            break;
                        case 2:
                            zuheredcodeC = buf;
                            break;
                        case 3:
                            zuheredcodeD = buf;
                            break;
                    }
                    bzuheflag = true;
                    studybuttonCount++;
                    if (studybuttonCount >= 4) {
                        mConnetListening.SendCmdCode(0x07, buf, 0, ip);
                        mProgressDialog.dismiss();
                        studybuttonsflag = false;
                        studybuttonCount = 0;
                    } else {
                        mProgressDialog.setMessage("已学习" + String.valueOf(studybuttonCount) + "个红外编码！"
                                + "\n" + "请按提示再按下一个要学习的红外按键！");
                        mStudyCodeMoreButtonThred = new StudyCodeMoreButtonThred();
                        mStudyCodeMoreButtonThred.start();
                    }
                } else {
                    //MainActivity.mConnetListening.SendCmdCode(0x07,buf,0,deviceip);
                    if (cmd == (byte) 0x88) {
                        switch (blongredid) {
                            case R.id.button_longredA:
                                blongredflagA = true;
                                longredcodeA = buf;
                                break;
                            case R.id.button_longredB:
                                blongredflagB = true;
                                longredcodeB = buf;
                                break;
                        }
                    } else {
                        switch (bshortredid) {
                            case R.id.button_shortredA:
                                bshortredflagA = true;
                                shortredcodeA = buf;
                                break;
                            case R.id.button_shortredB:
                                bshortredflagB = true;
                                shortredcodeB = buf;
                                break;
                        }
                    }
                    mProgressDialog.dismiss();
                }
                break;
            case (byte) 0x87:
                if (!studybuttonsflag) {
                    if (mStudyCodeCheckThred != null) {
                        mStudyCodeCheckThred.cancel();
                        StudyCodeCheckwork = false;
                        mStudyCodeCheckThred = null;
                    }
                }
                break;
            case (byte) 0x8B:
                if (mStudyCodeCheckThred != null) {
                    mStudyCodeCheckThred.cancel();
                    StudyCodeCheckwork = false;
                    mStudyCodeCheckThred = null;
                }
                break;
            case (byte) 0x8C:
                if (mCmdCheck.getCmdSetState() != 1) break;
                if (mStudyCodeCheckThred != null) {
                    mStudyCodeCheckThred.cancel();
                    StudyCodeCheckwork = false;
                    mStudyCodeCheckThred = null;
                }
                mProgressDialog.dismiss();
                byte[] buff = mCmdCheck.getCmdContentNoStateBuf();
                switch (b315id) {
                    case R.id.button_315a:
                        m315codeA = buff;
                        b315flagA = true;
                        break;
                    case R.id.button_315b:
                        m315codeB = buff;
                        b315flagB = true;
                        break;
                    case R.id.button_315c:
                        m315codeC = buff;
                        b315flagC = true;
                        break;
                    case R.id.button_315d:
                        m315codeD = buff;
                        b315flagD = true;
                        break;
                }
                break;
        }

    }

    private void Deviceinit(byte[] buf) {
        String ss = new String(buf, 0, buf.length);
        String[] args = ss.split(",");
        ip = args[0];
        Log.e("GetIp-->", "ip:" + ip);
        mProgressDialog.dismiss();
        Toast toast = Toast.makeText(ActivityMain.this, "获取IP：" + ip, Toast.LENGTH_SHORT);
        toast.show();

    }
//获取设备ip方法
    private void DeviceList() {
        mWifiList = ActivityMain.mConnetListening.ConnectWifiScanResult();
        if (mWifiList == null) return;
        Map<String, Object> list = new HashMap<String, Object>();
        for (int i = 0, j = 0; i < mWifiList.size(); i++) {
            if (mWifiList.get(i).SSID.toString().contains("IYK")) {
                list.put(String.valueOf(j++), mWifiList.get(i).SSID.toString());
            }
        }
        if (list.size() == 0) return;
        final CharSequence[] item = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            item[i] = list.get(String.valueOf(i)).toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
        builder.setTitle("提示！");
        builder.setItems(item, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mConnetListening.getLinkIPBroadcast(item[which].toString());
                Log.e("ActivityMain", "getLinkIPBroadcast:" + item[which].toString());
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(ActivityMain.this, "提示！", "正在获取" + item[which].toString() + "的ip...", true, true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    Log.d("ActivityMain", "1");
                } else {
                    mProgressDialog.setMessage("正在获取" + item[which].toString() + "的ip...");
                    mProgressDialog.show();
                    Log.d("ActivityMain", "2");
                }
            }
        });
        AlertDialog mAlerDialog = builder.create();
        mAlerDialog.show();
    }

    public static void setHandler(Handler handler) {
        mmHandler = handler;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConnetListening.LISTENING_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ConnetListening.LISTENING_CONNECTED:

                            break;
                        case ConnetListening.LISTENING_CONNECTFAILD:

                            break;
                        case ConnetListening.LISTENING_NONE:

                            break;
                    }
                    break;
                case ConnetListening.LISTENING_MESSAGE_STATE:
                    switch (msg.arg1) {
                        case ConnetListening.MESSAGE_READSUCCESS:

                            mCmdCheck = new CmdCheck((byte[]) msg.obj, msg.arg2);
                            CmdCheckState(mCmdCheck.getCmd());
                            Log.e("ActivityMain", "MESSAGE_READSUCCESS" + (mCmdCheck.getCmd() & 0xff));
                            if (mmHandler != null) {
                                mmHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj).sendToTarget();
                            }
                            break;
                        case ConnetListening.MESSAGE_READFAILD:
                            Log.e("ActivityMain", "ConnetListening_MESSAGE_READFAILD");
                            break;
                    }
                    break;
            }
        }
    };

}
