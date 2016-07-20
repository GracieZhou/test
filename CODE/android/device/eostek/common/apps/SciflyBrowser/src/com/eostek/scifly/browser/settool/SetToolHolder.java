package com.eostek.scifly.browser.settool;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.R;

public class SetToolHolder {

    private final String TAG = "SetToolHolder";

    private BrowserActivity mActivity;

    private SetToolLogic mLogic;

    public LinearLayout mSettingLl;

    public LinearLayout mBrowserTitleLl;

    public RelativeLayout mQrcodeRl;

    public RelativeLayout mHistoryRl;

    public RelativeLayout mClearRl;

    public RelativeLayout mAdvancedSettingRl;

    public RelativeLayout mAdvancedSettingContentRl;

    public RelativeLayout mClearContentRl;

    public LinearLayout mListSaveLl;

    public LinearLayout mClearChoiceLl;

    public ImageView mQrcodeFocusImg;

    public ImageView mQrcodeImg;

    public ImageView mHistoryFocusImg;

    public ImageView mClearFocusImg;

    public ImageView mAdvanedFocusImg;

    public ImageView mListSaveSwitchImg;

    public ImageView mRightArrowImg;
    
    private TextView   mQrcodeTxt ;

    public ListView mClearListView;

    public LinearLayout mHistoryContentLayout;

    public ListView mTodayListView;

    public ListView mYesteedayListView;

    public ListView mEarlyListView;
    
    public LinearLayout mCurrentFocusItem;

    public LinearLayout mLastFocusItemOperate = null;

    private boolean mIsInitViewCompleted = false;

    public SetToolHolder(BrowserActivity activity) {
        mActivity = activity;
    }
    
    public void setLogic(SetToolLogic logic) {
        mLogic = logic;
    }

    public void initView(View view) {
        mSettingLl = (LinearLayout) view.findViewById(R.id.setting_tools_main_layout);
        mBrowserTitleLl = (LinearLayout) view.findViewById(R.id.gv_title_bar);
        mQrcodeRl = (RelativeLayout) view.findViewById(R.id.qrcode_rl);
        mQrcodeImg = (ImageView) view.findViewById(R.id.qrcode_img);
        mQrcodeTxt = (TextView)view.findViewById(R.id.sciflyku_qrcode_txt2);
        mHistoryRl = (RelativeLayout) view.findViewById(R.id.history_rl);
        mClearRl = (RelativeLayout) view.findViewById(R.id.clear_rl);
        mAdvancedSettingRl = (RelativeLayout) view.findViewById(R.id.advaced_seetting_rl);
        mAdvancedSettingContentRl = (RelativeLayout) view.findViewById(R.id.advanced_setting_content_rl);
        mListSaveLl = (LinearLayout) view.findViewById(R.id.list_save_ll);
        mClearChoiceLl = (LinearLayout) view.findViewById(R.id.clear_choice_ll);
        mClearContentRl = (RelativeLayout) view.findViewById(R.id.clear_choice_content_rl);

        mQrcodeFocusImg = (ImageView) view.findViewById(R.id.sciflyku_qrcode_focus_backimg);
        mHistoryFocusImg = (ImageView) view.findViewById(R.id.history_focus_backimg);
        mClearFocusImg = (ImageView) view.findViewById(R.id.clear_focus_backimg);
        mAdvanedFocusImg = (ImageView) view.findViewById(R.id.advaced_seetting_focus_backimg);
        mListSaveSwitchImg = (ImageView) view.findViewById(R.id.list_save_btn_img);
        mRightArrowImg = (ImageView) view.findViewById(R.id.clear_choice_img);

        mClearListView = (ListView) view.findViewById(R.id.clear_listview);

        mHistoryContentLayout = (LinearLayout) view.findViewById(R.id.history_content_fl);
        mTodayListView = (ListView) view.findViewById(R.id.today_list);
        mYesteedayListView = (ListView) view.findViewById(R.id.yesterday_list);
        mEarlyListView = (ListView) view.findViewById(R.id.early_list);
        mIsInitViewCompleted = true;
    }

    public void getCurrentFocusItem() {
        try {
            if (mTodayListView != null && mTodayListView.isFocused()) {
                mCurrentFocusItem = (LinearLayout) mTodayListView.getChildAt((Integer) mTodayListView.getSelectedItem());
            } else if (mYesteedayListView != null && mYesteedayListView.isFocused()) {
                mCurrentFocusItem = (LinearLayout) mYesteedayListView.getChildAt((Integer) mYesteedayListView
                        .getSelectedItem());
            } else if (mEarlyListView != null && mEarlyListView.isFocused()) {
                mCurrentFocusItem = (LinearLayout) mEarlyListView.getChildAt((Integer) mEarlyListView.getSelectedItem());
            } else {
                mCurrentFocusItem = null;
            }
        } catch (NullPointerException e) {
            mCurrentFocusItem = null;
        }
    }

    public void setQRCodeImg(Bitmap bitmap) {
        mQrcodeImg.setImageBitmap(bitmap);
        
    }
    
    public void setQRCodeTxt(){
        mQrcodeTxt.setText("HerTec2 App");
    }

    public boolean isInitViewCompleted() {
        return mIsInitViewCompleted;
    }
}
