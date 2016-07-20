
package com.android.settings.bugreport;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.settings.util.ViewHolder;
import com.android.settings.widget.TitleWidget;

/**
 * @ClassName: FeedbackRecordHolder.
 * @Description:deal with FeedbackRecord view .
 * @author: lucky.li.
 * @date: Sep 15, 2015 10:49:22 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class FeedbackRecordHolder {
    /**
     * Show the feedback records
     */
    private ListView mFeedbackListView;

    /**
     * arrow up
     */
    private ImageView mArrowUp;

    /**
     * arrow down
     */
    private ImageView mArrowDown;

    /**
     * title
     */
    private TitleWidget mTitle;

    /**
     * mProgressBar
     */
    private ProgressBar mProgressBar;

    private BaseAdapter mAdapter;

    private List<RecordDetailBean> recordsList;

    /**
     * Update the down arrow logo flag
     */
    private final int UPDATEARROWDOWN = 0x001;

    /**
     * Update the up arrow logo flag
     */
    private final int UPDATEARROWUP = 0x002;

    /**
     * delay time
     */
    private final int DELAYTIME = 500;

    /**
     * record detail action
     */
    private final String recordDetailAction = "android.settings.action.FEEDBACKRECORDDETAILACTIVITY";

    private FeedbackRecordActivity mContext;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == UPDATEARROWDOWN) {
                mArrowDown.setImageResource(R.drawable.arrow_down_gray);
            } else if (msg.what == UPDATEARROWUP) {
                mArrowUp.setImageResource(R.drawable.arrow_up_gray);
            }
        };
    };

    /**
     * @Title: FeedbackRecordHolder
     * @Description: constructor
     * @param: @param context
     * @throws
     */
    public FeedbackRecordHolder(FeedbackRecordActivity context) {
        this.mContext = context;
        findViews();
        initViews();
    }

    /**
     * @Title: findViews
     * @Description: initial control
     * @param:
     * @return: void
     * @throws
     */
    public void findViews() {
        mTitle = (TitleWidget) mContext.findViewById(R.id.activity_shortcut_title);
        mFeedbackListView = (ListView) mContext.findViewById(R.id.shortcut_list);
        mArrowUp = (ImageView) mContext.findViewById(R.id.arrow_up);
        mArrowDown = (ImageView) mContext.findViewById(R.id.arrow_down);
        mProgressBar = (ProgressBar) mContext.findViewById(R.id.progressBar);
    }

    /**
     * @Title: showProgress.
     * @Description: show the Progress.
     * @param: .
     * @return: void.
     * @throws
     */
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * @Title: hideProgress.
     * @Description: hide Progress.
     * @param: .
     * @return: void.
     * @throws
     */
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * @Title: refresh.
     * @Description: refresh datas.
     * @param: @param feedbackRecord.
     * @return: void.
     * @throws
     */
    public void refresh(FeedbackRecordBean record) {
        if (record != null && "0".equals(record.getErr())) {
            if (record.getBd().getIts() != null && record.getBd().getIts().size() > 0) {
                recordsList = record.getBd().getIts();
                if (mAdapter == null) {
                    mAdapter = new ShortcutAdapter();
                    mFeedbackListView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                    mFeedbackListView.setSelection(0);
                }
            } else {
                if (mContext.getCurrentPageIndex() > 1) {
                    mContext.setCurrentPageIndex(mContext.getCurrentPageIndex() - 1);
                }
            }
        }
    }

    /**
     * @Title: initViews
     * @Description: initial title and list item
     * @param:
     * @return: void
     * @throws
     */
    private void initViews() {
        mTitle.setSubTitleText(mContext.getString(R.string.bugreport_title),
                mContext.getString(R.string.feedback_record));
        mFeedbackListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(recordDetailAction);
                Bundle bundle = new Bundle();
                bundle.putSerializable("recordDetail", recordsList.get(position));
                // put data to Intent
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        mFeedbackListView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        mHandler.removeMessages(UPDATEARROWDOWN);
                        mArrowDown.setImageResource(R.drawable.arrow_down_white);
                        mHandler.sendEmptyMessageDelayed(UPDATEARROWDOWN, DELAYTIME);
                        if (recordsList.size() >= 4
                                && mFeedbackListView.getSelectedItemPosition() == recordsList.size() - 1) {
                            mContext.setCurrentPageIndex(mContext.getCurrentPageIndex() + 1);
                            mContext.constructRequestParam(mContext.getCurrentPageIndex());
                            mContext.initJsonRequest();
                            showProgress();
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        mHandler.removeMessages(UPDATEARROWUP);
                        mArrowUp.setImageResource(R.drawable.arrow_up_white);
                        mHandler.sendEmptyMessageDelayed(UPDATEARROWUP, DELAYTIME);
                        if (mFeedbackListView.getSelectedItemPosition() == 0) {
                            if (mContext.getCurrentPageIndex() > 1) {
                                mContext.setCurrentPageIndex(mContext.getCurrentPageIndex() - 1);
                                mContext.constructRequestParam(mContext.getCurrentPageIndex());
                                mContext.initJsonRequest();
                                showProgress();
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * @ClassName: ShortcutAdapter
     * @Description:ShortcutAdapter.
     * @author: lucky.li
     * @date: 2015-8-24 pm5:01:34
     * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
     */
    class ShortcutAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return recordsList.size();
        }

        @Override
        public RecordDetailBean getItem(int position) {
            return recordsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_feedback_record, null);
            }
            TextView statusView = ViewHolder.get(convertView, R.id.status);
            TextView feedbackDate = ViewHolder.get(convertView, R.id.record_date);
            TextView feedbackContent = ViewHolder.get(convertView, R.id.record_content);
            String statusString = "";
            switch (getItem(position).getStatus()) {
                case 0:
                    statusString = mContext.getString(R.string.dealing);
                    break;
                case 1:
                    statusString = mContext.getString(R.string.acceptted);
                    break;
                case 2:
                    statusString = mContext.getString(R.string.resolved);
                    break;
                case 3:
                    statusString = mContext.getString(R.string.thanks);
                    break;
                case 4:
                    statusString = mContext.getString(R.string.published);
                    break;
                default:
                    statusString = mContext.getString(R.string.dealing);
                    break;
            }
            statusView.setText(statusString);
            feedbackContent.setText(getItem(position).getSubmitContent());
            feedbackDate.setText(Utils.longParseDate(getItem(position).getSubmitTime()));
            return convertView;
        }
    }
}
