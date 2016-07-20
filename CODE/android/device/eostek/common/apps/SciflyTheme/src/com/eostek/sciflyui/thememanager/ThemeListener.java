
package com.eostek.sciflyui.thememanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import scifly.dm.EosDownloadListener;
import scifly.dm.EosDownloadManager;
import scifly.dm.EosDownloadTask;
import scifly.thememanager.IThemeChangeListener;
import android.app.AlertDialog;
import android.app.DownloadManager.Request;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.sciflyui.thememanager.download.IDownloadProgressListener;
import com.eostek.sciflyui.thememanager.download.IUpgradeStateListener;
import com.eostek.sciflyui.thememanager.task.ThemeModel;
import com.eostek.sciflyui.thememanager.ui.TransparentDialog;
import com.eostek.sciflyui.thememanager.util.Constant;
import com.eostek.sciflyui.thememanager.util.ThemeManagerUtils;

/**
 * @author Admin
 */
public class ThemeListener {

    /**
     * TAG.
     */
    protected static final String TAG = "ThemeListener";

    private ThemeDisplayAct mActivity = null;

    private ThemeHolder mHolder = null;

    private static boolean isDownloading = false;

    private int mSelectedPosition = 0;

    /**
     * mWarning AlertDialog.
     */
    protected AlertDialog mWarning = null;

    private TransparentDialog mChangeThemeWaittingDialog;

    private Context mContext;

    private EosDownloadManager mManager;

    private EosDownloadTask mTask;

    private int mPercent;

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public EosDownloadManager getmManager() {
        return mManager;
    }

    public void setmManager(EosDownloadManager mManager) {
        this.mManager = mManager;
    }

    public EosDownloadTask getmTask() {
        return mTask;
    }

    public void setmTask(EosDownloadTask mTask) {
        this.mTask = mTask;
    }

    public int getmPercent() {
        return mPercent;
    }

    public void setmPercent(int mPercent) {
        this.mPercent = mPercent;
    }

    /**
     * set selected the position.
     * 
     * @param selectedPosition selected the position.
     */
    public final void setSelectedPosition(int selectedPosition) {
        this.mSelectedPosition = selectedPosition;
    }

    /**
     * constructor.
     * 
     * @param activity context.
     * @param holder views.
     */

    public ThemeListener(ThemeDisplayAct activity, ThemeHolder holder) {
        this.mActivity = activity;
        this.mHolder = holder;
    }

    private ProgressBar mCurrentProgressBar;

    /**
     * monitor delete event.
     */
    protected OnClickListener deleteThemeListener = new OnClickListener() {
        public void onClick(View arg0) {
            boolean isDeleteSuccessed = ThemeManagerUtils.deleteTheme(mActivity, mSelectedPosition);
            if (isDeleteSuccessed) {
                Log.i(TAG, "deleting successed");
                mActivity.mHandler.sendEmptyMessage(ThemeDisplayAct.UPGRADE_GRIDVIEW);
                mActivity.mSelected = mSelectedPosition;
            }
            mActivity.mDeleteThemeDialog.dismiss();
        }
    };

    /**
     * monitor cancel delete theme event.
     */
    protected OnClickListener deleteThemeCancelListener = new OnClickListener() {
        public void onClick(View arg0) {
            mActivity.mDeleteThemeDialog.dismiss();
        }
    };

    /**
     * monitor download progress.
     */
    protected class ThemeDownloadProgressListener implements IDownloadProgressListener {

        int mPosition = 0;

        /**
         * @param position position
         */
        public ThemeDownloadProgressListener(int position) {
            super();
            this.mPosition = position;
        }

