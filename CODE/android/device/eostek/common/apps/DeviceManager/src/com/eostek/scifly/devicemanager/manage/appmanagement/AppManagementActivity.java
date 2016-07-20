package com.eostek.scifly.devicemanager.manage.appmanagement;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.service.DownloadService;
import com.eostek.scifly.devicemanager.service.DownloadService.DownlaodServiceBinder;
import com.eostek.scifly.devicemanager.service.DownloadService.OnServiceListener;
import com.eostek.scifly.devicemanager.manage.appmanagement.AppManagementDialog.OnDialogListener;
import com.eostek.scifly.devicemanager.manage.appmanagement.AppManagementThread.AppUpdateInfo;
import com.eostek.scifly.devicemanager.manage.appmanagement.AppManagementThread.OnThreadListener;
import com.eostek.scifly.devicemanager.receiver.MessageListenerBase;
import com.eostek.scifly.devicemanager.receiver.MessageReceiver;
import com.eostek.scifly.devicemanager.ui.DownloadProcessView;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.SciflyToast;

import java.util.ArrayList;
import java.util.List;

public class AppManagementActivity extends Activity implements OnItemClickListener {

	private static final String TAG = AppManagementActivity.class.getSimpleName();

	public static final int MSG_TYPE_EVT_QUERY_SUCCESS = 0;
	public static final int MSG_TYPE_EVT_QUERY_FAILURE = 1;
	public static final int MSG_TYPE_EVT_SYNC_INFOLIST = 2;
	public static final int MSG_TYPE_EVT_UPDATE_ITEM = 3;
	public static final int MSG_TYPE_EVT_INSTALL_SUCCESS = 4;
    public static final int MSG_TYPE_EVT_INSTALL_FAILURE = 5;
    public static final int MSG_TYPE_EVT_TOAST_RUNNING = 6;
    public static final int MSG_TYPE_EVT_PACKAGE_DEL = 7;
    public static final int MSG_TYPE_EVT_PACKAGE_ADD = 8;
    public static final int MSG_TYPE_EVT_PACKAGE_REPLACED = 9;
    public static final int MSG_TYPE_EVT_UPDATE_PROCESS = 10;

	private Button mBtnUpdate;
	private CheckBox mCbUpdate;
	private GridView mGridView;
	private AppManagementAdapter mAdapter;
	private AppManagementHandler mHandler;
	private AppManagementThread mThread;
	private BroadcastReceiverListener mReceiverListener;
	private PackageManager mPackageManager;

	private DownloadServiceConnection mDownloadServiceConnection;
	private DownloadServiceListener mDownloadServiceListener;
	private DownloadService mDownloadService;

	private static List<AppManagementInfo> mAppManagementInfoList;
	
	private static List<String> mPacknameBlackList;

