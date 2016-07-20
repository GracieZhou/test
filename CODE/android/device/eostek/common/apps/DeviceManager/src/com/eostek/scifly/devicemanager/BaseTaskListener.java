package com.eostek.scifly.devicemanager;

public interface BaseTaskListener {

    void onTaskStarted();

    void onProgress(FileCollection collection);

    void onTaskCancelled(FileCollection collection);

    void onTaskCompleted(FileCollection collection);
}
