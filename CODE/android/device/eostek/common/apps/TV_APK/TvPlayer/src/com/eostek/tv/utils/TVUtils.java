
package com.eostek.tv.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;
import android.view.SurfaceHolder;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPvrManager;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.AudioManager;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumScreenMuteType;
import com.mstar.android.tvapi.common.vo.PvrFileInfo;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;

/*
 * projectName： Tv
 * moduleName： TVUtils.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-7-27 下午5:08:32
 * @Copyright © 2014 Eos Inc.
 */

public class TVUtils {

    public static int getCurTvSource() {
        return TvCommonManager.getInstance().getCurrentTvInputSource();
    }

    /**
     * Get Current input source. If current source is storage, we can get the tv
     * source(un storage) from database by this function.
     * 
     * @param context
     * @return
     */
    public static int queryCurInputSrc(Context context) {
        int value = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mstar.tv.usersetting/systemsetting"),
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        cursor.close();
        return value;
    }

    public static void setInputSource(int source) {
        TvCommonManager.getInstance().setInputSource(source);
    }

    public static void setStorage() {
        setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
    }

    /**
     * to find whether the source is atv or dtv
     * 
     * @param source
     * @return true if the source is atv or dtv,else false
     */
    public static boolean isAtvOrDTV(int source) {
        boolean atvOrDtv = false;
        if (source == TvCommonManager.INPUT_SOURCE_ATV || source == TvCommonManager.INPUT_SOURCE_DTV) {
            atvOrDtv = true;
        }
        return atvOrDtv;
    }

    public static boolean isVGA(int source) {
        return TvCommonManager.INPUT_SOURCE_VGA == source;
    }

    public static void enableMute(boolean mute) {
        AudioManager mTvAudioManager = TvManager.getInstance().getAudioManager();
        if (mTvAudioManager != null) {
            try {
                if (mute) {
                    mTvAudioManager.enableMute(EnumMuteType.E_MUTE_PERMANENT);
                } else {
                    mTvAudioManager.disableMute(EnumMuteType.E_MUTE_PERMANENT);
                }
            } catch (TvCommonException e) {
                LogUtil.e("failed to enableMute " + mute);
            }
        } else {
            LogUtil.e("failed to get audio manager");
        }
    }

    /**
     * Attend into pm sleep mode.
     * 
     * @param bMode True : enable standby init; False : disable standby init.
     * @param bNoSignalPwDn True : for no signal power down; False : for not no
     *            signal power down
     */
    public static void enterSleepMode(boolean bMode, boolean bNoSignalPwDn) {
        TvCommonManager.getInstance().enterSleepMode(bMode, bNoSignalPwDn);
    }

    public static void scaleToFullScreen() throws TvCommonException {
        TvPvrManager pvr = TvPvrManager.getInstance();
        VideoWindowType videoWindowType = new VideoWindowType();
        videoWindowType.height = 0;
        videoWindowType.width = 0;
        videoWindowType.x = 0xFFFF;
        videoWindowType.y = 0xFFFF;
        pvr.setPlaybackWindow(videoWindowType, 0, 0);
    }

    /**
     * @param x {@link VideoWindowType#x}
     * @param y {@link VideoWindowType#y}
     * @param width {@link VideoWindowType#width}
     * @param height {@link VideoWindowType#height}
     * @param calerWidth
     * @param calerHeight
     * @return
     * @throws TvCommonException
     */
    public static boolean setScreenSize(int x, int y, int width, int height, int calerWidth, int calerHeight)
            throws TvCommonException {
        TvPvrManager pvr = TvPvrManager.getInstance();
        VideoWindowType videoWindowType = new VideoWindowType();
        videoWindowType.height = height;
        videoWindowType.width = width;
        videoWindowType.x = x;
        videoWindowType.y = y;
        LogUtil.i("the [x,y][w,h]=" + videoWindowType.x + "," + videoWindowType.y + "][" + videoWindowType.width + ","
                + videoWindowType.height + "]");
        if (videoWindowType.width == 0 || videoWindowType.height == 0) {
            return false;
        }
        pvr.setPlaybackWindow(videoWindowType, calerWidth, calerHeight);
        return true;
    }

    /** handle pvr event and logic **/
    public static class PVRHelper {

        private static TvPvrManager mPvrManager = TvPvrManager.getInstance();

        /**
         * see more {@link TvPvrManager#stopPlayback}
         */
        public static void stopPvrPlayDirect() {
            mPvrManager.stopPlayback();
        }

        /**
         * see more {@link TvPvrManager#deletefile}
         * 
         * @param value
         * @param file
         */
        public static void deletefile(int value, String file) {
            mPvrManager.deletefile(value, file);
        }

