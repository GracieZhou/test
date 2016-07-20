
package com.eostek.tvmenu.pcimage;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eostek.tvmenu.R;


public class PCImageAdjustFragment extends Fragment {

    public PCImageAdjustHolder mHolder;
    public PCImageAdjustLogic mLogic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pc_image_adjust_fragment, null);
        
        
        mHolder = new PCImageAdjustHolder(this);
        mLogic = new PCImageAdjustLogic(this);
        mHolder.initView(view);
        mHolder.initData();
        mHolder.setListener();
        return view;
    }
    
	
//    @SuppressLint("myHandlerLeak")
//    private Handler myHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == AUTO_TUNE_START) {
//                mAutoTuneProgressDialog = ProgressDialog.show(getActivity(), null, getActivity()
//                        .getResources().getString(R.string.picadjust), true);
//                new Thread() {
//                    public void run() {
//                        boolean bAutoTuneSuccess = false;
//                        try {
//                            if (TvManager.getInstance() != null) {
//                                bAutoTuneSuccess = TvManager.getInstance().getPlayerManager()
//                                        .startPcModeAtuoTune();
//                            }
//                        } catch (TvCommonException e) {
//                            e.printStackTrace();
//                        }
//
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (bAutoTuneSuccess) {
//                            myHandler.sendEmptyMessage(AUTO_TUNE_SUCCESS);
//                        } else {
//                            myHandler.sendEmptyMessage(AUTO_TUNE_FAIL);
//                        }
//                    }
//                }.start();
//            } else if (msg.what == AUTO_TUNE_SUCCESS) {
//                initDate();
//                mAutoTuneProgressDialog.dismiss();
//                Toast.makeText(getActivity(), R.string.picadjustsuccess, Toast.LENGTH_SHORT).show();
//                mAutoTuning = false;
//            } else if (msg.what == AUTO_TUNE_FAIL) {
//                initDate();
//                mAutoTuneProgressDialog.dismiss();
//                Toast.makeText(getActivity(), R.string.picadjustfail, Toast.LENGTH_SHORT).show();
//                mAutoTuning = false;
//            } else if (msg.what == DELAYINITDATA) {
//                initDate();
//            }
//
//            super.handleMessage(msg);
//        }
//    };
//
//    @Override
//    protected void initItems() {
//        setTag("pcimage");
//        mTvPictureManager = TvPictureManager.getInstance();
////        mItems = new ArrayList<SettingItem>();
////        mTitlePcImage = getActivity().getResources().getStringArray(R.array.pc_adjust);
////        /* clock */
////        mItemPcModeClock = new SettingItem(this, mTitlePcImage[0], 0, 100, 0,
////                MenuConstants.ITEMTYPE_DIGITAL, true);
////        mItems.add(mItemPcModeClock);
////        /* phase */
////        mItemPcModePhase = new SettingItem(this, mTitlePcImage[1], 0, 100, 0,
////                MenuConstants.ITEMTYPE_DIGITAL, true);
////        mItems.add(mItemPcModePhase);
////        /* horizontal position */
////        mItemPcModeHPosition = new SettingItem(this, mTitlePcImage[2], 0, 100, 0,
////                MenuConstants.ITEMTYPE_DIGITAL, true);
////        mItems.add(mItemPcModeHPosition);
////        /* vertical position */
////        mItemPcModeVPosition = new SettingItem(this, mTitlePcImage[3], 0, 100, 0,
////                MenuConstants.ITEMTYPE_DIGITAL, true);
////        mItems.add(mItemPcModeVPosition);
////        mItemPcModeAutoTune = new SettingItem(this, mTitlePcImage[4],
////                MenuConstants.ITEMTYPE_BUTTON, true);
////        mItems.add(mItemPcModeAutoTune);
//
//        if (TvChannelManager.getInstance().isSignalStabled()) {
//            mAdapter.notifyDataSetChanged();
//            myHandler.sendEmptyMessageDelayed(DELAYINITDATA, DELAYINITDATATIME);
//        } else {
//            mAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    protected void callBack(int resultValue, int position) {
//        switch (position) {
//            case 0:
//                mTvPictureManager.setPCClock(resultValue);
//                break;
//            case 1:
//                mTvPictureManager.setPCPhase(resultValue);
//                break;
//            case 2:
//                mTvPictureManager.setPCHPos(resultValue);
//                break;
//            case 3:
//                mTvPictureManager.setPCVPos(resultValue);
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    protected void callBack(Boolean resultValue, int position) {
//
//    }
//
//    @Override
//    protected void callBack(int position) {
//        if (position == 4 && mAutoTuning == false) {
//            mAutoTuning = true;
//            myHandler.sendEmptyMessage(AUTO_TUNE_START);
//        }
//    }
//
//    @Override
//    protected void initDate() {
//        int[] values = mTvPictureManager.getPCImage();
//
//        if (values[0] < 0) {
//            mItemPcModeClock.setCurValue(0);
//        } else {
//            mItemPcModeClock.setCurValue(values[0]);
//        }
//        if (values[1] < 0) {
//            mItemPcModePhase.setCurValue(0);
//        } else {
//            mItemPcModePhase.setCurValue(values[1]);
//        }
//        if (values[2] < 0) {
//            mItemPcModeHPosition.setCurValue(0);
//        } else {
//            mItemPcModeHPosition.setCurValue(values[2]);
//        }
//        if (values[3] < 0) {
//            mItemPcModeVPosition.setCurValue(0);
//        } else {
//            mItemPcModeVPosition.setCurValue(values[3]);
//        }
//
//        mAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    protected boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
//        return false;
//    }
}
