
package com.android.packageinstaller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageParser;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewDebug.FlagToString;
import android.widget.Toast;
import com.android.internal.content.PackageHelper;
import com.android.packageinstaller.blockinstallation.BlockListAction;
import com.android.packageinstaller.blockinstallation.HttpUtil;
import com.android.packageinstaller.blockinstallation.db.DBManager;

import scifly.app.securelist.AppInfoChecker;
import scifly.app.common.Commons;

public class PakageInstallerLogic {

    private PackageInstallerActivity mContext;

    private PakageInstallerHolder mHolder;

    List<PackageModel> mApkList;

    List<PackageModel> mSelectedApkList;
    
    List<String> mEntriesNames;

    private final String TAG = "PakageInstallerLogic";

    private int mApkInstallSuccessNum = 0;
    
    private int mCurrentApkIndex = 0;
    
    public boolean isParseCompleted = false;
    
    private PackageModel mCurrentInstallApk;
    
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

    public static final String UNZIP_RELATIVE_PATH = "/apks";

    public static final String PREFIX = "/Android/data/com.android.packageinstaller/cache/";

    public static final String CACHE_PATH = Environment.getExternalStoragePublicDirectory(PREFIX).getAbsolutePath();

    private AppInfoChecker mAppInfoChecker;

    private Toast mReminderToast ;

    private PackageModel mTargetPkgModel;
    
    private static final String[] mPackageNameNotInstall ={"com.litv.mobile.gp.litv","com.netflix.mediaclient","tv.fourgtv.fourgtv"};

    public PakageInstallerLogic(Context context) {
        this.mContext = (PackageInstallerActivity) context;
        mAppInfoChecker = new AppInfoChecker(context, mHandler);
        mReminderToast = Toast.makeText(mContext, mContext.getString(R.string.reminder_txt), Toast.LENGTH_LONG);
    }

    public int getApk_install_success_num() {
        return mApkInstallSuccessNum;
    }

    public int getCurrent_apk_index() {
        return mCurrentApkIndex;
    }

    public void setmHolder(PakageInstallerHolder mHolder) {
        this.mHolder = mHolder;
    }

