package com.qukan.qkrecordupload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 线程管理
 */
public class ThreadPool
{
    private static ThreadPool instance = new ThreadPool();
    private ExecutorService pool = Executors.newCachedThreadPool();

    private ThreadPool()
    {
    }

    public static ThreadPool getInstance()
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
