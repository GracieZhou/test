package com.eostek.tvmenu.pcimage;

import com.eostek.tvmenu.R;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class PCImageAdjustLogic {
	
	private PCImageAdjustFragment mFragment;
	
	private ProgressDialog mAutoTuneProgressDialog;
	
	private TvPictureManager mTvPictureManager;
	
    private boolean mAutoTuning = false;
    
    private static final int AUTO_TUNE_START = 1;

    private static final int AUTO_TUNE_SUCCESS = 2;

    private static final int AUTO_TUNE_FAIL = 3;

    private static final int DELAYINITDATA = 4;

    private static final int DELAYINITDATATIME = 600;


	public PCImageAdjustLogic(PCImageAdjustFragment f) {
		mFragment = f;
	}

	protected void showAutoTuneDialog(){
		myHandler.sendEmptyMessage(AUTO_TUNE_START);
	}
	
	private Handler myHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
          if (msg.what == AUTO_TUNE_START) {
              mAutoTuneProgressDialog = ProgressDialog.show(mFragment.getActivity(), null, mFragment.getActivity()
                      .getResources().getString(R.string.picadjust), true);
              new Thread() {
                  public void run() {
                      boolean bAutoTuneSuccess = false;
                      try {
                          if (TvManager.getInstance() != null) {
                              bAutoTuneSuccess = TvManager.getInstance().getPlayerManager()
                                      .startPcModeAtuoTune();
                          }
                      } catch (TvCommonException e) {
                          e.printStackTrace();
                      }

                      try {
                          Thread.sleep(1000);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }

                      if (bAutoTuneSuccess) {
                          myHandler.sendEmptyMessage(AUTO_TUNE_SUCCESS);
                      } else {
                          myHandler.sendEmptyMessage(AUTO_TUNE_FAIL);
                      }
                  }
              }.start();
          } else if (msg.what == AUTO_TUNE_SUCCESS) {
        	  mFragment.mHolder.updateSeekbarUi();
              mAutoTuneProgressDialog.dismiss();
              Toast.makeText(mFragment.getActivity(), R.string.picadjustsuccess, Toast.LENGTH_SHORT).show();
              mAutoTuning = false;
          } else if (msg.what == AUTO_TUNE_FAIL) {
        	  mFragment.mHolder.updateSeekbarUi();
              mAutoTuneProgressDialog.dismiss();
              Toast.makeText(mFragment.getActivity(), R.string.picadjustfail, Toast.LENGTH_SHORT).show();
              mAutoTuning = false;
          } else if (msg.what == DELAYINITDATA) {
        	  mFragment.mHolder.updateSeekbarUi();
          }

          super.handleMessage(msg);
      }
  };
	
}
