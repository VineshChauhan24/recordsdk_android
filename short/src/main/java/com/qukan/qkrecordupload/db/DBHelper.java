package com.qukan.qkrecordupload.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qukan.qkrecordupload.SdkUtils;
import com.qukan.qkrecordupload.fileUpload.FileInfoStatus;
import com.qukan.qkrecorduploadsdk.utils.QLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/3/3.
 */
public class DBHelper
{
    private static final String DATABASE_NAME = "qk_file_upload.db";// 数据库名
    private static final Object mLock = new Object();
    private static DBHelper dbHelper_;

    private SQLiteDatabase db;

    public static DBHelper instance()
    {
        synchronized (mLock)
        {
            if (dbHelper_ == null)
            {
                dbHelper_ = new DBHelper(SdkUtils.getApplicationContext());
            }
            return dbHelper_;
        }
    }

    private DBHelper(Context context)
    {
        //开启数据库
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        createTable();
        createMapTable();
        QLog.i("sqlite3 dbpath=%s", db.getPath());
    }

    protected void createTable()
    {
        try
        {
            // TODO: 调试代码,需要删除的
//            db.execSQL("DROP TABLE IF EXISTS qk_file_upload;");

            // 创建数据库表
            db.execSQL("CREATE TABLE IF NOT EXISTS qk_file_upload (id INTEGER PRIMARY KEY AUTOINCREMENT,filePath TEXT,fileStatus TEXT,uploadPath TEXT,fileUploadName TEXT);");
            QLog.d("Create or Check Table qk_file_upload ok");
        }
        catch (Exception e)
        {
            QLog.w(e);
        }
    }

    /**
     * 查找数据
     */

    public boolean find(FileInfoStatus fileStatus)
    {
        try
        {
            Cursor cursor = db.query("qk_file_upload", null, "filePath='"+fileStatus.getInfo().getFilePath()+"'", null, null, null, null);
            if (cursor.getCount() <= 0)
            {
                return true;
            }

            return false;
        }
        catch (Exception e)
        {
            QLog.w(e);
            return false;
        }
    }

    /**
     * 增加数据
     */
    public boolean insert(FileInfoStatus fileStatus)
    {
        if (!find(fileStatus))
        {
            QLog.w("db have filePath : %s",fileStatus.getInfo().getFilePath());
            return false;
        }

        try
        {
            String sql = String.format("insert into qk_file_upload values(null,'%s','%s','%s','%s')",
                    fileStatus.getInfo().getFilePath(),
                    fileStatus.getStatus(),
                    fileStatus.getInfo().getUploadPath(),
                    fileStatus.getInfo().getUploadName());

            db.execSQL(sql);
            QLog.d("insert qk_file_upload ok,%s", fileStatus);
            return true;
        }
        catch (Exception e)
        {
            QLog.w(e);
            return false;
        }
    }

    public boolean update(FileInfoStatus fileStatus)
    {
        try
        {
            String sql = String.format("update qk_file_upload set fileStatus='%s',uploadPath='%s',fileUploadName='%s' where filePath='%s'",
                    fileStatus.getStatus(),
                    fileStatus.getInfo().getUploadPath(),
                    fileStatus.getInfo().getUploadName(),
                    fileStatus.getInfo().getFilePath());
            db.execSQL(sql);
            QLog.d("update qk_file_upload ok, %s", fileStatus);
            return true;
        }
        catch (Exception e)
        {
            QLog.w(e);
            return false;
        }
    }

    public boolean delete(String filePath)
    {
        try
        {
            String sql = String.format("delete from qk_file_upload where filePath='%s'", filePath);
            db.execSQL(sql);
            QLog.d("delete %s from qk_file_upload ok", filePath);
            return true;
        }
        catch (Exception e)
        {
            QLog.w(e);
            return false;
        }
    }

