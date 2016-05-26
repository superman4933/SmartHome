package com.xingniao.iykdemo;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class ConnetListening {
    private String TAG = "ConnetListening";
    public static int mlistenState;
    private static int mMsgState;
    private static int mCmdState = 0;

    public static final int CMDSTATE_NONE = 1;  // now connected to a remote device
    public static final int CMDSTATE_SENGSUCCESS = 2; // now initiating an outgoing connection
    public static final int CMDSTATE_SENGING = 3;  // now connected to a remote device
    public static final int CMDSTATE_SENGFAILD = 4;  // now connected to a remote device

    public static final int LISTENING_STATE_CHANGE = 3;//防止与ConnectService冲突
    public static final int LISTENING_MESSAGE_STATE = 4;

    public static final int LISTENING_NONE = 0;       // we're doing nothing
    public static final int LISTENING_CONNECTING = 1; // now initiating an outgoing connection
    public static final int LISTENING_CONNECTED = 2;  // now connected to a remote device
    public static final int LISTENING_CONNECTFAILD = 3;  // now connected to a remote device

    public static final int MESSAGE_WRITESUCCESS = 1;       // we're doing nothing
    public static final int MESSAGE_WRITEFAILD = 2; // now initiating an outgoing connection
    public static final int MESSAGE_WRITEING = 6; // now initiating an outgoing connection
    public static final int MESSAGE_WRITENONE = 7; // now initiating an outgoing connection
    public static final int MESSAGE_READSUCCESS = 3;
    public static final int MESSAGE_READFAILD = 4;
    public static final int MESSAGE_READLOST = 5;
    private static final int BUFFERLENGTH = 1024; //缓冲大小
    private static final int LISTENPORT = 8888; //监听端口
    private boolean onWork = false;    //线程工作标识
    private Context context;
    private static Handler mHandler;
    private WifiAdmin mwifiAdmin;
    private WifiInfo mWifiInfo;
    private CmdCheck mCmdCheck;
    private ConnetSendPacketThread mConnetSendPacketThread = null;
    private ConnetReceivePacketThread UDPPortListenThread = null;    //接收UDP数据线程
    private ListeningSocketThread mListeningSocketThread = null;    //接收UDP数据线程
    private DatagramSocket UDPSocket = null;    //用于接收和发送udp数据的socket
    private DatagramPacket UDPSendPacket = null;    //用于发送的udp数据包
    private DatagramPacket UDPResPacket = null;    //用于接收的udp数据包
    private byte[] resBuffer = new byte[BUFFERLENGTH];    //接收数据的缓存
    private byte[] sendBuffer = new byte[BUFFERLENGTH];
    private int sendcmd = 0;
    private int sendLen;
    private InetAddress sendIp;
    private int sendPort;
    private boolean UDPSendPacketFlag = false;
    private int UDPSendPacketcheckrescount = 0;
    private WifiManager.MulticastLock lock;
    private static int readbyteslen; // 数据缓存长度
    private static byte[] readBuf = new byte[2048]; //数据缓存
    private static int readcheckcount = 0; //校检次数

    public ConnetListening(Context context, Handler handler) {
        this.context = context;
        mHandler = handler;
        mwifiAdmin = new WifiAdmin(context);
        mwifiAdmin.openWifi();
        mWifiInfo = mwifiAdmin.getWifiInfo();
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("localWifi");//获取多点广播的锁，因为要打开广播
        // Log.e("mWifiInfo",mWifiInfo.getSSID());
        // Log.e("mWifiInfo",mWifiInfo.getMacAddress());
        // Log.e("mWifiInfo",Formatter.formatIpAddress(mWifiInfo.getIpAddress()));
        //StartListen();              				
    }

    public static boolean ReadCheck(byte[] Buf, int len) {

        int i = 0;
        int check = 0;
        readcheckcount++;
        for (i = 0; i < len; i++) {
            readBuf[readbyteslen++] = Buf[i];
        }
        for (i = 0; i < readbyteslen; i++) {
            switch (i) {
                case 0:
                    if (readBuf[0] != (byte) 0xF7) {
                        readbyteslen = 0;
                        readcheckcount = 0;//校验次数
                        mMsgState = MESSAGE_READFAILD;
                        mHandler.obtainMessage(LISTENING_MESSAGE_STATE, mMsgState, -1).sendToTarget();
                        return false;
                    }
                    break;
                case 1:
                    if (readBuf[1] != (byte) 0x7F) {
                        readbyteslen = 0;
                        readcheckcount = 0;
                        mMsgState = MESSAGE_READFAILD;
                        mHandler.obtainMessage(LISTENING_MESSAGE_STATE, mMsgState, -1).sendToTarget();
                        return false;
                    }
                    break;
            }
        }
        if (readbyteslen >= 6) { // 已收到这一帧数据的长度
            int readbyteslen0 = (readBuf[5] & 0xff) * 256 + (readBuf[6] & 0xff);
            if (readbyteslen0 == readbyteslen) {
                check = 0;
                for (i = 0; i < readbyteslen - 1; i++) {
                    check = check + readBuf[i] & 0xff;
                }
                check = check + 1;
                if (((check) & 0xff) == (readBuf[readbyteslen - 1] & 0xff)) {
                    readcheckcount = 0;
                    readbyteslen = 0;
                    return true;
                } else {
                    readcheckcount = 0;
                    readbyteslen = 0;
                    mMsgState = MESSAGE_READFAILD;
                    mHandler.obtainMessage(LISTENING_MESSAGE_STATE, mMsgState, -1).sendToTarget();
                    return false;
                }
            } else {
                if (readcheckcount > 2) {
                    readcheckcount = 0;
                    readbyteslen = 0;
                    mMsgState = MESSAGE_READFAILD;
                    mHandler.obtainMessage(LISTENING_MESSAGE_STATE, mMsgState, -1).sendToTarget();
                    Log.e("ConnectCheck", "readcheckcount>2");
                    return false;
                }
            }
        }
        return false;
    }

    public void ConnectWifiScan() {
        mwifiAdmin.startScan();
    }

    public List<ScanResult> ConnectWifiScanResult() {
        return mwifiAdmin.getWifiList();
    }

    public String getConnectWifiMAC() {
        return mwifiAdmin.getBSSID();

    }


    public String getConnectWifiSSID() {
        mWifiInfo = mwifiAdmin.getWifiInfo();
        return mWifiInfo.getSSID();
    }

    //连接指定的wifi网络
    public void ConnectWifi(String SSID, String password, int tap) {
        if (getConnectWifiSSID().equals('"' + SSID + '"')) {
        } else if (getConnectWifiSSID().equals(SSID)) {
        } else {
            mwifiAdmin.addNetwork(mwifiAdmin.CreateWifiInfo(SSID, password, tap));
        }
    }

    public void SendNetMe() {
        byte[] buf = new byte[100];
        byte[] message = new byte[100];
        int k = 0;
        message[k++] = 0;

        @SuppressWarnings("deprecation")
        String ip = Formatter.formatIpAddress(mWifiInfo.getIpAddress());
        buf = ip.getBytes();
        for (int i = 0; i < ip.length(); i++) {
            message[k++] = buf[i];
        }
        message[k++] = ',';
        String MAC = mWifiInfo.getMacAddress();
        buf = MAC.getBytes();
        for (int i = 0; i < MAC.length(); i++) {
            message[k++] = buf[i];
        }
        message[k++] = ',';
        String SSID = android.os.Build.MODEL;
        buf = SSID.getBytes();
        for (int i = 0; i < SSID.length(); i++) {
            message[k++] = buf[i];
        }
        SendBroadcastCmd(0xA0, message, k); // 0xA0为手机广播上线应答指令
        Log.e(TAG, "SendBroadcastCmd");
    }

    public Handler gethandle() {
        return mHandler;
    }

    public void getNetFriend() {
        byte[] buf = new byte[20];
        byte[] message = new byte[20];
        int k = 0;
        @SuppressWarnings("deprecation")
        String ip = Formatter.formatIpAddress(mWifiInfo.getIpAddress());
        buf = ip.getBytes();
        for (int i = 0; i < ip.length(); i++) {
            message[k++] = buf[i];
        }
        SendBroadcastCmd(0x20, message, k);
    }

    public void getNetDevices() {
        byte[] buf = new byte[1];
        buf[0] = (byte) 0x1A;
        SendBroadcastCmd(0X1A, buf, 0);
    }

    public void getLink(String ip, String ssid) {
        byte[] buf = ssid.getBytes();
        byte[] buff = new byte[buf.length + 1];
        buff[0] = (byte) 0xAA;
        for (int i = 0, j = 1; i < buf.length; i++) {
            buff[j++] = buf[i];
        }
        SendCmdCode(0X01, buff, buff.length, ip);
    }

    public void getLinkIPBroadcast(String ssid) { //发送指定SSID返回IP指令
        byte[] buf = ssid.getBytes();
        Log.i(TAG, "getLinkIPBroadcast" + ssid);
        SendBroadcastCmd(0x1C, buf, buf.length);
    }

    @SuppressWarnings("deprecation")
    public String getNetIPAddress() {
        return Formatter.formatIpAddress(mWifiInfo.getIpAddress());
    }

    public void SendBroadcastCmd(int code, byte[] buff, int len) {
        if (mCmdState != CMDSTATE_SENGSUCCESS) {
            Log.i(TAG, "mCmdState != CMDSTATE_SENGSUCCESS");
            return;
        }
        if (mlistenState != LISTENING_CONNECTED) {
            Log.i(TAG, "mlistenState != LISTENING_CONNECTED");
            return;
        }
        int i = 0;
        byte[] message = new byte[1024];
        message[i++] = (byte) 0xf7;
        message[i++] = (byte) 0x7f;//帧头

        message[i++] = (byte) 0x01;//帧版本号
        message[i++] = (byte) 0x01;//帧数
        message[i++] = (byte) 0x01;//帧号

        message[i++] = (byte) 0x00;//帧长度 高位
        message[i++] = (byte) 0x00;//帧长度 低位

        message[i++] = (byte) 0x01;//备用，蜂鸣器开关 0开，1关
        message[i++] = (byte) 0x01;//设备ID

        message[i++] = (byte) (code / 256);//帧命令 高位
        message[i++] = (byte) code;//帧命令 低位
        sendcmd = code;
        for (int j = 0; j < len; j++) {
            message[i++] = buff[j];
        }

        message[5] = (byte) ((i + 1) / 256);
        message[6] = (byte) (i + 1);
        code = 0;
        //计算校检
        for (int j = 0; j < i; j++) {
            code = code + message[j];
        }
        message[i++] = (byte) (code + 1);//校检码
        InetAddress broadcastAddr;
        try {
            broadcastAddr = InetAddress.getByName("255.255.255.255");    //广播地址
            sendBuffer = message;
            sendLen = i;
            sendIp = broadcastAddr;
            sendPort = LISTENPORT;
            mCmdState = CMDSTATE_NONE;
            Log.e(TAG, "noticeOnline()....广播地址255.255.255.255");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "noticeOnline()....广播地址有误");
        }
    }

    public void SendCmdCode(int code, byte[] buff, int len, String ip) {
        Log.e(TAG, "SendCmdCode > cmd:" + String.valueOf(code));
        if (mCmdState != CMDSTATE_SENGSUCCESS) return;
        if (mlistenState != LISTENING_CONNECTED) return;
        if (ip.equals("") || ip == null) {
            Log.e(TAG, "SendCmdCode > ip:" + ip + "空！");
            return;
        } else if (ip.equals("0.0.0.0") || mWifiInfo.getSSID().contains("IYK_")) {
            ip = "192.168.4.1";
            Log.e(TAG, "SendCmdCode > ip:" + ip);
        }
        int i = 0;
        byte[] message = new byte[2048];
        message[i++] = (byte) 0xf7;//int16进制的值，强转为byte

        message[i++] = (byte) 0x7f;//帧头
        message[i++] = (byte) 0x01;//帧版本号
        message[i++] = (byte) 0x01;//帧数
        message[i++] = (byte) 0x01;//帧号

        message[i++] = (byte) 0x00;//帧长度 高位
        message[i++] = (byte) 0x00;//帧长度 低位

        //Cursor mcuror =mDataBase.query("yaokongqitable", new String[]{"yaokongmode,yaokongname,yaokongqikind,deviceaddress"},
        //		"yaokongmode like ? AND yaokongname like ? AND yaokongqikind like ?", new String[]{"setting","sound","kai"}, null, null, null);
        //if(mcuror.getCount()==0){
        //	message[i++] = (byte) 0x01;//备用
        //}else{
        //	message[i++] = (byte) 0x00;//备用
        //}
        message[i++] = (byte) 0x01;//蜂鸣器状态，0为开，1为关

        message[i++] = (byte) 0x01;//设备ID

        message[i++] = (byte) (code / 256);//帧命令 高位
        message[i++] = (byte) code;//帧命令 低位
        sendcmd = code;
        for (int j = 0; j < len; j++) {
            message[i++] = buff[j];
        }
        message[5] = (byte) ((i + 1) / 256);
        message[6] = (byte) (i + 1);
        code = 0;
        //计算校检码
        for (int j = 0; j < i; j++) {
            code = code + message[j];
        }
        message[i++] = (byte) (code + 1);//校检码
        InetAddress broadcastAddr;
        try {
            broadcastAddr = InetAddress.getByName(ip);    //广播地址
            sendBuffer = message;

            sendLen = i;
            sendIp = broadcastAddr;
            sendPort = LISTENPORT;
            mCmdState = CMDSTATE_NONE;
            Log.e(TAG, "SendCmdCode > ip:" + ip);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "SendCmdCode > ip:" + ip + "失败！");
        }
    }

    private class ListeningSocketThread extends Thread {
        private int i = 0;

        public ListeningSocketThread() {
            i = 0;
        }

        public void run() {
            while (i < 2) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                android.net.NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                if (wifi == android.net.NetworkInfo.State.CONNECTED) {
                    onWork = true;  //设置标识为线程工作
                    if (UDPPortListenThread == null) {
                        UDPPortListenThread = new ConnetReceivePacketThread();
                        UDPPortListenThread.start();
                    } else {
                        UDPPortListenThread.cancel();
                        UDPPortListenThread = null;
                        UDPPortListenThread = new ConnetReceivePacketThread();
                        UDPPortListenThread.start();
                    }
                    if (mConnetSendPacketThread == null) {
                        UDPSendPacketFlag = true;
                        mCmdState = CMDSTATE_SENGSUCCESS;//第一次可以发送
                        mConnetSendPacketThread = new ConnetSendPacketThread();
                        mConnetSendPacketThread.start();
                    } else {
                        mConnetSendPacketThread.cancel();
                        mConnetSendPacketThread = null;
                        mCmdState = CMDSTATE_SENGSUCCESS;//第一次可以发送
                        UDPSendPacketFlag = true;
                        mConnetSendPacketThread = new ConnetSendPacketThread();
                        mConnetSendPacketThread.start();
                    }
                    mlistenState = LISTENING_CONNECTED;//发送数据
                    mHandler.obtainMessage(LISTENING_STATE_CHANGE, mlistenState, -1).sendToTarget();
                    break;
                } else if (i >= 2) {
                    mlistenState = LISTENING_CONNECTFAILD;//发送数据
                    mHandler.obtainMessage(LISTENING_STATE_CHANGE, mlistenState, -1).sendToTarget();
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                i++;
            }
        }

        public void cancel() {
            i = 2;
        }
    }

    private class ConnetReceivePacketThread extends Thread {

        public ConnetReceivePacketThread() {
            try {
                if (UDPSocket == null) {
                    UDPSocket = new DatagramSocket(LISTENPORT);
                    Log.i(TAG, "ConnetReceivePacketThread....绑定UDP端口成功");
                }
                if (UDPResPacket == null) {
                    UDPResPacket = new DatagramPacket(resBuffer, BUFFERLENGTH);
                    Log.i(TAG, "UDPResPacket = new DatagramPacket(resBuffer, BUFFERLENGTH)");
                }
            } catch (SocketException e) {
                // TODO Auto-generated catch block
                Log.i(TAG, "connectSocket()....绑定UDP端口失败");
                e.printStackTrace();
            }    //绑定端口
        }

        public void run() {
            while (onWork) {
                try {
                    StringBuffer stringBuffer = new StringBuffer();
                    lock.acquire();//打开广播功能，此处是一把多点广播锁
                    UDPSocket.receive(UDPResPacket);
String in;
                    for (byte b : resBuffer) {
                        in=Integer.toHexString(b&0xff);


                        if (in.length()==1) {
                            stringBuffer.append("0");
                        }
                        stringBuffer.append(in);
                        stringBuffer.append("\b");
                    }

                    Log.d("UDPPacket_rev", stringBuffer.toString() + "");
                    lock.release();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    onWork = false;
                    //lock.release();
                    if (UDPResPacket != null) {
                        UDPResPacket = null;
                        Log.e(TAG, "run() UDPResPacket = null");
                    }
                    if (UDPSocket != null) {
                        UDPSocket.close();
                        UDPSocket = null;
                        Log.e(TAG, "run() UDPSocket = null;");
                    }
                    Log.e(TAG, "UDP数据包接收失败！线程停止");
                    mlistenState = LISTENING_CONNECTFAILD;//发送数据
                    mHandler.obtainMessage(LISTENING_STATE_CHANGE, mlistenState, -1).sendToTarget();
                    break;
                }
                if (UDPResPacket.getLength() > 0) {
                    Log.i(TAG, "UDPResPacket.getLength() > 0");
                    if (ReadCheck(UDPResPacket.getData(), UDPResPacket.getLength())) {
                        //mCmdCheck = new CmdCheck(UDPResPacket.getData(), UDPResPacket.getLength());
                        mMsgState = MESSAGE_READSUCCESS;
                        mHandler.obtainMessage(LISTENING_MESSAGE_STATE, mMsgState, readbyteslen, readBuf).sendToTarget();
                    }
                }
            }
        }

        public void cancel() {
            onWork = false;
            if (UDPResPacket != null) {
                UDPResPacket = null;
                Log.e(TAG, "cancel() UDPResPacket = null;");
            }
            if (UDPSocket != null) {
                UDPSocket.close();
                UDPSocket = null;
                Log.e(TAG, "cancel() UDPSocket = null;");
            }
        }
    }

    private class ConnetSendPacketThread extends Thread {

        public void run() {
            while (UDPSendPacketFlag) {
                if (mCmdState == CMDSTATE_NONE) {
                    try {
                        mCmdState = CMDSTATE_SENGING;
                        UDPSendPacketcheckrescount = 0;
                        UDPSendPacket = new DatagramPacket(sendBuffer, sendLen, sendIp, sendPort);

                        if (UDPSocket != null) {
                            StringBuffer stringBuffer = new StringBuffer();
                            UDPSocket.send(UDPSendPacket);    //发送udp数据包
String in;
                            for (byte b : sendBuffer) {


                                in=Integer.toHexString(b&0xff);


                                if (in.length()==1) {
                                    stringBuffer.append("0");
                                }
                                stringBuffer.append(in);
                                stringBuffer.append("\b");

                            }

                            Log.d("UDPPacket_send", stringBuffer.toString());
                            Log.d("UDPPacket_send", "发送长度："+sendLen);
                        }
                        Log.i(TAG, "sendUdpData成功");
                        UDPSendPacket = null;
                        mMsgState = MESSAGE_WRITESUCCESS;//发送数据
                        mHandler.obtainMessage(LISTENING_MESSAGE_STATE, mMsgState, -1).sendToTarget();
                        Log.i(TAG, "sendUdpData成功");
                    } catch (IOException e) {    //发送UDP数据包出错
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        mCmdState = CMDSTATE_SENGFAILD;
                        mMsgState = MESSAGE_WRITEFAILD;//发送数据
                        mHandler.obtainMessage(LISTENING_MESSAGE_STATE, mMsgState, -1).sendToTarget();
                        UDPSendPacket = null;
                        Log.e(TAG, "sendUdpData(String sendStr, int port)....发送UDP数据包失败");
                    }
                } else if (mCmdState == CMDSTATE_SENGING) {
                    if (mCmdCheck != null) {
                        if (mCmdCheck.getCmd() == sendcmd + (byte) 0x80) {
                            UDPSendPacketcheckrescount = 0;
                            mCmdState = CMDSTATE_SENGSUCCESS;
                            Log.e(TAG, "mCmdState = CMDSTATE_SENGSUCCESS");
                        }
                    }
                 /*if(sendcmd==0x09||sendcmd==0x32||sendcmd==0x0D){
				   if(UDPSendPacketcheckrescount<20){
					  UDPSendPacketcheckrescount++;
				   }else{
					  UDPSendPacketcheckrescount = 0;
					  mCmdState = CMDSTATE_SENGSUCCESS;//600m秒恢复可发送状态
				   }
				 }else if((sendcmd<0x30&&sendcmd>0x20)||(sendcmd<0xB0&&sendcmd>0xA0)){
					 mCmdState = CMDSTATE_SENGSUCCESS;//直接跳过此段命令
				 }*/
                    if ((sendcmd < 0x30 && sendcmd > 0x20) || (sendcmd < 0xB0 && sendcmd > 0xA0)) {
                        mCmdState = CMDSTATE_SENGSUCCESS;//直接跳过此段命令
                    } else if (UDPSendPacketcheckrescount < 5) {
                        UDPSendPacketcheckrescount++;
                    } else {
                        UDPSendPacketcheckrescount = 0;
                        mCmdState = CMDSTATE_SENGSUCCESS;//1秒恢复可发送状态
                    }

                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            UDPSendPacketFlag = false;
            if (UDPSendPacket != null) {
                UDPSendPacket = null;
            }
            if (UDPSocket != null) {
                UDPSocket.close();
                UDPSocket = null;
            }
        }

    }

    public void StartListen() {
        Log.i(TAG, "StartListen()");
        if (mlistenState == LISTENING_CONNECTING || mlistenState == LISTENING_CONNECTED) return;
        mlistenState = LISTENING_CONNECTING;//发送数据
        if (mListeningSocketThread == null) {
            mListeningSocketThread = new ListeningSocketThread();
            mListeningSocketThread.start();
        } else {
            mListeningSocketThread.cancel();
            mListeningSocketThread = null;
            mListeningSocketThread = new ListeningSocketThread();
            mListeningSocketThread.start();
        }
    }

    public void stop() {
        mlistenState = LISTENING_NONE;//无连接状态
        mHandler.obtainMessage(LISTENING_STATE_CHANGE, mlistenState, -1).sendToTarget();
        if (mListeningSocketThread != null) {
            mListeningSocketThread.cancel();
            mListeningSocketThread = null;
        }
        if (mConnetSendPacketThread != null) {
            mConnetSendPacketThread.cancel();
            mConnetSendPacketThread = null;
        }
        if (UDPPortListenThread != null) {
            UDPPortListenThread.cancel();
            UDPPortListenThread = null;
        }
    }

}



