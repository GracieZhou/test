package com.utsmta.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MtaUtils {
	public static String createHexMD5(String value){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        md.update(value.getBytes());
        byte[] hash = md.digest();

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            int v = hash[i] & 0xFF;
            if (v < 16) {
                result.append("0");
            }
            result.append(Integer.toString(v, 16));
        }
        
        return result.toString();
    }
	
	public static String createHexMD5(byte[] bytes){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        md.update(bytes);
        byte[] hash = md.digest();

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            int v = hash[i] & 0xFF;
            if (v < 16) {
                result.append("0");
            }
            result.append(Integer.toString(v, 16));
        }

        return result.toString();
	}
	
	public static short[] convertByteArrayToShortArray(byte[] bytes){
        short[] data = new short[bytes.length];
        for (int i = 0; i < data.length; i++) {
        	data[i] = bytes[i];
        }
        return data;
	}
	
	public static byte[] convertShortArrayToByteArray(short[] data){
		byte[] bytes = new byte[data.length];
        for (int i = 0; i < bytes.length; i++) {
        	bytes[i] = (byte)(data[i] & 0xFF);
        }
        return bytes;
	}
	
	public static ArrayList<String> getMountedUsbDevices(String rootPath){
    	String regex = "sd[a-z][0-9]?";
    	Pattern pattern = Pattern.compile(regex);
		
    	ArrayList<String> paths = new ArrayList<String>();
    	
    	File usbMountDir =  new File(rootPath);
    	if(usbMountDir.exists()){
    		File[] dirs = usbMountDir.listFiles();
        	for(File dir:dirs){
        		if(dir.isDirectory()){
        			String dirName = dir.getName();
        			Matcher matcher = pattern.matcher(dirName);
        			if(matcher.matches()){
        				paths.add(dir.getAbsolutePath());
        			}
        		}
        	}
    	}
    	
    	return paths;
	}
	
	public static String getPropertyFromFile(String filePath, String propertyName){
		File file = new File(filePath);
		if(file.exists()){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String content = null;
				try {
					while((content = reader.readLine()) != null){
						if(content.contains(propertyName)){
							int index = content.indexOf("=");
							if(index >= 0 && content.length() > index+1){
								return content.substring(index+1);
							}
							
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					if(reader != null){
						try {
							reader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}					
		}
		
		return null;
	}
	
	public static String getPropertyFromFile(File file, String propertyName){
		if(file.exists()){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String content = null;
				try {
					while((content = reader.readLine()) != null){
						if(content.contains(propertyName)){
							int index = content.indexOf("=");
							if(index >= 0 && content.length() > index+1){
								return content.substring(index+1).trim();
							}
							
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					if(reader != null){
						try {
							reader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}					
		}
		
		return null;
	}
	
	public static boolean isTextEmpty(String text){
		if(null == text || text.isEmpty()){
			return true;
		}
		
		return false;
	}
	
	public static boolean isDigit(String str){
		if(isTextEmpty(str))
			return false;
		
		return str.matches("^[0-9]*$");
	}
}
