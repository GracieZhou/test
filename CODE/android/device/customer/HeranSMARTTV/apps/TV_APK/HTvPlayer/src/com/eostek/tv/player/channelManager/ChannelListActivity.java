
package com.eostek.tv.player.channelManager;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.ViewPropertyAnimator;

import com.eostek.tv.player.R;
import com.eostek.tv.player.channelManager.adapter.ChannelListAdapter;
import com.eostek.tv.player.util.ChannelManagerExt;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

/**
 * @projectName： EosTvPlayer
 * @moduleName：ChannelListActivity.java
 * @author jachensy.chen
 * @version 1.0.0.10
 * @time 2014-3-7
 * @Copyright © 2014 EOSTEK, Inc.
 */
public class ChannelListActivity extends Activity {
    public final static int ITEM_COUNT_ONE_PAGE = 11;

    private ChannelListViewHolder mHolder;

    private ChannelListAdapter mAdapter;

    private int mTotalCount = 0;

    private int mTotalPage = 1;

    // use for show on UI.Initial value is 1.
    private int mCurrentPage = 1;

    private List<ProgramInfo> mInfos;

    private ChannelManagerExt mManager;

    private ChannelListListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_channels);

        mManager = ChannelManagerExt.getInstance();

        mHolder = new ChannelListViewHolder(this);
        mHolder.findViews();
        mHolder.getTitleTxt().setText(R.string.dtv);

        mInfos = new ArrayList<ProgramInfo>();
        // get all channels(not include ship channels).
        mInfos.addAll(mManager.getChannels());
        if (mInfos != null && mInfos.size() > 0) {
            // get the position for setting the select position and page.if the
            // service is no DTV,select the first.
            int curPositon = mManager.getCurPosition();

            mAdapter = new ChannelListAdapter(this, mInfos);
            mAdapter.setCurrentPage(curPositon / ITEM_COUNT_ONE_PAGE);
            mHolder.getDtvList().setAdapter(mAdapter);
            mHolder.getDtvList().setSelection(curPositon % ITEM_COUNT_ONE_PAGE);
            mCurrentPage = curPositon / ITEM_COUNT_ONE_PAGE + 1;
            mTotalCount = mAdapter.getTotalCount();
            if (mTotalCount % ITEM_COUNT_ONE_PAGE == 0) {
                mTotalPage = mTotalCount / ITEM_COUNT_ONE_PAGE;
            } else {
                mTotalPage = mTotalCount / ITEM_COUNT_ONE_PAGE + 1;
            }

            mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
        }

        mListener = new ChannelListListener(mHolder, this, mManager, mInfos);
        mListener.setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mListener.getHandler().removeMessages(ChannelListListener.sChangeProgram);
            dismiss();
        }
        if (mInfos != null && mInfos.size() > 0) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    int tag_up = (Integer) mHolder.getDtvList().getSelectedView().getTag();
                    if (tag_up % ITEM_COUNT_ONE_PAGE == 0) {
                        if (tag_up / ITEM_COUNT_ONE_PAGE > 0) {
                            mCurrentPage = tag_up / ITEM_COUNT_ONE_PAGE;
                            mAdapter.setCurrentPage(mCurrentPage - 1);
                            mAdapter.notifyDataSetChanged();
                            mHolder.getDtvList().setSelection(ITEM_COUNT_ONE_PAGE - 1);
                            mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
                        } else if (tag_up / ITEM_COUNT_ONE_PAGE == 0) {
                            mCurrentPage = mTotalPage;
                            mAdapter.setCurrentPage(mCurrentPage - 1);
                            mAdapter.notifyDataSetChanged();
                            mHolder.getDtvList().setSelection(
                                    mInfos.size() % ITEM_COUNT_ONE_PAGE - 1);
                            mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    int tag_down = (Integer) mHolder.getDtvList().getSelectedView().getTag();
                    if ((tag_down + 1) % ITEM_COUNT_ONE_PAGE == 0 && tag_down + 1 < mTotalCount) {
                        mCurrentPage = (tag_down + 1) / ITEM_COUNT_ONE_PAGE + 1;
                        mAdapter.setCurrentPage((tag_down + 1) / ITEM_COUNT_ONE_PAGE);
                        mAdapter.notifyDataSetChanged();
                        mHolder.getDtvList().setSelection(0);
                        mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
                    } else if (tag_down == mInfos.size() - 1) {
                        mCurrentPage = 1;
                        mAdapter.setCurrentPage(0);
                        mAdapter.notifyDataSetChanged();
                        mHolder.getDtvList().setSelection(0);
                        mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
                    }
                    break;
                default:
                    break;
            }
        } else {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mInfos != null && mInfos.size() > 0
                && (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_PAGE_DOWN)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_PAGE_UP:
                    int tag_up = (Integer) mHolder.getDtvList().getSelectedView().getTag();
                    if (tag_up / ITEM_COUNT_ONE_PAGE > 0) {
                        mCurrentPage = tag_up / ITEM_COUNT_ONE_PAGE;
                        mAdapter.setCurrentPage(mCurrentPage - 1);
                        mAdapter.notifyDataSetChanged();
                        mHolder.getDtvList().setSelection(0);
                        mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
                    } else if (tag_up / ITEM_COUNT_ONE_PAGE == 0) {
                        mCurrentPage = mTotalPage;
                        mAdapter.setCurrentPage(mCurrentPage - 1);
                        mAdapter.notifyDataSetChanged();
                        mHolder.getDtvList().setSelection(0);
                        mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
                    }
                    break;
                case KeyEvent.KEYCODE_PAGE_DOWN:
                    int tag_down = (Integer) mHolder.getDtvList().getSelectedView().getTag();
                    if (tag_down / ITEM_COUNT_ONE_PAGE < (mTotalCount - 1) / ITEM_COUNT_ONE_PAGE) {
                        mCurrentPage = tag_down / ITEM_COUNT_ONE_PAGE + 2;
                        mAdapter.setCurrentPage(tag_down / ITEM_COUNT_ONE_PAGE + 1);
                        mAdapter.notifyDataSetChanged();
                        mHolder.getDtvList().setSelection(0);
                        mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
                    } else if (tag_down / ITEM_COUNT_ONE_PAGE == (mTotalCount - 1)
                            / ITEM_COUNT_ONE_PAGE) {
                        mCurrentPage = 1;
                        mAdapter.setCurrentPage(mCurrentPage - 1);
                        mAdapter.notifyDataSetChanged();
                        mHolder.getDtvList().setSelection(0);
                        mHolder.getDtvPage().setText(mCurrentPage + "/" + mTotalPage);
                    }
                    break;
                default:
                    break;
            }
            mListener.getHandler().removeMessages(ChannelListListener.sChangeProgram);
            Message msg = new Message();
            msg.arg1 = (mCurrentPage - 1) * ITEM_COUNT_ONE_PAGE;
            msg.arg2 = -1;
            msg.what = ChannelListListener.sChangeProgram;
            mListener.getHandler().sendMessageDelayed(msg, ChannelListListener.sDelayTime);
        } else {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    public List<ProgramInfo> getProgramInfos() {
        return mInfos;
    }

    public int getCurPage() {
        return mCurrentPage;
    }

    private void show() {
        findViewById(R.id.channellist_main_bg).setAlpha(1f);
        float x = 0f;
        float y = 0f;
        findViewById(R.id.channellist_main_bg).setX(-400f);
        findViewById(R.id.channellist_main_bg).setY(0f);

        ViewPropertyAnimator vpa = findViewById(R.id.channellist_main_bg).animate().x(x).y(y);
        vpa.setDuration(500);
    }

    private void dismiss() {
        findViewById(R.id.channellist_main_bg).setAlpha(1f);
        float x = -400f;
        float y = 0f;
        findViewById(R.id.channellist_main_bg).setX(0f);
        findViewById(R.id.channellist_main_bg).setY(0f);

        ViewPropertyAnimator vpa = findViewById(R.id.channellist_main_bg).animate().x(x).y(y);
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
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
    }
}
