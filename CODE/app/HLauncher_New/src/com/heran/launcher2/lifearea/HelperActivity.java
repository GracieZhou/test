
package com.heran.launcher2.lifearea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.heran.launcher2.R;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.Constants;

public class HelperActivity extends Activity {
    private static String mUri = "http://www.heran.com.tw/";

    private ListView mListView;

    // the contents of the array in the ListView
    private String[] mHelpTips;

    private MyHelperAdapter myHelperAdapter;

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.DISSMISS_HELPER:
                    finish();
                    overridePendingTransition(0, R.anim.photo_push_left_out);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.helper_main);

        mListView = (ListView) findViewById(R.id.list);
        mHelpTips = getResources().getStringArray(R.array.help_array);
        myHelperAdapter = new MyHelperAdapter(getApplication());
        mListView.setAdapter(myHelperAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                switch (arg2) {
                    case 1:
                        mUri = "http://www.jowinwin.com/hertv2msd/member/index.php?r=site/member";
                        goToWebUrl(mUri);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 10秒就關掉此Activity
        mHandler.sendEmptyMessageDelayed(Constants.DISSMISS_HELPER, Constants.DISSMISS_TIME);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 從列隊清除Messages
        mHandler.removeMessages(Constants.DISSMISS_HELPER);
        // 重新帶入,10秒後關閉
        mHandler.sendEmptyMessageDelayed(Constants.DISSMISS_HELPER, Constants.DISSMISS_TIME);
        switch (keyCode) {
            case KeyEvent.KEYCODE_PROG_RED: // finish() the HelperActivity
                finish();
                overridePendingTransition(0, R.anim.photo_push_left_out);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                // 當ItemPosition是最後一項時 ,移至第一項
                if (mListView.getSelectedItemPosition() == mHelpTips.length - 1) {
                    mListView.setSelection(0);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                // 當ItemPosition是第一項時 ,移致最後一項
                if (mListView.getSelectedItemPosition() == 0) {
                    mListView.setSelection(mHelpTips.length - 1);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.photo_push_left_out);
    }

    /*
     * Jump to a specific web page
     * @param url The web page URL
     */
    private void goToWebUrl(String url) {
        Intent intent = new Intent(this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        if (url == null || url.equals("")) {
            url = "http://www.baidu.com/";
        }
        bundle.putString("URL", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public final class ViewHolder {
        public TextView mTextView;
    }

    // custom MyHelperAdapter
    class MyHelperAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;

        public MyHelperAdapter(Context mContext) {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mHelpTips.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mHolder = null;
            if (null == convertView) {
                mHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.help_item, null);
                mHolder.mTextView = (TextView) convertView.findViewById(R.id.help_item_text);
                mHolder.mTextView.setText(mHelpTips[position]);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }
    }
}
