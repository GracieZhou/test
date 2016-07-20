
package com.android.settings.display;

import android.app.SystemWriteManager;
import android.content.Context;
import android.util.Log;

public class ScreenPositionManager {
    private String TAG = "ScreenPositionManager";

    private Context mContext = null;

    private SystemWriteManager sw = null;

    private final int MAX_Height = 100;

    private final int MIN_Height = 80;

    private static float outputsize_per = 0.1f;

    private static final String mDisplayAxis1080 = " 1920 1080 ";

    private static final String mDisplayAxis720 = " 1280 720 ";

    private static final String mDisplayAxis576 = " 720 576 ";

    private static final String mDisplayAxis480 = " 720 480 ";

    // sysfs path
    private final static String mCurrentResolution = "/sys/class/display/mode";

    private static final String UpdateFreescaleFb0File = "/sys/class/graphics/fb0/update_freescale";

    private final static String FreeScaleOsd0File = "/sys/class/graphics/fb0/free_scale";

    private final static String FreeScaleOsd1File = "/sys/class/graphics/fb1/free_scale";

    private final static String PpscalerRectFile = "/sys/class/ppmgr/ppscaler_rect";

    private final static String DisplayAxisFile = "/sys/class/display/axis";

    private final static String VideoAxisFile = "/sys/class/video/axis";

    private final static String CPU0ScalingMinFreqPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";

    private final static String sel_480ioutput_x = "ubootenv.var.480ioutputx";

    private final static String sel_480ioutput_y = "ubootenv.var.480ioutputy";

    private final static String sel_480ioutput_width = "ubootenv.var.480ioutputwidth";

    private final static String sel_480ioutput_height = "ubootenv.var.480ioutputheight";

    private final static String sel_480poutput_x = "ubootenv.var.480poutputx";

    private final static String sel_480poutput_y = "ubootenv.var.480poutputy";

    private final static String sel_480poutput_width = "ubootenv.var.480poutputwidth";

    private final static String sel_480poutput_height = "ubootenv.var.480poutputheight";

    private final static String sel_576ioutput_x = "ubootenv.var.576ioutputx";

    private final static String sel_576ioutput_y = "ubootenv.var.576ioutputy";

    private final static String sel_576ioutput_width = "ubootenv.var.576ioutputwidth";

    private final static String sel_576ioutput_height = "ubootenv.var.576ioutputheight";

    private final static String sel_576poutput_x = "ubootenv.var.576poutputx";

    private final static String sel_576poutput_y = "ubootenv.var.576poutputy";

    private final static String sel_576poutput_width = "ubootenv.var.576poutputwidth";

    private final static String sel_576poutput_height = "ubootenv.var.576poutputheight";

    private final static String sel_720poutput_x = "ubootenv.var.720poutputx";

    private final static String sel_720poutput_y = "ubootenv.var.720poutputy";

    private final static String sel_720poutput_width = "ubootenv.var.720poutputwidth";

    private final static String sel_720poutput_height = "ubootenv.var.720poutputheight";

    private final static String sel_1080ioutput_x = "ubootenv.var.1080ioutputx";

    private final static String sel_1080ioutput_y = "ubootenv.var.1080ioutputy";

    private final static String sel_1080ioutput_width = "ubootenv.var.1080ioutputwidth";

    private final static String sel_1080ioutput_height = "ubootenv.var.1080ioutputheight";

    private final static String sel_1080poutput_x = "ubootenv.var.1080poutputx";

    private final static String sel_1080poutput_y = "ubootenv.var.1080poutputy";

    private final static String sel_1080poutput_width = "ubootenv.var.1080poutputwidth";

    private final static String sel_1080poutput_height = "ubootenv.var.1080poutputheight";

    private final static String sel_4k2k24hzoutput_x = "ubootenv.var.4k2k24hz_x";

    private final static String sel_4k2k24hzoutput_y = "ubootenv.var.4k2k24hz_y";

    private final static String sel_4k2k24hzoutput_width = "ubootenv.var.4k2k24hz_width";

    private final static String sel_4k2k24hzoutput_height = "ubootenv.var.4k2k24hz_height";

    private final static String sel_4k2k25hzoutput_x = "ubootenv.var.4k2k25hz_x";

