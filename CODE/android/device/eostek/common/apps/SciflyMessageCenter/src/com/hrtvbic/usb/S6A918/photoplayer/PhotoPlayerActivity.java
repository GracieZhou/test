
package com.hrtvbic.usb.S6A918.photoplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;

import scifly.provider.metadata.Msg;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eostek.scifly.messagecenter.R;
import com.eostek.scifly.messagecenter.datacenter.MsgCenterManager;
import com.eostek.scifly.messagecenter.util.Util;
import com.hrtvbic.usb.S6A918.model.FileItem;
import com.hrtvbic.usb.S6A918.model.FileItem.FileType;
import com.hrtvbic.usb.S6A918.util.BitmapUtils;
import com.hrtvbic.usb.S6A918.util.Utils;
//import org.videolan.vlc.AudioService;
//import org.videolan.vlc.VLCApplication;
//import com.mstar.android.MDisplay;
//import com.mstar.android.MDisplay.PanelMode;

public class PhotoPlayerActivity extends Activity {
    /** Called when the activity is first created. */
    private static final String TAG = "PhotoPlayerActivity";

    private PhotoPlayerViewHolder photoPlayHolder;

    public static final int OPTION_STATE_PRE = 0x01;

    public static final int OPTION_STATE_PLAY = 0x02;

    public static final int OPTION_STATE_NEXT = 0x03;

    public static final int OPTION_STATE_ENL = 0x04;

    public static final int OPTION_STATE_PPT_PLAY_MODE = 0x05;

    public static final int OPTION_STATE_PLAY_MODE = 0x06;

    public static final int OPTION_STATE_TURNRIGHT = 0x07;

    public static final int PLAY_MODE_ORDER = 0x08;

    public static final int PLAY_MODE_REPEAT = 0x09;

    public static final int PLAY_MODE_RANDOM = 0x010;

    public static final int PHOTO_CLOUD = 0x01;

    public static final int PHOTO_DLNA = 0x02;

    public static final int PHOTO_LOCAL = 0x03;

    public int pptPlayMode = 0;

    public int pptPlayMode_copy = 0;

    public boolean needRunAnimation = false;

    public boolean firstRemoveDisk = true;

    public int[] imageviewbglist = {

            R.id.photo_bgphoto1, R.id.photo_bgphoto2, R.id.photo_bgphoto3, R.id.photo_bgphoto4
    };

    public int[] imageviewlist = {
            R.id.photo_firstphoto, R.id.photo_secendphoto, R.id.photo_thirdphoto, R.id.photo_fourthphoto
    };

    public int[] publicImageViewList = {
            R.id.photo_previousphoto, com.eostek.scifly.messagecenter.R.id.photo_firstphoto, R.id.photo_secendphoto,
            R.id.photo_thirdphoto, R.id.photo_fourthphoto, com.eostek.scifly.messagecenter.R.id.photo_nextphoto,
            R.id.photo_image
    };

    public int[] isPhotoPlayModeString = {
            R.string.photo_normal, R.string.photo_fade, R.string.photo_right_to_left, R.string.photo_left_to_right,
            R.string.photo_up_to_down, R.string.photo_down_to_up, R.string.photo_large, R.string.photo_narrow
    };

    public int[] photoList = {
            1, 2
    };

    public boolean[] imageFlay = {
            true, true, true, true, true, true, true
    };

    // 后台音乐是否播放
    static PhotoPlayerActivity photoPlayerActivity = null;

    public final String PLAY_MODE = "PHOTO PLAY MODE"; // 播放模式的存储标记

    public final String PPT_PLAY_MODE = "PHOTO PPTPLAY MODE"; // ppt播放模式存储标记

    private static boolean isPlaying = false;

    private boolean localPath = false;// false表示http图片，true表示使用的本地图片

    static boolean bThreadRun = false;// 线程是否在执行

    static boolean activityIsFinish = false;

    // private PhotoDiskChangeReceiver diskReceiver; // 监听磁盘

    // private PhotoDlnaChangeReceiver dlnaReceiver; // DLNA监听器

    // private Change4K2KReceiver change4K2KReceiver; // 切换4K2K广播

    // 控制条是否显示
    private boolean photo_enl = false;

    private LinearLayout playControlLayout;

    // 控制条和名字默认消失的时间，5秒
    private static final int DEFAULT_TIMEOUT = 5000;

    private static String photoListDevID = ""; // DLNA设备的标识符

    // 隐藏视频控制条和视频名字
    public static final int HIDE_PLAYER_CONTROL = 9;

    public static final int HIDE_LEFT_CONTROL = 10;

    public static final int HIDE_TEXT_CONTROL = 11;

    public static final int HIDE_TEXT_ENLARGE_CONTROL = 12;

    public static final int MAX_PHOTO_BITMAP = 20;

    public static final int MAX_HTTP_PHOTO = 8;

    public List<FileItem> photoDataList = new ArrayList<FileItem>();

    // public ArrayList<String> photoBitmap_Name = new ArrayList<String>();

    public ArrayList<String> httpPhoto_Name = new ArrayList<String>();

    private Map<String, SoftReference<Bitmap>> thumbnailCache = new HashMap<String, SoftReference<Bitmap>>(); // 用来缓存生成的缩略图

    // 图片旋转时的缓冲区定义
    protected Bitmap[] publicBitmap = new Bitmap[8];

    // protected Bitmap copyBitmap;
    // 用于判断哪个控件获取焦点
    private int state = OPTION_STATE_PLAY;

    // 从PhotoActivity中选中要播放图片的索引，通过索引可以获取图片的详细信息
    public int currentPosition = 0;

    public int density;

    // 图片放大或缩小的倍率168
    protected int zoomTimes = 0;

    protected int windowWidth;

    protected int windowHeight;

    protected int photoTop;// 放大页面，缩略图框顶部的位置

    protected int photoBottom;

    protected int photoLeft;

    protected int photoRight;

    protected int imgPost = -1;// 中间变量，临时存储图片的当前位置

    protected boolean isPhotoPlay = false;// 判定图片是否正在播放

    protected boolean playByList = true;

    protected int playMode = 0;

    protected int playMode_copy = 0;

    protected final int PPT_PLAYER = 0x50000001;// 设置传递的消息值，图片正在播放

    protected final int STOCHASTIC_PLAYER = 0x50000002;

    protected final int LEFT_PHOTO_MSG = 0x60000000;

    public float pointx = 0;

    public float pointy = 0;

    public float rotate = 0;

    public float width, height;// 放大界面小的展示框里面的bitmap 离边框的距离 width表示x方向
                               // height表示y方向，至少有一个为0

    public Matrix matrix;

    public Rect rect;

    public float[] values = new float[9];

    public float scaledDown = 0;// 旋转时需要缩放的比例

    public ImageState mapState = new ImageState();

    public int m_index;// 当前图片在整个list里面的位置

    public int count;// 图片总数

    public int reflushPhotoIndex; // 左侧需要刷新的控件对应的图片的index

    public int m_option_index = 0;// 左侧光标的位置 （0-3）

    public long mCurTime = 0;// 保存当前的系统时间

    private int menustatus;

    private int menudefault = 127;

    // public int m_option_index_copy = 0;
    public String publicFileName;

    public int photoListIndex = 0;// 需要刷新的图片在数组 publicImageViewList中的index

    public Object xlock = new Object();

    class RunableInfo {
        private int photoListIndex = 0;// 需要刷新的图片在数组 publicImageViewList中的index

        public void setRunableInfoIndex(int index) {
            synchronized (xlock) {
                photoListIndex = index;
            }
        }

        public int getRunableInfoIndex() {
            synchronized (xlock) {
                return photoListIndex;
            }
        }
    };

    public String photoUrl;// 当前线程下载图片的地址（在每次启动线程之前设置）

    public Thread getInternetPhotoThread;// 处理图片缩略图的线程

    public final String dirPath = CACHE_PATH;

    class PhotoDownList {
        public String m_sfilename;

        public String m_surl;

        public int m_nid;

        public int m_nindex;

        PhotoDownList(String sname, String surl, int nid, int nindex) {
            m_surl = surl;
            m_nid = nid;
            m_nindex = nindex;
            m_sfilename = sname;
        }
    };

    class PhotoSetBitMapList {
        public String m_sfilename;

        public int m_nindex;

        public int m_nid;

        PhotoSetBitMapList(String sname, int nindex, int nid) {
            m_nindex = nindex;
            m_nid = nid;
            m_sfilename = sname;
        }
    };

    public Geometry geom;

    public class ImageState {
        public float left;

        public float top;

        public float right;

        public float bottom;
    };// 然后获取ImageView的matrix，根据matrix的getValues获得3x3矩阵。

    public ArrayList<PhotoDownList> photoDownList = new ArrayList<PhotoDownList>();// 用于下载图片的列表

    public ArrayList<PhotoSetBitMapList> photoSetBitMapList = new ArrayList<PhotoSetBitMapList>();// 用于刷新左侧图片

    private boolean is4K2KMode; // 判断是不是4K2K图片

    static boolean support4K2K; // 是否是4K2K平台

    private boolean panel4K2K; // 是否已经设置了mode

    private String dir4K2K; // 存取suffaceview绘图的图片地址

    private boolean button4K2K = true; // 开关(切换模式的时候使用)

    protected SharedPreferences mPreferences;

    private static final String PREFIX = "Android/data/com.eostek.scifly.messagecenter/cache/";

    private static final String CACHE_PATH = Environment.getExternalStoragePublicDirectory(PREFIX).getAbsolutePath();

