
package com.eostek.scifly.album.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.album.AlbumApplication;
import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.icloud.CloudDiskImpl;
import com.eostek.scifly.album.utils.ViewHolder;
import com.google.common.collect.Lists;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName: MyPhotoAlbumActivity.
 * @Description:展示我的相册.
 * @author: lucky.li.
 * @date: Dec 15, 2015 5:17:40 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class AlbumListActivity extends Activity {
    @ViewInject(R.id.album_title)
    private TextView mAlbumName;

    @ViewInject(R.id.album_grid)
    private GridView mAlbumGrid;

    private PhotoAlbumAdapter mAdapter;

    private List<String> mAlbumFolderNameList = Lists.newArrayList();

    private final String ALBUM_IMAGES_LIST_ACTION = "android.intent.action.PHOTOALBUMLIST";

    private CloudDiskImpl mCloudDisk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photo_album);
        ViewUtils.inject(this);
        initDatasAndAdapter();
    }

    private void initDatasAndAdapter() {
        final int type = getIntent().getIntExtra(Constants.CLOUD_DISK_TYPE, Constants.BAIDU_CLOUD_DISK);
        mCloudDisk = CloudDiskImpl.getCloudDisk(type, this);
        mAlbumName.setText(getAlbumName(mCloudDisk.getUserName(), type));
        mAlbumFolderNameList.addAll(mCloudDisk.getAlbumFolderNameList().keySet());
        mAdapter = new PhotoAlbumAdapter();
        mAlbumGrid.setAdapter(mAdapter);
        mAlbumGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ALBUM_IMAGES_LIST_ACTION);
                intent.putExtra(Constants.CLOUD_DISK_TYPE, type);
                intent.putExtra(Constants.CURRENT_POSITION, position);
                startActivity(intent);
            }
        });
    }

    private String getAlbumName(String userName, int type) {
        String cloudTypeString = "";
        switch (type) {
            case Constants.BAIDU_CLOUD_DISK:
                cloudTypeString = getString(R.string.baidu_cloud);
                break;
            case Constants.SINA_VDISK:
                cloudTypeString = getString(R.string.sina_cloud_disk);
                break;
            case Constants.DROPBOX_CLOUD_DISK:
                cloudTypeString = getString(R.string.drop_box);
                break;
            case Constants.ONEDRIVE_CLOUD_DISK:
                cloudTypeString = getString(R.string.one_drive);
                break;
            default:
                break;
        }
        if ("US".equals(getCurrentLanguage(AlbumListActivity.this))
                || "FR".equals(getCurrentLanguage(AlbumListActivity.this))) {
            return cloudTypeString + getString(R.string.album_name) + " " + userName;
        } else {
            return userName + getString(R.string.de) + cloudTypeString + getString(R.string.album_name);
        }
    }

    class PhotoAlbumAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mAlbumFolderNameList != null) {
                return mAlbumFolderNameList.size();
            }
            return 0;
        }

        @Override
        public String getItem(int position) {
            return mAlbumFolderNameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AlbumListActivity.this).inflate(R.layout.item_my_photo_album, null);
            }
            ImageView mCoverView = ViewHolder.get(convertView, R.id.album_cover);
            TextView mAlbumName = ViewHolder.get(convertView, R.id.album_name);
            if (position == 0 && Constants.ROOT_ALBUM_NAME.equals(getItem(position))) {
                if (mCloudDisk.getRootImageUris().size() > 0) {
                    ImageLoader.getInstance().displayImage(mCloudDisk.getRootImageUris().get(0), mCoverView,
                            AlbumApplication.getInstance().getDisplayImageOptions());
                }
            } else {
                ImageLoader.getInstance().displayImage(mCloudDisk.getCoverUrlForFolder(getItem(position)), mCoverView,
                        AlbumApplication.getInstance().getDisplayImageOptions());
            }
            mAlbumName.setText(getItem(position));
            return convertView;
        }
    }

    /**
     * @param mActivity
     * @return the current language
     */
    private static String getCurrentLanguage(Activity mActivity) {
        Configuration conf = mActivity.getResources().getConfiguration();
        String language = conf.locale.getCountry();
        return language;
    }
}