    private final static String sel_4k2k25hzoutput_y = "ubootenv.var.4k2k25hz_y";

    private final static String sel_4k2k25hzoutput_width = "ubootenv.var.4k2k25hz_width";

    private final static String sel_4k2k25hzoutput_height = "ubootenv.var.4k2k25hz_height";

    private final static String sel_4k2k30hzoutput_x = "ubootenv.var.4k2k30hz_x";

    private final static String sel_4k2k30hzoutput_y = "ubootenv.var.4k2k30hz_y";

    private final static String sel_4k2k30hzoutput_width = "ubootenv.var.4k2k30hz_width";

    private final static String sel_4k2k30hzoutput_height = "ubootenv.var.4k2k30hz_height";

    private final static String sel_4k2ksmpteoutput_x = "ubootenv.var.4k2ksmpte_x";

    private final static String sel_4k2ksmpteoutput_y = "ubootenv.var.4k2ksmpte_y";

    private final static String sel_4k2ksmpteoutput_width = "ubootenv.var.4k2ksmpte_width";

    private final static String sel_4k2ksmpteoutput_height = "ubootenv.var.4k2ksmpte_height";

    private static final int OUTPUT480_FULL_WIDTH = 720;

    private static final int OUTPUT480_FULL_HEIGHT = 480;

    private static final int OUTPUT576_FULL_WIDTH = 720;

    private static final int OUTPUT576_FULL_HEIGHT = 576;

    private static final int OUTPUT720_FULL_WIDTH = 1280;

    private static final int OUTPUT720_FULL_HEIGHT = 720;

    private static final int OUTPUT1080_FULL_WIDTH = 1920;

    private static final int OUTPUT1080_FULL_HEIGHT = 1080;

    private static final int OUTPUT4k2k_FULL_WIDTH = 3840;

    private static final int OUTPUT4k2k_FULL_HEIGHT = 2160;

    private static final int OUTPUT4k2ksmpte_FULL_WIDTH = 4096;

    private static final int OUTPUT4k2ksmpte_FULL_HEIGHT = 2160;

    private static final String freescale_mode = "/sys/class/graphics/fb0/freescale_mode";

    private static final String free_scale_axis = "/sys/class/graphics/fb0/free_scale_axis";

    private static final String window_axis = "/sys/class/graphics/fb0/window_axis";

    private static final String free_scale = "/sys/class/graphics/fb0/free_scale";

    public static boolean mIsOriginWinSet = false;

    private static final String[] outputmode_array = {
            "480i", "480p", "576i", "576p", "720p", "1080i", "1080p", "720p50hz", "1080i50hz", "1080p50hz", "480cvbs",
            "576cvbs", "4k2k24hz", "4k2k25hz", "4k2k30hz", "4k2ksmpte", "1080p24hz"
    };

    private String mCurrentLeftString = null;

    private String mCurrentTopString = null;

    private String mCurrentWidthString = null;

    private String mCurrentHeightString = null;

    private int mCurrentLeft = 0;

    private int mCurrentTop = 0;

    private int mCurrentWidth = 0;

    private int mCurrentHeight = 0;

    private int mCurrentRate = MAX_Height;

    private int mCurrentRight = 0;

    private int mCurrentBottom = 0;

    private int mPreLeft = 0;

    private int mPreTop = 0;

    private int mPreRight = 0;

    private int mPreBottom = 0;

    private int mPreWidth = 0;

    private int mPreHeight = 0;

    private String mCurrentMode = null;

    private int mMaxRight = 0;

    private int mMaxBottom = 0;

    private int offsetStep = 2; // because 20% is too large ,so we divide a
                                // value to smooth the view

    public ScreenPositionManager(Context context) {
        mContext = context;
        sw = (SystemWriteManager) mContext.getSystemService("system_write");
    }

    public void initPostion() {

        mCurrentMode = sw.readSysfs(mCurrentResolution);
        Log.d(TAG, "initPostion(),mCurrentMode :" + mCurrentMode);
        initStep(mCurrentMode);
        initCurrentPostion();

        if (!sw.getPropertyBoolean("ro.platform.has.realoutputmode", false)) {
            writeFile(FreeScaleOsd0File, "1");
        } else if (mCurrentMode.contains("720") || mCurrentMode.contains("1080")) {
            if (mPreLeft == 0 && mPreTop == 0)
                setOriginWinForFreeScale();
        }
        setScalingMinFreq(408000);
    }