	private void queryUpdate() {
        mThread = new AppManagementThread(this);
        mThread.setOnThreadListener(new OnThreadListener() {
            @Override
            public void onSyncInfo(AppUpdateInfo info) {
                mHandler.obtainMessage(MSG_TYPE_EVT_SYNC_INFOLIST, info).sendToTarget();
            }

            @Override
            public void onQueryInfoSuccess() {
                mHandler.obtainMessage(MSG_TYPE_EVT_QUERY_SUCCESS).sendToTarget();
            }

            @Override
            public void onQueryInfoFailure() {

            }
        });
        mThread.start();
    }
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_app_management);

		mPackageManager = getPackageManager();
		mReceiverListener = new BroadcastReceiverListener();
		MessageReceiver.addMessageListener(mReceiverListener);
		
		mHandler = new AppManagementHandler();
		
		Intent mIntent = new Intent(this, DownloadService.class);
		mDownloadServiceConnection = new DownloadServiceConnection();
		bindService(mIntent, mDownloadServiceConnection, Context.BIND_AUTO_CREATE);

		initManageList();
		initViews();

	}

	@Override
	protected void onResume() {
		super.onResume();
		queryUpdate();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		MessageReceiver.removeMessageListener(mReceiverListener);
	}

	private void initManageList() {
	    mPacknameBlackList = new ArrayList<String>();
	    mAppManagementInfoList = new ArrayList<AppManagementInfo>();
	    mPacknameBlackList.clear();
	    mAppManagementInfoList.clear();
	    String[] blackList = getResources().getStringArray(R.array.manage_app_blacklist);
	    for(String packname : blackList) {
            mPacknameBlackList.add(packname);
        }
	    final Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	    List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentActivities(mIntent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packname = resolveInfo.activityInfo.applicationInfo.packageName;
            PackageInfo pkginfo = null;
            ApplicationInfo applicationInfo = null;
            AppManagementInfo item = new AppManagementInfo();
            try {
                pkginfo = mPackageManager.getPackageInfo(packname, 0);
                applicationInfo = mPackageManager.getApplicationInfo(packname, 0);
            } catch (NameNotFoundException e) {

            } catch (Exception e) {

            }

            if (packname.equals(getApplicationContext().getPackageName())) {
                continue;
            }
            
            if(mPacknameBlackList.contains(packname)) {
                continue;
            }

            item.setmDrawable(resolveInfo.loadIcon(mPackageManager));
            item.setmName(resolveInfo.loadLabel(mPackageManager));
            item.setmPkgName(packname);
            item.setmVersionName(pkginfo.versionName);
            item.setmUpdateFlag(false);

            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                    || (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                item.setmSystemApp(true);
            } else {
                item.setmSystemApp(false);
            }
            mAppManagementInfoList.add(item);
        }
	}
	
	private void addManageList(String packname) {
	    PackageInfo pkginfo = null;
        ApplicationInfo applicationInfo = null;
        AppManagementInfo item = new AppManagementInfo();
        try {
            pkginfo = mPackageManager.getPackageInfo(packname, 0);
            applicationInfo = mPackageManager.getApplicationInfo(packname, 0);
        } catch (NameNotFoundException e) {

        } catch (Exception e) {

        }

        if (packname.equals(getApplicationContext().getPackageName())) {
            return;
        }

        item.setmDrawable(applicationInfo.loadIcon(mPackageManager));
        item.setmName(applicationInfo.loadLabel(mPackageManager));
        item.setmPkgName(packname);
        item.setmVersionName(pkginfo.versionName);
        item.setmUpdateFlag(false);

        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                || (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            item.setmSystemApp(true);
        } else {
            item.setmSystemApp(false);
        }
        mAppManagementInfoList.add(item);
        adapterNotifyChanged();
	}
	
	private void delManageList(String packname) {
	    for (AppManagementInfo item : mAppManagementInfoList) {
            if (item.getmPkgName().equals(packname)) {
                mAppManagementInfoList.remove(item);
                adapterNotifyChanged();
                break;
            }
        }
	}

	private void initViews() {
	    boolean auto_update = false;
	    
		mBtnUpdate = (Button) findViewById(R.id.btn_one_click_update);
		mBtnUpdate.setEnabled(false);
		mBtnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (AppManagementThread.getUpdateList().size() == 0) {
					showToast(getResources().getString(R.string.no_update_app));
				} else {
				    if(mDownloadService != null) {
				        for (AppUpdateInfo item : AppManagementThread.getUpdateList()) {
				            mDownloadService.startDownloadItem(item.getmUrlPath(), item.getmPkgName());
	                    }
                    }
				}
			}
		});

		auto_update = AppManagementConfig.getAutoUpdateCfg(AppManagementActivity.this, Constants.CB_AUTOUPDATE);
		mCbUpdate = (CheckBox) findViewById(R.id.cb_update_on_start);
		mCbUpdate.setChecked(auto_update);
		mCbUpdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				boolean autoUpdateCfg = isChecked;
				AppManagementConfig
						.setAutoUpdateCfg(AppManagementActivity.this, Constants.CB_AUTOUPDATE, autoUpdateCfg);
			}
		});

		mGridView = (GridView) findViewById(R.id.app_management_gridview);
		mGridView.setOnItemClickListener(this);
		adapterNotifyChanged();
		mGridView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean gridViewFocus) {
				int i = mGridView.getSelectedItemPosition();
				view = mGridView.getChildAt(i);
				RelativeLayout mLayout = (RelativeLayout)view.findViewById(R.id.act_appmanagement_gridview_item_select);

				if (gridViewFocus) {
                    mLayout.setBackgroundResource(R.drawable.bg_autostart_dark_list_selector);
                } else {
                    mLayout.setBackgroundResource(R.drawable.pic_bg);
                }
			}
		});
	}

	private void adapterNotifyChanged() {
		if (mAdapter == null) {
			mAdapter = new AppManagementAdapter(AppManagementActivity.this, mAppManagementInfoList);
			mGridView.setAdapter(mAdapter);
		}
		mAdapter.notifyDataSetChanged();
	}

	private void showToast(String toastString) {
		SciflyToast.showShortToast(this, toastString);
	}

	private class AppManagementHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
		    String packname = null;
		    String toastStr = null;
		    AppManagementInfo appManagementInfo = null;
		    View viewItem = null;
		    DownloadProcessView processView = null;
		    
			switch (msg.what) {
			case MSG_TYPE_EVT_QUERY_SUCCESS:
				mBtnUpdate.setEnabled(true);
				if (AppManagementThread.getUpdateList().size() == 0) {
					showToast(getResources().getString(R.string.no_update_app));
				}
				adapterNotifyChanged();
				break;

			case MSG_TYPE_EVT_QUERY_FAILURE:
				showToast(getResources().getString(R.string.network_unavaliable));
				adapterNotifyChanged();
				break;

			case MSG_TYPE_EVT_SYNC_INFOLIST:
				AppUpdateInfo updateInfo = (AppUpdateInfo) msg.obj;
				for (int i = 0; i < mAppManagementInfoList.size(); i++) {
					if (mAppManagementInfoList.get(i).getmPkgName().equals(updateInfo.getmPkgName())) {
						mAppManagementInfoList.get(i).setmAbsolutePath(updateInfo.getmUrlPath());
						mAppManagementInfoList.get(i).setmUpdateFlag(updateInfo.getmUpdateFlag());
						adapterNotifyChanged();
						break;
					}
				}
				break;

			case MSG_TYPE_EVT_UPDATE_ITEM:
			    appManagementInfo = mAppManagementInfoList.get(msg.arg1);
			    if(mDownloadService != null) {
			        appManagementInfo.setIsUpdating(true);
                    mDownloadService.startDownloadItem(appManagementInfo.getmAbsolutePath(), appManagementInfo.getmPkgName());
                }
			    showToast(String.format(getString(R.string.act_management_dlg_toast_update), appManagementInfo.getmName()));
			    adapterNotifyChanged();
			    break;
			case MSG_TYPE_EVT_INSTALL_SUCCESS:
			    appManagementInfo = mAppManagementInfoList.get(msg.arg1);
			    viewItem = mGridView.getChildAt(msg.arg1);
			    if(viewItem != null) {
                    processView = (DownloadProcessView)viewItem.findViewById(R.id.act_appmanagement_gridview_item_process_view);
                    processView.setProgress(100);
			    }
			    adapterNotifyChanged();
			    toastStr = appManagementInfo.getmName() + getResources().getString(R.string.update_app_success);
                showToast(toastStr);
			    break;
			    
			case MSG_TYPE_EVT_INSTALL_FAILURE:
			    appManagementInfo = mAppManagementInfoList.get(msg.arg1);
                viewItem = mGridView.getChildAt(msg.arg1);
                if(viewItem != null) {
                    processView = (DownloadProcessView)viewItem.findViewById(R.id.act_appmanagement_gridview_item_process_view);
                    processView.setProgress(100);
                }
			    adapterNotifyChanged();
			    toastStr = appManagementInfo.getmName() + getResources().getString(R.string.update_app_failure);
                showToast(toastStr);
			    break;
			case MSG_TYPE_EVT_TOAST_RUNNING:
			    toastStr = (String)msg.obj + getResources().getString(R.string.app_is_updating);
                showToast(toastStr);
			    break;
			case MSG_TYPE_EVT_PACKAGE_DEL:
			    packname = (String)msg.obj;
			    delManageList(packname);
                break;
			case MSG_TYPE_EVT_PACKAGE_ADD:
			    packname = (String)msg.obj;
			    addManageList(packname);
	            queryUpdate();
			    break;
			    
			case MSG_TYPE_EVT_PACKAGE_REPLACED:
			    adapterNotifyChanged();
			    break;
			    
			case MSG_TYPE_EVT_UPDATE_PROCESS:
			    for(AppManagementInfo info : mAppManagementInfoList) {
			        if(info.getmPkgName().equals((String)msg.obj)) {
	                    viewItem = mGridView.getChildAt(info.getmPosition());
	                    if(viewItem != null) {
	                        processView = (DownloadProcessView)viewItem.findViewById(R.id.act_appmanagement_gridview_item_process_view);
	                        int percent = msg.arg1;
	                        int status = msg.arg2;
	                        if(status == DownloadService.DOWNLOAD_STATUS_RUNNING) {
	                            processView.setProgress(percent);
	                        } else if(status == DownloadService.DOWNLOAD_STATUS_OVER) {
	                            processView.setProgress(100);
	                        } else if(status == DownloadService.DOWNLOAD_STATUS_PAUSE) {
	                            processView.setProgress(percent);
	                        } else if(status == DownloadService.DOWNLOAD_STATUS_FAILED) {
	                            processView.setProgress(percent);
	                        } else if(status == DownloadService.DOWNLOAD_STATUS_PENDING) {
	                            processView.setProgress(percent);
	                        }
	                        
	                        Debug.d(TAG, "packname:" + info.getmPkgName() + " status:" + status + " percent:" + percent);
	                    }
	                }
	            }
			    break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
	    final AppManagementInfo item = mAppManagementInfoList.get(position);
		AppManagementDialog dialog = new AppManagementDialog(this, mHandler, item);
		dialog.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onUpdate() {
                mHandler.obtainMessage(MSG_TYPE_EVT_UPDATE_ITEM, position, 0).sendToTarget();
            }
        });
		dialog.show();
	}

	private class BroadcastReceiverListener extends MessageListenerBase {
		@Override
		public void onPackageRemoved(String packName) {
			super.onPackageRemoved(packName);
			mHandler.obtainMessage(MSG_TYPE_EVT_PACKAGE_DEL, packName).sendToTarget();
		}
		
		@Override
		public void onPackageAdded(String packName) {
		    super.onPackageAdded(packName);
		    mHandler.obtainMessage(MSG_TYPE_EVT_PACKAGE_ADD, packName).sendToTarget();
		}
		
		@Override
		public void onPackageReplaced(String packName) {
		    super.onPackageReplaced(packName);
		    mHandler.obtainMessage(MSG_TYPE_EVT_PACKAGE_REPLACED, packName).sendToTarget();
		}
	}

	private class DownloadServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mDownloadService = ((DownlaodServiceBinder) service).getService();
			mDownloadServiceListener = new DownloadServiceListener();
			mDownloadService.registerListener(mDownloadServiceListener);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	}

	private class DownloadServiceListener implements OnServiceListener {

		@Override
		public void onToastDownloadRunning(String packname) {
		    for(AppManagementInfo info : mAppManagementInfoList) {
                if(info.getmPkgName().equals(packname)) {
                    mHandler.obtainMessage(MSG_TYPE_EVT_TOAST_RUNNING, info.getmName()).sendToTarget();
                }
		    }
		}

		@Override
		public void onToastInstallSuccess(String packname) {
		    for (AppManagementInfo item : mAppManagementInfoList) {
                if(item.getmPkgName().equals(packname)){
                    item.setmUpdateFlag(false);
                    item.setIsUpdating(false);
                    mHandler.obtainMessage(MSG_TYPE_EVT_INSTALL_SUCCESS, item.getmPosition(), 0).sendToTarget();
                    break;
                }
            }
		}

		@Override
		public void onToastInstallFailure(String packname) {
		    for (AppManagementInfo item : mAppManagementInfoList) {
		        if(item.getmPkgName().equals(packname)){
		            item.setIsUpdating(false);
		            mHandler.obtainMessage(MSG_TYPE_EVT_INSTALL_FAILURE, item.getmPosition(), 0).sendToTarget();
                    break;
                }
            }
		}

		@Override
		public void onUpdateProcess(String packname, int status, int percent) {
		    mHandler.obtainMessage(MSG_TYPE_EVT_UPDATE_PROCESS, percent, status, packname).sendToTarget();
		}

        @Override
        public void onToastDownloadStart(String packname) {
            // TODO Auto-generated method stub
            
        }
	}
}
