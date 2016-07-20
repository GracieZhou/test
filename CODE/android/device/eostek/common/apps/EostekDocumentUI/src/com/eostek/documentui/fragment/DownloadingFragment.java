
package com.eostek.documentui.fragment;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.documentui.Constants;
import com.eostek.documentui.DocumentApplication;
import com.eostek.documentui.DocumentsActivity;
import com.eostek.documentui.R;
import com.eostek.documentui.data.DataProxy;
import com.eostek.documentui.data.DownloadInfor;
import com.eostek.documentui.model.DownloadGridItemBean;
import com.eostek.documentui.util.Utils;
import com.eostek.documentui.util.ViewHolder;
import com.eostek.documentui.view.DeleteModeDialog;
import com.eostek.documentui.view.MarqueeText;
import com.eostek.documentui.view.ProgressView;
import com.google.common.collect.Lists;

@SuppressLint("ValidFragment")
public class DownloadingFragment extends Fragment {
    private final String TAG = "DownloadingFragment";

    private DocumentsActivity mActivity;

    /**
     * downloading infos
     */
    private int mLastPosition = 0;
    
    private List<DownloadInfor> mDownloadingInfors;

    private TextView mEmptyLayout;

    private GridView mGridView;

    private View mRootView;

    private DownloadingGridViewAdapter mAdapter;

    private int currentIndex = 0;

    private List<DownloadGridItemBean> mItems;

    private DataProxy mDataProxy;

    private Observer mObserver;

    private DownloadingProgressReceiver mDownloadingProgressReceiver;

    private final int REFRESHMSG = 1;

    private final int PAUSEMSG = 2;
    
    private final int DELETEMESSAGE = 3;

    private int mposition = -1;

    private String isDeleteMode = "isDeleteMode";
    

    /**
     * pause to refresh ui
     */
    private boolean isPause = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case PAUSEMSG:
                    isPause = false;
                    break;
                case REFRESHMSG:
                    break;
                case DELETEMESSAGE:
                    View view;
                    if(mGridView.getChildAt(msg.arg1) != null){
                        view = mGridView.getChildAt(msg.arg1);
                        TextView fileName = (TextView) view.findViewById(R.id.file_name);
                        fileName.setText("");
                    }
                    
