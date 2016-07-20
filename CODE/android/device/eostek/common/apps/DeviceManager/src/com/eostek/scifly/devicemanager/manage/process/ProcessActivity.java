
package com.eostek.scifly.devicemanager.manage.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scifly.util.MachineState;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.DeviceManager;
import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.util.Debug;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;

public class ProcessActivity extends Activity {

    private static final String TAG = ProcessActivity.class.getSimpleName();

    private static final String STR_AD_PACKAGE_NAME = "com.eostek.scifly.advertising";

    private static final int TYPE_EVENT_SCAN = 0;
    private static final int TYPE_EVENT_CLEAN_READY = 1;
    private static final int TYPE_EVENT_CLEAN_DONE = 2;
    
    
    private static final int MAX_TASKS = 11; 

    private PackageManager mPackageManager;

    private ActivityManager mActivityManager;

    private ProcessGridAdapter mProcessGridAdapter;

    private TwoWayGridView mGridView;

    private ProgressBar mPbProcessIcon;
    private ImageView mIvProcessIcon;
    
    private TextView mTvProcessRate;

    private Button mBtnAction;
    
    private TextView mTvAction;
    
    private TextView mTvStatus;
    
    private LinearLayout mLlActionLayout;
    private RelativeLayout mRlStatusLayout;
    
    private int mMemRate = 0;
    
    private int mEventType = TYPE_EVENT_SCAN;