    /**
     * 查询数据库,获取未完成的记录
     */
    public ArrayList<FileInfoStatus> selectUnfinished()
    {
        ArrayList<FileInfoStatus> resultList = new ArrayList<>();

        // 查询数据库,获取未完成的记录
        Cursor cursor = db.query("qk_file_upload", null, null, null, null, null, null);
        if (cursor.getCount() <= 0)
        {
            return resultList;
        }
        QLog.d("Create or Check Table qk_file_upload ok");

        // 数据库的列索引
        int indexFilePath = cursor.getColumnIndex("filePath");
        int indexFileStatus = cursor.getColumnIndex("fileStatus");
        int indexUploadPath = cursor.getColumnIndex("uploadPath");
        int indexUploadName = cursor.getColumnIndex("fileUploadName");
        while (cursor.moveToNext())
        {
            String fileName = cursor.getString(indexFilePath);
            File file = new File(fileName);
            if (file.exists())
            {
                FileInfoStatus status = new FileInfoStatus();
                status.setStatus(cursor.getString(indexFileStatus));

                status.getInfo().setFileName(file.getName());
                status.getInfo().setFilePath(file.getPath());
                status.getInfo().setFileLength(String.valueOf(file.length()));
                status.getInfo().setUploadName(cursor.getString(indexUploadName));
                status.getInfo().setUploadPath(cursor.getString(indexUploadPath));
                status.getInfo().setFileType("3");

                resultList.add(status);
            }
        }

        return resultList;
    }

    //文件合并列表数据
    protected void createMapTable()
    {
        try
        {
            // TODO: 调试代码,需要删除的
//            db.execSQL("DROP TABLE IF EXISTS qk_file_map;");

            // 创建数据库表
            db.execSQL("CREATE TABLE IF NOT EXISTS qk_file_map (id INTEGER PRIMARY KEY AUTOINCREMENT,key TEXT,fileList TEXT,uploadList TEXT);");
            QLog.d("Create or Check Table qk_file_map ok");
        }
        catch (Exception e)
        {
            QLog.w(e);
        }
    }

    /**
     * 增加数据
     */
    public boolean insertMap(FileMergeInfo info)
    {
        if (info.getMerge())
        {
            return deleteMap(info);
        }

        try
        {
            String temp1 = info.getFileList().toString();
            String temp2 = info.getFileList().toString();

            String sql = String.format("insert into qk_file_map values(null,'%s','%s','%s')",
                    info.getKey(),
                    info.getFileList().toString(),
                    info.getUploadList().toString());

            db.execSQL(sql);
            QLog.d("insert qk_file_map ok,%s", info);
            return true;
        }
        catch (Exception e)
        {
            QLog.w(e);
            return false;
        }
    }

    public boolean updateMap(FileMergeInfo info)
    {
        if (info.getMerge())
        {
            return deleteMap(info);
        }

        try
        {
            String sql = String.format("update qk_file_map set fileList='%s',uploadList='%s' where key='%s'",
                    info.getFileList().toString(),
                    info.getUploadList().toString(),
                    info.getKey());

            db.execSQL(sql);
            QLog.d("update qk_file_upload ok, %s", info);
            return true;
        }
        catch (Exception e)
        {
            QLog.w(e);
            return false;
        }
    }

    public boolean deleteMap(FileMergeInfo info)
    {
        try
        {
            String sql = String.format("delete from qk_file_map where key='%s'", info.getKey());
            db.execSQL(sql);
            QLog.d("delete %s from qk_file_map ok", info);
            return true;
        }
        catch (Exception e)
        {
            QLog.w(e);
            return false;
        }
    }

    /**
     * 查询数据库,获取未完成的记录
     */
    public Map<String,FileMergeInfo> selectUnfinishedMap()
    {
        Map<String,FileMergeInfo> infoMap = new HashMap<String,FileMergeInfo>();

        // 查询数据库,获取未完成的记录
        Cursor cursor = db.query("qk_file_map", null, null, null, null, null, null);
        if (cursor.getCount() <= 0)
        {
            return infoMap;
        }
        QLog.d("Create or Check Table qk_file_upload ok");

        // 数据库的列索引
        int indexKey = cursor.getColumnIndex("key");
        int indexFileList = cursor.getColumnIndex("fileList");
        int indexUploadList = cursor.getColumnIndex("uploadList");
        while (cursor.moveToNext())
        {
            FileMergeInfo info = new FileMergeInfo();
            info.setKey(cursor.getString(indexKey));
            info.setMerge(false);
            info.setFileList(FileNameHelper.StringToList(cursor.getString(indexFileList)));
            info.setUploadList(FileNameHelper.StringToList(cursor.getString(indexUploadList)));

            infoMap.put(info.getKey(),info);
        }

        return infoMap;
    }
}
