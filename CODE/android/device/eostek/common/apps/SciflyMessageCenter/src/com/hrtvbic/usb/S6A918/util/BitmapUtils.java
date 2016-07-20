
package com.hrtvbic.usb.S6A918.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.util.Log;

public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    public static final int UNCONSTRAINED = -1;

    public static Bitmap ensureGLCompatibleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.getConfig() != null) {
            return bitmap;
        }
        Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, false);
        bitmap.recycle();
        return newBitmap;
    }

    public static Bitmap getImageThumbnail(String path, int width, int height) {
        int outWidth = -1;

        int outHeight = -1;

        int be = 1;

        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        FileInputStream mFileInputStream = null;
        try {
            mFileInputStream = new FileInputStream(path);
            FileDescriptor fd = mFileInputStream.getFD();
            if (fd == null) {
                closeSilently(mFileInputStream);
                // decodeBitmapFailed(R.string.picture_decode_failed);
                return null;
            }
            options.inPurgeable = false;
            options.inInputShareable = true;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd, null, options);

            // options.inSampleSize = computeSampleSizeLarger(options.outWidth,
            // options.outHeight);
            outWidth = options.outWidth;
            outHeight = options.outHeight;

            be = 1;
            if (width == -1 || height == -1) {
                be = 1;
            } else {
                // 计算缩放�?
                float be1 = outWidth / (float) width;
                float be2 = outHeight / (float) height;

                be = (int) ((be1 > be2) ? be2 : be1);
                if (be <= 0) {
                    be = 1;
                }
            }
            options.inSampleSize = be;
            options.inJustDecodeBounds = false;
            options.inPurgeable = false;
            options.inInputShareable = true;
            if (fd != null) {
                int repeat = 6;
                while (repeat > 0) {
                    try {
                        Log.i(TAG, "options.inSampleSize : " + options.inSampleSize);
                        bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
                        Log.i(TAG, "decodeFileDescriptor = " + bitmap);
                        break;
                    } catch (OutOfMemoryError err) {
                        Log.d(TAG, "inSampleSize is too small");
                        if (bitmap != null) {
                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                            bitmap = null;
                        }
                        options.inSampleSize *= 2;
                        repeat--;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // bitmap = BitmapFactory.decodeFile(path, options);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            closeSilently(mFileInputStream);
        } catch (Throwable t) {
            Log.e(TAG, "decode pic failed.");
            return null;
        }
        if (bitmap != null) {
            Log.i(TAG,
                    "path: " + path + ", be: " + be + ", width: " + bitmap.getWidth() + ", height: "
                            + bitmap.getHeight());
            if (be > 1 && outWidth == bitmap.getWidth() && outHeight == bitmap.getHeight()) {
                return getWantedBitmap(bitmap, outWidth / be, outHeight / be);
            }
        }
        return bitmap;
    }

    public static Bitmap decodeFileDescriptor(String path, int maxNumOfPixels) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(path);
        } catch (Exception e) {
            return null;
        }

        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // Does not allocate memory when inJustDecodeBounds is true
        opts.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(fis, null, opts);
        } catch (Exception e) {
            e.printStackTrace();
            closeSilently(fis);
            return null;
        }

        int w = opts.outWidth;
        int h = opts.outHeight;
        Log.i(TAG, "path: " + path + ", width: " + w + ", height: " + h);

        int sampleSize = computeSampleSize(opts, -1, maxNumOfPixels);
        opts.inSampleSize = sampleSize;
        // if(maxNumOfPixels / ((w / sampleSize) * (h / sampleSize)) >= 3)
        // {
        // opts.inSampleSize = sampleSize / 2;
        // }

        opts.inJustDecodeBounds = false;
        // Disable Dithering mode
        opts.inDither = false;
        // opts.inPreferredConfig = Config.RGB_565;
        // Tell to gc that whether it needs free memory, the Bitmap can be
        // cleared. must be false for plug disk
        opts.inPurgeable = false;
        // Which kind of reference will be used to recover the Bitmap data after
        // being clear, when it will be used in the future
        opts.inInputShareable = true;

        try {
            bitmap = BitmapFactory.decodeFileDescriptor(fis.getFD(), null, opts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeSilently(fis);
        }

        if (bitmap != null) {
            // Force scaling when the picture is not scaled force scaling
            if (w == bitmap.getWidth() && opts.inSampleSize > 1) {
                try {
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, w / opts.inSampleSize, h / opts.inSampleSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                Log.i(TAG, "path: " + path + ", width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight() + ", "
                        + opts.inSampleSize);
            }
        }

        return bitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        return initialSize <= 8 ? nextPowerOf2(initialSize) : (initialSize + 7) / 8 * 8;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int w = options.outWidth;
        int h = options.outHeight;

        if (maxNumOfPixels == UNCONSTRAINED && minSideLength == UNCONSTRAINED)
            return 1;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt((double) (w * h)
                / maxNumOfPixels));

        if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            int sampleSize = Math.min(w / minSideLength, h / minSideLength);
            return Math.max(sampleSize, lowerBound);
        }
    }

    public static int nextPowerOf2(int n) {
        if (n <= 0 || n > (1 << 30))
            throw new IllegalArgumentException();
        n -= 1;
        n |= n >> 16;
        n |= n >> 8;
        n |= n >> 4;
        n |= n >> 2;
        n |= n >> 1;
        return n + 1;
    }

    public static Bitmap getVideoThumbnail(String path, int widthWanted, int heightWanted) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        try {
            retriever.setDataSource(path);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
                retriever = null;
            } catch (RuntimeException ex) {
                return null;
            }
        }

        if (widthWanted == -1 || heightWanted == -1) {
            return bitmap;
        }

        return getWantedBitmap(bitmap, widthWanted, heightWanted);
    }

    public static Bitmap getAlbumBitmap(String path, int widthWanted, int heightWanted) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
        } catch (Exception e) {
            retriever.release();
            retriever = null;
            return null;
        }

        byte[] b = retriever.getEmbeddedPicture();
        if (b == null) {
            retriever.release();
            retriever = null;
            return null;
        }

        retriever.release();
        retriever = null;

        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        if (widthWanted == -1 || heightWanted == -1) {
            return bitmap;
        }

        return getWantedBitmap(bitmap, widthWanted, heightWanted);
    }

    public static Bitmap getApkIcon(Context context, String path, int widthWanted, int heightWanted) {
        if (context == null) {
            return null;
        }

        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

        Bitmap b = null;
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            appInfo.publicSourceDir = path;
            BitmapDrawable d = (BitmapDrawable) appInfo.loadIcon(pm);
            Bitmap icon = d.getBitmap();
            b = getWantedBitmap(icon, widthWanted, heightWanted);
        }

        return b;
    }

    public static Bitmap getWantedBitmap(Bitmap bitmap, int widthWanted, int heightWanted) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale;
        int maxSide = width > height ? width : height;
        if (maxSide > (widthWanted * 2 / 3)) {
            scale = Math.min((float) widthWanted / (float) width, (float) heightWanted / (float) height);
        } else {
            scale = 1.0f;
        }

        if (maxSide < heightWanted / 3) {
            Matrix matrix = new Matrix();
            matrix.postScale(2.0f, 2.0f);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }

        int dx = (int) ((widthWanted / scale - width) / 2);
        int dy = (int) ((heightWanted / scale - height) / 2);

        Bitmap b = Bitmap.createBitmap(widthWanted, heightWanted, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawColor(0);
        canvas.drawBitmap(bitmap, new Rect(-dx, -dy, (int) (widthWanted / scale) - dx, (int) (heightWanted / scale)
                - dy), new Rect(0, 0, widthWanted, heightWanted), paint);
        return b;
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        if (bitmap == null) {
            return;
        }

        Log.i(TAG, "saveBitmap path: " + path + ", width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        bitmap.compress(CompressFormat.PNG, 100, out);
        closeSilently(out);
    }

    public static boolean downloadFile(String uri, String path) {
        File file = new File(path);
        if (file.exists()) {
            if (!file.delete()) {
                Log.d(TAG, "file delete failed..." + uri + "  " + path);
                return false;
            }
        }

        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        boolean ret = false;
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            // connect timeout is 10s
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            Log.i(TAG, "responseCode: " + responseCode);
            if (responseCode == HttpStatus.SC_NOT_FOUND) {
                return false;
            }

            is = conn.getInputStream();
            fos = new FileOutputStream(path);

            byte[] buffer = new byte[1024];
            for (int len = 0;;) {
                len = is.read(buffer);
                if (len == -1) {
                    break;
                }

                fos.write(buffer, 0, len);
            }

            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        } finally {
            closeSilently(fos);
            closeSilently(is);
        }

        return ret;
    }

    /**
     * decode net picture.
     */
    public static Bitmap decodeBitmapFromNet(final String imagePath) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            is = new URL(imagePath).openStream();
            if (is == null) {
                Log.i(TAG, "is: " + is);
                return null;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;

            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeStream(is, null, options);
            if (bitmap == null) {
                Log.d(TAG, "BitmapFactory.decodeStream return null");
            }
            is.close();
            return bitmap;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException in decodeBitmap");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException in decodeBitmap");
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            Log.w(TAG, "close fail", t);
        }
    }

    public static Bitmap createReflectedImage(Bitmap originalImage) {
        if (originalImage == null) {
            Log.e(TAG, "createReflectedImage: originalImage is null");
            return null;
        }

        // The gap we want between the reflection and the original image
        final int reflectionGap = 0;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height * 2 / 3, width, height / 3, matrix, true);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 3 + reflectionGap),
                Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);

        // Draw in the gap
        Paint defaultPaint = new Paint();
        defaultPaint.setAntiAlias(true);
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        LinearGradient shader = new LinearGradient(0, height + reflectionGap, 0, (height + height / 3 + reflectionGap),
                0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height + reflectionGap, width, (height + height / 3 + reflectionGap), paint);

        return bitmapWithReflection;
    }
}
