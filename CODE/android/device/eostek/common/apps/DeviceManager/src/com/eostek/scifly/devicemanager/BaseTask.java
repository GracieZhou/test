
package com.eostek.scifly.devicemanager;

import android.content.Context;

import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.util.Constants;

public abstract class BaseTask implements Runnable {
    
    protected Context context;

    protected long size;
    
    protected BaseTaskListener listener;
    // This is the result of this task.    
    protected FileCollection collection = new FileCollection();
    private DeviceEngine engine;
    private int id = -1;
    //define the start time of the Thread;
    private long startTime;
    
    public BaseTask(final BaseTaskListener listener, final Context context) {
        this.listener = listener;
        this.context = context;
    }
    
    public BaseTask(final BaseTaskListener listener, final Context context, long size) {
        this.listener = listener;
        this.context = context;
        this.size = size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEngine(DeviceEngine engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        //the current time  starting
        startTime=System.currentTimeMillis();
        listener.onTaskStarted();
        try {
            execute();
            listener.onTaskCompleted(collection);
        } catch (TaskCancelledException e) {
            handleCancelEvent();
            listener.onTaskCancelled(collection);
        }
    }

    protected void execute() throws TaskCancelledException {
        // Execute this task.
    }

    protected void handleCancelEvent() {
        // Handler event when task cancelled.
    }

    protected void checkTaskCanceled() throws TaskCancelledException {
        long checkTime=System.currentTimeMillis();
        //space time must be greater than 500ms
        if (checkTime - startTime < Constants.MIN_SPACE_TIME) {
            return;
        }

        if (Thread.currentThread().isInterrupted()) {
            throw new TaskCancelledException();
        }
        if (engine == null || !engine.isTaskActual(id)) {
            throw new TaskCancelledException();
        }
    }

    public class TaskCancelledException extends Exception {
        
    }
}
