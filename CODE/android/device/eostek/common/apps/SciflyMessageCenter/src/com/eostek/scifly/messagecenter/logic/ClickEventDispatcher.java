
package com.eostek.scifly.messagecenter.logic;

import static com.eostek.scifly.messagecenter.util.Constants.APK_PLAYER_CLASS;
import static com.eostek.scifly.messagecenter.util.Constants.APK_PLAYER_KEY;
import static com.eostek.scifly.messagecenter.util.Constants.APK_PLAYER_TYPE;
import static com.eostek.scifly.messagecenter.util.Constants.AUDIO_PLAYER_CLASS;
import static com.eostek.scifly.messagecenter.util.Constants.AUDIO_PLAYER_KEY;
import static com.eostek.scifly.messagecenter.util.Constants.AUDIO_PLAYER_TYPE;
import static com.eostek.scifly.messagecenter.util.Constants.DOCX_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.DOC_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.EPUB_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.IMG_PLAYER_CLASS;
import static com.eostek.scifly.messagecenter.util.Constants.IMG_PLAYER_KEY;
import static com.eostek.scifly.messagecenter.util.Constants.IMG_PLAYER_TYPE;
import static com.eostek.scifly.messagecenter.util.Constants.PACKAGE_NAME;
import static com.eostek.scifly.messagecenter.util.Constants.PDF_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.PPTX_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.PPT_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.TXT_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.VIDEO_PLAYER_CLASS;
import static com.eostek.scifly.messagecenter.util.Constants.VIDEO_PLAYER_KEY;
import static com.eostek.scifly.messagecenter.util.Constants.VIDEO_PLAYER_TYPE;
import static com.eostek.scifly.messagecenter.util.Constants.XLSX_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.XLS_POSTFIX;

import java.io.File;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import scifly.datacache.DataCacheListener;
import scifly.datacache.DataCacheManager;
import scifly.datacache.DataCacheProgressListener;
import scifly.dm.EosDownloadListener;
import scifly.dm.EosDownloadManager;
import scifly.dm.EosDownloadTask;
import scifly.provider.SciflyStore.Messages;
import scifly.provider.metadata.Msg;
import scifly.provider.metadata.MsgService;
import scifly.provider.metadata.WebViewActivity;
import android.app.DownloadManager.Request;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.scifly.messagecenter.MainActivity;
import com.eostek.scifly.messagecenter.MessageCenterHolder;
import com.eostek.scifly.messagecenter.R;
import com.eostek.scifly.messagecenter.logic.SimpleAudioPlayer.OnAudioStopListener;
import com.eostek.scifly.messagecenter.ui.dialog.TextDetailDialog;
import com.eostek.scifly.messagecenter.util.Constants;
import com.eostek.scifly.messagecenter.util.Util;
import com.eostek.scifly.video.player.service.VideoInfo;
import com.media.eos.EosPlayer2;
import com.media.eos.EosPlayer2.OnCompletionListener;
import com.media.eos.EosPlayer2.OnPreparedListener;
import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * dispatcher the cilck event for message handle.
 */
public class ClickEventDispatcher {

    private static final String TAG = "ClickEventDispatcher";

    private static final int PERCENT_100 = 100;

    private MainActivity mContext;

    private int mPercent = PERCENT_100;

    private SimpleAudioPlayer mPlayer;

    private MessageDataCache mDataCache;

    private Map<Long, String> mTaskMap = new HashMap<Long, String>();

    private AnimationDrawable mAudioAnimation;

    private Msg mPlayingAudioMsg;

    private TextDetailDialog mTextDetailDialog;

    /**
     * Constructor.
     * 
     * @param holder
     * @param context
     */
    public ClickEventDispatcher(MessageCenterHolder holder, Context context) {
        this.mContext = (MainActivity) context;

        mDataCache = mContext.getDataCacheManager();

        // player init.:

        mPlayer = mContext.getAudioPlayer();

    }

