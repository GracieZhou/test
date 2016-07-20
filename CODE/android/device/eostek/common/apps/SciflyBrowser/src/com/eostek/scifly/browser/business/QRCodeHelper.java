
package com.eostek.scifly.browser.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import scifly.device.Device;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;

import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.util.Constants;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * projectName： Browser moduleName： QRCodeHelper.java
 * 
 * @author Shirley.jiang
 * @time 2016-1-27 
 */
public class QRCodeHelper {

    private final String TAG = "QRCodeHelper";

    private BrowserActivity mActivity;

    private static volatile QRCodeHelper mInstance;
    
    private QRCodeHelper(BrowserActivity activity) {
        mActivity = activity;
    }

    /**
     * get QRCodeHelper instance.
     * @param activity
     * @return
     */
    public static QRCodeHelper getInstance(BrowserActivity activity) {
        if (mInstance == null) {
            synchronized (QRCodeHelper.class) {
                if (mInstance == null) {
                    mInstance = new QRCodeHelper(activity);
                }
            }
        }
        return mInstance;
    }

    /**
     * get QRCode Bitmap.
     * @return
     */
    public Bitmap getQRCodeBitmap() {
        try {
            String url = "";
            if ("heran".equals(Build.DEVICE)) {
                url = "http://219.87.156.92:5555/TV/hertec/HerTec2.apk?bbno=" + Device.getBb();
            } else {
                url = "http://app.qq.com/#id=detail&appid=1104916236";
                if (!TextUtils.isEmpty(Device.getBb())) {
                    url = url + "&bbno=" + Device.getBb();
                }
            }
            Log.d(TAG, "url=" + url);

            Bitmap bitmap1 = encodeAsBitmap(url, BarcodeFormat.QR_CODE, 150, 150);
            if (bitmap1 != null) {
                Display currDisplay = mActivity.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                currDisplay.getSize(point);
                int width = point.x;
                int height = point.y;
                Matrix matrix = new Matrix();
                float scale = (width / 7) / bitmap1.getWidth();
                matrix.postScale(scale, scale); // 长和宽放大缩小的比例
                Bitmap resizeBmp = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix,
                        true);

                Context context = mActivity;
                if (resizeBmp != null) {
                    // save bitmap to local.
                    saveBitmap(resizeBmp);
                }
                return resizeBmp;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight)
            throws WriterException {
        final int WHITE = 0xFFFFFFFF; // 可以指定其他颜色，让二维码变成彩色效果
        final int BLACK = 0xFF000000;
        HashMap<EncodeHintType, String> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new HashMap<EncodeHintType, String>(2);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(contents, format, desiredWidth, desiredHeight, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private void saveBitmap (Bitmap bitmap) {
        File file = new File(Constants.CACHE_PATH, Constants.QR_CODE_NAME);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
