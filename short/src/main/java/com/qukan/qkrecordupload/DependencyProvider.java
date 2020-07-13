package com.qukan.qkrecordupload;

import android.content.Context;

import org.droidparts.AbstractDependencyProvider;
import org.droidparts.persist.sql.AbstractDBOpenHelper;

public class DependencyProvider extends AbstractDependencyProvider {
    private static DependencyProvider instance = null;

    public DependencyProvider(Context ctx) {
        super(ctx);

        instance = this;
    }

    // 获取静态实例
    public static DependencyProvider getInstance() {
        return instance;
    }

    @Override
    public AbstractDBOpenHelper getDBOpenHelper() {
        return null;
    }
}
