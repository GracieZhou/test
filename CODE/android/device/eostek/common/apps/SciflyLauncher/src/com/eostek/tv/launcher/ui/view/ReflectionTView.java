
package com.eostek.tv.launcher.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.ui.adapter.SourceAdapter;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.TvUtils;
import com.eostek.tv.launcher.util.UIUtil;

/**
 * projectName： TVLauncher 
 * moduleName： ReflectionTView.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-11-5 下午5:28:47
 * @Copyright © 2014 Eos Inc.
 */
/**
 * The custom with the reflecting component, used to display TV and Source;1
 * .which contains a SurfaceView, used to display TV small window ;2, a
 * ListView, used to display signal source list ; 3.Reflection will be different
 * when different signal sources selected
 **/
public class ReflectionTView extends RelativeLayout {

    private static final String TAG = ReflectionTView.class.getSimpleName();

    private static final String TerminalCharacteristicCode = UIUtil.getSpecialCode();

    private final float ScaleFactor = 1.1f;

    // if it's true then show the reflection
    private boolean mReflectionMode = true;

    // gap between reflection and origin
    private int mReflectionGap;

    // height of reflection
    private float mReflectionHeight;

    // the view to save the scale up item
    public View tmpView;

    private Matrix mMatrix = null;

    private boolean isgetCache = false;

    // the flag whether init tv
    public boolean isInitLV = false;

    private Context mContext;

    private SourceAdapter sourceAdapter;

    private ImageButton tvImageButton;

    private SurfaceView msurfaceView = null;

    private TextView textView;

    private ListView mListView;

    private String[] sourceList;

    private Bitmap originalImage;

    private Bitmap reflectionImage;

