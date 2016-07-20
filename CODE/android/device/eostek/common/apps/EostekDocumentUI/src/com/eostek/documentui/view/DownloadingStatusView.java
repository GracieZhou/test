
package com.eostek.documentui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.documentui.Constants;
import com.eostek.documentui.R;
import com.eostek.documentui.util.Utils;

/**
 * @ClassName: DownloadingStatusView.
 * @Description:show the DownloadingFragment item status .
 * @author: lucky.li.
 * @date: Sep 18, 2015 9:38:24 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class DownloadingStatusView extends RelativeLayout {
    private final String TAG = "DownloadingStatusView";

    private Context mContext;

    private int status = Constants.WAITINGFLAG;

    /**
     * panse,continue,waiting view
     */
    private ImageView mStatusImageView;

    private ImageView mDeleteImageView;

    /**
     * download fail view
     */
    private TextView mFailTextView;

    /**
     * downloding view
     */
    private DownloadSpeedLayout mDownloadSpeedLayout;

    /**
     * the speed of the download
     */
    private int mSpeed;

    /**
     * the percent of the downloading
     */
    private float mPercent;

    /**
     * @Title: DownloadingStatusView.
     * @Description: constructor.
     * @param: @param context
     * @param: @param attrs
     * @param: @param defStyle.
     * @throws
     */
    public DownloadingStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }

    /**
     * @Title: DownloadingStatusView.
     * @Description: constructor.
     * @param: @param context
     * @param: @param attrs.
     * @throws
     */
    public DownloadingStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    /**
     * @Title: DownloadingStatusView.
     * @Description: constructor.
     * @param: @param context.
     * @throws
     */
    public DownloadingStatusView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    /**
     * @Title: initView.
     * @Description: initial the controls and add views.
     * @param: .
     * @return: void.
     * @throws
     */
    private void initView() {
        setGravity(Gravity.CENTER);
        mStatusImageView = new ImageView(mContext);
        mDeleteImageView = new ImageView(mContext);
        mFailTextView = new TextView(mContext);
        mFailTextView.setGravity(Gravity.CENTER);
        mFailTextView.setText(R.string.failed_download);
        mFailTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
        mFailTextView.setTextSize(13);
        mFailTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                mContext.getResources().getDrawable(R.drawable.downloading_failed), null, null);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mStatusImageView.setLayoutParams(params);
        mDeleteImageView.setLayoutParams(params);
        mFailTextView.setLayoutParams(params);

        mDownloadSpeedLayout = new DownloadSpeedLayout(mContext);
        LayoutParams downloadSpeedParams = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(mContext, 15));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mDownloadSpeedLayout.setLayoutParams(downloadSpeedParams);

        addView(mStatusImageView);
        addView(mDeleteImageView);
        addView(mFailTextView);
        addView(mDownloadSpeedLayout);
    }

    /**
     * @Title: setStatus.
     * @Description: set the status to show diffent view.
     * @param: @param status.
     * @return: void.
     * @throws
     */
    public void setStatus(int status) {
        if (Constants.isDebug) {
            Log.i(TAG, "status==" + status);
        }
        this.status = status;
        switch (status) {
            case Constants.PAUSEFLAG:
                mStatusImageView.setVisibility(View.VISIBLE);
                mFailTextView.setVisibility(View.GONE);
                mDownloadSpeedLayout.setVisibility(View.GONE);
                mDeleteImageView.setVisibility(View.GONE);
                mStatusImageView.setImageResource(R.drawable.downloading_pause);
                break;
            case Constants.CONTINUEFLAG:
                mStatusImageView.setVisibility(View.VISIBLE);
                mDeleteImageView.setVisibility(View.GONE);
                mFailTextView.setVisibility(View.GONE);
                mDownloadSpeedLayout.setVisibility(View.GONE);
                mStatusImageView.setImageResource(R.drawable.downloading_continue);
                break;
            case Constants.WAITINGFLAG:
                mStatusImageView.setVisibility(View.VISIBLE);
                mDeleteImageView.setVisibility(View.GONE);
                mFailTextView.setVisibility(View.GONE);
                mDownloadSpeedLayout.setVisibility(View.GONE);
                mStatusImageView.setImageResource(R.drawable.downloading);
                break;
            case Constants.DELETEFLAG:
                mDeleteImageView.setVisibility(View.VISIBLE);
                mStatusImageView.setVisibility(View.GONE);
                mFailTextView.setVisibility(View.GONE);
                mDownloadSpeedLayout.setVisibility(View.GONE);
                mDeleteImageView.setImageResource(R.drawable.downloading_delete);
                break;
            case Constants.FAILFLAG:
                mFailTextView.setVisibility(View.VISIBLE);
                mDeleteImageView.setVisibility(View.GONE);
                mStatusImageView.setVisibility(View.GONE);
                mDownloadSpeedLayout.setVisibility(View.GONE);
                break;
            case Constants.DOWNLOADINGFLAG:
                mDownloadSpeedLayout.setVisibility(View.VISIBLE);
                mDeleteImageView.setVisibility(View.GONE);
                mFailTextView.setVisibility(View.GONE);
                mStatusImageView.setVisibility(View.GONE);
                break;
            default:
                this.status = Constants.DOWNLOADINGFLAG;
                mDownloadSpeedLayout.setVisibility(View.VISIBLE);
                mDeleteImageView.setVisibility(View.GONE);
                mFailTextView.setVisibility(View.GONE);
                mStatusImageView.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * @Title: setSpeed.
     * @Description: set speed.
     * @param: @param mSpeed.
     * @return: void.
     * @throws
     */
    public void setSpeed(int mSpeed) {
        this.mSpeed = mSpeed;
        mDownloadSpeedLayout.setSpeed(mSpeed);
    }

    /**
     * @Title: setPercent.
     * @Description: set percent.
     * @param: @param mPercent.
     * @return: void.
     * @throws
     */
    public void setPercent(float mPercent) {
        this.mPercent = mPercent;
        mDownloadSpeedLayout.setPercent(mPercent);
    }

    /**
     * @Title: refreshDownloadProgress.
     * @Description: refresh the DownloadProgress view.
     * @param: .
     * @return: void.
     * @throws
     */
    public void refreshDownloadProgress() {
        if (this.status == Constants.DOWNLOADINGFLAG) {
            mDownloadSpeedLayout.refresh(mSpeed, mPercent);
        }
    }

}
