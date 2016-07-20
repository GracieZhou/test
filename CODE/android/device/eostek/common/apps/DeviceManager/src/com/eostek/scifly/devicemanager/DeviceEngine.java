package com.eostek.scifly.devicemanager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.eostek.scifly.devicemanager.util.Debug;

public class DeviceEngine {
    
    private final static String TAG = DeviceEngine.class.getSimpleName();
    
    private ExecutorService threadPool;
    private static int taskId = -1;
    private Set<Integer> syncTaskSet = Collections.synchronizedSet(new HashSet<Integer>());

    protected DeviceEngine(int threadSize) {
        threadPool = Executors.newFixedThreadPool(threadSize);
        Debug.d(TAG, "DeviceEngine starting...");
        syncTaskSet.clear();
    }

    public int submit(BaseTask task) {
        task.setId(++taskId);
        threadPool.submit(task);
        syncTaskSet.add(taskId);
        return taskId;
    }

    public boolean cancel(int id) {
        return syncTaskSet.remove(id);
    }

    public void destroy() {
        syncTaskSet.clear();
        threadPool.shutdown();
        Debug.d(TAG, "DeviceEngine stopping...");
    }

    public boolean isTaskActual(int id) {
        return syncTaskSet.contains(id);
    }
}
