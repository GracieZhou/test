package com.eostek.documentui.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ProgressView extends View {
	
	private Paint mPaint;
	
	private int width,height;
	
	private Context context;
	
	private int progress=0;
	
	public ProgressView(Context context)
	{
		this(context, null);
	}

	public ProgressView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;
		mPaint=new Paint();
	}
	

    @SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        mPaint.setAntiAlias(true); 
        mPaint.setStyle(Paint.Style.FILL); 
        
        mPaint.setColor(Color.parseColor("#70000000"));
        canvas.drawRect(0, getHeight()*progress/100.0f, getWidth(), getHeight(), mPaint);
        Log.e("rick1", getHeight()+"");
        Log.e("rick1", progress+"");
        mPaint.setColor(Color.parseColor("#00000000"));
        canvas.drawRect(getHeight(), 0, 0,getHeight()*progress/100.0f, mPaint);
        Log.e("rick1", getWidth()+"");
        mPaint.setTextSize(30);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
		mPaint.setStrokeWidth(2);
		Rect rect=new Rect();
		mPaint.getTextBounds("100%", 0, "100%".length(), rect);
		
		int textWidth = getTextWidth(mPaint, "100%");
		//canvas.drawText(progress+"%", getWidth()/2.0f-textWidth/2.0f,getHeight()/2.0f, mPaint);
        
	}
	
	public void setProgress(int progress){
		this.progress=progress;
		postInvalidate();
	};
	
	//获取text精确宽度
	public static int getTextWidth(Paint paint, String str) {  
        int iRet = 0;  
        if (str != null && str.length() > 0) {  
            int len = str.length();  
            float[] widths = new float[len];  
            paint.getTextWidths(str, widths);  
            for (int j = 0; j < len; j++) {  
                iRet += (int) Math.ceil(widths[j]);  
            }  
        }  
        return iRet;  
    }  
 }  

