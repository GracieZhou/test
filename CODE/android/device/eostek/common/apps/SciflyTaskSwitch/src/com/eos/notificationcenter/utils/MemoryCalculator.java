
package com.eos.notificationcenter.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

/**
 *  Calculator memory usage.
 */
public class MemoryCalculator {

    long mTotalMemory = 0;

    Context mContext;

    /**
     * Constructor of MemoryCalculator.
     * @param context
     */
    public MemoryCalculator(Context context) {
        mContext = context;
    }

    /** get process. */
    public long getProcess() {
        if (mTotalMemory == 0) {
            mTotalMemory = getMem_TOLAL();
        }
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        MemoryInfo info = new MemoryInfo();
        mActivityManager.getMemoryInfo(info);
        // info.availMem 剩余内存.
//        System.out.println("memory.used :" + info.availMem);
//        System.out.println("memory.total:" + mTotalMemory);
        return 100 - (info.availMem / 1024) * 100 / mTotalMemory;
    }

    // 获得总内存
    private long getMem_TOLAL() {
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (content != null) {
            // beginIndex
            int begin = content.indexOf(':');
            // endIndex
            int end = content.indexOf('k');
            // 截取字符串信息

            content = content.substring(begin + 1, end).trim();
            return Integer.parseInt(content);
        }
        return 0;
        
    }

}
