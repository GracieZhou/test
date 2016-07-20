package com.eostek.scifly.devicemanager.manage.garbage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
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
import com.eostek.scifly.devicemanager.manage.garbage.listener.ScanBigFileTaskListener;
import com.eostek.scifly.devicemanager.manage.garbage.task.ScanBigFileTask;
import com.eostek.scifly.devicemanager.ui.ConfirmDialog;
import com.eostek.scifly.devicemanager.ui.ConfirmDialog.DialogOnClickListener;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.Util;

public class BigFileActivity extends Activity implements OnItemClickListener {

	private static final String TAG = BigFileActivity.class.getSimpleName();

	private TextView mTvClear;

	private GridView mGridView;

	private long mBigFileSize;

	private List<String> mBigFiles;

	private BigFileAdapter mAdapter;

	private List<BigFileInfo> mBigFileInfoList = new ArrayList<BigFileInfo>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_manage_garbage_big_file);
		initViews();
		initValues();
		registerListener();
		scanBigFile();
	}

	private void initViews() {
		mTvClear = (TextView) findViewById(R.id.tv_clear_big_file);
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
						CheckBox cb = (CheckBox) mGridView.findViewById(R.id.cb_big_file);
						if (cb.getVisibility() == 0) {
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

	private void scanBigFile() {
		if (mBigFileInfoList.size() != 0) {
			mBigFileInfoList.clear();
		}

		// must be more than 50MB
		DeviceManager.getInstance().startTask(
				new ScanBigFileTask(new ScanBigFileTaskListener(handler), this, Constants.HALF_A_HUNDRED_MB));
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		CheckBox cb = (CheckBox) view.findViewById(R.id.cb_big_file);
		final BigFileInfo info = mBigFileInfoList.get(position);
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
					scanBigFile();
				}

				@Override
				public void onCancelClick() {

				}
			});
			dialog.show();
		}
	}

	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.GARBAGE_MSG_BIG_FILE_AVAILABLE:
				mBigFileSize = msg.getData().getLong("size");
				mBigFiles = msg.getData().getStringArrayList("files");

				if (mBigFileSize == 0) {
					mTvClear.setText(String.format("%1$s", getString(R.string.act_garbage_tv_clean_complete)));
				} else {
					mTvClear.setText(String.format(getString(R.string.act_bigfile_tv_prefix_choose),
							Util.sizeToString(mBigFileSize), ""));
				}

				if (mBigFiles.size() > 0) {
					for (String str : mBigFiles) {
						BigFileInfo info = new BigFileInfo();
						File file = new File(str);
						if (file.exists() && file.canRead()) {
							info.setmAbsolutePath(file.getAbsolutePath());
							info.setmName(file.getName());
							info.setmSize(Util.sizeToString(file.length()));
							info.setmResourse(file.getAbsolutePath());
						}
						mBigFileInfoList.add(info);
					}
				}

				mAdapter = new BigFileAdapter(BigFileActivity.this, mBigFileInfoList);
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
						DeleteFile( mBigFileInfoList.toArray());
						mBigFileInfoList.clear();
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
						List<BigFileInfo> tmp = new ArrayList<BigFileInfo>(0);
						for (BigFileInfo info : mBigFileInfoList) {
							if(info.ismIsChecked()) tmp.add(info);
						}
						mBigFileInfoList.removeAll(tmp);
						DeleteFile( tmp.toArray());
						scanBigFile();
					}

					@Override
					public void onCancelClick() {

						dialog.dismiss();
					}
				});
				dialog.show();
			} else if (resultCode == MenuActivity.RESULT_SELECT_ALL) {
				for (BigFileInfo info : mBigFileInfoList) {
					info.setmIsChecked(true);
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	public void DeleteFile(Object... files) {

		File file = null;
		for (int i = 0; i < files.length; ++i) {
			if (files[i] instanceof BigFileInfo) {
				String filePath = ((BigFileInfo) files[i]).getmAbsolutePath();
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
