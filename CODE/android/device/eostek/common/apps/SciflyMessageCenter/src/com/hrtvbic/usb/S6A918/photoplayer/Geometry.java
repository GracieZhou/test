package com.hrtvbic.usb.S6A918.photoplayer;  
  
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
  
//public class Geometry extends Activity {  
//    public int mScreenWidth = 0;  
//    public int mScreenHeight = 0;  
//  
//    @Override  
//    protected void onCreate(Bundle savedInstanceState) {  
//    setContentView(new Geometry(this));  
//    super.onCreate(savedInstanceState);  
//  
//    }  
  
    class Geometry extends View {  
    Paint mPaint = null;  
  
    public int x1;
    public int x2;
    public int y1;
    public int y2;
    public Geometry(Context context,int nx1,int ny1,int nx2 ,int ny2) {  
       super(context);  
       x1 = nx1;
       x2 = nx2;
       y1 = ny1;
       y2 = ny2;
        mPaint = new Paint();  
//        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  // 此处会导致�?出图片播放后进E区程序挂�?
    }  
    @Override  
    
    protected void onDraw(Canvas canvas) {  
       super.onDraw(canvas);  
      
     //设置画布颜色 也就是背景颜�? 
       canvas.drawColor(0x00000000);  
        
//       mPaint.setColor(Color.BLACK);  
//        canvas.drawText("绘制无规则几何图形喔！！�?, 150, 30, mPaint);  
         
      //绘制�?���?
       //if(x1>=0)
       {
        mPaint.setColor(0xff00ffff);  
        mPaint.setStrokeWidth(2);  
        System.out.println( " x1   "+x1+"  y1   "+y1+"   x2  "+x2+"  y2  "+ y2+"\n");
        canvas.drawLine(x1, y1, x2, y1, mPaint);  
        canvas.drawLine(x2, y1, x2, y2, mPaint);  
        canvas.drawLine(x1, y1, x1, y2, mPaint);  
        canvas.drawLine(x1, y2, x2, y2, mPaint);  
       }
        
//      //绘制�?��矩形 
//      mPaint.setColor(Color.YELLOW);  
//        canvas.drawRect(30, 120, 100, 200, mPaint);  
//        
//      //绘制�?��圆形 
//        mPaint.setColor(Color.BLUE);  
//       canvas.drawCircle(80, 300, 50, mPaint);  
//         
//    //绘制�?��椭圆 
//       mPaint.setColor(Color.CYAN);  
//        canvas.drawOval(new RectF(300,370,120,100), mPaint);  
       
      //绘制多边�?  
  
//       mPaint.setColor(Color.BLACK);  
//       Path path = new Path();  
//        path.moveTo(150+5 , 400 -50);  
//        path.lineTo(150+45, 400 - 50);  
//        path.lineTo(150+30, 460 - 50);  
//        path.lineTo(150+20, 460 - 50);  
//        path.close();  
//       canvas.drawPath(path, mPaint);  
          
    }  
    }  
//} 