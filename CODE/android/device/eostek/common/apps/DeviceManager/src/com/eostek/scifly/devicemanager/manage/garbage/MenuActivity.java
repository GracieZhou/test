
package com.eostek.scifly.devicemanager.manage.garbage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.eostek.scifly.devicemanager.R;

public class MenuActivity extends Activity {
    
    public static final int RESULT_DELETE_ALL = 1;
    public static final int RESULT_SELECT_MORE = 2;
    public static final int RESULT_DELETE = 3;
    public static final int RESULT_SELECT_ALL = 4;
    
    private final int delayTime = 3 * 1000;

    private final int FADEMSG = 1;
    
    private int mMenuMode;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == FADEMSG) {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_bigfile_menu_layout);
        RelativeLayout noCheckboxlRelativeLayout = (RelativeLayout) findViewById(R.id.rl_no_checkbox);
        RelativeLayout haveCheckboxlRelativeLayout = (RelativeLayout) findViewById(R.id.rl_have_checkbox);
        TextView deleleAllView = (TextView) findViewById(R.id.delete_all);
        TextView chooseMoreView = (TextView) findViewById(R.id.select_more);
        TextView deleleView = (TextView) findViewById(R.id.delete);
        TextView chooseAllView = (TextView) findViewById(R.id.select_all);
        
        mMenuMode = getIntent().getIntExtra("request", 0);
        if (mMenuMode == 2) {
        	noCheckboxlRelativeLayout.setVisibility(View.VISIBLE);
        	haveCheckboxlRelativeLayout.setVisibility(View.GONE);
        	
        	deleleAllView.setOnClickListener(new OnClickListener() {
        		
        		@Override
        		public void onClick(View v) {
        			setResult(RESULT_DELETE_ALL);
        			finish();
        			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
        		}
        	});
        	chooseMoreView.setOnClickListener(new OnClickListener() {
        		
        		@Override
        		public void onClick(View arg0) {
        			setResult(RESULT_SELECT_MORE);
        			finish();
        			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
        		}
        	});
        } else if (mMenuMode == 1){
        	noCheckboxlRelativeLayout.setVisibility(View.GONE);
        	haveCheckboxlRelativeLayout.setVisibility(View.VISIBLE);
        	
        	deleleView.setOnClickListener(new OnClickListener() {
        		
        		@Override
        		public void onClick(View v) {
        			setResult(RESULT_DELETE);
        			finish();
        			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
        		}
        	});
        	
        	chooseAllView.setOnClickListener(new OnClickListener() {
        		
        		@Override
        		public void onClick(View arg0) {
        			setResult(RESULT_SELECT_ALL);
        			finish();
        			overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
        		}
        	});
		}
        
        mHandler.sendEmptyMessageDelayed(FADEMSG, delayTime);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mHandler.removeMessages(FADEMSG);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mHandler.sendEmptyMessageDelayed(FADEMSG, delayTime);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
    }
}
