
package com.eostek.scifly.album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.scifly.album.icloud.CloudDiskImpl;
import com.eostek.scifly.album.icloud.CloudDiskImpl.IRequestCallback;
import com.eostek.scifly.album.icloud.dropbox.DropboxCloud;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AlbumActivity extends Activity {
    @ViewInject(R.id.my_cloud_disk)
    private TextView mCloudDiskView;

    @ViewInject(R.id.baidu_photo_num)
    private TextView mBaiduCloudNumView;

    @ViewInject(R.id.sina_photo_num)
    private TextView mSinaCloudNumView;

    @ViewInject(R.id.drop_box_photo_num)
    private TextView mDropboxCloudView;

    @ViewInject(R.id.one_drive_photo_num)
    private TextView mOneDriveCloudView;

    @ViewInject(R.id.baidu_cloud)
    private ImageView mBaiduLayout;

    @ViewInject(R.id.baidu_state)
    private ImageView mBaiduStateView;

    @ViewInject(R.id.sina_state)
    private ImageView mSinaStateView;

    @ViewInject(R.id.drop_box_state)
    private ImageView mDropBoxView;

    @ViewInject(R.id.one_drive_state)
    private ImageView mOneDriveView;

    private final String MY_CLOUD_DISK_ACTION = "android.intent.action.MYCLOUDDISK";

    private final String MY_PHOTO_ALBUM_ACTION = "android.intent.action.MYPHOTOALBUM";

    private CloudDiskImpl mCloudDisk;

    private int mCloudType = Constants.BAIDU_CLOUD_DISK;

    private final int ERROR_MSG = 0x0006;

    private int dialog = 0;

    private ProgressDialog mDialog;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constants.BAIDU_CLOUD_DISK:
                    mBaiduStateView.setVisibility(View.GONE);
                    break;
                case Constants.SINA_VDISK:
                    mSinaStateView.setVisibility(View.GONE);
                    break;
                case Constants.ONEDRIVE_CLOUD_DISK:
                    mOneDriveView.setVisibility(View.GONE);
                    break;
                case Constants.DROPBOX_CLOUD_DISK:
                    mDropBoxView.setVisibility(View.GONE);
                    break;
                case ERROR_MSG:
                    Toast.makeText(AlbumActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 1000:
                    showDialog(getString(R.string.dialog_msg1));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);
        ViewUtils.inject(this);
        mBaiduLayout.requestFocus();
        if (CloudDiskImpl.isBaiduCloudBinded(this)) {
            dialog++;
            initCloudAccordingType(Constants.BAIDU_CLOUD_DISK);
        }
        if (CloudDiskImpl.isOneDriverBinded(this)) {
            dialog++;
            initCloudAccordingType(Constants.ONEDRIVE_CLOUD_DISK);
        }
        if (CloudDiskImpl.isSinaVDiskBinded(this)) {
            dialog++;
            initCloudAccordingType(Constants.SINA_VDISK);
        }
        if (CloudDiskImpl.isDropBoxBinded(this)) {
            dialog++;
            initCloudAccordingType(Constants.DROPBOX_CLOUD_DISK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCloudType == Constants.DROPBOX_CLOUD_DISK) {
            if (!CloudDiskImpl.isDropBoxBinded(this)) {
                ((DropboxCloud) mCloudDisk).authenticationSuccessful();
            }
        }
        refreshUI();

    }

    private void refreshUI() {
        if (CloudDiskImpl.isBaiduCloudBinded(this)) {
            mBaiduStateView.setVisibility(View.GONE);
        } else {
            mBaiduStateView.setVisibility(View.VISIBLE);
        }
        if (CloudDiskImpl.isOneDriverBinded(this)) {
            mOneDriveView.setVisibility(View.GONE);
        } else {
            mOneDriveView.setVisibility(View.VISIBLE);
        }
        if (CloudDiskImpl.isSinaVDiskBinded(this)) {
            mSinaStateView.setVisibility(View.GONE);
        } else {
            mSinaStateView.setVisibility(View.VISIBLE);
        }
        if (CloudDiskImpl.isDropBoxBinded(this)) {
            mDropBoxView.setVisibility(View.GONE);
        } else {
            mDropBoxView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @Title: initCloudAccordingType.
     * @Description: initial the cloud disk .
     * @param: @param type the cloud you want to initial.
     * @return: void.
     * @throws
     */
    private void initCloudAccordingType(final int type) {
        showDialog(getString(R.string.dialog_msg1));
        mCloudDisk = CloudDiskImpl.getCloudDisk(type, AlbumActivity.this);
        mCloudDisk.setActivity(this);
        mCloudDisk.setHander(mHandler);
        mCloudDisk.setCallback(new IRequestCallback() {

            @Override
            public void parseSuccess() {
                --dialog;
                if (dialog == 0) {
                    hideDialog();
                }
                mHandler.obtainMessage(type).sendToTarget();
            }

            @Override
            public void parseError(String errorMsg) {
                --dialog;
                if (dialog == 0) {
                    hideDialog();
                }
                mHandler.obtainMessage(ERROR_MSG, errorMsg).sendToTarget();
            }
        });
        mCloudDisk.logIn();
    }

    @OnClick({
            R.id.my_cloud_disk, R.id.baidu_cloud, R.id.sina_cloud, R.id.drop_box_cloud, R.id.one_drive_cloud
    })
    private void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.my_cloud_disk:
                intent.setAction(MY_CLOUD_DISK_ACTION);
                startActivity(intent);
                break;
            case R.id.baidu_cloud:
                if (CloudDiskImpl.isBaiduCloudBinded(this)) {
                    intent.setAction(MY_PHOTO_ALBUM_ACTION);
                    intent.putExtra(Constants.CLOUD_DISK_TYPE, Constants.BAIDU_CLOUD_DISK);
                    startActivity(intent);
                } else {
                    startLogin(intent, R.id.baidu_cloud);
                }
                break;
            case R.id.sina_cloud:
                if (CloudDiskImpl.isSinaVDiskBinded(this)) {
                    intent.setAction(MY_PHOTO_ALBUM_ACTION);
                    intent.putExtra(Constants.CLOUD_DISK_TYPE, Constants.SINA_VDISK);
                    startActivity(intent);
                } else {
                    startLogin(intent, R.id.sina_cloud);
                }
                break;
            case R.id.drop_box_cloud:
                if (CloudDiskImpl.isDropBoxBinded(this)) {
                    intent.setAction(MY_PHOTO_ALBUM_ACTION);
                    intent.putExtra(Constants.CLOUD_DISK_TYPE, Constants.DROPBOX_CLOUD_DISK);
                    startActivity(intent);
                } else {
                    startLogin(intent, R.id.drop_box_cloud);
                }
                break;
            case R.id.one_drive_cloud:
                if (CloudDiskImpl.isOneDriverBinded(this)) {
                    intent.setAction(MY_PHOTO_ALBUM_ACTION);
                    intent.putExtra(Constants.CLOUD_DISK_TYPE, Constants.ONEDRIVE_CLOUD_DISK);
                    startActivity(intent);
                } else {
                    startLogin(intent, R.id.one_drive_cloud);
                }
                break;
            default:
                break;
        }
    }

    private void startLogin(Intent intent, int id) {
        switch (id) {
            case R.id.baidu_cloud:
                mCloudType = Constants.BAIDU_CLOUD_DISK;
                break;
            case R.id.sina_cloud:
                mCloudType = Constants.SINA_VDISK;
                break;
            case R.id.drop_box_cloud:
                mCloudType = Constants.DROPBOX_CLOUD_DISK;
                break;
            case R.id.one_drive_cloud:
                mCloudType = Constants.ONEDRIVE_CLOUD_DISK;
                break;
            default:
                break;
        }
        mCloudDisk = CloudDiskImpl.getCloudDisk(mCloudType, AlbumActivity.this);
        mCloudDisk.setActivity(this);
        mCloudDisk.setHander(mHandler);
        mCloudDisk.setCallback(new IRequestCallback() {

            @Override
            public void parseSuccess() {
                hideDialog();
                mHandler.obtainMessage(mCloudType).sendToTarget();
            }

            @Override
            public void parseError(String errorMsg) {
                hideDialog();
                mHandler.obtainMessage(ERROR_MSG, errorMsg).sendToTarget();
            }
        });
        mCloudDisk.logIn();
    }

    @Override
    protected void onStop() {
        if (mDialog != null) {
            mDialog.cancel();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mDialog != null) {
            mDialog.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        forceStop();
        super.onBackPressed();
    }

    private void forceStop() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.killBackgroundProcesses("com.eostek.scifly.browser");
    }

    public void showDialog(String msg) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(AlbumActivity.this);
            mDialog.setTitle(getString(R.string.dialog_title));
            mDialog.setMessage(msg);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        } else if ((mDialog != null) && !mDialog.isShowing()) {
            mDialog.show();
        } else {
          
        }
    }

    public void hideDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

}
