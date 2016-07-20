package com.utsmta.common;

import com.utsmta.utils.LogUtil;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class MtaSoundPlayer {
	private final String TAG = "MstarSoundPlayer";
	
	private SoundPool soundPool = null;
	
	private int soundID  = 0;
	
	private int streamID = 0;
	
	private int resid = 0;
	
	private Context context = null;
	
	protected MtaSoundPlayer(){
		
	}
	
	public MtaSoundPlayer(Context context, int resid){
		this.context = context;
		this.resid = resid;
	}
	
	public void play(){
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		
		soundPool.setOnLoadCompleteListener(onLoadCompleteListener);
		
		soundID  = soundPool.load(this.context, this.resid, 1);
	}
	
	public void stop(){
		if(streamID > 0){
			soundPool.stop(streamID);
		}
		
		if(soundID > 0){
			soundPool.unload(soundID);		
		}
		
		soundPool.release();
	}
	
	protected OnLoadCompleteListener onLoadCompleteListener = new OnLoadCompleteListener() {
		
		@Override
		public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
			// TODO Auto-generated method stub
			if(soundID > 0){
				
				streamID = soundPool.play(soundID, 
						1.0f, 
						1.0f, 
						1, 0, 1);

				LogUtil.d(TAG, "streamID = "+streamID);
			}
		}
	};
}
