package com.iflytek.voicedemo;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
/**
 * Created by Administrator on 2016/5/24.
 * 利用UDP协议发送遥控指令线程，监听状态，随时准备发送数据
 */
public class ConnetSendPacketThread {
    Runnable runnable;
    Handler sendHandler;
    HandlerThread sendHandlerThread;
    InetAddress sendIp;
    int sendPort = 8888;
    byte[] sendBuffer = new byte[1024];
    int isRun;
    int RUN = 1;
    int NORUN = 2;
    int sendLen;
    DatagramSocket UDPSocket;
    DatagramPacket UDPSendPacket;
    static WifiManager.WifiLock wifiLock;

    public ConnetSendPacketThread(Context context) {
        Log.d("oncreate_thread", "创建ConnetSendPacketThread构造函数");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock("wifiLock");
        sendHandlerThread = new HandlerThread("sendRemote");
        sendHandlerThread.start();
        sendHandler = new Handler(sendHandlerThread.getLooper());
        try {
            sendIp = InetAddress.getByName("192.168.4.1");
            UDPSocket = new DatagramSocket(sendPort);
            Log.d("oncreate_thread", "创建ConnetSendPacketThread构造函数2");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendCMD(byte[] bCMD) {
        Log.d("oncreate_thread", "sendCMD开始运行");
        sendBuffer = null;
        sendBuffer = bCMD;
        isRun = RUN;
        sendLen = bCMD.length;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (UDPSocket == null) {
                    Log.d("oncreate_thread", "UDPSocket为null");
                }
                if (UDPSocket != null) {

                    Log.d("oncreate_thread", "准备发送数据");
                    try {
                        UDPSendPacket = new DatagramPacket(sendBuffer, sendLen, sendIp, sendPort);
                        UDPSocket.send(UDPSendPacket);    //发送udp数据包
                        Log.d("oncreate_thread", "发送完成" + UDPSendPacket.getAddress() + UDPSendPacket.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
/**打印发送数据，以日志形式在控制框输出*/
                    StringBuffer stringBuffer = new StringBuffer();
                    String in;
                    for (byte b : sendBuffer) {
                        in = Integer.toHexString(b & 0xff);
                        if (in.length() == 1) {
                            stringBuffer.append("0");
                        }
                        stringBuffer.append(in);
                        stringBuffer.append("\b");
                    }
                    Log.d("UDPPacket_send", stringBuffer.toString());
                    /*打印日志代码*/
                }
                try {
                    /**设备接收到数据后需要处理时间，短时持续发送指令设备会来不及
                     * 执行，所以发射完数据后，暂停一会*/
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        Log.d("oncreate_thread", "sendCMD准备发送runnable");
        sendHandler.post(runnable);
    }

    //把16进制的String命令，解析为byte数组，作为发送数据包的参数
    public byte[] parseCMD(String cmd) {
        String[] tmp = cmd.split("\\s");
        int parsecCmd;
        byte[] byteCmd = new byte[tmp.length];
        int i = 0;
        for (String cmdString : tmp) {
            parsecCmd = Integer.valueOf(cmdString, 16);
            Log.d("sendthreadint", parsecCmd + "");//解析成int成功
            byteCmd[i] = (byte) parsecCmd;
            Log.d("sendthreadbyte", Integer.toHexString(byteCmd[i] & 0xff) + "");
            i++;
        }
        Log.d("oncreate_thread", "命令解析完成");
        return byteCmd;
    }
}