    private void initCurrentPostion() {
        if (mCurrentMode.equals("480i")) {
            mPreLeft = sw.getPropertyInt(sel_480ioutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_480ioutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_480ioutput_width, OUTPUT480_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_480ioutput_height, OUTPUT480_FULL_HEIGHT);
        } else if (mCurrentMode.equals("480p")) {
            mPreLeft = sw.getPropertyInt(sel_480poutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_480poutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_480poutput_width, OUTPUT480_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_480poutput_height, OUTPUT480_FULL_HEIGHT);
        } else if (mCurrentMode.equals("576i")) {
            mPreLeft = sw.getPropertyInt(sel_576ioutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_576ioutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_576ioutput_width, OUTPUT576_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_576ioutput_height, OUTPUT576_FULL_HEIGHT);
        } else if (mCurrentMode.equals("576p")) {
            mPreLeft = sw.getPropertyInt(sel_576poutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_576poutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_576poutput_width, OUTPUT576_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_576poutput_height, OUTPUT576_FULL_HEIGHT);
        } else if (mCurrentMode.equals("720p") || mCurrentMode.equals("720p50hz")) {
            mPreLeft = sw.getPropertyInt(sel_720poutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_720poutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_720poutput_width, OUTPUT720_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_720poutput_height, OUTPUT720_FULL_HEIGHT);
        } else if (mCurrentMode.contains("1080i")) {
            mPreLeft = sw.getPropertyInt(sel_1080ioutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_1080ioutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_1080ioutput_width, OUTPUT1080_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_1080ioutput_height, OUTPUT1080_FULL_HEIGHT);
        } else if (mCurrentMode.contains("1080p")) {
            mPreLeft = sw.getPropertyInt(sel_1080poutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_1080poutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_1080poutput_width, OUTPUT1080_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_1080poutput_height, OUTPUT1080_FULL_HEIGHT);
        } else if (mCurrentMode.equals("480cvbs")) {
            mPreLeft = sw.getPropertyInt(sel_480ioutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_480ioutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_480ioutput_width, OUTPUT480_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_480ioutput_height, OUTPUT480_FULL_HEIGHT);
        } else if (mCurrentMode.equals("576cvbs")) {
            mPreLeft = sw.getPropertyInt(sel_576ioutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_576ioutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_576ioutput_width, OUTPUT576_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_576ioutput_height, OUTPUT576_FULL_HEIGHT);
        } else if (mCurrentMode.equals("4k2k24hz")) {
            mPreLeft = sw.getPropertyInt(sel_4k2k24hzoutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_4k2k24hzoutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_4k2k24hzoutput_width, OUTPUT4k2k_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_4k2k24hzoutput_height, OUTPUT4k2k_FULL_HEIGHT);
        } else if (mCurrentMode.equals("4k2k25hz")) {
            mPreLeft = sw.getPropertyInt(sel_4k2k25hzoutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_4k2k25hzoutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_4k2k25hzoutput_width, OUTPUT4k2k_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_4k2k25hzoutput_height, OUTPUT4k2k_FULL_HEIGHT);
        } else if (mCurrentMode.equals("4k2k30hz")) {
            mPreLeft = sw.getPropertyInt(sel_4k2k30hzoutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_4k2k30hzoutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_4k2k30hzoutput_width, OUTPUT4k2k_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_4k2k30hzoutput_height, OUTPUT4k2k_FULL_HEIGHT);
        } else if (mCurrentMode.equals("4k2ksmpte")) {
            mPreLeft = sw.getPropertyInt(sel_4k2ksmpteoutput_x, 0);
            mPreTop = sw.getPropertyInt(sel_4k2ksmpteoutput_y, 0);
            mPreWidth = sw.getPropertyInt(sel_4k2ksmpteoutput_width, OUTPUT4k2ksmpte_FULL_WIDTH);
            mPreHeight = sw.getPropertyInt(sel_4k2ksmpteoutput_height, OUTPUT4k2ksmpte_FULL_HEIGHT);
        }
        mCurrentLeft = mPreLeft;
        mCurrentTop = mPreTop;
        mCurrentWidth = mPreWidth;
        mCurrentHeight = mPreHeight;
        Log.d(TAG, "==== initCurrentPostion(),mPreLeft :" + mPreLeft + ",mPreTop:" + mPreTop + ",mPreWidth:"
                + mPreWidth + ",mPreHeight:" + mPreHeight);
    }

