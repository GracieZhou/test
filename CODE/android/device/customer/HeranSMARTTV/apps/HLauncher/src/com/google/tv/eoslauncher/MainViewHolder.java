
package com.google.tv.eoslauncher;

import java.util.HashMap;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.google.tv.eoslauncher.ui.fragment.AppFragment;
import com.google.tv.eoslauncher.ui.fragment.HomeFragment;
import com.google.tv.eoslauncher.ui.fragment.MediaFragment;
import com.google.tv.eoslauncher.ui.fragment.MediaHFragment;
import com.google.tv.eoslauncher.ui.fragment.PandoraFragment;
import com.google.tv.eoslauncher.ui.fragment.ShopFragment;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.HistoryRec;
import com.google.tv.eoslauncher.util.UIUtil;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

public class MainViewHolder {

    private static final String TAG = "MainViewHolder";

    private HomeActivity mContext;

    private Handler mHandler;

    // Ad txt obg
    public TextSwitcher textAd_Txt;

    // time obj
    public TextView time_txt;

    // data obj
    public TextView date_txt;

    // weather
    public TextView mUserName;

    public ImageView mStatusIcon;

    // main content
    public LinearLayout home_content = null;

    // mFGManger
    public FragmentManager mFGManger = null;

    public HomeFragment mHomeFragment = null;

    public AppFragment mAppFragment = null;

    public MediaFragment mediaFragment = null;

    public ShopFragment mShopFragment = null;

    public MediaHFragment mMediaHFragment = null;

    public PandoraFragment mPandoraFragment = null;

    // the mark of last fragment position when entry into the PandoraFragment
    public static int pandoraLastPosition = 0;

    // the mark of last selected fragment
    private int mLastPosition = -1;

    public int getmLastPosition() {
        return mLastPosition;
    }

    // save focus view when loop moving
    private HashMap<String, View> mTmpViews;

    public MainViewHolder(HomeActivity content, Handler mHandler) {
        this.mContext = content;
        this.mHandler = mHandler;
        this.mFGManger = content.getFragmentManager();
        initView();
    }

    private void initView() {
        time_txt = (TextView) mContext.findViewById(R.id.time_txt);
        date_txt = (TextView) mContext.findViewById(R.id.date_txt);

        textAd_Txt = (TextSwitcher) mContext.findViewById(R.id.textad_txt);
        textAd_Txt.setFactory(new ViewFactory() {

            @Override
            public View makeView() {
                TextView mScrollTextView = new TextView(mContext);
                mScrollTextView.setTextColor(Color.WHITE);
                mScrollTextView.setTextSize(20);
                mScrollTextView.setSingleLine();
                mScrollTextView.setEllipsize(TruncateAt.MARQUEE);
                mScrollTextView.setSelected(true);
                return mScrollTextView;
            }
        });

        addAnimation();

        showOSDMessage(mContext.getResources().getString(R.string.ad_txt_default));

        ContentResolver cv = mContext.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat == null || strTimeFormat.equals("24")) {
            time_txt.setText(UIUtil.formatDate(System.currentTimeMillis(), "HH:mm"));
        } else {
            time_txt.setText(UIUtil.formatDate(System.currentTimeMillis(), "a hh:mm"));
        }
        date_txt.setText(UIUtil.formatDate(System.currentTimeMillis(), mContext.getString(R.string.data_format)));
        home_content = (LinearLayout) mContext.findViewById(R.id.home_content);

        mUserName = (TextView) mContext.findViewById(R.id.user_name);
        mStatusIcon = (ImageView) mContext.findViewById(R.id.status_icon);

        mHandler.sendEmptyMessage(Constants.OSDMESSAGE);
        mHandler.sendEmptyMessageDelayed(Constants.TOUPDATETIME, Constants.DELAYUPDATETIME);

        // init Fragment
        mHomeFragment = new HomeFragment(mHandler, mContext, this);
        mAppFragment = new AppFragment(mContext, this);
        mediaFragment = new MediaFragment(mContext, this);
        mMediaHFragment = new MediaHFragment(mContext, this);
        mPandoraFragment = new PandoraFragment(mContext, this);
        mShopFragment = new ShopFragment(mContext, this);

        mTmpViews = new HashMap<String, View>();

