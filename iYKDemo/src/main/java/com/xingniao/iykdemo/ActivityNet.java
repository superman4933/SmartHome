package com.xingniao.iykdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityNet extends Activity {
	private ArrayAdapter<String> arrayadapter;
	private List<ScanResult> mWifiList;
	private BroadcastReceiver receiver;
	private CmdCheck mCmdCheck;
	private ProgressDialog mProgressDialog = null;
	private String sname = "";
	private String DeviceName = "";
	private String DevicePassword = "";
	private String NetName = "";
	private String NetPassword = "79559249";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_netlist);	
		listinit();
		ActivityMain.setHandler(mHandler);
	}
	
	private void listinit(){	    		
        arrayadapter = new ArrayAdapter<String>(ActivityNet.this, R.layout.textview_adapter);
        arrayadapter.add("设备名称："+DeviceName);
        arrayadapter.add("设备密码："+DevicePassword);
        arrayadapter.add("连接设备");
        arrayadapter.add("网络名称："+NetName);
        arrayadapter.add("网络密码："+NetPassword);
        arrayadapter.add("设置设备连接网络");
        
        ListView listview = (ListView)findViewById(R.id.list_net);
        listview.setAdapter(arrayadapter);
        listview.setDivider(null);
	    listview.setDividerHeight(10);
        listview.setOnItemClickListener(new OnItemClickListener() {       	
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub	
						switch(position){
						case 0:
							DeviceList();
							break;
						case 1:
							AlertDialogGetin("请输入设备密码！",0);
							break;
						case 2:
							if(DeviceName.equals("")||DevicePassword.equals("")){
								return;
							}else{
								if(ActivityMain.mConnetListening.getConnectWifiSSID().contains(DeviceName)){
									return;
								}else{
									ActivityMain.mConnetListening.ConnectWifi(DeviceName, DevicePassword, 3);
									if(mProgressDialog == null){
								    	   mProgressDialog = ProgressDialog.show(ActivityNet.this, "提示！","正在连接设备："+DeviceName +"...", true,true);	
								    	   mProgressDialog.setCanceledOnTouchOutside(false);						    	   
								    }
									mProgressDialog.setMessage("正在连接设备："+DeviceName +"...");
									mProgressDialog.show();
								}
								
							}
							break;
						case 3:
							NetList();
							break;
						case 4:
							AlertDialogGetin("请输入wifi密码！",1);
							break;
						case 5:
							if(NetName.equals("")||NetPassword.equals("")){
								return;
							}else{
								if(ActivityMain.mConnetListening.getConnectWifiSSID().contains(DeviceName)){
									if(mProgressDialog == null){
								    	   mProgressDialog = ProgressDialog.show(ActivityNet.this, "提示！","正在连接网络："+NetName +"...", true,true);	
								    	   mProgressDialog.setCanceledOnTouchOutside(false);						    	   
								    }
									mProgressDialog.setMessage("正在连接网络："+NetName +"...");
									mProgressDialog.show();
									LinkNet();
									Log.e("ActivityNet","LinkNet-->"+NetName);
									//return;
								}								
							}
							break;
						}
			}
		}); 		
	}
	
	public void reg(){
    	receiver = new MyReceiver();
    	IntentFilter filter = new IntentFilter();       
    	filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver,filter);
    }
	
	private void DeviceList(){		
		mWifiList = ActivityMain.mConnetListening.ConnectWifiScanResult();
		if(mWifiList == null)return;		
		Map<String, Object> list = new HashMap<String, Object>();
        for(int i = 0,j=0;i<mWifiList.size();i++){  
        	if(mWifiList.get(i).SSID.toString().contains("IYK")){       	  
        		list.put(String.valueOf(j++), mWifiList.get(i).SSID.toString());
        	}
        }
        if(list.size() == 0)return;
        final CharSequence[] items = new CharSequence[list.size()];
        for(int i=0;i<list.size();i++){
        	items[i] = list.get(String.valueOf(i)).toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNet.this);
		builder.setTitle("提示！");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				DeviceName = (String) items[which];
				arrayadapter.clear();
				arrayadapter.add("设备名称："+DeviceName);
		        arrayadapter.add("设备密码："+DevicePassword);
		        arrayadapter.add("连接设备");
		        arrayadapter.add("网络名称："+NetName);
		        arrayadapter.add("网络密码："+NetPassword);
		        arrayadapter.add("连接网络");
		        arrayadapter.notifyDataSetChanged();
			}
		});
		AlertDialog mAlerDialog = builder.create();
		mAlerDialog.show();
	}
	
	private void NetList(){
		mWifiList = ActivityMain.mConnetListening.ConnectWifiScanResult();
		if(mWifiList == null)return;		
		Map<String, Object> list = new HashMap<String, Object>();
        for(int i = 0,j=0;i<mWifiList.size();i++){  
        	if(mWifiList.get(i).SSID.toString().contains("IYK")){       	          		
        	}else{
        		list.put(String.valueOf(j++), mWifiList.get(i).SSID.toString());
        	}
        }
        if(list.size() == 0)return;
        final CharSequence[] items = new CharSequence[list.size()];
        for(int i=0;i<list.size();i++){
        	items[i] = list.get(String.valueOf(i)).toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNet.this);
		builder.setTitle("提示！");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				NetName = (String) items[which];
				Log.e("ActivityNet","NetName:"+NetName);
				arrayadapter.clear();
				arrayadapter.add("设备名称："+DeviceName);
		        arrayadapter.add("设备密码："+DevicePassword);
		        arrayadapter.add("连接设备");
		        arrayadapter.add("网络名称："+NetName);
		        arrayadapter.add("网络密码："+NetPassword);
		        arrayadapter.add("连接网络");
		        arrayadapter.notifyDataSetChanged();
			}
		});
		AlertDialog mAlerDialog = builder.create();
		mAlerDialog.show();
	}
	
	@SuppressLint("InlinedApi")
	private void AlertDialogGetin(String title,final int flag){

		AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNet.this);
        builder.setTitle(title);
        final EditText mingcheng = new EditText(ActivityNet.this);
        mingcheng.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(mingcheng);
        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try { 
					 Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
					 field.setAccessible(true); 
					 field.set(dialog, false); 
					} catch (Exception e) { 
					 e.printStackTrace(); 
					}  						
				sname = mingcheng.getText().toString();
				if(sname.equals("")){
				 Toast toast = Toast.makeText(ActivityNet.this,"请输入内容！",Toast.LENGTH_LONG);
				 toast.show();						
				 return;
				}else{														
		            try {
						Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
						} catch (Exception e) {
						e.printStackTrace();
					    }		      	     		      	     
				}
				if(flag == 0){
					DevicePassword = sname;
					Log.e("ActivityNet","DevicePassword:"+DevicePassword);
				}else{
					NetPassword = sname;
					Log.e("ActivityNet","NetPassword:"+NetPassword);
				}
				arrayadapter.clear();
				arrayadapter.add("设备名称："+DeviceName);
		        arrayadapter.add("设备密码："+DevicePassword);
		        arrayadapter.add("连接设备");
		        arrayadapter.add("网络名称："+NetName);
		        arrayadapter.add("网络密码："+NetPassword);
		        arrayadapter.add("连接网络");
		        arrayadapter.notifyDataSetChanged();
				return;
			}
		});
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try {
					Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
					} catch (Exception e) {
					e.printStackTrace();
					} 
			}
		});	            
        builder.show();
	}
	
	private void LinkNet(){
	    	byte[] buf = new byte[48];
		    byte[] buf1 = new byte[48];
		    int i = 0;
		    buf[i++]=(byte)0x22;
		    buf1 = NetName.getBytes();				    
		    for(int j =0;j<NetName.length();j++){
		    	buf[i++] = buf1[j];	
		    }
		    buf[i++]=(byte) 0x22;
		    buf[i++]=(byte) 0x2c;
		    buf[i++]=(byte) 0x22;
		    buf1 = NetPassword.getBytes();	
		    for(int j =0;j<NetPassword.length();j++){
		    	buf[i++] = buf1[j];	
		    }
		    buf[i++]=(byte) 0x22;		   
		    ActivityMain.mConnetListening.SendCmdCode(2, buf, buf.length,"255.255.255.255");
	    }
	
	public class MyReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            //Toast.makeText(context, intent.getAction(), 1).show(); 
      		
        		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
                if(wifiInfo.getState() !=  android.net.NetworkInfo.State.CONNECTED){
                	 Log.e("MainActivity","!!!wifiInfo.State.CONNECTED");
                	
                }else if(wifiInfo.getState() ==  android.net.NetworkInfo.State.CONNECTED){
                	Log.e("MainActivity","wifiInfo.State.CONNECTED");
                	if(ActivityMain.mConnetListening.getConnectWifiSSID().contains(DeviceName)){
                		if(mProgressDialog != null){
                			mProgressDialog.dismiss();
                		}
					}
                }
        		         
        }  
      
    }
	
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
            switch (msg.what){	         
            case ConnetListening.LISTENING_MESSAGE_STATE: 
            	switch(msg.arg1){
            	case ConnetListening.MESSAGE_READSUCCESS:
            		Log.e("Activity_Timeclock_Add","ConnetListening.MESSAGE_READSUCCESS");
            		mCmdCheck = new CmdCheck((byte[])msg.obj, msg.arg2);
            		if(mCmdCheck.getCmd() == (byte)0x82){
            			if(mCmdCheck.getCmdSetState() == 1){
                			mProgressDialog.cancel();
                    		mProgressDialog.dismiss();  
                    		ActivityMain.mConnetListening.ConnectWifi(NetName, NetPassword, 3);//切换到局域网； 
                    		ActivityNet.this.finish();
                		}else if(mCmdCheck.getCmdSetState() == 2){
                			mProgressDialog.cancel();
                    		mProgressDialog.dismiss(); 	
                    		Toast toast = Toast.makeText(ActivityNet.this,"WIFI连接失败！",Toast.LENGTH_SHORT);
        				    toast.show();
                		}
            		}
            		break;
            	case ConnetListening.MESSAGE_READFAILD:	            		
            		break;	
            	} 
                break;      
            }
        }
    };	
	
    @Override
    public void onResume() {
    	Log.e("MainActivity","onResume()"); 
    	super.onResume();    	
    	reg();
    }
    
    @Override   
    public void onDestroy() {
    	Log.e("MainActivity","onDestroy()");       
        unregisterReceiver(receiver);  
        super.onDestroy();       
       //this.finish();
    }
    
}
