package com.eostek.tvmenu;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tvmenu.advance.AdvanceSettingFragment;
import com.eostek.tvmenu.network.NetWorkFragment;
import com.eostek.tvmenu.pcimage.PCImageAdjustFragment;
import com.eostek.tvmenu.picture.PictureSettingFragment;
import com.eostek.tvmenu.sound.AudioSettingFragment;
import com.eostek.tvmenu.tune.ChannelManagerFragment;
import com.mstar.android.tv.TvCommonManager;

public class TvMenuHolder {
    
    private final static String TAG = TvMenuHolder.class.getSimpleName();
    
    public GridView mTitleGv;

    public String[] mDataList;

    public final static int UPDATE_FRAGMENT = 1;
    
    public static final int FINISH = 2;
    
    private final static int UPDATE_FRAGEMENT_DELAY_TIME = 150;

    private PictureSettingFragment mPictureFragment = new PictureSettingFragment();

    private AudioSettingFragment mAudioFragment = new AudioSettingFragment();

    private ChannelManagerFragment mChannelFragment = new ChannelManagerFragment();

    private PCImageAdjustFragment mPcImageFragment = new PCImageAdjustFragment();

    private NetWorkFragment mNetWorkFragment = new NetWorkFragment();

    private AdvanceSettingFragment mAdvanceFragment = new AdvanceSettingFragment();

    public TitleAdapter mTitleAdapter = null;

    private int mCurSource;
    
    private View mTmpView;
    
    private TvMenuActivity mActivity;
    
