
package com.android.packageinstaller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PakageInstallerHolder {

    private PackageInstallerActivity mContext;

    private PakageInstallerLogic mLogic;
    
    private String TAG = "PakageInstallerHolder";

    List<PackageModel> mApkList;

    List<PackageModel> mSlctedApkList;
    
    private static final int MAX_NUM = 5;

    public static final int CATEGORY_SELECT = 1;

    public static final int CATEGORY_INSTALLING = 2;
    
    public static final int CATEGORY_INSTALL_COMPLETED = 6;
    
    public static final int CATEGORY_SINGLE_INSTALL_CONFIRM = 7;
    
    public static final int CATEGORY_SINGLE_INSTALLING = 8;
    
    public static final int CATEGORY_SINGLE_INSTALL_COMPLETED = 9;
    
    public static final int CATEGORY_BLOCK_APK = 10;

    public static final int BATCH_INSTALL = 3;

    public static final int SINGLE_INSTALL = 4;

    public static final int UNINSTALL = 5;
    
    public static final int BLOCK_STALL=6;
    
    public static boolean mRefreshNum = false;
    
    public static boolean isFirst = true;
    
    private int mHeight = 0;
    
    public boolean isParseThreadStop = false;

    /**
     * 批量安装选择界面CATEGORY_SELECT，正在安装界面CATEGORY_INSTALLING
     */
    int what = CATEGORY_SELECT;

    /**
     * 安装类型：批量安装BATCH_INSTALL，单个安装SINGLE_INSTALL，卸载UNINSTALL
     */
    int mInstallCategory;

    private boolean isSelectAll = true;

    private TextView mApksSlctedNumTv;

    private TextView mAllApksNumTv;

    private TextView mApksSlctedSizeTv;

    private ListView mListView;

    private Button mCfirmInstallBtn;

    private Button mCancleInstallBtn;

    private ImageView mSelectAllView;

    private TextView mApksInstallingNumTv;

    private TextView mApksInstallingSumTv;

    private LinearLayout mInstallingTipLayout;

    private LinearLayout mInstallingCpltedLayout;
    
    private LinearLayout mBackLayout;
    
    private Button mInstallApksCpltedBtn;
    
    private Button mInstallApkCpltedBtn;
    
    private Button mOpenApkBtn;
    
    private TextView mInstalledNumText;
    
    private TextView mInstallSumNumText;
    
    private TextView mInstallApkTitleText;
    
    private TextView mApkNameText;
    
    private LinearLayout mSelectedAllLayout;

    public PakageInstallerHolder(Context context) {
        this.mContext = (PackageInstallerActivity) context;
    }

    public void setmLogic(PakageInstallerLogic mLogic) {
        this.mLogic = mLogic;
    }

    public ListView getmListView() {
        return mListView;
    }

    public Button getConfirm_install_btn() {
        return mCfirmInstallBtn;
    }

    public Button getCancle_install_btn() {
        return mCancleInstallBtn;
    }

    public ImageView getSelect_all() {
        return mSelectAllView;
    }

    public void findViews() {
        
        Log.d(TAG, "--->findViews , install_category : " + mInstallCategory + ", what : " + what);
        
        if (mInstallCategory == BATCH_INSTALL) {
            if (what == CATEGORY_SELECT) {
                
                mContext.setContentView(R.layout.select_apks_install);
                mBackLayout = (LinearLayout) mContext.findViewById(R.id.back_layout);
                mListView = (ListView) mContext.findViewById(R.id.apk_listview);
                mApksSlctedNumTv = (TextView) mContext.findViewById(R.id.apks_selected_num);
                mAllApksNumTv = (TextView) mContext.findViewById(R.id.all_apks_num);
                mApksSlctedSizeTv = (TextView) mContext.findViewById(R.id.selected_apks_size);
                mCfirmInstallBtn = (Button) mContext.findViewById(R.id.confirm_install);
                mCancleInstallBtn = (Button) mContext.findViewById(R.id.cancle_install);
                mSelectAllView = (ImageView) mContext.findViewById(R.id.select_all);
                mSelectedAllLayout = (LinearLayout) mContext.findViewById(R.id.selectedAllLayout);
                
            } else if (what == CATEGORY_INSTALLING) {
                mContext.setContentView(R.layout.apks_installing);
                mListView = (ListView) mContext.findViewById(R.id.apk_installing_listview);
                mApksSlctedNumTv = (TextView) mContext.findViewById(R.id.selected_apks_num);
                mApksSlctedSizeTv = (TextView) mContext.findViewById(R.id.selected_apks_size);
                mApksInstallingNumTv = (TextView) mContext.findViewById(R.id.apks_installing_num);
                mApksInstallingSumTv = (TextView) mContext.findViewById(R.id.apks_installing_sum);
                mInstallingTipLayout = (LinearLayout) mContext.findViewById(R.id.installing_tip);
                mInstallingCpltedLayout = (LinearLayout) mContext.findViewById(R.id.installing_completed);
            } else if (what == CATEGORY_INSTALL_COMPLETED) {
                mContext.setContentView(R.layout.install_apks_tip);
                mInstallApksCpltedBtn = (Button)mContext.findViewById(R.id.install_apks_complete_btn);
                mInstalledNumText = (TextView)mContext.findViewById(R.id.installed_num);
                mInstallSumNumText = (TextView)mContext.findViewById(R.id.install_sum_num);
            }
        } else if (mInstallCategory == SINGLE_INSTALL) {
            if (what == CATEGORY_SINGLE_INSTALL_CONFIRM) {
                mContext.setContentView(R.layout.install_confirm_eostek);
                mInstallApkTitleText = (TextView)mContext.findViewById(R.id.install_apk_title);
                mCfirmInstallBtn = (Button)mContext.findViewById(R.id.confirm_install);
                mCancleInstallBtn = (Button)mContext.findViewById(R.id.cancle_install);
                
            } else if (what == CATEGORY_SINGLE_INSTALLING) {
                mContext.setContentView(R.layout.install_progress);
                mApkNameText = (TextView)mContext.findViewById(R.id.apk_name);
            } else if (what == CATEGORY_SINGLE_INSTALL_COMPLETED) {
                mContext.setContentView(R.layout.install_success_tip);
                mInstallApkCpltedBtn = (Button)mContext.findViewById(R.id.install_complete_btn);
                mOpenApkBtn = (Button)mContext.findViewById(R.id.open_apk);
                
            }

        } else if (mInstallCategory == UNINSTALL) {

        } else if(mInstallCategory==BLOCK_STALL){
            if (what == CATEGORY_BLOCK_APK) {
            mContext.setContentView(R.layout.block_list_apk_alert);
            mCancleInstallBtn = (Button)mContext.findViewById(R.id.uninstall_system_apk_alert_btn);
        }
        }
    }

    public void regisertListener() {

        if (mInstallCategory == BATCH_INSTALL) {
            if (what == CATEGORY_SELECT) {
                mListView.setOnItemClickListener(new ApkItemClickListener());
                mListView.setOnFocusChangeListener(new OnListViewFocusChangeListener());
//                select_all.setOnClickListener(new SelectAllClickListener());
                mSelectedAllLayout.setOnClickListener(new SelectAllClickListener());
                mSelectedAllLayout.setOnFocusChangeListener(new SelectAllFocusChangeListener());
                
                mCfirmInstallBtn.setOnClickListener(new ConfirmInstallBtnClickListener());
                
                mCancleInstallBtn.setOnClickListener(new CancleInstallBtnClickListener());
            } else if (what == CATEGORY_INSTALL_COMPLETED) {
                mInstallApksCpltedBtn.setOnClickListener(new InstallCompletedButListener());
            }
        } else if (mInstallCategory == SINGLE_INSTALL) {
            if (what == CATEGORY_SINGLE_INSTALL_CONFIRM) {
                mCfirmInstallBtn.setOnClickListener(new SingleInstallBtnClickListener());
                
                mCancleInstallBtn.setOnClickListener(new CancleInstallBtnClickListener());
            } else if (what == CATEGORY_SINGLE_INSTALL_COMPLETED) {
                mInstallApkCpltedBtn.setOnClickListener(new CancleInstallBtnClickListener());
                
                mOpenApkBtn.setOnClickListener(new OpenApkBtnClickListener());
            }
        }else if(mInstallCategory==BLOCK_STALL){
            if (what == CATEGORY_BLOCK_APK) {
                mCancleInstallBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.finish();
                    }
                });
            }
        }
    }

    @SuppressLint("HandlerLeak")
    public void refreshUI() {

        mContext.runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.d(TAG, "refreshUI ... , install_category : " + mInstallCategory + ", what : " + what);
                
                int num = 0;
                if (mInstallCategory == BATCH_INSTALL) {
                    
                    ApkSeletedItemAdapter adapter = new ApkSeletedItemAdapter(mContext);
                    
                    if (what == CATEGORY_SELECT) {
                        mApkList = mLogic.mApkList;
                        mSlctedApkList = mLogic.mSelectedApkList;
                        Log.d(TAG, "" + mApksSlctedNumTv);
                        Log.d(TAG, "" + mSlctedApkList);
                        if (mApkList != null && mSlctedApkList != null) {
                            mApksSlctedNumTv.setText("" + mSlctedApkList.size());
                            mAllApksNumTv.setText("" + mApkList.size());
                            mApksSlctedSizeTv.setText("" + getApkListSize(mSlctedApkList));
                            
                            //num = mApkList.size();
                            if (mContext.mEntriesNames != null) {
                                num = mContext.mEntriesNames.size();
                            }
                            
                            adapter.setmList(mApkList);
                        }
                        setLayoutHeight(adapter, num);
                        
                    } else if (what == CATEGORY_INSTALLING) {
                        mApksSlctedNumTv.setText("" + mSlctedApkList.size());
                        mApksSlctedSizeTv.setText("" + getApkListSize(mSlctedApkList));
                        //apks_installing_num_textView.setText("" + mLogic.getApk_install_success_num());
                        mApksInstallingNumTv.setText("" + (mLogic.getCurrent_apk_index() + 1));
                        mApksInstallingSumTv.setText("" + mSlctedApkList.size());
                        
                        num = mSlctedApkList.size();
                        
                        adapter.setmList(mSlctedApkList);
                        setLayoutHeight(adapter, num);
                        
                    } else if (what == CATEGORY_INSTALL_COMPLETED) {
                        mInstalledNumText.setText("" + mLogic.getApk_install_success_num());
                        mInstallSumNumText.setText("" + mApkList.size());
                    }
                    
                } else if (mInstallCategory == SINGLE_INSTALL) {
                    if (what == CATEGORY_SINGLE_INSTALL_CONFIRM) {
                        Log.d(TAG, "" + mSlctedApkList.toString());
                        String string = mContext.getResources().getString(R.string.sure_install_tip1);
                        mInstallApkTitleText.setText(String.format(string, mSlctedApkList.get(0).getAppName()));
                    } else if (what == CATEGORY_SINGLE_INSTALLING) {
                        String string = mContext.getResources().getString(R.string.install_progress_tip);
                        mApkNameText.setText(String.format(string, mSlctedApkList.get(0).getAppName()));
                    }
                } else if (mInstallCategory == UNINSTALL) {
                    
                }
                mRefreshNum = true;
            }
        });
    }
    
    public void setLayoutHeight(ApkSeletedItemAdapter adapter, int num){
        Log.d(TAG, "Set listView Height ... ");
        mListView.setAdapter(adapter);

        View listItem = mListView.getAdapter().getView(0, null, mListView);
        listItem.measure(0, 0);
        int list_item_height = listItem.getMeasuredHeight() + mListView.getDividerHeight();
        ViewGroup.LayoutParams paramss = mListView.getLayoutParams();
        if (num > MAX_NUM) {
            paramss.height = list_item_height * MAX_NUM;
        } else {
            paramss.height = list_item_height * num;
        }
        paramss.width = LayoutParams.FILL_PARENT;
        mListView.setLayoutParams(paramss);
        
        if (what == CATEGORY_SELECT){ 
            ViewGroup.LayoutParams paramsss = mBackLayout.getLayoutParams();
            paramsss.height = 250 + paramss.height;
            mBackLayout.setLayoutParams(paramsss);
        }
        
        WindowManager.LayoutParams params = mContext.getWindow().getAttributes();
        params.height = 250 + paramss.height;
        
        if (adapter.getCount() > MAX_NUM) {
            mListView.setStackFromBottom(true);
        } else {
            mListView.setStackFromBottom(false);
        }
        mListView.setSelection(adapter.getCount()-1);
        mContext.getWindow().setAttributes(params);
    }

    private class ApkItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            ImageView status_view = (ImageView)view.findViewById(R.id.apk_install_status);
            if (what == CATEGORY_SELECT) {
                if (mApkList.get(position).getStatus() == PackageModel.FLAG_SELECTED) {
                    mApkList.get(position).setStatus(PackageModel.FLAG_NOT_SELECTED);
                    mSlctedApkList.remove(mApkList.get(position));
                    status_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_not_selected));
                } else if (mApkList.get(position).getStatus() == PackageModel.FLAG_NOT_SELECTED) {
                    mApkList.get(position).setStatus(PackageModel.FLAG_SELECTED);
                    mSlctedApkList.add(mApkList.get(position));
                    status_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_selected));
                }
                mApksSlctedNumTv.setText("" + mSlctedApkList.size());
                mApksSlctedSizeTv.setText("" + getApkListSize(mSlctedApkList));
                //refreshUI();
            }
        }
    }

    private class SelectAllClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (what == CATEGORY_SELECT) {
                if (isSelectAll) {
                    for (int i = 0; i < mApkList.size(); i++) {
                        mApkList.get(i).setStatus(PackageModel.FLAG_NOT_SELECTED);
                    }
                    mSelectAllView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_not_selected));
                    mSlctedApkList.clear();
                    isSelectAll = false;
                } else {
                    for (int i = 0; i < mApkList.size(); i++) {
                        mApkList.get(i).setStatus(PackageModel.FLAG_SELECTED);
                        mSlctedApkList.add(mApkList.get(i));
                    }
                    mSelectAllView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_selected));
                    isSelectAll = true;
                }
                refreshUI();
            }
        }

    }
    
    private class SelectAllFocusChangeListener implements OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(TAG, "----->hasFocus : " + hasFocus);
            if (hasFocus) {
                if (isSelectAll) {
                    mSelectAllView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_selected_focus));
                } else {
                    mSelectAllView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_not_selected_focus));
                }
            } else {
                if (isSelectAll) {
                    mSelectAllView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_selected));
                } else {
                    mSelectAllView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_not_selected));
                }
            }
        }
    }

    private class ConfirmInstallBtnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mLogic.isParseCompleted) {
                if (mInstallCategory == BATCH_INSTALL) {
                    what = CATEGORY_INSTALLING;
                    for (int i = 0; i < mSlctedApkList.size(); i++) {
                        mSlctedApkList.get(i).setStatus(PackageModel.FLAG_INSTALL_WAITING);
                    }
                    findViews();
                    refreshUI();
                }
                mLogic.install(mSlctedApkList);
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.parse_wait), Toast.LENGTH_LONG).show();
            }
        }

    }

    private class CancleInstallBtnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            isParseThreadStop = true;
            //mContext.parseThread.destroy();
            mLogic.mApkList.clear();
            mLogic.mSelectedApkList.clear();
            mContext.finish();
        }

    }
    
    public class InstallCompletedButListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mLogic.mApkList.clear();
            mLogic.mSelectedApkList.clear();
            mContext.finish();
        }
        
    }
    
    public class SingleInstallBtnClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            what = CATEGORY_SINGLE_INSTALLING;
            findViews();
            refreshUI();
            mLogic.install(mSlctedApkList);
        }
        
    }
    
    /**
     * 安装完成后打开apk
     * @author shirley
     *
     */
    public class OpenApkBtnClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mSlctedApkList != null && mSlctedApkList.size() > 0) {
                Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mSlctedApkList.get(0).getPkgName());
                boolean enabled = false;
                if (intent != null) {
                    List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(intent, 0);
                    if (list != null && list.size() > 0) {
                        enabled = true;
                    }
                }
                if (enabled) {
                    if (mSlctedApkList.get(0).getPkgName() != null) {
                        mContext.startActivity(intent);
                        mContext.finish();
                    } else {
                        mContext.finish();
                        Log.i(TAG, "Finished installing "+mSlctedApkList.get(0).getPkgName());
                    }
                }
            }
            
        }
        
    }
    
    public class OnListViewFocusChangeListener implements OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            ListView view = (ListView)v;
            if (hasFocus) {
                //聚焦
                mListView.smoothScrollToPosition(mListView.getBottom());
                mListView.setSelection(mListView.getBottom());
            } else {
                //失去焦点
            }
        }
        
    }
    

    public double getApkListSize(List<PackageModel> list) {
        double size = 0.0;
        if (list == null) {
            return size;
        }
        for (PackageModel apk : list) {
            size += apk.getSize();
        }
        size = Double.parseDouble(String.format(Locale.ROOT, "%.1f", size));
        return size;
    }

}
