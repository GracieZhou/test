
package com.eostek.documentui.ui;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.eostek.documentui.R;
import com.eostek.documentui.Constants;
import com.eostek.documentui.DocumentsActivity;
import com.eostek.documentui.data.DataProxy;
import com.eostek.documentui.data.DownloadInfor;
import com.eostek.documentui.model.DownloadGridItemBean;
import com.eostek.documentui.util.Utils;
import com.eostek.documentui.view.DeleteModeDialog;
import com.google.common.collect.Lists;

/**
 * @ClassName: DownloadedClassifyActivity.
 * @Description:used to dispaly the files that were downloaded.
 * @author: lucky.li.
 * @date: Oct 8, 2015 4:31:40 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class DownloadedClassifyActivity extends Activity {
    private final String TAG = "DownloadedClassifyActivity";

    /**
     * downloaded infos
     */
    private List<DownloadInfor> mDownloadedInfors;

    /**
     * downloaded vedio file
     */
    private List<DownloadGridItemBean> mDownloadedVideoInfos;

    /**
     * downloaded music file
     */
    private List<DownloadGridItemBean> mDownloadedMusicInfos;

    /**
     * downloaded pictrue file
     */
    private List<DownloadGridItemBean> mDownloadedPictrueInfos;

    /**
     * downloaded document file
     */
    private List<DownloadGridItemBean> mDownloadedDocumentInfos;

    /**
     * downloaded package file
     */
    private List<DownloadGridItemBean> mDownloadedPackageInfos;

    /**
     * downloaded other file
     */
    private List<DownloadGridItemBean> mDownloadedOtherInfos;

    private DataProxy mDataProxy;

    private DownloadedClassifyHolder mHolder;

    private int mType = 0;

    public static final int VIDEOINDEX = 0;

    public static final int MUSICINDEX = 1;

    public static final int PICTRUEINDEX = 2;

    public static final int DOCUMENTINDEX = 3;

    public static final int APKINDEX = 4;

    public static final int OTHERINDEX = 5;

    private final int REFRESHMSG = 6;

    private Observer mObserver;

    private String isDeleteMode = "isDeleteMode";

    private String isDetailMode = "isDetailMode";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == REFRESHMSG) {
                setValuesToUI();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_download_classify_layout);
        getWindow().setBackgroundDrawable(null);
        mDataProxy = new DataProxy(this);
        mType = getIntent().getIntExtra("type", 0);
        mHolder = new DownloadedClassifyHolder(this, mDataProxy);
        mHolder.findViews();
        mHolder.setType(mType);
        getValuesFromMimetype();
        setValuesToUI();
        if (mObserver == null) {
            mObserver = new Observer(mHandler);
        }
        getContentResolver().registerContentObserver(DataProxy.BASE_URL, true, mObserver);
    }

    /**
     * @Title: classyAccordingType.
     * @Description: classy to diferent set.
     * @param: .
     * @return: void.
     * @throws
     */
    public void getValuesFromMimetype() {
        this.mDownloadedInfors = mDataProxy.getAllDownload(DataProxy.STATUS_SUCCESS);
        if (mDownloadedInfors == null) {
            return;
        }
        if (Constants.isDebug) {
            Log.i(TAG, "datas==" + mDownloadedInfors.toString());
        }
        if (mDownloadedVideoInfos == null) {
            mDownloadedVideoInfos = Lists.newArrayList();
        }
        if (mDownloadedPictrueInfos == null) {
            mDownloadedPictrueInfos = Lists.newArrayList();
        }
        if (mDownloadedMusicInfos == null) {
            mDownloadedMusicInfos = Lists.newArrayList();
        }
        if (mDownloadedDocumentInfos == null) {
            mDownloadedDocumentInfos = Lists.newArrayList();
        }
        if (mDownloadedPackageInfos == null) {
            mDownloadedPackageInfos = Lists.newArrayList();
        }
        if (mDownloadedOtherInfos == null) {
            mDownloadedOtherInfos = Lists.newArrayList();
        }
        mDownloadedVideoInfos.clear();
        mDownloadedPictrueInfos.clear();
        mDownloadedMusicInfos.clear();
        mDownloadedDocumentInfos.clear();
        mDownloadedPackageInfos.clear();
        mDownloadedOtherInfos.clear();
        for (Iterator<DownloadInfor> iterator = mDownloadedInfors.iterator(); iterator.hasNext();) {
            DownloadInfor info = (DownloadInfor) iterator.next();
            File file = new File(info.fullSavePath);
            if (file.exists()) {
                DataProxy.Mimetype mimetype = DataProxy.MAP_MIMETYPE.get(Utils.getTypeFromName(info.saveName));
                if (mimetype == null) {
                    mDownloadedOtherInfos.add(new DownloadGridItemBean(info));
                } else if ((DataProxy.Mimetype.TYPE_AUDIO).equals(mimetype.customType)) {
                    mDownloadedMusicInfos.add(new DownloadGridItemBean(info));
                } else if ((DataProxy.Mimetype.TYPE_VIDEO).equals(mimetype.customType)) {
                    mDownloadedVideoInfos.add(new DownloadGridItemBean(info));
                } else if ((DataProxy.Mimetype.TYPE_IMAGE).equals(mimetype.customType)) {
                    mDownloadedPictrueInfos.add(new DownloadGridItemBean(info));
                } else if ((DataProxy.Mimetype.TYPE_DOCUMENT).equals(mimetype.customType)) {
                    mDownloadedDocumentInfos.add(new DownloadGridItemBean(info));
                } else if ((DataProxy.Mimetype.TYPE_INSTALLATION_PACKAGE).equals(mimetype.customType)) {
                    mDownloadedPackageInfos.add(new DownloadGridItemBean(info));
                }
            }
        }
    }

    /**
     * @Title: initValues.
     * @Description: set different datas to the gridView According to the type .
     * @param: .
     * @return: void.
     * @throws
     */
    public void setValuesToUI() {
        switch (mType) {
            case VIDEOINDEX:
                mHolder.setAdapterDatas(mDownloadedVideoInfos);
                break;
            case MUSICINDEX:
                mHolder.setAdapterDatas(mDownloadedMusicInfos);
                break;
            case PICTRUEINDEX:
                mHolder.setAdapterDatas(mDownloadedPictrueInfos);
                break;
            case DOCUMENTINDEX:
                mHolder.setAdapterDatas(mDownloadedDocumentInfos);
                break;
            case APKINDEX:
                mHolder.setAdapterDatas(mDownloadedPackageInfos);
                break;
            case OTHERINDEX:
                mHolder.setAdapterDatas(mDownloadedOtherInfos);
                break;
            default:
                break;
        }
    }

    public void goToActivity(String flag, DownloadGridItemBean bean) {

        if (flag.equals(isDeleteMode)) {
            Intent intent = new Intent(DownloadedClassifyActivity.this, DeleteModeDialog.class);
            Bundle bundle = new Bundle();
            bundle.putString("flag", isDeleteMode);
            bundle.putSerializable("bean", bean);
            intent.putExtras(bundle);
            startActivityForResult(intent, 5);
        }
        if (flag.equals(isDetailMode)) {
            Intent intent = new Intent(DownloadedClassifyActivity.this, DeleteModeDialog.class);
            Bundle bundle = new Bundle();
            bundle.putString("flag", isDetailMode);
            bundle.putSerializable("bean", bean);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
         * 单选模式下：
         */
        if (requestCode == 2) {
            if (resultCode == DocumentsActivity.RESULT_OK) {
                // 进入单选模式默认删除焦点所在的item
                mHolder.deleteSingleitemConfirm();
            }
            if (resultCode == DocumentsActivity.RESULT_FIRST_USER) {
                // 进入多选模式
                mHolder.startMutilDeleteMode();
            }
            if (resultCode == 2) {
                // 进入详情模式
                mHolder.startDatileMode();
            }
        }
        /*
         * 多选模式下：
         */
        if (requestCode == 3) {
            if (resultCode == DocumentsActivity.RESULT_OK) {
                // 删除多选的item
                mHolder.deleteMutilItemConfirm();
            }
            if (resultCode == 3) {
                // 取消多选
                mHolder.cancleDeleteMutilItem();
            }
            if (resultCode == DocumentsActivity.RESULT_FIRST_USER) {
                // 全选，全部删除
                mHolder.deleteAllItem();
            }
        }
        /**
         * 删除对话框点击确认删除
         */
        if (requestCode == 5 && resultCode == DocumentsActivity.RESULT_OK) {
            mHolder.deleteSelectedItemOk();
        }

    }

    private class Observer extends ContentObserver {

        public Observer(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (Constants.isDebug) {
                Log.i(TAG, "ContentObserver-----onChange-----");
            }
            if (!mHolder.isMutilDeletemode()) {
                getValuesFromMimetype();
                mHandler.sendEmptyMessage(REFRESHMSG);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver);
        try {
            if (mHolder.getMbitmap() != null && !mHolder.getMbitmap().isRecycled()) {
                mHolder.getMbitmap().recycle();
            }else if(mHolder.getMbitmap() != null){
                mHolder.setMbitmap(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
