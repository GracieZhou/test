
package com.eostek.documentui.view;

import java.util.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.eostek.documentui.R;
import com.eostek.documentui.model.DownloadGridItemBean;
import com.eostek.documentui.util.Utils;

public class DeleteModeDialog extends Activity {

    private Button dialog_bn_cancle;

    private Button dialog_bn_ok;

    private TextView downloaded_filename;

    private TextView downloaded_detail_filename;

    private TextView downloaded_detail_filesize;

    private TextView downloaded_detail_createtime;

    private TextView downloaded_detail_savepath;

    private Button detail_bn_ok;

    private int flag = 0;

    private DownloadGridItemBean bean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mflags = getIntent().getExtras().getString("flag");
        bean=(DownloadGridItemBean) getIntent().getExtras().getSerializable("bean");
        if (mflags.equals("isDeleteMode")) {
            flag = 1;
            setContentView(R.layout.delete_activity_dialog);
        } else if (mflags.equals("isDetailMode")) {
            flag = 2;
           /*mMainView = getLayoutInflater().inflate(R.layout.file_info_layout, null);*/
            setContentView(R.layout.file_info_layout);
            
        }

        findViews();

    }

    private void findViews() {
        if (flag == 1) {
            dialog_bn_cancle = (Button) findViewById(R.id.dialog_bn_cancle);
            dialog_bn_ok = (Button) findViewById(R.id.dialog_bn_ok);
            downloaded_filename = (TextView) findViewById(R.id.delete_activity_dialog_tv);
            if (bean == null) {
                downloaded_filename.setText(getResources().getString(R.string.delete_mutil_string));
            } else {
                downloaded_filename.setText(getResources().getString(R.string.sure_to_remove));
            }
            initListener();
            flag = 0;
        }
        if (flag == 2) {
	    downloaded_detail_filename = (TextView) findViewById(R.id.file_name);
            downloaded_detail_filesize = (TextView) findViewById(R.id.file_size);
            downloaded_detail_createtime = (TextView) findViewById(R.id.finish_time);
            downloaded_detail_savepath = (TextView) findViewById(R.id.file_location);
            detail_bn_ok = (Button) findViewById(R.id.ok_button);

            String fileName = bean.getSaveName();
            downloaded_detail_filename.setText(fileName);

            int fileSize = bean.getFileSize();
            Log.i("tag", "filesize===>" + fileSize);
            downloaded_detail_filesize.setText(Utils.formatFileSize(fileSize));

            long createTime = bean.getCreateTime();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=new Date(createTime);
            String createTimeStr=sdf.format(date);
            Log.i("tag", "createTimeStr===>" + createTimeStr);
            downloaded_detail_createtime.setText(createTimeStr);

            String savePath = bean.getFullSavePath();
            Log.i("tag", "savepath===>" + savePath);
            downloaded_detail_savepath.setText(savePath);

            flag = 0;

            detail_bn_ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    private void initListener() {
        dialog_bn_cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog_bn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("tag", "===click__ok===>");
                // mList.remove(mposition);
                setResult(RESULT_OK);
                finish();
            }
        });

    }

}
