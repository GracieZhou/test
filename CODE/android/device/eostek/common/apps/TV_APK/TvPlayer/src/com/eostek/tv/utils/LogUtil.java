
package com.eostek.tv.utils;

import java.util.Locale;

import android.util.Log;

/*
 * projectName： AndroidTest
 * moduleName： LogUtil.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-4-23 下午1:57:25
 * @Copyright © 2014 Eos Inc.
 */

public class LogUtil {

    public static String TAG = "LogUtil";

    /**
     * Log.isLoggable()的默认log级别是INFO，像级别低一些的DEBUG和VERBOSE的log就直接被过滤掉了,
     * 常用的5种级别是VERBOSE, DEBUG, INFO, WARN, ERROR。
     *  
     * @see Log.isLoggable
     * 
     * @author chadm
     * 在调试的时候，我们可以通过以下指令来调整log输出级别，如下指令，会将TAG的所有log都打印出来，TAG是我们自己定义的TAG名称 adb
     * root adb shell setprop log.tag.TAG VERBOSE
     **/
    public static boolean DEBUG = Log.isLoggable(TAG, Log.VERBOSE);

    public static void setTag(String tag) {
        d("Changing log tag to %s", tag);
        TAG = tag;

        // Reinitialize the DEBUG "constant"
        DEBUG = Log.isLoggable(TAG, Log.VERBOSE);
    }

    public static void v(String format) {
        if (DEBUG) {
            Log.v(TAG, buildMessage(format));
        }
    }

    public static void v(String format, Object... args) {
        if (DEBUG) {
            Log.v(TAG, buildMessage(format, args));
        }
    }

    public static void d(String format) {
        if (DEBUG) {
            Log.d(TAG, buildMessage(format));
        }
    }

    public static void d(String format, Object... args) {
        if (DEBUG) {
            Log.d(TAG, buildMessage(format, args));
        }
    }

    public static void i(String format) {
        Log.i(TAG, buildMessage(format));
    }

    public static void i(String format, Object... args) {
        Log.i(TAG, buildMessage(format, args));
    }

    public static void w(String format) {
        Log.w(TAG, buildMessage(format));
    }

    public static void w(String format, Object... args) {
        Log.w(TAG, buildMessage(format, args));
    }

    public static void e(String format) {
        Log.e(TAG, buildMessage(format));
    }

    public static void e(String format, Object... args) {
        Log.e(TAG, buildMessage(format, args));
    }

    /**
     * Formats the caller's provided message and prepends useful info like
     * calling thread ID and method name.
     * 
     * @param format
     * @param args can be empty
     * @return
     */
    private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        // Walk up the stack looking for the first caller outside of VolleyLog.
        // It will be at least two frames up, so start there.
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        // caller contains the class name and method name
        return String.format(Locale.US, "%s: %s", caller, msg);
    }

}