    private List<ProcessInfo> mRunningProcessInfoList;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_manage_process);
        
        mPackageManager = getPackageManager();
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        
        initViews();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mBtnAction.requestFocus();
        mHandler.postDelayed(runnable, 5000);// open timer to start refresh UI
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);  // close timer to stop refresh UI
    }
    
    private void setFuncView(int index) {
        if(index == TYPE_EVENT_SCAN) {
            mTvStatus.setText(R.string.act_process_tv_scan_running);
            mPbProcessIcon.setVisibility(View.VISIBLE);
            mIvProcessIcon.setVisibility(View.GONE);
            mLlActionLayout.setVisibility(View.GONE);
            mRlStatusLayout.setVisibility(View.VISIBLE);
            
            mEventType = TYPE_EVENT_CLEAN_READY;
        } else if(index == TYPE_EVENT_CLEAN_READY) {
            mTvAction.setText(R.string.act_process_tv_clean_ready);
            mBtnAction.setText(R.string.act_process_btn_clean_ready);
            mPbProcessIcon.setVisibility(View.GONE);
            mIvProcessIcon.setVisibility(View.VISIBLE);
            mLlActionLayout.setVisibility(View.VISIBLE);
            mRlStatusLayout.setVisibility(View.GONE);
        } else if(index == TYPE_EVENT_CLEAN_DONE) {
            mTvAction.setText(String.format(getString(R.string.act_process_tv_confirm), mMemRate));
            mBtnAction.setText(R.string.act_process_btn_confirm);
            mPbProcessIcon.setVisibility(View.GONE);
            mIvProcessIcon.setVisibility(View.VISIBLE);
            mLlActionLayout.setVisibility(View.VISIBLE);
            mRlStatusLayout.setVisibility(View.GONE);
        }
    }
    
    private void initViews() {
        mGridView = (TwoWayGridView) findViewById(R.id.act_manage_process_gridview);
        mGridView.setSmoothScrollbarEnabled(true);
        mGridView.setNumRows(1);
        mGridView.setNumColumns(22);
        mGridView.setSmoothScrollbarEnabled(true);
        mGridView.setSelector(android.R.color.transparent);
        mRunningProcessInfoList = getRunningProcessInfoList();
        mProcessGridAdapter = new ProcessGridAdapter(this, mRunningProcessInfoList);
        mProcessGridAdapter.setProcessInfoList(mRunningProcessInfoList);
        mGridView.setAdapter(mProcessGridAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(TwoWayAdapterView<?> parent, View view, final int position, long id) {
                Debug.d(TAG, " click position " + position);
                String str = (String) view.findViewById(R.id.iv_process_icon).getTag();

                DeviceManager.getInstance().killProcess(ProcessActivity.this, str);
                Debug.d(TAG, "kill  procress:" + str);

                long memory = MachineState.getMemUsage(ProcessActivity.this);
                mTvProcessRate.setText((int) memory + "%");

                mProcessGridAdapter.setProcessInfoList(getRunningProcessInfoList());
                mProcessGridAdapter.notifyDataSetChanged();
            }
        });
        
        mPbProcessIcon = (ProgressBar) findViewById(R.id.act_manage_process_pb_icon);
        mIvProcessIcon = (ImageView) findViewById(R.id.act_manage_process_iv_icon);
        mTvProcessRate = (TextView)findViewById(R.id.act_manage_process_tv_memsize);
        long memory = MachineState.getMemUsage(getApplicationContext());
        mTvProcessRate.setText((int) memory + "%");
        
        mBtnAction = (Button) findViewById(R.id.act_manage_process_btn_action);
        mBtnAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                switch (mEventType) {
                    case TYPE_EVENT_CLEAN_READY:
                        long preMem = MachineState.getMemUsage(ProcessActivity.this);
                        DeviceManager.getInstance().killAllProcess(ProcessActivity.this);
                        long curMem = MachineState.getMemUsage(ProcessActivity.this);
                        mMemRate = (int) ((preMem - curMem > 0) ? preMem - curMem : 0);
                        mTvProcessRate.setText((int) curMem + "%");
                        mProcessGridAdapter = null;
                        mGridView.setAdapter(mProcessGridAdapter);
                        mEventType = TYPE_EVENT_CLEAN_DONE;
                        setFuncView(mEventType);
                        break;
                    case TYPE_EVENT_CLEAN_DONE:
                        finish();
                        break;
                    default:
                        break;
                }
                
            }
        });
        
        mTvAction = (TextView)findViewById(R.id.act_manage_process_tv_action);
        mTvStatus = (TextView)findViewById(R.id.act_manage_process_tv_status);
        
        mLlActionLayout = (LinearLayout)findViewById(R.id.act_manage_process_ll_action);
        mRlStatusLayout = (RelativeLayout)findViewById(R.id.act_manage_process_rl_status);
        
        
        setFuncView(TYPE_EVENT_SCAN);
        mHandler.postDelayed(mRunnable, 1000);
    }

    private List<ProcessInfo> getRunningProcessInfoList() {
        List<ApplicationInfo> listAppcations = mPackageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(mPackageManager));
        Map<String, RunningAppProcessInfo> processPkgMap = new HashMap<String, RunningAppProcessInfo>();
        List<String> recentTaskList = getRecentTaskPkgName();

        List<RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo runningProcess : processInfos) {
            String[] pkgNameList = runningProcess.pkgList;
            for (int i = 0; i < pkgNameList.length; i++) {
                String pkgName = pkgNameList[i];
                if (recentTaskList.contains(pkgName)) {
                    Debug.d(TAG, "pkgName : " + pkgName);
                    processPkgMap.put(pkgName, runningProcess);
                }
            }
        }
        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
        for (ApplicationInfo app : listAppcations) {
            if (processPkgMap.containsKey(app.packageName)) {

                int pid = processPkgMap.get(app.packageName).pid;
                int[] memoryPid = new int[] {
                    pid
                };
                MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(memoryPid);
                int memory = memoryInfo[0].dalvikPrivateDirty;
                
                Debug.d(TAG, "dalvikPrivateClean : " + memoryInfo[0].dalvikPrivateClean);
                
                ProcessInfo processInfo = new ProcessInfo();
                processInfo.setName(app.loadLabel(mPackageManager).toString());
                processInfo.setIcon(app.loadIcon(mPackageManager));

                processInfo.setPid(pid);
                processInfo.setPackageName(app.packageName);
                processInfo.setProcessName(app.processName);
                processInfo.setMemory(memory);
                                
                processInfoList.add(processInfo);
            }
        }
        return processInfoList;
    }

    private List<String> getRecentTaskPkgName() {
        List<String> recentTaskPkgList = new ArrayList<String>();
        List<RecentTaskInfo> recentTasks = mActivityManager.getRecentTasks(MAX_TASKS, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).resolveActivityInfo(mPackageManager, 0);
        for (int i = 0; i < recentTasks.size() && (i < MAX_TASKS); ++i) {
            RecentTaskInfo recentInfo = recentTasks.get(i);
            Intent intent = new Intent(recentInfo.baseIntent);
            if (recentInfo.origActivity != null) {
                intent.setComponent(recentInfo.origActivity);
            }

            if (isCurrentHomeActivity(intent.getComponent(), homeInfo)) {
                continue;
            }

            if (intent.getComponent().getPackageName().equals(this.getPackageName())) {
                continue;
            }
            
            if (recentInfo.id == -1) {
                continue;
            }
            
            Debug.d(TAG, "intent.getComponent().getPackageName() : " + intent.getComponent().getPackageName());

            String ss = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            List<String> sss = getLauncherPackageName();
            if (sss.contains(intent.getComponent().getPackageName())
                    || ss.startsWith(intent.getComponent().getPackageName())
                    || STR_AD_PACKAGE_NAME.equals(intent.getComponent().getPackageName())) {
            } else {
                recentTaskPkgList.add(intent.getComponent().getPackageName());
            }
        }
        return recentTaskPkgList;
    }

    private boolean isCurrentHomeActivity(ComponentName component, ActivityInfo homeInfo) {
        if (homeInfo == null) {
            homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).resolveActivityInfo(
                    mPackageManager, 0);
        }
        return homeInfo != null && homeInfo.packageName.equals(component.getPackageName()) && homeInfo.name.equals(component.getClassName());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            switch (keyCode) {
                case KeyEvent.KEYCODE_0:
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (!mGridView.isFocused()) {
                        mGridView.requestFocus();
                        if (getRunningProcessInfoList().size() != 0) {
                            List<ProcessInfo> list = getRunningProcessInfoList();
                            mProcessGridAdapter = new ProcessGridAdapter(ProcessActivity.this, list);
                            mProcessGridAdapter.setProcessInfoList(list);
                            mGridView.setAdapter(mProcessGridAdapter);
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (!mBtnAction.isFocused()) {
                        mBtnAction.requestFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mGridView.isFocused()) {
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mGridView.isFocused()) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private List<String> getLauncherPackageName() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
            Debug.d(TAG, "packageName =" + ri.activityInfo.packageName);
        }
        return names;
    }

    public int getCurrentPositionOnScreen() {
        if (mProcessGridAdapter == null) {
            mProcessGridAdapter = (ProcessGridAdapter) mGridView.getAdapter();
        }
        return getPositionOnScreen(mProcessGridAdapter.getLastSelectedPostion());
    }

    private int getPositionOnScreen(int position) {
        if (position < 0 || position > mGridView.getCount()) {
            return -1;
        }
        return position - mGridView.getFirstVisiblePosition();
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            setFuncView(mEventType);
        }
    };
    
    private boolean compareList(List<ProcessInfo> mlist) {
        int count = mGridView.getCount();
        List<String> listString = new ArrayList<String>();
        if (mlist.size() != count) {
            return false;
        } 
        
        if (count != 0) {
            listString.clear();
            List<ProcessInfo> list = mProcessGridAdapter.getmProcessInfoList();
            for (int i = 0; i < list.size(); i++) {
                listString.add(list.get(i).getPackageName());
            }
            for (int j = 0; j < mlist.size(); j++) {
                String str = mlist.get(j).getPackageName();
                if (!listString.contains(str)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long memory = MachineState.getMemUsage(ProcessActivity.this);
            mTvProcessRate.setText((int) memory + "%");
            mRunningProcessInfoList.clear();
            mRunningProcessInfoList = getRunningProcessInfoList();
            if ( !compareList(mRunningProcessInfoList) ) {
                mProcessGridAdapter = new ProcessGridAdapter(ProcessActivity.this, mRunningProcessInfoList);
                mProcessGridAdapter.setProcessInfoList(mRunningProcessInfoList);
                mGridView.setAdapter(mProcessGridAdapter);
            }
            mHandler.postDelayed(this, 5000);
        }
    };
}
