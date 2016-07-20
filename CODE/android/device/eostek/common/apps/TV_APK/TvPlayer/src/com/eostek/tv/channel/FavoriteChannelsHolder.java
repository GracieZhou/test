
package com.eostek.tv.channel;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.eostek.tv.R;
import com.eostek.tv.channel.adapter.ChannelListAdapter;
import com.eostek.tv.utils.ChannelManagerExt;
import com.eostek.tv.widget.AnimatedSelector;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

public class FavoriteChannelsHolder {
    public final static int ITEM_COUNT_ONE_PAGE = 11;

    private Activity mContext;

    private ListView mDtvListView;

    /**
     * show page num
     */
    private TextView mDtvPageTxt;

    private View mSelector;

    private AnimatedSelector animatedSelector;

    private RelativeLayout mainView;

    /**
     * channel infos
     */
    private List<ProgramInfo> mInfos;

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
     * currrent page num
     */
    private int mCurrentPage = 1;

    /**
     * channel num
     */
    private List<Integer> mChannelNums;

    /**
     * @param mContext
     */
    public FavoriteChannelsHolder(Activity mContext) {
        this.mContext = mContext;
        initViews();
        initAdapter();
        registetListener();
    }

    /**
     * initialize the view
     */
    private void initViews() {
        mContext.setContentView(R.layout.channel_list_activity);
        ((TextView) mContext.findViewById(R.id.list_title)).setText(R.string.favorite);
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
        mInfos = ChannelManagerExt.getInstance().getFavoriteChannels();
        mChannelNums = ChannelManagerExt.getInstance().getChannelNums();
        if (mInfos != null && mInfos.size() > 0) {
            int curPositon = 0;
            mAdapter = new ChannelListAdapter(mContext, mInfos);
            mAdapter.setCurrentPage(curPositon / ITEM_COUNT_ONE_PAGE);
            mDtvListView.setAdapter(mAdapter);
            mDtvListView.setSelection(curPositon % ITEM_COUNT_ONE_PAGE);

            mCurrentPage = curPositon / ITEM_COUNT_ONE_PAGE + 1;
            mTotalCount = mAdapter.getTotalCount();
            if (mTotalCount % ITEM_COUNT_ONE_PAGE == 0) {
                mTotalPage = mTotalCount / ITEM_COUNT_ONE_PAGE;
            } else {
                mTotalPage = mTotalCount / ITEM_COUNT_ONE_PAGE + 1;
            }
            setPageText(mCurrentPage + "/" + mTotalPage);
        }
    }

    /**
     * register the listener
     */
    private void registetListener() {
        mDtvListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int index = mChannelNums.indexOf(mInfos.get(position).number);
                if (index != -1) {
                    ChannelManagerExt.getInstance().programSel(index);
                }
            }
        });
    }

    /**
     * refresh the listView Response to upEvent
     */
    public void refreshUpEvent() {
        int tag_up = (Integer) mDtvListView.getSelectedView().getTag();
        if (tag_up % ITEM_COUNT_ONE_PAGE == 0) {
            if (tag_up / ITEM_COUNT_ONE_PAGE > 0) {
                mCurrentPage = tag_up / ITEM_COUNT_ONE_PAGE;
                mAdapter.setCurrentPage(mCurrentPage - 1);
                mAdapter.notifyDataSetChanged();
                mDtvListView.setSelection(ITEM_COUNT_ONE_PAGE - 1);
                setPageText(mCurrentPage + "/" + mTotalPage);
            } else if (tag_up / ITEM_COUNT_ONE_PAGE == 0) {
                mCurrentPage = mTotalPage;
                mAdapter.setCurrentPage(mCurrentPage - 1);
                mAdapter.notifyDataSetChanged();
                mDtvListView.setSelection(mInfos.size() % ITEM_COUNT_ONE_PAGE - 1);
                setPageText(mCurrentPage + "/" + mTotalPage);
            }
        }
    }

    /**
     * refresh the listView Response to downEvent
     */
    public void refreshDownEvent() {
        int tag_down = (Integer) mDtvListView.getSelectedView().getTag();
        if ((tag_down + 1) % ITEM_COUNT_ONE_PAGE == 0 && tag_down + 1 < mTotalCount) {
            mCurrentPage = (tag_down + 1) / ITEM_COUNT_ONE_PAGE + 1;
            mAdapter.setCurrentPage((tag_down + 1) / ITEM_COUNT_ONE_PAGE);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        } else if (tag_down == mInfos.size() - 1) {
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
        if (tag_up / ITEM_COUNT_ONE_PAGE > 0) {
            mCurrentPage = tag_up / ITEM_COUNT_ONE_PAGE;
            mAdapter.setCurrentPage(mCurrentPage - 1);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        } else if (tag_up / ITEM_COUNT_ONE_PAGE == 0) {
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
        if (tag_down / ITEM_COUNT_ONE_PAGE < (mTotalCount - 1) / ITEM_COUNT_ONE_PAGE) {
            mCurrentPage = tag_down / ITEM_COUNT_ONE_PAGE + 2;
            mAdapter.setCurrentPage(tag_down / ITEM_COUNT_ONE_PAGE + 1);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
        } else if (tag_down / ITEM_COUNT_ONE_PAGE == (mTotalCount - 1) / ITEM_COUNT_ONE_PAGE) {
            mCurrentPage = 1;
            mAdapter.setCurrentPage(mCurrentPage - 1);
            mAdapter.notifyDataSetChanged();
            mDtvListView.setSelection(0);
            setPageText(mCurrentPage + "/" + mTotalPage);
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
}
