package com.qukan.qkrecordupload;

import android.content.Context;

import org.droidparts.util.L;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler
{
    private static CrashHandler instance;

    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;

    // 程序的Context对象
    private Context mContext;

    public static CrashHandler getInstance()
    {
        if (instance == null)
        {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context context)
    {
        this.mContext = context;

        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        L.e(ex);

        if (mDefaultHandler != null)
        {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }
}