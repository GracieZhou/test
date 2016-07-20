
package com.eostek.scifly.devicemanager.manage.garbage.listener;

import android.os.Handler;

import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.BaseTaskListener;
import com.eostek.scifly.devicemanager.util.Constants;

public class ClearGarbageTaskListener implements BaseTaskListener {

    private Handler mhandler;

    public ClearGarbageTaskListener(Handler mhandler) {
        super();
        this.mhandler = mhandler;
    }

    @Override
    public void onTaskCompleted(FileCollection collection) {
        // inform mainthread of handling the progressbar
        mhandler.sendEmptyMessage(Constants.GARBAGE_MSG_CLEAN_COMPLETED);
    }

    @Override
    public void onTaskStarted() {
    }

    @Override
    public void onProgress(FileCollection collection) {
    }

    @Override
    public void onTaskCancelled(FileCollection collection) {
    }
}
