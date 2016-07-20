package com.utsmta.mstar.inspect;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.net.RouteInfo;

import com.utsmta.common.FactoryInspect;
// import com.mstar.android.ethernet.EthernetManager;

public class MstarEthInspect extends FactoryInspect {
	private static final String TAG = MstarEthInspect.class.toString();
	private Context context = null;

	 ConnectivityManager connectivity = null;
	
	private EthernetManager ethernetManager = null;
	
	protected MstarEthInspect(){
		
	}
	
	public MstarEthInspect(Context context){
		this.context = context;
		 // this.ethernetManager = EthernetManager.getInstance();
		 
	     connectivity = (ConnectivityManager) this.context.getSystemService(context.CONNECTIVITY_SERVICE);
		 this.ethernetManager =  (EthernetManager) context.getSystemService(context.ETHERNET_SERVICE);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		checkEthernet();
        IntentFilter intentFilter = new IntentFilter();
       // intentFilter.addAction(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
      //  intentFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
         intentFilter.addAction(connectivity.CONNECTIVITY_ACTION);
         intentFilter.addAction(connectivity.CONNECTIVITY_ACTION_IMMEDIATE);
         intentFilter.addAction(connectivity.INET_CONDITION_ACTION);
	   
        context.registerReceiver(ethernetReceiver, intentFilter);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		context.unregisterReceiver(ethernetReceiver);
	}

	private BroadcastReceiver ethernetReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			checkEthernet();
		}
	};
	
	private void checkEthernet(){
		if(getGateway() != null && isNetworkConnected()){
			updateInspectResult(true, 0, null);
		}
	}
	
	private String getGateway(){
		 List<RouteInfo> mInfos;	
	 	try {         
			 mInfos = connectivity.getLinkProperties(connectivity.TYPE_ETHERNET).getRoutes();
        	} catch (Exception e) {
              // TODO Auto-generated catch block
              		e.printStackTrace();
              		return null;
          	
        	}		
			
        	for (RouteInfo routeInfo : mInfos) {
            	InetAddress address = routeInfo.getGateway();
            	if (address instanceof Inet4Address) {
                String gateway = address.getHostAddress();
                	if ("0.0.0.0".equals(gateway)) {
                    	continue;
                }
                return gateway;
            }
           
        }
        return "";
	}
	
	private boolean isNetworkConnected(){
     //   ConnectivityManager connectivity = (ConnectivityManager) 
        //	this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
	    NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
	    if (networkInfos != null) {
	        for (int i = 0; i < networkInfos.length; i++) {
	            if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
	                return true;
	            }
	        }
	    }

		return false;		
	}
}


