package com.eostek.scifly.devicemanager.manage.autostart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.DeviceManager;
import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.ui.ColorScheme;
import com.eostek.scifly.devicemanager.ui.DominantColorCalculator;
import com.eostek.scifly.devicemanager.util.Debug;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;

public class AutoStartActivity extends Activity {
	private static String TAG = "AutoStartActivity";

	private final static int NOTIFY_ADAPTER = 99;

	private TwoWayGridView mAllowGrid;

	private TwoWayGridView mForbiddenGrid;

	private ImageView mAllowMore;

	private ImageView mForbiddenMore;

	// auto start Application package name list
	List<String> whiteList;

	// allowed auto start Application info list
	List<AutoStartApplicantionInfo> allowedAppList;

	// forbidden auto start Application info list
	List<AutoStartApplicantionInfo> forbiddenAppList;

	AutoStartGridAdapter mAllowAdapter;

	AutoStartGridAdapter mForbiddenAdapter;

	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NOTIFY_ADAPTER:
				mAllowAdapter.notifyDataSetChanged();
				mForbiddenAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.ac_manage_auto_start);

		mAllowGrid = (TwoWayGridView) findViewById(R.id.allowed_app);
		mAllowGrid.setOnItemClickListener(mAllowGridItemClickListener);
		mForbiddenGrid = (TwoWayGridView) findViewById(R.id.forbidden_app);
		mForbiddenGrid.setOnItemClickListener(mForbiddenGridItemClickListener);

		mAllowMore = (ImageView) findViewById(R.id.allow_right_more);
		mForbiddenMore = (ImageView) findViewById(R.id.forbidden_right_more);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			switch (keyCode) {
			case KeyEvent.KEYCODE_0:
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:

				if (mForbiddenGrid.isFocused()) {
					Debug.d(TAG, "forbiddengridview down,do nothing!!!");
				} else if (mAllowGrid.isFocused()) {
					Debug.d(TAG, "mAllowGridView, down toforbiddengridview!!!");
					mForbiddenGrid.requestFocus();

				}

				mAllowMore.setBackgroundResource(R.drawable.right_more);
				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				if (mAllowGrid.isFocused()) {
					Debug.d(TAG, "mAllowGridView ,do nothing!!!");
				} else if (mForbiddenGrid.isFocused()) {
					Debug.d(TAG, "mForbiddenGrid, down to mAllowGridView!!!");
					mAllowGrid.requestFocus();
				}

				mForbiddenMore.setBackgroundResource(R.drawable.right_more);
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (mAllowGrid.isFocused()) {
					mAllowMore.setBackgroundResource(R.drawable.right_more);
					return true;
				}
				if (mForbiddenGrid.isFocused()) {
					mForbiddenMore.setBackgroundResource(R.drawable.right_more);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (mAllowGrid.isFocused()) {
					if (mAllowGrid.getSelectedItemPosition() > 7) {
					mAllowMore.setBackgroundResource(R.drawable.right_more_focuse);
					}
					return true;
				}
				if (mForbiddenGrid.isFocused()) {
					if (mForbiddenGrid.getSelectedItemPosition() > 7) {
					mForbiddenMore.setBackgroundResource(R.drawable.right_more_focuse);
					}
					return true;
				}
				break;
			default:
				break;
			} 
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnItemClickListener mAllowGridItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
			TextView appName = (TextView) view.findViewById(R.id.tv_app_name);
			// ImageView appIcon=(ImageView)
			// view.findViewById(R.id.iv_app_icon);
			String pkgName = (String) appName.getTag();
			// ImageView appSwitch = (ImageView)
			// view.findViewById(R.id.iv_switch);
			RelativeLayout autoStart = (RelativeLayout) view.findViewById(R.id.auto_start_layout);
			// add pkgName to blacklist
			if (!TextUtils.isEmpty(pkgName)) {
				Debug.d(TAG, "AutoStartActivity:onClick->set false [" + pkgName + "]");
				DeviceManager.getInstance().setSelfStartingProcessEnable(pkgName, false);
			}
			// appSwitch.setBackgroundResource(R.drawable.app2);
			// autoStart.setBackgroundResource(R.drawable.bg_autostart_dark_list_selector);

			forbiddenAppList.add(0, allowedAppList.remove(parent.getFirstVisiblePosition() + position));
			mhandler.sendEmptyMessage(NOTIFY_ADAPTER);

			if (allowedAppList.size() <= 8)  {
				mAllowMore.setVisibility(View.GONE);
			}
			if (forbiddenAppList.size() >8) {
				mForbiddenMore.setVisibility(View.VISIBLE);
				mForbiddenMore.setBackgroundResource(R.drawable.right_more);
			}

		}
	};

	private OnItemClickListener mForbiddenGridItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {

			TextView appName = (TextView) view.findViewById(R.id.tv_app_name);
			// ImageView appIcon=(ImageView)
			// view.findViewById(R.id.iv_app_icon);
			String pkgName = (String) appName.getTag();
			RelativeLayout autoStart = (RelativeLayout) view.findViewById(R.id.auto_start_layout);
			// ImageView appSwitch = (ImageView)
			// view.findViewById(R.id.iv_switch);

			// add pkgName to blacklist
			if (!TextUtils.isEmpty(pkgName)) {
				Debug.d(TAG, "AutoStartActivity:onClick->set  mfalse [" + pkgName + "]");
				DeviceManager.getInstance().setSelfStartingProcessEnable(pkgName, true);
			}
			// appSwitch.setBackgroundResource(R.drawable.app1);
			// autoStart.setBackgroundResource(R.drawable.bg_autostart_dark_list_selector);
			allowedAppList.add(0, forbiddenAppList.remove(parent.getFirstVisiblePosition() + position));
			mhandler.sendEmptyMessage(NOTIFY_ADAPTER);

			if (forbiddenAppList.size() <= 8) {
				mForbiddenMore.setVisibility(View.GONE);
			}
			if (allowedAppList.size() > 8) {
				mAllowMore.setVisibility(View.VISIBLE);
				mAllowMore.setBackgroundResource(R.drawable.right_more);
			}

		}
	};

	private int getColor(Drawable drawable) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		Bitmap bitmap = bitmapDrawable.getBitmap();
		DominantColorCalculator colorCalculator = new DominantColorCalculator(bitmap);
		ColorScheme colorScheme = colorCalculator.getColorScheme();
		return colorScheme.primaryAccent;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (whiteList == null) {
			whiteList = new ArrayList<String>();
		} else {
			whiteList.clear();
		}
		whiteList = DeviceManager.getInstance().getSelfStartingProcessList();

		if (allowedAppList == null) {
			allowedAppList = new ArrayList<AutoStartApplicantionInfo>();
		} else {
			allowedAppList.clear();
		}

		if (forbiddenAppList == null) {
			forbiddenAppList = new ArrayList<AutoStartApplicantionInfo>();
		} else {
			forbiddenAppList.clear();
		}
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> appList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		Collections.sort(appList, new ApplicationInfo.DisplayNameComparator(pm));

		for (ApplicationInfo info : appList) {
			if (info.packageName.equals("com.eostek.scifly.devicemanager")) {
				continue;
			}
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				// system app, skip it!
				continue;
			}
			if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				// used to be a system app, skip it!
				continue;
			}
			if (isMyAppLauncherDefault(info.packageName)) {
				continue;
			}
			AutoStartApplicantionInfo appInfo = new AutoStartApplicantionInfo(info.loadLabel(pm), info.loadIcon(pm),
					info.packageName);
			// , getColor(info.loadIcon(pm))
			if (whiteList.contains(info.packageName)) {
				if (!allowedAppList.contains(appInfo)) {
					allowedAppList.add(appInfo);
				}
			} else {
				if (!forbiddenAppList.contains(appInfo)) {
					forbiddenAppList.add(appInfo);
				}
			}
		}

		if (allowedAppList.size() > 8) {
			mAllowMore.setVisibility(View.VISIBLE);
			mAllowMore.setBackgroundResource(R.drawable.right_more);
		} else {
			mAllowMore.setVisibility(View.GONE);
		}

		if (forbiddenAppList.size() > 8) {
			mForbiddenMore.setVisibility(View.VISIBLE);
			mForbiddenMore.setBackgroundResource(R.drawable.right_more);
		} else {
			mForbiddenMore.setVisibility(View.GONE);
		}

		mAllowAdapter = new AutoStartGridAdapter(this, allowedAppList, true);
		mForbiddenAdapter = new AutoStartGridAdapter(this, forbiddenAppList, false);
		mAllowGrid.setAdapter(mAllowAdapter);
		mForbiddenGrid.setAdapter(mForbiddenAdapter);

		// initialize appinstall view
		mAllowGrid.setNumRows(1);
		mAllowGrid.setNumColumns(6);
		mAllowGrid.setSmoothScrollbarEnabled(true);
		mAllowGrid.setSelector(android.R.color.transparent);
		// mAllowGrid.setLayoutAnimation(UIAnimationUtil.getLayoutShowAnimation(300));
		mForbiddenGrid.setNumRows(1);
		mForbiddenGrid.setNumColumns(6);
		mForbiddenGrid.setSmoothScrollbarEnabled(true);

		// mForbiddenGrid.setHorizontalSpacing();
		mForbiddenGrid.setSelector(android.R.color.transparent);

	}

	private boolean isMyAppLauncherDefault(String packageName) {
		if (PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission(
				"android.permission.RECEIVE_BOOT_COMPLETED", packageName)) {
			return false;
		}
		return true;
	}
}