    /**
     * 收到消息后退出播放器.
     */
    public static Handler notifyExitHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (null != photoPlayerActivity) {
                photoPlayerActivity.exitPlayer();
            }
        }
    };

    private void exitPlayer() {
        cleanPhotoCacheAndFinish();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setContentView(R.layout.photo_player);
        photoPlayerActivity = this;
        getWindow().setBackgroundDrawable(null);
        m_index = 0;
        findView();

        // is4K2KMode = TvPictureManager.getInstance().is4K2KMode();
        // is4K2KMode = (MDisplay.getPanelMode() ==
        // PanelMode.E_PANELMODE_4K2K_15HZ); //xiongz判断4K2K
        support4K2K = SystemProperties.getBoolean("mstar.4k2k.photo", false);
        // Log.i(TAG, "support4K2K: " + support4K2K);

        // if (support4K2K){
        // photoPlayHolder.imageSurfaceLL.setVisibility(View.VISIBLE);
        // photoPlayHolder.imageLL.setVisibility(View.INVISIBLE);
        //
        // }else{
        // photoPlayHolder.imageSurfaceLL.setVisibility(View.INVISIBLE);
        // photoPlayHolder.imageLL.setVisibility(View.VISIBLE);
        // }
        // if (is4K2KMode) {
        // photoPlayHolder.showImageSurfaceViewLL();
        // } else {
        // photoPlayHolder.showImageLL();
        // // photoPlayHolder.showImageSurfaceViewLL();
        // }

        density = getResources().getDisplayMetrics().densityDpi;

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        windowWidth = point.x;
        windowHeight = point.y;

        Log.i("ImageSurfaceView", "windowWidth: " + windowWidth + ", windowHeight: " + windowHeight);
        publicBitmap[7] = BitmapFactory.decodeResource(getResources(), R.drawable.type_photo);// 默认图片的位图
        Intent intent = getIntent();
        Log.e(TAG, "intent: " + intent);
        // 此处不能采用传递列表的方式，当数量达到两千就会出问题
        // photoDataList = bundle.getParcelableArrayList("browser.photo.list");
        String action = intent.getAction();
        Uri uri = intent.getData();
        String userId = (String) intent.getStringExtra("userId");
        Log.i(TAG, "msgCenter.imgPlayer:" + uri);
        Log.i(TAG, "msgCenter.userId:" + userId);

        // if (action == null) {
        // // photoDataList =
        // VLCApplication.getInstance().getCurrentMediaFolder().getChildren();
        // } else if (action.equals(Intent.ACTION_VIEW) && uri != null) {
        //
        // List<Msg> msgs =
        // MsgCenterManager.getInstance(this).getPhotosBySenderCategory(userId);
        // for(Msg msg:msgs){
        // String path = Util.getPathByName(CACHE_PATH, null, msg.mData);
        // File file = new File(path);
        //
        // FileItem item = new
        // MediaFileBuilderImpl(this,FileUtils.IMAGE_FILTER).buildItem(file);
        // photoDataList = Lists.newArrayList();
        // photoDataList.add(item);
        // }

        // }

        currentPosition = intent.getIntExtra("index", 0);
        photoListDevID = intent.getStringExtra("udn");
        Log.i(TAG, "===============photoList size===============:" + photoDataList.size());
        // count = photoDataList.size();
        // Log.i(TAG, "count = " + count);

        // FolderItem currentFolder =
        // VLCApplication.getInstance().getCurrentMediaFolder();

        // if (currentFolder instanceof DLNAFolder) {
        // localPath = false;
        // } else {
        // localPath = true;
        // }
        // FIXME
        localPath = true;

        // added by charles.tai [2014/06/20] begin
        boolean isFormMsgPhoto = intent.getBooleanExtra("fromMsgPhoto", false);
        if (isFormMsgPhoto) {
            photoDataList.clear();
            // photoDataList.add(new FileItem(uri, FileType.PHOTO,
            // Utils.getFileName(uri.toString())));
            // Log.d(TAG, "URI ： " + uri + "  FileName : " +
            // Utils.getFileName(uri.toString()));

            List<Msg> msgs = MsgCenterManager.getInstance(this).getPhotosBySenderCategory(userId);
            for (int i = 0; i < msgs.size(); i++) {
                Msg msg = msgs.get(i);
                Uri path = Util.getUriByName(CACHE_PATH, ".png", msg.mData);
                if (path.equals(uri)) {
                    currentPosition = i;
                }
                Log.d(TAG, "URI ： " + uri + "  FileName : " + msg.mData);
                Log.d(TAG, "path ： " + path);
                photoDataList.add(new FileItem(path, msg.mData, FileType.PHOTO, msg.mTitle));
            }
            count = photoDataList.size();
            Log.i(TAG, "count = " + count);
        }
        // added by charles.tai [2014/06/20] end
        m_index = currentPosition;
        Log.e(TAG, "currentPosition: " + currentPosition);

        playMode = Utils.getIntPref(photoPlayerActivity, PLAY_MODE, 0);
        pptPlayMode = Utils.getIntPref(photoPlayerActivity, PPT_PLAY_MODE, 0);
        // setPptPlayMode();
        photoPlayHolder.textphotoPptPlayMode.setText(isPhotoPlayModeString[pptPlayMode]);// init
                                                                                         // pptplay
                                                                                         // mode
        // setPlayMode();
        switch (playMode)// init playmode
        {
            case 0:
                photoPlayHolder.bt_photoPlayMode.setImageResource(R.drawable.music_play_mode_order);
                photoPlayHolder.textphotoPlayMode.setText(R.string.all_play);
                break;

            case 1:
                photoPlayHolder.bt_photoPlayMode.setImageResource(R.drawable.music_play_mode_all_repeat);
                photoPlayHolder.textphotoPlayMode.setText(R.string.all_repeat);
                break;

            case 2:
                photoPlayHolder.bt_photoPlayMode.setImageResource(R.drawable.music_play_mode_random);
                photoPlayHolder.textphotoPlayMode.setText(R.string.random_play);
                break;

        }
    }

    protected void onDestroy() {
        stopPhotoPlay();
        cleanBitmapArray();
        photoPlayerActivity = null;
        super.onDestroy();
    }

    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        photoPlayHolder.mImageView.setImageDrawable(null);
        photoPlayHolder.mImageView.clear();

        if (support4K2K && is4K2KMode) {
            panel4K2K = false;
            is4K2KMode = false;
            // MDisplay.setPanelMode(PanelMode.E_PANELMODE_NORMAL);
        }
        exitPlayer();

        // String pkgName = Utils.getDisplayActivityPackageName(this);
        // Log.i(TAG, "onStop pkgName: " + pkgName);
        // if (!pkgName.equals("com.hrtvbic.usb.S6A918")) {
        // if (pkgName.equals("com.haier.settings")) {
        // sendBroadcast(new Intent(AudioService.ACTION_REMOTE_STOP));
        // }
        //
        // finish();
        // }
    }

    protected void onPause() {

        /*
         * if (is4K2KMode) {
         * TvPictureManager.getInstance().enter4K2KMode(EN_4K2K_MODE
         * .E_NORMAL_MODE); }
         */

        // unregisterReceiver(diskReceiver);
        // unregisterReceiver(dlnaReceiver);
        // unregisterReceiver(change4K2KReceiver);

        // menustatus = SystemProperties.getInt("haier.menu-display-status",
        // 127);
        menustatus = menudefault & 127;
        SystemProperties.set("haier.menu-display-status", menustatus + "");
        Log.v(TAG, "=====onPause======menustatus============" + menustatus);
        super.onPause();
    }

    public void onStart() {
        super.onStart();
        if (count == 0) {
            finish();
            return;
        } else if (count <= currentPosition) {
            currentPosition = count - 1;
        }

        if (localPath)// 进入的时候发现文件不在就退出播放
        {
            if (currentPosition < count && currentPosition >= 0) {
                File f = new File(photoDataList.get(currentPosition).getLocation());
                if ((!f.exists())) {
                    Utils.showToast(PhotoPlayerActivity.this, R.string.music_file_not_found, Toast.LENGTH_SHORT);
                    finish();
                    return;
                }
            }
        }
        // File dir = new File(dirPath);
        // if ((dir.exists())) {
        // Utils.deleteFile(dirPath);
        // dir.delete();
        // }
        // dir.mkdir();
        activityIsFinish = false;
        bThreadRun = false;
        initBitmap();// 中间的大图
        hideImage(count);
        initLeftPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // // 创建磁盘事件接收器（监听插拔事件）
        // IntentFilter filter = new IntentFilter();
        // filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        // filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        // filter.addAction(Intent.ACTION_MEDIA_EJECT);
        // filter.addDataScheme("file");
        // diskReceiver = new PhotoDiskChangeReceiver();// 磁盘监听器
        // registerReceiver(diskReceiver, filter);

        // // 创建DLNA事件接收器（监听断开事件）
        // IntentFilter filter2 = new IntentFilter();
        // filter2.addAction(DLNAScanner.DEVICE_DISAPPEAR);
        // dlnaReceiver = new PhotoDlnaChangeReceiver();
        // registerReceiver(dlnaReceiver, filter2);
        //
        // IntentFilter filter3 = new IntentFilter();
        // filter3.addAction("com.haiertv.Change.4K2K");
        // change4K2KReceiver = new Change4K2KReceiver();
        // registerReceiver(change4K2KReceiver, filter3);

        // menustatus = SystemProperties.getInt("haier.menu-display-status",
        // 127);
        menustatus = menudefault & 55;
        // SystemProperties.set("haier.menu-display-status", menustatus + "");
        Log.v(TAG, "=====onResume======menustatus============" + menustatus);
    }

    /**
     * 初始化bitmap显示的数组.
     */
    private void initBitmap() {
        // 解析第一张显示的图片
        refurbishBigPhoto(m_index, R.id.photo_image);
        Log.i(TAG, "width=" + windowWidth + ", height=" + windowHeight);
    }

    // 获取网络图片的位图
    private void getHttpBitmap(String name, String url, int id, int index) {
        if (url == null)
            return;
        if (id == R.id.photo_image || id == R.id.photo_surface)// 如果是中间的大图则显示缓冲动画
        {
            photoPlayHolder.photo_progressbar.setVisibility(View.VISIBLE);
        }
        PhotoDownList downPath = new PhotoDownList(name, url, id, index);
        photoDownList.add(downPath);// 加入任务到下载列表里面
        synchronized (this) {
            beginDownPhoto();
        }
    }

    private void beginDownPhoto() {
        if (bThreadRun == true)// 如果线程在执行，返回 如果没有执行判断是否需要执行
        {
            return;
        } else {
            Log.i(TAG, "beginDownPhoto  photoDownList.size()=" + photoDownList.size());
            if (photoDownList.size() > 0) {
                bThreadRun = true;
                int id = photoDownList.get(0).m_nid;
                int index = 0;
                while (id != publicImageViewList[index])// 判断当前需要刷新的是哪一个控件
                {
                    index++;
                }
                if (index > 6) {
                    photoDownList.remove(0);// 移除第一个结点
                    return;
                }
                photoListIndex = index;// ???noted by youpeng.one
                reflushPhotoIndex = photoDownList.get(0).m_nindex;
                photoUrl = photoDownList.get(0).m_surl;
                publicFileName = photoDownList.get(0).m_sfilename;
                photoDownList.remove(0);// 移除第一个结点
                Log.i(TAG, "==========================reflushPhotoIndex=" + reflushPhotoIndex + "==========");
                Log.e(TAG, "-" + photoListIndex);
                Log.e(TAG, "-" + photoUrl);
                getInternetPhotoThread = new Thread(getInternetPhoto);
                getInternetPhotoThread.start();
            } else
                return;
        }
    }

    private void beginReflushPhoto() //
    {
        if (bThreadRun == true)// 如果线程在执行，返回 如果没有执行判断是否需要执行
            return;
        else {
            if (photoSetBitMapList.size() > 0) {
                bThreadRun = true;
                int id = photoSetBitMapList.get(0).m_nid;
                int index = 0;
                while (id != publicImageViewList[index])// 判断当前需要刷新的是哪一个控件
                {
                    index++;
                }
                if (index > 6) {
                    photoSetBitMapList.remove(0);// 移除第一个结点
                    return;
                }
                photoListIndex = index;
                reflushPhotoIndex = photoSetBitMapList.get(0).m_nindex;
                publicFileName = photoSetBitMapList.get(0).m_sfilename;
                photoSetBitMapList.remove(0);// 移除第一个结点
                Log.e(TAG, "-" + reflushPhotoIndex);
                Log.e(TAG, "-" + photoListIndex);
                getInternetPhotoThread = new Thread(getInternetPhoto);
                getInternetPhotoThread.setPriority(Thread.MAX_PRIORITY);
                getInternetPhotoThread.start();
            } else
                return;
        }
    }

    Runnable getInternetPhoto = new Runnable() {
        public void run() {
            if (reflushPhotoIndex >= photoDataList.size()) {
                return;
            }

            FileItem fileInfo = photoDataList.get(reflushPhotoIndex);
            if (!fileExists()) {
                Log.i(TAG, "getInternetPhoto  start httpdown\n");
                // if (fileInfo.getSize2() >= 25 * 1024 * 1024) {
                // Message msg = leftPhotoHandle.obtainMessage();// 启动下一个图片
                // msg.what = LEFT_PHOTO_MSG + 8;
                // leftPhotoHandle.sendMessage(msg);
                // return;
                // }
                // String httpphotoname = getFileName(photoUrl);
                String f = Util.getPathByName(CACHE_PATH, ".png", fileInfo.getDownloadURL());// dirPath
                                                                                             // +
                                                                                             // "/http"
                                                                                             // +
                                                                                             // httpphotoname;//
                                                                                             // 设置保存路径
                if (f == null) {
                    Message msg = leftPhotoHandle.obtainMessage();// 启动下一个图片
                    msg.what = LEFT_PHOTO_MSG + 8;
                    leftPhotoHandle.sendMessage(msg);
                    return;
                }
                String httpphoto = f;// "http" + httpphotoname;//
                                     // 设置arraylist里面的文件名
                int httpPhotoIndex = httpPhotoIsExist(httpphoto);
                int photoBitMapIndex = bitmapIsExist(f);
                File photofile = new File(f);
                // 保存网络图片到本地
                if ((httpPhotoIndex < 0 && photoBitMapIndex < 0) || (httpPhotoIndex < 0 && photoListIndex == 6)
                        || (httpPhotoIndex > 0 && !photofile.exists()))// 需要下载图片
                {
                    Log.i(TAG, "begin down photo " + photoUrl + "\n");
                    if (!downLoadPhoto(photoUrl, f)) {
                        Message msg = leftPhotoHandle.obtainMessage();// 如果抛出异常则不刷新当前图片并启动下一个图片
                        msg.what = LEFT_PHOTO_MSG + 8 + photoListIndex;
                        leftPhotoHandle.sendMessage(msg);
                        return;
                    } else {
                        addHttpPhotoName(httpphoto, httpPhotoIndex);// 刷新arraylist
                    }
                } else// 图片已经在本地了
                {
                    addHttpPhotoName(httpphoto, httpPhotoIndex);// 刷新arraylist
                }
                if (photoListIndex != 6)// 如果是左侧列表
                {
                    String filename = getFileName(photoUrl);
                    int nfileindex = bitmapIsExist(filename);
                    if (nfileindex < 0)// 本地不存在缩略图时，进行解析并存到本地
                    {
                        if (activityIsFinish == true)
                            return;
                        Log.i(TAG, "保存第" + (reflushPhotoIndex + 1) + "张缩略图" + "******************");
                        Bitmap thumbnail;
                        File newfile = new File(f);
                        if (newfile.exists()) {
                            if (newfile.length() >= 25 * 1024 * 1024) {
                                Message msg = leftPhotoHandle.obtainMessage();// //
                                                                              // 图片太大就直接返回
                                msg.what = LEFT_PHOTO_MSG + 8 + photoListIndex;
                                leftPhotoHandle.sendMessage(msg);
                                return;
                            }
                        }
                        // thumbnail = decodeBitmap(f, 150, 150);
                        // if (thumbnail != null) {
                        // if (thumbnail.getWidth() > 150 &&
                        // thumbnail.getHeight() > 150)// 判断到底缩放是不是成功的
                        // {
                        // thumbnail = myExtractThumbnail(thumbnail, 150, 150);
                        // }
                        // }
                        // if (thumbnail != null) {
                        // thumbnailCache.put(fileInfo.getLocation(), new
                        // SoftReference<Bitmap>(thumbnail));
                        // saveToLocalPhoto(thumbnail, dirPath + "/" +
                        // filename);
                        // // if ()
                        // // addBitmapName(filename, nfileindex);
                        // }
                    } else {
                        if (activityIsFinish == true)
                            return;
                        // Bitmap thumbnail = decodeBitmap((dirPath + "/" +
                        // filename), 150, 150);
                        // if (thumbnail != null) {
                        // thumbnailCache.put(fileInfo.getLocation(), new
                        // SoftReference<Bitmap>(thumbnail));
                        // Log.i(TAG, "***********保存第" + (reflushPhotoIndex + 1)
                        // + "张缩略图" + "******************");
                        // // addBitmapName(filename, nfileindex);
                        // }
                    }
                } else {
                    if (activityIsFinish == true)
                        return;
                    File newfile = new File(f);
                    if (newfile.exists()) {
                        if (newfile.length() >= 25 * 1024 * 1024) {
                            Message msg = leftPhotoHandle.obtainMessage();// //
                                                                          // 图片太大就直接返回
                            msg.what = LEFT_PHOTO_MSG + 8 + photoListIndex;
                            msg.arg1 = 1000;
                            leftPhotoHandle.sendMessage(msg);
                            return;
                        }
                    }

                    if (support4K2K && is4K2KPhoto(f)) {
                        is4K2KMode = true;
                    } else {
                        is4K2KMode = false;
                    }
                    dir4K2K = f;
                    publicBitmap[6] = decodeBitmap(f, windowWidth, windowHeight);

                    // Bitmap thumbnail = null;
                    // if (publicBitmap[6] != null) {
                    // thumbnail = myExtractThumbnail(publicBitmap[6], 150,
                    // 150);
                    // }
                    // if (thumbnail != null) {
                    // thumbnailCache.put(fileInfo.getLocation(), new
                    // SoftReference<Bitmap>(thumbnail));
                    // }

                }
                Message msg = leftPhotoHandle.obtainMessage();// bitmap生成完之后发送消息，刷新界面
                msg.what = LEFT_PHOTO_MSG + photoListIndex;
                msg.arg2 = reflushPhotoIndex;
                leftPhotoHandle.sendMessage(msg);
            } else// 异步处理本地图片
            {
                String filename = getFileName(fileInfo.getLocation());
                int nfileindex = bitmapIsExist(filename);// 判断这个文件名是否加入了arraylist
                if (photoListIndex == 6) {
                    if (activityIsFinish == true)
                        return;
                    File newfile = new File(fileInfo.getLocation());
                    if (newfile.exists()) {
                        if (newfile.length() >= 25 * 1024 * 1024) {
                            Message msg = leftPhotoHandle.obtainMessage();// 图片太大就直接返回
                            msg.what = LEFT_PHOTO_MSG + 8 + photoListIndex;
                            msg.arg1 = 1000;
                            leftPhotoHandle.sendMessage(msg);

                            publicBitmap[6] = publicBitmap[7];
                            thumbnailCache.put(fileInfo.getLocation(), new SoftReference<Bitmap>(publicBitmap[6]));
                            Message m = leftPhotoHandle.obtainMessage();// bitmap生成完之后发送消息，刷新界面
                            m.what = LEFT_PHOTO_MSG + photoListIndex;
                            m.arg2 = reflushPhotoIndex;
                            if (photoPlayHolder.mImageSurface.isCreate) {
                                leftPhotoHandle.sendMessage(m);
                            } else {
                                leftPhotoHandle.sendMessageDelayed(m, 500);
                            }
                            return;
                        }
                    }

                    if (support4K2K && is4K2KPhoto(fileInfo.getLocation())) {
                        is4K2KMode = true;

                    } else {
                        is4K2KMode = false;
                    }
                    dir4K2K = fileInfo.getLocation();
                    publicBitmap[6] = decodeBitmap(fileInfo.getLocation(), windowWidth, windowHeight);

                    // Bitmap thumbnail = null;
                    // if (publicBitmap[6] != null) {
                    // thumbnail = myExtractThumbnail(publicBitmap[6], 150,
                    // 150);
                    // }
                    // if (thumbnail != null) {
                    // thumbnailCache.put(fileInfo.getLocation(), new
                    // SoftReference<Bitmap>(thumbnail));
                    // }

                } else {
                    if (nfileindex < 0)// 本地不存在缩略图时，进行解析并存到本地
                    {
                        if (activityIsFinish == true)
                            return;
                        File newfile = new File(fileInfo.getLocation());
                        if (newfile.exists()) {
                            if (newfile.length() >= 25 * 1024 * 1024) {
                                Message msg = leftPhotoHandle.obtainMessage();// 图片太大就直接返回
                                msg.what = LEFT_PHOTO_MSG + 8 + photoListIndex;
                                leftPhotoHandle.sendMessage(msg);
                                return;
                            }
                        }
                        // Bitmap thumbnail = null;
                        // thumbnail = decodeBitmap(fileInfo.getLocation(), 150,
                        // 150);
                        // if (thumbnail != null) {
                        // thumbnailCache.put(fileInfo.getLocation(), new
                        // SoftReference<Bitmap>(thumbnail));
                        // saveToLocalPhoto(thumbnail, dirPath + "/" +
                        // filename);
                        // // if ()// 存为本地缩略图
                        // // {
                        // // addBitmapName(filename, nfileindex);
                        // // }
                        // }
                    } else// 直接使用本地缩略图
                    {
                        Log.i(TAG, " 使用本地缩略图 " + dirPath + "/" + filename);
                        if (activityIsFinish == true)
                            return;
                        // Bitmap thumbnail = null;
                        // {
                        // thumbnail = decodeBitmap(fileInfo.getLocation(), 150,
                        // 150);
                        // }
                        // if (thumbnail != null) {
                        // thumbnailCache.put(fileInfo.getLocation(), new
                        // SoftReference<Bitmap>(thumbnail));
                        // // addBitmapName(filename, nfileindex);
                        // }
                    }
                }

                Message msg = leftPhotoHandle.obtainMessage();// bitmap生成完之后发送消息，刷新界面
                msg.what = LEFT_PHOTO_MSG + photoListIndex;
                msg.arg2 = reflushPhotoIndex;
                if (photoPlayHolder.mImageSurface.isCreate) {
                    leftPhotoHandle.sendMessage(msg);
                } else {
                    leftPhotoHandle.sendMessageDelayed(msg, 500);
                }
            }
        }
    };

    /**
     * 进入图片播放器时，隐藏不需要的控件
     */
    public void hideImage(int count) {
        if (count < 6) {
            for (int i = count + 1; i <= 5; i++) {
                ImageView image = (ImageView) findViewById(publicImageViewList[i]);
                imageFlay[i] = false;
                image.setVisibility(View.INVISIBLE);// 影藏左侧不需要的图片控件
            }
        }
        if (count < 6)// 隐藏上方箭头下面的控件
        {
            ImageView image = (ImageView) findViewById(publicImageViewList[0]);
            imageFlay[0] = false;
            image.setVisibility(View.INVISIBLE);
        }
    }

    public void addHttpPhotoName(String name, int index)// 添加的网络图片的名字和插入的位置
    {
        String s = name;
        Log.i(TAG, "add photo's  to arraylist");
        if (index >= 0)// 该名字已经存在
        {
            httpPhoto_Name.remove(index);
            httpPhoto_Name.add(s);
        } else {
            if (httpPhoto_Name.size() == MAX_HTTP_PHOTO) {
                File f = new File(dirPath + "/" + httpPhoto_Name.get(0));// delelt
                                                                         // //删除文件
                if (!f.delete()) {
                    Log.d(TAG, "file delete failed...");
                    return;
                }
                httpPhoto_Name.remove(0);
                httpPhoto_Name.add(s);
            } else {
                httpPhoto_Name.add(s);
            }
        }
    }

    public int httpPhotoIsExist(String name)// 返回-1表示网络图片不存在，>=0表示存在，
    {
        int num = 0;
        int index = -1;
        while (num < httpPhoto_Name.size()) {
            if (name.equals(httpPhoto_Name.get(num))) {
                index = num;
                break;
            }
            num++;
        }
        return index;
    }

    public int bitmapIsExist(String name)// 返回-1表示缩略图不存在，>=0表示存在，
    {
        return -1;
        // int num = 0;
        // int index = -1;
        // Log.i(TAG, "bitmap'sname=*************" + name +
        // "photoBitmap_Name.size()=" + photoBitmap_Name.size());
        // while (num < photoBitmap_Name.size()) {
        // if (name.equals(photoBitmap_Name.get(num))) {
        // index = num;
        // break;
        // }
        // num++;
        // }
        // return index;
    }

    // /**
    // * 添加任务（生成缩略图）
    // */
    // public void addBitmapName(String name, int index)// 添加的缩略图名字和插入的位置
    // {
    // String s = name;
    // Log.i(TAG, "add bitmap's name to arraylist");
    // if (index >= 0)// 改名字已经存在
    // {
    // photoBitmap_Name.remove(index);
    // photoBitmap_Name.add(s);
    // } else {
    // if (photoBitmap_Name.size() >= MAX_PHOTO_BITMAP) {
    // File f = new File(dirPath + "/" + photoBitmap_Name.get(0));// delelt
    // // //删除文件
    // f.delete();
    // photoBitmap_Name.remove(0);
    // photoBitmap_Name.add(s);
    // } else {
    // photoBitmap_Name.add(s);
    // }
    // }
    // }

    /**
     * 生成对应的缩略图,并设置
     */
    protected void setBitmap(String name, int index, int id) // 生成对应的缩略图,并设置
    {
        PhotoSetBitMapList setBitMapList = new PhotoSetBitMapList(name, index, id);
        photoSetBitMapList.add(setBitMapList);// 加入任务到下载列表里面
        synchronized (this) {
            Log.i(TAG, "make and setbitmap");
            beginReflushPhoto();
        }
    }

    /**
     * 下载图片 _photoUrl 图片的网络地址 _filePath 文件保存路径
     */
    public boolean downLoadPhoto(String _photoUrl, String _filePath) {
        FileOutputStream fos = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        boolean isDownPhotoSuccess = true;
        try {
            URL url = new URL(_photoUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(3 * 1000);
            conn.setReadTimeout(3 * 1000);
            conn.connect();
            is = conn.getInputStream();
            fos = new FileOutputStream(new File(_filePath));
            byte[] buffer = new byte[1024];
            if (is != null)
                for (int len = 0; true;) {
                    try {
                        len = is.read(buffer);
                    } catch (IOException e) {
                        isDownPhotoSuccess = false;
                        break;
                    }
                    if (len == -1) {
                        Log.i("***************", "len == -1*************");
                        break;
                    }
                    fos.flush();
                    fos.write(buffer, 0, len);
                }
        } catch (NullPointerException e) {
            isDownPhotoSuccess = false;
            e.printStackTrace();
        } catch (ConnectTimeoutException e) {
            isDownPhotoSuccess = false;
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            isDownPhotoSuccess = false;
            e.printStackTrace();
        } catch (IOException e) {
            isDownPhotoSuccess = false;
            e.printStackTrace();
        } catch (OutOfMemoryError err) {
            isDownPhotoSuccess = false;
            err.printStackTrace();
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != conn) {
                conn.disconnect();
                conn = null;
            }
        }
        return isDownPhotoSuccess;
    }

    /**
     * 把bitmap存为本地图片 bitmap filePath 文件路径
     */
    public boolean saveToLocalPhoto(Bitmap bitmap, String filePath) {
        if (bitmap == null)
            return false;
        boolean saveToLocal = true;
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
            saveToLocal = false;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            saveToLocal = false;
            return false;
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
            Log.i(TAG, "getInternetPhoto" + " save");
        } catch (IOException e) {
            e.printStackTrace();
            saveToLocal = false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            saveToLocal = false;
        }
        return saveToLocal;
    }

    /**
     * 截取文件的名字
     */
    public String getFileName(String pathandname) {// 截取文件的名字

        try {
            int start = pathandname.lastIndexOf("/");
            if (start != -1) {
                return pathandname.substring(start + 1);
            }
            return pathandname;
        } catch (NullPointerException e) {
            Log.i("NullPointerException", "\n");
            return null;
        } catch (IndexOutOfBoundsException e) {
            Log.i("IndexOutOfBoundsException", "\n");
            return null;
        }
    }

    /**
	 * 
	 * 
	 */
    protected void setBitmapp(int index, int id, int nwidth, int nheight) // 生成对应的缩略图,并设置,需要传入控件的宽和高
    {
        Bitmap bitmap = decodeBitmap(photoDataList.get(index).getLocation(), nwidth, nheight);
        ImageView iv = (ImageView) findViewById(id);
        if (bitmap != null)
            iv.setImageBitmap(bitmap);
        else {
            iv.setImageBitmap(publicBitmap[7]);
        }
        bitmap = null;
    }

    // 处理图片播放与暂停
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == PPT_PLAYER) {// ppt播放图片.
                if (isPhotoPlay) {
                    if (!bThreadRun)// 如果现在还在跑 就再等5秒
                    {
                        moveNextOrPrevious(1);
                    }
                    if (pptPlayMode == 0) {
                        handler.sendEmptyMessageDelayed(PPT_PLAYER, 5000);
                    }// 每个5秒钟发送一次消息（即每个5秒播放一张图片）
                    else {
                        handler.sendEmptyMessageDelayed(PPT_PLAYER, 9000);
                    }

                }
            } else if (msg.what == STOCHASTIC_PLAYER) {
                if (!bThreadRun)// 如果现在还在跑 就再等5秒
                {
                    randomPlay();
                }
                if (pptPlayMode == 0) {
                    handler.sendEmptyMessageDelayed(STOCHASTIC_PLAYER, 5000);
                }// 每个5秒钟发送一次消息（即每个5秒播放一张图片）
                else {
                    handler.sendEmptyMessageDelayed(STOCHASTIC_PLAYER, 9000);
                }
            }
        }
    };

    /**
     * 清除所有的bitmapArray.
     */
    protected void cleanBitmapArray() {
        for (int i = 0; i < publicBitmap.length; i++) {
            if (publicBitmap[i] != null) {
                if (!publicBitmap[i].isRecycled())
                    publicBitmap[i].recycle();
                publicBitmap[i] = null;
            }
        }
        if (publicBitmap[6] != null) {
            if (!publicBitmap[6].isRecycled())
                publicBitmap[6].recycle();
        }
    }

    /**
     * 删除本地图片缓存
     * 
     * @param null
     * @return void
     */
    protected void cleanPhotoCacheAndFinish() {
        bThreadRun = true;
        activityIsFinish = true;
        // while (getInternetPhotoThread.isAlive()) {
        // // Log.i("TAT ", "getInternetPhotoThread is alive!" );
        // }
        if (handler != null)// 退出之前移除消息
        {
            handler.removeMessages(PPT_PLAYER);
            handler.removeMessages(STOCHASTIC_PLAYER);
            handler = null;
        }
        if (hideHandler != null)// 退出之前移除消息
        {
            cancleDelayHide();
            hideHandler = null;
        }
        // for (int i = 0; i < httpPhoto_Name.size(); i++) {
        // File f = new File(dirPath + "/" + httpPhoto_Name.get(i));// delelt
        // // //删除网络图片
        // f.delete();
        // }
        // for (int i = 0; i < photoBitmap_Name.size(); i++) {
        // File f = new File(dirPath + "/" + photoBitmap_Name.get(i));// delelt
        // // //删除缩略图
        // f.delete();
        // }
        thumbnailCache.clear();
        Utils.setIntPref(this, PLAY_MODE, playMode);
        Utils.setIntPref(this, PPT_PLAY_MODE, pptPlayMode);
        Log.i(TAG, "****************************finish phothplay"
                + "**************************************************************************");
        this.finish();
    }

    /**
     * 组件显示
     */
    private void findView() {
        playControlLayout = (LinearLayout) findViewById(R.id.photo_suspension_layout);
        photoPlayHolder = new PhotoPlayerViewHolder(this);
        photoPlayHolder.findViews();
        PhotoImageViewClickListener listener = new PhotoImageViewClickListener();
        photoPlayHolder.setOnClickListener(listener);
    }

    /**
     * 获取随机播放序号，返回结果范围[0, parent)
     */
    private static int getRandom(int parent) {
        int position = new SecureRandom().nextInt(parent);
        return position;
    }

    /**
     * 按键事件的处理
     * 
     * @param null
     * @return void
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown");
        Date curDate = new Date(System.currentTimeMillis());//
        long newtime = curDate.getTime();
        Log.i(TAG, "newtime = " + newtime + "mCurTime = " + mCurTime);
        if (newtime - mCurTime >= 200) {
            mCurTime = newtime;
        } else {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            return true;
        }
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_1) {
            Log.i(TAG, "getHeight" + publicBitmap[6].getHeight());
            Log.i(TAG, "getWidth" + publicBitmap[6].getWidth());
        }
        if (playControlLayout.isShown())// 菜单栏显示
        {
            if (photoPlayHolder.photo_play_mode_selector_area.isShown())// 设置播放模式的时候
            {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        switch (playMode) {
                            case 0: {
                                playMode = 2;
                                showPlayModeArea();// 刷新
                                break;
                            }
                            case 1: {
                                playMode = 0;
                                showPlayModeArea();// 刷新
                                break;
                            }
                            case 2: {
                                playMode = 1;
                                showPlayModeArea();// 刷新
                                break;
                            }
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        switch (playMode) {
                            case 0: {
                                playMode = 1;
                                showPlayModeArea();// 刷新
                                break;
                            }
                            case 1: {
                                playMode = 2;
                                showPlayModeArea();// 刷新
                                break;
                            }
                            case 2: {
                                playMode = 0;
                                showPlayModeArea();// 刷新
                                break;
                            }
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_ESCAPE: {
                        photoPlayHolder.photo_play_mode_selector_area.setVisibility(View.INVISIBLE);
                        playMode = playMode_copy;
                        // photoPlayHolder.bt_photoPlayMode.setBackgroundResource(R.drawable.music_button_left_sel);
                        state = OPTION_STATE_PLAY_MODE;
                        moveAndAddDelay();
                        break;
                    }
                    case KeyEvent.KEYCODE_ENTER: {
                        setPlayMode();
                        break;
                    }
                }
                return true;
            } else if (photoPlayHolder.ppt_play_mode_selector_area.isShown())// 设置ppt播放模式
            {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        if (pptPlayMode > 0) {
                            pptPlayMode--;
                        } else {
                            pptPlayMode = 7;
                        }
                        showPptPlayModeArea();
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        if (pptPlayMode < 7) {
                            pptPlayMode++;
                        } else {
                            pptPlayMode = 0;
                        }
                        showPptPlayModeArea();
                        break;
                    }
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_ESCAPE: {
                        photoPlayHolder.ppt_play_mode_selector_area.setVisibility(View.INVISIBLE);
                        pptPlayMode = pptPlayMode_copy;
                        photoPlayHolder.setPhotoNarrowSelect(true);
                        moveAndAddDelay();
                        break;
                    }
                    case KeyEvent.KEYCODE_ENTER: {
                        setPptPlayMode();
                        break;
                    }
                }
                return true;
            } else {
                moveAndAddDelay();
                switch (keyCode) {
                // case KeyEvent.KEYCODE_DPAD_UP:
                // if (m_option_index == 0) {
                // clearList();
                // }
                // moveNextOrPrevious(-1);
                // break;
                //
                // case KeyEvent.KEYCODE_DPAD_DOWN:
                // if (m_option_index == 3 || m_index == count - 1) {
                // clearList();
                // }
                // moveNextOrPrevious(1);
                // break;

                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        drapLeft();
                        break;

                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        drapRight();
                        break;
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        registerListeners();
                        break;
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_ESCAPE:
                        // if (isPhotoPlay) {
                        // PlayProcess();
                        // } else {
                        // cleanPhotoCacheAndFinish();
                        // }
                        hideController();
                        hideControlDelay();
                        break;

                    // case KeyEvent.KEYCODE_MENU: {
                    // hideController();
                    // hideControlDelay();
                    // }
                    // break;
                    default:
                        Log.i("default is click!!", "break");
                        break;
                }
            }
            return true;
        } else// 未显示菜单栏
        {
            if (photo_enl == false)// 没有选中放大
            {
                moveAndAddDelay();
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        if (m_option_index == 0) {
                            clearList();
                        }
                        // showTextController();
                        moveNextOrPrevious(-1);
                        break;
                    }

                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        if (m_option_index == 3 || m_index == count - 1) {
                            clearList();
                        }
                        // showTextController();
                        moveNextOrPrevious(1);
                        break;
                    }
                    // case KeyEvent.KEYCODE_DPAD_LEFT: {
                    // Intent intent = new Intent();
                    // //intent.setAction("com.haiertv.Change.4K2K");
                    // intent.putExtra("mode", false);
                    // this.sendBroadcast(intent);

                    // if (is4K2KMode){
                    // if (!button4K2K){
                    // Utils.showToast(PhotoPlayerActivity.this, "图片已处于普通模式预览",
                    // Toast.LENGTH_SHORT);
                    // break;
                    // }
                    // button4K2K = false;
                    // Utils.showToast(PhotoPlayerActivity.this, "正在为您切换到普通模式",
                    // Toast.LENGTH_SHORT);
                    // changeMode4K2K(false);
                    // }else{
                    // Utils.showToast(PhotoPlayerActivity.this,
                    // "图片是非4K2K，无法进行切换操作", Toast.LENGTH_SHORT);
                    // }

                    // showTextController();
                    // break;
                    // }
                    // case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    // Intent intent = new Intent();
                    // //intent.setAction("com.haiertv.Change.4K2K");
                    // intent.putExtra("mode", true);
                    // this.sendBroadcast(intent);

                    // if (is4K2KMode){
                    // if (button4K2K){
                    // Utils.showToast(PhotoPlayerActivity.this,
                    // "图片已处于4K2K模式预览", Toast.LENGTH_SHORT);
                    // break;
                    // }
                    // button4K2K = true;
                    // Utils.showToast(PhotoPlayerActivity.this,
                    // "正在为您切换到4K2K模式", Toast.LENGTH_SHORT);
                    // changeMode4K2K(true);
                    // }else{
                    // Utils.showToast(PhotoPlayerActivity.this,
                    // "图片是非4K2K，无法进行切换操作", Toast.LENGTH_SHORT);
                    // }
                    // showTextController();
                    // break;
                    // }

                    case KeyEvent.KEYCODE_ENTER: {
                        showController();
                        break;
                    }

                    case KeyEvent.KEYCODE_MENU:
                        showController();
                        break;
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_ESCAPE: {
                        if (isPhotoPlay) {
                            PlayProcess();
                        } else {
                            cleanPhotoCacheAndFinish();
                        }
                        break;
                    }
                    // case KeyEvent.KEYCODE_ENTER: {
                    // if (!photoPlayHolder.photo_left_list.isShown() &&
                    // !isPhotoPlay) {
                    // showPhotoLeftList();
                    // showTextController();
                    // } else if ((!photoPlayHolder.photo_left_list.isShown() &&
                    // isPhotoPlay)) {
                    // PlayProcess();// 暂停
                    // } else {
                    // photoPlayHolder.setAllUnSelect(false, isPlaying);
                    // photoPlayHolder.setPhotoPlaySelect(true, isPlaying);
                    // hidePhotoLeftList();
                    // PlayProcess();// 播放
                    // }
                    // break;
                    // }
                }
                return true;
            } else// 选中放大（显示的放大新页面）
            {
                matrix = photoPlayHolder.mImageView.getImageMatrix();
                rect = photoPlayHolder.mImageView.getDrawable().getBounds();
                matrix.getValues(values);
                mapState.left = values[2];
                mapState.top = values[5];
                mapState.right = mapState.left + rect.width() * values[0];
                mapState.bottom = mapState.top + rect.height() * values[0];

                // 获取图片的4个方向的坐标
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_ESCAPE: {
                        photo_enl = false;
                        Log.i(TAG, "zoomTimes =" + zoomTimes);
                        if (zoomTimes != 0)// 退出放大模式时，把图片还原到之前的大小
                        {
                            zoomTimes = 0;
                            photoPlayHolder.mImageView.panBy(-pointx, -pointy);// 回到初始位置
                            photoPlayHolder.mImageView.zoomTo(1.0f);
                        }
                        // hideTextEnlargeController();
                        // showPhotoLeftList();
                        // showTextController();
                        photoPlayHolder.photo_fun_lenearlayout.setVisibility(View.INVISIBLE);// 放大界面展示缩略图的框
                        break;
                    }
                    case KeyEvent.KEYCODE_ENTER: {
                        // showTextEnlargeController();
                        if (zoomTimes >= 10) {
                            zoomTimes = 0;
                            pointy = 0;
                            pointx = 0;
                            photoPlayHolder.mImageView.zoomTo(1.0f);
                            photoPlayHolder.photo_fun_lenearlayout.removeView(geom);
                        } else {
                            zoomTimes++;
                            photoPlayHolder.mImageView.zoomIn();
                        }
                        drawRecttangleAgain();
                        break;

                    }
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        // showTextEnlargeController();
                        moveDown();
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        // showTextEnlargeController();
                        moveUp();
                        break;

                    }

                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        // showTextEnlargeController();
                        moveRight();
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        // showTextEnlargeController();
                        moveLeft();
                        break;
                    }
                }
            }
        }
        return true;
    }

    //
    private void moveLeft() {
        if (mapState.left >= 0) {
            return;
        } else if (mapState.left <= -50 * density / (float) 160) {
            pointx = pointx + 50 * density / (float) 160;
            photoPlayHolder.mImageView.panBy(50 * density / (float) 160, 0);
        } else {
            pointx = pointx - mapState.left;
            photoPlayHolder.mImageView.panBy(-(mapState.left), 0);
        }
        drawRecttangleAgain();
    }

    private void moveRight() {
        if (mapState.right - photoPlayHolder.mImageView.getWidth() <= 0) {
            return;
        } else if (mapState.right - photoPlayHolder.mImageView.getWidth() >= 50 * density / (float) 160) {
            pointx = pointx - 50 * density / (float) 160;
            photoPlayHolder.mImageView.panBy(-50 * density / (float) 160, 0);
        } else {
            pointx = pointx - (mapState.right - photoPlayHolder.mImageView.getWidth());
            photoPlayHolder.mImageView.panBy(-(mapState.right - photoPlayHolder.mImageView.getWidth()), 0);
        }
        drawRecttangleAgain();
    }

    private void moveUp() {
        if (mapState.top >= 0) {
            return;
        } else if (mapState.top <= -50 * density / (float) 160) {
            pointy = pointy + 50 * density / (float) 160;
            photoPlayHolder.mImageView.panBy(0, 50 * density / (float) 160);
        } else {
            pointy = pointy - mapState.top;
            photoPlayHolder.mImageView.panBy(0, -(mapState.top));
        }
        drawRecttangleAgain();
    }

    private void moveDown() {
        if (mapState.bottom - photoPlayHolder.mImageView.getHeight() <= 0) {
            return;
        } else if (mapState.bottom - photoPlayHolder.mImageView.getHeight() >= 50 * density / (float) 160) {
            pointy = pointy - 50 * density / (float) 160;
            photoPlayHolder.mImageView.panBy(0, -50 * density / (float) 160);
        } else {
            pointy = pointy - mapState.bottom - photoPlayHolder.mImageView.getHeight();
            photoPlayHolder.mImageView.panBy(0, -(mapState.bottom - photoPlayHolder.mImageView.getHeight()));
        }
        drawRecttangleAgain();
    }

    /**
     * 重新绘制四边形
     */
    private void drawRecttangleAgain() {
        getValue();
        photoPlayHolder.photo_fun_lenearlayout.removeView(geom);
        geom = new Geometry(this, photoLeft, photoTop, photoRight, photoBottom);
        photoPlayHolder.photo_fun_lenearlayout.addView(geom);
    }

    /**
     * 遥控器左移，焦点切换
     */
    private void drapLeft() {
        switch (state) {
            case OPTION_STATE_PLAY_MODE:// 焦点在(播放模式上)
                state = OPTION_STATE_ENL;
                photoPlayHolder.setPhotoTurnLeftSelect(false);
                photoPlayHolder.setPhotoEnlargeSelect(true);
                break;

            case OPTION_STATE_PRE:// 焦点在“上一张”
                state = OPTION_STATE_PPT_PLAY_MODE;
                photoPlayHolder.setPhotoPreSelect(false);
                photoPlayHolder.setPhotoNarrowSelect(true);
                break;

            case OPTION_STATE_NEXT:// 焦点在"下一个"
                state = OPTION_STATE_PLAY;
                photoPlayHolder.setPhotoNextSelect(false);
                photoPlayHolder.setPhotoPlaySelect(true, isPhotoPlay);
                break;

            case OPTION_STATE_PLAY:// 焦点在“播放”
                state = OPTION_STATE_PRE;
                photoPlayHolder.setPhotoPlaySelect(false, isPhotoPlay);
                photoPlayHolder.setPhotoPreSelect(true);

                break;

            case OPTION_STATE_ENL:// 焦点在放大上
                state = OPTION_STATE_TURNRIGHT;
                photoPlayHolder.setPhotoEnlargeSelect(false);
                photoPlayHolder.setPhotoTurnRightSelect(true);
                break;

            case OPTION_STATE_PPT_PLAY_MODE:// 焦点在缩小上
                state = OPTION_STATE_PLAY_MODE;
                photoPlayHolder.setPhotoNarrowSelect(false);
                photoPlayHolder.setPhotoTurnLeftSelect(true);
                break;
            case OPTION_STATE_TURNRIGHT:// 焦点在右旋
                state = OPTION_STATE_NEXT;
                photoPlayHolder.setPhotoTurnRightSelect(false);
                photoPlayHolder.setPhotoNextSelect(true);
                break;
        }
    }

    /**
     * 遥控器右移，焦点切换
     */
    private void drapRight() {
        switch (state) {
            case OPTION_STATE_PLAY_MODE:// 焦点在随机播放上
                state = OPTION_STATE_PPT_PLAY_MODE;
                photoPlayHolder.setPhotoTurnLeftSelect(false);
                photoPlayHolder.setPhotoNarrowSelect(true);
                break;
            case OPTION_STATE_PRE:// 焦点在“上一张”
                state = OPTION_STATE_PLAY;
                photoPlayHolder.setPhotoPreSelect(false);
                photoPlayHolder.setPhotoPlaySelect(true, isPhotoPlay);
                break;
            case OPTION_STATE_NEXT:// 焦点在"下一个"
                state = OPTION_STATE_TURNRIGHT;
                photoPlayHolder.setPhotoNextSelect(false);
                photoPlayHolder.setPhotoTurnRightSelect(true);
                break;
            case OPTION_STATE_PLAY:// 焦点在“播放”
                state = OPTION_STATE_NEXT;
                photoPlayHolder.setPhotoPlaySelect(false, isPhotoPlay);
                photoPlayHolder.setPhotoNextSelect(true);
                break;
            case OPTION_STATE_ENL:// 焦点在放大上
                state = OPTION_STATE_PLAY_MODE;
                photoPlayHolder.setPhotoEnlargeSelect(false);
                photoPlayHolder.setPhotoTurnLeftSelect(true);// 随机模式
                break;
            case OPTION_STATE_PPT_PLAY_MODE:// 焦点在缩小上
                state = OPTION_STATE_PRE;
                photoPlayHolder.setPhotoNarrowSelect(false);
                photoPlayHolder.setPhotoPreSelect(true);
                break;
            case OPTION_STATE_TURNRIGHT:// 焦点在右旋
                state = OPTION_STATE_ENL;
                photoPlayHolder.setPhotoTurnRightSelect(false);
                photoPlayHolder.setPhotoEnlargeSelect(true);
                break;
        }
    }

    /**
     * 控制图片的播放
     * 
     * @param null
     * @return void
     */
    private void PlayProcess() {
        if (isPhotoPlay) {
            // 如果正在播放.
            Toast.makeText(this, R.string.stop_slide_show, Toast.LENGTH_SHORT).show();
            stopPhotoPlay();
        } else {
            if (playMode == 0) {
                moveAndAddDelay();
                if ((currentPosition + 1) < count) {
                    isPhotoPlay = true;
                    // photoPlayHolder.photo_play.setBackgroundResource(R.drawable.music_button_ok_pause);
                    photoPlayHolder.textphotoPlay.setText(R.string.stop_photo);
                    photoPlayHolder.bt_photoPlay.setImageResource(R.drawable.music_menu_button_pause);
                    if (handler != null)
                        handler.sendEmptyMessage(PPT_PLAYER);
                } else {
                    Utils.showToast(this, R.string.already_is_the_last_picture, Toast.LENGTH_SHORT);
                }
            } else if (playMode == 1) {
                moveAndAddDelay();
                if ((currentPosition + 1) < count) {
                    isPhotoPlay = true;
                    // photoPlayHolder.photo_play.setBackgroundResource(R.drawable.music_button_ok_pause);
                    photoPlayHolder.textphotoPlay.setText(R.string.stop_photo);
                    photoPlayHolder.bt_photoPlay.setImageResource(R.drawable.music_menu_button_pause);
                    if (handler != null)
                        handler.sendEmptyMessage(PPT_PLAYER);
                } else if (currentPosition == count - 1) {
                    isPhotoPlay = true;
                    // photoPlayHolder.photo_play.setBackgroundResource(R.drawable.music_button_ok_pause);
                    photoPlayHolder.textphotoPlay.setText(R.string.stop_photo);
                    photoPlayHolder.bt_photoPlay.setImageResource(R.drawable.photo_player_icon_pause_focus);
                    currentPosition = 0;
                    if (handler != null)
                        handler.sendEmptyMessage(PPT_PLAYER);
                }
            } else// 随机播放模式
            {
                if (count != 1) {
                    // hidePhotoLeftList();// 影藏右侧图片
                    isPhotoPlay = true;
                    // photoPlayHolder.photo_play.setBackgroundResource(R.drawable.music_button_ok_pause);
                    photoPlayHolder.textphotoPlay.setText(R.string.stop_photo);
                    photoPlayHolder.bt_photoPlay.setImageResource(R.drawable.photo_player_icon_pause_focus);
                    if (handler != null)
                        handler.sendEmptyMessage(STOCHASTIC_PLAYER);
                } else {
                    Utils.showToast(this, R.string.cannot_carry_on_the_stochastic_broadcast, Toast.LENGTH_SHORT);
                }
            }
        }
        state = OPTION_STATE_PLAY;
        photoPlayHolder.setPhotoPlaySelect(true, isPhotoPlay);
    }

    /**
     * 进入放大界面
     * 
     * @param null
     * @return void
     */
    private void zoomIn() {
        photo_enl = true;// 放大
        pointy = 0;
        pointx = 0;
        if (rotate != 0) {
            photoPlayHolder.mImageView.rotateImage(-(rotate));// 如果之前图片被旋转了，则需要先把图片还原
            rotate = 0;
        }// 先再旋转回初始位置还原控件的大小
        if (scaledDown > 0) {
            photoPlayHolder.mImageView.zoomTo(1.0f);
            scaledDown = 0;
        }
        playControlLayout.setVisibility(View.INVISIBLE);
        // hideTextController();
        // showTextEnlargeController();
        if (localPath) {
            setBitmapp(currentPosition, photoPlayHolder.photo_ok_enlImage.getId(),
                    photoPlayHolder.photo_ok_enlImage.getWidth(), photoPlayHolder.photo_ok_enlImage.getHeight());
        } else {
            if (publicBitmap[6] == null) {
                Utils.showToast(this, R.string.the_form_does_not_support, Toast.LENGTH_SHORT);
                photoPlayHolder.photo_ok_enlImage.setImageBitmap(publicBitmap[7]);
            } else
                photoPlayHolder.photo_ok_enlImage.setImageBitmap(publicBitmap[6]);
        }
        computationTheSize();
        photoPlayHolder.photo_ok_enlImage.setVisibility(View.VISIBLE);
        photoPlayHolder.photo_fun_lenearlayout.setVisibility(View.VISIBLE);
        photoPlayHolder.photo_fun_lenearlayout.removeView(geom);
        Matrix matrix = photoPlayHolder.mImageView.getImageMatrix();
        Rect rect = photoPlayHolder.mImageView.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        mapState.left = values[2];
        mapState.top = values[5];
        mapState.right = mapState.left + rect.width() * values[0];
        mapState.bottom = mapState.top + rect.height() * values[0];
        photoTop = 2 + (int) ((mapState.top - 0) * (191 * density / (float) 160) / (mapState.bottom - mapState.top));// 框子上边缘和位图上边缘的距离
        photoBottom = -2
                + (int) ((mapState.bottom - photoPlayHolder.mImageView.getHeight()) * (191 * density / (float)160) / (mapState.bottom - mapState.top));
        photoLeft = 2 + (int) ((mapState.left - 0) * (303 * density / (float) 160) / (mapState.right - mapState.left));
        photoRight = -2
                + (int) ((mapState.right - photoPlayHolder.mImageView.getWidth()) * (303 * density / (float) 160) / (mapState.right - mapState.left));
        Log.i("photoTop" + photoTop, "photoBottom" + photoBottom + "photoLeft" + photoLeft + "photoRight" + photoRight);
        if (photoTop <= 0 || photoBottom >= 0 || photoLeft <= 0 || photoRight >= 0) {
            geom = new Geometry(this, -photoLeft, -photoTop, (303 * density / 160) - photoRight, (191 * density / 160)
                    - photoBottom);
            photoPlayHolder.photo_fun_lenearlayout.addView(geom);
        }

    }

    // 加载网络图片或者本地图片时，刷新右侧图片的handler
    private Handler leftPhotoHandle = new Handler() {
        public void handleMessage(Message msg) {
            if (activityIsFinish)
                return;
            ImageView imageview;
            int what = msg.what - LEFT_PHOTO_MSG;
            Log.i(TAG, "<leftPhotoHandle>  what = " + what);
            Log.i(TAG, "<LEFT_PHOTO_MSG>" + LEFT_PHOTO_MSG);
            bThreadRun = false;
            if (!localPath)
                beginDownPhoto();// 启动下一张图片的下载
            else
                beginReflushPhoto();// 刷新下一张
            switch (msg.what) {
                case LEFT_PHOTO_MSG:
                case LEFT_PHOTO_MSG + 1:
                case LEFT_PHOTO_MSG + 2:
                case LEFT_PHOTO_MSG + 3:
                case LEFT_PHOTO_MSG + 4:
                case LEFT_PHOTO_MSG + 5: {
                    int i = msg.what - LEFT_PHOTO_MSG;
                    imageview = (ImageView) findViewById(publicImageViewList[i]);
                    if (msg.arg2 > -1 && msg.arg2 < photoDataList.size()) {
                        FileItem fileInfo = photoDataList.get(msg.arg2);
                        SoftReference<Bitmap> reference = thumbnailCache.get(fileInfo.getLocation());
                        if (reference != null) {
                            Bitmap bitmap = reference.get();
                            if (bitmap != null) {
                                imageview.setImageBitmap(bitmap);
                            } else {
                                imageview.setImageBitmap(publicBitmap[7]);
                            }
                        } else {
                            imageview.setImageBitmap(publicBitmap[7]);
                        }
                    }
                    break;
                }
                case LEFT_PHOTO_MSG + 6: {
                    Log.i(TAG, "***********************************************刷新大图"
                            + "*******************************************");
                    photoPlayHolder.photo_progressbar.setVisibility(View.GONE);

                    if (needRunAnimation) {
                        // setDisplayBitmap(publicBitmap[7]);
                    } else
                        needRunAnimation = true;
                    centerPhotoShowAnimation();
                    break;
                }
                case LEFT_PHOTO_MSG + 8:// 网络超时 或连接异常
                case LEFT_PHOTO_MSG + 9:
                case LEFT_PHOTO_MSG + 10:
                case LEFT_PHOTO_MSG + 11:
                case LEFT_PHOTO_MSG + 12:
                case LEFT_PHOTO_MSG + 13: {
                    ImageView image;
                    image = (ImageView) findViewById(publicImageViewList[msg.what - LEFT_PHOTO_MSG - 8]);
                    image.setImageBitmap(publicBitmap[7]);
                    break;
                }
                case LEFT_PHOTO_MSG + 14: {
                    if (msg.arg1 == 1000)
                        Utils.showToast(PhotoPlayerActivity.this, R.string.photo_too_big, Toast.LENGTH_SHORT);
                    else
                        Utils.showToast(PhotoPlayerActivity.this,
                                R.string.downloading_defeat_please_inspect_network_tries_again, Toast.LENGTH_SHORT);
                    photoPlayHolder.photo_progressbar.setVisibility(View.GONE);
                    ImageViewTouch imagecenterview;
                    ImageView image;
                    imagecenterview = (ImageViewTouch) findViewById(publicImageViewList[6]);
                    // image = (ImageView)
                    // findViewById(publicImageViewList[m_option_index + 1]);
                    {
                        // image.setImageBitmap(publicBitmap[7]);
                        imagecenterview.setImageBitmap(publicBitmap[7]);
                    }
                    break;
                }
                default: {
                }
                    break;
            }
        }
    };

    /**
     * 弹出播放模式的设置框
     * 
     * @param null
     * @return void
     */
    private void showPlayModeArea() {
        cancleDelayHide();
        photoPlayHolder.textphotoPlayMode.setVisibility(View.VISIBLE);
        switch (playMode) {
            case 0: {
                photoPlayHolder.photo_play_mode_order.setBackgroundResource(R.drawable.list_focused_holo2);
                photoPlayHolder.photo_play_mode_all_repeat.setBackgroundResource(R.drawable.list_selected_holo2);
                photoPlayHolder.photo_play_mode_random.setBackgroundResource(R.drawable.list_selected_holo2);
                photoPlayHolder.photo_play_mode_order.requestFocusFromTouch();
                break;
            }
            case 1: {
                photoPlayHolder.photo_play_mode_order.setBackgroundResource(R.drawable.list_selected_holo2);
                photoPlayHolder.photo_play_mode_all_repeat.setBackgroundResource(R.drawable.list_focused_holo2);
                photoPlayHolder.photo_play_mode_random.setBackgroundResource(R.drawable.list_selected_holo2);
                photoPlayHolder.photo_play_mode_all_repeat.requestFocusFromTouch();
                break;
            }
            case 2: {
                photoPlayHolder.photo_play_mode_order.setBackgroundResource(R.drawable.list_selected_holo2);
                photoPlayHolder.photo_play_mode_all_repeat.setBackgroundResource(R.drawable.list_selected_holo2);
                photoPlayHolder.photo_play_mode_random.setBackgroundResource(R.drawable.list_focused_holo2);
                photoPlayHolder.photo_play_mode_random.requestFocusFromTouch();
                break;
            }
        }
        if (!photoPlayHolder.photo_play_mode_selector_area.isShown()) {
            photoPlayHolder.photo_play_mode_selector_area.setVisibility(View.VISIBLE);
            // photoPlayHolder.bt_photoPlayMode.setBackgroundResource(R.drawable.jump_sel_bg_left);
        }
    }

    /**
     * 设置播放模式
     * 
     * @param null
     * @return void
     */
    private void setPlayMode() {
        switch (playMode) {
            case 0: {
                photoPlayHolder.bt_photoPlayMode.setImageResource(R.drawable.music_play_mode_order);
                photoPlayHolder.textphotoPlayMode.setText(R.string.all_play);
                break;
            }
            case 1: {
                photoPlayHolder.bt_photoPlayMode.setImageResource(R.drawable.music_play_mode_all_repeat);
                photoPlayHolder.textphotoPlayMode.setText(R.string.all_repeat);
                break;
            }
            case 2: {
                photoPlayHolder.bt_photoPlayMode.setImageResource(R.drawable.music_play_mode_random);
                photoPlayHolder.textphotoPlayMode.setText(R.string.random_play);
                break;
            }
        }
        photoPlayHolder.setPhotoTurnLeftSelect(true);
        photoPlayHolder.photo_play_mode_selector_area.setVisibility(View.INVISIBLE);
        // photoPlayHolder.bt_photoPlayMode.setBackgroundResource(R.drawable.music_button_left_sel);
        moveAndAddDelay();
    }

    /**
     * 弹出ppt播放模式的设置框
     * 
     * @param null
     * @return void
     */
    private void showPptPlayModeArea() {
        cancleDelayHide();
        photoPlayHolder.textphotoPptPlayMode.setVisibility(View.VISIBLE);
        photoPlayHolder.ppt_play_mode_selector_area.setVisibility(View.VISIBLE);
        photoPlayHolder.ppt_play_mode_a.setBackgroundResource(R.drawable.list_selected_holo2);
        photoPlayHolder.ppt_play_mode_b.setBackgroundResource(R.drawable.list_selected_holo2);
        photoPlayHolder.ppt_play_mode_c.setBackgroundResource(R.drawable.list_selected_holo2);
        photoPlayHolder.ppt_play_mode_d.setBackgroundResource(R.drawable.list_selected_holo2);
        photoPlayHolder.ppt_play_mode_e.setBackgroundResource(R.drawable.list_selected_holo2);
        photoPlayHolder.ppt_play_mode_f.setBackgroundResource(R.drawable.list_selected_holo2);
        photoPlayHolder.ppt_play_mode_g.setBackgroundResource(R.drawable.list_selected_holo2);
        photoPlayHolder.ppt_play_mode_h.setBackgroundResource(R.drawable.list_selected_holo2);
        switch (pptPlayMode) {
            case 0: {
                photoPlayHolder.ppt_play_mode_a.setBackgroundResource(R.drawable.list_focused_holo2);
                break;
            }
            case 1: {
                photoPlayHolder.ppt_play_mode_b.setBackgroundResource(R.drawable.list_focused_holo2);
                break;
            }
            case 2: {
                photoPlayHolder.ppt_play_mode_c.setBackgroundResource(R.drawable.list_focused_holo2);
                break;
            }
            case 3: {
                photoPlayHolder.ppt_play_mode_d.setBackgroundResource(R.drawable.list_focused_holo2);
                break;
            }
            case 4: {
                photoPlayHolder.ppt_play_mode_e.setBackgroundResource(R.drawable.list_focused_holo2);
                break;
            }
            case 5: {
                photoPlayHolder.ppt_play_mode_f.setBackgroundResource(R.drawable.list_focused_holo2);
                break;
            }
            case 6: {
                photoPlayHolder.ppt_play_mode_g.setBackgroundResource(R.drawable.list_focused_holo2);
                break;
            }
            case 7: {
                photoPlayHolder.ppt_play_mode_h.setBackgroundResource(R.drawable.list_focused_holo2);
                break;
            }
        }
        // photoPlayHolder.bt_photoPptPlayMode.setBackgroundResource(R.drawable.jump_sel_bg);
    }

    /**
     * 设置ppt播放模式(点击确定之后的操作)
     * 
     * @param null
     * @return void
     */
    private void setPptPlayMode() {
        // photoPlayHolder.bt_photoPptPlayMode.setBackgroundResource(R.drawable.music_button_middle_sel);
        photoPlayHolder.textphotoPptPlayMode.setText(isPhotoPlayModeString[pptPlayMode]);
        photoPlayHolder.setPhotoNarrowSelect(true);
        photoPlayHolder.ppt_play_mode_selector_area.setVisibility(View.INVISIBLE);
        moveAndAddDelay();
    }

    /**
     * 右旋
     * 
     * @param null
     * @return void
     */
    private void rotateImageRight() {
        Log.i(TAG, "rotateImageRight");
        if (zoomTimes != 0) {
            zoomTimes = 0;
            photoPlayHolder.mImageView.zoomTo(1.0f);
        }
        rotate = rotate + 90.0f;
        rotate = rotate % 360;
        if (rotate == 90)// 右旋90度
        {
            computationScaledDown(); // 90度的时候计算出ScaledDown，180、270、360度时直接使用，并在360度的时候或者切换图片的时候重置
            if (scaledDown > 1.0f) {
                photoPlayHolder.mImageView.zoomOut(scaledDown);
            }
        } else if (rotate == 270)// 右旋270度
        {
            if (scaledDown > 1.0f) {
                photoPlayHolder.mImageView.zoomOut(scaledDown);
            }
        } else {
            if (scaledDown > 1.0f)
                photoPlayHolder.mImageView.zoomIn(scaledDown);// 还原比例
            if (rotate == 0)// 转一圈后重置 ScaledDown
                scaledDown = 0;
        }
        photoPlayHolder.mImageView.rotateImage(90.0f);
    }

    /**
     * 计算旋转时需要对图片进行缩小的倍率 （以适应控件 ）
     * 
     * @param null
     * @return void
     */
    public void computationScaledDown() {
        matrix = photoPlayHolder.mImageView.getImageMatrix();
        rect = photoPlayHolder.mImageView.getDrawable().getBounds();
        matrix.getValues(values);
        scaledDown = (float) (rect.width() * values[0]) / (float) photoPlayHolder.mImageView.getHeight();// *
                                                                                                         // values[0]这个一定需要，不然图片会显示不完全
        Log.i(TAG, "-----------------ScaledDown= " + scaledDown);
    }

    /**
     * 计算放大界面的小框里面的bitmap离边缘的距离
     * 
     * @param null
     * @return void
     */
    public void computationTheSize() {
        if ((191 * density / (float) 160)
                / (float) (photoPlayHolder.mImageView.mBitmapDisplayed.getBitmap().getHeight()) >= (303 * density / (float)160)
                / (float) photoPlayHolder.mImageView.mBitmapDisplayed.getBitmap().getWidth()) {
            width = 0;
            height = (191 * density / (float)160)
                    - ((float) photoPlayHolder.mImageView.mBitmapDisplayed.getBitmap().getHeight()
                            * (303 * density / (float)160) / (float) photoPlayHolder.mImageView.mBitmapDisplayed.getBitmap()
                            .getWidth());
        } else {
            width = (303 * density / (float)160)
                    - ((float) photoPlayHolder.mImageView.mBitmapDisplayed.getBitmap().getWidth()
                            * (191 * density / (float)160) / (float) photoPlayHolder.mImageView.mBitmapDisplayed.getBitmap()
                            .getHeight());
            height = 0;
        }
    }

    /**
     * 获取中间大图的bitmap的位置，并计算出 需要绘制的可移动彩色框 的各个方向的位置
     * 
     * @param null
     * @return void
     */
    public void getValue() {
        rect = photoPlayHolder.mImageView.getDrawable().getBounds();
        matrix.getValues(values);
        mapState.left = values[2];
        mapState.top = values[5];
        mapState.right = mapState.left + rect.width() * values[0];
        mapState.bottom = mapState.top + rect.height() * values[0];
        if (height == 0) {
            photoTop = 2 + (int) height / 2
                    + (int) ((0 - mapState.top) * (191 * density / (float) 160) / (mapState.bottom - mapState.top));
            photoBottom = 2
                    + (int) height
                    / 2
                    + (int) ((0 - mapState.top + photoPlayHolder.mImageView.getHeight()) * (191 * density / (float)160) / (mapState.bottom - mapState.top));

            photoLeft = 4 + (int) width / 2
                    + (int) ((0 - mapState.left) * (191 * density / (float)160) / (mapState.bottom - mapState.top));
            photoRight = 4
                    + (int) width
                    / 2
                    + (int) ((0 - mapState.left + photoPlayHolder.mImageView.getWidth()) * (191 * density / (float)160) / (mapState.bottom - mapState.top));
        } else {
            photoTop = 2 + (int) height / 2
                    + (int) ((0 - mapState.top) * (303 * density /(float) 160) / (mapState.right - mapState.left));
            photoBottom = 2
                    + (int) height
                    / 2
                    + (int) ((0 - mapState.top + photoPlayHolder.mImageView.getHeight()) * (303 * density / (float)160) / (mapState.right - mapState.left));

            photoLeft = 4 + (int) width / 2
                    + (int) ((0 - mapState.left) * (303 * density / (float)160) / (mapState.right - mapState.left));
            photoRight = 4
                    + (int) width
                    / 2
                    + (int) ((0 - mapState.left + photoPlayHolder.mImageView.getWidth()) * (303 * density / (float)160) / (mapState.right - mapState.left));
        }
    }

    /**
     * 随机播放
     * 
     * @param null
     * @return void
     */
    private void randomPlay() {
        currentPosition = getRandom(count);
        m_index = currentPosition;
        bitmapArrayCurrent();
    }

    /**
     * 刷新界面的左侧图片，控制缩略图上焦点的移动
     * 
     * @param null
     * @return void
     */
    private void refurbishLeftPhotos(int event) {

        if (event == 1) {
            if (m_option_index < 3 && m_option_index < count - 1)// 此时不用刷新左侧列表，只用切换焦点
            {
                if (m_index < count - 1)
                    m_index++;
                else
                    m_index = 0;
                clickPhotoChangeFocus(m_option_index, m_option_index + 1);
                m_option_index++;
            } else// 刷新左侧列表
            {
                if (m_index < count - 1)
                    m_index++;
                else
                    m_index = 0;
                initLeftPhotos();
            }
        } else if (event == -1) {
            {
                if (m_option_index > 0)// 此时不用刷新左侧列表，只用切换焦点
                {
                    if (m_index > 0)
                        m_index--;
                    else
                        m_index = count - 1;

                    clickPhotoChangeFocus(m_option_index, m_option_index - 1);
                    m_option_index--;
                } else// 刷新左侧图片
                {
                    if (m_index > 0)
                        m_index--;
                    else
                        m_index = count - 1;
                    initLeftPhotos();
                }
            }
        }
    }

    /**
     * 初始化左侧图片
     * 
     * @param null
     * @return void
     */
    private void initLeftPhotos() {
        Log.i(TAG, "----------initLeftPhotos---------\n" + "m_index=" + m_index + "option=" + m_option_index);

        refurbishPhoto(m_index, publicImageViewList[m_option_index + 1]); // 首先刷新光标所在位置的图片
        if (imageFlay[0])
            refurbishPhoto(m_index + (-1 - m_option_index), photoPlayHolder.previousphotoImageView.getId());// pre
                                                                                                            // 左侧最上面的箭头下面那张

        if (m_option_index != 0 && imageFlay[1])
            refurbishPhoto(m_index + (0 - m_option_index), photoPlayHolder.firstPhotoImageView.getId());// 1
        if (m_option_index != 1 && imageFlay[2])
            refurbishPhoto(m_index + (1 - m_option_index), photoPlayHolder.secendPhotoImageView.getId());// 2
        if (m_option_index != 2 && imageFlay[3])
            refurbishPhoto(m_index + (2 - m_option_index), photoPlayHolder.thirdPhotoImageView.getId());// 3
        if (m_option_index != 3 && imageFlay[4])
            refurbishPhoto(m_index + (3 - m_option_index), photoPlayHolder.fourthPhotoImageView.getId());// 4
        if (imageFlay[5])
            refurbishPhoto(m_index + (4 - m_option_index), photoPlayHolder.nextphotoImageView.getId());// next
                                                                                                       // 左侧最下面的箭头下面的那张图片
    }

    /**
     * 刷新控件上的照片 ,index: 需要设置的图片在list里面的index ,id :控件id
     */
    private void refurbishPhoto(int index, int id) {
        ImageView image = (ImageView) findViewById(id);
        Log.i(TAG, "index=" + index + "  count=" + count);
        if (index >= 0 && index < count) {

        } else if (index < 0) {
            if (index + count < 0)
                return;
            index = index + count;
        } else {
            if (index - count >= count)
                return;
            index = index - count;
        }
        Log.i(TAG, "-------------index=" + index + "  count=" + count);

        if (index >= photoDataList.size()) {
            return;
        }

        FileItem fileInfo = photoDataList.get(index);

        SoftReference<Bitmap> reference = thumbnailCache.get(fileInfo.getLocation());
        if (reference != null) {
            Bitmap bitmap = reference.get();
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
            } else {
                thumbnailCache.remove(fileInfo.getLocation());// 删除bitmap为空的hashkey
                image.setImageBitmap(publicBitmap[7]);
                if (!localPath)
                    getHttpBitmap(fileInfo.getName(), fileInfo.getLocation(), id, index);
                else
                    setBitmap(fileInfo.getName(), index, id);
            }
        } else {
            image.setImageBitmap(publicBitmap[7]);
            if (!localPath)
                getHttpBitmap(fileInfo.getName(), fileInfo.getLocation(), id, index);
            else
                setBitmap(fileInfo.getName(), index, id);
        }
    }

    private synchronized void refurbishBigPhoto(int index, int id) {
        if (index >= 0 && index < count) {
        } else if (index < 0) {
            index = index + count;
        } else {
            index = index - count;
        }

        if (index >= photoDataList.size()) {
            return;
        }

        FileItem fileInfo = photoDataList.get(index);
        if (!fileExists()) {
            getHttpBitmap(fileInfo.getName(), fileInfo.getDownloadURL(), id, index);
        } else
            setBitmap(fileInfo.getName(), index, id);
    }

    public void clearList() {
        if (photoDownList != null) {
            int i = 0;
            while (photoDownList.size() > i)// 保证大图的任务不被完全清除（如果很多大图任务则保留2张的解析任务）,避免一直按键的时候
                                            // 屏幕上面的图片始终不变
            {
                if (photoDownList.get(i).m_nid != R.id.photo_image)
                    photoDownList.remove(i);
                else
                    i++;
            }
            while (photoDownList.size() > 2) {
                photoDownList.remove(0);
            }
        }
        if (photoSetBitMapList != null) {
            int i = 0;
            while (photoSetBitMapList.size() > i)// 保证大图的任务不被完全清除（如果很多大图任务则保留2张的解析任务）,避免一直按键的时候
                                                 // 屏幕上面的图片始终不变
            {
                if (photoSetBitMapList.get(i).m_nid != R.id.photo_image)
                    photoSetBitMapList.remove(i);
                else
                    i++;
            }
            while (photoSetBitMapList.size() > 2) {
                photoSetBitMapList.remove(0);
            }
        }
    }

    /**
     * 各个按钮上面按ok时的响应事件
     */
    private void registerListeners() {
        switch (state) {
            case OPTION_STATE_PRE:
                if (m_option_index == 0) {
                    clearList();
                }
                moveNextOrPrevious(-1);
                break;
            case OPTION_STATE_PLAY:
                PlayProcess();
                break;
            case OPTION_STATE_NEXT:
                if (m_option_index == 3 || m_index == count - 1) {
                    clearList();
                }
                moveNextOrPrevious(1);
                break;
            case OPTION_STATE_ENL: {
                if (!isPhotoPlay)
                    zoomIn();
                else {
                    Utils.showToast(this, R.string.enlargement_function_not_available_please_first_suspend_the_play,
                            Toast.LENGTH_SHORT);
                }
            }
                break;
            case OPTION_STATE_PPT_PLAY_MODE:
                // zoomOut();
                pptPlayMode_copy = pptPlayMode;
                showPptPlayModeArea();
                break;
            case OPTION_STATE_PLAY_MODE:// 播放模式
            {
                playMode_copy = playMode;
                showPlayModeArea();
                photoPlayHolder.bt_photoPlayMode.setFocusable(false);
                // rotateImageLeft();
                break;
            }
            case OPTION_STATE_TURNRIGHT:// 旋转
                rotateImageRight();
                break;
        }
    }

    /**
     * 响应鼠标click按钮
     */
    class PhotoImageViewClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Date curDate = new Date(System.currentTimeMillis());// 按键延时
            long newtime = curDate.getTime();
            Log.i(TAG, "newtime = " + newtime + "mCurTime = " + mCurTime);
            if (newtime - mCurTime >= 200) {
                mCurTime = newtime;
            } else {
                return;
            }
            if (photoPlayHolder.ppt_play_mode_selector_area.isShown())// 设置ppt播放模式
            {
                switch (v.getId()) {
                    case R.id.ppt_play_mode_a: {
                        pptPlayMode = 0;
                        break;
                    }
                    case R.id.ppt_play_mode_b: {
                        pptPlayMode = 1;
                        break;
                    }
                    case R.id.ppt_play_mode_c: {
                        pptPlayMode = 2;
                        break;
                    }
                    case R.id.ppt_play_mode_d: {
                        pptPlayMode = 3;
                        break;
                    }
                    case R.id.ppt_play_mode_e: {
                        pptPlayMode = 4;
                        break;
                    }
                    case R.id.ppt_play_mode_f: {
                        pptPlayMode = 5;
                        break;
                    }
                    case R.id.ppt_play_mode_g: {
                        pptPlayMode = 6;
                        break;
                    }
                    case R.id.ppt_play_mode_h: {
                        pptPlayMode = 7;
                        break;
                    }
                }
                setPptPlayMode();
                return;
            } else if (photoPlayHolder.photo_play_mode_selector_area.isShown())// 设置图片播放器模式
            {
                switch (v.getId()) {
                    case R.id.photo_play_mode_order: {
                        playMode = 0;
                        break;
                    }
                    case R.id.photo_play_mode_all_repeat: {
                        playMode = 1;
                        break;
                    }
                    case R.id.photo_play_mode_random: {
                        playMode = 2;
                        break;
                    }
                }
                setPlayMode();
                return;
            } else// 下方菜单栏和左侧列表
            {
                moveAndAddDelay();
                switch (v.getId()) {
                    case R.id.photo_player_previous: {
                        Log.i(TAG, "photo_player_previous is click");
                        photoPlayHolder.setAllUnSelect(isPhotoPlay, isPlaying);
                        moveNextOrPrevious(-1);
                        photoPlayHolder.setPhotoPreSelect(true);
                        state = OPTION_STATE_PRE;
                        break;
                    }
                    case R.id.photo_play:
                        Log.i(TAG, "photo_play is click");
                        photoPlayHolder.setAllUnSelect(isPhotoPlay, isPlaying);
                        PlayProcess();
                        // photoPlayHolder.setPhotoPlaySelect(isPhotoPlay,
                        // isPlaying);
                        state = OPTION_STATE_PLAY;
                        break;

                    case R.id.photo_next: {
                        Log.i(TAG, "photo_next is click");
                        photoPlayHolder.setAllUnSelect(false, isPlaying);
                        if (m_option_index == 3 || m_index == count - 1) {
                            clearList();
                        }
                        moveNextOrPrevious(1);
                        photoPlayHolder.setPhotoNextSelect(true);
                        state = OPTION_STATE_NEXT;
                        break;
                    }
                    case R.id.photo_enlarge:
                        Log.i(TAG, "photo_enlarge is click");
                        if (is4K2KMode) {
                            Utils.showToast(PhotoPlayerActivity.this, R.string.display_not_support, Toast.LENGTH_SHORT);
                        } else {
                            photoPlayHolder.imageLL.setVisibility(View.VISIBLE);
                            photoPlayHolder.imageSurfaceLL.setVisibility(View.INVISIBLE);
                            photoPlayHolder.setAllUnSelect(false, isPlaying);
                            if (!isPhotoPlay) {
                                zoomIn();
                            } else {
                                Utils.showToast(PhotoPlayerActivity.this,
                                        R.string.enlargement_function_not_available_please_first_suspend_the_play,
                                        Toast.LENGTH_SHORT);
                            }
                            photoPlayHolder.setPhotoEnlargeSelect(true);
                            state = OPTION_STATE_ENL;
                        }

                        break;

                    case R.id.photo_ppt_play_mode:// ppt浏览模式 ——> 用之前的缩小功能改的
                        Log.i(TAG, "photo_ppt_play_mode is click");
                        if (is4K2KMode) {
                            Utils.showToast(PhotoPlayerActivity.this, R.string.display_not_support, Toast.LENGTH_SHORT);
                        } else {
                            photoPlayHolder.setAllUnSelect(isPhotoPlay, isPlaying);
                            cancleDelayHide();
                            photoPlayHolder.setPhotoNarrowSelect(true);
                            pptPlayMode_copy = pptPlayMode;
                            showPptPlayModeArea();
                            state = OPTION_STATE_PPT_PLAY_MODE;
                        }

                        break;

                    case R.id.photo_play_mode:// 播放模式
                        Log.i(TAG, "photo_play_mode is click");
                        photoPlayHolder.setAllUnSelect(isPhotoPlay, isPlaying);
                        photoPlayHolder.bt_photoPlayMode.setFocusable(false);
                        cancleDelayHide();
                        playMode_copy = playMode;
                        showPlayModeArea();
                        state = OPTION_STATE_PLAY_MODE;
                        break;
                    case R.id.photo_turn_right:
                        Log.i(TAG, "photo_turn_right is click");
                        if (is4K2KMode) {
                            Utils.showToast(PhotoPlayerActivity.this, R.string.display_not_support, Toast.LENGTH_SHORT);
                        } else {
                            photoPlayHolder.imageLL.setVisibility(View.VISIBLE);
                            photoPlayHolder.imageSurfaceLL.setVisibility(View.INVISIBLE);
                            photoPlayHolder.setAllUnSelect(isPhotoPlay, isPlaying);
                            rotateImageRight();
                            photoPlayHolder.setPhotoTurnRightSelect(true);
                            state = OPTION_STATE_TURNRIGHT;
                        }

                        break;

                    case R.id.photo_up: {
                        clearList();
                        moveNextOrPrevious(-1);
                    }
                        break;
                    case R.id.photo_firstphoto:
                    case R.id.photo_secendphoto:
                    case R.id.photo_thirdphoto:
                    case R.id.photo_fourthphoto: {
                        int _index = -1;
                        switch (v.getId()) {
                            case R.id.photo_firstphoto: {
                                if (m_option_index == 0)
                                    break;
                                _index = 0;
                                break;
                            }
                            case R.id.photo_secendphoto: {
                                if (m_option_index == 1)
                                    break;
                                _index = 1;
                                break;

                            }
                            case R.id.photo_thirdphoto: {
                                if (m_option_index == 2)
                                    break;
                                _index = 2;
                                break;
                            }
                            case R.id.photo_fourthphoto: {
                                if (m_option_index == 3)
                                    break;
                                _index = 3;
                                break;
                            }
                        }
                        if (_index != -1) {
                            clickPhotoChangeFocus(m_option_index, _index);
                            int i = _index - m_option_index;
                            if (currentPosition + i >= photoDataList.size())
                                currentPosition = currentPosition + i - count;
                            else if (currentPosition + i < 0)
                                currentPosition = currentPosition + i + count;
                            else
                                currentPosition = currentPosition + i;
                            m_index = currentPosition;
                            initBitmap();
                            m_option_index = _index;
                        }
                        break;
                    }
                    case R.id.photo_down: {
                        clearList();
                        moveNextOrPrevious(1);
                    }
                        break;
                    case R.id.photo_left_relativelayout: {
                        showPhotoLeftList();
                        break;
                    }
                    case R.id.photo_fun_enl_lenearlayout: {
                        matrix = photoPlayHolder.mImageView.getImageMatrix();
                        rect = photoPlayHolder.mImageView.getDrawable().getBounds();
                        matrix.getValues(values);
                        mapState.left = values[2];
                        mapState.top = values[5];
                        mapState.right = mapState.left + rect.width() * values[0];
                        mapState.bottom = mapState.top + rect.height() * values[0];

                        if (zoomTimes >= 10) {
                            zoomTimes = 0;
                            pointy = 0;
                            pointx = 0;
                            photoPlayHolder.mImageView.zoomTo(1.0f);
                            photoPlayHolder.photo_fun_lenearlayout.removeView(geom);
                        } else {
                            zoomTimes++;
                            photoPlayHolder.mImageView.zoomIn();
                        }
                        drawRecttangleAgain();
                        break;
                    }
                    default: {
                        System.err.println("default is click!!");
                        break;
                    }
                }
            }
        }
    }

    protected Bitmap decodeBitmap(String path, int width, int height) {
        // return BitmapUtils.getImageBitmap(path, width, height);
        return BitmapUtils.getImageThumbnail(path, width, height);
    }

    public Bitmap myExtractThumbnail(Bitmap bitmap, int width, int height) {
        if (bitmap == null)
            return null;
        int inSampleSize = (bitmap.getWidth() / width) > (bitmap.getHeight() / height) ? (bitmap.getWidth() / width)
                : (bitmap.getHeight() / height);
        if (inSampleSize <= 0)
            inSampleSize = 1;
        Bitmap myBitmap = null;
        myBitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth() / inSampleSize, bitmap.getHeight()
                / inSampleSize);
        return myBitmap;
    }

    /**
     * 鼠标点击切换图片时，取消之前的焦点，重新设置焦点
     * 
     * @param optinon 需要取消焦点的控件编号
     * @param nextoptinon 需要设置的控焦点控件编号
     */
    void clickPhotoChangeFocus(int option, int nextoption) {
        ImageView image = (ImageView) findViewById(imageviewbglist[m_option_index]);
        image.setVisibility(View.INVISIBLE);
        image = (ImageView) findViewById(imageviewlist[m_option_index]);
        image.setPadding(10, 10, 10, 10);
        image = (ImageView) findViewById(imageviewbglist[nextoption]);
        image.setVisibility(View.VISIBLE);
        image = (ImageView) findViewById(imageviewlist[nextoption]);
        image.setPadding(0, 0, 0, 0);
    }

    /**
     * 移动前一个或者下一个.
     * 
     * @param delta
     * @return void
     */
    protected void moveNextOrPrevious(int delta) {
        // if(bThreadRun)//如果还在下载图片，不刷新下一组图片
        // return;
        zoomTimes = 0;

        Log.i(TAG, "moveNextOrPrevious1\n");
        imgPost = currentPosition + delta;
        if (imgPost <= -1) {// 已处于
            imgPost = count - 1;
        } else if (imgPost >= count) {
            // imgPost = photoDataList.size() - 1;
            if (playMode == 0 && isPhotoPlay)// 如果是顺序播放器
            {
                Utils.showToast(this, R.string.already_is_the_last_picture, Toast.LENGTH_SHORT);
                PlayProcess();// 停止播放
                return;
            } else {
                imgPost = 0;
            }
        }
        m_index = currentPosition;
        Log.i(TAG, "moveNextOrPrevious2\n");
        synchronized (photoList) {
            if (delta < 0) {
                Log.i(TAG, "moveNextOrPrevious3\n");
                bitmapArrayMoveLeft();
            } else {
                Log.i(TAG, "moveNextOrPrevious4\n");
                bitmapArrayMoveRight();
            }
        }
        // refurbishLeftPhotos(delta);// 刷新左侧的6张图片
        currentPosition = imgPost;
    }

    /**
     * 停止图片的播放.
     */
    protected void stopPhotoPlay() {
        isPhotoPlay = false;// 停止循环.
        // photoPlayHolder.photo_play.setBackgroundResource(R.drawable.photo_auto_play);
        photoPlayHolder.setAllUnSelect(isPhotoPlay, isPlaying);
        photoPlayHolder.textphotoPlay.setText(R.string.play_photo);
        photoPlayHolder.bt_photoPlay.setImageResource(R.drawable.photo_player_icon_play_focus);
        if (!playControlLayout.isShown()) {
            // showPhotoLeftList();
            // showTextController();
        }
        moveAndAddDelay();
        if (handler != null) {
            handler.removeMessages(PPT_PLAYER);// 移除自动顺序播放的message
            handler.removeMessages(STOCHASTIC_PLAYER);// 移除随机播放的message
        }

        if (playMode == 2)// 随机播放后需要从新刷新左侧图片列表
        {
            // initLeftPhotos();
        }
    }

    /**
     * 图片向下解析.
     * 
     * @param null
     * @return void
     */
    protected void bitmapArrayMoveRight() {

        if (currentPosition == count - 1) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        refurbishBigPhoto(currentPosition, R.id.photo_image);
    }

    /**
     * 解析当前大图片.
     * 
     * @param null
     * @return void
     */
    protected void bitmapArrayCurrent() {
        if ((currentPosition) < count) {
            refurbishBigPhoto(currentPosition, R.id.photo_image);
        }
    }

    /**
     * 图片向上解析.
     * 
     * @param null
     * @return void
     */
    protected void bitmapArrayMoveLeft() {
        if (currentPosition == 0) {
            currentPosition = count - 1;
        } else {
            currentPosition--;
        }
        refurbishBigPhoto(currentPosition, R.id.photo_image);
    }

    /**
     * 显示控制条
     */
    private void showController() {
        if (playControlLayout != null) {
            if (playControlLayout.getVisibility() == View.INVISIBLE) {
                Animation downTocurrentAnimation = AnimationUtils.loadAnimation(this, R.anim.down_to_current);
                playControlLayout.startAnimation(downTocurrentAnimation);
                playControlLayout.setVisibility(View.VISIBLE);
                hidePhotoLeftList();
                hideTextController();
            }
        } else
            Log.i("showController", "playControlLayout is null ptr===");
    }

    /**
     * 多久之后隐藏控制条->加入定时器
     */
    private void hideControlDelay() {
        if (hideHandler == null)
            return;
        hideHandler.sendEmptyMessageDelayed(PhotoPlayerActivity.HIDE_PLAYER_CONTROL, DEFAULT_TIMEOUT);
    }

    /**
     * 多久之后隐藏左侧列表->加入定时器
     */
    private void hideLeftControlDelay() {
        if (hideHandler == null)
            return;
        hideHandler.sendEmptyMessageDelayed(PhotoPlayerActivity.HIDE_LEFT_CONTROL, DEFAULT_TIMEOUT);
    }

    /**
     * 多久之后隐藏主页下方的提示文字->加入定时器
     */
    private void hideTextControlDelay() {
        if (hideHandler == null)
            return;
        hideHandler.sendEmptyMessageDelayed(PhotoPlayerActivity.HIDE_TEXT_CONTROL, DEFAULT_TIMEOUT);
    }

    /**
     * 多久之后隐藏放大页面下方的提示文字->加入定时器
     */
    private void hideTextEnlargeControlDelay() {
        if (hideHandler == null)
            return;
        hideHandler.sendEmptyMessageDelayed(PhotoPlayerActivity.HIDE_TEXT_ENLARGE_CONTROL, DEFAULT_TIMEOUT);
    }

    /**
     * 取消延时隐藏
     */
    private void cancleDelayHide() {
        if (hideHandler == null)
            return;
        hideHandler.removeMessages(PhotoPlayerActivity.HIDE_PLAYER_CONTROL);
        hideHandler.removeMessages(PhotoPlayerActivity.HIDE_LEFT_CONTROL);
        hideHandler.removeMessages(PhotoPlayerActivity.HIDE_TEXT_CONTROL);
        hideHandler.removeMessages(PhotoPlayerActivity.HIDE_TEXT_ENLARGE_CONTROL);
    }

    /**
     * 切换中间大图时的动画（show）
     */
    private void centerPhotoShowAnimation() {
        Log.i(TAG, "pptPlayMode: " + pptPlayMode);
        if (pptPlayMode == 0) {
            setDisplayBitmap();
            if (is4K2KMode && button4K2K) {
                photoPlayHolder.imageSurfaceLL.setVisibility(View.VISIBLE);
                photoPlayHolder.imageLL.setVisibility(View.INVISIBLE);
            } else {
                photoPlayHolder.imageSurfaceLL.setVisibility(View.INVISIBLE);
                photoPlayHolder.imageLL.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (is4K2KMode) {
            setDisplayBitmap();
            if (button4K2K) {
                photoPlayHolder.imageSurfaceLL.setVisibility(View.VISIBLE);
                photoPlayHolder.imageLL.setVisibility(View.INVISIBLE);
            } else {
                photoPlayHolder.imageSurfaceLL.setVisibility(View.INVISIBLE);
                photoPlayHolder.imageLL.setVisibility(View.VISIBLE);
            }
            return;
        }

        // photoPlayHolder.mImageView.setImageDrawable(null);
        // photoPlayHolder.mImageView.clear();
        photoPlayHolder.imageSurfaceLL.setVisibility(View.INVISIBLE);
        photoPlayHolder.imageLL.setVisibility(View.VISIBLE);

        Animation animation;

        switch (pptPlayMode) {
            case 0:// 正常
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_normal);
                break;
            case 1:// 淡出效果
                animation = AnimationUtils.loadAnimation(this, R.anim.fade);
                break;
            case 2:// 从右到左
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_push_left_in);
                break;
            case 3:// 从左到右
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_push_right_in);
                break;
            case 4:// 从上到下
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_push_down_in);
                break;
            case 5:// 从下到上
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_push_up_in);
                break;
            case 6:// 从小到大
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_wave_scale);
                break;
            case 7:// 由大变小
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_zoom_enter);
                break;
            case 8:// 从左上角出来，并逐渐变大
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_zoom_exit);
                break;
            case 9:// 从
                animation = AnimationUtils.loadAnimation(this, R.anim.photo_wave_scale);
                break;
            default:
                animation = AnimationUtils.loadAnimation(this, R.anim.fade);
                break;
        }
        animation.setRepeatCount(0);
        animation.setRepeatMode(1);
        animation.setFillAfter(false);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                setDisplayBitmap();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
            }
        });

        photoPlayHolder.mImageView.startAnimation(animation);
    }

    /**
     * 隐藏下方控制条
     */
    private void hideController() {
        if (playControlLayout != null) {
            if (photoPlayHolder.ppt_play_mode_selector_area.isShown()
                    || photoPlayHolder.photo_play_mode_selector_area.isShown()) {
                return;
            } else {
                if (playControlLayout.getVisibility() == View.VISIBLE) {
                    Animation currentToUpAnimation = AnimationUtils.loadAnimation(this, R.anim.current_to_down);
                    // 启动动画
                    playControlLayout.startAnimation(currentToUpAnimation);
                    playControlLayout.setVisibility(View.INVISIBLE);
                }
            }
        } else
            Log.i("hideController", "playControlLayout is null ptr!!");
    }

    /**
     * 隐藏左侧控制条
     */
    private void hidePhotoLeftList() {
        if (photoPlayHolder.photo_left_list.isShown()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.photo_left_list_hide);
            animation.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    photoPlayHolder.photo_right_list_bg.setVisibility(View.INVISIBLE);
                    // photoPlayHolder.photo_left_list.setVisibility(View.INVISIBLE);
                }
            });
            photoPlayHolder.photo_left_list.startAnimation(animation);
        }
    }

    /**
     * 显示左侧控制条
     */
    private void showPhotoLeftList() {
        if (!photoPlayHolder.photo_left_list.isShown() && !playControlLayout.isShown()) {
            photoPlayHolder.photo_right_list_bg.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.photo_left_list_show);
            animation.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    // photoPlayHolder.photo_left_list.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
            photoPlayHolder.photo_left_list.startAnimation(animation);
        }
    }

    /**
     * 隐藏主页下方的提示文字
     */
    private void hideTextController() {
        photoPlayHolder.photo_autoplay.setVisibility(View.INVISIBLE);
        photoPlayHolder.photo_changephoto.setVisibility(View.INVISIBLE);
        photoPlayHolder.photo_stop.setVisibility(View.INVISIBLE);
        // photoPlayHolder.photo_showmenu.setVisibility(View.INVISIBLE);
    }

    /**
     * 隐藏放大界面下方的提示文字
     */
    private void hideTextEnlargeController() {
        photoPlayHolder.photo_stop.setVisibility(View.INVISIBLE);
        photoPlayHolder.photo_move.setVisibility(View.INVISIBLE);
        photoPlayHolder.photo_fun_enl.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示放大界面下方的提示文字
     */
    private void showTextEnlargeController() {
        photoPlayHolder.photo_stop.setVisibility(View.VISIBLE);
        photoPlayHolder.photo_move.setVisibility(View.VISIBLE);
        photoPlayHolder.photo_fun_enl.setVisibility(View.VISIBLE);
    }

    /**
     * 显示主页下方的提示文字
     */
    private void showTextController() {
        photoPlayHolder.photo_autoplay.setVisibility(View.VISIBLE);
        photoPlayHolder.photo_changephoto.setVisibility(View.VISIBLE);
        photoPlayHolder.photo_stop.setVisibility(View.VISIBLE);
        // photoPlayHolder.photo_showmenu.setVisibility(View.VISIBLE);
    }

    /**
     * 移除所有延时隐藏事件并重新添加
     */
    private void moveAndAddDelay() {
        cancleDelayHide();
        hideControlDelay();
        hideLeftControlDelay();
        hideTextControlDelay();
        hideTextEnlargeControlDelay();
    }

    /**
     * 处理各种隐藏的handler// 隔多长时间隐藏控制条
     */
    private Handler hideHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PhotoPlayerActivity.HIDE_PLAYER_CONTROL) {
                hideController();
            }
            if (msg.what == PhotoPlayerActivity.HIDE_LEFT_CONTROL) {
                // hidePhotoLeftList();
            }
            if (msg.what == PhotoPlayerActivity.HIDE_TEXT_CONTROL) {
                // hideTextController();
            }
            if (msg.what == PhotoPlayerActivity.HIDE_TEXT_ENLARGE_CONTROL) {
                // hideTextEnlargeController();
            }
        };
    };

    // /**
    // * 磁盘事件接收器
    // */
    //
    // public class PhotoDiskChangeReceiver extends BroadcastReceiver {
    // public void onReceive(Context context, Intent intent) {
    // String action = intent.getAction();
    // Uri uri = intent.getData();
    //
    // String path = null;
    // if (uri != null) {
    // path = uri.getPath();
    // }
    // Log.i(TAG, "action = " + action + ", path = " + path);
    // String lastPlayPath = "";
    // try {
    // FileItem fileInfo = photoDataList.get(photoDataList.size() - 1);
    // lastPlayPath = fileInfo.getLocation();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // Log.i(TAG, "lastPlayPath = " + lastPlayPath);
    // if (action.equals(Intent.ACTION_MEDIA_EJECT)) // 磁盘移除
    // {
    // if (path != null && lastPlayPath.startsWith(path)) {
    // // MmUtils.showToast(PhotoPlayerActivity.this,R.string.disk_eject,
    // // Toast.LENGTH_SHORT);
    // if (!firstRemoveDisk) {
    // return;
    // } else {
    // firstRemoveDisk = false;
    // }
    // mPreferences.edit().putBoolean(MainActivity.PREF_NEED_RELOAD,
    // true).apply();
    // sendBroadcast(new Intent(AudioService.ACTION_REMOTE_STOP));
    // new AlertDialog.Builder(photoPlayerActivity)
    // .setTitle(photoPlayerActivity.getResources().getString(R.string.show_info))
    // .setMessage(R.string.disk_eject_when_playing)
    // .setPositiveButton(photoPlayerActivity.getResources().getString(android.R.string.ok),
    // new AlertDialog.OnClickListener() {
    // @Override
    // public void onClick(DialogInterface dialog, int which) {
    // cleanPhotoCacheAndFinish();
    // return;
    // }
    // }).setCancelable(false).show();
    // }
    // } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
    // }
    // }
    // }

    // /**
    // * DLNA事件接收器
    // */
    // public class PhotoDlnaChangeReceiver extends BroadcastReceiver {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // String action = intent.getAction();
    // String devId = intent.getStringExtra("udn");
    // Log.i(TAG, "action: " + action + ", udn: " + devId + ", X: " +
    // photoListDevID);
    // try {
    // // DLNA设备断开
    // if (action.equals(DLNAScanner.DEVICE_DISAPPEAR)) {
    // if (devId != null && devId.equals(photoListDevID)) {
    // // Utils.showToast(PhotoPlayerActivity.this,
    // // R.string.dlna_disconnected, Toast.LENGTH_SHORT);
    // mPreferences.edit().putBoolean(MainActivity.PREF_NEED_RELOAD,
    // true).apply();
    // cleanPhotoCacheAndFinish();
    // }
    // }
    // } catch (Exception e) {
    // Log.e("DlnaChangeReceiver", "DlnaChangeReceiver error: " + e);
    // }
    // }
    // }

    private void setDisplayBitmap() {
        Log.i("ImageSurfaceView", "setDisplayBitmap");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!support4K2K) {
                    mHandler.sendEmptyMessage(RESTORE_NORMAL); // 如果不支持
                    Log.d(TAG, "setDisplayBitmap>>>>>>>support4K2K:" + support4K2K);
                    return;
                }

                if (is4K2KMode) {
                    mHandler.sendEmptyMessage(PLAY_NORMAL_PHOTO); // 设置Mode
                } else {
                    mHandler.sendEmptyMessage(RESTORE_NORMAL); // 设置Mode
                }
            }
        });

    }

    private boolean is4K2KPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = false;
        options.inInputShareable = true;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        Log.d(TAG, "options " + options.outHeight + " " + options.outWidth);
        if (options.outHeight >= 2160 && options.outWidth >= 3840) {
            return true;
        }
        return false;

    }

    // 切换ImageView和SuffaceView
    private final static int PLAY_NORMAL_PHOTO = 0xba;

    private final static int RESTORE_NORMAL = 0xbc;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESTORE_NORMAL:
                    if (rotate != 0) {
                        photoPlayHolder.mImageView.rotateImage(-(rotate));// 如果之前图片被旋转了，则需要先把图片还原
                        rotate = 0;
                    }
                    if (scaledDown > 0) {
                        photoPlayHolder.mImageView.zoomTo(1.0f);
                        scaledDown = 0;
                    }
                    if (publicBitmap[6] != null) {
                        photoPlayHolder.mImageView.setImageBitmap(publicBitmap[6]);
                    } else {
                        Utils.showToast(PhotoPlayerActivity.this, R.string.the_form_does_not_support,
                                Toast.LENGTH_SHORT);
                        photoPlayHolder.mImageView.setImageBitmap(publicBitmap[7]);
                    }

                    break;
                case PLAY_NORMAL_PHOTO:
                    photoPlayHolder.mImageSurface.destroyDrawingCache();
                    if (photoPlayHolder.mImageSurface.setImagePath(dir4K2K, windowWidth, windowHeight)) {
                        dir4K2K = "";
                        photoPlayHolder.mImageSurface.drawImage();
                    } else {
                        dir4K2K = "";
                        photoPlayHolder.mImageSurface.drawImage();
                        Utils.showToast(PhotoPlayerActivity.this, R.string.the_form_does_not_support,
                                Toast.LENGTH_SHORT);
                        break;
                    }

                    if (rotate != 0) {
                        photoPlayHolder.mImageView.rotateImage(-(rotate));// 如果之前图片被旋转了，则需要先把图片还原
                        rotate = 0;
                    }
                    if (scaledDown > 0) {
                        photoPlayHolder.mImageView.zoomTo(1.0f);
                        scaledDown = 0;
                    }
                    if (publicBitmap[6] != null) {
                        photoPlayHolder.mImageView.setImageBitmap(publicBitmap[6]);
                    } else {
                        photoPlayHolder.mImageView.setImageBitmap(publicBitmap[7]);
                    }
                    // photoPlayHolder.mImageView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    // /**
    // * DLNA事件接收器
    // */
    // public class Change4K2KReceiver extends BroadcastReceiver {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // String action = intent.getAction();
    // Log.i(TAG, action);
    // Boolean flag4k2k = (Boolean) intent.getExtras().get("mode");
    // Log.i(TAG, "广播接收到的模式" + flag4k2k);
    // if (null == flag4k2k) {
    // return;
    // }
    // boolean photoMode = flag4k2k.booleanValue(); // 如果true,选择4K2K模式
    // // 如果false,普通模式
    // try {
    // if (action.equals("com.haiertv.Change.4K2K")) {
    // if (is4K2KMode) {
    // if (!photoMode && !button4K2K) {
    // Utils.showToast(PhotoPlayerActivity.this, R.string.photo_is_in_normal,
    // Toast.LENGTH_SHORT);
    // } else if (!photoMode && button4K2K) {
    // button4K2K = false;
    // Utils.showToast(PhotoPlayerActivity.this, R.string.photo_is_enter_normal,
    // Toast.LENGTH_SHORT);
    // changeMode4K2K(false);
    // } else if (photoMode && button4K2K) {
    // Utils.showToast(PhotoPlayerActivity.this, R.string.photo_is_in_4k2k,
    // Toast.LENGTH_SHORT);
    // } else if (photoMode && !button4K2K) {
    // button4K2K = true;
    // Utils.showToast(PhotoPlayerActivity.this, R.string.photo_is_enter_4k2k,
    // Toast.LENGTH_SHORT);
    // changeMode4K2K(true);
    // }
    // } else {
    // // Utils.showToast(PhotoPlayerActivity.this,
    // //
    // photoPlayerActivity.getResources().getString(R.string.plantform_is_not_supprot_4k2k),
    // // Toast.LENGTH_SHORT);
    // }
    //
    // }
    // } catch (Exception e) {
    // Log.e("Change4K2KReceiver", "Change4K2KReceiver error: " + e);
    // }
    // }
    // }

    private void changeMode4K2K(boolean flag) {
        if (flag) {
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    photoPlayHolder.imageLL.setVisibility(View.INVISIBLE);
                    photoPlayHolder.imageSurfaceLL.setVisibility(View.VISIBLE);
                    super.handleMessage(msg);
                }
            };
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    photoPlayHolder.imageLL.setVisibility(View.VISIBLE);
                    photoPlayHolder.imageSurfaceLL.setVisibility(View.INVISIBLE);
                    super.handleMessage(msg);
                }
            };
            handler.sendEmptyMessageDelayed(0, 2000);

        }
    }

    private boolean fileExists() {
        FileItem item = photoDataList.get(currentPosition);
        String downloadURL = item.getDownloadURL();

        String filePath = Util.getPathByName(CACHE_PATH, ".png", downloadURL);
        System.out.println(filePath);
        File f = new File(filePath);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }

}
