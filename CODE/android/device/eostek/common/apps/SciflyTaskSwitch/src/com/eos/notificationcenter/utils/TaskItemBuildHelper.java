
package com.eos.notificationcenter.utils;

import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.bq.tv.task.TaskItem;
import com.eos.notificationcenter.R;

/**
 *  Help to build a task item.
 */
public class TaskItemBuildHelper {

    /**
     * Tag to show in logcat.
     */
    public static String TAG = "TaskItemBuildHelper";

    /**
     * 
     */
    public static final String LAUNCH_TASKSWITCH_RECENT = "qingyu.TaskSwitch.launch.recent";

    /**
     * 
     */
    public static final String LAUNCH_RUNNING_TASK = "qingyu.TaskSwitch.launch.runningtask";

    private Context mContext;

    private ActivityManager mActivityManager;

    private static final int MAX_TASKS = 8;

    private String mCurrentTaskType = LAUNCH_TASKSWITCH_RECENT;

    private List<String> mPackageWhiteList = new ArrayList<String>();
    
    private static List<TaskItem> mRecentTaskList = new ArrayList<TaskItem>();

    /**
     * Constructor of TaskItemBuildHelper.
     * @param mContext
     */
    public TaskItemBuildHelper(Context mContext) {
        this.mContext = mContext;

        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        mPackageWhiteList = Arrays.asList(mContext.getResources().getStringArray(R.array.package_whitelist));

        Log.i(TAG, "package white list " + mPackageWhiteList);
    }

    /** get task items */
    public List<TaskItem> getTaskItems() {
        return getTaskFromAMSService();
    }

    private List<TaskItem> getTaskFromAMSService() {

        List<TaskItem> fileItems;

        long start = System.currentTimeMillis();
        if (mCurrentTaskType == LAUNCH_RUNNING_TASK) {
            fileItems = getRunningTaskFromAMService();
        } else {
            fileItems = getRecentTaskFromAMService(false);
        }

        Log.v(TAG, "currentTaskType:" + mCurrentTaskType + " load cost:" + (System.currentTimeMillis() - start)
                + "......mFileItems.size:" + fileItems.size());

        return fileItems;
    }

    private List<TaskItem> getRunningTaskFromAMService() {
        return getRecentTaskFromAMService(true);
    }

    /**
     * Build a task item.
     * @param taskId
     * @param persistentTaskId
     * @param baseIntent
     * @param origActivity
     * @return
     */
    private TaskItem buildTaskItem(int taskId, int persistentTaskId, Intent baseIntent, ComponentName origActivity) {

        Intent intent = new Intent(baseIntent);
        if (origActivity != null) {
            intent.setComponent(origActivity);
        }
        final PackageManager pm = mContext.getPackageManager();
        intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);

        if (resolveInfo != null) {
            final ActivityInfo info = resolveInfo.activityInfo;
            final String title = info.loadLabel(pm).toString();

            if (title != null && title.length() > 0) {

                final ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

                Bitmap bitmap = null; 
                
                try {
                	//Android 4.4 等
                	//bitmap = am.getTaskThumbnails(taskId).mainThumbnail;
                	if(Build.VERSION.SDK_INT < 21) {
	                	Method getTaskThumbnails = Class.forName("android.app.ActivityManager").getMethod("getTaskThumbnails", int.class);
	                	Class<?> taskThumbnails = Class.forName("android.app.ActivityManager$TaskThumbnails");
	                	Field field = taskThumbnails.getDeclaredField("mainThumbnail");
	                	Object retObject = getTaskThumbnails.invoke(am, taskId);
	                	field.setAccessible(true);
	                	bitmap = (Bitmap)field.get(retObject);
                	}
                	//Android 5.0、5.1等
                	//bitmap = am.getTaskThumbnail(taskId).mainThumbnail;
                	else if(Build.VERSION.SDK_INT >= 21) {
                		Method getTaskThumbnail = Class.forName("android.app.ActivityManager").getMethod("getTaskThumbnail", int.class);
	                	Class<?> TaskThumbnail = Class.forName("android.app.ActivityManager$TaskThumbnail");
	                	Field field = TaskThumbnail.getDeclaredField("mainThumbnail");
	                	Object retObject = getTaskThumbnail.invoke(am, taskId);
	                	field.setAccessible(true);
	                	bitmap = (Bitmap)field.get(retObject);
                	}
                } catch (SecurityException e) {
                    Log.i(TAG, "need system permission");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (bitmap == null) {
                    bitmap = drawableToBitmap(mContext.getResources().getDrawable(R.drawable.ic_launcher), mContext
                            .getResources().getDimensionPixelSize(R.dimen.APP_THUMB_WIDTH), mContext.getResources()
                            .getDimensionPixelSize(R.dimen.APP_THUMB_WIDTH));
                }

                return TaskItem.buildTaskItem(taskId, title, persistentTaskId, baseIntent, info.packageName,
                        info.loadIcon(pm), bitmap);
            } else {
                Log.v(TAG, "SKIPPING item " + persistentTaskId);
            }
        }

        return null;
    }

