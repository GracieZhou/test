
package com.android.settings.bugreport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.settings.R;
/**
 * 
 * @ClassName:  BugReportActivity. 
 * @Description:Provide user submit bug.   
 * @author: lucky.li.  
 * @date:   Sep 15, 2015 9:23:39 AM.   
 * @Copyright:  Eostek Co., Ltd. Copyright ,  All rights reserved.
 */
public class BugReportActivity extends Activity {
    private BugReportHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugreport);
        mHolder = new BugReportHolder(this);
        mHolder.findViews();
        mHolder.initViews();
        mHolder.registerListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            mHolder.setTextToEditText(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
