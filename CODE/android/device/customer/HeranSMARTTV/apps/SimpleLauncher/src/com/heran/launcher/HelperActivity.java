
package com.heran.launcher;

import com.heran.launcher.util.Constants;
import com.heran.launcher.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
import android.widget.Toast;



public class HelperActivity extends Activity {
    private static String mUri = "http://www.heran.com.tw/";

    private ListView mListView;

    // the contents of the array in the ListView
    private String[] mHelpTips;

    //private AnimatedSelector animatedSelector;

    private MyHelperAdapter myHelperAdapter;
    
    private Handler mHandler = new Handler() {

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

    protected boolean adStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.helper_main);

        mListView = (ListView) findViewById(R.id.list);
        mHelpTips = getResources().getStringArray(R.array.help_array);
        myHelperAdapter = new MyHelperAdapter(getApplication());
        mListView.setAdapter(myHelperAdapter);
        //View selector = findViewById(R.id.list_selector);
        //animatedSelector = new AnimatedSelector(selector, mListView.getSelector());
        //mListView.setSelector(animatedSelector);
      /*  mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
            }
        });

        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                animatedSelector.ensureViewVisible();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                animatedSelector.hideView();
            }
        });

        mListView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !mListView.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        animatedSelector.hideView();
                    }
                }
                return false;
            }
        });*/
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // when click the first item,show the TipDetailView;click other
                // item ,hide the TipDetailView
                Intent intent;
                switch (arg2) {
                    case 1:
                        mUri = "http://www.jowinwin.com/hertv2msd/member/index.php?r=site/member";
                        goToWebUrl(mUri);
                        break;
                    case 2:
//                        intent = new Intent("android.intent.action.MAIN");
//                        intent.setClassName("com.heran.instruction", "com.heran.instruction.MainActivity");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//                        startActivity(intent);
//                        finish();
                    	intent = new Intent("android.intent.action.MAIN");
                        intent.setClassName("com.example.remotecontrol", "com.example.remotecontrol.MainActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(intent);
                        finish();

                        break;
                    case 3:
//                        mUri = "http://www.jowinwin.com/hertv2msd/member/index.php?r=site/member";
//                        goToWebUrl(mUri);
//                        開啟「logo2廣告開關」對話框
//                        mHandler.removeMessages(Constants.DISSMISS_HELPER);
//                        adSwitchDialog2();
                        break;
                    case 4:
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
        mHandler.sendEmptyMessageDelayed(Constants.DISSMISS_HELPER, Constants.DISSMISS_TIME);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mHandler.removeMessages(Constants.DISSMISS_HELPER);
        mHandler.sendEmptyMessageDelayed(Constants.DISSMISS_HELPER, Constants.DISSMISS_TIME);
        switch (keyCode) {
            case KeyEvent.KEYCODE_PROG_RED: // finish() the HelperActivity
                finish();
                overridePendingTransition(0, R.anim.photo_push_left_out);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mListView.getSelectedItemPosition() == mHelpTips.length - 1) {
                    mListView.setSelection(0);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
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
        Intent intent = new Intent("eos.intent.action.WEB");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        Bundle bundle = new Bundle();
        bundle.putString("URL", url);
        intent.putExtras(bundle);

        if (Utils.isIntentAvailable(this, intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "intent is not found", Toast.LENGTH_SHORT).show();
        }

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

        private LayoutInflater mInflater;

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
                convertView = mInflater.inflate(R.layout.help_item, parent,false);
                mHolder.mTextView = (TextView) convertView.findViewById(R.id.help_item_text);
                mHolder.mTextView.setText(mHelpTips[position]);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }
    }

   /* private void showSelector(boolean bShow) {
        if (animatedSelector == null)
            return;
        if (bShow) {
            animatedSelector.ensureViewVisible();
        } else {
            animatedSelector.hideView();
        }
    }*/
    public void adSwitchDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("活動設定");
        builder.setMessage("您願意接受之後由HERTV平台提供的好康活動訊息嗎？");
        builder.setPositiveButton("願意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Settings.System.putInt(getContentResolver(), "adSwitch", 1);
                adStatus  = true;
                Toast.makeText(getApplicationContext(), "已開啟「接受好康活動訊息」", Toast.LENGTH_LONG).show();
                finish();
                overridePendingTransition(0, R.anim.photo_push_left_out);
            }

        });

        builder.setNeutralButton("不願意", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Settings.System.putInt(getContentResolver(), "adSwitch", 2);
                adStatus = false;
                Toast.makeText(getApplicationContext(), "已關閉「接受好康活動訊息」", Toast.LENGTH_LONG).show();
                finish();
                overridePendingTransition(0, R.anim.photo_push_left_out);
            }
        });
        builder.setNegativeButton("稍後再說", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                overridePendingTransition(0, R.anim.photo_push_left_out);
            }
        });
        builder.show();
    }
}