        @Override
        public void onDownloadSizeChange(int taskId, final int i) {
            // mThemes.get(current).progress = i;
            if (i == Constant.RESOURCE_TOTAL_SIZE) {
                Log.i(TAG, "download finished");
                mActivity.mThemes.get(mPosition).downloadIsCompleted = true;
                mActivity.mThemes.get(mPosition).mType = ThemeModel.TYPE.LOCAL;
            }

            // shielded by youpeng.wan
            // mHandler.sendEmptyMessage(1);

            if (mCurrentProgressBar != null) {
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mCurrentProgressBar.setProgress(i);
                    }
                });
            }
        }
    }

    /**
     * update state.
     */
    protected class ThemeUpgradeStateListener implements IUpgradeStateListener {

        private int mPosition = 0;

        /**
         * @param position position
         */
        public ThemeUpgradeStateListener(int position) {
            super();
            this.mPosition = position;

        }

        @Override
        public void onResume(int taskId, String string) {

            Log.i(TAG, "onResume");
        }

        @Override
        public void onPreparedSuccess(int taskId, String string) {
            isDownloading = true;
            Log.i(TAG, "onPreparedSuccess");
        }

        @Override
        public void onPreparedFailed(int taskId, String string) {
            Log.i(TAG, "onPreparedFailed");
            isDownloading = false;
        }

        @Override
        public void onPause(int taskId, String string) {

            Log.i(TAG, "onPause");
        }

        @Override
        public void onInstalledSuccess(int taskId, String string) {

            Log.i(TAG, "onInstalledSuccess");
        }

        @Override
        public void onInstalledFailed(int taskId, String string) {
            Log.i(TAG, "onInstalledFailed");
        }

        @Override
        public void onDownloadedSuccess(int taskId, String localUrl) {

            isDownloading = false;
            // ThemeManager.getInstance().changeTheme(localUrl, listener);
            mActivity.mThemes.get(mPosition).mType = ThemeModel.TYPE.LOCAL;
            mActivity.setSelected(mPosition);
            mActivity.mHandler.sendEmptyMessage(ThemeDisplayAct.UPGRADE_GRIDVIEW);
            setItemHoverListener();

        }

        @Override
        public void onDownloadFailed(int taskId, String string) {
            isDownloading = false;
            mActivity.mThemes.get(mPosition).isError = true;
        }

        @Override
        public void onCleanSuccess(int taskId, String string) {
        }

        @Override
        public void onCleanFailed(int taskId, String string) {

        }

    }

    private IThemeChangeListener mIThemeChangeListener = new IThemeChangeListener.Stub() {

        @Override
        public void onSucceed() throws RemoteException {
            Log.i(TAG, "Theme changed succeed.");
        }

        @Override
        public void onStatus(String arg0) throws RemoteException {

        }

        @Override
        public void onFailed(String arg0) throws RemoteException {
            Log.i(TAG, "Theme changed failed.");
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mWarning = new AlertDialog.Builder(mActivity).setMessage(R.string.warning)
                            .setPositiveButton(R.string.confirm, null).show();

                }
            });
        }
    };

    /**
     * monitor gridView event.
     */
    public void setListeners() {
        mHolder.getGirdview().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, "id = " + position);

                final ThemeModel selectedModel = mActivity.mThemes.get(position);
                if (selectedModel != null) {

                    if (selectedModel.mType == ThemeModel.TYPE.LOCAL || selectedModel.mType == ThemeModel.TYPE.DEFAULT) {

                        mActivity.setSelected(position);

                        if (!selectedModel.equals(mActivity.mCurrentThemeModel)) {

                            // mHolder.changeWallpaperWatting.setVisibility(View.VISIBLE);
                            mChangeThemeWaittingDialog = new TransparentDialog(mActivity);
                            mChangeThemeWaittingDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // switch theme
                                    // ThemeManager.getInstance().changeTheme(selectedModel.mLocalUrl,
                                    // mIThemeChangeListener);
                                    try {
                                        Log.d(TAG, "selectedModel.mLocalUrl=" + selectedModel.mLocalUrl);
                                        upZipFile(selectedModel.mLocalUrl, "/data/eostek/");
                                    } catch (ZipException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    changeWallpaper(selectedModel);
                                    mActivity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            mActivity.changeBackgound();
                                            Toast.makeText(mActivity, R.string.change_success, Toast.LENGTH_LONG)
                                                    .show();
                                            mChangeThemeWaittingDialog.dismiss();
                                        }
                                    });

                                }

                                private static final int BUFF_SIZE = 1024 * 1024; // 1M
                                                                                  // Byte

                                public void deleteAllFiles(File file, boolean delParent) {

                                    if (file.exists()) {
                                        if (file.isFile()) {
                                            file.delete();
                                        } else {
                                            File[] files = file.listFiles();
                                            for (File f : files) {
                                                if (file.isFile()) {
                                                    file.delete();
                                                } else {
                                                    deleteAllFiles(f, true);
                                                }
                                            }

                                            if (delParent) {
                                                file.delete();
                                            }
                                        }
                                    }
                                }

                                public void upZipFile(String source, String destPath) throws ZipException, IOException {
                                    // delete folder and files.
                                    File file = new File(destPath);
                                    try {
                                        deleteAllFiles(file, false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // dest path is same as the zip file
                                    File destDir = new File(destPath);
                                    if (!destDir.exists()) {
                                        destDir.mkdirs();

                                        // change access right of folder.
                                        FileUtils.setPermissions(source, 0777, -1, -1);
                                    }

                                    ZipFile zf = new ZipFile(source);
                                    for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
                                        ZipEntry entry = ((ZipEntry) entries.nextElement());

                                        String str = destPath + File.separator + entry.getName();

                                        if (entry.isDirectory()) {
                                            File desFile = new File(str);
                                            if (!desFile.exists()) {
                                                desFile.mkdirs();

                                                // change access right of
                                                // folder.
                                                FileUtils.setPermissions(str, 0777, -1, -1);

                                            }
                                        } else {
                                            // unzip file.
                                            InputStream in = zf.getInputStream(entry);
                                            File desFile = new File(str);
                                            if (!desFile.exists()) {
                                                File fileParentDir = desFile.getParentFile();
                                                if (!fileParentDir.exists()) {
                                                    fileParentDir.mkdirs();
                                                    FileUtils.setPermissions(fileParentDir.getAbsolutePath(), 0777, -1,
                                                            -1);
                                                }

                                                desFile.createNewFile();

                                                // change access right of file.
                                                FileUtils.setPermissions(str, 0777, -1, -1);
                                            }

                                            OutputStream out = new FileOutputStream(desFile);
                                            byte buffer[] = new byte[BUFF_SIZE];
                                            int realLength;
                                            while ((realLength = in.read(buffer)) > 0) {
                                                out.write(buffer, 0, realLength);
                                            }
                                            out.flush();
                                            in.close();
                                            out.close();
                                            in = null;
                                            out = null;

                                        }
                                    }
                                    zf.close();
                                }

                            }).start();

                            ImageView mCurrent = null;
                            if (mActivity.mCurrentView != null) {
                                mCurrent = (ImageView) mActivity.mCurrentView.findViewById(R.id.selected);
                            }
                            final ImageView mSelected = (ImageView) view.findViewById(R.id.selected);
                            final ImageView current = mCurrent;
                            mActivity.mCurrentThemeModel = selectedModel;
                            mActivity.mCurrentView = view;

                            /**
                             * refresh selected icon in current view and
                             * selected view
                             */
                            mActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    mSelected.setVisibility(View.VISIBLE);
                                    if (current != null) {
                                        current.setVisibility(View.GONE);
                                    }
                                }
                            });

                        }

                    } else if (selectedModel.mType == ThemeModel.TYPE.ONLINE) {
                        Log.i(TAG, "isDownloading " + isDownloading);
                        if (isDownloading) {
                            Toast.makeText(mActivity, R.string.downloading_tip, Toast.LENGTH_LONG).show();
                            return;
                        }
                        isDownloading = true;

                        if (selectedModel.mDownloadUrl == null || "".equals(selectedModel.mDownloadUrl)) {
                            isDownloading = false;
                            Toast.makeText(mActivity, R.string.no_downloadurl_tip, Toast.LENGTH_LONG).show();
                            return;
                        }

                        mActivity.setSelected(position);

                        mCurrentProgressBar = (ProgressBar) view.findViewById(R.id.progress);
                        mCurrentProgressBar.setVisibility(View.VISIBLE);

                        final TextView downloadInfo = (TextView) view.findViewById(R.id.item_textview);
                        ImageView download = (ImageView) view.findViewById(R.id.download);

                        // 1.download theme.
                        downloadInfo.setText(R.string.downloading);
                        Request request = new Request(Uri.parse(selectedModel.mDownloadUrl));
                        // Log.i("test", selectedModel.mLocalUrl);
                        // Log.i("test", selectedModel.mDownloadUrl);
                        request.setDestinationInExternalPublicDir(Constant.PREFIX, "/" + selectedModel.mTitle + "_"
                                + selectedModel.mThemeVersion + ".zip");
                        // 打印u盘路径
                        File file = Environment.getExternalStoragePublicDirectory(Constant.PREFIX);
                        Log.d(TAG, "download path=" + file.getAbsolutePath());
                        EosDownloadListener listener = new EosDownloadListener() {

                            @Override
                            public void onDownloadStatusChanged(int status) {
                                // TODO Auto-generated method stub
                                Log.i("test", "onDownloadStatusChanged: " + status);

                                if (status == 4) {
                                    // Toast.makeText(getmContext(),
                                    // R.string.info,
                                    // Toast.LENGTH_SHORT).show();
                                    isDownloading = false;
                                    Log.i("test", "-------------> 4");
                                    mActivity.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            Toast.makeText(mActivity, R.string.download_fail_tip, Toast.LENGTH_SHORT)
                                                    .show();
                                            downloadInfo.setText(R.string.info);
                                            mManager.removeTask(mTask.getTaskId());
                                            mCurrentProgressBar.setProgressDrawable(mActivity.getResources()
                                                    .getDrawable(R.drawable.progress_pause));
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onDownloadSize(long size) {
                                // TODO Auto-generated method stub
                                Log.i("test", "onDownloadSize: " + size);
                            }

                            @Override
                            public void onDownloadComplete(final int percent) {
                                // TODO Auto-generated method stub
                                Log.i("test", "onDownloadComplete: " + percent);
                                mPercent = percent;
                                mActivity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        mCurrentProgressBar.setProgress(percent);
                                        if (percent == 100) {
                                            isDownloading = false;
                                            downloadInfo.setText(selectedModel.mTitle);
                                            mCurrentProgressBar.setProgress(0);
                                            selectedModel.mType = ThemeModel.TYPE.LOCAL;
                                        }
                                    }
                                });
                            }
                        };
                        mTask = new EosDownloadTask(request, listener);
                        mManager = new EosDownloadManager(mContext);
                        long num = mManager.addTask(mTask);
                        download.setVisibility(View.GONE);
                        Log.i("test", "" + num);
                    }
                }
            }

            private synchronized void changeWallpaper(ThemeModel selectedModel) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(mActivity);

                Bitmap bitmap = ThemeManagerUtils.getWallpaperFromZip(selectedModel.mLocalUrl);
                try {
                    if (bitmap != null) {
                        Log.i(TAG, "set wallpaper");
                        wallpaperManager.setBitmap(bitmap);
                        mActivity.mHandler.sendEmptyMessage(ThemeDisplayAct.RESTART_SCIFLY_VIDEO);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mHolder.getGirdview().setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // mActivity.setSelected(arg2);
                Log.i(TAG, "" + arg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = mActivity.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * setItemHoverListener.
     */
    public void setItemHoverListener() {
        Log.i(TAG, "mHolder.getGirdview().getCount() " + mHolder.getGirdview().getCount());
        final ViewTreeObserver observer = mHolder.getGirdview().getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!observer.isAlive()) {
                    ViewTreeObserver observer = mHolder.getGirdview().getViewTreeObserver();
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeOnGlobalLayoutListener(this);
                }
                for (int i = 0; i < mHolder.getGirdview().getCount(); i++) {
                    View child = mHolder.getGirdview().getChildAt(i);
                    Log.i(TAG, "child== null?" + (child == null));
                    if (child != null) {
                        Log.i(TAG, "child i" + i);
                        child.setOnHoverListener(new ThemeHoverListener());
                    }
                }
            }
        });
        mHolder.getMainLayout().setOnHoverListener(new ThemeHoverListener());
        mHolder.getGirdview().setOnHoverListener(new ThemeHoverListener());
    }

    /**
     * @author admin
     */
    public class ThemeHoverListener implements OnHoverListener {
        @Override
        public boolean onHover(View view, MotionEvent motionEvent) {
            mActivity.setHoveredView(view);
            return true;
        }
    }

    /**
     * get Change Theme Waitting Dialog.
     * 
     * @return TransparentDialog
     */
    public TransparentDialog getChangeThemeWaittingDialog() {
        return mChangeThemeWaittingDialog;
    }

}
