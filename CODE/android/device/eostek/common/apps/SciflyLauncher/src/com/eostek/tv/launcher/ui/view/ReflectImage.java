
package com.eostek.tv.launcher.ui.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eostek.tv.launcher.HomeActivity;
import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.business.DownloadManager;
import com.eostek.tv.launcher.business.database.DBManager;
import com.eostek.tv.launcher.model.DownloadInfo;
import com.eostek.tv.launcher.model.MetroInfo;
import com.eostek.tv.launcher.ui.BlurActivity;
import com.eostek.tv.launcher.util.GoogleAnalyticsUtil;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.TvUtils;
import com.eostek.tv.launcher.util.UIUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 
 * projectName： TVLauncher
 * moduleName： ReflectImage.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-11-5 下午6:36:25
 * @Copyright © 2014 Eos Inc.
 */
/**
 * A reflection display custom components, can control the inverted image
 * display position and size. Each ReflectImage object which holds a MetroInfo
 * object, Keep relevant information for each Metro block, see more info
 * {@link MetroInfo}
 **/
public class ReflectImage extends RelativeLayout {

    private final String TAG = ReflectImage.class.getSimpleName();

    private DisplayImageOptions options;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    // if it's true then show the reflection
    private boolean mReflectionMode = true;

    // gap between reflection and origin
    private int mReflectionGap;

    // height of reflection
    private float mReflectionHeight;

    private ImageView mReflectImage;

    /** the origin button to show picture **/
    public ImageButton mOriginButton;

    /** the origin framelayout to show picture,include progressbar **/
    public FrameLayout mOriginFrameLayout;

    private FocusView focusView;

    private int pageNum;

    private int mHasReflectPadding;

    private Context mContext;

    private final Rect focusRect = new Rect();

    private MetroInfo metroInfo;

    private int mMarginTop;

    private Bitmap reflectBitmap;

    private Bitmap originBitmap;

    private final float SCALE = 1.1f;

    private final float SCALE_CENTER = 0.5f;

    private float factor;

    private int mFocusBorderGap;

    private int mPagegroupLeft;

    private int mImageMarginTop;

    private AppWidgetManager appWidgetManager;

    private LauncherAppWidgetHost appWidgetHost;

    private Bitmap widgetBitmap;

    private volatile LauncherWidgetHostView hostView;

    /**
     * the flag whether pass the hover event to its child.true do not pass to
     * its childs,false pass to its childs
     **/
    private boolean mInterceptHover;

    private int mId = 1;

    private final int GET_WIDGET_REFLECTION = 99;

    private final int SHOW_WIDGET_REFLECT = 100;

    private int mAppWidgetID = -1;

