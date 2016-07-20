
package com.eostek.documentui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.TextView;

import com.eostek.documentui.Constants;
import com.eostek.documentui.R;

public class MenuActivity extends Activity implements OnClickListener, OnFocusChangeListener {
    private final int delayTime = 3 * 1000;

    private final int FADEMSG = 1;

    private int flag = 0;

    private TextView deleleView;

    private TextView mutil_choose;

    private TextView detail;

    private TextView mutil_delete;

    private TextView mutil_delete_cancle;

    private TextView mutil_delete_ok;

    private TextView downloading_delete;

    private TextView downloading_start_all;

    private TextView downloading_pause_all;

    private View focus_delete;

    private View focus_allstart;

    private View focus_allpause;

    private View focus_mutil_delete;

    private View focus_mutil_cancle;

    private View focus_mutil_ok;

    private View focus_single_delete;

    private View focus_single_mutil_choose;

    private View focus_detail;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == FADEMSG) {
                setResult(Constants.NOTHINGFINISH);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String menuMode = (String) getIntent().getExtras().get("menuMode");
        /**
         * chose different mode menu layout
         */
        if (menuMode.equals("isSingleDeleteMode")) {
            // isSingleDeleteMode
            setContentView(R.layout.activity_menu_layout);
            flag = 1;
        }
        if (menuMode.equals("isMutilDeleteMode")) {
            // isMutilDeleteMode
            setContentView(R.layout.activity_mutil_menu_layout);
            flag = 2;
        }
        if (menuMode.equals("isDownloadingMode")) {
            // isDownloadingMode
            setContentView(R.layout.activity_menu_layout_new);
            flag = 3;
        }
        findViews();
        initListener();

        mHandler.sendEmptyMessageDelayed(FADEMSG, delayTime);
    }

    private void findViews() {
        // isSingleDeleteMode
        deleleView = (TextView) findViewById(R.id.delete);
        mutil_choose = (TextView) findViewById(R.id.mutil_choose);
        detail = (TextView) findViewById(R.id.detail);
        focus_single_delete = findViewById(R.id.single_foucus1);
        focus_single_mutil_choose = findViewById(R.id.single_foucus2);
        focus_detail = findViewById(R.id.single_foucus3);

        // isMutilDeleteMode
        mutil_delete = (TextView) findViewById(R.id.mutil_delete);
        mutil_delete_cancle = (TextView) findViewById(R.id.mutil_delete_cancle);
        mutil_delete_ok = (TextView) findViewById(R.id.mutil_delete_ok);
        focus_mutil_delete = findViewById(R.id.mutil_foucus1);
        focus_mutil_cancle = findViewById(R.id.mutil_foucus2);
        focus_mutil_ok = findViewById(R.id.mutil_foucus3);

        // isDownloadingMode
        downloading_delete = (TextView) findViewById(R.id.downloading_delete);
        downloading_start_all = (TextView) findViewById(R.id.all_start);
        downloading_pause_all = (TextView) findViewById(R.id.all_pause);
        focus_delete = findViewById(R.id.foucus1);
        focus_allstart = findViewById(R.id.foucus2);
        focus_allpause = findViewById(R.id.foucus3);

    }

    private void initListener() {
        if (flag == 1) {
            // isSingleDeleteMode
            deleleView.setOnClickListener(this);
            mutil_choose.setOnClickListener(this);
            detail.setOnClickListener(this);

            deleleView.setOnFocusChangeListener(this);
            mutil_choose.setOnFocusChangeListener(this);
            detail.setOnFocusChangeListener(this);
            flag = 0;
        }
        if (flag == 2) {
            // isMutilDeleteMode
            mutil_delete.setOnClickListener(this);
            mutil_delete_cancle.setOnClickListener(this);
            mutil_delete_ok.setOnClickListener(this);

            mutil_delete.setOnFocusChangeListener(this);
            mutil_delete_cancle.setOnFocusChangeListener(this);
            mutil_delete_ok.setOnFocusChangeListener(this);
            flag = 0;
        }
        if (flag == 3) {
            // isDownloadingMode
            downloading_delete.setOnClickListener(this);
            downloading_start_all.setOnClickListener(this);
            downloading_pause_all.setOnClickListener(this);

            downloading_delete.setOnFocusChangeListener(this);
            downloading_start_all.setOnFocusChangeListener(this);
            downloading_pause_all.setOnFocusChangeListener(this);
            flag = 0;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU){
            setResult(Constants.NOTHINGFINISH);
            finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
        }
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
        //super.onBackPressed();
        setResult(Constants.NOTHINGFINISH);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                // 单选模式
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;
            case R.id.mutil_choose:
                // 多选模式
                setResult(RESULT_FIRST_USER);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;
            case R.id.detail:
                // 详情模式
                setResult(2);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;

            case R.id.mutil_delete:
                // 多选模式下删除多选item
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;
            case R.id.mutil_delete_cancle:
                // 多选模式下取消删除多选item
                setResult(3);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;
            case R.id.mutil_delete_ok:
                // 多选模式下全选删除所有item
                setResult(RESULT_FIRST_USER);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;

            case R.id.downloading_delete:
                // 下载中模式下单选删除
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;
            case R.id.all_start:
                // 下载中模式下下载全部开始
                setResult(RESULT_FIRST_USER);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;
            case R.id.all_pause:
                // 下载中模式下下载全部暂停
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;

            default:
                break;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
        // 单选模式
            case R.id.delete:
                if (hasFocus) {
                    focus_single_delete.setVisibility(View.VISIBLE);
                } else {
                    focus_single_delete.setVisibility(View.GONE);
                }
                break;
            case R.id.mutil_choose:
                if (hasFocus) {
                    focus_single_mutil_choose.setVisibility(View.VISIBLE);
                } else {
                    focus_single_mutil_choose.setVisibility(View.GONE);
                }
                break;
            case R.id.detail:
                if (hasFocus) {
                    focus_detail.setVisibility(View.VISIBLE);
                } else {
                    focus_detail.setVisibility(View.GONE);
                }
                break;

            // 多选模式
            case R.id.mutil_delete:
                if (hasFocus) {
                    focus_mutil_delete.setVisibility(View.VISIBLE);
                } else {
                    focus_mutil_delete.setVisibility(View.GONE);
                }
                break;
            case R.id.mutil_delete_cancle:
                if (hasFocus) {
                    focus_mutil_cancle.setVisibility(View.VISIBLE);
                } else {
                    focus_mutil_cancle.setVisibility(View.GONE);
                }
                break;
            case R.id.mutil_delete_ok:
                if (hasFocus) {
                    focus_mutil_ok.setVisibility(View.VISIBLE);
                } else {
                    focus_mutil_ok.setVisibility(View.GONE);
                }
                break;
            case R.id.downloading_delete:
                if (hasFocus) {
                    focus_delete.setVisibility(View.VISIBLE);
                } else {
                    focus_delete.setVisibility(View.GONE);
                }
                break;

            // 下载中模式
            case R.id.all_start:
                if (hasFocus) {
                    focus_allstart.setVisibility(View.VISIBLE);
                } else {
                    focus_allstart.setVisibility(View.GONE);
                }
                break;
            case R.id.all_pause:
                if (hasFocus) {
                    focus_allpause.setVisibility(View.VISIBLE);
                } else {
                    focus_allpause.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }
}