    /**
     * click event dispatch
     * 
     * @param msg
     */
    public void clickEventDispatch(Msg msg) {
        if (msg == null || TextUtils.isEmpty(msg.mData)) {
            toast(R.string.msg_uri_empty);
            return;
        }

        msg.toString();
        Log.i(TAG, "[clickEventDispatch]msg.mData=" + msg.mData);
        Intent intent = new Intent();
        switch (msg.mCategory) {

            case Messages.CATEGORY_EPG:
                try {

                    // FIXME Testing Code:
                    intent = new Intent();
                    intent.setClassName("com.eostek.scifly.video", "com.eostek.scifly.video.player.DemandPlayer");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();

                    VideoInfo tempInfo = Util.parseVideoInfo(msg);

                    bundle.putParcelable("videoInfo", tempInfo);
                    intent.putExtras(bundle);
                    intent.putExtra("isFromThirdPartyApp", false);// 来自消息中心的赋值为false.
                    // Testing Code End.

                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.d(TAG, e.toString());
                }
                break;
            case Messages.CATEGORY_MSG_LIVE:
                try {
                    intent = new Intent();
                    intent.setClassName("com.eostek.scifly.video", "com.eostek.scifly.video.live.LiveActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();

                    HashMap<String, String> liveInfo = Util.parseLiveInfo(msg.mData);
                    if (liveInfo.containsKey(Constants.LIVE_KEY_PLAYURL)) {
                        bundle.putString(Constants.LIVE_KEY_PLAYURL, liveInfo.get(Constants.LIVE_KEY_PLAYURL));
                        bundle.putString(Constants.LIVE_KEY_PLAYTITLE, liveInfo.get(Constants.LIVE_KEY_PLAYTITLE));
                    } else if (liveInfo.containsKey(Constants.LIVE_KEY_PGM_ID)) {
                        bundle.putString(Constants.LIVE_KEY_PGM_ID, liveInfo.get(Constants.LIVE_KEY_PGM_ID));
                        bundle.putString(Constants.LIVE_KEY_CONTENT_ID, liveInfo.get(Constants.LIVE_KEY_CONTENT_ID));
                    }
                    bundle.putString(Constants.LIVE_KEY_CHANNEL_LOGO, msg.mThumb);
                    bundle.putBoolean(Constants.LIVE_FROM_MSG, true);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.d(TAG, e.toString());
                }
                break;
            case Messages.CATEGORY_TEXT:
                playTextMessage(msg);
                break;
            case Messages.CATEGORY_URL:
                playUrlMessage(msg);
                break;
            case Messages.CATEGORY_APK:
                if (Util.isInstalled(msg, mContext)) {
                    playApkFileDirect(msg);
                    break;
                }
            case Messages.CATEGORY_VIDEO:
            case Messages.CATEGORY_IMAGE:
            case Messages.CATEGORY_MUSIC:
            case Messages.CATEGORY_VOICE:
            case Messages.CATEGORY_DOCUMENT:
                playMediaFile(msg.mData, msg, DataCacheManager.DATA_CACHE_TYPE_FILE);
                break;
            case Messages.CATEGORY_EPG_CACHE:
                try {
                    Intent epgIntent = Intent.parseUri(msg.mData, Intent.URI_INTENT_SCHEME);
                    epgIntent.setClassName("com.eostek.scifly.video",
                            "com.eostek.scifly.video.download.DownloadVideoListActivity");
                    epgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    VideoInfo tempInfo = Util.parseVideoInfo(msg);
                    epgIntent.putExtra("ContentId", tempInfo.contentId);
                    epgIntent.putExtra("PgmContentId", tempInfo.pgmContentId);
                    epgIntent.putExtra("Channel", tempInfo.channelCode);

                    mContext.startActivity(epgIntent);
                } catch (URISyntaxException e) {
                    Log.d(TAG, e.toString());
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, R.string.not_app_tip, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }
    }

    private void playApkFileDirect(Msg msg) {
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(Util.parseUrl(msg).get("id").toString());
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            toast(R.string.not_app_tip);
        }
    }

    private void playTextMessage(Msg msg) {
        if (mTextDetailDialog == null) {
            mTextDetailDialog = new TextDetailDialog(mContext);
        }

        if (msg == null) {
            return;
        }

        String timeString = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);

        String content = msg.mData;

        mTextDetailDialog.show();
        mTextDetailDialog.setContent(timeString, content);

    }