    public void parseZip(String zipFile) {

        if (TextUtils.isEmpty(zipFile)) {
            return;
        }

        Log.d(TAG, "Begin to parse zip ... ");
        String destPath = null; 
        try {
            destPath = ZipUtils.getDestPath("", mContext);
        } catch (Exception e) {
            e.printStackTrace();
            mContext.finish();
        }

        // dest path is same as the zip file
        // String destPath = zipFile.substring(0,
        // zipFile.lastIndexOf(File.separator)) + UNZIP_RELATIVE_PATH;
        Log.d(TAG, "destPath : " + destPath);
        File destDir = new File(destPath);
        if (!destDir.exists()) {
            Log.d(TAG, "111" + destDir.mkdirs());
        }

        ZipFile zf;
        try {
            zf = new ZipFile(zipFile);
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
                
                if (!mHolder.isParseThreadStop) {
                    
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    Log.d(TAG, "entry : " + entry.toString());
                    
                    //获取sdcard的可用空间
                    long availableSize = ZipUtils.getSdcardAvailaleSize();
                    long fileSize = entry.getSize();
                    Log.d(TAG, "sdcard available : " + availableSize);
                    Log.d(TAG, "fileSize : " + fileSize);
                    
                    if (availableSize > fileSize) {
                        InputStream in = zf.getInputStream(entry);
                        String str = destPath + File.separator + entry.getName();
                        File desFile = new File(str);
                        Log.d(TAG, "str:" + str);
                        if (!desFile.exists()) {
                            File fileParentDir = desFile.getParentFile();
                            if (!fileParentDir.exists()) {
                                Log.d(TAG, "222" + fileParentDir.mkdirs());
                            }
                            Log.d(TAG, "333" + desFile.createNewFile());
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
                        //一个apk解压完成
                        addToList(getApkByFile(desFile));
                        mHolder.refreshUI();
                    } else {
                        //sdcard空间不足, 停止解压文件
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.no_free_space), Toast.LENGTH_LONG);
                        break;
                    }
                    
                
                } else {
                    //线程需要停止则返回
                    return;
                }
            }
            isParseCompleted = true;
            mHolder.refreshUI();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    public void install(final PackageModel apk) {
        if(null != apk) {
            Log.d(TAG, "installing " + apk.getPkgName());
            mHandler.obtainMessage(REQUEST_INSTALL_APK, apk).sendToTarget();
        }

    }
    private void reallyInstall(final PackageModel apk) {

        Log.d(TAG, "reallyInstall " + apk.getPkgName());

        mCurrentInstallApk = apk;
        mHandler.sendEmptyMessage(1000);
      DBManager db = DBManager.getDBInstance(mContext);
      boolean dataExist = db.isDataExist();
      Log.i(TAG, "dataExist="+dataExist);
      if (!dataExist) {
         for (int i = 0; i < mPackageNameNotInstall.length; i++) {
            db.insertBlockPkg(mPackageNameNotInstall[i]);
        }
    }
      boolean pkgNameExist = db.isPkgNameExist(apk.getPackageInfo().packageName);
      Log.i(TAG, "pkgNameExist="+pkgNameExist);
      if (HttpUtil.isNetworkConnected(mContext)) {
          Log.i(TAG, "NetworkConnected");
          BlockListAction blockListAction = new BlockListAction(mContext);
          blockListAction.getBlockListData();
          }
      if (pkgNameExist) {
          mHandler.sendEmptyMessage(BLOCK_APK);
          return;
      }
        File installFile = new File(apk.getPath());
        final Uri uri = Uri.fromFile(installFile);
        int installFlags = 0;
        final PackageManager pm = mContext.getPackageManager();// 得到包管理实例
        try {
            PackageInfo pi = pm.getPackageInfo(apk.getPkgName(),
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            if(pi != null) {
                installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
            }
        } catch (NameNotFoundException e) {
        }
        if((installFlags & PackageManager.INSTALL_REPLACE_EXISTING )!= 0) {
            Log.w(TAG, "Replacing package:" + apk.getPkgName());
        }
        
        int installLocation = android.provider.Settings.Global.getInt(mContext.getContentResolver(), Global.DEFAULT_INSTALL_LOCATION, PackageHelper.APP_INSTALL_AUTO);
        Log.d(TAG, "install location : " + installLocation);
        if ((installLocation & PackageHelper.APP_INSTALL_INTERNAL) != 0) {
            installFlags |= PackageManager.INSTALL_INTERNAL;
        } else if ((installLocation & PackageHelper.APP_INSTALL_EXTERNAL) != 0) {
            installFlags |= PackageManager.INSTALL_EXTERNAL;
        }
        Log.d(TAG, "install flags : " + installFlags);

        final int flag = installFlags;
        new Thread(new Runnable() {

            @Override
            public void run() {
                pm.installPackage(uri, new IPackageInstallObserver.Stub() {

                    @Override
                    public void packageInstalled(String packageName, int resultcode) throws RemoteException {

                        Log.d(TAG, "observer:" + packageName + ",resultcode:" + resultcode);
                        mHandler.sendEmptyMessage(resultcode);

                    }
                }, flag, apk.getPkgName());// 改方法为隐藏方法
            }
        }).start();

    }

    public void install(List<PackageModel> apks) {
        if (mSelectedApkList != null && mSelectedApkList.size() > 0) {
            mCurrentApkIndex = 0;
            install(mSelectedApkList.get(mCurrentApkIndex));
        }
    }

    public void uninstall(PackageModel app) {

    }

    public List<PackageModel> getmApkList() {
        return mApkList;
    }

    public String getDestPath() {
        String destPath = CACHE_PATH + UNZIP_RELATIVE_PATH;
        return destPath;
    }

    public void getunInstallApkInfo(String destPath) {
        if (TextUtils.isEmpty(destPath)) {
            return;
        }
        File file = new File(destPath);
        File[] files = null;
        if (file.isDirectory()) {
            files = file.listFiles();
        }

        PackageManager pm = mContext.getPackageManager();
        if (files != null) {
            for (File f : files) {
                PackageModel app = getApkByFile(f);
                if (mApkList == null) {
                    mApkList = new ArrayList<PackageModel>();
                }
                if (isApkCanIstall(app)) {
                    if (app != null) {
                        mApkList.add(app);
                    }
                }
            }
        }
        initSelectedApkList();
    }
    
    public void addToList(PackageModel apk) {
        if (mApkList == null) {
            mApkList = new ArrayList<PackageModel>();
        }
        if (mSelectedApkList == null) {
            mSelectedApkList = new ArrayList<PackageModel>();
        }
        //if (isApkCanIstall(apk)) {
            if (apk != null) {
                mApkList.add(apk);
                mSelectedApkList.add(apk);
            }
       // }
    }

    public static long getFileLength(File file) {
        long size = 0;
        if (!file.exists()) {
            return size;
        }
        size += file.length();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                size += getFileLength(f);
            }
        }
        return size;
    }

    public void initSelectedApkList() {
        if (mApkList != null) {
            if (mSelectedApkList == null) {
                mSelectedApkList = new ArrayList<PackageModel>();
            }
            for (PackageModel apk : mApkList) {
                mSelectedApkList.add(apk);
            }
        }
    }

    public static Drawable getApkIcon(String apkPath, Context mContext) {
        Drawable icon = null;
        try {
            ApplicationInfo info = PackageUtil.getApplicationInfo(new File(apkPath));
            Resources pRes = mContext.getResources();
            AssetManager assmgr = new AssetManager();
            assmgr.addAssetPath(apkPath);
            Resources res = new Resources(assmgr, pRes.getDisplayMetrics(), pRes.getConfiguration());
            if (info.icon != 0) {
                icon = res.getDrawable(info.icon);
            }
            info = null;
            pRes = null;
            assmgr = null;
            res = null;
        } catch (Exception e) {
            icon = null;
        }
        return icon;
    }

    /**
     * 
     * @param apk
     * @return 
     * true    1，apk未安装   2，apk安装了但是此版本比已安装版本要高
     * false   1，apk已安装，并且此版本不必已安装的要高     2，路径为空
     */
    public boolean isApkCanIstall(PackageModel apk) {
        String packageName = apk.getPkgName();
        if (packageName == null || "".equals(packageName))
            //路径为空，不需安装
            return false;
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(apk.getPkgName(), PackageManager.GET_UNINSTALLED_PACKAGES);
            //已安装
            if (pi.versionCode >= apk.getVersionCode()) {
                //已安装的版本较高，不需要安装
                return false;
            } else{
                //已安装版本较低
                return true;
            }
        } catch (NameNotFoundException e) {
            //未安装，则需安装
            return true;
        }
    }
    
    public boolean getSingleInstallApk(String path){
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.file_not_exist), Toast.LENGTH_LONG).show();
            return false;
        }
        File file = new File(path);
        PackageModel apk = getApkByFile(file);
        if (apk == null) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.Parse_error_dlg_text), Toast.LENGTH_LONG).show();
            return false;
        }
        if (mApkList == null){
            mApkList = new ArrayList<PackageModel>();
        }
        if (mSelectedApkList == null) {
            mSelectedApkList = new ArrayList<PackageModel>();
        }
        mApkList.clear();
        mSelectedApkList.clear();
        mApkList.add(apk);
        mSelectedApkList.add(apk);
        mHolder.mSlctedApkList = mSelectedApkList;
        mHolder.mApkList = mApkList;
        Log.d(TAG, "" + mSelectedApkList.toString());
        return true;
    }
    
    
    /**
     * 根据apk路径获取apk
     * @param str
     */
    public PackageModel getApkByFile(File file){
        PackageManager pm = mContext.getPackageManager();
        PackageInfo packageinfo = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        
        if (packageinfo != null) {
            PackageModel app = new PackageModel();
            ApplicationInfo appInfo = packageinfo.applicationInfo;

            appInfo.sourceDir = file.getAbsolutePath();
            appInfo.publicSourceDir = file.getAbsolutePath();
            Log.d(TAG, "packageInfo : " + packageinfo.toString());
            Log.d(TAG, "appInfo : " + appInfo.toString());
            Log.d(TAG, pm.getApplicationLabel(appInfo).toString());

            app.setAppName(pm.getApplicationLabel(appInfo).toString());

            Drawable icon = getApkIcon(file.getAbsolutePath(), mContext);
            if (icon == null) {
                app.setIcon(mContext.getResources().getDrawable(R.drawable.ic_launcher));
            } else {
                app.setIcon(getApkIcon(file.getAbsolutePath(), mContext));
            }
            app.setPackageInfo(packageinfo);

            app.setPath(file.getAbsolutePath());

            app.setPkgName(appInfo.packageName);

            app.setStatus(PackageModel.FLAG_SELECTED);

            app.setVersionName(packageinfo.versionName);

            app.setVersionCode(packageinfo.versionCode);

            double size = 1.0 * getFileLength(file) / 1048576;
            String result = String.format(Locale.ROOT, "%.1f", size);
            app.setSize(Double.parseDouble(result));
            return app;
        } else {
            return null;
        }
    }

    private static final int INSATLL_SUCCESS = 1;

    private static final int INSATLLING = 1000;
    
    private static final int BLOCK_APK=1111;

    private static final int REQUEST_INSTALL_APK = 902;

    private static final boolean APP_SCURELIST_ON = android.os.SystemProperties.getBoolean("ro.scifly.securelist.on",
            true);

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Commons.MSG_REMINDER_SHOW:
                    mReminderToast.show();
                    break;

                case Commons.MSG_REMINDER_DISMISS:
                    mReminderToast.cancel();
                    break;

                case Commons.MSG_DLG_BTN_OK:
                    reallyInstall(mTargetPkgModel);
                    mTargetPkgModel = null;
                    break;

                case Commons.MSG_DLG_BTN_CANCEL:
                    mTargetPkgModel = null;
                    mContext.finish();
                    break;

                case REQUEST_INSTALL_APK:
                    mTargetPkgModel = (PackageModel)msg.obj;
                    if (APP_SCURELIST_ON) {
                        boolean isSafe = mAppInfoChecker.isSafe(mTargetPkgModel.getPath(), null);
                        if (!isSafe) {
                            new BadAppAlertDialog(mContext, mHandler, 10);
                        } else {
                            reallyInstall(mTargetPkgModel);
                            mTargetPkgModel = null;
                        }
                    } else {
                        reallyInstall(mTargetPkgModel);
                        mTargetPkgModel = null;
                    }
                    break;

                case INSATLLING:
                    if (mHolder.mInstallCategory == PakageInstallerHolder.BATCH_INSTALL) {
                        mCurrentInstallApk.setStatus(PackageModel.FLAG_INSTALLING);
                        mHolder.refreshUI();
                    }
                    break;

                case INSATLL_SUCCESS:
                    if (mHolder.mInstallCategory == PakageInstallerHolder.BATCH_INSTALL) {
                        mApkInstallSuccessNum++;
                        mCurrentInstallApk.setStatus(PackageModel.FLAG_INSTALL_SUCCESS);
                        Log.d(TAG, mCurrentInstallApk.getAppName() + " install success(batch install) . ");
                        mHolder.refreshUI();
                        if ((++mCurrentApkIndex) < mSelectedApkList.size() ) {
                            install(mSelectedApkList.get(mCurrentApkIndex));
                        } else {
                            mSelectedApkList.clear();
                            mHolder.what = PakageInstallerHolder.CATEGORY_INSTALL_COMPLETED;
                            mHolder.findViews();
                            mHolder.regisertListener();
                            mHolder.refreshUI();
                        }
                    } else if (mHolder.mInstallCategory == PakageInstallerHolder.SINGLE_INSTALL){
                        Log.d(TAG, mCurrentInstallApk.getAppName() + " install success(single install) . ");
                        mHolder.what = PakageInstallerHolder.CATEGORY_SINGLE_INSTALL_COMPLETED;
                        mHolder.findViews();
                        mHolder.regisertListener();
                        mHolder.refreshUI();
                    }
                    break;
                case BLOCK_APK:
                    mHolder.mInstallCategory = PakageInstallerHolder.BLOCK_STALL;
                    mHolder.what=PakageInstallerHolder.CATEGORY_BLOCK_APK;
                    mHolder.findViews();
                    mHolder.regisertListener();
                    break;
                
                default :
                    // install failed
                    if (mHolder.mInstallCategory == PakageInstallerHolder.BATCH_INSTALL) {
                        mCurrentInstallApk.setStatus(PackageModel.FLAG_INSTALL_FAILUER);
                        Log.d(TAG, mCurrentInstallApk.getAppName() + " install fail(batch install) . ");
                        mHolder.refreshUI();
                        
                        if ((++mCurrentApkIndex) < mSelectedApkList.size() ) {
                            install(mSelectedApkList.get(mCurrentApkIndex));
                        } else {
                            mSelectedApkList.clear();
                            mHolder.what = PakageInstallerHolder.CATEGORY_INSTALL_COMPLETED;
                            mHolder.findViews();
                            mHolder.regisertListener();
                            mHolder.refreshUI();
                        }
                    } else if (mHolder.mInstallCategory == PakageInstallerHolder.SINGLE_INSTALL && mCurrentInstallApk != null){
                        Log.d(TAG, mCurrentInstallApk.getAppName() + "install fail(single install) .");
                        // out of space
                        if (msg.what == PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE) {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.out_of_space), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, mCurrentInstallApk.getAppName() + mContext.getResources().getString(R.string.install_fail_tip), Toast.LENGTH_LONG).show();
                        }
                        mContext.finish();
                    }
                    break;
            }

        }
    };

}
