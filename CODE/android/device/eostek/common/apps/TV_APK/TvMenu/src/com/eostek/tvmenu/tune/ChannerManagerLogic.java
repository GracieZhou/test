package com.eostek.tvmenu.tune;

import android.content.Intent;
import android.view.View;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.Constants;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.common.vo.EnumProgramCountType;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;

public class ChannerManagerLogic {

	private ChannelManagerFragment mFragment;

	public ChannerManagerLogic(ChannelManagerFragment f) {
		mFragment = f;
	}
	
	/**
	 * start DTVAutoTunningActivity
	 */
	void startDTVAutoTunning() {
		Intent intent = new Intent(mFragment.getActivity(), DtvAutoTuningActivity.class);
		mFragment.getActivity().startActivity(intent);
		mFragment.getActivity().finish();
	}
	
	/**
	 * start ATVAutoTunningActivity
	 */
    void startATVAutoTunning() {
    	Intent intent = new Intent(mFragment.getActivity(), AtvAutoTuningActivity.class);
    	mFragment.startActivity(intent);
    	mFragment.getActivity().finish();
    }
    
    /**
     * show FineTuning Dialog
     */
    void showFineTuningDialog(){
    	 AtvFineTuningDialog fineTuningDialog = new AtvFineTuningDialog(mFragment.getActivity());
         fineTuningDialog.show();
         mFragment.getActivity().findViewById(R.id.main).setVisibility(View.INVISIBLE);
    }
    
    /**
     * show DtvManulTuning Dialog
     */
    void showDtvManulTuningDialog(){
    	DtvManualTuningDialog dtvdialog = new DtvManualTuningDialog(mFragment.getActivity());
        dtvdialog.show();
        mFragment.getActivity().findViewById(R.id.main).setVisibility(View.INVISIBLE);
    }
    
    /**
     * show ChannelEdit Dialog
     */
    void showChannelEditDialog(){
    	 Intent intent = new Intent(Constants.CHANNEL_EDIT_ACTION);
    	 mFragment.startActivity(intent);
    	 mFragment.getActivity().finish();
    }
	
    /**
     * show Password CheckDialog
     */
    void showPasswordCheckDialog(String tuneFlag){
        new PasswordCheckDialog(mFragment.getActivity(), tuneFlag).show();
        mFragment.getActivity().findViewById(R.id.main).setVisibility(View.INVISIBLE);
    }
    
    /**
     * if has locked channel,return locked channel number
     * @return
     */
    public int getLockChannelCount() {
	    int count = 0;
	    int indexBase = 0;
	    int channelconunt = 0;
	    int dataCount = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV_DATA);
	    channelconunt = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV) - dataCount;
	    for (int i = indexBase; i < channelconunt; i++) {
	        ProgramInfo pi = null;
	        pi = getProgramInfoByIndex(i);
	        if (pi.isLock) {
	            count++;
	        }
	    }
	    return count;
    }
    
    public ProgramInfo getProgramInfoByIndex(int programIndex) {
    	ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
    	qc.queryIndex = programIndex;
    	ProgramInfo pi = TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_DATABASE_INDEX);
    	return pi;
    }
	
}
