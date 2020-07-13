package com.qukan.qkrecordupload;


import android.content.Context;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.qukan.qkrecorduploadsdk.RecordSdk;
import com.qukan.qkrecorduploadsdk.utils.QLog;

import org.litepal.LitePal;

import lombok.Getter;

/**
 * Created by android on 2015/1/6.
 */
public class QkApplication {

    @Getter
    private static Context context;

    public static void onCreate(Context context) {
        // 设置上下文
        QkApplication.context = context;
        //初始化sdk，上线修改日志等级
        RecordSdk.init(context, QLog.DEBUG);

        // 初始化图片加载库
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)

                .memoryCache(new WeakMemoryCache())

                .memoryCacheSize(1 * 1024 * 1024) //缓存到内存的最大数据

                .discCacheSize(10 * 1024 * 1024)  //缓存到文件的最大数据

                .discCacheFileCount(100)     //文件数量

                .build();

        ImageLoader.getInstance().init(config);
        LitePal.initialize(context);
    }
}
