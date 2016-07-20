
package com.eostek.tv.widget;

import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.eostek.tv.R;
import com.eostek.tv.TvApplication;
import com.eostek.tv.advertisement.AdInfo;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.UtilsTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public abstract class InfoControlView extends FrameLayout {
    private static final String TAG = "InfoControlView";

    protected ImageView mAdView;

    protected View mOSDLayout;

    protected boolean hasShow = false;

    protected Context mContext;

    protected Bitmap mB;

    protected static final int DISMISS_INFO = 1;

    protected static final int DISMISS_AD = 2;

    protected static final int DELAY_DIMISS_INFO_TIME = 4000;

    @SuppressLint("HandlerLeak")
    protected Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISMISS_INFO:
                    dismissInfoView();
                    break;
                case DISMISS_AD:
                    dismissAdView();
                    break;
                default:
                    break;
            }
        }

    };

    public InfoControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public InfoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoControlView(Context context) {
        super(context);
    }

    protected void handleImage(List<AdInfo> adinfos) {
        DisplayImageOptions options;
        if (UtilsTools.hasDiskCache()) {
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .build();
        } else {
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(false)
                    .build();
        }
        Log.i(TAG, "source infos size : " + adinfos.size());
        for (AdInfo adinfo : adinfos) {
            Log.i(TAG, "source url : " + adinfo.getPic_url());
            changeImagePos(adinfo);
            ImageSize targetSize = new ImageSize(mContext.getResources().getInteger(
                    R.integer.ad_target_image_width), mContext.getResources().getInteger(
                    R.integer.ad_target_image_height));
            final int t = adinfo.getDismiss_time();
            ImageLoader.getInstance().loadImage(adinfo.getPic_url(), targetSize, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            mAdView.setImageBitmap(loadedImage);
                            mRefreshHandler.removeMessages(DISMISS_AD);
                            mRefreshHandler.sendEmptyMessageDelayed(DISMISS_AD, t);
                            if (mB != null && loadedImage != mB) {
                                mB.recycle();
                            }
                            mB = loadedImage;
                        }
                    });
        }
    }

    private void changeImagePos(AdInfo adinfo) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.height = mContext.getResources().getInteger(R.integer.ad_target_image_height);
        lp.width = mContext.getResources().getInteger(R.integer.ad_target_image_width);
        switch (mContext.getResources().getConfiguration().densityDpi) {
            case Constants.DENSITY_1280:
                lp.setMargins(adinfo.getPos_x(), adinfo.getPos_y(), 0, 0);
                break;
            case Constants.DENSITY_1920:
                lp.setMargins((int) (adinfo.getPos_x() * 1.5), (int) (adinfo.getPos_y() * 1.5), 0,
                        0);
                break;
            default:
        }
        mAdView.setLayoutParams(lp);
    }

    public void dismissView() {
        dismissAdView();
        dismissInfoView();
    }

    public void dismissAdView() {
        mAdView.setImageBitmap(null);
        mAdView.setVisibility(View.INVISIBLE);
    }

    public void dismissInfoView() {
        hasShow = false;
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(mOSDLayout, "Y", mContext
                .getResources().getInteger(R.integer.ChannelInfo_details_animator_height));
        AnimatorSet as = new AnimatorSet();
        as.play(translationUp);
        as.start();
    }

    protected void show() {
        hasShow = true;
        mOSDLayout.setAlpha(1f);
        float x = mContext.getResources().getInteger(R.integer.ChannelInfo_details_animator_x);
        float y = mContext.getResources().getInteger(R.integer.ChannelInfo_details_animator_y);
        mOSDLayout.setX(mContext.getResources()
                .getInteger(R.integer.ChannelInfo_details_animator_x));
        mOSDLayout.setY(mContext.getResources().getInteger(
                R.integer.ChannelInfo_details_animator_height));

        ViewPropertyAnimator vpa = mOSDLayout.animate().x(x).y(y);

        vpa.setDuration(1500);
        vpa.setInterpolator(new BounceInterpolator());

        mAdView.setVisibility(View.VISIBLE);
    }

    public boolean isShow() {
        return hasShow;
    }
}