    public void setCurrentPostion(int left, int top) {
        mCurrentMode = sw.readSysfs(mCurrentResolution);
        initStep(mCurrentMode);

        mCurrentLeft = left;
        mCurrentRight = top;
        mCurrentRight = mMaxRight + mCurrentLeft;
        mCurrentBottom = mMaxBottom + mCurrentRight;
        mCurrentWidth = mCurrentRight + mCurrentLeft;
        mCurrentHeight = mCurrentBottom + mCurrentTop;
        Log.d(TAG, "====== zoomByPercent(), mCurrentLeft : " + mCurrentLeft);
        Log.d(TAG, "====== zoomByPercent(), mCurrentTop : " + mCurrentTop);
        Log.d(TAG, "====== zoomByPercent(), mCurrentRight : " + mCurrentRight);
        Log.d(TAG, "====== zoomByPercent(), mCurrentBottom : " + mCurrentBottom);
        Log.d(TAG, "====== zoomByPercent(), mCurrentWidth : " + mCurrentWidth);
        Log.d(TAG, "====== zoomByPercent(), mCurrentHeight : " + mCurrentHeight);

        setPosition(mCurrentLeft, mCurrentTop, mCurrentRight, mCurrentBottom, 0);
    }

    public int getRateValue() {
        mCurrentMode = sw.readSysfs(mCurrentResolution);
        initStep(mCurrentMode);
        int m = (100 * 2 * offsetStep) * mPreLeft;
        if (m == 0) {
            return 100;
        }
        int rate = 100 - m / (mMaxRight + 1) - 1;
        Log.d(TAG, "====  getRateValue() , value:" + rate);
        return rate;
    }

    public void savePostion() {
        /*
         * if(!mIsOriginWinSet) return;
         */
        if (!isScreenPositionChanged())
            return;
        savePosition(mCurrentLeft, mCurrentTop, mCurrentWidth, mCurrentHeight);
        setScalingMinFreq(96000);
    }

    private void writeFile(String file, String value) {
        sw.writeSysfs(file, value);
    }

    private void setOriginWinForFreeScale() {
        Log.e(TAG, "==== setOriginWinForFreeScale(), mIsOriginWinSet:" + mIsOriginWinSet);
        if (mIsOriginWinSet)
            return;
        else
            mIsOriginWinSet = true;

        writeFile(freescale_mode, "1");
        writeFile(free_scale, "0x10001");
        Log.e(TAG, "==== setOriginWinForFreeScale(), w:" + mPreWidth + ",h:" + mPreHeight);
    }

    private final void setScalingMinFreq(int scalingMinFreq) {

        int minFreq = scalingMinFreq;
        String minFreqString = Integer.toString(minFreq);

        sw.writeSysfs(CPU0ScalingMinFreqPath, minFreqString);

    }

    private void initStep(String mode) {

        if (mode.contains("480")) {
            mMaxRight = 719;
            mMaxBottom = 479;
        } else if (mode.contains("576")) {
            mMaxRight = 719;
            mMaxBottom = 575;
        } else if (mode.contains("720")) {
            mMaxRight = 1279;
            mMaxBottom = 719;
        } else if (mode.contains("1080")) {
            mMaxRight = 1919;
            mMaxBottom = 1079;
        } else if (mode.contains("4k")) {
            if (mode.contains("4k2ksmpte")) {
                mMaxRight = OUTPUT4k2ksmpte_FULL_WIDTH - 1;
                mMaxBottom = OUTPUT4k2ksmpte_FULL_HEIGHT - 1;
            } else {
                mMaxRight = OUTPUT4k2k_FULL_WIDTH - 1;
                mMaxBottom = OUTPUT4k2k_FULL_HEIGHT - 1;
            }
        } else {
            mMaxRight = 1919;
            mMaxBottom = 1079;
        }
    }
    
    public int getmMaxRight() {
        return mMaxRight;
    }