    private void playApkFile(final String localPath, String clsName, String type, String from, Msg msg) {
        File file = new File(localPath);
        if (file.exists()) {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo newPackageInfo = packageManager.getPackageArchiveInfo(localPath, PackageManager.GET_ACTIVITIES);

            if (newPackageInfo == null) {
                Toast.makeText(mContext, R.string.bad_apk_file, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "" + mContext.getString(R.string.bad_apk_file));
                if (file.isFile() && file.exists()) {
                    file.delete();
                }
                return;
            }

            List<PackageInfo> packinfos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            int installedVerCode = 0;
            for (PackageInfo installedInfo : packinfos) {
                if (installedInfo.packageName.equals(newPackageInfo.packageName)) {
                    installedVerCode = installedInfo.versionCode;
                    if (newPackageInfo.versionCode == installedInfo.versionCode) {
                        Log.i(TAG, "该程序已存在,直接打开！");

                        Intent intent = packageManager.getLaunchIntentForPackage(newPackageInfo.packageName);
                        try {
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            toast(R.string.not_app_tip);
                        }

                    } else {
                        Log.i(TAG, "该程序已存在,询问用户是否替换！");
                        playMessage(localPath, clsName, type, from, msg);

                    }
                    Log.i("installedInfo.versionCode", "" + installedVerCode);
                    Log.i("newInfo.versionCode", "" + newPackageInfo.versionCode);
                    return;
                }
            }
            Log.i(TAG, "该程序不存在,询问用户是否安装！");
            playMessage(localPath, clsName, type, from, msg);
        }
    }

