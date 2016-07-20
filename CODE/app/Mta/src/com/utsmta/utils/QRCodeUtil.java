package com.utsmta.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

	
/**
 * 浜岀淮鐮佺敓鎴愬伐鍏风被
 */
public class QRCodeUtil {
    public static boolean createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm, String filePath) {
        try {
            if (content == null || "".equals(content)) {
                return false;
            }

            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            if (logoBm != null) {
//                bitmap = addLogo(bitmap, logoBm);
            }
            
            File file = new File(filePath.substring(0, filePath.lastIndexOf('/')));
            if(!file.exists()) file.mkdirs();
            return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
		}

        return false;
    }
    
    public static Bitmap createQRCodeBitmap(String content, int widthPix, int heightPix){
    	Bitmap bitmap = null;
        try {
            if (content == null || "".equals(content)) {
                return null;
            }

            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 鍦ㄤ簩缁寸爜涓棿娣诲姞Logo鍥炬
     */
//    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
//        if (src == null) {
//            return null;
//        }
//
//        if (logo == null) {
//            return src;
//        }
//
//        //鑾峰彇鍥剧墖鐨勫楂�        int srcWidth = src.getWidth();
//        int srcHeight = src.getHeight();
//        int logoWidth = logo.getWidth();
//        int logoHeight = logo.getHeight();
//
//        if (srcWidth == 0 || srcHeight == 0) {
//            return null;
//        }
//
//        if (logoWidth == 0 || logoHeight == 0) {
//            return src;
//        }
//
//        //logo澶у皬涓轰簩缁寸爜鏁翠綋澶у皬鐨�/4
//        float scaleFactor = srcWidth * 1.0f / 4 / logoWidth;
//        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
//        try {
//            Canvas canvas = new Canvas(bitmap);
//            //鐢荤瑪璁剧疆淇濊瘉鍥剧墖鎶楅敮榻�            Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setFilterBitmap(true);
//            
//            canvas.drawBitmap(src, 0, 0, paint);
//            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
//            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, paint);
//
//            canvas.save(Canvas.ALL_SAVE_FLAG);
//            canvas.restore();
//        } catch (Exception e) {
//            bitmap = null;
//            e.getStackTrace();
//        }
//
//        return bitmap;
//    }

}