package com.hrtvbic.usb.S6A918.photoplayer;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;

import com.eostek.scifly.messagecenter.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//import com.mstar.android.MDisplay;
//import com.mstar.android.MDisplay.PanelMode;

/**
 * @date 2012-4-1 支持3D和正常模式显示的图片控件
 */
public class ImageSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = "ImageSurfaceView";

	private Bitmap bitmap;

	private Context context;
	
	private FileInputStream fileInputStream = null;

	private boolean hasEnter4K2K;

	public boolean isCreate = false;

	public ImageSurfaceView(Context context) {
		super(context);
		this.context = context;
		getHolder().addCallback(this);
	}

	public ImageSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
		getHolder().addCallback(this);
	}

	public ImageSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(TAG, "surfaceChanged" + " width: " + width + ", height: "
				+ height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// set3D();
		Log.i(TAG, "surfaceCreated" + " isCreate: " + isCreate);
		isCreate = true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		if (bitmap != null) {
			bitmap.recycle();
		}
	}
	
	
	 // 图片读取和加载start.
    protected void drawImage() {
        Log.i(TAG,"============drawImage===XUNING============");
		if (PhotoPlayerActivity.support4K2K) {
			Log.i(TAG, "hasEnter4K2K: " + hasEnter4K2K);
			if (hasEnter4K2K) {

			} else {
				hasEnter4K2K = true;
				// TvManager.getInstance().enter4K2KMode(EN_4K2K_MODE.E_4K2K_15HZ_MODE);
				//MDisplay.setPanelMode(PanelMode.E_PANELMODE_4K2K_15HZ);
				Surface surface = getHolder().getSurface();

				String surfaceString = surface.toString();
				int firstPos = surfaceString.indexOf("identity=") + 9;
				int lastPos = surfaceString.lastIndexOf(")");
				String identityString = surfaceString.substring(firstPos,
						lastPos);
				int identityInt = Integer.parseInt(identityString);
				Log.d(TAG, "surface.toString() = " + surfaceString + ";firstpos = "
						+ firstPos + ";lastpos = " + lastPos + ";identitystring = "
						+ identityString + ";identityint = " + identityInt);
				//MDisplay.setBypassTransformMode(identityInt, 1);
			}
		}
        
        Canvas canvas = getHolder().lockCanvas(null);
        Log.i(TAG,"#####======canvas is :"+canvas+"======");
        if (bitmap != null && canvas != null) {
        	canvas.drawColor(Color.BLACK);
            Paint paint = new Paint();
            int bitmapHeight = bitmap.getHeight();
            int bitmapWidth = bitmap.getWidth();
            int surfaceHeight = this.getHeight();
            int surfaceWidth = this.getWidth();
            android.graphics.Rect src = new android.graphics.Rect();
            android.graphics.Rect dst = new android.graphics.Rect();
            src.left = 0;
            src.top = 0;
            src.bottom = bitmapHeight;
            src.right = bitmapWidth;
            Log.i(TAG,"$$$$____xuning______surfaceHeight  is :"+surfaceHeight);
            Log.i(TAG,"$$$$____xuning______surfaceWidth  is :"+surfaceWidth);
            Log.i(TAG,"$$$$____xuning______src.bottom  is :"+src.bottom);
            Log.i(TAG,"$$$$____xuning______src.right  is :"+src.right);
            if (PhotoPlayerActivity.support4K2K) {
				bitmapHeight = bitmapHeight * 2;
				bitmapWidth = bitmapWidth * 2;
				// Log.i(TAG, "bitmapHeight: " + bitmapHeight +
				// ", bitmapWidth: " + bitmapWidth);
				if (bitmapHeight > (surfaceHeight / 2)) {
					float rate = ((float) bitmapWidth)
							/ ((float) bitmapHeight);
					// Log.i(TAG, "rate: " + rate);
					bitmapHeight = surfaceHeight;
					bitmapWidth = (int) (bitmapHeight * rate);
				}

				dst.left = (surfaceWidth - bitmapWidth) / 2;
				dst.top = (surfaceHeight - bitmapHeight) / 2;
				// dst.right = (surfaceWidth + bitmapWidth) / 2;
				// dst.bottom = (surfaceHeight + bitmapHeight) / 2;
				dst.bottom = bitmapHeight + dst.top;
				dst.right = bitmapWidth + dst.left;
			} else {
				dst.left = 0;
				dst.top = 0;
				dst.bottom = surfaceHeight;
				dst.right = surfaceWidth;
			}
            
            Log.i(TAG,"$$$$____xuninglong___ss___dst.left  is :"+dst.left);
            Log.i(TAG,"$$$$____xuninglong__ss____dst.top  is :"+dst.top);
            Log.i(TAG,"$$$$____xuninglong__ss____dst.bottom  is :"+dst.bottom);
            Log.i(TAG,"$$$$____xuninglong__ss____dst.right  is :"+dst.right);
            Log.i(TAG,"============Stephen bbbb================");
            canvas.drawBitmap(bitmap, src, dst, paint);
            Log.i(TAG,"============Stephen xxzzzooo================");
        }

        if (canvas != null) {
            getHolder().unlockCanvasAndPost(canvas);
            if (bitmap != null){
            	 bitmap.recycle();
            }
        }
    }
	


	protected void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	protected boolean updateView() {
		if (this.bitmap != null) {
			invalidate();
			return true;
		}

		return false;
	}
	
	protected boolean setImagePath(String imagePath, int width, int height) {
	      bitmap = decodeBitmap(imagePath, width, height);
	      
	      if (bitmap != null) {

	              invalidate();
	            return true;
	        } else {
	        	  bitmap = setDefaultPhoto();
	        	  invalidate();
	            return false;
	        }
	    }

	    protected Bitmap decodeBitmap(String imagePath,int width, int height) {
	        File file = new File(imagePath);
	        Log.d(TAG, "size of photo : " + file.length());
	        if (!file.exists()) {
	            return null;
	        }
	        
	        // 4k2k mode 时，大于100M的图片强制不让其解析
	        if(file.length() / 1024 / 1024 > 100) {
	              return null;
	        }

	        Bitmap bitmap = null;
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        try {
	            closeSilently(fileInputStream);
	            fileInputStream = new FileInputStream(imagePath);
	            FileDescriptor fd = fileInputStream.getFD();
	            if (fd == null) {
	                closeSilently(fileInputStream);
	                return null;
	                
	            }
	            // 插拔磁盘，下面这个必须设置为false.
	            options.inPurgeable = false;
	            options.inInputShareable = true;
	            options.inJustDecodeBounds = true;
	            BitmapFactory.decodeFileDescriptor(fd, null, options);
	            Log.i("*************", "options " + options.outHeight + " " + options.outWidth);
	            if (!checkImageSize(options)) {
	                closeSilently(fileInputStream);

	                return null;
	            }
	            
	            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	            options.inSampleSize = 1;
	           
	            options.inJustDecodeBounds = false;
	            bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);

	            bitmap = resizeDownIfTooBig(bitmap, 3840, true);

	        } catch (Exception e) {
	            try {
	                closeSilently(fileInputStream);
	                fileInputStream = new FileInputStream(imagePath);
	                bitmap = BitmapFactory.decodeStream(fileInputStream);
	            } catch (Exception error) {
	                error.printStackTrace();

	                return null;
	            } finally {
	                closeSilently(fileInputStream);
	            }
	        } finally {
	            closeSilently(fileInputStream);
	        }

	        return ensureGLCompatibleBitmap(bitmap);
	    }

	    private void closeSilently(Closeable c) {
	        if (c == null) {
	            return;
	        }

	        try {
	            c.close();
	        } catch (Throwable t) {
	        }
	    }

	    // This function should not be called directly from
	    // DecodeUtils.requestDecode(...), since we don't have the knowledge
	    // if the bitmap will be uploaded to GL.
	    private static Bitmap ensureGLCompatibleBitmap(Bitmap bitmap) {
	        if (bitmap == null || bitmap.getConfig() != null)
	            return bitmap;

	        Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, false);
	        bitmap.recycle();

	        return newBitmap;
	    }

	    // Resize the bitmap if each side is >= targetSize * 2
	    private Bitmap resizeDownIfTooBig(Bitmap bitmap, int targetSize, boolean recycle) {
	        int srcWidth = bitmap.getWidth();
	        int srcHeight = bitmap.getHeight();
	        float scale = Math.max((float)targetSize / srcWidth, (float)targetSize / srcHeight);
	        if (scale > 0.5f) {
	            return bitmap;
	        }

	        return resizeBitmapByScale(bitmap, scale, recycle);
	    }

	    private Bitmap resizeBitmapByScale(Bitmap bitmap, float scale, boolean recycle) {
	        int width = Math.round(bitmap.getWidth() * scale);
	        int height = Math.round(bitmap.getHeight() * scale);
	        if (width == bitmap.getWidth() && height == bitmap.getHeight()) {
	            return bitmap;
	        }

	        Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
	        Canvas canvas = new Canvas(target);
	        canvas.scale(scale, scale);
	        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
	        canvas.drawBitmap(bitmap, 0, 0, paint);
	        if (recycle) {
	            bitmap.recycle();
	        }

	        return target;
	    }

	    private static Bitmap.Config getConfig(Bitmap bitmap) {
	        Bitmap.Config config = bitmap.getConfig();
	        if (config == null) {
	            config = Bitmap.Config.ARGB_8888;
	        }

	        return config;
	    }

	    private boolean checkImageSize(BitmapFactory.Options options) {

	        long limit = 100000000;// �?���?00MB内存
	        
	        long pixSize = options.outWidth * options.outHeight * 4;
	        Log.i(TAG, "消耗内存————————"+pixSize/1024/1024+" M");
	        if (pixSize <= limit) {
	            return true;
	        }

	        return false;
	    }
	   
	    private Bitmap setDefaultPhoto() {
	        
	        BitmapFactory.Options opt = new BitmapFactory.Options();
	        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

	        opt.inPurgeable = true;
	        opt.inInputShareable = true;
	        // 获取资源图片
	        InputStream is = this.getResources().openRawResource(R.drawable.type_photo);
	        if (is == null) {
	            return null;
	        }

	        return ensureGLCompatibleBitmap(BitmapFactory.decodeStream(is, null, opt));
	    }
}