    private void playUrlMessage(Msg msg) {
        if (msg == null) {
            return;
        }
//FIXME
        if (TextUtils.isEmpty(msg.mData)) {
            Log.d(TAG, "Url must not be empty !");
            return;
        }
        Log.d(TAG, ">>>>>showWebView>>>>>" + msg.mData);
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("URL", msg.mData);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    // private VideoInfo parseVideoInfo(Msg msg) {
    // String mData = msg.mData;
    // VideoInfo info = new VideoInfo();
    // String url = "";
    // try {
    // url = mData.substring(0, mData.lastIndexOf("?"));
    //
    // String lastInfo = mData.substring(mData.lastIndexOf("?") + 1);
    // System.out.println(lastInfo);
    // String[] values = lastInfo.split("&");
    // for (String value : values) {
    // String[] mp = value.split("=");
    // if (mp[0].equals("sourceUrl")) {
    // url = mp[1];
    // } else if (mp[0].equals("programId")) {
    // info.programId = Integer.parseInt(mp[1]);
    // } else if (mp[0].equals("pgrpId")) {
    // info.pgrpId = Integer.parseInt(mp[1]);
    // } else if (mp[0].equals("channelCode")) {
    // info.channelCode = mp[1];
    // } else if (mp[0].equals("videoSource")) {
    // info.videoSource = mp[1];
    // } else if (mp[0].equals("videoName")) {
    // String fileName = URLDecoder.decode(mp[1]);
    // if (TextUtils.isEmpty(fileName)) {
    // fileName = msg.mTitle;
    // }
    // info.videoName = fileName;
    // } else if (mp[0].equals("curPosition")) {
    // info.curPosition = Integer.parseInt(mp[1]);
    // } else if (mp[0].equals("videoType")) {
    // info.videoType = Integer.parseInt(mp[1]);
    // } else if (mp[0].equals("programIndex")) {
    // info.programIndex = Integer.parseInt(mp[1]);
    // } else if (mp[0].equals("hd")) {
    // info.hd = Integer.parseInt(mp[1]);
    // } else if (mp[0].equals("contentId")) {
    // info.contentId = mp[1];
    // } else if (mp[0].equals("pgmContentId")) {
    // info.pgmContentId = mp[1];
    // }
    // }
    // } catch (Exception e) {
    // info.videoType = 0;
    // }
    //
    // info.videoUrl = url;
    //
    // return info;
    // }

    /**
     * simulate auto click event
     * 
     * @param msg
     */
    public void simulateAutoClick(final Msg msg) {
        if (msg == null || TextUtils.isEmpty(msg.mData)) {
            toast(R.string.msg_uri_empty);
            return;
        }

        Map<String, String> map = new HashMap<String, String>();

        switch (msg.mCategory) {

            case Messages.CATEGORY_MUSIC:
            case Messages.CATEGORY_VOICE:

                map.put(DataCacheManager.EXTRA_KEY_POSTFIX, ".ogg");

                mDataCache.loadCache(DataCacheManager.DATA_CACHE_TYPE_FILE, msg.mData, map, new MsgDataCacheListener(
                        msg, mTaskMap) {

                    int mPositionInQueue = -1;

                    @Override
                    public void onLoadingStarted(String requestUri, View view) {
                        super.onLoadingStarted(requestUri, view);
                        if(mPlayer !=null){
                        mPositionInQueue = mPlayer.getCount();
                        }

                        if (mPlayer != null && mPlayer.isPlaying()) {

                            final ViewTreeObserver observer = mContext.getHolder().getMessageGridView()
                                    .getViewTreeObserver();
                            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    observer.removeOnGlobalLayoutListener(this);
                                    ImageView img = null;
                                    int tag = ("voice" + mPlayingAudioMsg.mId).hashCode();
                                    img = (ImageView) getMsgImageByTag(tag);
                                    if (img != null) {
                                        try {
                                            mAudioAnimation = (AnimationDrawable) img.getBackground();
                                            mAudioAnimation.setOneShot(false);
                                            mAudioAnimation.start();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                        Log.i(TAG, "audio download completed");

                        mTaskMap.remove(msg.mId);

                        if (!(dataObject instanceof File)) {
                            return;
                        }

                        File file = (File) dataObject;

                        Log.i(TAG, "file.getAbsolutePath() = " + file.getAbsolutePath());

                        mPlayer.setOnPreparedListener(new MsgAudioOnPreparedListener(msg));

                        mPlayer.setOnCompletionListener(new MsgAudioOnCompletionListener(msg) {
                            @Override
                            public void onCompletion(EosPlayer2 arg0) {

                                if (mAudioAnimation != null && mAudioAnimation.isRunning()) {
                                    mAudioAnimation.selectDrawable(0);
                                    mAudioAnimation.stop();
                                }

                                mPlayer.playAudio();

                            }
                        });

                        mPlayer.setOnAudioStopListener(new MsgOnAudioStopListener(msg));

                        if (mPlayer.isPlaying()) {
                            Log.i(TAG, "正在播放，进入播放队列!新语音消息在队列中的位置为 : " + mPositionInQueue);
                            mPlayer.addPlayPathAt(mPositionInQueue, file.getAbsolutePath());
                            return;
                        }

                        mPlayer.addPlayPath(file.getAbsolutePath());

                        mPlayer.playAudio();

                    }
                }, null);
                break;
            default:
                break;
        }
    }

    private void playMediaFile(String uriStr, final Msg msg, int cacheType) {

        if (mTaskMap.containsKey(msg.mId)) {
            toast(R.string.please_wait);
            return;
        }

        final String descText;

        Map<String, String> map = new HashMap<String, String>();

        switch (msg.mCategory) {
            case Messages.CATEGORY_VIDEO:
                map.put(DataCacheManager.EXTRA_KEY_POSTFIX, ".mp4");
                break;
            case Messages.CATEGORY_IMAGE:
                map.put(DataCacheManager.EXTRA_KEY_POSTFIX, ".png");
                break;
            case Messages.CATEGORY_APK:
                map.put(DataCacheManager.EXTRA_KEY_POSTFIX, ".apk");
                break;
            case Messages.CATEGORY_MUSIC:
                map.put(DataCacheManager.EXTRA_KEY_POSTFIX, Util.getExtensionName(msg.mTitle));
                break;
            case Messages.CATEGORY_VOICE:
                map.put(DataCacheManager.EXTRA_KEY_POSTFIX, ".ogg");
                break;
            case Messages.CATEGORY_DOCUMENT:
                String postfix = msg.mTitle.toLowerCase(Locale.US);
                if (postfix.endsWith(TXT_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, TXT_POSTFIX);
                } else if (postfix.endsWith(DOC_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, DOC_POSTFIX);
                } else if (postfix.endsWith(DOCX_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, DOCX_POSTFIX);
                } else if (postfix.endsWith(XLS_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, XLS_POSTFIX);
                } else if (postfix.endsWith(XLSX_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, XLSX_POSTFIX);
                } else if (postfix.endsWith(PPT_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, PPT_POSTFIX);
                } else if (postfix.endsWith(PPTX_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, PPTX_POSTFIX);
                } else if (postfix.endsWith(PDF_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, PDF_POSTFIX);
                } else if (postfix.endsWith(EPUB_POSTFIX)) {
                    map.put(DataCacheManager.EXTRA_KEY_POSTFIX, EPUB_POSTFIX);
                } else {
                    toast(R.string.type_unkown);
                    return;
                }
                break;
            default:
                break;
        }

        String cachePath = Constants.CACHE_PATH + "/" + uriStr.hashCode() + map.get(DataCacheManager.EXTRA_KEY_POSTFIX);
        Log.d(TAG, "cachePath : " + cachePath);
        final File file = new File(cachePath);
        if (!file.exists()) {
            File parentFileDir = new File(file.getParent());
            if (!parentFileDir.exists()) {
                Log.d(TAG, "make parent dir : " + parentFileDir.mkdirs());
            }
            Log.d(TAG, "uriStr : " + uriStr);
            Request request = new Request(Uri.parse(uriStr));
            request.setDestinationInExternalPublicDir(Constants.PREFIX, "/" + uriStr.hashCode() + map.get(DataCacheManager.EXTRA_KEY_POSTFIX));
            EosDownloadListener listener = new EosDownloadListener() {
                
                @Override
                public void onDownloadStatusChanged(int status) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "" + status);
                }
                
                @Override
                public void onDownloadSize(long size) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "downloadSize : " + size);
                }
                
                @Override
                public void onDownloadComplete(final int percent) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "download percent : " + percent);
                    MainActivity activity = (MainActivity) mContext;
                    final TextView desc = (TextView) getViewByTag(msg.mId);
                    if (desc != null) {
                        if (percent != PERCENT_100) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    
                                    if (desc.getVisibility() != View.VISIBLE) {
                                        desc.setVisibility(View.VISIBLE);
                                    }
                                    
                                    desc.setText(mContext.getResources().getString(R.string.download_downloading)
                                            + (percent > 0 ? percent : 0) + "%");
                                    
                                    desc.getLayoutParams().width = LayoutParams.MATCH_PARENT;
                                }
                            });
                        } else {
                            if (msg.mTitle != null && !msg.mTitle.equals("null")) {
                                Log.i(TAG, "msg.mTitle " + msg.mTitle);
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        desc.setText(msg.mTitle);
                                    }
                                });
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        desc.setText("");
                                    }
                                });
                            }
                            playMessage(file, msg);
                        }
                    }
                }
            };
            
            EosDownloadTask task = new EosDownloadTask(request, listener);
            EosDownloadManager manager = new EosDownloadManager(mContext);
            long num = manager.addTask(task);
            Log.d(TAG, "num : " + num);
        } else {
            playMessage(file, msg);
        }
        
        /*mDataCache.loadCache(cacheType, uriStr, map, new MsgDataCacheListener(msg, mTaskMap),
                new DataCacheProgressListener() {

                    MainActivity activity = (MainActivity) mContext;

                    int lastPercent = -1;

                    boolean showWarning = true;

                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                        int EXPIRED_SIZE = 49;
                        if (total <= EXPIRED_SIZE && showWarning) {
                            toast(R.string.download_expired);
                            showWarning = false;
                        }

                        double percentage = current / (total * 1.0) * 100;

                        final int percent2show = (int) percentage;

                        if (lastPercent == percent2show) {
                            return;
                        }

                        lastPercent = percent2show;

                        if (percent2show % 30 == 0) {
                            // not to show too many log
                            Log.i(TAG, "percent" + percent2show + " msg.mId = " + msg.mId);
                        }

                        final TextView desc = (TextView) getViewByTag(msg.mId);
                        if (desc != null) {
                            if (percent2show != PERCENT_100) {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {

                                        if (desc.getVisibility() != View.VISIBLE) {
                                            desc.setVisibility(View.VISIBLE);
                                        }

                                        desc.setText(mContext.getResources().getString(R.string.download_downloading)
                                                + percent2show + "%");

                                        desc.getLayoutParams().width = LayoutParams.MATCH_PARENT;
                                    }
                                });
                            } else {
                                if (msg.mTitle != null && !msg.mTitle.equals("null")) {
                                    Log.i(TAG, "msg.mTitle " + msg.mTitle);
                                    desc.setText("" + msg.mTitle);
                                } else {
                                    desc.setText("");
                                }
                            }
                        }
                    }
                });*/

    }
    
    public String getMD5(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] inputByte = input.getBytes();
            digest.update(inputByte);
            byte[] result = digest.digest();
            
            return byteArrayToHex(result);
            
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    public String byteArrayToHex(byte[] byteArray){
        
        char[] chars = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F'};
        char[] resultChars = new char[byteArray.length * 2];
        
        int index = 0;
        for (byte b : byteArray) { 
            resultChars[index++] = chars[b>>>4 & 0xf];
            resultChars[index++] = chars[b & 0xf];
        }
        
        return new String(resultChars);
    }
    
    public void playMessage(File file, Msg msg) {
        Log.i(TAG, "file.getAbsolutePath() = " + file.getAbsolutePath());

        switch (msg.mCategory) {
            case Messages.CATEGORY_VIDEO:
                playMessage(file.getAbsolutePath(), VIDEO_PLAYER_CLASS, VIDEO_PLAYER_TYPE, VIDEO_PLAYER_KEY, msg);
                break;
            case Messages.CATEGORY_IMAGE:
                playMessage(file.getAbsolutePath(), IMG_PLAYER_CLASS, IMG_PLAYER_TYPE, IMG_PLAYER_KEY, msg);
                break;
            case Messages.CATEGORY_APK:
                playApkFile(file.getAbsolutePath(), APK_PLAYER_CLASS, APK_PLAYER_TYPE, APK_PLAYER_KEY, msg);
                break;
            case Messages.CATEGORY_MUSIC:
                playMessage(file.getAbsolutePath(), AUDIO_PLAYER_CLASS, AUDIO_PLAYER_TYPE, AUDIO_PLAYER_KEY, msg);
                break;
            case Messages.CATEGORY_VOICE:

                mPlayer.setOnPreparedListener(new MsgAudioOnPreparedListener(msg));

                mPlayer.setOnCompletionListener(new MsgAudioOnCompletionListener(msg));

                mPlayer.setOnAudioStopListener(new MsgOnAudioStopListener(msg));

                playMessage(file.getAbsolutePath(), AUDIO_PLAYER_CLASS, AUDIO_PLAYER_TYPE, AUDIO_PLAYER_KEY, msg);
                break;
            case Messages.CATEGORY_DOCUMENT:
                playMessage(file.getAbsolutePath(), null, null, null, msg);
                break;
            default:
                break;
        }
    }

    private void playMessage(final String localPath, String clsName, String type, String from, Msg msg) {

        if (type != null && type.equals(AUDIO_PLAYER_TYPE)) {
            mPlayer.clearPlayPath();
            mPlayer.addPlayPath(localPath);
            Log.i(TAG, "voice path " + localPath);
            mPlayer.playAudio();

            return;
        }

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
        if (msg.mCategory == Messages.CATEGORY_DOCUMENT) {
            intent.setData(Uri.parse("file://" + localPath));
            if (msg.mTitle.endsWith(EPUB_POSTFIX)) {
                intent.setClassName("com.chaozh.iReaderFree", "com.chaozh.iReader.ui.activity.WelcomeActivity");
            } else {
                intent.setClassName("cn.wps.moffice_eng", "cn.wps.moffice.documentmanager.PreStartActivity");
            }
        }

        if (type != null) {
            intent.setDataAndType(Uri.fromFile(new File(localPath)), type);
        }

        intent.setAction(Intent.ACTION_VIEW);
        if (from != null) {
            intent.putExtra(from, true);
        }

        try {
            if (mPercent == PERCENT_100) {
                mContext.startActivity(intent);
                Log.d(TAG, "Start Activity Success . " + clsName);
            } else {
                toast(R.string.please_wait);
            }
        } catch (ActivityNotFoundException e) {
            toast(R.string.not_app_tip);
        }
    }

    private void toast(final int toastId) {
        mContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mContext, toastId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View getViewByTag(long tag) {
        View desc = null;
        desc = mContext.getHolder().getMessageGridView().findViewWithTag(tag);
        return desc;
    }

    private View getMsgImageByTag(int tag) {
        View desc = null;
        desc = mContext.getHolder().getMessageGridView().findViewWithTag(tag);
        return desc;
    }

    /**
     * Listener for return the state of Message Datacache
     */
    private class MsgDataCacheListener extends DataCacheListener {

        private Msg msg;

        private Map<Long, String> taskMap;

        public MsgDataCacheListener(Msg msg, Map<Long, String> taskMap) {
            this.msg = msg;
            this.taskMap = taskMap;
        }

        @Override
        public void onLoadingStarted(String requestUri, View view) {
            taskMap.put(msg.mId, msg.mData);
        }

        @Override
        public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
            Log.i(TAG, "onLoadingFailed failReason : " + failReason.getType().name());
            taskMap.remove(msg.mId);
            toast(R.string.download_error_failed);
        }

        @Override
        public void onLoadingComplete(String requestUri, View view, Object dataObject) {
            Log.i(TAG, "onLoadingComplete requestUri = " + requestUri);

            taskMap.remove(msg.mId);

            if (!(dataObject instanceof File)) {
                return;
            }

            File file = (File) dataObject;

            Log.i(TAG, "file.getAbsolutePath() = " + file.getAbsolutePath());

            switch (msg.mCategory) {
                case Messages.CATEGORY_VIDEO:
                    playMessage(file.getAbsolutePath(), VIDEO_PLAYER_CLASS, VIDEO_PLAYER_TYPE, VIDEO_PLAYER_KEY, msg);
                    break;
                case Messages.CATEGORY_IMAGE:
                    playMessage(file.getAbsolutePath(), IMG_PLAYER_CLASS, IMG_PLAYER_TYPE, IMG_PLAYER_KEY, msg);
                    break;
                case Messages.CATEGORY_APK:
                    playApkFile(file.getAbsolutePath(), APK_PLAYER_CLASS, APK_PLAYER_TYPE, APK_PLAYER_KEY, msg);
                    break;
                case Messages.CATEGORY_MUSIC:
                    playMessage(file.getAbsolutePath(), AUDIO_PLAYER_CLASS, AUDIO_PLAYER_TYPE, AUDIO_PLAYER_KEY, msg);
                    break;
                case Messages.CATEGORY_VOICE:

                    mPlayer.setOnPreparedListener(new MsgAudioOnPreparedListener(msg));

                    mPlayer.setOnCompletionListener(new MsgAudioOnCompletionListener(msg));

                    mPlayer.setOnAudioStopListener(new MsgOnAudioStopListener(msg));

                    playMessage(file.getAbsolutePath(), AUDIO_PLAYER_CLASS, AUDIO_PLAYER_TYPE, AUDIO_PLAYER_KEY, msg);
                    break;
                case Messages.CATEGORY_DOCUMENT:
                    playMessage(file.getAbsolutePath(), null, null, null, msg);
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onLoadingCancelled(String requestUri, View view) {
            Log.i(TAG, "onLoadingCancelled");
            taskMap.remove(msg.mId);
        }

        @Override
        public void onCheckingComplete(String requestUri, View view, Object dataObject) {
            Log.i(TAG, "onCheckingComplete");
            taskMap.remove(msg.mId);
        }
    }

    /**
     * msgaudio onprepared listener.
     */
    private class MsgAudioOnPreparedListener implements OnPreparedListener {

        private Msg msg;

        public MsgAudioOnPreparedListener(Msg msg) {
            this.msg = msg;
        }

        @Override
        public void onPrepared(EosPlayer2 arg0) {

            mPlayer.start();

            if (mAudioAnimation != null && mAudioAnimation.isRunning()) {
                mAudioAnimation.selectDrawable(0);
                mAudioAnimation.stop();
            }

            ImageView img = null;
            int tag = ("voice" + msg.mId).hashCode();
            img = (ImageView) getMsgImageByTag(tag);
            if (img != null) {
                try {
                    mAudioAnimation = (AnimationDrawable) img.getBackground();
                    mAudioAnimation.setOneShot(false);
                    mAudioAnimation.start();
                    mPlayingAudioMsg = msg;
                    Log.i(TAG, "voice start " + msg.mId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * msg audio on completion listener.
     */
    private class MsgAudioOnCompletionListener implements OnCompletionListener {

        private Msg msg;

        public MsgAudioOnCompletionListener(Msg msg) {
            this.msg = msg;
        }

        @Override
        public void onCompletion(EosPlayer2 arg0) {
            Log.i(TAG, "voice end " + msg.mId);
            ImageView img = null;
            int tag = ("voice" + msg.mId).hashCode();
            img = (ImageView) getMsgImageByTag(tag);
            if (img != null) {
                try {
                    mAudioAnimation = (AnimationDrawable) img.getBackground();
                    if (mAudioAnimation != null && mAudioAnimation.isRunning()) {
                        mAudioAnimation.selectDrawable(0);
                        mAudioAnimation.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * msg on audio stop listener
     */
    private class MsgOnAudioStopListener implements OnAudioStopListener {

        private Msg msg;

        public MsgOnAudioStopListener(Msg msg) {
            this.msg = msg;
        }

        @Override
        public void onAudioStop() {

            if (mAudioAnimation != null && mAudioAnimation.isRunning()) {
                mAudioAnimation.selectDrawable(0);
                mAudioAnimation.stop();
            }

            Log.i(TAG, "voice stop " + msg.mId);
            ImageView img = null;
            int tag = ("voice" + msg.mId).hashCode();
            img = (ImageView) getMsgImageByTag(tag);
            if (img != null) {
                try {
                    mAudioAnimation = (AnimationDrawable) img.getBackground();
                    if (mAudioAnimation != null && mAudioAnimation.isRunning()) {
                        mAudioAnimation.selectDrawable(0);
                        mAudioAnimation.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopMessage() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }
}
