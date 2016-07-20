
package scifly.provider.metadata;

import static com.eostek.scifly.messagecenter.util.Constants.IMG_PLAYER_CLASS;
import static com.eostek.scifly.messagecenter.util.Constants.IMG_PLAYER_KEY;
import static com.eostek.scifly.messagecenter.util.Constants.IMG_PLAYER_TYPE;
import static com.eostek.scifly.messagecenter.util.Constants.PACKAGE_NAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.datacache.DataCacheListener;
import scifly.datacache.DataCacheManager;
import scifly.device.Device;
import scifly.dm.EosDownloadListener;
import scifly.dm.EosDownloadManager;
import scifly.dm.EosDownloadTask;
import scifly.permission.Permission;
import scifly.provider.SciflyStatistics;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Messages;
import scifly.util.LogUtils;
import android.R.integer;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.IPackageDeleteListener;
import android.app.IPackageInstallListener;
import android.app.PackageManagerExtra;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.eostek.scifly.messagecenter.R;
import com.eostek.scifly.messagecenter.logic.MessageDataCache;
import com.eostek.scifly.messagecenter.util.Constants;
import com.eostek.scifly.messagecenter.util.Util;
import com.eostek.streamnetplusservice.service.ResultListener;
import com.eostek.streamnetplusservice.service.StreamNetManager;
import com.eostek.streamnetplusservice.service.TaskInfoInternal;
import com.eostek.streamnetplusservice.service.TaskListener;
import com.eostek.tm.cpe.manager.CpeManager;
import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * @author Charles.tai
 * @date 2014-05-01
 * @category The service to handle the command from the Server.
 */
public class MsgService extends Service implements Runnable {

    protected static final String TAG = "MsgService";

    private static final boolean DBG = true;

    private static final int SHOW_MARQUEE = 101;

    private static final int DISMISS_DIALOG = 102;

    private static final int APK_STARTUP_NOW = 103;

    private static final int APK_STARTUP_LATE = 104;

    private static final long TWO_THOUSAND_TEN_YEARS_TIME_STAMP = 1262275200000L;

    private static final long ONE_DAY = SystemProperties.getLong("persist.debug.startapk", 24 * 60 * 60 * 1000);

    private static final int SINGLE_LINE_LENGTH = 62;
    
    private static int CONNECT_TIMES =0;

    protected static final int SHOW_TIME = 10000;

    private static final String UPGRADE_SUCCEED = "0";

    private static final String UPGRADE_FAILED = "1";

    private static final String FULL_OTA_PACKAGE = "1";

    private static final String ACTION_UPDATE_INCREMENTAL_ALL = "scifly.intent.action.UPDATE_INCREMENTAL_ALL";

    private static final String ACTION_UPDATE_CLOUD_PUSH = "scifly.intent.action.UPDATE_CLOUD_PUSH";

    private static final String EXTRA_INCREMENTAL_ALL = "extra_incremental_all";

    private static final String ALARM_ACTION_INTENT = "android.alarm.action";

    public static final String SHARED_PREFERENCE_NAME = "scifly_msg_service";

    private static final String AD_SWITCH_PROPERTY = "persist.sys.ads.switch";

    private static final String BOOT_VIDEO_FILE_NAME = "video.ts";

    private static final String BOOT_VIDEO_PREDICT_FILE_NAME = "video_predict";

    private static final String BOOT_VIDEO_FILE_PATH = "/data/video/video.ts";

    private static final String BOOT_VIDEO_DIR_PATH = "/data/video/";

    public static final String EXTRA_CLOUD_PUSH_TASKID = "extra_cloud_push_taskid";

    public static final String EXTRA_CLOUD_PUSH_FORCE = "extra_cloud_push_force";

    private IMsgStateListener mListener;

    private Msg mMessage;

    private AlertDialog mDialog;

    // add overlay view
    private WindowManager mWindowManager = null;

    // for inflate view
    private LayoutInflater mInflater = null;

    // view for server message.
    private View mMessageView = null;

    private TextSwitcher mTextSwitcher;

    private EosDownloadTask mDownloadTask;

    private EosDownloadManager mDownloadManager;

    private StreamNetManager mSNManager;

    private ServiceHandler mHandler;

    private int mScrollCount = 0;

    private String[] mMessageStrs = new String[10];

    private int mTimes = 0;

    private int mLength = 0;

    private Queue mQueue;

