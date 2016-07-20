
package com.eostek.tv.player.pvr;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mstar.android.tv.TvPvrManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

public abstract class PVRThumbnail extends Gallery {

    private TvPvrManager pvr = null;

    private Context mContext = null;

    private int totalNumber = 0;

    private boolean isShown = false;

    private ThumbnailAdapter adapter = null;

    private ArrayList<ImageView> images = null;

    public PVRThumbnail(Context context, TvPvrManager pvrManager) {
        super(context);
        mContext = context;
        pvr = pvrManager;
        try {
            constructImages();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        adapter = new ThumbnailAdapter();
        this.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                onItemClicked(position);
            }
        });
        this.setAdapter(adapter);
        // this.setUnselectedAlpha((float) 1.0);
    }

    //
    public class ThumbnailAdapter extends BaseAdapter {

        public ThumbnailAdapter() {
        }

        @Override
        public int getCount() {
            return totalNumber;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return images.get(position);
        }
    }

    private void constructImages() throws TvCommonException {
        images = new ArrayList<ImageView>();
        images.clear();
        // int total = totalNumber = pvr.getThumbnailNumber();
        int total = totalNumber = 0; // Currently we don't support the PVR
                                     // Thumbnail function.
        for (int i = 0; i < total; i++) {
            ImageView image = createThumbnailImage(pvr.getThumbnailPath(i),
                    pvr.getThumbnailDisplay(i));
            if (image == null) {
                totalNumber--;
            } else {
                images.add(createThumbnailImage(pvr.getThumbnailPath(i), pvr.getThumbnailDisplay(i)));
            }
        }
    }

    @SuppressWarnings("deprecation")
    private ImageView createThumbnailImage(String path, String info) {
        if (path == null || info == null) {
            return null;
        }
        Button image = new Button(mContext);
        Bitmap bitmap = getBitmapfromPath(path);
        if (bitmap == null)
            return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = dip2px(300);
        int newHeight = dip2px(150);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
        image.setBackgroundDrawable(bmd);
        image.setText(info);
        image.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        image.setShadowLayer(2, 0, 0, Color.rgb(00, 00, 00));
        image.setDrawingCacheEnabled(true);
        image.measure(MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY));
        image.layout(0, 0, image.getMeasuredWidth(), image.getMeasuredHeight());
        image.buildDrawingCache();

        ImageView imageView = new ImageButton(mContext);
        imageView.setImageBitmap(image.getDrawingCache());
        return imageView;
    }

    private Bitmap getBitmapfromPath(String path) {
        byte[] data = null;
        Log.e("PVRTHUM", "==============>>>>> getBitmapfromPath = " + path);
        try {
            FileInputStream fin = new FileInputStream(path);
            int length = fin.available();
            Log.e("PVRTHUM", "==============>>>>> data length = " + length);
            data = new byte[length];
            fin.read(data);
            fin.close();
        } catch (Exception e) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        Bitmap bitmap = Bitmap.createBitmap(192, 108, Bitmap.Config.RGB_565);
        bitmap.copyPixelsFromBuffer(buffer);
        buffer.clear();
        return bitmap;
    }

    public boolean addThumbnail(int index) {
        totalNumber = pvr.getThumbnailNumber();
        images.add(index,
                createThumbnailImage(pvr.getThumbnailPath(index), pvr.getThumbnailDisplay(index)));
        adapter.notifyDataSetChanged();
        return true;
    }

    public boolean updateThumbnail() {
        try {
            constructImages();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        return true;
    }

    public boolean isShown() {
        return isShown;
    }

    public void Show(boolean show) {
        if (show) {
            this.setVisibility(View.VISIBLE);
            requestFocus();
        } else {
            this.setVisibility(View.INVISIBLE);
        }
        isShown = show;
    }

    private Bitmap createTxtImage(String txt, int txtSize) {
        Bitmap mbmpTest = Bitmap.createBitmap(txt.length() * txtSize + 4, txtSize + 4,
                Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(mbmpTest);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        p.setTextSize(txtSize);
        canvasTemp.drawText(txt, 2, txtSize - 2, p);
        return mbmpTest;
    }

    private Bitmap createReflectedImage(Bitmap originalImage) {
        // The gap we want between the reflection and the original image
        final int reflectionGap = 0;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width,
                height / 2, matrix, false);
        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2),
                Config.ARGB_8888);
        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // Draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    private int dip2px(float dipValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    abstract void onItemClicked(int position);
}
