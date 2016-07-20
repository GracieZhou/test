
package com.android.settings.bugreport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.util.Utils;

/**
 * @ClassName: FeedbackRecordDetailActivity.
 * @Description:Look at each of the specific information feedback records.
 * @author: lucky.li.
 * @date: Sep 15, 2015 10:32:25 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class FeedbackRecordDetailActivity extends Activity {
    /**
     * feedback time
     */
    private TextView mRecordDate;

    /**
     * feedback content
     */
    private TextView mRecordContent;

    /**
     * feedback status
     */
    private TextView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_record_detail);
        mRecordDate = (TextView) findViewById(R.id.record_date);
        mRecordContent = (TextView) findViewById(R.id.record_content);
        mStatusView = (TextView) findViewById(R.id.status);
        initViews();
    }

    /**
     * @Title: initViews
     * @Description: Initializes the interface element value
     * @param:
     * @return: void
     * @throws
     */
    private void initViews() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            RecordDetailBean record = (RecordDetailBean) bundle.get("recordDetail");
            String statusString = "";
            switch (record.getStatus()) {
                case 0:
                    statusString = getString(R.string.dealing);
                    break;
                case 1:
                    statusString = getString(R.string.acceptted);
                    break;
                case 2:
                    statusString = getString(R.string.resolved);
                    break;
                case 3:
                    statusString = getString(R.string.thanks);
                    break;
                case 4:
                    statusString = getString(R.string.published);
                    break;
                default:
                    statusString = getString(R.string.dealing);
                    break;
            }
            mStatusView.setText(statusString);
            mRecordContent.setText("       " + record.getSubmitContent());
            mRecordDate.setText(Utils.longParseDate(record.getSubmitTime()));
        }
        findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FeedbackRecordDetailActivity.this.finish();
            }
        });
    }
}
