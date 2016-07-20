package com.utsmta.mstar;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MstarNative {
	private static MstarNative gInstance = null;
	
	private Lock lock = new ReentrantLock();
	
	static{
		System.loadLibrary("mtaJNI");
	}
	
	public void uploadLibrary(){
		
	}
	
	private MstarNative(){
		
	}
	
	public static MstarNative getInstance(){
		if(null == gInstance){
			synchronized (MstarNative.class) {
				if(null == gInstance){
					gInstance = new MstarNative();
				}
			}
		}
		return gInstance;
	}
	
	public int scanTouchPadKey(){
		lock.lock();
		try{
			return scan_touch_pad_key();
		}finally{
			lock.unlock();
		}
	}
	
	private static native int scan_touch_pad_key();
}
