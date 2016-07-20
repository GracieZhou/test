package com.eostek.scifly.devicemanager.manage.garbage.task;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.BaseTask;
import com.eostek.scifly.devicemanager.BaseTaskListener;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.FileTool;

public class ClearGarbageTask extends BaseTask {
    private static final String TAG = ClearGarbageTask.class.getSimpleName();
    
    private FileCollection delCollection;

    public ClearGarbageTask(final BaseTaskListener listener, final Context context) {
        super(listener, context);
    }

    public void setCollection(FileCollection collection) {
        delCollection = collection;
    }

    @Override
    protected void execute() throws TaskCancelledException {
        super.execute();

        if (delCollection == null || delCollection.isEmpty()) {
            Debug.d(TAG, "No files need to be deleted!");
            return;
        }
        ArrayList<String> delFiles = delCollection.getFiles();
        for (String filename : delFiles) {
            File file = new File(filename);
            if (file.exists() && file.canRead() && file.canWrite()) {
                Debug.d(TAG, "delete [" + filename + "]");
                FileTool.deleteFile(file);
                collection.add(filename);
                listener.onProgress(collection);
            }

            checkTaskCanceled();
        }
    }

}
