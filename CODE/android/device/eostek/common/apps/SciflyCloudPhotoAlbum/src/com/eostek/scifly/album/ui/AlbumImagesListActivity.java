
package com.eostek.scifly.album.ui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.scifly.album.AlbumApplication;
import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.icloud.CloudDiskImpl;
import com.eostek.scifly.album.icloud.CloudDiskImpl.IRequestCallback;
import com.eostek.scifly.album.utils.ViewHolder;
import com.google.common.collect.Lists;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName: PhotoAlbumListActivity.
 * @Description:小图预览我的图片.
 * @author: lucky.li.
 * @date: Dec 16, 2015 9:13:18 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class AlbumImagesListActivity extends Activity {
    @ViewInject(R.id.album_title)
    private TextView mListName;

    @ViewInject(R.id.album_grid_list)
    private GridView mAlbumGridView;

    private PhotoAlbumListAdapter mAdapter;

    @ViewInject(R.id.array_down)
    private ImageView mArrayDownView;

    private List<String> mAlbumFolderNameList = Lists.newArrayList();

    private List<String> mImageUris = Lists.newArrayList();

    private int mCurrentPos = 0;

    private CloudDiskImpl mCloudDisk;

    private final int SUCCESS_MSG = 0x0008;

    private final int ERROR_MSG = 0x0009;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SUCCESS_MSG) {
                mAdapter.notifyDataSetChanged();
            } else if (msg.what == ERROR_MSG) {
                Toast.makeText(AlbumImagesListActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album_list);
        ViewUtils.inject(this);
        initDatasAndAdapter();
    }

    private void initDatasAndAdapter() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            int type = getIntent().getIntExtra(Constants.CLOUD_DISK_TYPE, Constants.BAIDU_CLOUD_DISK);
            int position = getIntent().getIntExtra(Constants.CURRENT_POSITION, 0);
            mCloudDisk = CloudDiskImpl.getCloudDisk(type, this);
            mAlbumFolderNameList.addAll(mCloudDisk.getAlbumFolderNameList().keySet());
            if (mAlbumFolderNameList.size() > position) {

                final String albumNameString = mAlbumFolderNameList.get(position);
                mListName.setText(albumNameString);
                if (Constants.ROOT_ALBUM_NAME.equals(albumNameString)) {
                    mImageUris = mCloudDisk.getRootImageUris();
                } else {
                    int time = 0;
                    if (mCloudDisk.getAlbumFolderInfos().get(albumNameString) != null
                            && mCloudDisk.getAlbumFolderInfos().get(albumNameString).size() > 0) {
                        mImageUris = mCloudDisk.getAlbumFolderInfos().get(albumNameString);
                        time = 3000;
                    }

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCloudDisk.setActivity(AlbumImagesListActivity.this);
                            mCloudDisk.setCallback(new IRequestCallback() {
                                @Override
                                public void parseSuccess() {
                                    List<String> mList = mCloudDisk.getFolderImagesUris();
                                    if (mList == null || mList.size() <= 0
                                            || (mList.contains(mImageUris) && mImageUris.contains(mList))) {
                                        return;
                                    }
                                    mImageUris = mList;
                                    Set<String> set = new LinkedHashSet<String>();
                                    set.addAll(mImageUris);
                                    mImageUris.clear();
                                    mImageUris.addAll(set);
                                    mHandler.obtainMessage(SUCCESS_MSG).sendToTarget();
                                }

                                @Override
                                public void parseError(String errorMsg) {
                                    mHandler.obtainMessage(ERROR_MSG, errorMsg).sendToTarget();
                                }
                            });
                            mCloudDisk.getImageUrlsFromFolder(albumNameString);
                        }
                    }, time);

                }
            }
        }
        mAdapter = new PhotoAlbumListAdapter();
        mAlbumGridView.setAdapter(mAdapter);
        mAlbumGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position;
                startSlideShow(false);
            }
        });
        mAlbumGridView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void startSlideShow(boolean aotupaly) {
        Intent intent = new Intent(Constants.IMAG_EPAGER_ACTION);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.IMAGE_PATHS, (ArrayList<String>) mImageUris);
        bundle.putInt(Constants.CURRENT_POSITION, mCurrentPos);
        bundle.putBoolean(Constants.AUTO_PLAY, aotupaly);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            Intent intent = new Intent(Constants.MENU_ACTION);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(Constants.IMAGE_PATHS, (ArrayList<String>) mImageUris);
            bundle.putInt(Constants.CURRENT_POSITION, mCurrentPos);
            intent.putExtra("bundle", bundle);
            startActivityForResult(intent, 2);
            overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK) {
            startSlideShow(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class PhotoAlbumListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mImageUris.size();
        }

        @Override
        public String getItem(int position) {
            return mImageUris.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AlbumImagesListActivity.this).inflate(R.layout.item_photo_album_list,
                        null);
            }
            ImageView mCoverView = ViewHolder.get(convertView, R.id.album_cover);
            ImageLoader.getInstance().displayImage(getItem(position), mCoverView,
                    AlbumApplication.getInstance().getDisplayImageOptions());
            return convertView;
        }

    }
}
