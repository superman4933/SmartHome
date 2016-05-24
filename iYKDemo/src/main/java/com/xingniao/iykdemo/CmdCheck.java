package com.xingniao.iykdemo;

public class CmdCheck {
	
    private byte[] buf;
    //private int len;
	public CmdCheck(byte[]buf,int len){
		this.buf=buf;
		//this.len = len;
	}
	public int getCmdVersion(){
		return buf[1];
	}
	public int getCmdNumber(){
		return buf[2];
	}
	public int getCmdNO(){
		return buf[3];
	}
	public int getCmdLen(){
		return (buf[5]&0xff)*256+buf[6]&0xff;
	}
	public int getCmd(){
		//Log.e("CmdCheck", "getCmd"+":"+String.valueOf((buf[9]*256+buf[10])));
		if((buf[9]*256+buf[10])<0x80)return buf[9]*256+buf[10];
		if(getCmdState() == 0){
		return buf[9]*256+buf[10];
		}else{
			return 0;
		}
	}
	public int getCmdConnt(){
		return (buf[5]&0xFF)*256+(buf[6]&0xff) - 12;
	}
	public byte[] getCmdBuf(){//内容 包含命令状态码 不包括校检码
		byte[] buff = new byte[getCmdConnt()];
		for(int i=0,j=11;i<getCmdConnt();i++){
			buff[i] = buf[j++];
		}
		return buff;
	}
	
	public byte[] getCmdContentBuf(){//不包括帧信息、校检码、命令状态码；
		byte[] buff = new byte[getCmdConnt()-1];
		for(int i=0,j=12;i<getCmdConnt()-1;i++){
			buff[i] = buf[j++];
		}
		return buff;
	}
	public byte[] getCmdContentNoStateBuf(){ ////不包括帧信息、校检码、命令状态码、内容状态码的内容
		byte[] buff = null;
		switch(getCmd()){
		case (byte)0x81: //红外学习码
		case (byte)0x88: //红外学习码
		case (byte)0xB1: //短红外学习码
		case (byte)0x8C: // 315M学习码	
		case (byte)0x94: //离线定时状态
		case (byte)0x9A: //ip查询
		case (byte)0x9C: //指定SSID ip查询
		case (byte)0xA0:
		case (byte)0xAF:
			buff = new byte[getCmdConnt()-2];
			for(int i = 0,j=13;i<getCmdConnt()-2;i++){
				buff[i] = buf[j++];
			}
			break;
		}				
		return buff;
	}
	public int getCmdState(){		
		return buf[11];
	}
	//设置WIFI模块后需要返回的状态
	public int getCmdSetState(){
		int r=0;
		if(getCmdState()!=0)return r;
		switch(getCmd()){
		case (byte)0x81:
		case (byte)0x82:
		case (byte)0x83:
		case (byte)0x84:
		case (byte)0x86:
		case (byte)0x87:	
		case (byte)0x88:
		case (byte)0x89:
		case (byte)0x8A:
		case (byte)0x8B:
		case (byte)0x8C:
		case (byte)0x8F:
		case (byte)0x90:
		case (byte)0x93:
		case (byte)0x94:
		case (byte)0x9A:
		case (byte)0x9B:
		case (byte)0x9C:
			
		case (byte)0xA1://手机分享指令
		case (byte)0xA2:
		case (byte)0xA3:
		case (byte)0xA4:
		case (byte)0xAF:
			
		case (byte)0xB1:
		case (byte)0xB3:
		case (byte)0xB4:
			r = buf[12];
			break;
		}
		return r;	
	}	
}
