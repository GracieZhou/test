
package com.eostek.documentui.ui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.documentui.Constants;
import com.eostek.documentui.DocumentApplication;
import com.eostek.documentui.R;
import com.eostek.documentui.data.DataProxy;
import com.eostek.documentui.data.DownloadInfor;
import com.eostek.documentui.model.DownloadGridItemBean;
import com.eostek.documentui.util.Utils;
import com.eostek.documentui.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @ClassName: DownloadedClassifyHolder.
 * @Description:DownloadedClassifyHolder.
 * @author: lucky.li.
 * @date: Oct 9, 2015 9:22:17 AM.o
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class DownloadedClassifyHolder {
    private final String TAG = "DownloadedClassifyHolder";

    private DownloadedClassifyActivity mActivity;

    private TextView mDownloadedType;

    private GridView mGridView;

    private String serviceString = Context.DOWNLOAD_SERVICE;

    private List<DownloadGridItemBean> mDownloadInfos;

    private DownloadedClassifyAdapter mAdapter;

    private DownloadManager downloadManager;

    private boolean isSingleDeletemode = false;

    private boolean isMutilDeletemode = false;

    private String isDeleteMode = "isDeleteMode";

    private String isDetailMode = "isDetailMode";

    private DataProxy mDataProxy;

    private TextView mEmptyLayout;

    private TextView mMenuTipView;

    private Bitmap mBitmap;

    private Drawable mThumbnaildraw;

    private List<HashMap<String, DownloadInfor>> selectedItemList = null;

    private ArrayList<HashMap<String, Integer>> positionList = null;

    private int mSelectCount = -1;

    private int mposition = -1;

    /**
     * the current type eg:video,music .etc
     */
    private int mType = 0;

    private DisplayImageOptions mOptions;

    public boolean isDeletemode() {
        return isSingleDeletemode;
    }

    public boolean isMutilDeletemode() {
        return isMutilDeletemode;
    }

    /**
     * @Title: DownloadedClassifyHolder.
     * @Description: constructor.
     * @param: @param mActivity
     * @param: @param proxy.
     * @throws
     */
    public DownloadedClassifyHolder(DownloadedClassifyActivity mActivity, DataProxy proxy) {
        this.mActivity = mActivity;
        this.mDataProxy = proxy;
        mOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default)
                .showImageForEmptyUri(R.drawable.bg_default).showImageOnFail(R.drawable.bg_default).cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(Utils.dip2px(mActivity, 13))).build();
        downloadManager = (DownloadManager) mActivity.getSystemService(serviceString);
    }

    /**
     * @Title: findViews.
     * @Description: find controls by id and set listener.
     * @param: .
     * @return: void.
     * @throws
     */
    public void findViews() {
        mDownloadedType = (TextView) mActivity.findViewById(R.id.downloaded_type);
        mGridView = (GridView) mActivity.findViewById(R.id.grid);
        mMenuTipView = (TextView) mActivity.findViewById(R.id.menu);
        mEmptyLayout = (TextView) mActivity.findViewById(R.id.empty_layout);
        // setListener();
    }

    /**
     * @Title: setListener.
     * @Description: set the listener to the gridView.
     * @param: .
     * @return: void.
     * @throws
     */
    private void setListener() {
        mGridView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (!isMutilDeletemode && keyCode == KeyEvent.KEYCODE_MENU && mposition != -1) {
                        Intent intent = new Intent(Constants.MENUACTION);
                        Bundle bundle = new Bundle();
                        bundle.putString("menuMode", "isSingleDeleteMode");
                        intent.putExtras(bundle);
                        mActivity.startActivityForResult(intent, 2);
                        mActivity.overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                        return true;
                    }
                    if (isMutilDeletemode && keyCode == KeyEvent.KEYCODE_MENU) {
                        Intent intent = new Intent(Constants.MENUACTION);
                        Bundle bundle = new Bundle();
                        bundle.putString("menuMode", "isMutilDeleteMode");
                        intent.putExtras(bundle);
                        mActivity.startActivityForResult(intent, 3);
                        mActivity.overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                        return true;
                    }
                    if (isMutilDeletemode && keyCode == KeyEvent.KEYCODE_BACK) {
                        isMutilDeletemode = false;
                        if (mDownloadInfos.size() == 0) {
                            return true;
                        }
                        for (int i = 0; i < mDownloadInfos.size(); i++) {
                            mDownloadInfos.get(i).setMutilDeleteMode(false);
                        }
                        mAdapter.notifyDataSetChanged();
                        mActivity.getValuesFromMimetype();
                        mActivity.setValuesToUI();
                        return true;
                    }
                }
                return false;
            }
        });
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (isMutilDeletemode) {

                    final DownloadInfor info = mDownloadInfos.get(position).getDownloadInfor();
                    // seletedCheckBox(position, info);

                    Log.i("tag", "====seletedCheckBox()=====");
                    CheckBox cbox = (CheckBox) mGridView.getChildAt(position).findViewById(
                            R.id.downloaded_type_item_checkbox);

                    HashMap<String, DownloadInfor> map = new HashMap<String, DownloadInfor>();
                    HashMap<String, Integer> positionMap = new HashMap<String, Integer>();

                    if (!cbox.isChecked()) {
                        Log.i("tag", "===>cbox.isChecked()===true==>");
                        cbox.setChecked(true);
                        Log.i("tag", "====cbox==1===>" + cbox.isChecked());
                        map.put("info", info);
                        positionMap.put("position", position);
                        mSelectCount++;
                        selectedItemList.add(map);
                        positionList.add(positionMap);
                        Log.i("tag", "===>selectedItemList===true==>" + selectedItemList);
                        Log.i("tag", "===>positionList===true==>" + positionList);
                    } else {
                        Log.i("tag", "===>cbox.isChecked()===false==>");
                        cbox.setChecked(false);
                        Log.i("tag", "====cbox==2===>" + cbox.isChecked());
                        if (mSelectCount >= 0) {
                            /*
                             * selectedItemList.remove(mSelectCount);
                             * positionList.remove(mSelectCount);
                             * mSelectCount--;
                             */
                            for (int i = 0; i < positionList.size(); i++) {
                                if (positionList.get(i).get("position") == position) {
                                    selectedItemList.remove(i);
                                    positionList.remove(i);
                                    mSelectCount--;
                                    break;
                                }
                            }
                        } else {
                            mSelectCount = -1;
                            selectedItemList.clear();
                            positionList.clear();
                        }
                    }

                } else {
                    openFile(mDownloadInfos.get(position));
                }
            }

        });

        mGridView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                TextView textView = (TextView) v.findViewById(R.id.file_name);
                if (hasFocus && mGridView.getChildAt(0) != null) {
                    // mGridView.setSelection(0);
                    // mAdapter.notifyDataSetChanged();
                    Log.e("onFocusChange", "has focus,set selected true");
                    if (textView != null) {
                        textView.setSelected(true);
                    }
                    mposition = 0;
                } else if (!hasFocus) {
                    if (textView != null) {
                        textView.setSelected(false);
                    }
                    Log.e("onFocusChange", "has no focus,set selected false");
                }
            }
        });

        mGridView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mposition = position;
                Log.e("onItemSelected", "come in onitemselected");
                /*
                 * if(view.findViewById(R.id.file_name) != null){ TextView
                 * filename = (TextView) view.findViewById(R.id.file_name); }
                 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("onItemSelected", "onNothingSelected =============================>");
                mposition = -1;

            }
        });
    }

    /**
     * @Title: openFile.
     * @Description: open the selected file with the default tools.
     * @param: @param file.
     * @return: void.
     * @throws
     */
    private void openFile(DownloadGridItemBean file) {
        if (this.mType == DownloadedClassifyActivity.OTHERINDEX) {
            Toast.makeText(mActivity, mActivity.getString(R.string.unfound_tools_open), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!new File(file.getFullSavePath()).exists()) {
            Toast.makeText(mActivity, mActivity.getString(R.string.unfound_tools_open), Toast.LENGTH_SHORT).show();
            return;

        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        DataProxy.Mimetype mimetype = DataProxy.MAP_MIMETYPE.get(Utils.getTypeFromName(file.getSaveName()));
        if (mimetype != null) {
            intent.setDataAndType(Uri.fromFile(new File(file.getFullSavePath())), mimetype.mineType);
        }
        try {
            mActivity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mActivity, mActivity.getString(R.string.unfound_tools_open), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @Title: setAdapter.
     * @Description: set the data to grid.
     * @param: @param Infos.
     * @return: void.
     * @throws
     */
    public void setAdapterDatas(List<DownloadGridItemBean> Infos) {
        this.mDownloadInfos = Infos;
        if (mDownloadInfos == null || mDownloadInfos.size() <= 0) {
            mEmptyLayout.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mMenuTipView.setVisibility(View.GONE);
            return;
        } else {
            mEmptyLayout.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            mMenuTipView.setVisibility(View.VISIBLE);
        }
        if (Constants.isDebug) {
            if (mDownloadInfos != null && mDownloadInfos.size() > 0) {
                for (int i = 0; i < mDownloadInfos.size(); i++) {
                    Log.i(TAG, "item[" + i + "]==" + mDownloadInfos.get(i).toString() + "\n");
                }
            }
        }
        if (mAdapter == null) {
            mAdapter = new DownloadedClassifyAdapter();
            mGridView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        setListener();
    }

    /**
     * delete the file
     */
    public void deleteSelectedItemOk() {
        if (!isMutilDeletemode) {
            deleteSingleitemOk();
        } else {
            deleteMutilItemOk();
        }
    }

    /**
     * @Title: startDeleteSingleMode.
     * @Description: start delete item.
     * @param: .
     * @return: void.
     * @throws
     */
    public void deleteSingleitemConfirm() {
        Log.i("tag", "====deleteSingleitem()=====");
        if (mDownloadInfos.size() == 0) {
            mMenuTipView.setVisibility(View.GONE);
            return;
        }
        DownloadGridItemBean bean = mDownloadInfos.get(mposition);
        mActivity.goToActivity(isDeleteMode, bean);

    }

    public void deleteSingleitemOk() {
        final DownloadInfor info = mDownloadInfos.get(mposition).getDownloadInfor();
        mDownloadInfos.remove(mposition);
        mAdapter.notifyDataSetChanged();
        DocumentApplication.getInstance().addTask(new Runnable() {

            @Override
            public void run() {
                // delete the file
                mDataProxy.deleteDownload(info);
            }
        });
    }

    /**
     * @Title: startDeleteAllMode.
     * @Description: start delete mode.
     * @param: .
     * @return: void.
     * @throws
     */
    public void startMutilDeleteMode() {
        Log.i("tag", "====startMutilDeleteMode()=====");
        if (mDownloadInfos.size() == 0) {
            return;
        }
        isSingleDeletemode = false;
        isMutilDeletemode = true;
        if (selectedItemList == null) {
            selectedItemList = new ArrayList<HashMap<String, DownloadInfor>>();
        }
        if (positionList == null) {
            positionList = new ArrayList<HashMap<String, Integer>>();
        }

        for (int i = 0; i < mDownloadInfos.size(); i++) {
            mDownloadInfos.get(i).setMutilDeleteMode(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 查看点击的item详情
     */
    public void startDatileMode() {
        Log.i("tag", "====startDatileMode()=====");
        isMutilDeletemode = false;
        DownloadGridItemBean bean = mDownloadInfos.get(mposition);
        mActivity.goToActivity(isDetailMode, bean);
    }

    /*
     * 多选模式下：
     */

    public void deleteMutilItemConfirm() {
        Log.i("tag", "=====deleteMutilItem()=====>");
        mActivity.goToActivity(isDeleteMode, null);
    }

    /**
     * 删除多选的item,或者删除全部item
     */
    @SuppressLint("ShowToast")
    public void deleteMutilItemOk() {
        Log.i("tag", "=====deleteMutilItemOK()=====>");

        if (selectedItemList.size() == 0 || selectedItemList == null) {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.nothing_selected), 1000).show();
            return;
        } else {
            Log.i("tag", "==deleteMutilItem()===mDownloadInfos===before==>" + mDownloadInfos.size());
            DocumentApplication.getInstance().addTask(new Runnable() {

                @Override
                public void run() {
                    List<DownloadGridItemBean> tmp = new ArrayList<DownloadGridItemBean>();
                    List<Long> tmp1 = new ArrayList<Long>();
                    for (int i = 0; i < mGridView.getChildCount(); i++) {
                        CheckBox cbox = (CheckBox) mGridView.getChildAt(i).findViewById(
                                R.id.downloaded_type_item_checkbox);
                        Log.i("tag1", "mGridView.getChildCount()" + mGridView.getChildCount());
                        Log.i("tag1", "mDownloadInfos.size()" + mDownloadInfos.size());

                        if (cbox.isChecked()) {
                            // mDataProxy.deleteDownload(mDownloadInfos.get(i).getDownloadInfor());
                            tmp.add(mDownloadInfos.get(i));
                            tmp1.add(mDownloadInfos.get(i).getDownloadInfor().id);
                        }
                    }
                    for (int m = 0; m < tmp1.size(); m++) {
                        downloadManager.remove(tmp1.get(m));
                    }

                }
            });
        }
        selectedItemList.clear();
        positionList.clear();
        mSelectCount = -1;
        Log.i("tag", "=====positionList===after==>" + positionList.size());
        isMutilDeletemode = false;
        if (mDownloadInfos.size() == 0) {
            return;
        }
        for (int i = 0; i < mDownloadInfos.size(); i++) {
            mDownloadInfos.get(i).setMutilDeleteMode(false);
            Log.i("tag", "==================>" + mDownloadInfos.get(i));
        }
        mActivity.getValuesFromMimetype();
        mActivity.setValuesToUI();
        Log.i("xixi", "==deleteMutilItem()===mDownloadInfos===after==>" + mDownloadInfos.size());
    }

    /**
     * 取消删除多个item，checkbox全不勾选,已勾选的全部取消删除
     */
    public void cancleDeleteMutilItem() {
        Log.i("tag", "=====cancleDeleteMutilItem()=====>");
        if (selectedItemList.size() == 0 || selectedItemList == null) {
            return;
        }
        Log.i("tag", "==cancleDeleteMutilItem()===positionList===before==>" + positionList.size());
        for (int i = 0; i < mGridView.getCount(); i++) {
            CheckBox cbox = (CheckBox) mGridView.getChildAt(i).findViewById(R.id.downloaded_type_item_checkbox);
            if (cbox.isChecked()) {
                cbox.setChecked(false);
            }
        }
        mAdapter.notifyDataSetChanged();
        selectedItemList.clear();
        positionList.clear();
        mSelectCount = -1;
        Log.i("tag", "==cancleDeleteMutilItem()===positionList===after==>" + positionList.size());
    }

    /**
     * checkbox全选，全部删除;
     */
    public void deleteAllItem() {
        Log.i("tag", "=====deleteAllItem()=====>");
        HashMap<String, DownloadInfor> selectedMaps = new HashMap<String, DownloadInfor>();
        HashMap<String, Integer> positionMaps = new HashMap<String, Integer>();
        positionList.clear();
        selectedItemList.clear();
        for (int i = 0; i < mGridView.getCount(); i++) {
            CheckBox cbox = (CheckBox) mGridView.getChildAt(i).findViewById(R.id.downloaded_type_item_checkbox);
            cbox.setChecked(true);
            selectedMaps.put("info", mDownloadInfos.get(i).getDownloadInfor());
            positionMaps.put("position", i);
            positionList.add(positionMaps);
            selectedItemList.add(selectedMaps);
        }
        Log.e("fatal", "mGridView.getCount() == " + mGridView.getCount());
        Log.e("fatal", "mDownloadInfos.size() == " + mDownloadInfos.size());

        mSelectCount = mGridView.getCount() - 1;
        deleteMutilItemConfirm();
    }

    /**
     * @Title: setTypeString.
     * @Description: set type text.
     * @param: @param type.
     * @return: void.
     * @throws
     */
    public void setType(int type) {
        this.mType = type;
        String typeString = "";
        switch (type) {
            case DownloadedClassifyActivity.VIDEOINDEX:
                typeString = mActivity.getString(R.string.downloaded_vedio);
                break;
            case DownloadedClassifyActivity.MUSICINDEX:
                typeString = mActivity.getString(R.string.downloaded_music);
                break;
            case DownloadedClassifyActivity.PICTRUEINDEX:
                typeString = mActivity.getString(R.string.downloaded_pictrue);
                break;
            case DownloadedClassifyActivity.DOCUMENTINDEX:
                typeString = mActivity.getString(R.string.downloaded_document);
                break;
            case DownloadedClassifyActivity.APKINDEX:
                typeString = mActivity.getString(R.string.downloaded_package);
                break;
            case DownloadedClassifyActivity.OTHERINDEX:
                typeString = mActivity.getString(R.string.downloaded_other);
                break;
            default:
                break;
        }
        mDownloadedType.setText(typeString);
    }

    /**
     * @Title: getIconFromType.
     * @Description: get the default icon from type.
     * @param: @return.
     * @return: int.
     * @throws
     */
    private int getIconFromType() {
        int icon = R.drawable.video_default_icon;
        switch (mType) {
            case DownloadedClassifyActivity.VIDEOINDEX:
                icon = R.drawable.video_default_icon;
                break;
            case DownloadedClassifyActivity.MUSICINDEX:
                icon = R.drawable.music_default_icon;
                break;
            case DownloadedClassifyActivity.PICTRUEINDEX:
                icon = R.drawable.image_default_icon;
                break;
            case DownloadedClassifyActivity.DOCUMENTINDEX:
                icon = R.drawable.document_default_icon;
                break;
            case DownloadedClassifyActivity.APKINDEX:
                icon = R.drawable.apk_default_icon;
                break;
            case DownloadedClassifyActivity.OTHERINDEX:
                icon = R.drawable.other_default_icon;
                break;
            default:
                break;
        }
        return icon;
    }

    /**
     * @ClassName: DownloadedClassifyAdapter.
     * @Description:DownloadedClassifyAdapter.
     * @author: lucky.li.
     * @date: Oct 9, 2015 9:21:45 AM.
     * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
     */
    class DownloadedClassifyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDownloadInfos.size();
        }

        @Override
        public DownloadGridItemBean getItem(int position) {
            return mDownloadInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener(); 
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.new_activity_download_type_item_layout,
                        null);
            }
            ImageView thumbnail = ViewHolder.get(convertView, R.id.grid_item_thumbnail);
            TextView fileSizeView = ViewHolder.get(convertView, R.id.file_size);
            TextView fileNameView = ViewHolder.get(convertView, R.id.file_name);
            CheckBox cbox = ViewHolder.get(convertView, R.id.downloaded_type_item_checkbox);
            // cbox.setBackgroundColor(color)
            ImageView iconView = ViewHolder.get(convertView, R.id.file_type_image);

            String imagePath = getItem(position).getFullSavePath();

            if (mType == DownloadedClassifyActivity.PICTRUEINDEX) {
                iconView.setVisibility(View.INVISIBLE);
                if (!imagePath.equals(thumbnail.getTag())) {
                    thumbnail.setTag(imagePath);
                    // set pictures' height and width
                    // get dp from dimens to px
                    final int btheight = mActivity.getResources().getDimensionPixelSize(R.dimen.img_photo_height);
                    final int btwidth = mActivity.getResources().getDimensionPixelSize(R.dimen.img_photo_width);
                    ViewGroup.LayoutParams lp;
                    lp = thumbnail.getLayoutParams();
                    lp.height = btheight;
                    lp.width = btwidth;
                    thumbnail.setLayoutParams(lp);
                    ImageLoader.getInstance().displayImage("file://"+imagePath, thumbnail, mOptions, animateFirstListener);  
                    //ViewGroup.LayoutParams lp;
                    //lp = thumbnail.getLayoutParams();
                    //lp.height = btheight;
                    //lp.width = btwidth;
                    //thumbnail.setLayoutParams(lp);
                    //mBitmap = BitmapFactory.decodeFile(imagePath);
                    //mThumbnaildraw = new BitmapDrawable(mBitmap);
                    //thumbnail.setBackground(mThumbnaildraw);
                    
                }
            } else if (mType == DownloadedClassifyActivity.APKINDEX) {
                PackageManager pm = mActivity.getPackageManager();
                try {
                    PackageInfo packageinfo = pm.getPackageArchiveInfo(imagePath, PackageManager.GET_ACTIVITIES);
                    if (packageinfo != null) {
                        ApplicationInfo appInfo = packageinfo.applicationInfo;
                        ApplicationInfo appInfo2 = packageinfo.applicationInfo;
                        appInfo.sourceDir = imagePath;
                        appInfo.publicSourceDir = imagePath;
                        Drawable icon = appInfo2.loadIcon(pm);
                        // BitmapDrawable bitmapDrawable = (BitmapDrawable)
                        // icon;
                        // Bitmap bitmap = bitmapDrawable.getBitmap();

                        final int btheight = mActivity.getResources().getDimensionPixelSize(R.dimen.img_photo_height);
                        final int btwidth = mActivity.getResources().getDimensionPixelSize(R.dimen.img_photo_width);
                        ViewGroup.LayoutParams lp;
                        lp = thumbnail.getLayoutParams();
                        lp.height = btheight;
                        lp.width = btwidth;
                        thumbnail.setLayoutParams(lp);

                        thumbnail.setBackgroundDrawable(icon);
                        iconView.setVisibility(View.INVISIBLE);

                    }
                } catch (Exception e) {
                    Log.e("error", "getPackageArchiveInfo Exception!");
                    thumbnail.setBackground(mActivity.getResources().getDrawable(R.drawable.griditemview_bg));
                    iconView.setBackgroundResource(getIconFromType());
                }

            } else {
                thumbnail.setBackground(mActivity.getResources().getDrawable(R.drawable.griditemview_bg));
                iconView.setBackgroundResource(getIconFromType());
            }

            fileSizeView.setText(Utils.formatFileSize(getItem(position).getFileSize()));
            if (TextUtils.isEmpty(getItem(position).getSaveName())) {
                fileNameView.setText(Utils.getFileName(getItem(position).getFullURL()));
            } else {
                fileNameView.setText(getItem(position).getSaveName());
            }
            if (getItem(position).isMutilDeleteMode()) {
                cbox.setVisibility(View.VISIBLE);
            } else {
                cbox.setVisibility(View.INVISIBLE);
                cbox.setChecked(false);
            }
            return convertView;
        }

    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {  
        
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());  
  
        @Override  
        public void onLoadingComplete(String imageUri, View view, Object loadedImage) {  
            if (loadedImage != null) {  
                ImageView imageView = (ImageView) view;  
                // 是否第一次显示  
                boolean firstDisplay = !displayedImages.contains(imageUri);  
                if (firstDisplay) {  
                    // 图片淡入效果  
                    FadeInBitmapDisplayer.animate(imageView, 500);  
                    displayedImages.add(imageUri);  
                }  
            }  
        }  
    }  
    
    public Bitmap getMbitmap() {
        return mBitmap;
    }

    public void setMbitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

}
