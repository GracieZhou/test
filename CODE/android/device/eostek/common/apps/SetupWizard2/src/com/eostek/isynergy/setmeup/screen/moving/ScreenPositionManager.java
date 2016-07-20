
package com.eostek.isynergy.setmeup.screen.moving;

import java.lang.reflect.Method;

import android.content.Context;

import com.eostek.isynergy.setmeup.utils.Utils;

public class ScreenPositionManager {
    private Class SystemWriteManager;

    private String TAG = "ScreenPositionManager";

    private Context mContext = null;

    private Object sw = null;

    private static final String mDisplayAxis1080 = " 1920 1080 ";

    private static final String mDisplayAxis720 = " 1280 720 ";

    private static final String mDisplayAxis576 = " 720 576 ";

    private static final String mDisplayAxis480 = " 720 480 ";

    // sysfs path
    private final static String mCurrentResolution = "/sys/class/display/mode";

    private static final String UpdateFreescaleFb0File = "/sys/class/graphics/fb0/update_freescale";

    private final static String FreeScaleOsd0File = "/sys/class/graphics/fb0/free_scale";

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

    private static final String window_axis = "/sys/class/graphics/fb0/window_axis";

    private static final String free_scale = "/sys/class/graphics/fb0/free_scale";

    public static boolean mIsOriginWinSet = false;

    private static final String[] outputmode_array = {
            "480i", "480p", "576i", "576p", "720p", "1080i", "1080p", "720p50hz", "1080i50hz", "1080p50hz", "480cvbs",
            "576cvbs", "4k2k24hz", "4k2k25hz", "4k2k30hz", "4k2ksmpte", "1080p24hz"
    };

    private int mCurrentLeft = 0;

    private int mCurrentTop = 0;

    private int mCurrentWidth = 0;

    private int mCurrentHeight = 0;

    private int mCurrentRight = 0;

    private int mCurrentBottom = 0;

    private int mPreLeft = 0;

    private int mPreTop = 0;

    private int mPreWidth = 0;

    private int mPreHeight = 0;

    private String mCurrentMode = null;

    private int mMaxRight = 0;

    private int mMaxBottom = 0;

    private int offsetStep = 2; // because 20% is too large ,so we divide a
                                // value to smooth the view

