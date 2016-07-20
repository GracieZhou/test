
package com.eostek.scifly.devicemanager.manage.garbage.task;

import java.util.TimerTask;

import com.eostek.scifly.devicemanager.util.Debug;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ProgressBarTimerTask extends TimerTask {
    
    private static final String TAG = ProgressBarTimerTask.class.getSimpleName();
    
    protected static final int END_TIMER = -100;

    protected static final int RUNNING_TIMER = -99;

    protected static final int UPDATE_MPB = -1000;

    protected static final int INIT_MPB = -999;

    private Handler mhandler;

    private int flag;

    private int percent;

    public ProgressBarTimerTask(Handler mhandler, int flag, int percent) {
        this.mhandler = mhandler;
        this.flag = flag;
        this.percent = percent;
    }

    int p = 0;

    int q = percent;

    @Override
    public void run() {

        synchronized (this) {
            switch (flag) {
                case INIT_MPB:
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    if (p < percent) {
                        data.putInt("percent", p += 1);
                        msg.setData(data);
                        msg.what = RUNNING_TIMER;

                    } else {
                        data.putInt("percent", percent);
                        msg.setData(data);
                        msg.what = END_TIMER;
                    }
                    mhandler.sendMessage(msg);
                    break;
                case UPDATE_MPB:
                    Debug.d(TAG, "UPDATE_MPB");
                    Message msg1 = new Message();
                    Bundle data1 = new Bundle();
                    if (q > 0) {
                        data1.putInt("percent", q--);
                        msg1.setData(data1);
                        msg1.what = RUNNING_TIMER;
                    } else {
                        data1.putInt("percent", percent);
                        msg1.setData(data1);
                        msg1.what = END_TIMER;
                    }
                    mhandler.sendMessage(msg1);
                    break;
                default:
                    Debug.d(TAG, "initTvOnmPbBySecond param error");
                    break;
            }
        }

    }

}
