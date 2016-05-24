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
	public byte[] getCmdBuf(){//���� ��������״̬�� ������У����
		byte[] buff = new byte[getCmdConnt()];
		for(int i=0,j=11;i<getCmdConnt();i++){
			buff[i] = buf[j++];
		}
		return buff;
	}
	
	public byte[] getCmdContentBuf(){//������֡��Ϣ��У���롢����״̬�룻
		byte[] buff = new byte[getCmdConnt()-1];
		for(int i=0,j=12;i<getCmdConnt()-1;i++){
			buff[i] = buf[j++];
		}
		return buff;
	}
	public byte[] getCmdContentNoStateBuf(){ ////������֡��Ϣ��У���롢����״̬�롢����״̬�������
		byte[] buff = null;
		switch(getCmd()){
		case (byte)0x81: //����ѧϰ��
		case (byte)0x88: //����ѧϰ��
		case (byte)0xB1: //�̺���ѧϰ��
		case (byte)0x8C: // 315Mѧϰ��	
		case (byte)0x94: //���߶�ʱ״̬
		case (byte)0x9A: //ip��ѯ
		case (byte)0x9C: //ָ��SSID ip��ѯ
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
	//����WIFIģ�����Ҫ���ص�״̬
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
			
		case (byte)0xA1://�ֻ�����ָ��
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
