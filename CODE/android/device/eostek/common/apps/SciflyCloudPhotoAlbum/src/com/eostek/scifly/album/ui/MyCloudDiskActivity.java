
package com.eostek.scifly.album.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.icloud.CloudDiskImpl;
import com.eostek.scifly.album.icloud.CloudDiskImpl.IRequestCallback;
import com.eostek.scifly.album.icloud.dropbox.DropboxCloud;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyCloudDiskActivity extends Activity {

    @ViewInject(R.id.baidu_cloud_bind)
    private TextView mBaiduBindView;

    @ViewInject(R.id.sina_cloud_bind)
    private TextView mSinaBindView;

    @ViewInject(R.id.drop_box_bind)
    private TextView mDropBoxBindView;

    @ViewInject(R.id.one_drive_bind)
    private TextView mOneDriveBindView;

    private CloudDiskImpl mCloudDisk;

    private int mCloudType = Constants.BAIDU_CLOUD_DISK;

    private final int ERROR_MSG = 0x0007;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constants.BAIDU_CLOUD_DISK:
                    mBaiduBindView.setText(getString(R.string.binded));
                    break;
                case Constants.SINA_VDISK:
                    mSinaBindView.setText(getString(R.string.binded));
                    break;
                case Constants.ONEDRIVE_CLOUD_DISK:
                    mOneDriveBindView.setText(getString(R.string.binded));
                    break;
                case Constants.DROPBOX_CLOUD_DISK:
                    mDropBoxBindView.setText(getString(R.string.binded));
                    break;
                case ERROR_MSG:
                    Toast.makeText(MyCloudDiskActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cloud_disk);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        if (CloudDiskImpl.isBaiduCloudBinded(this)) {
            mBaiduBindView.setText(getString(R.string.binded));
        }
        if (CloudDiskImpl.isOneDriverBinded(this)) {
            mOneDriveBindView.setText(getString(R.string.binded));
        }
        if (CloudDiskImpl.isSinaVDiskBinded(this)) {
            mSinaBindView.setText(getString(R.string.binded));
        }
        if (CloudDiskImpl.isDropBoxBinded(this)) {
            mDropBoxBindView.setText(getString(R.string.binded));
        }
    }

    @OnClick({
            R.id.clean_Thumb_cache, R.id.baidu_cloud, R.id.sina_cloud, R.id.drop_box, R.id.one_drive
    })
    private void onclick(View view) {
        switch (view.getId()) {
            case R.id.clean_Thumb_cache:
                ImageLoader.getInstance().clearMemoryCache();
                ImageLoader.getInstance().clearDiskCache();
                Toast.makeText(this,getString(R.string.Thumb_cache)+" "
                +getString(R.string.have_cleaned),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.baidu_cloud:
                if (mBaiduBindView.getText().equals(getString(R.string.binded))) {
                    showDialog(getString(R.string.baidu_cloud));
                } else {
                    startLogin(R.id.baidu_cloud);
                }
                break;
            case R.id.sina_cloud:
                if (mSinaBindView.getText().equals(getString(R.string.binded))) {
                    showDialog(getString(R.string.sina_cloud_disk));
                } else {
                    startLogin(R.id.sina_cloud);
                }
                break;
            case R.id.drop_box:
                if (mDropBoxBindView.getText().equals(getString(R.string.binded))) {
                    showDialog(getString(R.string.drop_box));
                } else {
                    startLogin(R.id.drop_box);
                }
                break;
            case R.id.one_drive:
                if (mOneDriveBindView.getText().equals(getString(R.string.binded))) {
                    showDialog(getString(R.string.one_drive));
                } else {
                    startLogin(R.id.one_drive);
                }
                break;
            default:
                break;
        }
    }

    private void showDialog(final String content) {
        Dialog dialog = new AlertDialog.Builder(this).setMessage(getString(R.string.log_off) +" "+ content + "?")
                .setPositiveButton(getString(R.string.confirm), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (content.equals(getString(R.string.one_drive))) {
                            mOneDriveBindView.setText(getString(R.string.no_bind));
                            CloudDiskImpl.getCloudDisk(Constants.ONEDRIVE_CLOUD_DISK, MyCloudDiskActivity.this)
                                    .logOut();
                        } else if (content.equals(getString(R.string.baidu_cloud))) {
                            mBaiduBindView.setText(getString(R.string.no_bind));
                            CloudDiskImpl.getCloudDisk(Constants.BAIDU_CLOUD_DISK, MyCloudDiskActivity.this).logOut();
                        } else if (content.equals(getString(R.string.drop_box))) {
                            mDropBoxBindView.setText(getString(R.string.no_bind));
                            CloudDiskImpl.getCloudDisk(Constants.DROPBOX_CLOUD_DISK, MyCloudDiskActivity.this).logOut();
                        } else if (content.equals(getString(R.string.sina_cloud_disk))) {
                            mSinaBindView.setText(getString(R.string.no_bind));
                            CloudDiskImpl.getCloudDisk(Constants.SINA_VDISK, MyCloudDiskActivity.this).logOut();
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.cancel), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void startLogin(int id) {
        switch (id) {
            case R.id.baidu_cloud:
                mCloudType = Constants.BAIDU_CLOUD_DISK;
                break;
            case R.id.sina_cloud:
                mCloudType = Constants.SINA_VDISK;
                break;
            case R.id.drop_box:
                mCloudType = Constants.DROPBOX_CLOUD_DISK;
                break;
            case R.id.one_drive:
                mCloudType = Constants.ONEDRIVE_CLOUD_DISK;
                break;
            default:
                break;
        }
        mCloudDisk = CloudDiskImpl.getCloudDisk(mCloudType, this);
        mCloudDisk.setActivity(this);
        mCloudDisk.setHander(mHandler);
        mCloudDisk.setCallback(new IRequestCallback() {

            @Override
            public void parseSuccess() {
//                mCloudDisk.hideDialog();
                mHandler.obtainMessage(mCloudType).sendToTarget();
            }

            @Override
            public void parseError(String errorMsg) {
//                mCloudDisk.hideDialog();
                mHandler.obtainMessage(ERROR_MSG, errorMsg).sendToTarget();
            }
        });
        mCloudDisk.logIn();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mCloudType == Constants.DROPBOX_CLOUD_DISK) {
            if (!CloudDiskImpl.isDropBoxBinded(this)) {
                ((DropboxCloud) mCloudDisk).authenticationSuccessful();
            }
        }
    }
}