    public int getmMaxBottom() {
        return mMaxBottom;
    }

    public void savePosition(int left, int top, int width, int height) {
        int index = 4; // 720p
        String x = String.valueOf(left);
        String y = String.valueOf(top);
        String w = String.valueOf(width);
        String h = String.valueOf(height);

        for (int i = 0; i < outputmode_array.length; i++) {
            if (mCurrentMode.equalsIgnoreCase(outputmode_array[i])) {
                index = i;
            }
        }
        switch (index) {
            case 0: // 480i
            case 10: // 480cvbs
                sw.setProperty(sel_480ioutput_x, x);
                sw.setProperty(sel_480ioutput_y, y);
                sw.setProperty(sel_480ioutput_width, w);
                sw.setProperty(sel_480ioutput_height, h);
                break;
            case 1: // 480p
                sw.setProperty(sel_480poutput_x, x);
                sw.setProperty(sel_480poutput_y, y);
                sw.setProperty(sel_480poutput_width, w);
                sw.setProperty(sel_480poutput_height, h);
                break;
            case 2: // 576i
            case 11: // 576cvbs
                sw.setProperty(sel_576ioutput_x, x);
                sw.setProperty(sel_576ioutput_y, y);
                sw.setProperty(sel_576ioutput_width, w);
                sw.setProperty(sel_576ioutput_height, h);
                break;
            case 3: // 576p
                sw.setProperty(sel_576poutput_x, x);
                sw.setProperty(sel_576poutput_y, y);
                sw.setProperty(sel_576poutput_width, w);
                sw.setProperty(sel_576poutput_height, h);
                break;
            case 4: // 720p
            case 7: // 720p50hz
                sw.setProperty(sel_720poutput_x, x);
                sw.setProperty(sel_720poutput_y, y);
                sw.setProperty(sel_720poutput_width, w);
                sw.setProperty(sel_720poutput_height, h);
                break;
            case 5: // 1080i
            case 8: // 1080i50hz
                sw.setProperty(sel_1080ioutput_x, x);
                sw.setProperty(sel_1080ioutput_y, y);
                sw.setProperty(sel_1080ioutput_width, w);
                sw.setProperty(sel_1080ioutput_height, h);
                break;
            case 6: // 1080p
            case 9: // 1080p50hz
            case 16: // 1080p24hz
                sw.setProperty(sel_1080poutput_x, x);
                sw.setProperty(sel_1080poutput_y, y);
                sw.setProperty(sel_1080poutput_width, w);
                sw.setProperty(sel_1080poutput_height, h);
                break;
            case 12: // 4k2k24hz
                sw.setProperty(sel_4k2k24hzoutput_x, x);
                sw.setProperty(sel_4k2k24hzoutput_y, y);
                sw.setProperty(sel_4k2k24hzoutput_width, w);
                sw.setProperty(sel_4k2k24hzoutput_height, h);
                break;
            case 13: // 4k2k25hz
                sw.setProperty(sel_4k2k25hzoutput_x, x);
                sw.setProperty(sel_4k2k25hzoutput_y, y);
                sw.setProperty(sel_4k2k25hzoutput_width, w);
                sw.setProperty(sel_4k2k25hzoutput_height, h);
                break;
            case 14: // 4k2k30hz
                sw.setProperty(sel_4k2k30hzoutput_x, x);
                sw.setProperty(sel_4k2k30hzoutput_y, y);
                sw.setProperty(sel_4k2k30hzoutput_width, w);
                sw.setProperty(sel_4k2k30hzoutput_height, h);
                break;
            case 15: // 4k2ksmpte
                sw.setProperty(sel_4k2ksmpteoutput_x, x);
                sw.setProperty(sel_4k2ksmpteoutput_y, y);
                sw.setProperty(sel_4k2ksmpteoutput_width, w);
                sw.setProperty(sel_4k2ksmpteoutput_height, h);
                break;

        }
        if (sw.getPropertyBoolean("ro.platform.has.realoutputmode", false)) {
            String display_str = x + " " + y + getDisplayAxisByMode(mCurrentMode) + x + " " + y + " " + 18 + " " + 18;
            writeFile(DisplayAxisFile, display_str);
            writeFile(VideoAxisFile, x + " " + y + " " + (left + width - 1) + " " + (top + height - 1));
            Log.d(TAG, "==== video_axis" + x + " " + y + " " + (left + width) + " " + (top + height));
        }
        Log.d(TAG, "=====savePosition(), left:" + left + ",top:" + top + ",width:" + width + ",height:" + height);
    }