    public ScreenPositionManager(Context context) {
        mContext = context;
        try {
            SystemWriteManager = Class.forName("android.app.SystemWriteManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        sw = mContext.getSystemService("system_write");
    }

    private String readSysfs(String path) {
        String result = "";
        try {
            Method method = SystemWriteManager.getMethod("readSysfs", new Class[] {
                String.class
            });
            Object retobj = method.invoke(sw, path);
            result = (String) retobj;
            Utils.print(TAG, "readSysfs: " + result);
        } catch (Throwable e) {
            System.err.println(e);
        }
        return result;
    }

    private boolean getPropertyBoolean(String path, boolean b) {
        boolean result = false;
        try {
            Class paramType[] = new Class[2];
            paramType[0] = path.getClass();
            paramType[1] = boolean.class;
            Method method = SystemWriteManager.getMethod("getPropertyBoolean", paramType);
            Object paramObj[] = new Object[2];
            paramObj[0] = path;
            paramObj[1] = b;
            Object retobj = method.invoke(sw, paramObj);
            result = (Boolean) retobj;
            Utils.print(TAG, "getPbool: " + result);
        } catch (Throwable e) {
            System.err.println(e);
        }
        return result;
    }

    private boolean setProperty(String path1, String path2) {
        boolean result = false;
        try {
            Method method = SystemWriteManager.getMethod("setProperty", new Class[] {
                String.class, String.class
            });
            Object paramObj[] = new Object[2];
            paramObj[0] = path1;
            paramObj[1] = path2;
            Object retobj = method.invoke(sw, paramObj);
            result = (Boolean) retobj;
            Utils.print(TAG, "setPro: " + result);
        } catch (Throwable e) {
            System.err.println(e);
        }
        return result;
    }

    private boolean writeSysfs(String path1, String path2) {
        boolean result = false;
        try {
            Method method = SystemWriteManager.getMethod("writeSysfs", new Class[] {
                String.class, String.class
            });
            Object paramObj[] = new Object[2];
            paramObj[0] = path1;
            paramObj[1] = path2;
            Object retobj = method.invoke(sw, paramObj);
            result = (Boolean) retobj;
            Utils.print(TAG, "writeSysfs: " + result);
        } catch (Throwable e) {
            System.err.println(e);
        }
        return result;
    }

    private int getPropertyInt(String path, int i) {
        int result = 0;
        try {
            Class paramType[] = new Class[2];
            paramType[0] = path.getClass();
            paramType[1] = Integer.TYPE;
            Method method = SystemWriteManager.getMethod("getPropertyInt", paramType);
            Object paramObj[] = new Object[2];
            paramObj[0] = path;
            paramObj[1] = i;
            Object retobj = method.invoke(sw, paramObj);
            result = (Integer) retobj;
            Utils.print(TAG, "getPInt: " + result);
        } catch (Throwable e) {
            System.err.println(e);
        }
        return result;
    }

    public void initPostion() {

        mCurrentMode = readSysfs(mCurrentResolution);
        Utils.print(TAG, "initPostion(),mCurrentMode :" + mCurrentMode);
        initStep(mCurrentMode);
        initCurrentPostion();

        if (!getPropertyBoolean("ro.platform.has.realoutputmode", false)) {
            writeFile(FreeScaleOsd0File, "1");
        } else if (mCurrentMode.contains("720") || mCurrentMode.contains("1080")) {
            if (mPreLeft == 0 && mPreTop == 0) {
                setOriginWinForFreeScale();
            }
        }
        setScalingMinFreq(408000);
    }

    private void initCurrentPostion() {
        if (mCurrentMode.equals("480i")) {
            mPreLeft = getPropertyInt(sel_480ioutput_x, 0);
            mPreTop = getPropertyInt(sel_480ioutput_y, 0);
            mPreWidth = getPropertyInt(sel_480ioutput_width, OUTPUT480_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_480ioutput_height, OUTPUT480_FULL_HEIGHT);
        } else if (mCurrentMode.equals("480p")) {
            mPreLeft = getPropertyInt(sel_480poutput_x, 0);
            mPreTop = getPropertyInt(sel_480poutput_y, 0);
            mPreWidth = getPropertyInt(sel_480poutput_width, OUTPUT480_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_480poutput_height, OUTPUT480_FULL_HEIGHT);
        } else if (mCurrentMode.equals("576i")) {
            mPreLeft = getPropertyInt(sel_576ioutput_x, 0);
            mPreTop = getPropertyInt(sel_576ioutput_y, 0);
            mPreWidth = getPropertyInt(sel_576ioutput_width, OUTPUT576_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_576ioutput_height, OUTPUT576_FULL_HEIGHT);
        } else if (mCurrentMode.equals("576p")) {
            mPreLeft = getPropertyInt(sel_576poutput_x, 0);
            mPreTop = getPropertyInt(sel_576poutput_y, 0);
            mPreWidth = getPropertyInt(sel_576poutput_width, OUTPUT576_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_576poutput_height, OUTPUT576_FULL_HEIGHT);
        } else if (mCurrentMode.equals("720p") || mCurrentMode.equals("720p50hz")) {
            mPreLeft = getPropertyInt(sel_720poutput_x, 0);
            mPreTop = getPropertyInt(sel_720poutput_y, 0);
            mPreWidth = getPropertyInt(sel_720poutput_width, OUTPUT720_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_720poutput_height, OUTPUT720_FULL_HEIGHT);
        } else if (mCurrentMode.contains("1080i")) {
            mPreLeft = getPropertyInt(sel_1080ioutput_x, 0);
            mPreTop = getPropertyInt(sel_1080ioutput_y, 0);
            mPreWidth = getPropertyInt(sel_1080ioutput_width, OUTPUT1080_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_1080ioutput_height, OUTPUT1080_FULL_HEIGHT);
        } else if (mCurrentMode.contains("1080p")) {
            mPreLeft = getPropertyInt(sel_1080poutput_x, 0);
            mPreTop = getPropertyInt(sel_1080poutput_y, 0);
            mPreWidth = getPropertyInt(sel_1080poutput_width, OUTPUT1080_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_1080poutput_height, OUTPUT1080_FULL_HEIGHT);
        } else if (mCurrentMode.equals("480cvbs")) {
            mPreLeft = getPropertyInt(sel_480ioutput_x, 0);
            mPreTop = getPropertyInt(sel_480ioutput_y, 0);
            mPreWidth = getPropertyInt(sel_480ioutput_width, OUTPUT480_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_480ioutput_height, OUTPUT480_FULL_HEIGHT);
        } else if (mCurrentMode.equals("576cvbs")) {
            mPreLeft = getPropertyInt(sel_576ioutput_x, 0);
            mPreTop = getPropertyInt(sel_576ioutput_y, 0);
            mPreWidth = getPropertyInt(sel_576ioutput_width, OUTPUT576_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_576ioutput_height, OUTPUT576_FULL_HEIGHT);
        } else if (mCurrentMode.equals("4k2k24hz")) {
            mPreLeft = getPropertyInt(sel_4k2k24hzoutput_x, 0);
            mPreTop = getPropertyInt(sel_4k2k24hzoutput_y, 0);
            mPreWidth = getPropertyInt(sel_4k2k24hzoutput_width, OUTPUT4k2k_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_4k2k24hzoutput_height, OUTPUT4k2k_FULL_HEIGHT);
        } else if (mCurrentMode.equals("4k2k25hz")) {
            mPreLeft = getPropertyInt(sel_4k2k25hzoutput_x, 0);
            mPreTop = getPropertyInt(sel_4k2k25hzoutput_y, 0);
            mPreWidth = getPropertyInt(sel_4k2k25hzoutput_width, OUTPUT4k2k_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_4k2k25hzoutput_height, OUTPUT4k2k_FULL_HEIGHT);
        } else if (mCurrentMode.equals("4k2k30hz")) {
            mPreLeft = getPropertyInt(sel_4k2k30hzoutput_x, 0);
            mPreTop = getPropertyInt(sel_4k2k30hzoutput_y, 0);
            mPreWidth = getPropertyInt(sel_4k2k30hzoutput_width, OUTPUT4k2k_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_4k2k30hzoutput_height, OUTPUT4k2k_FULL_HEIGHT);
        } else if (mCurrentMode.equals("4k2ksmpte")) {
            mPreLeft = getPropertyInt(sel_4k2ksmpteoutput_x, 0);
            mPreTop = getPropertyInt(sel_4k2ksmpteoutput_y, 0);
            mPreWidth = getPropertyInt(sel_4k2ksmpteoutput_width, OUTPUT4k2ksmpte_FULL_WIDTH);
            mPreHeight = getPropertyInt(sel_4k2ksmpteoutput_height, OUTPUT4k2ksmpte_FULL_HEIGHT);
        }
        mCurrentLeft = mPreLeft;
        mCurrentTop = mPreTop;
        mCurrentWidth = mPreWidth;
        mCurrentHeight = mPreHeight;
        Utils.print(TAG, "initCurrentPostion(),mPreLeft :" + mPreLeft + ",mPreTop:" + mPreTop + ",mPreWidth:"
                + mPreWidth + ",mPreHeight:" + mPreHeight);
    }

    public void setCurrentPostion(int left, int top) {
        mCurrentMode = readSysfs(mCurrentResolution);
        initStep(mCurrentMode);

        mCurrentLeft = left;
        mCurrentRight = top;
        mCurrentRight = mMaxRight + mCurrentLeft;
        mCurrentBottom = mMaxBottom + mCurrentRight;
        mCurrentWidth = mCurrentRight + mCurrentLeft;
        mCurrentHeight = mCurrentBottom + mCurrentTop;
        Utils.print(TAG, "zoomByPercent(), mCurrentLeft : " + mCurrentLeft);
        Utils.print(TAG, "zoomByPercent(), mCurrentTop : " + mCurrentTop);
        Utils.print(TAG, "zoomByPercent(), mCurrentRight : " + mCurrentRight);
        Utils.print(TAG, "zoomByPercent(), mCurrentBottom : " + mCurrentBottom);
        Utils.print(TAG, "zoomByPercent(), mCurrentWidth : " + mCurrentWidth);
        Utils.print(TAG, "zoomByPercent(), mCurrentHeight : " + mCurrentHeight);

        setPosition(mCurrentLeft, mCurrentTop, mCurrentRight, mCurrentBottom, 0);
    }

    public int getRateValue() {
        mCurrentMode = readSysfs(mCurrentResolution);
        initStep(mCurrentMode);
        int m = (100 * 2 * offsetStep) * mPreLeft;
        if (m == 0) {
            return 100;
        }
        int rate = 100 - m / (mMaxRight + 1) - 1;
        Utils.print(TAG, "getRateValue() , value:" + rate);
        return rate;
    }

    public void savePostion() {
        /*
         * if(!mIsOriginWinSet) return;
         */
        if (!isScreenPositionChanged()) {
            return;
        }
        savePosition(mCurrentLeft, mCurrentTop, mCurrentWidth, mCurrentHeight);
        setScalingMinFreq(96000);
    }

    private void writeFile(String file, String value) {
        writeSysfs(file, value);
    }

    private void setOriginWinForFreeScale() {
        Utils.print(TAG, "setOriginWinForFreeScale(), mIsOriginWinSet:" + mIsOriginWinSet);
        if (mIsOriginWinSet) {
            return;
        } else {
            mIsOriginWinSet = true;
        }
        writeFile(freescale_mode, "1");
        writeFile(free_scale, "0x10001");
        Utils.print(TAG, "setOriginWinForFreeScale(), w:" + mPreWidth + ",h:" + mPreHeight);
    }

    private final void setScalingMinFreq(int scalingMinFreq) {

        int minFreq = scalingMinFreq;
        String minFreqString = Integer.toString(minFreq);

        writeSysfs(CPU0ScalingMinFreqPath, minFreqString);

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
                setProperty(sel_480ioutput_x, x);
                setProperty(sel_480ioutput_y, y);
                setProperty(sel_480ioutput_width, w);
                setProperty(sel_480ioutput_height, h);
                break;
            case 1: // 480p
                setProperty(sel_480poutput_x, x);
                setProperty(sel_480poutput_y, y);
                setProperty(sel_480poutput_width, w);
                setProperty(sel_480poutput_height, h);
                break;
            case 2: // 576i
            case 11: // 576cvbs
                setProperty(sel_576ioutput_x, x);
                setProperty(sel_576ioutput_y, y);
                setProperty(sel_576ioutput_width, w);
                setProperty(sel_576ioutput_height, h);
                break;
            case 3: // 576p
                setProperty(sel_576poutput_x, x);
                setProperty(sel_576poutput_y, y);
                setProperty(sel_576poutput_width, w);
                setProperty(sel_576poutput_height, h);
                break;
            case 4: // 720p
            case 7: // 720p50hz
                setProperty(sel_720poutput_x, x);
                setProperty(sel_720poutput_y, y);
                setProperty(sel_720poutput_width, w);
                setProperty(sel_720poutput_height, h);
                break;
            case 5: // 1080i
            case 8: // 1080i50hz
                setProperty(sel_1080ioutput_x, x);
                setProperty(sel_1080ioutput_y, y);
                setProperty(sel_1080ioutput_width, w);
                setProperty(sel_1080ioutput_height, h);
                break;
            case 6: // 1080p
            case 9: // 1080p50hz
            case 16: // 1080p24hz
                setProperty(sel_1080poutput_x, x);
                setProperty(sel_1080poutput_y, y);
                setProperty(sel_1080poutput_width, w);
                setProperty(sel_1080poutput_height, h);
                break;
            case 12: // 4k2k24hz
                setProperty(sel_4k2k24hzoutput_x, x);
                setProperty(sel_4k2k24hzoutput_y, y);
                setProperty(sel_4k2k24hzoutput_width, w);
                setProperty(sel_4k2k24hzoutput_height, h);
                break;
            case 13: // 4k2k25hz
                setProperty(sel_4k2k25hzoutput_x, x);
                setProperty(sel_4k2k25hzoutput_y, y);
                setProperty(sel_4k2k25hzoutput_width, w);
                setProperty(sel_4k2k25hzoutput_height, h);
                break;
            case 14: // 4k2k30hz
                setProperty(sel_4k2k30hzoutput_x, x);
                setProperty(sel_4k2k30hzoutput_y, y);
                setProperty(sel_4k2k30hzoutput_width, w);
                setProperty(sel_4k2k30hzoutput_height, h);
                break;
            case 15: // 4k2ksmpte
                setProperty(sel_4k2ksmpteoutput_x, x);
                setProperty(sel_4k2ksmpteoutput_y, y);
                setProperty(sel_4k2ksmpteoutput_width, w);
                setProperty(sel_4k2ksmpteoutput_height, h);
                break;

        }
        if (getPropertyBoolean("ro.platform.has.realoutputmode", false)) {
            String display_str = x + " " + y + getDisplayAxisByMode(mCurrentMode) + x + " " + y + " " + 18 + " " + 18;
            writeFile(DisplayAxisFile, display_str);
            writeFile(VideoAxisFile, x + " " + y + " " + (left + width - 1) + " " + (top + height - 1));
            Utils.print(TAG, "video_axis" + x + " " + y + " " + (left + width) + " " + (top + height));
        }
        Utils.print(TAG, "savePosition(), left:" + left + ",top:" + top + ",width:" + width + ",height:" + height);
    }

    public static String getDisplayAxisByMode(String mode) {
        if (mode.indexOf("1080") >= 0) {
            return mDisplayAxis1080;
        }
        else if (mode.indexOf("720") >= 0) {
            return mDisplayAxis720;
        }
        else if (mode.indexOf("576") >= 0) {
            return mDisplayAxis576;
        }
        else {
            return mDisplayAxis480;
        }
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

        mCurrentMode = readSysfs(mCurrentResolution);
        initStep(mCurrentMode);

        mCurrentLeft = (100 - percent) * (mMaxRight) / (100 * 2 * offsetStep);
        mCurrentTop = (100 - percent) * (mMaxBottom) / (100 * 2 * offsetStep);
        mCurrentRight = mMaxRight - mCurrentLeft;
        mCurrentBottom = mMaxBottom - mCurrentTop;
        mCurrentWidth = mCurrentRight - mCurrentLeft;
        mCurrentHeight = mCurrentBottom - mCurrentTop;
        Utils.print(TAG, "zoomByPercent(), mCurrentLeft : " + mCurrentLeft);
        Utils.print(TAG, "zoomByPercent(), mCurrentTop : " + mCurrentTop);
        Utils.print(TAG, "zoomByPercent(), mCurrentRight : " + mCurrentRight);
        Utils.print(TAG, "zoomByPercent(), mCurrentBottom : " + mCurrentBottom);
        Utils.print(TAG, "zoomByPercent(), mCurrentWidth : " + mCurrentWidth);
        Utils.print(TAG, "zoomByPercent(), mCurrentHeight : " + mCurrentHeight);

        setPosition(mCurrentLeft, mCurrentTop, mCurrentRight, mCurrentBottom, 0);

    }

    public void setPosition(int l, int t, int r, int b, int mode) {
        String str = "";
        int left = l;
        int top = t;
        int right = r;
        int bottom = b;

        if (left < 0) {
            left = 0;
        }

        if (top < 0) {
            top = 0;
        }
        right = Math.min(right, mMaxRight);
        bottom = Math.min(bottom, mMaxBottom);

        if (getPropertyBoolean("ro.platform.has.realoutputmode", false)) {
            writeFile(window_axis, left + " " + top + " " + (right - 1) + " " + (bottom - 1));
            writeFile(free_scale, "0x10001");
        } else {
            str = left + " " + top + " " + right + " " + bottom + " " + mode;
            writeFile(PpscalerRectFile, str);
            writeFile(UpdateFreescaleFb0File, "1");
        }

        Utils.print(TAG, "setPosition : " + left + "  " + top + " " + right + "   " + bottom);
    }

    // EosTek Patch Begin
    // comment : solve the stuck problem of screenposition
    public String[] getCurrentScreenAxis() {
        String axisFileString = readSysfs(window_axis);
        Utils.print(TAG, "axisFileString: " + axisFileString);
        int index = 0;
        for (int i = 0; i < axisFileString.length(); i++) {
            if (91 == axisFileString.charAt(i)) {
                index = i;
                break;
            }
        }
        Utils.print(TAG, "index: " + index);
        Utils.print(TAG, "axisFileString.length(): " + axisFileString.length());
        String axisString = "";
        axisString = axisFileString.substring(index + 1, axisFileString.length() - 1);
        Utils.print(TAG, "axisString: " + axisString);
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

    // EosTek end
    public boolean isScreenPositionChanged() {
        return !(mPreLeft == mCurrentLeft && mPreTop == mCurrentTop && mPreWidth == mCurrentWidth && mPreHeight == mCurrentHeight);
    }

}