    TvMenuHolder(Activity activity){
        mActivity = (TvMenuActivity)activity;
    }
    
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_FRAGMENT:
                    if (mActivity.isFinishing()) {
                        return;
                    }
                    updateFragment(msg.arg1);
                    break;
                case FINISH:
                    mActivity.finish();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    
    public void initView(){
        mTitleGv = (GridView) mActivity.findViewById(R.id.titles_gv);
        mCurSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        if (mCurSource == TvCommonManager.INPUT_SOURCE_STORAGE) {
            mCurSource = mActivity.mLogic.queryCurInputSrc();
            Log.v(TAG, "Source is storage,queryCurInputSrc ,curSource = " + mCurSource);
        }
        if (mCurSource == TvCommonManager.INPUT_SOURCE_ATV || mCurSource == TvCommonManager.INPUT_SOURCE_DTV) {
            mDataList = mActivity.getResources().getStringArray(R.array.menu_setting_title_tv);
        } else if (mCurSource == TvCommonManager.INPUT_SOURCE_VGA) {
            mDataList = mActivity.getResources().getStringArray(R.array.menu_setting_title_vga);
        } else {
            mDataList = mActivity.getResources().getStringArray(R.array.menu_setting_title);
        }
        mTitleGv.setNumColumns(mDataList.length);
        mTitleAdapter = new TitleAdapter();
        mTitleGv.setAdapter(mTitleAdapter);
        setListener();
        
        if (mActivity.getIntent().getBooleanExtra("gotoadvance", false)) {
            Toast.makeText(mActivity, R.string.nohotkeytip, Toast.LENGTH_LONG).show();
            FragmentManager fm = mActivity.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_setting, mAdvanceFragment);
            ft.commitAllowingStateLoss();
            mTitleGv.setSelection(mDataList.length - 1);
        } else {
            int locale = System.getInt(mActivity.getContentResolver(), "locale", 0);
            if (locale == 1) {
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_setting, mPictureFragment);
                ft.commitAllowingStateLoss();
                mTitleGv.setSelection(0);
            } else {
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_setting, mAdvanceFragment);
                ft.commitAllowingStateLoss();
                mTitleGv.setSelection(mDataList.length - 1);
            }
        }
        System.putInt(mActivity.getContentResolver(), "locale", 0);
    }
    
    private void setListener() {
        mTitleGv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                RelativeLayout rl = null;
                if (hasFocus) {
                    if (mTmpView != null) {
                        mTmpView.findViewById(R.id.title_item_rl1).setVisibility(View.VISIBLE);
                    } else {
                        rl = (RelativeLayout) mActivity.findViewById(R.id.title_item_rl1);
                        rl.setVisibility(View.VISIBLE);
                        mTmpView = rl;
                    }
                } else {
                    if (mTmpView != null) {
                        mTmpView.findViewById(R.id.title_item_rl1).setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mTitleGv.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mTmpView != null) {
                    mTmpView.findViewById(R.id.title_item_rl1).setVisibility(View.INVISIBLE);
                }
                view.findViewById(R.id.title_item_rl1).setVisibility(View.VISIBLE);
                mTmpView = view; 
                
                mHandler.removeMessages(UPDATE_FRAGMENT);
                Message msg = mHandler.obtainMessage();
                msg.arg1 = position;
                msg.what = UPDATE_FRAGMENT;
//                new java.lang.Exception("zhu").printStackTrace();
                mHandler.sendMessageDelayed(msg, UPDATE_FRAGEMENT_DELAY_TIME);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    
    public class TitleAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDataList.length;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            if (view == null) {
                view = inflater.inflate(R.layout.menu_title_item, null);
                //define grids' height
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 100);
                view.setLayoutParams(params);
            }
            TextView txt = (TextView) view.findViewById(R.id.menuitem_title_txt);
            txt.setText(mDataList[i]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
    }
    
    /**
     * update the fragment when press the key right or left.
     * 
     * @param position
     */
    public void updateFragment(int position) {
        FragmentManager fm = mActivity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        /**
         * animation happen when fragment changes, will be used if needed
         */
//        if (position > mLastPosition) {
//            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
//        } else {
//            ft.setCustomAnimations(R.anim.eos_menu_anim_leftin, R.anim.eos_menu_anim_rightout);
//        }
//        mLastPosition = position;
        if (mCurSource == TvCommonManager.INPUT_SOURCE_ATV || mCurSource == TvCommonManager.INPUT_SOURCE_DTV) {
            updateForTv(ft, position);
        } else if (mCurSource == TvCommonManager.INPUT_SOURCE_VGA) {
            updateForVGA(ft, position);
        } else {
            updateForOthers(ft, position);
        }
    }
    
    /**
     * to do the different action for tv. If the source is tv,we have the
     * channel manager.
     * 
     * @param ft
     * @param position
     */
    private void updateForTv(FragmentTransaction ft, int position) {
        switch (position) {
            case 0:
                ft.replace(R.id.content_setting, mPictureFragment);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.content_setting, mAudioFragment);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.content_setting, mChannelFragment);
                ft.commit();
                break;
            case 3: {
                ft.replace(R.id.content_setting, mNetWorkFragment);
                ft.commit();
                break;
            }
            case 4: {
                ft.replace(R.id.content_setting, mAdvanceFragment);
                ft.commit();
                break;
            }
            default:
                break;
        }

    }

    /**
     * to do the different action for vga. If the source is tv,we have the pc
     * image.
     * 
     * @param ft
     * @param position
     */
    private void updateForVGA(FragmentTransaction ft, int position) {
        switch (position) {
            case 0:
                ft.replace(R.id.content_setting, mPictureFragment);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.content_setting, mAudioFragment);
                ft.commit();
                break;
            case 2: {
                ft.replace(R.id.content_setting, mPcImageFragment);
                ft.commit();
                break;
            }
            case 3: {
                ft.replace(R.id.content_setting, mNetWorkFragment);
                ft.commit();
                break;
            }
            case 4: {
                ft.replace(R.id.content_setting, mAdvanceFragment);
                ft.commit();
                break;
            }
            default:
                break;
        }

    }

    /**
     * the title doesn't have the channel Manager if the source isn't tv.
     * 
     * @param ft
     * @param position
     */
    private void updateForOthers(FragmentTransaction ft, int position) {
        switch (position) {
            case 0:
                ft.replace(R.id.content_setting, mPictureFragment);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.content_setting, mAudioFragment);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.content_setting, mNetWorkFragment);
                ft.commit();
                break;
            case 3: {
                ft.replace(R.id.content_setting, mAdvanceFragment);
                ft.commit();
                break;
            }
            default:
                break;
        }
    }
    
}