    private Bitmap reflection;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }

    };

    public ReflectionTView(Context context) {
        super(context);
        setWillNotDraw(false);
        init(context);
    }

    public ReflectionTView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init(context);
    }

    public ReflectionTView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        init(context);
    }

    /**
     * shadom from pixels
     * 
     * @param pixels
     * @param srcWidth
     * @param srcHeight
     * @param alphastart
     */
    public static void shadowFromPixels(int[] pixels, int srcWidth, int srcHeight, int alphastart) {
        int b = alphastart;
        float temp = (alphastart >>> 24);
        float percent = (temp / srcHeight);
        int alpha = 0;

        for (int i = 0; i < srcHeight; i++) {
            alpha = b - (((int) (i * percent)) << 24);
            for (int j = 0; j < srcWidth; j++) {
                pixels[j + i * srcWidth] = pixels[j + i * srcWidth] & alpha;
            }
        }
    }

    public int getReflectionGap() {
        return mReflectionGap;
    }

    public void setReflectionGap(int reflectionGap) {
        this.mReflectionGap = reflectionGap;
    }

    public float getReflectionHeight() {
        return mReflectionHeight;
    }

    public void setReflectionHeight(float reflectionHeight) {
        this.mReflectionHeight = reflectionHeight;
    }

    public void setReflectionMode(boolean isRef) {
        mReflectionMode = isRef;
        postInvalidate();
    }

    public boolean getReflectionMode() {
        return mReflectionMode;
    }

    public TextView getTextView() {
        return textView;
    }

    public SurfaceView getSurfaceView() {
        return msurfaceView;
    }

    public ImageButton getTvImageButton() {
        return tvImageButton;
    }

    public ListView getSourceListView() {
        return mListView;
    }

    /**
     * set the current source text
     * 
     * @param postion The index of string in list
     */
    public void setSourceText(int postion) {
        Log.v(TAG, "setSourceText postion = " + postion);
        textView.setText(sourceAdapter.getItem(postion).toString());
    }

    /**
     * reload source adapter when locale change
     */
    public void notifiDateSetChange() {
        if (TerminalCharacteristicCode.equals(LConstants.FEATURE_BENQ_828)) {
            mContext.getResources().getStringArray(R.array.source_array_828);
        } else {
            sourceList = mContext.getResources().getStringArray(R.array.source_array);
        }
        sourceAdapter = new SourceAdapter(mContext, sourceList);
        mListView = (ListView) findViewById(R.id.source_list);
        mListView.setAdapter(sourceAdapter);
    }

    /**
     * release the bitmap resource in TViewReflection
     */
    public void releaseTViewReflection() {
        if (originalImage != null) {
            originalImage.recycle();
            originalImage = null;
        }
        if (reflectionImage != null) {
            reflectionImage.recycle();
            reflectionImage = null;
        }
        if (reflection != null) {
            reflection.recycle();
            reflection = null;
        }
        System.gc();
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "draw" + isgetCache + " " + mReflectionMode);
        super.onDraw(canvas);
        if (isgetCache || !mReflectionMode) {
            return;
        }
        int height = getHeight();
        int width = getWidth();
        if (height <= 0 || width <= 0) {
            return;
        }

        Log.i(TAG, "" + width + " " + height);
        height -= mReflectionHeight;
        isgetCache = true;
        setDrawingCacheEnabled(true);
        releaseTViewReflection();
        originalImage = Bitmap.createBitmap(getDrawingCache());
        reflectionImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, mMatrix, false);
        originalImage.recycle();
        int[] pixels = new int[(int) (width * (height))];
        reflectionImage.getPixels(pixels, 0, width, 0, 0, width, height);
        shadowFromPixels(pixels, width, height, 0x20ffffff);
        reflectionImage.recycle();
        reflection = Bitmap.createBitmap(pixels, width, height, Config.ARGB_8888);
        canvas.drawBitmap(reflection, 0, height, null);
        reflection.recycle();

        setDrawingCacheEnabled(false);
        isgetCache = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int gap = getResources().getInteger(R.integer.tv_scale_gap);
        setMeasuredDimension(getMeasuredWidth() + gap * 2, getMeasuredHeight());
    }

    private class SouceItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemSelected position:" + position);
            // onItemSelected will be invoke at position 0 when listview first
            // init, so cancel the following work this time
            if (isInitLV) {
                isInitLV = false;
                return;
            }
            TextView tv = (TextView) view.findViewById(R.id.source_name);
            if (sourceAdapter.signalStatus[position]) {
                tv.setTextColor(mContext.getResources().getColor(R.color.text_white));
            } else {
                tv.setTextColor(mContext.getResources().getColor(R.color.text_grey));
            }
            if (null != tmpView) {
                UIUtil.viewScaleDown(mContext, tmpView, ScaleFactor, ScaleFactor);
            }
            UIUtil.viewScaleUp(mContext, view, ScaleFactor, ScaleFactor);
            tmpView = view;
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 300);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.d(TAG, "onNothingSelected");
            if (null != tmpView) {
                UIUtil.viewScaleDown(mContext, tmpView, ScaleFactor, ScaleFactor);
                tmpView = null;
            }
        }
    }

    private void init(Context context) {
        mContext = context;
        mMatrix = new Matrix();
        mMatrix.preScale(1, -1);

        LayoutInflater.from(mContext).inflate(R.layout.tv_layout, this, true);
        msurfaceView = (SurfaceView) findViewById(R.id.tv_sur);
        msurfaceView.setBackgroundColor(Color.TRANSPARENT);

        tvImageButton = (ImageButton) findViewById(R.id.tv_image);
        // init source listview
        if (TerminalCharacteristicCode.equals(LConstants.FEATURE_BENQ_828)) {
            Log.d(TAG, "TerminalCharacteristicCode=" + TerminalCharacteristicCode);// ///////////////////////
            sourceList = mContext.getResources().getStringArray(R.array.source_array_828);
        } else {
            sourceList = mContext.getResources().getStringArray(R.array.source_array);
        }
        sourceAdapter = new SourceAdapter(mContext, sourceList);
        mListView = (ListView) findViewById(R.id.source_list);
        mListView.setAdapter(sourceAdapter);
        mListView.setOnItemSelectedListener(new SouceItemSelectedListener());

        mReflectionHeight = mContext.getResources().getInteger(R.integer.reflect_image_height);
        mReflectionGap = mContext.getResources().getInteger(R.integer.reflect_image_gap);

        textView = (TextView) findViewById(R.id.tv_info);
        int curSourcePosition = TvUtils.getCurInputSourcePosition(mContext);
        textView.setText(sourceAdapter.getItem(curSourcePosition).toString());

    }
}