    private boolean isGetMsg = true;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IBinder mBinder = new IMsgManager.Stub() {

        @Override
        public void putCommand(Msg msg) throws RemoteException {
            if (DBG) {
                Log.d(TAG, "putCommand TO service : " + msg.mTitle);
            }
            if (mQueue == null) {
                mQueue = new Queue();
            }
            mQueue.add(msg);
            Log.d(TAG, "msg queue size=" + mQueue.size() + ", isGetMsg=" + isGetMsg);
            if (mQueue.size() == 1 && isGetMsg) {
                showMsg();
            }
        }

        @Override
        public void setMsgStateChangeListener(IMsgStateListener listener) throws RemoteException {
            if (DBG) {
                Log.d(TAG, "setMsgStateChangeListener");
            }
            mListener = listener;
        }
    };

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ALARM_ACTION_INTENT);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
        Thread thread = new Thread(this, "MsgService");
        thread.start();
        if (mSNManager == null) {
            mSNManager = new StreamNetManager(getApplicationContext());
        }
    };

    private void showMsg() {
        if (isGetMsg && mQueue.size() > 0) {
            isGetMsg = false;
            mMessage = mQueue.poll();
            mHandler.removeMessages(mMessage.mCategory);
            mHandler.sendEmptyMessage(mMessage.mCategory);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "intent.getAction() = " + intent.getAction());
            if (intent.getAction().equals(ALARM_ACTION_INTENT)
                    || intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (isNetworkAvailable(getApplicationContext())) {
                    List<Msg> listMessage = queryStartApkList();
                    if (listMessage.size() != 0) {
                        for (Msg msg : listMessage) {
                            startBackgroundApp(msg, getApplicationContext());
                        }
                    } else {
                        Log.d(TAG, "listMessage size = " + listMessage.size());
                    }
                    redownloadBootVideo();
                } else {
                    Log.e(TAG, "onReceive : network is not available");
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * handler to dispatch the command.
     * 
     * @author charles.tai
     */
    private final class ServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Messages.CATEGORY_TEXT:
                    showText(Messages.CATEGORY_TEXT);
                    break;
                case Messages.CATEGORY_IMAGE:
                    loadImage();
                    break;
                case Messages.CATEGORY_URL:
                    showWebView(mMessage.mData, getApplicationContext());
                    break;
                case Messages.CATEGORY_BROADCAST:
                    sendBroadcast();
                    break;
                case Messages.CATEGORY_UPDATE_RESULT:
                    showUpgradeResult(mMessage.mExtra);
                    break;
                case Messages.CATEGORY_UPLOAD_LOG:
                    uploadLog(false);
                    break;
                case Messages.CATEGORY_UPLOAD_SCREEN_SHOT:
                    uploadLog(true);
                    break;
                case Messages.CATEGORY_BOOT_LOGO:
                    downloadFile(mMessage.mImgUrl);
                    break;
                case Messages.CATEGORY_APK_INSTALL:
                    downloadFile(mMessage.mData);
                    break;
                case Messages.CATEGORY_APK_UNINSTALL:
                    deletePackage(mMessage.mData);
                    changCmdState();
                    break;
                case SHOW_MARQUEE:
                    setMarqueeText(mMessage.mData);
                    break;
                case DISMISS_DIALOG:
                    Log.d(TAG, "Dismiss dialog after 10 seconds");
                    if (null != mDialog) {
                        mDialog.dismiss();
                    }
                    changCmdState();
                    break;
                case Messages.CATEGORY_APK_STARTUP:
                    handleStartApkMessage(mMessage);
                    break;
                case APK_STARTUP_NOW:
                    startBackgroundApp(mMessage, getApplicationContext());
                    changCmdState();
                    break;
                case APK_STARTUP_LATE:
                    if ((mMessage.mTime + ONE_DAY) <= System.currentTimeMillis()) {
                        startBackgroundApp(mMessage, getApplicationContext());
                    }
                    changCmdState();
                    break;
                case Messages.CATEGORY_ADS_SWITCH:
                    changeADsSwitch();
                    changCmdState();
                    break;
                case Messages.CATEGORY_BOOT_VIDEO:
                    if (isVideoExist()) {
                        changCmdState();
                    } else {
                        String engine = SciflyStore.Global.getString(getContentResolver(), SciflyStore.Global.DOWNLOAD_ENGINE, Constants.DOWNLOAD_ENGINE_HTTP);
                        if (Constants.DOWNLOAD_ENGINE_P2P.equals(engine)) {
                            downloadBootVideoWithSN();
                        } else {
                            downloadBootVideo();
                        }
                    }
                    break;
                case Messages.CATEGORY_BOOT_VIDEO_RECOVERY:
                    deleteVideoFile();
                    changCmdState();
                    break;
                case Messages.CATEGORY_UPDATE_CLOUD_PUSH :
                    startOtaUpdateService();
                    changCmdState();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new ServiceHandler();
        Looper.loop();
    }

    /**
     * Handle the command for text of Dialog or Marquee.
     * 
     * @param category
     */
    private void showText(int category) {
        if (DBG) {
            Log.d(TAG, "show text");
        }
        if (mMessage.mSource == Messages.SOURCE_CPE) {
            JSONObject jsonObject;
            try {
                Log.d(TAG, "msg is from cpe");
                jsonObject = new JSONObject(mMessage.mExtra);
                String layout = jsonObject.getString("layout");
                if ("center".equals(layout)) {
                    // show the dialog from CPE.
                    showDialog(category);
                } else if ("bottom".equals(layout)) {
                    // show the marquee from CPE
                    int scrollCount = jsonObject.getInt("scrollCount");
                    showMarquee(scrollCount, mMessage.mData);
                } else {
                    showDialog(category);
                }
            } catch (JSONException e) {
                changCmdState();
                e.printStackTrace();
            }
        } else if (mMessage.mSource == Messages.SOURCE_ISYNERGY) {
            // from the wechat to show text message.
            showMarquee(1, mMessage.mData);
        }
    }

    /**
     * show the Marquee in the bottom of screen.
     * 
     * @param scrollCount
     * @param message
     */
    private void showMarquee(int scrollCount, final String message) {
        Log.d(TAG, ">>>>>>showMarquee>>>>>>" + scrollCount);
        if (null == mWindowManager) {
            mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        }
        if (null == mInflater) {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        mScrollCount = scrollCount;
        mMessageView = mInflater.inflate(R.layout.cmd_marquee, null);
        mTextSwitcher = (TextSwitcher) mMessageView.findViewById(R.id.ts_message);
        mTextSwitcher.setFactory(new ViewFactory() {

            @Override
            public View makeView() {
                TextView scrollTextView = new TextView(getApplicationContext());
                // scrollTextView.setTextColor(Color.WHITE);
                float textSize = 0;
                DisplayMetrics dm = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(dm);
                int height = dm.heightPixels;
                if (1080 == height) {
                    textSize = 24;
                } else {
                    textSize = getResources().getDimensionPixelSize(R.dimen.marquee_textsize);
                }
                Log.d(TAG, "height : + height " + " textsize : " + textSize);
                TextPaint paint = scrollTextView.getPaint();
                paint.setFakeBoldText(true);
                scrollTextView.setTextSize(textSize);
                scrollTextView.setSingleLine();
                scrollTextView.setEllipsize(TruncateAt.MARQUEE);
                scrollTextView.setSelected(true);
                scrollTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                return scrollTextView;
            }
        });
        mLength = message.length();
        splitString(message);
        int marqueeHeight = getResources().getDimensionPixelSize(R.dimen.marquee_view_height);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                marqueeHeight, WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
        mWindowManager.addView(mMessageView, params);
        mHandler.sendEmptyMessage(SHOW_MARQUEE);
    }

    /**
     * set the text of the marquee
     * 
     * @param message
     */
    private void setMarqueeText(String message) {
        int num = (int) Math.ceil(1.0 * mLength / SINGLE_LINE_LENGTH);
        if (num > 1) {
            if (mTimes == num) {
                mTimes = 0;
            }
            mTextSwitcher.setText(mMessageStrs[mTimes]);
        } else {
            mTextSwitcher.setText(message);
        }
        mHandler.removeMessages(SHOW_MARQUEE);
        mHandler.sendEmptyMessageDelayed(SHOW_MARQUEE, 3000);
        mTimes += 1;
        mScrollCount--;
        if (mScrollCount == -1) {
            mHandler.removeMessages(SHOW_MARQUEE);
            try {
                // remove the view which is showing the marquee.
                mWindowManager.removeView(mMessageView);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            changCmdState();

        }
    }

    /**
     * Show dialog when the Server put message to the client.
     * 
     * @param category
     */
    private void showDialog(final int category) {
        if (DBG) {
            Log.d(TAG, ">>>>>Show Dialog>>>>>");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Translucent);
        mDialog = builder.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.cmd_dialog);
        TextView messageTitle = (TextView) window.findViewById(R.id.tv_message_title);
        TextView messageTime = (TextView) window.findViewById(R.id.tv_message_time);
        TextView messageContent = (TextView) window.findViewById(R.id.tv_message_content);
        Button closeButton = (Button) window.findViewById(R.id.bt_close);
        Button viewButton = (Button) window.findViewById(R.id.bt_view_detail);
        LinearLayout viewLinearLayout = (LinearLayout) window.findViewById(R.id.ll_button);
        LinearLayout twoButtonLayout = (LinearLayout) window.findViewById(R.id.ll_two_button);
        ImageView iconImageView = (ImageView) window.findViewById(R.id.iv_message_icon);
        messageTime.setText(Util.mill2String(mMessage.mTime, Constants.TIME_YMD_HMS));

        if (category == Messages.CATEGORY_TEXT) {
            // text command show the content dialog
            twoButtonLayout.setVisibility(View.GONE);
            viewLinearLayout.setVisibility(View.VISIBLE);
            viewButton.setText(R.string.close);
            iconImageView.setImageResource(R.drawable.message_icon);
            messageTitle.setText(mMessage.mTitle);
            messageContent.setText(mMessage.mData);
            viewButton.setFocusable(true);
            viewButton.setFocusableInTouchMode(true);
            viewButton.requestFocus();
            viewButton.requestFocusFromTouch();
        }
        mHandler.sendEmptyMessageDelayed(DISMISS_DIALOG, SHOW_TIME);
        viewButton.setOnClickListener(new DismissDialogListener());
        closeButton.setOnClickListener(new DismissDialogListener());
        mDialog.setOnKeyListener(new DialogKeyListener());
    }

    /**
     * show the result of the upgrade info.
     * 
     * @param result 0-succeed; 1-failed
     */
    
    
    TextView countdownTv ;
    private int mCountdownTime = 20;
    private Runnable mCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            countdownTv.setText(MsgService.this.getString(R.string.countdown_text, mCountdownTime));

            if (mCountdownTime > 0) {
                --mCountdownTime;
                mHandler.postDelayed(this, 1000);
            } else if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    };

    private void showUpgradeResult(String result) {
        if (DBG) {
            Log.d(TAG, ">>>>>showUpgradeStatus>>>>>" + result);
        }
        // stop the boot animation while show dialog.
        if ("T".equals(SystemProperties.get("ro.eostek.tv"))) {
            SystemProperties.set("ctl.stop", "bootanim");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Translucent);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.upgrade_info_dialog);
        RelativeLayout relativeLayout = (RelativeLayout) window.findViewById(R.id.rl_button);
        TextView upgradeTitleView = (TextView) window.findViewById(R.id.tv_upgrade_title);
        TextView upgradeInfoView = (TextView) window.findViewById(R.id.tv_upgrade_info);
        countdownTv = (TextView)window.findViewById(R.id.tv_counting);
        Button viewListButton = (Button) window.findViewById(R.id.bt_view_info);
        Button closeButton = (Button) window.findViewById(R.id.bt_close);
        if (UPGRADE_SUCCEED.equals(result)) {
            // upgrade succeed to show the version info
            String systemVersion = Build.VERSION.INCREMENTAL.substring(1);
            relativeLayout.setVisibility(View.GONE);
            countdownTv.setVisibility(View.VISIBLE);
            countdownTv.setText(this.getString(R.string.countdown_text, mCountdownTime));
            upgradeTitleView.setText(R.string.upgrade_success);
            upgradeInfoView.setText(getString(R.string.version_tips, systemVersion));
            viewListButton.setText(R.string.view_update_list);
            closeButton.setText(R.string.close);
            LayoutParams params = closeButton.getLayoutParams();
            params.width = getResources().getDimensionPixelSize(R.dimen.dialog_button_width);
            closeButton.setLayoutParams(params);
            closeButton.requestLayout();

            mDialog = dialog;
            closeButton.setOnClickListener(new DismissDialogListener());
            mHandler.post(mCountDownRunnable);
        }
        if (UPGRADE_FAILED.equals(result)) {
            // upgrade failed to download the full ota package.
            upgradeTitleView.setText(R.string.upgrade_failed);
            upgradeInfoView.setText(R.string.failed_info);
            viewListButton.setText(R.string.not_upgrade);
            closeButton.setText(R.string.download_full_package);
            mDialog = dialog;
            viewListButton.setOnClickListener(new DismissDialogListener());
            closeButton.setOnClickListener(new DownloadFullPackageListener());
        }
        closeButton.setFocusable(true);
        closeButton.setFocusableInTouchMode(true);
        closeButton.requestFocus();
        closeButton.requestFocusFromTouch();

        mDialog.setOnKeyListener(new DialogKeyListener());
    }

    /**
     * show the web content when the category is url.
     */
    public void showWebView(String url, Context context) {
        if (TextUtils.isEmpty(url)) {
            Log.d(TAG, "Url must not be empty !");
            return;
        }
        Log.d(TAG, ">>>>>showWebView>>>>>" + url);
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("URL", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        changCmdState();
    }

    /**
     * download image to sdcard and loading it when download complete.
     */
    private void loadImage() {
        Map<String, String> map = new HashMap<String, String>();
        MessageDataCache mDataCache = MessageDataCache.getCacheLoader(this);
        map.put(DataCacheManager.EXTRA_KEY_POSTFIX, ".png");
        mDataCache.loadCache(DataCacheManager.DATA_CACHE_TYPE_FILE, mMessage.mData, map, new DataCacheListener() {
            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                super.onLoadingComplete(requestUri, view, dataObject);
                Log.i(TAG, "onLoadingComplete requestUri = " + requestUri);

                if (!(dataObject instanceof File)) {
                    changCmdState();
                    return;
                }
                File file = (File) dataObject;
                Log.i(TAG, "file.getAbsolutePath() = " + file.getAbsolutePath());
                playImage(file.getAbsolutePath(), IMG_PLAYER_CLASS, IMG_PLAYER_TYPE, IMG_PLAYER_KEY, mMessage);
            }

            @Override
            public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                super.onLoadingFailed(requestUri, view, failReason);
                Log.i(TAG, "onLoadingFailed changCmdState");
                changCmdState();
            }

        }, null);
    }

    /**
     * play image by PhotoPlayerActivity.
     * 
     * @param url
     * @param clsName
     * @param type
     * @param from
     * @param msg
     */
    private void playImage(String url, String clsName, String type, String from, Msg msg) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (clsName != null) {
            // intent.setClassName(PACKAGE_NAME, clsName);
            if (type.equals(IMG_PLAYER_TYPE)) {
                intent.setClassName("com.eostek.scifly.messagecenter", clsName);
                intent.putExtra("userId", msg.mUserId);
            } else {
                intent.setClassName(PACKAGE_NAME, clsName);
            }
        }
        if (type != null) {
            intent.setDataAndType(Uri.parse(url), type);
        }
        intent.setAction(Intent.ACTION_VIEW);
        if (from != null) {
            intent.putExtra(from, true);
        }
        try {
            startActivity(intent);
            Log.d(TAG, "Start Activity Success . " + clsName);
            changCmdState();
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "no app..........");
        }
    }

    /**
     * Transfer the BroadCast from CPE to other apps.
     */
    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(mMessage.mTitle);
        intent.putExtra(mMessage.mExtra, mMessage.mData);
        if (DBG) {
            Log.d(TAG, "msg.Action : " + mMessage.mTitle);
        }
        sendBroadcast(intent);
        changCmdState();
    };

    /**
     * crash log auto upload
     */
    private void uploadLog(boolean screenshot) {
        if (DBG) {
            Log.d(TAG, "CPE call MessageCenter to upload log:" + screenshot);
        }
        LogUtils.captureLog(this, mMessage.mData, screenshot, resultListener);
        changCmdState();
    }

    private LogUtils.IResultListener resultListener = new LogUtils.IResultListener() {

        @Override
        public void captureResult(boolean result) {
            Log.d(TAG, "captureLog result:" + result);
        }
    };

    /**
     * set boot logo
     * 
     * @param fileName
     * @param downloadUrl
     * @param downloadPath
     */

    private void setBootLogo(String fileName, String downloadUrl, String downloadPath) {
        if (fileName == null || downloadUrl == null || downloadPath == null) {
            Log.d(TAG, "fileName or downloadUrl or downloadPath not be null ! ");
            return;
        } else {
            if (DBG) {
                Log.d(TAG, "fileName : " + fileName + " downloadUrl : " + downloadUrl + " downLoadPath : "
                        + downloadPath);
            }
            String tmpFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getPath() + "/" + fileName;
            try {
                Log.i(TAG, "download success, begin copy file from tmpPath: " + tmpFilePath + " to targetPath:"
                        + downloadPath + "/" + fileName);
                File tmpFile = new File(tmpFilePath);
                if (tmpFile.exists()) {
                    Permission permission = new Permission("JCheZW50ZXI6cmVtb3RlanJt");
                    boolean b_shell = permission.exec("cp " + tmpFilePath + " " + downloadPath + "/" + fileName);
                    if (b_shell) {
                        permission.exec("chmod 644 " + downloadPath + "/" + fileName);
                        Log.d(TAG, "copy file succeed ! ");
                        permission.exec("rm " + tmpFilePath);
                    } else {
                        Log.d(TAG, "copy file failed");
                    }
                } else {
                    Log.d(TAG, "file not exist ! ");
                }
            } catch (Exception e) {
                Log.d(TAG, "copy file failed...");
                e.printStackTrace();
            }
        }
        changCmdState();
    }

    /**
     * 设置开机视频
     * @param fromPath :视频文件下载位置
     * @param toPath :视频播放位置
     * @param downloadMode :视频下载模式(决定设置完视频后是否删除下载文件)
     */
    private void setBootVideo(String fromPath, String toPath, String downloadMode) {
        // /sdcard/Download/xxx.x     /sdcard/Download/XXXX/xxx.x 
        // /data/data/video.ts        /data/data/video_predict
        if (TextUtils.isEmpty(fromPath)) {
            Log.d(TAG, "fromPath is empty.");
            changCmdState();
            return;
        }
        File fromFile = new File(fromPath);
        if (!fromFile.exists() || fromFile.isDirectory()) {
            Log.d(TAG, "from file is not exist, or is directory.");
            changCmdState();
            return;
        }
        File toFile = new File(toPath);
        File toFileDir = toFile.getParentFile();
        if (!toFileDir.exists() || !toFileDir.isDirectory()) {
            Log.d(TAG, "to file dir make " + (toFileDir.mkdirs() ? "successed !" : "failed !"));
        }
        try {
            boolean success = Util.copyToFile(new FileInputStream(fromFile), toFile);
            Log.d(TAG, "cp " + fromPath + " " + toPath + (success ? " successed !" : " failed !"));
            if (success && toPath.endsWith(BOOT_VIDEO_PREDICT_FILE_NAME)) {
                File file = new File(toFileDir.getAbsolutePath() + "/" + BOOT_VIDEO_FILE_NAME);
                Log.d(TAG, "rename from " + toFile.getAbsolutePath() + " to " + file.getAbsolutePath() + (toFile.renameTo(file) ? "successed !" : "failed !"));
            }
        } catch (IOException e) {
            Log.d(TAG, "cp " + fromPath + " " + toPath + "failed");
            e.printStackTrace();
        }
        if (Constants.DOWNLOAD_ENGINE_HTTP.equals(downloadMode)) {
            fromFile.delete();
        }
        changCmdState();
    }

    /**
     * change the state of the command. [call back]
     */
    private void changCmdState() {
        isGetMsg = true;
        showMsg();
        if (null != mListener) {
            try {
                mListener.stateChanged(mMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void splitString(String message) {
        mTimes = 0;
        if (mLength > 500) {
            Log.e(TAG, "The length of the text is more than screenWidth.");
            changCmdState();
            return;
        } else {
            int num = (int) Math.ceil(1.0 * mLength / SINGLE_LINE_LENGTH);
            mScrollCount = mScrollCount * num;
            for (int i = 0; i < num - 1; i++) {
                mMessageStrs[i] = message.substring(i * SINGLE_LINE_LENGTH, (i + 1) * SINGLE_LINE_LENGTH);
            }
            mMessageStrs[num - 1] = message.substring((num - 1) * SINGLE_LINE_LENGTH, mLength);
        }
    }

    /**
     * start dowload file in background
     * 
     * @param message
     */
    private void downloadFile(String url) {
        if (null == url) {
            Log.d(TAG, "url not be null ... ");
            return;
        } else {
            Request request = new Request(Uri.parse(url));
            // download file into /mnt/sdcard/Download/

            if (mMessage.mCategory == Messages.CATEGORY_APK_INSTALL
                    || mMessage.mCategory == Messages.CATEGORY_APK_STARTUP) {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mMessage.mTitle);
            } else {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mMessage.mData);
            }
            request.setVisibleInDownloadsUi(false);

            mDownloadManager = new EosDownloadManager(getApplicationContext());
            SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            long taskId = preference.getLong("id", -1);
            String taskUrl = preference.getString("url", "");
            if (taskId != -1 && url == taskUrl) {
                mDownloadManager.restartTask(taskId, mDownloadListener);
            } else {
                mDownloadTask = new EosDownloadTask(request, mDownloadListener);
                long id = mDownloadManager.addTask(mDownloadTask);
                SharedPreferences.Editor edit = preference.edit();
                edit.putLong("id", id);
                edit.putString("url", url);
                edit.commit();
            }
        }
    }

    /**
     * 下载组件监听器
     */
    private EosDownloadListener mDownloadListener = new EosDownloadListener() {

        @Override
        public void onDownloadStatusChanged(int status) {
        }

        @Override
        public void onDownloadSize(long size) {
            // Log.d(TAG, "Download size : " + size);
        }

        @Override
        public void onDownloadComplete(int percent) {
            Log.d(TAG, "DownLoad percent : " + percent);
            if (percent == 100) {
                SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = preference.edit();
                edit.putLong("id", -1);
                edit.commit();
                switch (mMessage.mCategory) {
                    case Messages.CATEGORY_BOOT_LOGO:
                        setBootLogo(mMessage.mData, mMessage.mImgUrl, mMessage.mExtra);
                        break;
                    case Messages.CATEGORY_APK_STARTUP:
                    case Messages.CATEGORY_APK_INSTALL:
                        SciflyStatistics.getInstance(MsgService.this).recordEvent(
                                "scifly.provider.metadata.MsgService", "DOWNLOAD_APP_COMPLETE", "APKNAME",
                                mMessage.mTitle);
                        String tmpFilePath = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS).getPath()
                                + "/" + mMessage.mTitle;
                        Log.d(TAG, "tmpName : " + mMessage.mTitle + " tmpFilePath : " + tmpFilePath);
                        installPackage(tmpFilePath);
                        if (mMessage.mCategory == Messages.CATEGORY_APK_INSTALL) {
                            changCmdState();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private EosDownloadListener mBootVideoDownloadListener = new EosDownloadListener() {

        @Override
        public void onDownloadStatusChanged(int status) {
            SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            String md5 = preference.getString("md5", "");
            String time = preference.getString("videoTime", "");
            switch (status) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    reportDataWithBootVideo(md5, 0, time);
                    break;

                case DownloadManager.STATUS_FAILED:
                    changCmdState();
                    reportDataWithBootVideo(md5, 1, time);
                case DownloadManager.STATUS_PAUSED:
                    changCmdState();
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onDownloadSize(long size) {
        }

        @Override
        public void onDownloadComplete(int percent) {
            Log.d(TAG, "DownLoad percent : " + percent);
            if (percent == 100) {
                SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
                String md5 = preference.getString("md5", "");
                String time = preference.getString("videoTime", "");
                SharedPreferences.Editor edit = preference.edit();
                edit.putLong("taskId", -1);
                edit.commit();
                File file = new File(BOOT_VIDEO_DIR_PATH + BOOT_VIDEO_PREDICT_FILE_NAME);
                String fromPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + BOOT_VIDEO_FILE_NAME;
                
                if (verify(fromPath, md5)) {
                    if (!file.isDirectory() && file.exists()) {
                        Log.d(TAG, "predict video is exist.");
                        setBootVideo(fromPath, BOOT_VIDEO_DIR_PATH + "/" + BOOT_VIDEO_PREDICT_FILE_NAME, Constants.DOWNLOAD_ENGINE_HTTP);
                    } else {
                        Log.d(TAG, "predict video is not exist.");
                        setBootVideo(fromPath, BOOT_VIDEO_FILE_PATH, Constants.DOWNLOAD_ENGINE_HTTP);
                    }
                } else {
                    Log.d(TAG, "HTTP download file md5 is wrong.");
                    changCmdState();
                    edit.remove("md5");
                    edit.remove("videoTime");
                    edit.remove("taskId");
                    edit.remove("bootVideoUrl");
                    edit.remove("engine");
                    edit.commit();
                    File fromFile = new File(fromPath);
                    fromFile.delete();
                    reportDataWithBootVideo(md5, 3, time);
                }
            }
        }
    };
    /**
     * 静默安装应用
     */
    private void installPackage(String path) {
        PackageManagerExtra managerExtra = PackageManagerExtra.getInstance();
        Uri packageURI = Uri.fromFile(new File(path));
        // 安装接口
        managerExtra.installPackage(getApplicationContext(), packageURI, mInstallListener);
    }

    /**
     * 安装结果回调
     */
    IPackageInstallListener mInstallListener = new IPackageInstallListener() {

        @Override
        public void packageInstalled(String packageName, int returnCode) {
            // returncode 1 代表安装成功；其他表示安装失败
            Log.d(TAG, "Listener : >>>PackageManager PackageName : " + packageName + " ReturnCode : " + returnCode);
            if (returnCode == 1 && mMessage.mCategory == SciflyStore.Messages.CATEGORY_APK_STARTUP) {
                Log.d(TAG, "install successed send APK_STARTUP_NOW message");
                mHandler.sendEmptyMessage(APK_STARTUP_NOW);
            }
            SciflyStatistics.getInstance(MsgService.this).recordEvent("scifly.provider.metadata.MsgService",
                    "PACKAGE_INSTALL_RESULT", "RETURNCODE_" + packageName, "" + returnCode);
        }
    };

    /**
     * 静默卸载应用
     */
    private void deletePackage(String packageName) {
        PackageManagerExtra managerExtra = PackageManagerExtra.getInstance();
        managerExtra.deletePackage(getApplicationContext(), packageName, mDeleteListener);
    }

    /**
     * 卸载结果回调
     */
    IPackageDeleteListener mDeleteListener = new IPackageDeleteListener() {

        @Override
        public void packageDeleted(String packageName, int returnCode) {
            // returncode 1 代表安装成功；其他表示安装失败
            Log.d(TAG, "Listener : >>>PackageManager PackageName : " + packageName + " ReturnCode : " + returnCode);
            SciflyStatistics.getInstance(MsgService.this).recordEvent("scifly.provider.metadata.MsgService",
                    "PACKAGE_UNINSTALL_RESULT", "RETURNCODE_" + packageName, "" + returnCode);
        }
    };

    /**
     * start Settings to download Full OTA package.
     */
    private class DownloadFullPackageListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ACTION_UPDATE_INCREMENTAL_ALL);
            intent.putExtra(EXTRA_INCREMENTAL_ALL, FULL_OTA_PACKAGE);
            startService(intent);
            mDialog.dismiss();
			if(Device.isVipMode(getApplicationContext())){
				Toast.makeText(MsgService.this, R.string.system_net_update_toast, Toast.LENGTH_LONG).show();	
			}else{
				Toast.makeText(MsgService.this, R.string.system_net_update_toast_simple, Toast.LENGTH_LONG).show();	
			}            
            changCmdState();
        }
    }

    /**
     * dismiss the dialog.
     */
    private class DismissDialogListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            mDialog.dismiss();
            changCmdState();
        }
    }

    /**
     * update the status of current message to read when click back key or home
     * key
     */
    private class DialogKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(DialogInterface view, int keyCode, KeyEvent event) {
            Log.d(TAG, "keyCode : " + keyCode + "  keyEvent : " + event.getAction());
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                    changCmdState();
                }
            }
            return false;
        }
    }

    private class Queue {

        private LinkedList<Msg> mLinkedList = new LinkedList<Msg>();

        private boolean add(Msg message) {
            return mLinkedList.add(message);
        }

        private Msg poll() {
            if (mLinkedList.size() == 0) {
                Log.e(TAG, "the size of mLinedList is 0.");
                return mMessage;
            }
            return mLinkedList.removeFirst();
        }

        @SuppressWarnings("unused")
        private Msg getNewMsg() {
            if (mLinkedList.size() == 0) {
                return null;
            }
            return mLinkedList.get(mLinkedList.size() - 1);
        }

        @SuppressWarnings("unused")
        private boolean isEmpty() {
            return mLinkedList.isEmpty();
        }

        private int size() {
            return mLinkedList.size();
        }
    }

    /**
     * handler the message which category = 19 .
     * 
     * @param message
     */
    private void handleStartApkMessage(Msg message) {
        if (TextUtils.isEmpty(message.mTitle)) {
            Log.d(TAG, "packageName must not be empty !");
            return;
        }
        // delete the same title message before .
        deleteStartApkMessage(message.mTitle);
        // whether the app is exist or not .
        if (isAppExist(message, getApplicationContext())) {
            // judge current time .
            if (System.currentTimeMillis() < TWO_THOUSAND_TEN_YEARS_TIME_STAMP) {
                Log.d(TAG, "APK_STARTUP_LATE   system time :" + System.currentTimeMillis());
                mHandler.sendEmptyMessageDelayed(APK_STARTUP_LATE, 60 * 1000);
            } else {
                Log.d(TAG, "APK_STARTUP_NOW  system time :" + System.currentTimeMillis());
                mHandler.sendEmptyMessage(APK_STARTUP_NOW);
            }
        } else {
            // download the apk .
            if (isNetworkAvailable(getApplicationContext())) {
                downloadFile(message.mData);
            } else {
                changCmdState();
            }
        }
    }
    /**
     * To judge the network
     * 
     * @param context
     */
    public static boolean isNetworkAvailable(Context context) {
        // Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * To judge the app is exist or not
     * 
     * @param message
     * @param context
     */
    private boolean isAppExist(Msg message, Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(message.mTitle,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * start the apk silent .
     * 
     * @param message
     * @param context
     */
    private void startBackgroundApp(Msg message, Context context) {
        if (TextUtils.isEmpty(message.mTitle)) {
            Log.d(TAG, "packageName must not be empty !");
            return;
        }
        if (isNetworkAvailable(getApplicationContext())) {
            Log.d(TAG, " start app silent :" + message.mTitle);
            Intent intent = getPackageManager().getLaunchIntentForPackage(message.mTitle);
            if (intent != null) {
                intent.addCategory("com.eostek.scifly.intent.category.SILENT");
                context.startActivity(intent);
                SciflyStatistics.getInstance(MsgService.this).recordEvent("scifly.provider.metadata.MsgService",
                        "PACKAGE_START_RESULT", "RETURNCODE_" + message.mTitle, "" + "SLIENT_START");
                changeMessage(getApplicationContext(), message);
            } else {
                Log.d(TAG, "Launch Intent For Package=" + intent);
            }
        }
    }

    /**
     * to change the message after started .
     * 
     * @param context
     * @param message
     */
    private void changeMessage(Context context, Msg message) {
        try {
            JSONObject extra = new JSONObject(message.mExtra);
            int times = extra.getInt("times") - 1;
            int uninstall = extra.getInt("uninstall");
            JSONObject mextra = new JSONObject();
            mextra.put("times", times);
            mextra.put("uninstall", uninstall);
            message.mTime = System.currentTimeMillis();

            if (times <= 0 && uninstall == 1) {
                Log.d(TAG, "delete the package : " + message.mTitle);
                deletePackage(message.mTitle);
            }
            if (times <= 0) {
                updateStartApkMessage(mextra.toString(), message.mTime, message.mTitle);
            } else {
                updateStartApkMessage(mextra.toString(), message.mTime, message.mTitle);
                setAlarmTime(context, message.mTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * set alarm for next 24 hours
     * 
     * @param context
     * @param timeInMillis
     */
    private void setAlarmTime(Context context, long timeInMillis) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ALARM_ACTION_INTENT);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Log.d(TAG, "set alarm : " + ONE_DAY + "  milli second");
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis + ONE_DAY, sender);
    }

    private List<Msg> listMessage = new ArrayList<Msg>();

    private static final Uri CONTENT_URI = Uri.parse("content://com.eostek.scifly.provider/message");

    private static final String MESSAGE_ID = "_id";

    private static final String MESSAGE_TITLE = "title";

    private static final String MESSAGE_TIME = "time";

    private static final String MESSAGE_EXTRA = "extra";

    private static final String MESSAGE_STATUS = "status";

    private static final String MESSAGE_CATEGORY = "category";

    private static final String ID_SELECTION = "title= ? and category=?";

    private static final String DELETE_SELECTION = "title= ? and category=? and _id < ?";

    private static final String SELECTION = "category=?";

    private static final String[] ID_SELECTION_ARGS = {
        MESSAGE_ID
    };

    private static final String[] PROJECTION = {
            MESSAGE_TITLE, MESSAGE_TIME, MESSAGE_EXTRA, MESSAGE_STATUS, MESSAGE_CATEGORY
    };

    private static final String[] SELECTION_ARGS = {
        String.valueOf(SciflyStore.Messages.CATEGORY_APK_STARTUP)
    };

    // delete the message which less than the MAX _id
    private void deleteStartApkMessage(String title) {
        Log.d(TAG, "the max id of message : " + queryMaxId(title));
        getContentResolver().delete(CONTENT_URI, DELETE_SELECTION, new String[] {
                title, String.valueOf(SciflyStore.Messages.CATEGORY_APK_STARTUP), String.valueOf(queryMaxId(title))
        });
    }

    // query the MAX _id of the message
    private int queryMaxId(String title) {
        int maxId = 0;
        Cursor cursor;
        try {
            cursor = getContentResolver().query(CONTENT_URI, ID_SELECTION_ARGS, ID_SELECTION, new String[] {
                    title, String.valueOf(SciflyStore.Messages.CATEGORY_APK_STARTUP)
            }, MESSAGE_ID);
            while (cursor != null && cursor.moveToNext()) {
                maxId = cursor.getInt(cursor.getColumnIndex(MESSAGE_ID));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxId;
    }

    // update the message
    private void updateStartApkMessage(String extra, long time, String title) {
        ContentValues values = new ContentValues();
        values.put("extra", extra);
        values.put("time", time);
        getContentResolver().update(CONTENT_URI, values, ID_SELECTION, new String[] {
                title, String.valueOf(SciflyStore.Messages.CATEGORY_APK_STARTUP)
        });
    }

    // query the message from db
    private List<Msg> queryStartApkList() {
        listMessage.clear();
        Cursor cursor;
        try {
            cursor = getContentResolver().query(CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, null);
            if (cursor == null) {
                Log.d(TAG, "queryStartApkList error: cursor == null!");
                return null;
            }
            Msg message = new Msg();
            while (cursor.moveToNext()) {
                message.mTitle = cursor.getString(cursor.getColumnIndex(MESSAGE_TITLE));
                message.mTime = cursor.getLong(cursor.getColumnIndex(MESSAGE_TIME));
                message.mExtra = cursor.getString(cursor.getColumnIndex(MESSAGE_EXTRA));
                message.mStatus = cursor.getInt(cursor.getColumnIndex(MESSAGE_STATUS));
                message.mCategory = cursor.getInt(cursor.getColumnIndex(MESSAGE_CATEGORY));
                if ((!TextUtils.isEmpty(message.mTitle)) && ((message.mTime + ONE_DAY) <= System.currentTimeMillis())) {
                    try {
                        JSONObject extra = new JSONObject(message.mExtra);
                        Log.d(TAG, "query Start Apk List    times :" + extra.getInt("times"));
                        if (extra.getInt("times") > 0) {
                            listMessage.add(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMessage;
    }

    private void changeADsSwitch() {
        String data = mMessage.mData;
        Log.d(TAG, "ads switch : " + data);

        if (TextUtils.isEmpty(data)) {
            return;
        }
        try {
            int adsSwitch = Integer.parseInt(data);
            if (adsSwitch == 0) {
                SystemProperties.set(AD_SWITCH_PROPERTY, "0");
            } else if (adsSwitch == 1) {
                SystemProperties.set(AD_SWITCH_PROPERTY, "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isVideoExist() {
        SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String lastMd5 = preference.getString("md5", null);
        boolean fileExist = false;
        if (lastMd5 != null && lastMd5.endsWith(mMessage.mExtra)) {
            fileExist = true;
        }
        Log.d(TAG, "boot video file exist=" + fileExist);
        return fileExist;
    }

    private void downloadBootVideo() {
        if (TextUtils.isEmpty(mMessage.mTitle)) {
            Log.d(TAG, "download url is null");
            changCmdState();
            return;
        }

        Request request = new Request(Uri.parse(mMessage.mTitle));
        // download file into /mnt/sdcard/Download/
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, BOOT_VIDEO_FILE_NAME);
        request.setVisibleInDownloadsUi(false);

        if (mDownloadManager == null) {
            mDownloadManager = new EosDownloadManager(getApplicationContext());
        }
        SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        long taskId = preference.getLong("taskId", -1);
        String taskUrl = preference.getString("bootVideoUrl", "");
        if (taskId != -1 && taskUrl.equals(mMessage.mTitle)) {
            mDownloadManager.restartTask(taskId, mBootVideoDownloadListener);
        } else {
            mDownloadTask = new EosDownloadTask(request, mBootVideoDownloadListener);
            long id = mDownloadManager.addTask(mDownloadTask);
            SharedPreferences.Editor edit = preference.edit();
            String[] array = mMessage.mExtra.split(",");
            edit.putString("md5", array[0]);
            edit.putString("videoTime", array[1]);
            edit.putLong("taskId", id);
            edit.putString("bootVideoUrl", mMessage.mTitle);
            edit.putString("engine", Constants.DOWNLOAD_ENGINE_HTTP);
            edit.commit();
        }
    }

    private void downloadBootVideoWithSN() {
        // FIXME 
        if (TextUtils.isEmpty(mMessage.mTitle)) {
            Log.d(TAG, "download url is null");
            changCmdState();
            return;
        }
        if (mSNManager == null) {
            mSNManager = new StreamNetManager(getApplicationContext());
        }
     
        deleteLastSNFile();
       
    }
    private void createTaskWithSNIfDeleteSucceed(){
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        Log.d(TAG, "Environment.getExternalStoragePublicDirectory()=" + downloadPath);

        final SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = preference.edit();
        mSNManager.createDownloadTask(mMessage.mTitle, downloadPath, downloadPath, new ResultListener() {
            @Override
            public void OnCreated(List<TaskInfoInternal> taskList) throws RemoteException {
                super.OnCreated(taskList);
                final TaskInfoInternal info = taskList.get(0);
                if (info == null) {
                    Log.d(TAG, "creat download task failed...");
                    changCmdState();
                    return;
                }

                // 存储下载的相关信息。
                String[] array = mMessage.mExtra.split(",");
                final String md5 = array[0];
                final String time = array[1];
                edit.putString("md5", md5);
                edit.putString("videoTime", time);
                edit.putString("taskIdSN", info.getPlayURL());
                edit.putString("bootVideoUrl", mMessage.mTitle);
                edit.putString("engine", Constants.DOWNLOAD_ENGINE_P2P);
                edit.commit();

                mSNManager.startDownload(info.getPlayURL());
                mSNManager.setTaskListener(info.getPlayURL(), new TaskListener() {
                    @Override
                    public void OnInfo(int progress, int speed) throws RemoteException {
                        super.OnInfo(progress, speed);
                        Log.d(TAG, "SN download progress:" + progress);
                    }

                    @Override
                    public void OnComplete() throws RemoteException {
                        super.OnComplete();
                        TaskInfoInternal infoTemp = mSNManager.getTaskInfo(info.getPlayURL());
                        Log.d(TAG, "SN download completed, target file=" + infoTemp.getDetail());
                        // md5校验
                        if (verify(infoTemp.getDetail(), md5)) {
                            File file = new File(BOOT_VIDEO_DIR_PATH + BOOT_VIDEO_PREDICT_FILE_NAME);
                            if (file.exists() && !file.isDirectory()) {
                                Log.d(TAG, "predict video is exist.");
                                setBootVideo(infoTemp.getDetail(), BOOT_VIDEO_DIR_PATH + "/" + BOOT_VIDEO_PREDICT_FILE_NAME, Constants.DOWNLOAD_ENGINE_P2P);
                            } else {
                                Log.d(TAG, "predict video is not exist.");
                                setBootVideo(infoTemp.getDetail(), BOOT_VIDEO_FILE_PATH, Constants.DOWNLOAD_ENGINE_P2P);
                            }
                            reportDataWithBootVideo(md5, 0, time);
                        } else {
                            Log.d(TAG, "SN download file's md5 is wrong.");
                            changCmdState();
                            reportDataWithBootVideo(md5, 3, time);
                            mSNManager.removeTaskAndFile(infoTemp.getDetail());
                            edit.remove("md5");
                            edit.remove("videoTime");
                            edit.remove("taskIdSN");
                            edit.remove("lastIdSN");
                            edit.remove("bootVideoUrl");
                            edit.remove("engine");
                            edit.commit();
                        }
                    }

                    @Override
                    public void OnTaskChanged(int state) throws RemoteException {
                        super.OnTaskChanged(state);
                        switch (state) {
                            case Constants.SN_TASK_STATE_ERROR:
                                reportDataWithBootVideo(md5, 1, time);
                            case Constants.SN_TASK_STATE_PAUSED:
                                changCmdState();
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void OnError(int code, String detail) throws RemoteException {
                        Log.d(TAG, "SN+ download failed, code=" + code + ", detail=" + detail);
                        super.OnError(code, detail);
                    }
                }, true);
            }

            @Override
            public void OnError(int code, String detail) throws RemoteException {
                changCmdState();
                Log.d(TAG, "SN+ create download task failed, code=" + code + ", detail=" + detail);
                String[] array = mMessage.mExtra.split(",");
                String md5 = array[0];
                String time = array[1];
                reportDataWithBootVideo(md5, 5, time);
                super.OnError(code, detail);
            }
        }, null);
    }

    // 继续下载开机视频
    private void redownloadBootVideo() {
        SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String engine = preference.getString("engine", "");
        Log.d(TAG, "begin to redownload with " + engine);
        if (TextUtils.isEmpty(engine)) {
            Log.d(TAG, "there is no redownload task.");
            return;
        }
        if (Constants.DOWNLOAD_ENGINE_HTTP.equals(engine)) {
            long taskId = preference.getLong("taskId", -1);
            if (taskId == -1) {
                return;
            }
            if (mDownloadManager == null) {
                mDownloadManager = new EosDownloadManager(getApplicationContext());
            }
            int status = mDownloadManager.getDownloadStatus(taskId);
            Log.d(TAG, "http boot video last download status=" + status);
            switch (status) {
                case DownloadManager.STATUS_FAILED:
                case DownloadManager.STATUS_PAUSED:
                case DownloadManager.STATUS_PENDING:
                    mDownloadManager.restartTask(taskId, mBootVideoDownloadListener);
                    break;

                default:
                    break;
            }
        } else if (Constants.DOWNLOAD_ENGINE_P2P.equals(engine)) {
            String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            Log.d(TAG, downloadPath + "is load=" + mSNManager.isDiskReady(downloadPath));
            if (CONNECT_TIMES<10) {
                if(!mSNManager.isDiskReady(downloadPath)) {
                    mSNManager.addDiskPath(downloadPath);
                    CONNECT_TIMES++;
                    mHandler2.sendEmptyMessageDelayed(MSG_DELAY_DOWNLOAD, 1000);
                } else {
                    mHandler2.removeMessages(MSG_DELAY_DOWNLOAD);
                    CONNECT_TIMES=0;
                    redownloadBootVideoWithSN();
                }
            } else {
                if(!mSNManager.isDiskReady(downloadPath)) {
                    CONNECT_TIMES=0;
                    mHandler2.removeMessages(MSG_DELAY_DOWNLOAD);
                    changCmdState();
                }
            }
//            while (!mSNManager.isDiskReady(downloadPath)) {
//                mSNManager.addDiskPath(downloadPath);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    mHandler2.sendEmptyMessageDelayed(MSG_DELAY_DOWNLOAD, 1000);
//                    e.printStackTrace();
//                }
//            }
//            if (mSNManager.isDiskReady(downloadPath)) {
//                redownloadBootVideoWithSN();
//            } else {
//                mSNManager.addDiskPath(downloadPath);
//                mHandler2.sendEmptyMessageDelayed(MSG_DELAY_DOWNLOAD, 1000);
//            }
        }
    }

    private void redownloadBootVideoWithSN() {
        Log.d(TAG, "redownloadBootVideoWithSN........");
        SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        final Editor edit = preference.edit();
        String taskIdSN = preference.getString("taskIdSN", "");
        if (TextUtils.isEmpty(taskIdSN)) {
            Log.d(TAG, "SN+ task id is empty");
            return;
        }
        if (mSNManager == null) {
            mSNManager = new StreamNetManager(getApplicationContext());
        }
        final TaskInfoInternal info = mSNManager.getTaskInfo(taskIdSN);
        final String md5 = preference.getString("md5", "");
        final String time = preference.getString("time", "");
        int status = info.getTaskState();
        Log.d(TAG, "SN boot video last download status=" + status);
        switch (status) {
            case Constants.SN_TASK_STATE_ERROR:
            case Constants.SN_TASK_STATE_PAUSED:
                mSNManager.startDownload(taskIdSN);
                mSNManager.setTaskListener(info.getPlayURL(), new TaskListener() {
                    @Override
                    public void OnInfo(int progress, int speed) throws RemoteException {
                        super.OnInfo(progress, speed);
                        Log.d(TAG, "SN download progress:" + progress);
                    }

                    @Override
                    public void OnComplete() throws RemoteException {
                        super.OnComplete();
                        Log.d(TAG, "SN download completed.");
                        // md5校验
                        if (!verify(info.getDetail(), md5)) {
                            Log.d(TAG, "download file's md5 is wrong.");
                            reportDataWithBootVideo(md5, 3, time);
                            mSNManager.removeTaskAndFile(info.getDetail());
                            edit.remove("md5");
                            edit.remove("videoTime");
                            edit.remove("taskIdSN");
                            edit.remove("lastIdSN");
                            edit.remove("bootVideoUrl");
                            edit.remove("engine");
                            edit.commit();
                        } else {
                            File file = new File(BOOT_VIDEO_DIR_PATH + BOOT_VIDEO_PREDICT_FILE_NAME);
                            if (!file.isDirectory() && file.exists()) {
                                Log.d(TAG, "predict video is exist.");
                                setBootVideo(info.getDetail(), BOOT_VIDEO_DIR_PATH + "/" + BOOT_VIDEO_PREDICT_FILE_NAME, Constants.DOWNLOAD_ENGINE_P2P);
                            } else {
                                Log.d(TAG, "predict video is not exist.");
                                setBootVideo(info.getDetail(), BOOT_VIDEO_FILE_PATH, Constants.DOWNLOAD_ENGINE_P2P);
                            }
                            reportDataWithBootVideo(md5, 0, time);
                        }
                    }

                    @Override
                    public void OnTaskChanged(int state) throws RemoteException {
                        super.OnTaskChanged(state);
                        switch (state) {
                            case Constants.SN_TASK_STATE_ERROR:
                                reportDataWithBootVideo(md5, 1, time);
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void OnError(int code, String detail) throws RemoteException {
                        Log.d(TAG, "SN+ download failed, code=" + code + ", detail=" + detail);
                        super.OnError(code, detail);
                    }
                }, true);
                break;
            case Constants.SN_TASK_STATE_COMPLETE:
            case Constants.SN_TASK_STATE_READY:
            case Constants.SN_TASK_STATE_RUNNING:
                break;
            default:
                break;
        }
    }

    // 恢复默认开机视频
    private void deleteVideoFile() {
        Log.d(TAG, "delete boot video file.");

        SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preference.edit();
        String md5 = preference.getString("md5", "");
        edit.remove("md5");
        edit.remove("taskId");
        edit.remove("bootVideoUrl");
        edit.remove("videoTime");
        edit.remove("engine");
        edit.remove("taskIdSN");
        edit.remove("lastIdSN");
        edit.commit();

        File file = new File(BOOT_VIDEO_FILE_PATH);
        if (file.exists() && !file.isDirectory()) {
            boolean flag = file.renameTo(new File(BOOT_VIDEO_DIR_PATH + "/" + BOOT_VIDEO_PREDICT_FILE_NAME));
            Log.d(TAG, "mv video.ts to video_predict " + (flag ? "success" : "fail"));
            if (flag) {
                reportDataWithBootVideo(md5, 2, mMessage.mExtra);
            } else {
                reportDataWithBootVideo(md5, 4, mMessage.mExtra);
            }
        } else {
            Log.d(TAG, "boot video file is not exist.");
        }
    }

    /**
     * 开机视频数据上报
     * @param md5 : 文件md5
     * @param status : 0文件下载成功；1文件下载失败，2文件删除成功, 3md5不匹配, 4文件删除失败，5任务创建失败
     * @param videoAddTime : 开机视频文件下载时间
     */
    private void reportDataWithBootVideo(final String md5, final int status, final String videoAddTime) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String request = getRequestString(md5, status, videoAddTime);
                Log.d(TAG, "request json=" + request);
                String response = getJsonString(request);
                Log.d(TAG, "response json=" + response);
            }
        }).start();
    }

    private String getRequestString(final String md5, final int status, String videoAddTime) {
        String devMac = Device.getHardwareAddress(getApplicationContext());
        if (devMac != null && !devMac.equals("")) {
            String[] strArray = devMac.split(":");
            StringBuffer modifiedMac = new StringBuffer();
            for (int i = 0; i < strArray.length; i++) {
                modifiedMac.append(strArray[i]);
            }
            devMac = modifiedMac.toString();
        } else {
            devMac = "000000000000";
        }

        try {
            JSONObject json = new JSONObject();
            json.put("ifid", "BootVideo");
            json.put("Ttag", Device.getDeviceCode());
            json.put("bbno", Device.getBb());
            json.put("mac", devMac);
            json.put("md5", md5);
            json.put("time", videoAddTime);
            json.put("status", status);
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJsonString(String json) {
        CpeManager manager = CpeManager.getInstance();
        HttpURLConnection conn = null;
        Writer writer = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            String serverUrl = SystemProperties.get(Constants.SERVER_URL_PROPERTY);
            Log.i(TAG, "server url :: " + serverUrl);
            URL url = new URL(serverUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
            conn.setRequestProperty("Ttag", manager.getProductClass());
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(20 * 1000);
            conn.setReadTimeout(20 * 1000);
            conn.setDoOutput(true);

            conn.connect();

            writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            writer.write(json);
            writer.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    private void deleteLastSNFile() {
        final SharedPreferences preference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        final String lastIdSN = preference.getString("taskIdSN", null);
          final String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        if (lastIdSN != null) {
            if (CONNECT_TIMES<10) {
                if(!mSNManager.isDiskReady(downloadPath)) {
                    mSNManager.addDiskPath(downloadPath);
                    CONNECT_TIMES++;
                    mHandler2.sendEmptyMessageDelayed(Msg_DELAY_DELETE, 1000);
                } else {
                    mHandler2.removeMessages(Msg_DELAY_DELETE);
                    Log.d(TAG, "begin to delete last file, lastIdSN=" + lastIdSN);
                    CONNECT_TIMES=0;
                    mSNManager.removeTaskAndFile(lastIdSN);
                    mHandler2.sendEmptyMessage(MSG_CREATE_TASK);
                }
            } else {
                if(!mSNManager.isDiskReady(downloadPath)) {
                    CONNECT_TIMES=0;
                    mHandler2.removeMessages(Msg_DELAY_DELETE);
                    changCmdState();
                }
            }
        }
    }

    /**
     * Verify package through md5.
     * 
     * @param path Package path.
     * @param md5 Md5 String from Server.
     * @return True if successful, otherwise false.
     */
    private boolean verify(String path, String md5) {
        File packageFile = new File(path);
        if (!packageFile.exists()) {
            Log.d(TAG, "Verify MD5, file " + path + " not exists");
            return false;
        }
        if (TextUtils.isEmpty(md5)) {
            Log.d(TAG, "Verify MD5, path=" + path + ", md5 is empty, force Successful");
            return true;
        }
        String fileMd5 = Util.calcMD5(packageFile);
        Log.d(TAG, "Verifly MD5, path=" + path + ", md5=" + md5 + ", fileMd5=" + fileMd5);
        return md5.equals(fileMd5);
    }

    private static final int MSG_DELAY_DOWNLOAD = 1;

    private static final int Msg_DELAY_DELETE = 2;
    
    private static final int MSG_CREATE_TASK = 3;

    private Handler mHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELAY_DOWNLOAD:
                    redownloadBootVideo();
                    break;
                case Msg_DELAY_DELETE:
                    deleteLastSNFile();
                    break;
                 case MSG_CREATE_TASK:
                     createTaskWithSNIfDeleteSucceed();

                default:
                    break;
            }
        };
    };

    private void startOtaUpdateService() {
        Intent intent = new Intent(ACTION_UPDATE_CLOUD_PUSH);
        intent.putExtra(EXTRA_CLOUD_PUSH_TASKID, mMessage.mData);
        boolean forcePush = Boolean.parseBoolean(mMessage.mExtra);
        Log.d(TAG, "EXTRA_CLOUD_PUSH_FORCE=" + forcePush);
        intent.putExtra(EXTRA_CLOUD_PUSH_FORCE, forcePush);
        startService(intent);
    }
}
