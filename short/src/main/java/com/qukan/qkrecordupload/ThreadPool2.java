package com.qukan.qkrecordupload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 线程管理
 */
public class ThreadPool2
{
    private static ThreadPool2 instance = new ThreadPool2();
    private ExecutorService pool = Executors.newCachedThreadPool();

    private ThreadPool2()
    {
    }

    public static ThreadPool2 getInstance()
    {
        return instance;
    }

    public void execute(Runnable t)
    {
        pool.submit(t);
    }

    public void shutDown()
    {
        pool.shutdown();
    }
}
