package com.eostek.scifly.browser.settool;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.BrowserContract.Combined;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.browser.BrowserHistoryPage.ClearHistoryTask;
import com.android.browser.BrowserSettings;
import com.android.browser.DataController;
import com.android.browser.DataController.OnQueryHistory;
import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.BrowserApplication;
import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.business.QRCodeHelper;
import com.eostek.scifly.browser.business.WebViewHelper;
import com.eostek.scifly.browser.modle.UrlModle;
import com.eostek.scifly.browser.util.Constants;

public class SetToolLogic {

    private final String TAG = "SetToolLogic";

    private BrowserActivity mActivity;

    private SetToolHolder mHolder;

    private ClearAdapter mClearAdapter;

    private String[] mClearStr;

    private static final int SHOW_CLEAR_TOAST = 0;

    private static final int SHOW_WHITE_ARROW = 1;
    
    public static final int HISTORY_TODAY = 2;
    
    public static final int HISTORY_YESTERDAY = 3;
    
    public static final int HISTORY_EARLY = 4;

    public static final int INIT_HISTORY_UI = 5;

    public static final int SHOW_QR_CODE = 6;

    public static final int UPDATE_ADAPTER = 7;

    public static interface HistoryQuery {
      static final String[] PROJECTION = new String[] {
              Combined._ID, // 0
              Combined.DATE_LAST_VISITED, // 1
              Combined.TITLE, // 2
              Combined.URL, // 3
              Combined.FAVICON, // 4
              Combined.VISITS, // 5
              Combined.IS_BOOKMARK, // 6
      };

      static final int INDEX_ID = 0;
      static final int INDEX_DATE_LAST_VISITED = 1;
      static final int INDEX_TITE = 2;
      static final int INDEX_URL = 3;
      static final int INDEX_FAVICON = 4;
      static final int INDEX_VISITS = 5;
      static final int INDEX_IS_BOOKMARK = 6;
  }

    public static boolean isSetToolPage;

    public static boolean isAdvanceSettingPage;

    public static boolean isClearChoicePage;

    public static boolean isHistoryPage;

    private TodayHistoryItemClickListener mTodayHistoryItemClickListener;

    private YesterdayHistoryItemClickListener mYesterdayHistoryItemClickListener;

    private EarlyHistoryItemClickListener mEarlyHistoryItemClickListener;

    private HistoryItemSelectedListener mHistoryItemSelectedListener;

    private HistoryFocusChangeListener mHistoryFocusChangeListener;

    HistoryAdapter mTodayAadapter;

    HistoryAdapter mYesterdayAadapter;

    HistoryAdapter mEarlyAadapter;

    private AlertDialog mDialog;

    private ArrayList<UrlModle> mTodayList = new ArrayList<UrlModle>();

    private ArrayList<UrlModle> mYesterdayList = new ArrayList<UrlModle>();

    private ArrayList<UrlModle> mEarlyList = new ArrayList<UrlModle>();

    private long mTodayMorningTime;

    private long mTodayNightTime;

    private long mYestMorningTime;

    private DataController mDataController;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_CLEAR_TOAST:
                    finishClearing();
                    break;

                case SHOW_WHITE_ARROW:
                    mHolder.mRightArrowImg.setBackgroundResource(R.drawable.arrow_right_white);
                    break;
                    
                case INIT_HISTORY_UI:
                    if (mTodayAadapter == null) {
                        mTodayAadapter = new HistoryAdapter(mActivity, mTodayList);
                        mHolder.mTodayListView.setAdapter(mTodayAadapter);
                    }
                    if (mYesterdayAadapter == null) {
                        mYesterdayAadapter = new HistoryAdapter(mActivity, mYesterdayList);
                        mHolder.mYesteedayListView.setAdapter(mYesterdayAadapter);
                    }
                    if (mEarlyAadapter == null) {
                        mEarlyAadapter = new HistoryAdapter(mActivity, mEarlyList);
                        mHolder.mEarlyListView.setAdapter(mEarlyAadapter);
                    }

