
package com.eostek.tv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.eostek.tv.R;

public class TextProgress extends RelativeLayout {

    private String mDrawingText = "";

    private ProgressBar mProgress;

    private int PROGRESSID = 0x001;

    private ProgressText mProgressText;

    private ProgressBar mABProgressBar;

    private android.widget.RelativeLayout.LayoutParams mABParams;

    public TextProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init(context);
    }

    public TextProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init(context);
    }

    public TextProgress(Context context) {
        super(context);
        setWillNotDraw(false);
        init(context);
    }

    private void init(Context mContext) {
        mProgress = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
        mProgress.setId(PROGRESSID);
        mProgress.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(
                R.dimen.play_record_progress_layout_height)));
        mProgress.setProgressDrawable(getResources().getDrawable(R.drawable.pvr_progress_bg));

        mABProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
        mABProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.pvr_progress_bg));
        mABParams = new android.widget.RelativeLayout.LayoutParams(0, (int) getResources().getDimension(
                R.dimen.play_record_progress_layout_height));
        mABProgressBar.setVisibility(View.INVISIBLE);

        mProgressText = new ProgressText(mContext);
        LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources()
                .getDimension(R.dimen.progress_layout_height));
        params.addRule(RelativeLayout.BELOW, mProgress.getId());
        mProgressText.setLayoutParams(params);
        mProgressText.setGravity(Gravity.CENTER_VERTICAL);
        addView(mProgress);
        addView(mABProgressBar);
        addView(mProgressText);

    }

    public int getProgress() {
        return mProgress.getProgress();
    }

    public int getProgressMax() {
        // if do not set max value,default set to 1
        if (mProgress.getMax() == 0) {
            return 1;
        } else {
            return mProgress.getMax();
        }
    }

    public void setProgressMax(int value) {
        mProgress.setMax(value);
    }

    public ProgressBar getABProgressBar() {
        return mABProgressBar;
    }

    public LayoutParams getABParams() {
        return mABParams;
    }

    public void setABProgressVisible(int visibility) {
        mABProgressBar.setVisibility(visibility);
    }

    public void setMaxProgress(int max) {
        mProgress.setMax(max);
    }

    /**
     * set the progress
     * 
     * @param text
     * @param progress
     */
    public void setTextProgress(String text, int progress) {
        this.mDrawingText = text;
        mProgress.setProgress(progress);
        float position = progress * mProgress.getWidth() / (mProgress.getMax() == 0 ? 1 : mProgress.getMax());
        mProgressText.setmText(mDrawingText);
        mProgressText.setX(position > dip2px(40) ? position - dip2px(40) : 0);
        mProgressText.refresh();
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