                default:
                    break;
            }
            getValues();
            setDatasToUI();
        };
    };

    /**
     * @Title: DownloadingFragment.
     * @Description: constructor.
     * @param: @param activity
     * @param: @param mDataProxy.
     * @throws
     */
    public DownloadingFragment(DocumentsActivity activity, DataProxy mDataProxy) {
        this.mActivity = activity;
        this.mDataProxy = mDataProxy;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dload_gridviewlayout, container, false);
        mEmptyLayout = (TextView) mRootView.findViewById(R.id.empty_layout);
        mGridView = (GridView) mRootView.findViewById(R.id.downloading_grid);
        getValues();
        setDatasToUI();
        registListerner();
        mDownloadingProgressReceiver = new DownloadingProgressReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.eostek.documentui.UPDATEPROGRESS");
        mActivity.registerReceiver(mDownloadingProgressReceiver, mIntentFilter);

        return mRootView;
    }

    /**
     * @Title: setDatasToUI.
     * @Description:set datas to adapter and show or hide empty layout.
     * @param: .
     * @return: void.
     * @throws
     */
    private void setDatasToUI() {
        if (mItems != null && mItems.size() > 0) {
            mGridView.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
            mActivity.getHolder().setIndicatorVisibility(View.VISIBLE);
            mActivity.getHolder().setIndicatorText(mItems.size() + "");
        } else {
            mGridView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
            mActivity.getHolder().setIndicatorVisibility(View.INVISIBLE);
        }
        if (mAdapter == null) {
            mAdapter = new DownloadingGridViewAdapter(mActivity, mItems);
            mGridView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
           
        }

    }
    
    

    /**
     * @Title: getValues.
     * @Description: get the values to show the ui.
     * @param: .
     * @return: void.
     * @throws
     */
    private void getValues() {
        this.mDownloadingInfors = mDataProxy.getAllDownload(-1);
        for (Iterator<DownloadInfor> iterator = mDownloadingInfors.iterator(); iterator.hasNext();) {
            DownloadInfor info = iterator.next();
            // delete the item which has dowloaded
            if (info.downloadState == DataProxy.STATUS_SUCCESS) {
                iterator.remove();
            }
        }

        cpToItemBean();

        if (Constants.isDebug) {
            if (mItems != null && mItems.size() > 0) {
                for (Iterator<DownloadGridItemBean> iterator = mItems.iterator(); iterator.hasNext();) {
                    DownloadGridItemBean type = (DownloadGridItemBean) iterator.next();
                    Log.i(TAG, "item===" + type.toString() + "\n");
                }
            }
        }
    }

    /**
     * @Title: registListerner.
     * @Description: register the listerner.
     * @param: .
     * @return: void.
     * @throws
     */
    private void registListerner() {

        if (mObserver == null) {
            mObserver = new Observer(mHandler);
        }
        mActivity.getContentResolver().registerContentObserver(DataProxy.BASE_URL, true, mObserver);
        mGridView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                currentIndex = mGridView.getSelectedItemPosition();
                mposition = currentIndex;
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.e(TAG, "event.getAction() == KeyEvent.ACTION_DOWN-------> : " + mposition);

                    if ((currentIndex == 0 || currentIndex == 1 || currentIndex == 2 || currentIndex == 3
                            || currentIndex == 4 || currentIndex == 5)
                            && keyCode == KeyEvent.KEYCODE_DPAD_UP) {

                        mActivity.getHolder().reuqestFocus(Constants.DownloadingFragmentIndex);
                        return true;
                    }
                    if ((currentIndex == mItems.size() - 1) && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    }
                    if (mposition != -1 && (keyCode == KeyEvent.KEYCODE_MENU)) {
                        Intent intent = new Intent(Constants.MENUACTION);
                        Bundle bundle = new Bundle();
                        bundle.putString("menuMode", "isDownloadingMode");
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
                        mActivity.overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                        return true;
                    }
                    //if(currentIndex % 6 == 0),why not use this way？
                    if ((currentIndex == 0 || currentIndex == 6 || currentIndex == 12 || currentIndex == 18
                            || currentIndex == 24 || currentIndex == 30 || currentIndex == 36 || currentIndex == 42)
                            && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                return false;
            }
        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                mAdapter.setIndex(position);
                mAdapter.notifyDataSetChanged();
                DownloadGridItemBean bean = mItems.get(position);
                final DownloadInfor downloadInfo = bean.getDownloadInfor();
                switch (bean.getDownloadState()) {
                    case DataProxy.STATUS_RUNNING:
                        // change running to pause
                        downloadInfo.downloadState = DataProxy.STATUS_PAUSED_BY_APP;
                        downloadInfo.controlRun = DataProxy.CONTROL_PAUSED;
                        bean.setStatus(Constants.PAUSEFLAG);
                        break;
                    case DataProxy.STATUS_PAUSED_BY_APP:
                    case DataProxy.STATUS_WAITING_FOR_NETWORK:
                    case DataProxy.STATUS_WAITING_TO_RETRY:
                    case DataProxy.STATUS_QUEUED_FOR_WIFI:
                    case DataProxy.STATUS_FAILED:
                    case DataProxy.STATUS_PENDING:
                        // to running
                        downloadInfo.downloadState = DataProxy.STATUS_RUNNING;
                        downloadInfo.controlRun = DataProxy.CONTROL_RUN;
                        bean.setStatus(Constants.CONTINUEFLAG);
                        break;
                    default:
                        // to running
                        downloadInfo.downloadState = DataProxy.STATUS_RUNNING;
                        downloadInfo.controlRun = DataProxy.CONTROL_RUN;
                        bean.setStatus(Constants.CONTINUEFLAG);
                        break;
                }
                mAdapter.notifyDataSetChanged();
                isPause = true;
                mHandler.removeMessages(PAUSEMSG);
                mHandler.sendEmptyMessageDelayed(PAUSEMSG, 1500);
                DocumentApplication.getInstance().addTask(new Runnable() {

                    @Override
                    public void run() {
                        // delete the task
                        mDataProxy.update(downloadInfo);
                    }
                });
            }
        });
        mGridView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                View lineView = mActivity.findViewById(R.id.line2);
                TextView textView = (TextView)v.findViewById(R.id.file_name);
                if (hasFocus) {
                    //textView.setSelected(true);
                    if (mItems != null && mItems.size() > 0) {
                        currentIndex = mGridView.getSelectedItemPosition();
                        mActivity.getHolder().setMenuTipsVisibility(View.VISIBLE);
                        lineView.setVisibility(View.VISIBLE);
                        mAdapter.setIndex(currentIndex);
                        mAdapter.notifyDataSetChanged();
                        mLastPosition = currentIndex;
                    }
                    Log.e(TAG, "come in ------------------->hasFocus onFocusChange");
                    
                } else {
                    textView.setSelected(false);
                    lineView.setVisibility(View.INVISIBLE);
                    // clear last position value
                    try {
                        @SuppressWarnings("unchecked")
                        Class<GridView> c = (Class<GridView>) Class.forName("android.widget.GridView");
                        Method[] flds = c.getDeclaredMethods();
                        for (Method f : flds) {
                            if ("setSelectionInt".equals(f.getName())) {
                                f.setAccessible(true);
                                f.invoke(v, new Object[] {
                                    Integer.valueOf(-1)
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mActivity.getHolder().setMenuTipsVisibility(View.GONE);
                   

                }

            }
        });

        mGridView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mposition = position;
                mLastPosition = position;
                mAdapter.setIndex(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mposition = -1;
            }
        });

    }

    /*@Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        int gridviewItemNumbers = mGridView.getCount();
        int downloadNumber = mDataProxy.getSetting().downloadNumber;
        Log.e("onresume", "downloadNumber====>" + downloadNumber);
        int downloadingBeansNumbers = 0;
        for (DownloadGridItemBean mbean : mItems) {
            if (mbean.getStatus() == Constants.DOWNLOADINGFLAG) {
                downloadingBeansNumbers++;
            }
        }
        Log.e("onresume", "downloadingBeansNumbers====>" + downloadingBeansNumbers);
        if (downloadingBeansNumbers > downloadNumber) {
            for(DownloadGridItemBean mbean : mItems){
                try {
                    if (mbean.getDownloadState() == DataProxy.STATUS_PAUSED_BY_APP) {
                        return;
                    }
                    final DownloadInfor downloadInfo = mbean.getDownloadInfor();
                    downloadInfo.downloadState = DataProxy.STATUS_PAUSED_BY_APP;
                    downloadInfo.controlRun = DataProxy.CONTROL_PAUSED;
                    mbean.setStatus(Constants.PAUSEFLAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
            for (int position = 0; position < downloadNumber-1; position++) {
                if (mGridView.getChildAt(position) != null) {
                    changeStatus(position, Constants.DOWNLOADINGFLAG);
                }
            }
        }

    }*/
    // changeStatus
    public void changeStatus(int position, int flag) {
        Log.i(TAG, "===changeStatus()===>");

        DownloadGridItemBean bean = mItems.get(position);
        final DownloadInfor downloadInfo = bean.getDownloadInfor();

        if (flag == Constants.DOWNLOADINGFLAG) {
            if (bean.getDownloadState() == DataProxy.STATUS_RUNNING) {
                return;
            }
            downloadInfo.downloadState = DataProxy.STATUS_RUNNING;
            downloadInfo.controlRun = DataProxy.CONTROL_RUN;
            bean.setStatus(Constants.DOWNLOADINGFLAG);
        }
        if (flag == Constants.PAUSEFLAG) {
            if (bean.getDownloadState() == DataProxy.STATUS_PAUSED_BY_APP) {
                return;
            }
            downloadInfo.downloadState = DataProxy.STATUS_PAUSED_BY_APP;
            downloadInfo.controlRun = DataProxy.CONTROL_PAUSED;
            bean.setStatus(Constants.PAUSEFLAG);
        }
        if(flag == Constants.WAITINGFLAG){
            if (bean.getDownloadState() == DataProxy.STATUS_PENDING) {
                return;
            }
            downloadInfo.downloadState = DataProxy.STATUS_RUNNING;
            downloadInfo.controlRun = DataProxy.CONTROL_RUN;
            bean.setStatus(Constants.CONTINUEFLAG);
            //downloadInfo.downloadState = DataProxy.STATUS_PENDING;
            //downloadInfo.controlRun = DataProxy.CONTROL_PAUSED;
            //bean.setStatus(Constants.WAITINGFLAG);
        }

        mAdapter.notifyDataSetChanged();
        DocumentApplication.getInstance().addTask(new Runnable() {

            @Override
            public void run() {
                // delete the task
                mDataProxy.update(downloadInfo);
            }
        });
    }

    /**
     * deleteSelectedItemConfirm
     */
    private void deleteSelectedItemConfirm() {
        Log.i("tag", "===deleteSelectedItemConfirm()===>");
        if (mposition == -1) {
            return;
        }
        DownloadGridItemBean bean = mItems.get(mposition);
        Intent intent = new Intent(mActivity, DeleteModeDialog.class);
        Bundle bundle = new Bundle();
        bundle.putString("flag", isDeleteMode);
        bundle.putSerializable("bean", bean);
        intent.putExtras(bundle);
        startActivityForResult(intent, 4);
    }

    /**
     * deleteSelectedItemOK
     */
    private void deleteSelectedItemOk() {
        Log.i("tag", "===deleteSelectedItemOk===>");
        final DownloadInfor info = mItems.get(mposition).getDownloadInfor();
        mItems.remove(mposition);
        mAdapter.notifyDataSetChanged();
        mActivity.getHolder().setIndicatorText(mItems.size() + "");
        DocumentApplication.getInstance().addTask(new Runnable() {

            @Override
            public void run() {
                // delete the task
                mDataProxy.deleteDownload(info);
            }
        });
        /*Message msg = mHandler.obtainMessage();
        msg.what = DELETEMESSAGE;
        msg.arg1 = mposition;
        mHandler.sendMessageDelayed(msg, 200);*/
    }

    /**
     * startAll
     */
    public void startAll() {
        Log.i("tag", "===startAll()===>");

        int downloadNumber = mDataProxy.getSetting().downloadNumber;
        if (mItems.size() > downloadNumber) {
            Toast.makeText(
                    getActivity(),
                    getActivity().getResources().getString(R.string.limit_downloading_nobur_text_before)
                            + downloadNumber
                            + getActivity().getResources().getString(R.string.limit_downloading_nobur_text_after), 1000)
                    .show();
            for (int i = 0; i < downloadNumber; i++) {
                changeStatus(i, Constants.DOWNLOADINGFLAG);
            }
            for (int waitnum = downloadNumber;waitnum < mItems.size();waitnum ++){
                changeStatus(waitnum,Constants.WAITINGFLAG);
            }
        } else {
            for (int i = 0; i < mItems.size(); i++) {
                changeStatus(i, Constants.DOWNLOADINGFLAG);
            }
        }
    }

    /**
     * pauseAll
     */
    public void pauseAll() {
        Log.i("tag", "===pauseAll()===>");

        for (int i = 0; i < mItems.size(); i++) {
            changeStatus(i, Constants.PAUSEFLAG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // menu_result
        if (requestCode == 1) {
            if (resultCode == DocumentsActivity.RESULT_OK) {
                deleteSelectedItemConfirm();
            }
            if (resultCode == DocumentsActivity.RESULT_CANCELED) {
                pauseAll();
            }
            if (resultCode == DocumentsActivity.RESULT_FIRST_USER) {
                startAll();
            }
            if (resultCode == Constants.NOTHINGFINISH){
                return;
            }
        }
        // dialog_result
        if (requestCode == 4 && resultCode == DocumentsActivity.RESULT_OK) {
            deleteSelectedItemOk();
        }
    }

    /**
     * add a new object{@link #isDeleteMode} to the bean in order to manage the
     * delete mode
     */
    private void cpToItemBean() {
        if (mItems == null) {
            mItems = Lists.newArrayList();
        }
        mItems.clear();
        if (mDownloadingInfors != null && mDownloadingInfors.size() > 0) {
            for (int i = 0; i < mDownloadingInfors.size(); i++) {
                DownloadGridItemBean item = new DownloadGridItemBean(mDownloadingInfors.get(i));
                mItems.add(item);
            }
        }
        Collections.sort(mItems);
    }

    private class Observer extends ContentObserver {

        public Observer(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (Constants.isDebug) {
                Log.i(TAG, "ContentObserver-----onChange-----");
            }
            if (!isPause) {
                mHandler.sendEmptyMessage(REFRESHMSG);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.getContentResolver().unregisterContentObserver(mObserver);
        mActivity.unregisterReceiver(mDownloadingProgressReceiver);
    }

    private class DownloadingProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "receive broadcast---------------->");
            // TODO Auto-generated method stub
            int position = intent.getIntExtra("itemPosition", 0);
            int downloadingProgress = intent.getIntExtra("downloadingProgress", 0);
            View viewItem = mGridView.getChildAt(position);
            if (viewItem != null) {
                ProgressView pView = (ProgressView) viewItem.findViewById(R.id.downloading_progress);
                pView.setProgress(downloadingProgress);
            }
        }

    }

    private class DownloadingGridViewAdapter extends BaseAdapter {
        private Context mContext;

        private List<DownloadGridItemBean> gridItemList;
        
        private int index = 0;  

        public DownloadingGridViewAdapter(Context context, List<DownloadGridItemBean> items) {
            this.mContext = context;
            this.gridItemList = items;
        }
        
        public void setIndex(int selected) {  
            index = selected;  
        }  

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return gridItemList != null ? gridItemList.size() : 0;
        }

        @Override
        public DownloadGridItemBean getItem(int position) {
            return gridItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.dload_gridviewitem, null);
            }

            ImageView itemfocusimg = ViewHolder.get(convertView, R.id.item_focus_img);
            TextView downloadSpeed = ViewHolder.get(convertView, R.id.file_size);
            ImageView fileTypeImg = ViewHolder.get(convertView, R.id.file_type_image);
            TextView fileName = ViewHolder.get(convertView, R.id.file_name);
            ImageView downloadingstatusimg = ViewHolder.get(convertView, R.id.downloading_status_img);
            ProgressView downloadingProgress = ViewHolder.get(convertView, R.id.downloading_progress);
            
            /*if (index == position) {
                fileName.setSelected(true);
            }else{
                fileName.setSelected(false);
            }*/
            
            DownloadGridItemBean bean = getItem(position);
            String mimeType = bean.getMimetype();

            if (mimeType == null) {
                fileTypeImg.setBackgroundResource(R.drawable.other_default_icon);
            } else {
                Log.i("tag", "===mimeType===>" + mimeType);
                if (mimeType.contains("text")) {
                    fileTypeImg.setBackgroundResource(R.drawable.document_default_icon);
                } else if (mimeType.contains("video")) {
                    fileTypeImg.setBackgroundResource(R.drawable.video_default_icon);
                } else if (mimeType.contains("image")) {
                    fileTypeImg.setBackgroundResource(R.drawable.image_default_icon);
                } else if (mimeType.contains("music")) {
                    fileTypeImg.setBackgroundResource(R.drawable.music_default_icon);
                } else if (mimeType.contains("application")) {
                    fileTypeImg.setBackgroundResource(R.drawable.apk_default_icon);
                } else {
                    fileTypeImg.setBackgroundResource(R.drawable.other_default_icon);
                }
            }
            if(!(fileName.getText().toString().equals(Utils.getFileName(getItem(position).getFullURL()))||
                    fileName.getText().toString().equals(getItem(position).getSaveName()))){
                if (TextUtils.isEmpty(bean.getSaveName())) {
                    fileName.setText(Utils.getFileName(getItem(position).getFullURL()));
                } else {
                    fileName.setText(getItem(position).getSaveName());
                }
                
            }
            downloadSpeed.setText(((bean.getSpeed()) / 1000) + "KB/S");
            if (bean.getFileSize() <= 0) {
                downloadingProgress.setProgress(0);
            } else {
                float pro = ((float) bean.getCurrentBytes()) / bean.getFileSize();
                int progress = (int) (100 * pro);
                downloadingProgress.setProgress(progress);
                /*Intent intent = new Intent();
                intent.setAction("com.eostek.documentui.UPDATEPROGRESS");
                intent.putExtra("itemPosition", position);
                intent.putExtra("downloadingProgress", progress);
                mContext.sendBroadcast(intent);*/
            }
            switch (bean.getStatus()) {
                case Constants.PAUSEFLAG:
                    downloadingstatusimg.setVisibility(View.VISIBLE);
                    downloadingstatusimg.setBackgroundResource(R.drawable.continuedload);
                    downloadSpeed.setVisibility(View.GONE);
                    break;
                case Constants.CONTINUEFLAG:
                    downloadingstatusimg.setVisibility(View.VISIBLE);
                    downloadingstatusimg.setBackgroundResource(R.drawable.waiting);
                    downloadSpeed.setVisibility(View.GONE);
                    break;
                case Constants.WAITINGFLAG:
                    downloadingstatusimg.setVisibility(View.VISIBLE);
                    downloadingstatusimg.setBackgroundResource(R.drawable.waiting);
                    downloadSpeed.setVisibility(View.GONE);
                    break;
                case Constants.DELETEFLAG:
                    downloadingstatusimg.setVisibility(View.VISIBLE);
                    downloadingstatusimg.setBackgroundResource(R.drawable.failure);
                    downloadSpeed.setVisibility(View.GONE);
                    break;
                case Constants.FAILFLAG:
                    downloadingstatusimg.setVisibility(View.VISIBLE);
                    downloadingstatusimg.setBackgroundResource(R.drawable.failure);
                    downloadSpeed.setVisibility(View.GONE);
                    break;
                case Constants.DOWNLOADINGFLAG:
                    downloadingstatusimg.setVisibility(View.VISIBLE);
                    downloadingstatusimg.setBackgroundResource(R.drawable.downloading);
                    downloadSpeed.setVisibility(View.VISIBLE);
                    break;
                default:
                    bean.setStatus(Constants.DOWNLOADINGFLAG);
                    downloadingstatusimg.setVisibility(View.VISIBLE);
                    downloadingstatusimg.setBackgroundResource(R.drawable.downloading);
                    downloadSpeed.setVisibility(View.VISIBLE);
                    break;
            }

            return convertView;
        }

        // setGridViewItemListener end

    }

}

