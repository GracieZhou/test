package com.heran.launcher2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;


public class Tools {

    private final static int DEFAULT_BUFFER_SIZE = 1024;

    private static final boolean LOG = true;

    private static final String MSTAR_PRODUCT_CHARACTERISTICS = "mstar.product.characteristics";
    private static final String MSTAR_PRODUCT_STB = "stb";
    private static String mProduct = null;
    
    public static boolean isBox() {

        if (mProduct == null) {

            Class<?> systemProperties = null;

            Method method = null;

            try {

                systemProperties = Class.forName("android.os.SystemProperties");

                method = systemProperties.getMethod("get", String.class, String.class);

                mProduct = (String) method.invoke(null, MSTAR_PRODUCT_CHARACTERISTICS, "");

            } catch (Exception e) {

                return false;

            }

        }

        // Log.d("Tools", "mstar.product.characteristics is " + mProduct);

        if (MSTAR_PRODUCT_STB.equals(mProduct)) {

            return true;

        } else {

            return false;

        }

    }
    
    
    public static String sizeToM(long size) {
        if (size / 1024 / 1024 >= 1) {
            return size / 1024 / 1024 + (size / 1024 % 1024 / 1024.0 + "").substring(1, 3) + "MB";
        } else {
            return size / 1024 + (size % 1024 / 1024.0 + "").substring(1, 3) + "KB";
        }
    }

    public static void intentForward(Context context, Class<?> forwardClass) {
        Intent intent = new Intent();
        intent.setClass(context, forwardClass);
        context.startActivity(intent);
    }

    public static boolean string2File(String res, String filePath) {
        boolean flag = true;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists())
                distFile.getParentFile().mkdirs();
            bufferedReader = new BufferedReader(new StringReader(res));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024];
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
            return flag;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static String file2String(File file, String encoding) {
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            if (encoding == null || "".equals(encoding.trim())) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                reader = new InputStreamReader(new FileInputStream(file));
            }
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return writer.toString();
    }

    public static String getSystemVersion() {
        return Build.VERSION.INCREMENTAL.substring(0);
    }

    public static boolean matchIP(String ip) {
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static String[] resolutionIP(String ip) {
        return ip.split("\\.");
    }

    public static Bitmap byte2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        System.out.println("mi.availMem;" + mi.availMem);

        return Formatter.formatFileSize(context, mi.availMem);
    }

    public static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);
    }

    public static boolean isMonkeyTesting() {
        return ActivityManager.isUserAMonkey();
    }

    // comment : add proxy
    public static int validate(final String hostname, final String port) {
        // check
        if (TextUtils.isEmpty(hostname)) {
            return -1;
        }
        if (TextUtils.isEmpty(port)) {
            return -1;
        }

        final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$";
        final Pattern HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
        Matcher match = HOSTNAME_PATTERN.matcher(hostname);
        if (!match.matches())
            return -1;

        if (hostname.length() > 0 && port.length() == 0) {
            return -2;
        }

        if (port.length() > 0) {
            if (hostname.length() == 0) {
                return -1;
            }
            int portVal = -1;
            try {
                portVal = Integer.parseInt(port);
            } catch (NumberFormatException ex) {
                return -2;
            }
            if (portVal <= 0 || portVal > 0xFFFF) {
                return -2;
            }
        }

        return 0;
    }

    public static void logd(final String tag, final String msg) {
        if (LOG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            Log.d(tag, msg + " @method : " + traceElement.getMethodName() + " line : "
                    + traceElement.getLineNumber());
        }
    }

    public static String buildUpNetmask(final String ip, final String gateway) {
        if (ip == null || gateway == null) {
            return "255.255.0.0";
        }

        StringBuffer netmask = new StringBuffer();
        String[] ipArray = ip.split("\\.");
        String[] gatewayArray = gateway.split("\\.");
        if (ipArray[0].equals(gatewayArray[0])) {
            netmask.append("255");
        } else {
            netmask.append("0");
        }
        if (ipArray[1].equals(gatewayArray[1])) {
            netmask.append(".255");
        } else {
            netmask.append(".0");
        }
        if (ipArray[2].equals(gatewayArray[2])) {
            netmask.append(".255");
        } else {
            netmask.append(".0");
        }
        if (ipArray[3].equals(gatewayArray[3])) {
            netmask.append(".255");
        } else {
            netmask.append(".0");
        }

        return netmask.toString();
    }
}