        /**
         * see more {@link TvPvrManager#jumpToThumbnail}
         * 
         * @param position
         */
        public static void jumpToThumbnail(int position) {
            mPvrManager.jumpToThumbnail(position);
        }

        /**
         * see more {@link TvPvrManager#getCurPlaybackingFileName}
         * 
         * @return
         */
        public static String getCurPlaybackingFileName() {
            return mPvrManager.getCurPlaybackingFileName();
        }

        /**
         * see more {@link TvPvrManager#getPvrFileNumber}
         * 
         * @return
         */
        public static int getPvrFileNumber() {
            return mPvrManager.getPvrFileNumber();
        }

        /**
         * see more {@link TvPvrManager#getPvrFileInfo}
         * 
         * @param index
         * @return
         */
        public static PvrFileInfo getPvrFileInfo(int index) {
            return mPvrManager.getPvrFileInfo(index, mPvrManager.getMetadataSortKey());
        }

        /**
         * see more {@link TvPvrManager#getFileLcn}
         * 
         * @param index
         * @return
         */
        public static int getFileLcn(int index) {
            return mPvrManager.getFileLcn(index);
        }

        /**
         * see more {@link TvPvrManager#getFileServiceName}
         * 
         * @param file
         * @return
         */
        public static String getFileServiceName(String file) {
            return mPvrManager.getFileServiceName(file);
        }

        /**
         * see more {@link TvPvrManager#getFileEventName}
         * 
         * @param file
         * @return
         */
        public static String getFileEventName(String file) {
            return mPvrManager.getFileEventName(file);
        }

        /**
         * see more {@link TvPvrManager#getCurRecordingFileName}
         * 
         * @return
         */
        public static String getCurRecordingFileName() {
            return mPvrManager.getCurRecordingFileName();
        }

        /**
         * get the selected(focus item) file name. see more
         * {@link TvPvrManager#getPvrFileInfo}
         * 
         * @param index
         * @return
         * @throws TvCommonException
         */
        public static String getSelectedFileName(int index) throws TvCommonException {
            PvrFileInfo fileInfo = new PvrFileInfo();
            fileInfo = mPvrManager.getPvrFileInfo(index, mPvrManager.getMetadataSortKey());
            return fileInfo.filename;
        }

        /**
         * see more {@link TvPvrManager#assignThumbnailFileInfoHandler}
         * 
         * @param fileName
         * @throws TvCommonException
         */
        public static void constructThumbnailList(String fileName) throws TvCommonException {
            if (fileName != null) {
                mPvrManager.assignThumbnailFileInfoHandler(fileName);
            }
        }

        /**
         * get the file name by index. see more
         * {@link TvPvrManager#getPvrFileInfo}
         * 
         * @param index
         * @return
         * @throws TvCommonException
         */
        public static String getFileNameByIndex(int index) throws TvCommonException {
            PvrFileInfo fileInfo = new PvrFileInfo();
            fileInfo = mPvrManager.getPvrFileInfo(index, mPvrManager.getMetadataSortKey());
            return fileInfo.filename;
        }

        /**
         * see more {@link TvPvrManager#getPvrMountPath}
         * 
         * @return
         */
        public static String getPvrMountPath() {
            return mPvrManager.getPvrMountPath();
        }

        /**
         * see more {@link TvPvrManager#clearMetadata}
         */
        public static void clearMetadata() {
            mPvrManager.clearMetadata();
        }

        /**
         * see more {@link TvPvrManager#setPvrParams}
         * 
         * @param path
         * @param arg
         */
        public static void setPvrParams(String path, short arg) {
            mPvrManager.setPvrParams(path, arg);
        }

        /**
         * see more {@link TvPvrManager#createMetadata}
         * 
         * @param path
         */
        public static void createMetadata(String path) {
            mPvrManager.createMetadata(path);
        }

        /**
         * if the PVR is play,stop it
         */
        public static void stopPvrPlay() {
            if (mPvrManager.isPlaybacking()) {
                mPvrManager.stopPlayback();
                mPvrManager.stopPlaybackLoop();
            }
        }

        /**
         * see more {@link TvPvrManager#startPvrPlayback}
         * 
         * @param path
         * @return
         */
        public static int startPvrPlayback(String path) {
            return mPvrManager.startPvrPlayback(path);
        }

        /**
         * see more {@link TvPvrManager#jumpPlaybackTime}
         * 
         * @param time
         */
        public static void jumpPlaybackTime(int time) {
            mPvrManager.jumpPlaybackTime(time);
        }

        /**
         * see more {@link TvPvrManager#getCurPlaybackTimeInSecond}
         * 
         * @return
         */
        public static int getCurPlaybackTimeInSecond() {
            return mPvrManager.getCurPlaybackTimeInSecond();
        }

