package com.iflytek.voicedemo;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Administrator on 2016/5/24.
 * 利用UDP协议发送遥控指令线程，监听状态，随时准备发送数据
 */
public class ConnetSendPacketThread extends Thread {

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
    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
   wifiLock= wifiManager.createWifiLock("wifiLock");

    try {
        sendIp = InetAddress.getByName("192.168.4.1");
        UDPSocket = new DatagramSocket(sendPort);

    } catch (Exception e) {
        e.printStackTrace();
    }
}



    @Override
    public void run() {
//        wifiLock.acquire();
        while (true) {
            if (isRun == RUN) {
                if (UDPSocket != null) {

                    Log.d("oncreate_thread", "准备发送数据");
                    try {
                        UDPSendPacket = new DatagramPacket(sendBuffer, sendLen, sendIp,sendPort);
                        UDPSocket.send(UDPSendPacket);    //发送udp数据包
                        Log.d("oncreate_thread", "发送完成"+UDPSendPacket.getAddress()+UDPSendPacket.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


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

                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                isRun = NORUN;
                sendBuffer = null;
                UDPSendPacket=null;
            }
        }


    }

    public void sendCMD(byte[] bCMD) {

        sendBuffer = null;
        sendBuffer = bCMD;
        isRun = RUN;
        sendLen=bCMD.length;


    }

    //    要发送的遥控命令
    public byte[] remoteCMD(byte[] buff, int len) {
        int i = 0;
        byte[] message = new byte[2048];
        message[i++] = (byte) 0xf7;//int16进制的值，强转为byte
        message[i++] = (byte) 0x7f;//帧头
        message[i++] = (byte) 0x01;//帧版本号
        message[i++] = (byte) 0x01;//帧数
        message[i++] = (byte) 0x01;//帧号
        message[i++] = (byte) 0x02;//帧长度 高位
        message[i++] = (byte) 0x0c;//帧长度 低位
        message[i++] = (byte) 0x01;//蜂鸣器状态，0为开，1为关
        message[i++] = (byte) 0x01;//设备ID
        message[i++] = (byte) 0x00;//帧命令 高位
        message[i++] = (byte) 0x32;//帧命令 低位
        for (int j = 0; j < len; j++) {
            message[i++] = buff[j];
        }
        int code = 0;
        //计算校检码
        for (int j = 0; j < i; j++) {
            code = code + message[j];
        }
        message[i++] = (byte) (code + 1);//校检码
        return message;
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
