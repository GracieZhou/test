
package com.eostek.uitemplate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.eostek.uitemplate.widget.ConfirmDialog;
import com.eostek.uitemplate.widget.NoticeDialog;
import com.eostek.uitemplate.widget.ProgressDialog;


public class DialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

    }

    public void showDialog(View v) {
        ConfirmDialog dialog = new ConfirmDialog(DialogActivity.this);
        dialog.setTitle(R.string.scifly_dialog_title);
        dialog.show();
    }

    public void showNoticeDialog(View v) {
        NoticeDialog dialog = new NoticeDialog(DialogActivity.this);
        // dialog.setTitle(R.string.scifly_dialog_title);
        dialog.show();
    }

    public void showProgressDialog(View v) {
        ProgressDialog dialog = new ProgressDialog(DialogActivity.this);
        // dialog.setTitle(R.string.scifly_dialog_title);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
