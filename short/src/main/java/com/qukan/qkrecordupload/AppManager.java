package com.qukan.qkrecordupload;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import org.droidparts.util.L;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * app管理(初始化,退出,配置等等)
 * 必须在主线程(UI线程)调用
 *
 * @author fyxjsj
 */
public class AppManager
{
    private static LinkedList<Activity> activityList = new LinkedList<Activity>();
    private static AppManager instance = null;

    public static boolean appIsOpen = false;

    private AppManager()
    {
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance()
    {
        if (instance == null)
        {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到末尾
     */
    public void addActivity(Activity activity)
    {

        if (!activityList.contains(activity)) {
            activityList.addLast(activity);
        }

        L.i("called,%s，size=%d", activity.toString(), activityList.size());
    }

    /**
     * 获取当前Activity;
     */
    public Activity currentActivity()
    {
        return activityList.getLast();
    }

    /**
     * 结束当前Activity
     */
    public void finishActivity()
    {
        Activity activity = activityList.getLast();
        if (null == activity)
        {
            L.e("ERROR: currentActivity is null");
            return;
        }

        L.i("called,%s", activity.toString());
        activityList.removeLast().finish();
    }

    /**
     * 结束指定的Activity实例
     */
    public static void finishActivity(Activity activity)
    {
        if (null == activity)
        {
            L.w("activity is null");
            return;
        }

        // 从List中移除该activity
        if (activityList.remove(activity))
        {
            L.i("called,%s", activity.toString());

            // 关闭该activity
            activity.finish();
        }
    }

    public static void finishSingleActivityByClass(Class cls) {
        Activity tempActivity = null;
        for (Activity activity : activityList) {
            if (activity.getClass().equals(cls)) {
                tempActivity = activity;
            }
        }
        if (tempActivity != null) {
            finishActivity(tempActivity);
        }
    }

    /**
     * 结束指定类名的所有的Activity
     */
    public void finishActivity(Class<?> cls)
    {
        // 将数据swap到临时链表中
        List<Activity> tempList = new ArrayList<Activity>(activityList);
        activityList.clear();

        // 遍历所有的activity,把cls的实例全部finish
        for (Activity activity : tempList)
        {
            if (activity.getClass().equals(cls))
            {
                L.i("called,%s", activity.toString());
                activity.finish();
            }
            else
            {
                // 重新添加到activityList中
                addActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity()
    {
        while (!activityList.isEmpty())
        {
            Activity activity = activityList.getLast();
            L.i("called,%s", activity.toString());

            activityList.removeLast().finish();
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context, boolean isCrashed)
    {
        L.i("called,isCrashed=" + isCrashed);

        try
        {
            // 清除所有的activity
            finishAllActivity();
        }
        catch (Exception e)
        {
            L.e(e);
        }

        try
        {
            // 表示异常退出
            if (isCrashed)
            {
                // 杀死后台进程
                ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                activityMgr.killBackgroundProcesses(context.getPackageName());
                System.exit(isCrashed ? 1 : 0);
            }

            AppManager.appIsOpen = false;
        }
        catch (Exception e)
        {
            L.e(e);
        }
    }
}
