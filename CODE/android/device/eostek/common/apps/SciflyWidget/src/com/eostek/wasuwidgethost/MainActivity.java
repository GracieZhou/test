
package com.eostek.wasuwidgethost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;

import com.eostek.scifly.widget.R;

/**
 * projectName：WasuWidgetHost.
 * moduleName： MainActivity.java
 *
 * @author vicky.wang
 * @version 1.0.0
 * @time  2014-8-14 4:30 pm
 * @Copyright © 2014 Eos Inc.
 *
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                startWasuApp();
                return false;
            case KeyEvent.KEYCODE_1:
                startWasuweb();
                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * start the wasu app.
     */
    @SuppressLint("InlinedApi")
    public final void startWasuApp() {
        String msg = "{cTitle:'约会专家',cType:'2',nodeId:'',pic:'http://html5-epg.wasu.tv//_CMS_NEWS_IMG_/www224/2014-08/06/1407264163372_794099.jpg',linkUrl: 'http://html5-epg.wasu.tv/android_tv2.0_auth/widget.jsp?widgetUrl=aHR0cDovL2h0bWw1LWVwZy53YXN1LnR2L2FuZHJvaWRfbHVuY2hlci9kZXRhaWxzLmFjdGlvbj9wYXJhbT1hSFIwY0RvdkwyaDBiV3cxTFdWd1p5NTNZWE4xTG5SMkwzTmxZWEpqYUM5elpYSjJiR1YwTDBwVFQwNUVZWFJoUDJGemMyVjBTV1E5TVRBMk9EQTVNU1owZVhCbFBYTmxiR1ZwYm5GMWFYSjVKbVp2Y214a1EyOWtaVDB4TmpVek9BPT0mYmFja1VybD1odHRwJTNBJTJGJTJGaHRtbDUtZXBnLndhc3UudHYlMkZhbmRyb2lkX2x1bmNoZXIlMkZNb3ZpZS5hY3Rpb24='}";
        String webUrl = "http://html5-epg.wasu.tv/android_tv2.0_auth/widget.jsp?widgetUrl=aHR0cDovL2h0bWw1LWVwZy53YXN1LnR2L2FuZHJvaWRfbHVuY2hlci9kZXRhaWxzLmFjdGlvbj9wYXJhbT1hSFIwY0RvdkwyaDBiV3cxTFdWd1p5NTNZWE4xTG5SMkwzTmxZWEpqYUM5elpYSjJiR1YwTDBwVFQwNUVZWFJoUDJGemMyVjBTV1E5TVRBMk9EQTVNU1owZVhCbFBYTmxiR1ZwYm5GMWFYSjVKbVp2Y214a1EyOWtaVDB4TmpVek9BPT0mYmFja1VybD1odHRwJTNBJTJGJTJGaHRtbDUtZXBnLndhc3UudHYlMkZhbmRyb2lkX2x1bmNoZXIlMkZNb3ZpZS5hY3Rpb24=";
        Intent intent = new Intent();
        // 设置intent Action的属性
        intent.setAction("com.sihuatech.broadcast_WEB_URL");
        intent.putExtra("WEB_URL", webUrl); // url指推荐位中的地址
        intent.putExtra("packageName", "cn.com.wasu.main");
        intent.putExtra("startActivity", "WelcomeActivity");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // 发出广播
        sendBroadcast(intent);
        Log.d("MainActivity", "startWasuApp");

    }

    /**
     * send broadcast of web.
     */
    public final void startWasuweb() {
        Intent intent = new Intent();
        // set the attribution of intent Action 
        intent.setAction("com.sihuatech.broadcast_WEB_URL");
        intent.putExtra("WEB_URL", "http://html5-epg.wasu.tv/movie.shtml"); // url指推荐位中的地址
        intent.putExtra("packageName", "cn.com.wasu.main");
        intent.putExtra("startActivity", "WelcomeActivity");
        sendBroadcast(intent);
        Log.d("MainActivity", "startWasuApp");
    }
}