        /**
         * return pvr total time
         * 
         * @return
         */
        public static int getPvrTotalTime() {
            if (mPvrManager.isRecording()) {
                return mPvrManager.getCurRecordTimeInSecond();
            } else {
                return mPvrManager.getCurPlaybackTimeInSecond();
            }
        }

        /**
         * see more {@link TvPvrManager#getRecordedFileDurationTime}
         * 
         * @param path
         * @return
         */
        public static int getRecordedFileDurationTime(String path) {
            return mPvrManager.getRecordedFileDurationTime(path);
        }

        public static boolean isBootedByRecord() {
            return TvManager.getInstance().getPvrManager().getIsBootByRecord();
        }

        public static void goToStandbySystem() {
            if (isBootedByRecord()) {
                TvManager.getInstance().getPvrManager().setIsBootByRecord(false);
                TvCommonManager.getInstance().standbySystem("standby");
            }
        }

        /**
         * see more {@link TvPvrManager#setMetadataSortAscending}
         * 
         * @param value
         */
        public static void setMetadataSortAscending(boolean value) {
            mPvrManager.setMetadataSortAscending(value);
        }

        /**
         * handle usb remove event when pvr
         */
        public static void handlePvrNotifyUsbRemoved() {
            final TvPvrManager pvr = TvPvrManager.getInstance();
            if (pvr != null) {
                pvr.stopPvr();
                if (pvr.getIsBootByRecord()) {
                    pvr.setIsBootByRecord(false);
                    TvCommonManager.getInstance().standbySystem("pvr");
                }
            }
        }

        /**
         * Set sleep time mode: sleep after certain time.
         * <p>
         * The supported type are
         * <ul>
         * <li> {@link #SLEEP_TIME_OFF}
         * <li> {@link #SLEEP_TIME_10MIN}
         * <li> {@link #SLEEP_TIME_20MIN}
         * <li> {@link #SLEEP_TIME_30MIN}
         * <li> {@link #SLEEP_TIME_60MIN}
         * <li> {@link #SLEEP_TIME_90MIN}
         * <li> {@link #SLEEP_TIME_120MIN}
         * <li> {@link #SLEEP_TIME_180MIN}
         * <li> {@link #SLEEP_TIME_240MIN}
         * </ul>
         * 
         * @param mode sleep time mode
         * @see #SLEEP_TIME_OFF
         * @see #SLEEP_TIME_10MIN
         * @see #SLEEP_TIME_20MIN
         * @see #SLEEP_TIME_30MIN
         * @see #SLEEP_TIME_60MIN
         * @see #SLEEP_TIME_90MIN
         * @see #SLEEP_TIME_120MIN
         * @see #SLEEP_TIME_180MIN
         * @see #SLEEP_TIME_240MIN
         * @return boolean true: success, false: fail
         */
        public static boolean setSleepTimeMode(int mode) {
            return TvTimerManager.getInstance().setSleepTimeMode(mode);
        }

        /**
         * see more {@link TvPvrManager#getSleepTimeMode}
         * 
         * @return
         */
        public static int getSleepTimeMode() {
            return TvTimerManager.getInstance().getSleepTimeMode();
        }

        /**
         * Enalbe auto power off feature. This function will load time set by
         * setOffTimer to enable auto power off feature.
         * 
         * @param bEnable true: enable, false: disable
         * @return boolean true: success, or false: fail
         */
        public static boolean setOffTimerEnable(boolean bEnable) {
            return TvTimerManager.getInstance().setOffTimerEnable(bEnable);
        }

        /**
         * return is pvr Available, see more
         * {@link TvPvrManager#getPvrFileNumber}
         * 
         * @return
         */
        public static boolean isPVRAvailable() {
            boolean isAvailable = false;
            TvPvrManager pvr = TvPvrManager.getInstance();
            if (pvr.getPvrFileNumber() > 0) {
                isAvailable = true;
            }
            LogUtil.i("isAvailable = " + isAvailable);
            return isAvailable;
        }

    }

    /**
     * {@link TvManager.getInstance().getPlayerManager().setDisplay}
     * 
     * @param holder
     */
    public static void setDisplay(SurfaceHolder holder) {
        try {
            TvManager.getInstance().getPlayerManager().setDisplay(holder);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * mute current source
     */
    public static void setVideoMute() {
        try {
            TvManager.getInstance().setVideoMute(false, EnumScreenMuteType.E_BLACK, 0,
                    TvCommonManager.getInstance().getCurrentInputSource());
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * cancle valid epg timer event
     */
    public static void cancelValidEpgTimerEvent() {
        Time currTime = new Time();
        currTime.setToNow();
        currTime.set(currTime.toMillis(true));
        TimerManager timer = TvManager.getInstance().getTimerManager();
        try {
            if (timer != null) {
                timer.cancelEpgTimerEvent((int) ((currTime.toMillis(true) / 1000) + 10 + 3), false);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

}