    private List<TaskItem> getRecentTaskFromAMService(boolean filterNotRunning) {

        //List<TaskItem> taskList = new ArrayList<TaskItem>();

        final int origPri = android.os.Process.getThreadPriority(android.os.Process.myTid());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        final PackageManager pm = mContext.getPackageManager();

        final List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager.getRecentTasks(MAX_TASKS,
                ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        int numTasks = recentTasks.size();
        ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).resolveActivityInfo(
                pm, 0);
        
        for(int i = 0; i < mRecentTaskList.size();i++) {
            mRecentTaskList.get(i).destroy();
        }
        
        mRecentTaskList.clear();

        for (int i = 0; i < numTasks && (i < MAX_TASKS); ++i) {
            final ActivityManager.RecentTaskInfo recentInfo = recentTasks.get(i);

            Intent intent = new Intent(recentInfo.baseIntent);
            if (recentInfo.origActivity != null) {
                intent.setComponent(recentInfo.origActivity);
            }

            // white list
            if (mPackageWhiteList.contains(intent.getComponent().getPackageName())) {
                Log.i(TAG, "in the whitelist not to show : " + intent.getComponent().getPackageName());
                continue;
            }

            if (intent.hasCategory(Intent.CATEGORY_HOME)) {
                Log.d(TAG, "hasCategory package name: " + intent.getComponent().getPackageName());
                continue;
            }
            
            // Don't load the current home activity.
            if (isCurrentHomeActivity(intent.getComponent(), homeInfo)) {
                continue;
            }

            // Don't load ourselves
            if (intent.getComponent().getPackageName().equals(mContext.getPackageName())) {
                continue;
            }

            if (filterNotRunning && recentInfo.id == -1) {// 如果要求过滤斌且taskid==-1则过滤掉.
                continue;
            }

            TaskItem item = buildTaskItem(recentInfo.id, recentInfo.persistentId, recentInfo.baseIntent,
                    recentInfo.origActivity);

            if (item != null) {
                mRecentTaskList.add(item);
            }
        }

        android.os.Process.setThreadPriority(origPri);

        return mRecentTaskList;// .toArray(new TaskItem[taskList.size()]);
    }

    private boolean isCurrentHomeActivity(ComponentName component, ActivityInfo homeInfo) {
        if (homeInfo == null) {
            final PackageManager pm = mContext.getPackageManager();
            homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);
        }
        return homeInfo != null && homeInfo.packageName.equals(component.getPackageName())
                && homeInfo.name.equals(component.getClassName());
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        // if ((drawable instanceof BitmapDrawable))
        // return ((BitmapDrawable) drawable).getBitmap();

        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        drawable.setBounds(0, 0, localCanvas.getWidth(), localCanvas.getHeight());
        drawable.draw(localCanvas);
        return localBitmap;
    }

}