                    invalidate();
                    break;
                case SHOW_QR_CODE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    mHolder.setQRCodeImg(bitmap);
                    break;
                case UPDATE_ADAPTER:
                    invalidate();
                    break;
            }
        }

    };


    public SetToolLogic(BrowserActivity activity) {
        mActivity = activity;
    }

    public void setHolder(SetToolHolder holder) {
        mHolder = holder;
    }

    public void initData() {
        
        if ("heran".equals(Build.DEVICE)){
            mHolder.setQRCodeTxt();
        }

        isSetToolPage = true;

        showQRcode();

        if (BrowserSettings.getInstance(mActivity).isAutofillEnabled()) {
            mHolder.mListSaveSwitchImg.setBackgroundResource(R.drawable.check_on);
        } else {
            mHolder.mListSaveSwitchImg.setBackgroundResource(R.drawable.check_off);
        }

        mClearStr = mActivity.getResources().getStringArray(R.array.clear_item_vals);
        mClearAdapter = new ClearAdapter(mClearStr, mActivity);
        mHolder.mClearListView.setAdapter(mClearAdapter);

        initHistoryData();
    }

    private void setListViewHeight(ListView listView, int count) {
        HistoryAdapter listAdapter = (HistoryAdapter) listView.getAdapter();
        if (listAdapter == null || listView.getAdapter().getCount() <= 0) {
            return;
        }

        View listItem = listView.getAdapter().getView(0, null, listView);
        listItem.measure(0, 0);
        int listItemHeight = listItem.getMeasuredHeight() + listView.getDividerHeight();
        Log.d(TAG, "" + listItemHeight);
        ViewGroup.LayoutParams paramss = listView.getLayoutParams();
        paramss.height = listItemHeight * count + 30;
        listView.setLayoutParams(paramss);
    }

    private void finishClearing() {
        Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.clear_completed_tip), Toast.LENGTH_SHORT).show();
    }

    public void setListener() {

        mHolder.mQrcodeRl.setOnFocusChangeListener(mOnFocusChangeListener);
        mHolder.mHistoryRl.setOnFocusChangeListener(mOnFocusChangeListener);
        mHolder.mClearRl.setOnFocusChangeListener(mOnFocusChangeListener);
        mHolder.mAdvancedSettingRl.setOnFocusChangeListener(mOnFocusChangeListener);

        mHolder.mHistoryRl.setOnClickListener(mOnClickListener);
        mHolder.mClearRl.setOnClickListener(mOnClickListener);
        mHolder.mAdvancedSettingRl.setOnClickListener(mOnClickListener);
        mHolder.mListSaveLl.setOnClickListener(mOnClickListener);
        mHolder.mClearChoiceLl.setOnClickListener(mOnClickListener);

        mHolder.mClearListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeView(position);
            }

        });

        //history page.
        if (mTodayHistoryItemClickListener == null) {
            mTodayHistoryItemClickListener = new TodayHistoryItemClickListener();
        }
        if (mYesterdayHistoryItemClickListener == null) {
            mYesterdayHistoryItemClickListener = new YesterdayHistoryItemClickListener();
        }
        if (mEarlyHistoryItemClickListener == null) {
            mEarlyHistoryItemClickListener = new EarlyHistoryItemClickListener();
        }
        mHolder.mTodayListView.setOnItemClickListener(mTodayHistoryItemClickListener);
        mHolder.mYesteedayListView.setOnItemClickListener(mYesterdayHistoryItemClickListener);
        mHolder.mEarlyListView.setOnItemClickListener(mEarlyHistoryItemClickListener);

        if (mHistoryItemSelectedListener == null) {
            mHistoryItemSelectedListener = new HistoryItemSelectedListener();
        }
        mHolder.mTodayListView.setOnItemSelectedListener(mHistoryItemSelectedListener);
        mHolder.mYesteedayListView.setOnItemSelectedListener(mHistoryItemSelectedListener);
        mHolder.mEarlyListView.setOnItemSelectedListener(mHistoryItemSelectedListener);

        if (mHistoryFocusChangeListener == null) {
            mHistoryFocusChangeListener = new HistoryFocusChangeListener();
        }
        mHolder.mTodayListView.setOnFocusChangeListener(mHistoryFocusChangeListener);
        mHolder.mYesteedayListView.setOnFocusChangeListener(mHistoryFocusChangeListener);
        mHolder.mEarlyListView.setOnFocusChangeListener(mHistoryFocusChangeListener);
    }
    
    public void showClearContent() {
        mHolder.mSettingLl.setVisibility(View.INVISIBLE);
        mHolder.mAdvancedSettingContentRl.setVisibility(View.INVISIBLE);
        mHolder.mClearContentRl.setVisibility(View.VISIBLE);
        mHolder.mHistoryContentLayout.setVisibility(View.INVISIBLE);
        isSetToolPage = false;
        isClearChoicePage = true;
        isAdvanceSettingPage = false;
        isHistoryPage = false;
        mHolder.mClearListView.requestFocus();
    }
    
    public void showAdvancedContent() {
        mActivity.mHolder.showSetToolView(false);
        mHolder.mSettingLl.setVisibility(View.INVISIBLE);
        mHolder.mClearContentRl.setVisibility(View.INVISIBLE);
        mHolder.mAdvancedSettingContentRl.setVisibility(View.VISIBLE);
        mHolder.mHistoryContentLayout.setVisibility(View.INVISIBLE);
        isSetToolPage = false;
        isClearChoicePage = false;
        isAdvanceSettingPage = true;
        isHistoryPage = false;
        mHolder.mListSaveLl.requestFocus();
    }
    
    public void showSetToolMainPage() {
        mHolder.mSettingLl.setVisibility(View.VISIBLE);
        mHolder.mClearContentRl.setVisibility(View.INVISIBLE);
        mHolder.mAdvancedSettingContentRl.setVisibility(View.INVISIBLE);
        mHolder.mHistoryContentLayout.setVisibility(View.INVISIBLE);
        isSetToolPage = true;
        isClearChoicePage = false;
        isAdvanceSettingPage = false;
        isHistoryPage = false;
    }
    
    public void showHistory() {
        mActivity.mHolder.showSetToolView(true);
        initHistoryData();

        Log.d(TAG, "mHolder=" + mHolder);
        if (!mHolder.isInitViewCompleted()) {
            SetToolFragment setToolFragment = mActivity.mHolder.getSetToolFragment();
            setToolFragment.mHandler.removeMessages(setToolFragment.MSG_SHOW_HISTORY);
            setToolFragment.mHandler.sendEmptyMessageDelayed(setToolFragment.MSG_SHOW_HISTORY, 200);
        } else {
            mHolder.mSettingLl.setVisibility(View.INVISIBLE);
            mHolder.mClearContentRl.setVisibility(View.INVISIBLE);
            mHolder.mAdvancedSettingContentRl.setVisibility(View.INVISIBLE);
            mHolder.mHistoryContentLayout.setVisibility(View.VISIBLE);
            mHolder.mTodayListView.requestFocus();
            isSetToolPage = false;
            isClearChoicePage = false;
            isAdvanceSettingPage = false;
            isHistoryPage = true;
        }
    }
    
    private void changeView(int position) {
        BrowserSettings settings = BrowserSettings.getInstance(mActivity);
        switch (position) {
            case 0:
                if (settings.isClearHistory()) {
                    settings.setClearHistory(false);
                } else {
                    settings.setClearHistory(true);
                }

                break;
            case 1:
                if (settings.isClearCache()) {
                    settings.setClearCache(false);
                } else {
                    settings.setClearCache(true);
                }
                break;
            case 2:
                if (settings.isClearFormData()) {
                    settings.setClearFormData(false);
                } else {
                    settings.setClearFormData(true);
                }
                break;
            case 3:
                if (settings.isClearPassword()) {
                    settings.setClearPassword(false);
                } else {
                    settings.setClearPassword(true);
                }
                break;
            case 4:
                if (settings.isClearCookie()) {
                    settings.setClearCookie(false);
                } else {
                    settings.setClearCookie(true);
                }
                break;
            case 5:
                if (settings.isCancleLocatPermission()) {
                    settings.setCancleLocatPermission(false);
                } else {
                    settings.setCancleLocatPermission(true);
                }
                break;

            default:
                break;
        }
        // change view
        mClearAdapter.notifyDataSetChanged();
    }

    public void showDialog() {
        if (mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(R.string.tip_clean_all_history);
            builder.setPositiveButton(R.string.clean, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final ContentResolver resolver = mActivity.getContentResolver();
                    final ClearHistoryTask clear = new ClearHistoryTask(resolver);
                    clear.start();
                    mTodayList.clear();
                    mYesterdayList.clear();
                    mEarlyList.clear();
                    mHandler.obtainMessage(UPDATE_ADAPTER).sendToTarget();;
                }

            });
            builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDialog.dismiss();
                }
            });
            mDialog = builder.create();
        }
        mDialog.show();
    }

    OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean hasfocus) {
            if (hasfocus) {
                switch (view.getId()) {
                    case R.id.qrcode_rl: {
                        mHolder.mQrcodeFocusImg.setVisibility(view.VISIBLE);
                    }
                        break;
                    case R.id.history_rl: {
                        mHolder.mHistoryFocusImg.setVisibility(view.VISIBLE);
                    }
                        break;
                    case R.id.clear_rl: {
                        mHolder.mClearFocusImg.setVisibility(view.VISIBLE);
                    }
                        break;
                    case R.id.advaced_seetting_rl: {
                        mHolder.mAdvanedFocusImg.setVisibility(view.VISIBLE);
                    }
                        break;
                }
            } else {
                switch (view.getId()) {
                    case R.id.qrcode_rl: {
                        mHolder.mQrcodeFocusImg.setVisibility(view.INVISIBLE);
                    }
                        break;
                    case R.id.history_rl: {
                        mHolder.mHistoryFocusImg.setVisibility(view.INVISIBLE);
                    }
                        break;
                    case R.id.clear_rl: {
                        mHolder.mClearFocusImg.setVisibility(view.INVISIBLE);
                    }
                        break;
                    case R.id.advaced_seetting_rl: {
                        mHolder.mAdvanedFocusImg.setVisibility(view.INVISIBLE);
                    }
                        break;
                }
            }

        }

    };

    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.history_rl: {
                    showHistory();
                }
                    break;
                case R.id.clear_rl: {
                    clearData();
                }
                    break;
                case R.id.advaced_seetting_rl: {
                    showAdvancedContent();
                }
                    break;
                case R.id.list_save_ll: {
                    if (BrowserSettings.getInstance(mActivity).isAutofillEnabled()) {
                        mHolder.mListSaveSwitchImg.setBackgroundResource(R.drawable.check_off);
                        BrowserSettings.getInstance(mActivity).setAutofillEnabled(false);
                    } else {
                        mHolder.mListSaveSwitchImg.setBackgroundResource(R.drawable.check_on);
                        BrowserSettings.getInstance(mActivity).setAutofillEnabled(true);
                    }
                }
                    break;
                case R.id.clear_choice_ll: {
                    mHolder.mRightArrowImg.setBackgroundResource(R.drawable.arrow_right_green);
                    mHandler.sendEmptyMessageDelayed(SHOW_WHITE_ARROW, 500);
                    showClearContent();
                }
                    break;
            }

        }

    };

    class TodayHistoryItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            TextView textView = (TextView) view.findViewById(R.id.open_or_remove);
            if (textView.getText().equals(mActivity.getResources().getString(R.string.open_history))) {
                mActivity.mLogic.showWeb();
                WebViewHelper.getInstance(mActivity).loadUrlFromContext(mTodayList.get(position).mUrl);
            } else {
                DataController.getInstance(mActivity).deleteHistoryByUrl(mTodayList.get(position).mUrl);
                mTodayList.remove(position);
                mHandler.obtainMessage(UPDATE_ADAPTER).sendToTarget();;
            }
        }
    }
    
    class YesterdayHistoryItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            TextView textView = (TextView) view.findViewById(R.id.open_or_remove);
            if (textView.getText().equals(mActivity.getResources().getString(R.string.open_history))) {
                mActivity.mLogic.showWeb();
                WebViewHelper.getInstance(mActivity).loadUrlFromContext(mYesterdayList.get(position).mUrl);
            } else {
                DataController.getInstance(mActivity).deleteHistoryByUrl(mYesterdayList.get(position).mUrl);
                mYesterdayList.remove(position);
                mHandler.obtainMessage(UPDATE_ADAPTER).sendToTarget();;
            }
        }
    }
    
    class EarlyHistoryItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            TextView textView = (TextView) view.findViewById(R.id.open_or_remove);
            if (textView.getText().equals(mActivity.getResources().getString(R.string.open_history))) {
                mActivity.mLogic.showWeb();
                WebViewHelper.getInstance(mActivity).loadUrlFromContext(mEarlyList.get(position).mUrl);
            } else {
                DataController.getInstance(mActivity).deleteHistoryByUrl(mEarlyList.get(position).mUrl);
                mEarlyList.remove(position);
                mHandler.obtainMessage(UPDATE_ADAPTER).sendToTarget();
            }
        }
    }

    class HistoryItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View view, int positon, long id) {
            if (mHolder.mLastFocusItemOperate != null) {
                mHolder.mLastFocusItemOperate.setVisibility(View.INVISIBLE);
            }
            mHolder.getCurrentFocusItem();
            if (mHolder.mCurrentFocusItem != null) {
                LinearLayout layout = (LinearLayout) mHolder.mCurrentFocusItem.findViewById(R.id.operate);
                ListView listView = (ListView) mHolder.mCurrentFocusItem.getParent();
                if (listView.isFocused()) {
                    layout.setVisibility(View.VISIBLE);
                    mHolder.mLastFocusItemOperate = layout;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    class HistoryFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            try {
                if (mHolder.mLastFocusItemOperate != null) {
                    mHolder.mLastFocusItemOperate.setVisibility(View.INVISIBLE);
                }
                mHolder.getCurrentFocusItem();
                LinearLayout layout = (LinearLayout) mHolder.mCurrentFocusItem.findViewById(R.id.operate);
                ListView listView = (ListView) mHolder.mCurrentFocusItem.getParent();
                if (listView.isFocused()) {
                    layout.setVisibility(View.VISIBLE);
                    mHolder.mLastFocusItemOperate = layout;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initHistoryData() {
        mDataController = DataController.getInstance(mActivity);
        mDataController.queryHistory(new OnQueryHistory() {
            
            @Override
            public void onQueryHistory(Cursor cursor) {
                if (cursor == null) {
                    return;
                }
                mTodayList.clear();
                mYesterdayList.clear();
                mEarlyList.clear();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(HistoryQuery.PROJECTION[HistoryQuery.INDEX_ID]));
                    String url = cursor.getString(cursor.getColumnIndex(HistoryQuery.PROJECTION[HistoryQuery.INDEX_URL]));
                    String title = cursor.getString(cursor.getColumnIndex(HistoryQuery.PROJECTION[HistoryQuery.INDEX_TITE]));
                    long vistedTime = cursor.getLong(cursor.getColumnIndex(HistoryQuery.PROJECTION[HistoryQuery.INDEX_DATE_LAST_VISITED]));
                    UrlModle urlModle = new UrlModle(id, url, title, vistedTime);
                    switch (getWhichDayByTime(vistedTime)) {
                        case HISTORY_TODAY:
                            mTodayList.add(urlModle);
                            break;
                            
                        case HISTORY_YESTERDAY:
                            mYesterdayList.add(urlModle);
                            break;
                            
                        case HISTORY_EARLY:
                            mEarlyList.add(urlModle);
                            break;
                            
                        default:
                            break;
                    }
                }
                mHandler.obtainMessage(INIT_HISTORY_UI).sendToTarget();
            }
        });
    }

    private int getWhichDayByTime(long time) {
        long now  = System.currentTimeMillis();
        Log.d(TAG, "now=" + now + " , time=" + time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        int whichDay = -1;
        try {
            date = sdf.parse(sdf.format(now));
            mTodayMorningTime = date.getTime();
            long dayTime = 24 * 60 * 60 * 1000;
            mTodayNightTime = mTodayMorningTime + dayTime;
            mYestMorningTime = mTodayMorningTime - dayTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (time > mTodayMorningTime && time <= mTodayNightTime) {
            whichDay = HISTORY_TODAY;
        } else if (time > mYestMorningTime && time <= mTodayMorningTime) {
            whichDay = HISTORY_YESTERDAY;
        } else {
            whichDay = HISTORY_EARLY;
        }
        Log.d(TAG, "whichDay=" + whichDay);
        return whichDay;
    }

    private void invalidate() {
        mTodayAadapter.setDataList(mTodayList);
        mYesterdayAadapter.setDataList(mYesterdayList);
        mEarlyAadapter.setDataList(mEarlyList);

//        mTodayAadapter.notifyDataSetChanged();
//        mYesterdayAadapter.notifyDataSetChanged();
//        mEarlyAadapter.notifyDataSetChanged();

        setListViewHeight(mHolder.mTodayListView, mTodayList.size());
        setListViewHeight(mHolder.mYesteedayListView, mYesterdayList.size());
        setListViewHeight(mHolder.mEarlyListView, mEarlyList.size());
    }

    private void clearData() {
        BrowserSettings settings = BrowserSettings.getInstance(mActivity);
        if (settings.isClearHistory()) {
            settings.clearHistory();
        }
        if (settings.isClearCache()) {
            settings.clearCache();
            settings.clearDatabases();
        }
        if (settings.isClearFormData()) {
            settings.clearFormData();
        }
        if (settings.isClearPassword()) {
            settings.clearPasswords();
            settings.clearCookies();
        }
        if (settings.isClearCookie()) {
            settings.clearCookies();
        }
        if (settings.isCancleLocatPermission()) {
            settings.clearLocationAccess();
        }
        mHandler.sendEmptyMessageDelayed(SHOW_CLEAR_TOAST, 1000);
    }

    private void showQRcode() {
        final File file = new File(Constants.CACHE_PATH, Constants.QR_CODE_NAME);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            mHandler.obtainMessage(SHOW_QR_CODE, bitmap).sendToTarget();
        } else {
            BrowserApplication.getInstance().addThreadTask(new Runnable() {
                
                @Override
                public void run() {
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        mHandler.obtainMessage(SHOW_QR_CODE, bitmap).sendToTarget();
                    } else {
                        Bitmap bitmap = QRCodeHelper.getInstance(mActivity).getQRCodeBitmap();
                        if (bitmap != null) {
                            mHandler.obtainMessage(SHOW_QR_CODE, bitmap).sendToTarget();
                        }
                    }
                }
            });
        }
    }
}