        // ----------- add by Jason
        // ----------------------------------------------------------------------------------------------
        if (!HistoryRec.isRecFileExist) {
            if (HistoryRec.createRecFile()) {
                Log.d(TAG, "createRecFile ok ");
                HistoryRec.isRecFileExist = true;
            } else {
                Log.d(TAG, "createRecFile fail");
                HistoryRec.isRecFileExist = false;
            }
        }
        // -----------------------------------------------------------------------------------------------------------------------

    }

    public void addAnimation() {
        textAd_Txt.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_in));
        textAd_Txt.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_out));
    }

    /**
     * show osd message
     * 
     * @param osdmsg
     */
    public void showOSDMessage(String osdmsg) {
        textAd_Txt.setText(osdmsg);
    }

    public void showDefaultMsg() {
        textAd_Txt.clearAnimation();
        showOSDMessage(mContext.getString(R.string.ad_playing));
    }

    /**
     * update the fragment when press the key right or left.You do the different
     * animation.
     * 
     * @param position
     */
    public void updateFragment(int position) {
        if (mLastPosition == position) {
            return;
        }
        mFGManger.popBackStack();
        FragmentTransaction ft = mFGManger.beginTransaction();
        if (position == Constants.HOMEVIEW && mLastPosition == Constants.SHOPWEBVIEW) {
            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
        } else if (position == Constants.SHOPWEBVIEW && mLastPosition == Constants.HOMEVIEW) {
            ft.setCustomAnimations(R.anim.eos_menu_anim_leftin, R.anim.eos_menu_anim_rightout);
        } else if ((position > mLastPosition)) {
            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
        } else {
            ft.setCustomAnimations(R.anim.eos_menu_anim_leftin, R.anim.eos_menu_anim_rightout);
        }

        switch (position) {
            case Constants.HOMEVIEW:
                ft.remove(mHomeFragment);
                ft.replace(R.id.home_content, mHomeFragment);
                ft.commitAllowingStateLoss();
                mStatusIcon.setBackground(mContext.getResources().getDrawable(R.drawable.home));
                break;
            case Constants.APPVIEW:
                ft.remove(mAppFragment);
                ft.replace(R.id.home_content, mAppFragment);
                ft.commitAllowingStateLoss();
                mStatusIcon.setBackground(mContext.getResources().getDrawable(R.drawable.app));
                break;
            case Constants.MEDIAVIEW:
                if (Constants.kok_device) {
                    ft.remove(mMediaHFragment);
                    ft.replace(R.id.home_content, mMediaHFragment);
                } else {
                    ft.remove(mediaFragment);
                    ft.replace(R.id.home_content, mediaFragment);
                }
                ft.commitAllowingStateLoss();
                mStatusIcon.setBackground(mContext.getResources().getDrawable(R.drawable.media));
                pandoraLastPosition = position;
                break;
            case Constants.PANDORAVIEW:
                ft.remove(mPandoraFragment);
                ft.replace(R.id.home_content, mPandoraFragment);
                ft.commitAllowingStateLoss();
                mStatusIcon.setBackground(mContext.getResources().getDrawable(R.drawable.heran));
                break;
            case Constants.SHOPWEBVIEW:
                ft.remove(mShopFragment);
                ft.replace(R.id.home_content, mShopFragment);
                ft.commitAllowingStateLoss();
                mStatusIcon.setBackground(mContext.getResources().getDrawable(R.drawable.heran));
                pandoraLastPosition = position;
                break;
            default:
                break;
        }
        mLastPosition = position;
    }

    /**
     * First,save HomeFragment、AppFragmen、MediaFragment Focus; Then set
     * HomeFragment、AppFragmen、MediaFragment Focus to null
     */
    public void cleanFragmentAllFocus() {
        if (mHomeFragment != null) {
            mTmpViews.put("HomeFragment", mHomeFragment.getmViewBean().getmCurFocusView());
            mHomeFragment.getmViewBean().setmCurFocusView(null);
        }
        if (mAppFragment != null) {
            mTmpViews.put("AppFragment", mAppFragment.getmViewBean().getmCurFocusView());
            mAppFragment.getmViewBean().setmCurFocusView(null);
        }
        if (mediaFragment != null) {
            mTmpViews.put("MediaFragment", mediaFragment.getmViewBean().getmCurFocusView());
            mediaFragment.getmViewBean().setmCurFocusView(null);
        }
        if (mMediaHFragment != null) {
            mTmpViews.put("MediaHFragment", mMediaHFragment.getmViewBean().getmCurFocusView());
            mMediaHFragment.getmViewBean().setmCurFocusView(null);
        }
        if (mPandoraFragment != null) {
            mTmpViews.put("PandoraFragment", mPandoraFragment.getmViewBean().getmCurFocusView());
            mPandoraFragment.getmViewBean().setmCurFocusView(null);
        }
        if (mShopFragment != null) {
            mTmpViews.put("ShopFragment", mShopFragment.getmViewBean().getmCurFocusView());
            mShopFragment.getmViewBean().setmCurFocusView(null);
        }
    }

    /**
     * restore HomeFragment、AppFragmen、MediaFragment Focus
     */
    public void restoreFragmentFocus() {
        // when mTmpViews is empty,set a view for mark ,to avoid first start the
        // app,then pressed left key ,the focus view at the wrong place
        if (mTmpViews.get("HomeFragment") == null) {
            mTmpViews.put("HomeFragment", home_content);
        }
        mHomeFragment.getmViewBean().setmCurFocusView(mTmpViews.get("HomeFragment"));

        if (mTmpViews.get("AppFragment") == null) {
            mTmpViews.put("AppFragment", home_content);
        }
        mAppFragment.getmViewBean().setmCurFocusView(mTmpViews.get("AppFragment"));

        if (mTmpViews.get("MediaFragment") == null) {
            mTmpViews.put("MediaFragment", home_content);
        }
        mediaFragment.getmViewBean().setmCurFocusView(mTmpViews.get("MediaFragment"));

        if (mTmpViews.get("MediaHFragment") == null) {
            mTmpViews.put("MediaHFragment", home_content);
        }
        mMediaHFragment.getmViewBean().setmCurFocusView(mTmpViews.get("MediaHFragment"));

        if (mTmpViews.get("PandoraFragment") == null) {
            mTmpViews.put("PandoraFragment", home_content);
        }
        mPandoraFragment.getmViewBean().setmCurFocusView(mTmpViews.get("PandoraFragment"));

        if (mTmpViews.get("ShopFragment") == null) {
            mTmpViews.put("ShopFragment", home_content);
        }
        mShopFragment.getmViewBean().setmCurFocusView(mTmpViews.get("ShopFragment"));
    }
}