    private int tryTime = 0;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_WIDGET_REFLECTION:
                    setWidgetReflect(hostView);
                    break;
                case SHOW_WIDGET_REFLECT:
                    if (reflectBitmap != null) {
                        reflectBitmap.recycle();
                    }
                    reflectBitmap = (Bitmap) msg.obj;
                    if (mReflectImage != null && reflectBitmap != null) {
                        mReflectImage.setImageBitmap(reflectBitmap);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    public ReflectImage(Context context) {
        super(context);
    }

    public ReflectImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReflectImage(Context context, FocusView view) {
        super(context);
        this.focusView = view;
        initLayout(context);
    }

    public ReflectImage(Context context, AttributeSet attrs, FocusView view) {
        super(context, attrs);
        this.focusView = view;
        initLayout(context);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (mInterceptHover) {
            return true;
        }
        return super.onInterceptHoverEvent(event);
    }

    @Override
    public void onHoverChanged(boolean hovered) {
        Log.v(TAG, "onHoverChanged");
        super.onHoverChanged(hovered);
    }

    public void setListener() {
        mOriginButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            	
                if (hasFocus) {
                	//BenQ 828 左右山下循环滑动
                	if(LConstants.FEATURE_BENQ_828.equals(UIUtil.getSpecialCode())){
                		if(ReflectImage.this.getMetroInfo().getY() == 310 || ReflectImage.this.getMetroInfo().getX() == 1240){
                    		Settings.System.putInt(mContext.getContentResolver(), "bottomimg", 1);
                    		Log.d(TAG,"platform is BenQ 828");
                    	}
                	}
                	
                    if (HomeApplication.getFocusType() == LConstants.FOCUS_TYPE_STATIC) {
                        mOriginFrameLayout.setBackgroundResource(R.drawable.imagebutton_focus_border);
                        focusView.setVisibility(View.GONE);
                    } else if (HomeApplication.getFocusType() == LConstants.FOCUS_TYPE_DYNAMIC) {
                        getGlobalVisibleRect(focusRect);
                        // If there is reflection, because of the focus frame
                        // shows only in the whole picture, does not contain
                        // reflections area, so the focus frame top and
                        // bottom need to change
                        if (mReflectionMode) {
                            focusRect.top -= mHasReflectPadding;
                            focusRect.bottom -= mReflectionGap + mReflectionHeight + mHasReflectPadding;
                        }

                        // when ViewPager move, the View inside pagedgroud first
                        // gets the focus, the upper and the lower will be
                        // offset, the focus frame top and bottom need to
                        // subtract the marginTops
                        if (focusRect.top - metroInfo.getY() * factor >= mMarginTop) {
                            focusRect.top -= mMarginTop;
                            focusRect.bottom -= mMarginTop;
                        }

                        Log.v(TAG, " left = " + (focusRect.left) + "; top = " + (focusRect.top));

                        // Due to the design of my TV and movie library in the
                        // same Page, move left and right will scroll a distance
                        // (distance left +TV window width), move the focus
                        // position is obtained after the box (on the left on
                        // the right of the +TV window from the +TV window width
                        // distance), the focus location requires special
                        // treatment
                        if (pageNum == 0
                                && focusRect.left == mContext.getResources().getInteger(R.integer.focus_rect_left)
                                        - mFocusBorderGap) {
                            focusRect.left -= mPagegroupLeft
                                    + mContext.getResources().getInteger(R.integer.focus_rect_delta);
                            focusRect.right -= mPagegroupLeft
                                    + mContext.getResources().getInteger(R.integer.focus_rect_delta);
                        }
                        focusRect.left += mFocusBorderGap + 1;
                        focusRect.top += mFocusBorderGap + 1;
                        focusRect.bottom -= mFocusBorderGap * 2;
                        focusRect.right -= mFocusBorderGap * 2;

                        focusView.startAnimation(focusRect);
                    }
                    // when has focus ,clear the hover view,set the hover view
                    // to null if exsits
                    PagedGroup mParentGroup = (PagedGroup) getParent();
                    if (mParentGroup != null) {
                        ReflectImage tmpImage = (ReflectImage) mParentGroup.getHoverView();
                        if (tmpImage != null && tmpImage.mOriginButton != null && tmpImage.mOriginFrameLayout != null) {
                            viewScaleDown(tmpImage, SCALE, SCALE);
                            mParentGroup.setHoverView(null);
                        }
                    }
                    viewScaleUp(ReflectImage.this, SCALE, SCALE);
                } else {
                	if(LConstants.FEATURE_BENQ_828.equals(UIUtil.getSpecialCode())){
                		if(ReflectImage.this.getMetroInfo().getY() == 310 || ReflectImage.this.getMetroInfo().getX() == 1240){
                    		Settings.System.putInt(mContext.getContentResolver(), "bottomimg", 0);
                    		Log.d(TAG,"platform is BenQ 828");
                    	}
                	}
                    viewScaleDown(ReflectImage.this, SCALE, SCALE);
                    if (HomeApplication.getFocusType() == LConstants.FOCUS_TYPE_STATIC) {
                        mOriginFrameLayout.setBackgroundResource(R.color.transparent);
                    }
                }
            }

        });

        mOriginButton.setOnClickListener(new OriginButtonOnClickListener());

        mOriginButton.setOnHoverListener(new OnHoverListener() {

            @Override
            public boolean onHover(View v, MotionEvent event) {
                int what = event.getAction();
                switch (what) {
                    case MotionEvent.ACTION_HOVER_ENTER: // mouse enter
                        focusView.setVisibility(View.GONE);
                        viewScaleUp(ReflectImage.this, SCALE, SCALE);
                        // set hover view
                        PagedGroup mParentGroup = (PagedGroup) getParent();
                        mParentGroup.setHoverView(ReflectImage.this);
                        return true;
                    case MotionEvent.ACTION_HOVER_MOVE: // mouse move
                        mParentGroup = (PagedGroup) getParent();
                        mParentGroup.setHoverView(ReflectImage.this);
                        // if there is a focus view,clear the focus
                        if (mParentGroup.getFocusedChild() != null) {
                            if (mParentGroup.getFocusedChild() instanceof ReflectImage) {
                                ReflectImage tmpImage = (ReflectImage) mParentGroup.getFocusedChild();
                                mParentGroup.clearFocus();
                                tmpImage.mOriginFrameLayout.setBackgroundResource(R.color.transparent);
                                viewScaleDown(tmpImage, SCALE, SCALE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_HOVER_EXIT: // mouse exit
                        viewScaleDown(ReflectImage.this, SCALE, SCALE);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    /**
     * load picture from drawable
     * 
     * @param drawable
     */
    public void loadDrawable(int drawable) {
        startLoaded("drawable://" + drawable);
    }

    /**
     * load picture from given res ID
     * 
     * @param resId
     */
    public void setLocalResource(int resId) {
        setReflectResource(resId);
    }

    /**
     * load picture from http request
     * 
     * @param url
     */
    public void loadHttp(String url) {
        startLoaded(url);
    }

    /**
     * load picture from sdcard
     * 
     * @param filename
     */
    public void loadSDCard(String filename) {
        startLoaded("file:///mnt/sdcard/" + filename);
    }

    /**
     * load picture from asserts
     * 
     * @param filename
     */
    public void loadAssets(String filename) {
        startLoaded("assets://" + filename);
    }

    /**
     * load picture from content
     * 
     * @param filename
     */
    public void loadContent(String url) {
        startLoaded("content://" + url);
    }

    /**
     * when the background picture url is illeagle,call this method to load
     * image
     * 
     * @param url
     */
    public void loadWidget(String url, AppWidgetManager widgetManager, LauncherAppWidgetHost host) {
        appWidgetManager = widgetManager;
        appWidgetHost = host;
        startLoaded(url);
    }

    /**
     * create reflect bitmap from given bitmap,and set the reflect bitmap to
     * ReflectImage
     * 
     * @param bmp The origin bitmap
     */
    public void setReflectResource(Bitmap bmp) {
        // Drawable orginDrawable = new BitmapDrawable(getResources(), bmp);
        mOriginButton.setImageBitmap(bmp);
        int width = (int) (metroInfo.getWidthSize() * factor);
        int height = (int) (metroInfo.getHeightSize() * factor);
        reflectBitmap = drawReflection(Bitmap.createScaledBitmap(bmp, width, height, false));
        mReflectImage.setImageBitmap(reflectBitmap);
        // reflectBitmap.recycle();
        setReflectionVisible();
    }

    /**
     * create reflect bitmap from given resId,and set the reflect bitmap to
     * ReflectImage
     * 
     * @param resId
     */
    public void setReflectResource(int resId) {
        originBitmap = BitmapFactory.decodeResource(getResources(), resId);
        setReflectResource(originBitmap);
    }

    /**
     * set origin framelayout LayoutParams
     * 
     * @param width the origin layout width to set
     * @param height the origin layout height to set
     */
    public void setOriginLayoutParams(int width, int height) {
        LayoutParams lParams = new LayoutParams(width, height);
        mOriginFrameLayout.setLayoutParams(lParams);
    }

    /**
     * set reflection imageview LayoutParams
     * 
     * @param width the reflect layout width to set
     * @param height the reflect layout height to set
     */
    public void setmReflectImageParams(int width, int height) {
        RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(width, height);
        lParams.setMargins(mFocusBorderGap, mImageMarginTop, 0, 0);
        lParams.addRule(RelativeLayout.BELOW, mId);
        mReflectImage.setLayoutParams(lParams);
    }

    /**
     * release the bitmap create in ReflctImagge
     */
    public void releaseReflectBitmap() {
        if (reflectBitmap != null) {
            reflectBitmap.recycle();
            reflectBitmap = null;
        }
        if (widgetBitmap != null) {
            widgetBitmap.recycle();
            widgetBitmap = null;
        }
        if (originBitmap != null) {
            originBitmap.recycle();
            originBitmap = null;
        }
        hostView = null;
        appWidgetHost = null;
        System.gc();
    }

    public void setInterceptHover(boolean intercept) {
        mInterceptHover = intercept;
    }

    public void setPageNum(int num) {
        this.pageNum = num;
    }

    public void setMetroInfo(MetroInfo infMetroInfo) {
        this.metroInfo = infMetroInfo;
    }

    public MetroInfo getMetroInfo() {
        return metroInfo;
    }

    public void setReflectionGap(int reflectionGap) {
        this.mReflectionGap = reflectionGap;
    }

    public void setReflectionHeight(float reflectionHeight) {
        this.mReflectionHeight = reflectionHeight;
    }

    public void setReflectionMode(boolean isRef) {
        mReflectionMode = isRef;
        setReflectionVisible();
    }

    public boolean getReflectionMode() {
        return mReflectionMode;
    }

    private void initLayout(Context context) {
        this.mContext = (HomeActivity) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mOriginFrameLayout = (FrameLayout) inflater.inflate(R.layout.reflection_item, null);
        mOriginFrameLayout.setId(mId);
        mOriginButton = (ImageButton) mOriginFrameLayout.findViewById(R.id.image);
        mOriginButton.setScaleType(ScaleType.FIT_XY);
        addView(mOriginFrameLayout, 0);
        mReflectImage = new ImageView(context);
        RelativeLayout.LayoutParams lpParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lpParams.addRule(RelativeLayout.BELOW, mId);
        addView(mReflectImage, lpParams);
        setReflectionVisible();

        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        mReflectionGap = mContext.getResources().getInteger(R.integer.reflect_image_gap);
        mReflectionHeight = mContext.getResources().getInteger(R.integer.reflect_image_height);
        mHasReflectPadding = mContext.getResources().getInteger(R.integer.reflect_has_reflection_padding);
        mMarginTop = mContext.getResources().getInteger(R.integer.reflect_margin_top);
        mFocusBorderGap = mContext.getResources().getInteger(R.integer.focus_border_gap);
        mPagegroupLeft = mContext.getResources().getInteger(R.integer.page_group_left);
        mImageMarginTop = mContext.getResources().getInteger(R.integer.reflect_image_maring_top);
        setInterceptHover(false);

        factor = HomeApplication.getFactor();

        setListener();
    }

    private void setReflectionVisible() {
        if (mReflectionMode) {
            mReflectImage.setVisibility(VISIBLE);
        } else {
            mReflectImage.setVisibility(GONE);
        }
    }

    private void startLoaded(final String url) {
        Log.i(TAG, metroInfo.getItemType() + " ;" + url);

        if (metroInfo.getItemType() == LConstants.METRO_ITEM_WIDGET) {
            // if widget is installed,get widget view ; else load default
            // picture,download apk when click
            if ("com.eostek.wasuwidgethost".equals(metroInfo.getPkgName())) {
                metroInfo.setPkgName("com.eostek.scifly.widget");
            }
            boolean isApkInstalled = UIUtil.isApkInstalled(mContext, metroInfo.getPkgName(), metroInfo.getClsName(),
                    metroInfo.getItemType());
            if (isApkInstalled) {
                getWidgetView(metroInfo.getPkgName());
                return;
            }
        }

        final ProgressBar spinnerBar = (ProgressBar) mOriginFrameLayout.findViewById(R.id.loading);
        spinnerBar.setIndeterminate(true);
        ImageSize targetSize = new ImageSize(mOriginButton.getWidth(), mOriginButton.getHeight());
        imageLoader.loadImage(url, targetSize, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                spinnerBar.setVisibility(View.VISIBLE);
                Log.v(TAG, "onLoadingStarted " + url);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Object loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (loadedImage == null) {
                    UIUtil.uploadNetworkDataError(mContext,"onLoadingComplete image is nul");
                    Log.e(TAG, "onLoadingComplete image is null");
                } else {
                    setReflectResource((Bitmap) loadedImage);
                    spinnerBar.setVisibility(View.GONE);
                    Log.v(TAG, "onLoadingComplete " + url);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                // clear the cache when load failed
                ImageLoader.getInstance().clearDiskCache(url);
                ImageLoader.getInstance().clearMemoryCache(url);
                // reset the ETag for next time get data from network
                DBManager.getDbManagerInstance(HomeApplication.getInstance()).resetETag(UIUtil.getLanguage());
                Log.e(TAG, "onLoadingFailed " + failReason.toString());
            }

        }, new ImageLoadingProgressListener() {

            @Override
            public void onProgressUpdate(String arg0, View arg1, int current, int total) {
                // spinnerBar.setProgress(Math.round(100.0f * current / total));
                // Log.v(TAG, "onProgressUpdate " + Math.round(100.0f * current
                // / total));
            }
        });
    }

    /**
     * scale up the given view
     * 
     * @param view
     * @param xValue Horizontal scaling factor to apply at the end of the
     *            animation
     * @param yValue Vertical scaling factor to apply at the end of the
     *            animation
     */
    private void viewScaleUp(View view, float xValue, float yValue) {
        // Select the magnifying effect
        ScaleAnimation inAnimation = new ScaleAnimation(1.0f, xValue, 1.0f, yValue, ScaleAnimation.RELATIVE_TO_SELF,
                SCALE_CENTER, ScaleAnimation.RELATIVE_TO_SELF, SCALE_CENTER);
        inAnimation.setDuration(mContext.getResources().getInteger(R.integer.scale_animation_duration));
        inAnimation.setFillAfter(true);
        view.startAnimation(inAnimation);
        // To solve the View amplified by other View blocking problem
        view.bringToFront();
        mOriginButton.bringToFront();
    }

    /**
     * scale down the given view
     * 
     * @param view
     * @param xValue Horizontal scaling factor to apply at the end of the
     *            animation
     * @param yValue Vertical scaling factor to apply at the end of the
     *            animation
     */
    private void viewScaleDown(View view, float xValue, float yValue) {
        ScaleAnimation inAnimation = new ScaleAnimation(xValue, 1.0f, yValue, 1.0f, ScaleAnimation.RELATIVE_TO_SELF,
                SCALE_CENTER, ScaleAnimation.RELATIVE_TO_SELF, SCALE_CENTER);
        inAnimation.setDuration(mContext.getResources().getInteger(R.integer.scale_animation_duration));
        inAnimation.setFillAfter(true);
        view.startAnimation(inAnimation);
    }

    /**
     * get the widget with given package name
     * 
     * @return AppWidgetHostView if the application have widgets,else null
     */
    private void getWidgetView(String packageName) {
        long time = System.currentTimeMillis();
        if (appWidgetHost == null || appWidgetManager == null) {
            return;
        }
        AppWidgetProviderInfo newAppWidgetProviderInfo = new AppWidgetProviderInfo();
        List<AppWidgetProviderInfo> appWidgetInfos = new ArrayList<AppWidgetProviderInfo>();
        appWidgetInfos = appWidgetManager.getInstalledProviders();
        for (int j = 0; j < appWidgetInfos.size(); j++) {
            newAppWidgetProviderInfo = appWidgetInfos.get(j);
            if (newAppWidgetProviderInfo.provider.getClassName().equals(metroInfo.getClsName())) {
                String widgetID = UIUtil.getStringFromXml(mContext, metroInfo.getClsName());
                if (widgetID != null) {
                    mAppWidgetID = Integer.valueOf(widgetID);
                    addWidget(newAppWidgetProviderInfo);
                } else {
                    mAppWidgetID = appWidgetHost.allocateAppWidgetId();
                    boolean testboolean = appWidgetManager.bindAppWidgetIdIfAllowed(mAppWidgetID,
                            newAppWidgetProviderInfo.provider);
                    if (!testboolean) {
                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetID);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, newAppWidgetProviderInfo.provider);
                        ((Activity) mContext).startActivityForResult(intent, 2);
                    } else if (newAppWidgetProviderInfo.configure != null) {
                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                        intent.setComponent(newAppWidgetProviderInfo.configure);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetID);
                        ((Activity) mContext).startActivityForResult(intent, 3);
                    } else {
                        addWidget(newAppWidgetProviderInfo);
                    }
                }

            }
        }
        Log.v(TAG, " getWidgetView time = " + (System.currentTimeMillis() - time) + ";" + mAppWidgetID);
    }

    private void addWidget(AppWidgetProviderInfo newAppWidgetProviderInfo) {
        hostView = (LauncherWidgetHostView) appWidgetHost.createView(mContext, mAppWidgetID, newAppWidgetProviderInfo);
        LayoutParams lpLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        hostView.setLayoutParams(lpLayoutParams);
        // add widget view below the imagetbutton
        mOriginFrameLayout.addView(hostView, 0);
        // if has reflect,send message to get widget reflection
        if (mReflectionMode) {
            mHandler.sendEmptyMessageDelayed(GET_WIDGET_REFLECTION, 5 * 1000);
        }
        // write the widget id to xml file
        UIUtil.writeStringToXml(mContext, metroInfo.getClsName(), String.valueOf(mAppWidgetID));
    }

    private void setWidgetReflect(final LauncherWidgetHostView widgetHostView) {
        if (widgetHostView != null) {
            // create reflection for widget view
            if (widgetBitmap != null) {
                widgetBitmap.recycle();
            }
            widgetHostView.setDrawingCacheEnabled(true);
            widgetBitmap = widgetHostView.getDrawingCache();
            if (widgetBitmap != null) {
                int width = (int) (metroInfo.getWidthSize() * factor);
                int height = (int) (metroInfo.getHeightSize() * factor);
                Bitmap bitmap = drawReflection(Bitmap.createScaledBitmap(widgetBitmap, width, height, false));
                Message message = Message.obtain();
                message.what = SHOW_WIDGET_REFLECT;
                message.obj = bitmap;
                mHandler.sendMessage(message);
                widgetHostView.setDrawingCacheEnabled(false);
            } else {
                if (tryTime < 50) {
                    mHandler.sendEmptyMessageDelayed(GET_WIDGET_REFLECTION, 200);
                    tryTime++;
                } else {
                    tryTime = 0;
                    widgetHostView.setDrawingCacheEnabled(false);
                    Log.e(TAG, "can not get widgetBitmap after ten seconds");
                }
            }
        }

    }

    private Bitmap drawReflection(Bitmap originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1.0f);
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, false);

        Bitmap bitmap4Reflection = Bitmap.createBitmap(width, (int) (mReflectionHeight), Config.ARGB_8888);

        Canvas canvasRef = new Canvas(bitmap4Reflection);
        canvasRef.drawBitmap(reflectionImage, 0, mReflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, mReflectionGap, 0, mReflectionHeight, 0x20ffffff, 0x00000000,
                TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        canvasRef.drawRect(0, mReflectionGap, width, bitmap4Reflection.getHeight(), paint);
        reflectionImage.recycle();
        reflectionImage = null;

        // resize the reflect image to reduce memory
        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        bitmap4Reflection.compress(Bitmap.CompressFormat.PNG, 100, bmpStream);
        ByteArrayInputStream isBm = new ByteArrayInputStream(bmpStream.toByteArray());
        bitmap4Reflection.recycle();
        bitmap4Reflection = null;
        System.gc();

        Options options = new Options();
        options.inSampleSize = 2;
        bitmap4Reflection = BitmapFactory.decodeStream(isBm, null, options);
        try {
            isBm.close();
            bmpStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap4Reflection;
    }

    private class OriginButtonOnClickListener implements OnClickListener {

        /*
         * (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
            String pkgName = metroInfo.getPkgName();
            String clsName = metroInfo.getClsName();
            String extroInfo = metroInfo.getExtraStrInfo();
            Log.v(TAG, "onClick " + pkgName + "; " + clsName + ";" + metroInfo.getItemType());
            if (pkgName == null || clsName == null || TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(clsName)) {
                Log.e(TAG, "the pkgName or clsName is empty!");
                UIUtil.uploadNetworkDataError(mContext,"the pkgName or clsName is empty");
                return;
            }
            // if apk is not installed ,download it
            if (!UIUtil.isApkInstalled(mContext, pkgName, clsName, metroInfo.getItemType())) {

                GoogleAnalyticsUtil.sendEvent(metroInfo, false, false);

                if (TextUtils.isEmpty(metroInfo.getApkUrl())) {
                    // if the path is empty, then send toast msg.
                    String str = mContext.getResources().getString(R.string.download_illegal_url);
                    UIUtil.uploadNetworkDataError(mContext,"illegal apk download url");
                    Log.e(TAG, "illegal apk download url");
                    Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
                } else {
                    String fileName = metroInfo.getApkUrl().substring(metroInfo.getApkUrl().lastIndexOf("/") + 1);
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .getAbsolutePath() + File.separator + fileName;
                    String realFileName = filePath.split("\\?")[0];
                    File file = new File(realFileName);
                    if (!file.exists()) {
                        // if file does not exist, then start download
                        Intent intent = new Intent(mContext, BlurActivity.class);
                        intent.putExtra("DownloadUrl", metroInfo.getApkUrl());
                        mContext.startActivity(intent);
                    } else {
                        // file exists, that means has downloaded apk or
                        // downloading apk.
                        DownloadManager dm = DownloadManager.getDownloadManagerInstance(mContext);
                        DownloadInfo downloadInfo = dm.getDownloadInfo(metroInfo.getApkUrl());
                        if (downloadInfo == null) {
                            Log.e(TAG, "Database has no this info. The uri is " + metroInfo.getApkUrl());
                            return;
                        }
                        Log.i(TAG, "current_bytes  " + downloadInfo.getPresentBytes());
                        Log.i(TAG, "total_bytes  " + downloadInfo.getTotalBytes());
                        if (downloadInfo.getPresentBytes() != downloadInfo.getTotalBytes()) {
                            // is downloading
                            String str = mContext.getResources().getString(R.string.apk_downloading);
                            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
                        } else if (downloadInfo.getPresentBytes() == downloadInfo.getTotalBytes()) {
                            // downloaded
                            UIUtil.install(mContext, filePath);
                        }
                    }
                }
            } else {
                GoogleAnalyticsUtil.sendEvent(metroInfo, true, false);

                switch (metroInfo.getItemType()) {
                    case LConstants.METRO_ITEM_APK:
                        UIUtil.startApk(mContext, pkgName, clsName, extroInfo, metroInfo.getExtraIntInfo());
                        TvUtils.setInputToStorage(metroInfo.getPkgName());
                        break;
                    case LConstants.METRO_ITEM_SERVICE:

                        break;
                    case LConstants.METRO_ITEM_WEB_URL:

                        break;
                    case LConstants.METRO_ITEM_WIDGET:
                        if (clsName != null && !clsName.equals("")) {
                            Intent intent = new Intent(clsName);
                            mContext.sendBroadcast(intent);
                        }
                        break;
                    case LConstants.METRO_ITEM_OTHERS:

                        break;

                    default:
                        break;
                }
            }
        }

    }

}
