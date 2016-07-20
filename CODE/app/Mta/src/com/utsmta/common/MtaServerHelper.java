package com.utsmta.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.utsmta.utils.MtaUtils;

public class MtaServerHelper {
	protected static String SERVER_CONFIG_FILE_NAME = "eostek-fmtac.ini";
	
	public static String findMtaServerAddress(){
    	ArrayList<String> usbDirs = MtaUtils.getMountedUsbDevices("/mnt/usb/");
    	for(String dir : usbDirs){
			File file = new File(dir+"/"+SERVER_CONFIG_FILE_NAME);
		    if(file.exists()){
		    	return getHttpServerAddrFromFile(file);
		    }	
    	}
    	
    	return null;
	}
	
	protected static String getHttpServerAddrFromFile(File file){
	    String serverAddr = null;
	    try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String content = null;
            try {
                while((content = reader.readLine()) != null){
                    if(content.contains("fmtac-inter-url")){
                        int index = content.indexOf("=");
                        if(index >= 0 && content.length() > index+1){
                            serverAddr = content.substring(index+1);
                        }
                        break;
                    }
                }
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    return serverAddr;
	}
}
