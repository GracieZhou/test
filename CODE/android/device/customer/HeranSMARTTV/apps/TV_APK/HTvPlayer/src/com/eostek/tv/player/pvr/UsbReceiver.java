package com.eostek.tv.player.pvr;

import java.io.File;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StatFs;
import android.util.Log;

public class UsbReceiver extends BroadcastReceiver{
    
    //map to save disk info ,key is the path and value is percent *100 
    // key的格式如下：/mnu/usb/sda1  ｈｅａｄ �? 尾部没有/
    
    private static int count=0;
    private static HashMap<String,Integer> diskDesc=new HashMap<String,Integer>();
    
    public static synchronized void addDiskDesc(String key, Integer value){
        diskDesc.put(key, value);
    }
    
    public static synchronized void removeDiskDesc(String key){
        diskDesc.remove(key);
    }
    
    public static String formatKey(String value){
        if(value==null){
            return null;
        }
        if(!value.startsWith("/")){
            value="/"+value;
        }
        if(value.endsWith("/")){
            value=value.substring(0,value.length()-1);
            return formatKey(value);
        }
        else{
            return value;  
        } 
    }
    
    /**
     * 去掉字符串首位的/ 防止key不匹�
     * @param source
     * @return
     */
    public static String splitHeadAndTail(String source){
        if(source==null){
            return null;
        }
        if(source.startsWith("/")){
            source=source.substring(1);
        }
        if(source.endsWith("/")){
            source=source.substring(0, source.length()-1);
        }
        return source;
    }
    
    /**
     * 初始化磁盘信�
     */
    private static synchronized void initDiskDesc(){
        String parent="/mnt/usb/";
        File pFile=new File(parent);
        File [] children=pFile.listFiles();
        if(children!=null){
            String str=null;
            for(File file: children){
                str=file.getName();
                if(str!=null){
                    //将磁盘放入其�
                    Log.e("qhc","find disk "+str);
                    addDiskDesc(parent+str,0);
                }
            }
        }
    }
    
    public UsbReceiver(){
        Log.d("qhc","UsbReceiver constructor!!!");
        //保证这个广播只会扫描一次磁�
        if(count==0){
            initDiskDesc();
            count++;
        }
    }
    
    /**
     * 
     * @return disk count
     */
    public static synchronized int getDiskCount(){
        if(diskDesc!=null){
            return diskDesc.size();
        }
        else{
            return 0;
        }  
    }
    
    
    
    /**
     * return disk capacity percent by path
     * @param key
     * @return
     */
    public static synchronized int getDiskCapacityPercent(String key){
        Integer tmp=diskDesc.get(key);
        if(tmp==null){
            return -1;
        }
        else{
            return tmp;
        }
    }
    
    public static synchronized boolean isDiskExisted(String path){
        path=formatKey(path);
        if(diskDesc.containsKey(path)){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                || action.equals(Intent.ACTION_MEDIA_EJECT)) {
            Uri uri = intent.getData();
            final String path = uri.getPath();
            
            Log.w("qhc","path:"+path);
            
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                Log.w("qhc","ACTION_MEDIA_MOUNTED:"+path);
                addDiskDesc(path,0);
                new Thread(){
                    public void run() {
                    	try{
                        StatFs sf = new StatFs(path);
                        int percent=(int) ((1 - (float) sf.getFreeBlocks() / sf.getBlockCount()) * 100);
                        } catch (IllegalArgumentException e) {  
                            Log.e("qhc", "Error : ", e);  
                        } 
                        
                    };
                }.start();
               
            }
            else if(action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                Log.w("qhc","ACTION_MEDIA_UNMOUNTED:"+path);
            }
            else if(action.equals(Intent.ACTION_MEDIA_EJECT)) {
               
                Log.w("qhc","ACTION_MEDIA_EJECT:"+path);
                removeDiskDesc(path);
                 
            }
            
        }
        
          
        }  

}
