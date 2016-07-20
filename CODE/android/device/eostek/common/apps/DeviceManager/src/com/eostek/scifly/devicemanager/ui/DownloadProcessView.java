package com.eostek.scifly.devicemanager.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DownloadProcessView extends View {
	
    private static final String TAG = DownloadProcessView.class.getSimpleName();
    
	private Paint mPaint;

	private Context mContext;
	
	private int progress = 0;
	 
	public DownloadProcessView(Context context) {
		this(context, null);
	}

	public DownloadProcessView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public DownloadProcessView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext=context;
		mPaint = new Paint();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	    int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int rectWidth = getWidth() - paddingLeft - paddingRight;
        int rectHeight = getHeight() - paddingTop - paddingBottom;
        
        mPaint.setAntiAlias(true); 
        mPaint.setStyle(Paint.Style.FILL); 
        
        //up
        mPaint.setColor(Color.parseColor("#00000000"));
        canvas.drawRect(paddingLeft, paddingTop, getWidth() - paddingLeft, rectHeight * progress / 100.0f + paddingTop, mPaint);  
        
        //down
        mPaint.setColor(Color.parseColor("#70000000"));
        canvas.drawRect(paddingLeft, paddingTop + rectHeight * progress / 100.0f, getWidth() - paddingRight, getHeight() - paddingBottom, mPaint);
         
        super.onDraw(canvas);
	}

	public void setProgress(int progress){
		this.progress = progress;
		postInvalidate();
	}
 }  

