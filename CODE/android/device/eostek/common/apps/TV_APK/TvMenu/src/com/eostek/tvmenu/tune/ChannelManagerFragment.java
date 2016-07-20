
package com.eostek.tvmenu.tune;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eostek.tvmenu.R;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;


public class ChannelManagerFragment extends Fragment {

    private final static String TAG = ChannelManagerFragment.class.getName();
    
    private View mView;

    private EnumInputSource mCurSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    public static final String DIALOGID_AUTOTUNING = "DTV_AUTOTUNING";

    public static final String DIALOGID_MANUALTUNING = "DTV_MANUALTUNING";
    
    private String platform = "";
    
    public ChannelManagerHolder mHolder;
    public ChannerManagerLogic mLogic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	 //check current input source
    	 checkSource();
    	 
    	 if (mCurSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
    	      mView = inflater.inflate(R.layout.dtv_channel_manager_fragment, null);
            
            mHolder = new ChannelManagerHolder(this);
            mLogic = new ChannerManagerLogic(this);
            mHolder.initDtvView(mView);
            mHolder.initDtvData();
            mHolder.setDtvListener();
           
         }else if (mCurSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
        	 mView = inflater.inflate(R.layout.atv_channel_manager_fragment, null);
    		
    		 mHolder = new ChannelManagerHolder(this);
             mLogic = new ChannerManagerLogic(this);
             mHolder.initAtvView(mView);
             mHolder.initAtvData();
             mHolder.setAtvListener();
    	 }
	        
	     return mView;
    }
	
    /**
     * check current Source 
     */
	private void checkSource() {
	    mCurSource = TvCommonManager.getInstance().getCurrentInputSource();
	    if (mCurSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
	        mCurSource = EnumInputSource.values()[queryCurInputSrc()];
	        Log.v(TAG, "Source is storage,queryCurInputSrc ,curSource = " + mCurSource);
	    }
		
	}
    
	/**
	* query the current input source
	* 
	* @return InputSourceType
	*/
	public int queryCurInputSrc() {
	   int value = 0;
	   Cursor cursor = getActivity().getContentResolver().query(
	           Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
	   if (cursor != null && cursor.moveToFirst()) {
          value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
	   }
      cursor.close();
      return value;
    }
	
}