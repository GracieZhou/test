package com.eostek.scifly.devicemanager.manage.garbage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.DeviceManager;
import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.manage.garbage.listener.ScanApkTaskListener;
import com.eostek.scifly.devicemanager.manage.garbage.task.ScanApkTask;
import com.eostek.scifly.devicemanager.ui.ConfirmDialog;
import com.eostek.scifly.devicemanager.ui.ConfirmDialog.DialogOnClickListener;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.Util;

public class ApkFileActivity extends Activity implements OnItemClickListener {

	private static final String TAG = ApkFileActivity.class.getSimpleName();

	private TextView mTvClear;

	private GridView mGridView;

	private long mApkFileSize;

	private List<String> mApkFiles;

	private ApkFileAdapter mAdapter;

	private List<ApkFileInfo> mApkFileInfoList = new ArrayList<ApkFileInfo>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_manage_garbage_apk_file);
		initViews();
		initValues();
		registerListener();
		scanApkFile();
	}

	private void initViews() {
		mTvClear = (TextView) findViewById(R.id.tv_clear_apk_file);
		mGridView = (GridView) findViewById(R.id.gridview);
	}

	private void initValues() {
		mGridView.setSmoothScrollbarEnabled(true);
		mGridView.setSelector(android.R.color.transparent);
	}

	private void registerListener() {
		mGridView.setOnItemClickListener(this);
		mGridView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_MENU) {
						Intent intent = new Intent(Constants.MENUACTION);
						CheckBox cb = (CheckBox) mGridView.findViewById(R.id.cb_apk_file);
						if (cb.getVisibility() == View.VISIBLE) {
							intent.putExtra("request", 1);
						} else {
							intent.putExtra("request", 2);
						}
						startActivityForResult(intent, 1);
						overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
						return true;
					}
				}
				return false;
			}
		});
	}

	private void scanApkFile() {
		if (mApkFileInfoList.size() != 0) {
		    mApkFileInfoList.clear();
		}

		DeviceManager.getInstance().startTask(new ScanApkTask(new ScanApkTaskListener(handler), this));
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		CheckBox cb = (CheckBox) view.findViewById(R.id.cb_apk_file);
		final ApkFileInfo info = mApkFileInfoList.get(position);
		if (cb.isShown()) {
			Debug.d(TAG, "onItemClick:checkbox is shown");
			if (info.ismIsChecked()) {
				info.setmIsChecked(false);
				cb.setChecked(false);
				mAdapter.notifyDataSetChanged();
			} else {
				info.setmIsChecked(true);
				cb.setChecked(true);
				mAdapter.notifyDataSetChanged();
			}
		} else {
			Debug.d(TAG, "onItemClick:delete dialog is shown");
			ConfirmDialog dialog = new ConfirmDialog(this);
			dialog.setTitleMain(getResources().getString(R.string.act_bigfile_dialog_single_delete));
			dialog.setDialogOnClickListener(new DialogOnClickListener() {
				@Override
				public void onConfirmClick() {
					File file = new File(info.getmAbsolutePath());
					file.delete();
					scanApkFile();
				}

				@Override
				public void onCancelClick() {

				}
			});
			dialog.show();
		}
	}

	private Handler handler = new Handler() {
	    
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.GARBAGE_MSG_APK_AVAILABLE:
			    mApkFileSize = msg.getData().getLong("size");
			    mApkFiles = msg.getData().getStringArrayList("files");

				if (mApkFileSize == 0) {
					mTvClear.setText(String.format("%1$s", getString(R.string.act_garbage_tv_clean_complete)));
				} else {
					mTvClear.setText(String.format(getString(R.string.act_bigfile_tv_prefix_choose),
							Util.sizeToString(mApkFileSize), ""));
				}

				if (mApkFiles.size() > 0) {
					for (String str : mApkFiles) {
					    ApkFileInfo info = new ApkFileInfo();
						File file = new File(str);
						if (file.exists() && file.canRead()) {
							info.setmAbsolutePath(file.getAbsolutePath());
							info.setmName(file.getName());
							info.setmSize(Util.sizeToString(file.length()));
							info.setmResourse(file.getAbsolutePath());
						}
						mApkFileInfoList.add(info);
					}
				}

				mAdapter = new ApkFileAdapter(ApkFileActivity.this, mApkFileInfoList);
				mGridView.setAdapter(mAdapter);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == MenuActivity.RESULT_DELETE_ALL) {
				final ConfirmDialog dialog = new ConfirmDialog(this);
				dialog.setTitleMain(getResources().getString(R.string.act_bigfile_dialog_select_delete));
				dialog.setDialogOnClickListener(new DialogOnClickListener() {
					@Override
					public void onConfirmClick() {
						DeleteFile( mApkFileInfoList.toArray());
						mApkFileInfoList.clear();
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onCancelClick() {
						dialog.dismiss();
					}
				});
				dialog.show();

			} else if (resultCode == MenuActivity.RESULT_SELECT_MORE) {
				mAdapter.setShow(true);
				mAdapter.notifyDataSetChanged();
				
			} else if (resultCode == MenuActivity.RESULT_DELETE) {
				final ConfirmDialog dialog = new ConfirmDialog(this);
				dialog.setTitleMain(getResources().getString(R.string.act_bigfile_dialog_select_delete));
				dialog.setDialogOnClickListener(new DialogOnClickListener() {
					@Override
					public void onConfirmClick() {
						List<ApkFileInfo> tmp = new ArrayList<ApkFileInfo>(0);
						for (ApkFileInfo info : mApkFileInfoList) {
							if(info.ismIsChecked()) tmp.add(info);
						}
						mApkFileInfoList.removeAll(tmp);
						DeleteFile( tmp.toArray());
						scanApkFile();
					}

					@Override
					public void onCancelClick() {
						dialog.dismiss();
					}
				});
				dialog.show();
			} else if (resultCode == MenuActivity.RESULT_SELECT_ALL) {
				for (ApkFileInfo info : mApkFileInfoList) {
					info.setmIsChecked(true);
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	public void DeleteFile(Object... files) {

		File file = null;
		for (int i = 0; i < files.length; ++i) {
			if (files[i] instanceof ApkFileInfo) {
				String filePath = ((ApkFileInfo) files[i]).getmAbsolutePath();
				file = new File(filePath);
				file.delete();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if(mAdapter != null && mAdapter.isShow()){
					mAdapter.setShow(false);
					mAdapter.notifyDataSetChanged();
					return true;
				}else{
					finish();
				}

			default:
				break;
			}
		return super.onKeyDown(keyCode, keyEvent);
	}

}
