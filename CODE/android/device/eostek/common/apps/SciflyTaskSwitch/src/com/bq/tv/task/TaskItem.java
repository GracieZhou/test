
package com.bq.tv.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.UserHandle;

import com.bq.tv.task.item.FileItem;

//line 64 97
/**
 *  A class of task item.
 */
public class TaskItem extends FileItem {

    protected static final String schema_prix = "task://";

    private int mTaskId;

    private int mPersistentTaskId;

    private Intent mIntent;

    final String mpackageName;

    private Bitmap mThumbnail;

    private Drawable mAppIcon;

    /**
     * Constructor of TaskItem.
     * @param uri
     * @param name
     * @param taskId
     * @param taskIntent
     * @param persistenTaskId
     * @param packageName
     * @param appIcon
     * @param thumbnail
     */
    protected TaskItem(Uri uri, String name, int taskId, Intent taskIntent, int persistenTaskId, String packageName,
            Drawable appIcon, Bitmap thumbnail) {
        super(uri, FileType.PHOTO, name);

        this.mTaskId = taskId;
        this.mIntent = taskIntent;
        this.mPersistentTaskId = persistenTaskId;
        this.mpackageName = packageName;
        this.mAppIcon = appIcon;
        this.mThumbnail = thumbnail;
    }

    /**
     * Get the task Id.
     * @return
     */
    public int getTaskId() {
        return mTaskId;
    }

    /**
     * Get the persistent task Id.
     * @return
     */
    public int getPersistentTaskId() {
        return this.mPersistentTaskId;
    }

    /**
     * Get intent.
     * @return
     */
    public Intent getIntent() {
        return this.mIntent;
    }

    /**
     * Get package name.
     * @return
     */
    public String getPackagename() {
        return this.mpackageName;
    }

    /**
     * Get application icon.
     * @return
     */
    public Drawable getAppIcon() {
        return mAppIcon;
    }

    /**
     * Get thumbnail.
     * @return
     */
    public Bitmap getThumbnail() {
        return mThumbnail;
    }


    /**
     * Build a TaskItem,  call the constructor.
     * @param taskId
     * @param title
     * @param persistentTaskId
     * @param intent
     * @param packageName
     * @param appIcon
     * @param thumbnail
     * @return
     */
    public static TaskItem buildTaskItem(int taskId, String title, int persistentTaskId, Intent intent,
            String packageName, Drawable appIcon, Bitmap thumbnail) {

        return new TaskItem(Uri.parse(schema_prix + taskId), title, taskId, intent, persistentTaskId, packageName,
                appIcon, thumbnail);
    }

    // public static TaskItem buildTaskItem(int taskId, String title, int
    // persistentTaskId, Intent intent,
    // String packageName) {
    //
    // return new TaskItem(Uri.parse(schema_prix + taskId), title, taskId,
    // intent, persistentTaskId, packageName);
    // }
    
    /**
     * Launch a task
     * @param context
     */
    public void launchTask(Context context) {
        // FIXME 判断应用是否启动.
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_TASK_ON_HOME
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivityAsUser(intent, null, new UserHandle(UserHandle.USER_CURRENT));
    }

    /* (non-Javadoc)
     * @see com.bq.tv.dataitem.FileItem#showLabel()
     */
    @Override
    public boolean showLabel() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.bq.tv.dataitem.FileItem#destroy()
     */
    @Override
    public void destroy() {
        if (mThumbnail != null)
            mThumbnail.recycle();

        mThumbnail = null;
    }
}
