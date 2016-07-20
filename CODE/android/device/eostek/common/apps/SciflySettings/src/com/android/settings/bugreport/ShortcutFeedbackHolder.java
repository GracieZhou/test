
package com.android.settings.bugreport;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.android.settings.util.ViewHolder;
import com.android.settings.widget.TitleWidget;

/**
 * @ClassName: ShortcutFeedbackHolder.
 * @Description:ShortcutFeedback ui.
 * @author: lucky.li.
 * @date: 2015-8-26 pm 12:34:00
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
 */
public class ShortcutFeedbackHolder {

    private Activity mContext;

    /**
     * mShortcutListView
     */
    private ListView mShortcutListView;

    /**
     * up array
     */
    private ImageView mArrowUp;

    /**
     * down array
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

    /**
     * shortcutStrings
     */
    private String[] shortcutStrings;

    private ShortcutAdapter mAdapter;

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
     * @Title: ShortcutFeedbackHolder.
     * @Description: constructor.
     * @param: @param context.
     * @throws
     */
    public ShortcutFeedbackHolder(Activity context) {
        this.mContext = context;
        findViews();
        initViews();
    }

    /**
     * @Title: findViews.
     * @Description:initial controls.
     * @param:
     * @return: void
     * @throws
     */
    private void findViews() {
        mTitle = (TitleWidget) mContext.findViewById(R.id.activity_shortcut_title);
        mShortcutListView = (ListView) mContext.findViewById(R.id.shortcut_list);
        mShortcutListView.setSelector(R.drawable.listview_item_focus);
        mArrowUp = (ImageView) mContext.findViewById(R.id.arrow_up);
        mArrowDown = (ImageView) mContext.findViewById(R.id.arrow_down);
        mProgressBar = (ProgressBar) mContext.findViewById(R.id.progressBar);
    }

    /**
     * @Title: initViews
     * @Description: initial title and list item.
     * @param:
     * @return: void
     * @throws
     */
    private void initViews() {
        mTitle.setSubTitleText(mContext.getString(R.string.bugreport_title),
                mContext.getString(R.string.shortcut_title));
        mShortcutListView.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("static-access")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("shortcutFeedback", shortcutStrings[position]);
                mContext.setResult(mContext.RESULT_OK, intent);
                mContext.finish();
                mContext.overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
            }
        });
        mShortcutListView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        mHandler.removeMessages(UPDATEARROWDOWN);
                        mArrowDown.setImageResource(R.drawable.arrow_down_white);
                        mHandler.sendEmptyMessageDelayed(UPDATEARROWDOWN, DELAYTIME);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        mHandler.removeMessages(UPDATEARROWUP);
                        mArrowUp.setImageResource(R.drawable.arrow_up_white);
                        mHandler.sendEmptyMessageDelayed(UPDATEARROWUP, DELAYTIME);
                    }
                }
                return false;
            }
        });
    }

    /**
     * @Title: showProgress.
     * @Description: showProgress.
     * @param: .
     * @return: void.
     * @throws
     */
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * @Title: hideProgress.
     * @Description: hideProgress.
     * @param: .
     * @return: void.
     * @throws
     */
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * @Title: refresh
     * @Description: refresh ui.
     * @param:
     * @return: void
     * @throws
     */
    public void refresh(ShorcutFeedbackBean bean) {
        // If you have data and access to success
        if (bean != null && "0".equals(bean.getErr())) {
            shortcutStrings = bean.getBd();
            if (shortcutStrings != null && shortcutStrings.length > 0) {
                if (mAdapter == null) {
                    mAdapter = new ShortcutAdapter();
                    mShortcutListView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * @ClassName: ShortcutAdapter
     * @Description:ShortcutAdapter.
     * @author: lucky.li
     * @date: 2015-8-24 下午5:01:34
     * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
     */
    class ShortcutAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return shortcutStrings.length;
        }

        @Override
        public Object getItem(int position) {
            return shortcutStrings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_shortcut_feedback, null);
            }
            TextView shortcutContentView = ViewHolder.get(convertView, R.id.shortcut_content);
            shortcutContentView.setText(shortcutStrings[position]);
            return convertView;
        }

    }
}
