
package com.eostek.tv.channel;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.eostek.tv.R;
import com.eostek.tv.channel.adapter.ChannelListAdapter;
import com.eostek.tv.utils.ChannelManagerExt;
import com.eostek.tv.widget.AnimatedSelector;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

/**
 * @projectName： EosTvPlayer
 * @moduleName： DtvProgramViewHolder.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-20
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class ChannelListHolder {
    private Activity mContext;

    private ListView mDtvListView;

    /**
     * show page num
     */
    private TextView mDtvPageTxt;

    private View mSelector;

    private AnimatedSelector animatedSelector;

    private RelativeLayout mainView;

    private Handler mHandler;

    private ChannelListAdapter mAdapter;

    /**
     * toatal channel num
     */
    private int mTotalCount = 0;

    /**
     * total page num
     */
    private int mTotalPage = 1;

    /**
     * use for show on UI.Initial value is 1.
     */
    private int mCurrentPage = 1;

    /**
     * channel infos
     */
    private List<ProgramInfo> mProgramInfos;

    /**
     * @param context
     * @param mHandler
     */
    public ChannelListHolder(Activity context, Handler mHandler) {
        this.mContext = context;
        this.mHandler = mHandler;
        initViews();
        initAdapter();
        registetListener();
    }

    /**
     * initialize the view
     */
    public void initViews() {
        mContext.setContentView(R.layout.channel_list_activity);
        ((TextView) mContext.findViewById(R.id.list_title)).setText(R.string.dtv);
        mDtvListView = (ListView) mContext.findViewById(R.id.dtvprogram_lv);
        mDtvPageTxt = (TextView) mContext.findViewById(R.id.dtvpage);
        mainView = (RelativeLayout) mContext.findViewById(R.id.channellist_main_bg);
        mSelector = mContext.findViewById(R.id.channel_selector);
        animatedSelector = new AnimatedSelector(mSelector, mDtvListView.getSelector());
        mDtvListView.setSelector(animatedSelector);
        animatedSelector.setLeftOffset(20);
    }

    /**
     * initialize the adapter
     */
    private void initAdapter() {
        // get all channels(not include ship channels).
        mProgramInfos = ChannelManagerExt.getInstance().getChannels();
        if (mProgramInfos != null && mProgramInfos.size() > 0) {
            // get the position for setting the select position and page.if the
            // service is no DTV,select the first.
            int curPositon = ChannelManagerExt.getInstance().getCurPosition();
            mAdapter = new ChannelListAdapter(mContext, mProgramInfos);
            mAdapter.setCurrentPage(curPositon / ChannelListActivity.ITEM_COUNT_ONE_PAGE);
            mDtvListView.setAdapter(mAdapter);
            mDtvListView.setSelection(curPositon % ChannelListActivity.ITEM_COUNT_ONE_PAGE);

            mCurrentPage = curPositon / ChannelListActivity.ITEM_COUNT_ONE_PAGE + 1;
            mTotalCount = mAdapter.getTotalCount();
            if (mTotalCount % ChannelListActivity.ITEM_COUNT_ONE_PAGE == 0) {
                mTotalPage = mTotalCount / ChannelListActivity.ITEM_COUNT_ONE_PAGE;
            } else {
                mTotalPage = mTotalCount / ChannelListActivity.ITEM_COUNT_ONE_PAGE + 1;
            }
            setPageText(mCurrentPage + "/" + mTotalPage);
        }
    }

    /**
     * register the listener
     */
    private void registetListener() {
        mDtvListView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !mDtvListView.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        animatedSelector.hideView();
                    }
                }
                return false;
            }
        });
        mDtvListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
            }
        });
        mDtvListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                mHandler.removeMessages(ChannelListActivity.CHANGEPROGRAM);
                ChannelManagerExt.getInstance().programSel(
                        position + ((mCurrentPage - 1)) * ChannelListActivity.ITEM_COUNT_ONE_PAGE);
            }
        });
        mDtvListView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
                animatedSelector.ensureViewVisible();
                mHandler.removeMessages(ChannelListActivity.CHANGEPROGRAM);
                Message msg = mHandler.obtainMessage();
                msg.what = ChannelListActivity.CHANGEPROGRAM;
                msg.arg1 = position + ((mCurrentPage - 1))
                        * ChannelListActivity.ITEM_COUNT_ONE_PAGE;
                mHandler.sendMessageDelayed(msg, ChannelListActivity.DELAYTIME);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                animatedSelector.hideView();
            }
        });
    }

    /**
     * refresh the listView Response to upEvent
     */
    public void refreshUpEvent() {
        int tag_up = (Integer) mDtvListView.getSelectedView().getTag();
        if (tag_up % ChannelListActivity.ITEM_COUNT_ONE_PAGE == 0) {
            if (tag_up / ChannelListActivity.ITEM_COUNT_ONE_PAGE > 0) {
                mCurrentPage = tag_up / ChannelListActivity.ITEM_COUNT_ONE_PAGE;
                mAdapter.setCurrentPage(mCurrentPage - 1);
                mAdapter.notifyDataSetChanged();
                mDtvListView.setSelection(ChannelListActivity.ITEM_COUNT_ONE_PAGE - 1);
                setPageText(mCurrentPage + "/" + mTotalPage);
            } else if (tag_up / ChannelListActivity.ITEM_COUNT_ONE_PAGE == 0) {
                mCurrentPage = mTotalPage;
                mAdapter.setCurrentPage(mCurrentPage - 1);
                mAdapter.notifyDataSetChanged();
                mDtvListView.setSelection(mProgramInfos.size()
                        % ChannelListActivity.ITEM_COUNT_ONE_PAGE - 1);
                setPageText(mCurrentPage + "/" + mTotalPage);
            }
        }
    }

    /**
     * refresh the listView Response to downEvent
     */
    public void refreshDownEvent() {
        int tag_down = (Integer) mDtvListView.getSelectedView().getTag();
        if ((tag_down + 1) % ChannelListActivity.ITEM_COUNT_ONE_PAGE == 0
                && tag_down + 1 < mTotalCount) {
            mCurrentPage = (tag_down + 1) / ChannelListActivity.ITEM_COUNT_ONE_PAGE + 1;
            mAdapter.setCurrentPage((tag_down + 1) / ChannelListActivity.ITEM_COUNT_ONE_PAGE);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        } else if (tag_down == mProgramInfos.size() - 1) {
            mCurrentPage = 1;
            mAdapter.setCurrentPage(0);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        }
    }

    /**
     * refresh the listView Response to pageUpEvent
     */
    public void refreshPageUpEvent() {
        int tag_up = (Integer) mDtvListView.getSelectedView().getTag();
        if (tag_up / ChannelListActivity.ITEM_COUNT_ONE_PAGE > 0) {
            mCurrentPage = tag_up / ChannelListActivity.ITEM_COUNT_ONE_PAGE;
            mAdapter.setCurrentPage(mCurrentPage - 1);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        } else if (tag_up / ChannelListActivity.ITEM_COUNT_ONE_PAGE == 0) {
            mCurrentPage = mTotalPage;
            mAdapter.setCurrentPage(mCurrentPage - 1);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        }
    }

    /**
     * refresh the listView Response to pageDownEvent
     */
    public void refreshPageDownEvent() {
        int tag_down = (Integer) mDtvListView.getSelectedView().getTag();
        if (tag_down / ChannelListActivity.ITEM_COUNT_ONE_PAGE < (mTotalCount - 1)
                / ChannelListActivity.ITEM_COUNT_ONE_PAGE) {
            mCurrentPage = tag_down / ChannelListActivity.ITEM_COUNT_ONE_PAGE + 2;
            mAdapter.setCurrentPage(tag_down / ChannelListActivity.ITEM_COUNT_ONE_PAGE + 1);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        } else if (tag_down / ChannelListActivity.ITEM_COUNT_ONE_PAGE == (mTotalCount - 1)
                / ChannelListActivity.ITEM_COUNT_ONE_PAGE) {
            mCurrentPage = 1;
            mAdapter.setCurrentPage(mCurrentPage - 1);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        }
    }

    /**
     * change the channel
     */
    public void updateSelectChannel() {
        mHandler.removeMessages(ChannelListActivity.CHANGEPROGRAM);
        Message msg = new Message();
        msg.arg1 = (mCurrentPage - 1) * ChannelListActivity.ITEM_COUNT_ONE_PAGE;
        msg.arg2 = -1;
        msg.what = ChannelListActivity.CHANGEPROGRAM;
        mHandler.sendMessageDelayed(msg, ChannelListActivity.DELAYTIME);
    }

    /**
     * add the showAnimation
     */
    public void show() {
        mainView.setAlpha(1f);
        float x = 0f;
        float y = 0f;
        mainView.setX(-400f);
        mainView.setY(0f);
        ViewPropertyAnimator vpa = mainView.animate().x(x).y(y);
        vpa.setDuration(500);
    }

    /**
     * add the dismissAnimation
     */
    public void dismiss() {
        mainView.setAlpha(1f);
        float x = -400f;
        float y = 0f;
        mainView.setX(0f);
        mainView.setY(0f);
        ViewPropertyAnimator vpa = mainView.animate().x(x).y(y);
        vpa.setDuration(300);
        vpa.setListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mContext.finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
    }

    /**
     * @param isShow
     */
    public void showSelector(boolean isShow) {
        if (isShow) {
            animatedSelector.ensureViewVisible();
        } else {
            animatedSelector.hideView();
        }
    }

    /**
     * update the page text
     * 
     * @param text
     */
    public void setPageText(String text) {
        mDtvPageTxt.setText(text);
    }

}