    public static String getDisplayAxisByMode(String mode) {
        if (mode.indexOf("1080") >= 0)
            return mDisplayAxis1080;
        else if (mode.indexOf("720") >= 0)
            return mDisplayAxis720;
        else if (mode.indexOf("576") >= 0)
            return mDisplayAxis576;
        else
            return mDisplayAxis480;
    }

    public void zoomByPercent(int percent) {

        if (percent > 100) {
            percent = 100;
            return;
        }

        if (percent < 80) {
            percent = 80;
            return;
        }

        mCurrentMode = sw.readSysfs(mCurrentResolution);
        initStep(mCurrentMode);

        mCurrentLeft = (100 - percent) * (mMaxRight) / (100 * 2 * offsetStep);
        mCurrentTop = (100 - percent) * (mMaxBottom) / (100 * 2 * offsetStep);
        mCurrentRight = mMaxRight - mCurrentLeft;
        mCurrentBottom = mMaxBottom - mCurrentTop;
        mCurrentWidth = mCurrentRight - mCurrentLeft;
        mCurrentHeight = mCurrentBottom - mCurrentTop;
        Log.d(TAG, "====== zoomByPercent(), mCurrentLeft : " + mCurrentLeft);
        Log.d(TAG, "====== zoomByPercent(), mCurrentTop : " + mCurrentTop);
        Log.d(TAG, "====== zoomByPercent(), mCurrentRight : " + mCurrentRight);
        Log.d(TAG, "====== zoomByPercent(), mCurrentBottom : " + mCurrentBottom);
        Log.d(TAG, "====== zoomByPercent(), mCurrentWidth : " + mCurrentWidth);
        Log.d(TAG, "====== zoomByPercent(), mCurrentHeight : " + mCurrentHeight);

        setPosition(mCurrentLeft, mCurrentTop, mCurrentRight, mCurrentBottom, 0);

    }

    public void setPosition(int l, int t, int r, int b, int mode) {
        String str = "";
        int left = l;
        int top = t;
        int right = r;
        int bottom = b;
        int width = mCurrentWidth;
        int hight = mCurrentHeight;

        if (left < 0) {
            left = 0;
        }

        if (top < 0) {
            top = 0;
        }
        right = Math.min(right, mMaxRight);
        bottom = Math.min(bottom, mMaxBottom);

        if (sw.getPropertyBoolean("ro.platform.has.realoutputmode", false)) {
            writeFile(window_axis, left + " " + top + " " + (right - 1) + " " + (bottom - 1));
            writeFile(free_scale, "0x10001");
        } else {
            str = left + " " + top + " " + right + " " + bottom + " " + mode;
            writeFile(PpscalerRectFile, str);
            writeFile(UpdateFreescaleFb0File, "1");
        }

        Log.d(TAG, "====== setPosition : " + left + "  " + top + " " + right + "   " + bottom);
    }

    // comment : solve the stuck problem of screenposition
    public String[] getCurrentScreenAxis() {
        String axisFileString = sw.readSysfs(window_axis);
        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>axisFileString: " + axisFileString);
        int index = 0;
        for (int i = 0; i < axisFileString.length(); i++) {
            if (91 == axisFileString.charAt(i)) {
                index = i;
                break;
            }
        }
        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>index: " + index);
        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>axisFileString.length(): " + axisFileString.length());
        String axisString = "";
        axisString = axisFileString.substring(index + 1, axisFileString.length() - 1);
        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>axisString: " + axisString);
        if (null != axisString) {
            String[] currentSize = axisString.split(" ");
            return currentSize;
        } else {
            String[] defaultAxis = {
                    "0", "0", "1279", "719"
            };
            return defaultAxis;
        }
    }


    public boolean isScreenPositionChanged() {
        if (mPreLeft == mCurrentLeft && mPreTop == mCurrentTop && mPreWidth == mCurrentWidth
                && mPreHeight == mCurrentHeight)
            return false;
        else
            return true;
    }

}
