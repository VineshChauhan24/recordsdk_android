package com.qukan.qkrecordupload.file;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.qukan.qkrecordupload.QkApplication;

import org.droidparts.util.L;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/3.
 */
public class DBHelper {
    private static final String DATABASE_NAME = "qk_live_file_upload.db";// 数据库名
    private static final Object mLock = new Object();
    private static DBHelper dbHelper_;

    public static final String db_liveVideo = "liveVideo";
    public static final String db_recordVideo = "recordVideo";
    public static final String db_clipVideo = "clipVideo";
    public static final String db_takePicture = "takePicture";
    public static final String db_mp3 = "mp3";
    public static final String db_newsFile = "news";
    public static final String db_phoneFile = "phoneFile";//手机内文件

    public static final String db_alluploadedFile = "alluploaded";// 获取已经上传完毕的文件列表,即state>1
    public static final String db_needUploadFile = "needupload";// 获取被标记为需要上传的文件列表,即state=1

    private SQLiteDatabase db;

    public static DBHelper instance() {
        synchronized (mLock) {
            if (dbHelper_ == null) {
                dbHelper_ = new DBHelper(QkApplication.getContext());
            }
            return dbHelper_;
        }
    }

    private DBHelper(Context context) {
        //开启数据库
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        createTable();
//        createMapTable();
        L.i("sqlite3 dbpath=%s", db.getPath());
    }

    protected void createTable() {
        try {
            // TODO: 调试代码,需要删除的
//            db.execSQL("DROP TABLE IF EXISTS qk_file_upload;");

            // 创建数据库表,创建两个表,一个用于记录所有的状态信息,一个只用来记录上传完成的文件
            db.execSQL("CREATE TABLE IF NOT EXISTS qk_file_upload (id INTEGER PRIMARY KEY AUTOINCREMENT,filePath TEXT,liveId INTEGER,status INTEGER,fileType TEXT,fileName TEXT,fileDisplayName TEXT,fileGroup TEXT,uploadName TEXT,userId TEXT,percent double,fileLength TEXT,timeDate TEXT,timeLength TEXT,pause INTEGER);");

            db.execSQL("CREATE TABLE IF NOT EXISTS qk_file_completed (id INTEGER PRIMARY KEY AUTOINCREMENT,filePath TEXT,fileType TEXT,fileName TEXT,fileDisplayName TEXT,userId TEXT,fileLength TEXT,timeDate TEXT,timeLength TEXT);");

            L.d("Create or Check Table qk_file_upload ok");
        } catch (Exception e) {
            L.w(e);
        }
    }

    /**
     * 增加一条上传完成的记录
     */
    public boolean insertItem(FileInfoStatus infoStatus) {
        if (infoStatus == null) {
            return false;
        }

        // 如果数据已经存在数据库中
        if (!find(infoStatus, "qk_file_completed")) {
            L.w("db have item : %s", infoStatus.getFilePath());
            deleteItem(infoStatus.getFilePath());
        }

        try {

            String sql = String.format("insert into qk_file_completed ('fileType','fileName', 'fileDisplayName', 'filePath','userId','fileLength','timeDate','timeLength') values('%s','%s','%s','%s','%s','%s','%s','%s')",
                    infoStatus.getFileType(),
                    infoStatus.getFileName(),
                    infoStatus.getFileDisplayName(),
                    infoStatus.getFilePath(),
                    infoStatus.getUserId(),
                    infoStatus.getFileLength(),
                    infoStatus.getTimeDate(),
                    infoStatus.getTimeLength()
            );
            db.execSQL(sql);
            L.d("insert qk_file_completed ok" + sql);
            return true;
        } catch (Exception e) {
            L.w(e);
            return false;
        }
    }

    /**
     * 删除一条上传完成的记录
     */
    public boolean deleteItem(String filePath) {
        try {
            String sql = String.format("delete from qk_file_completed where filePath='%s'", filePath);
            db.execSQL(sql);
            L.d("delete %s from qk_file_completed ok", filePath);
            return true;
        } catch (Exception e) {
            L.w(e);
            return false;
        }
    }

    /**
     * 取出所有已经上传完成的记录
     */
    public ArrayList<FileInfoStatus> getCompletedList(String userId) {
        ArrayList<FileInfoStatus> resultList = new ArrayList<>();

        // 查询数据库,获取未完成的记录
        Cursor cursor = null;

        //desc为降序排列asc为升序
        cursor = db.query("qk_file_completed", null,"userId =? ", new String[]{userId}, null, null, "id desc");

        if (cursor.getCount() <= 0) {
            return resultList;
        }
        L.d("Create or Check Table qk_file_completed ok");

        // 数据库的列索引
        int indexFileType = cursor.getColumnIndex("fileType");
        int indexFileId = cursor.getColumnIndex("id");
        int indexFileName = cursor.getColumnIndex("fileName");
        int indexFileDisplayName = cursor.getColumnIndex("fileDisplayName");
        int indexFilePath = cursor.getColumnIndex("filePath");
        int indexUserId = cursor.getColumnIndex("userId");
        int indexFileLength = cursor.getColumnIndex("fileLength");
        int timeDate = cursor.getColumnIndex("timeDate");
        int timeLength = cursor.getColumnIndex("timeLength");


        while (cursor.moveToNext()) {
                FileInfoStatus fileInfo = new FileInfoStatus();
                fileInfo.setFileType(cursor.getString(indexFileType));
                fileInfo.setFileId(cursor.getInt(indexFileId));
                fileInfo.setFileName(cursor.getString(indexFileName));
                fileInfo.setFileDisplayName(cursor.getString(indexFileDisplayName));
                fileInfo.setFilePath(cursor.getString(indexFilePath));
                fileInfo.setUserId(cursor.getString(indexUserId));
                fileInfo.setFileLength(cursor.getString(indexFileLength));
                fileInfo.setTimeDate(cursor.getString(timeDate));
                fileInfo.setTimeLength(cursor.getString(timeLength));
                resultList.add(fileInfo);
        }
        if (cursor != null) {
            cursor.close();
        }
        return resultList;
    }


    /**
     * 查找数据
     * 找不到返回true
     */

    public boolean find(FileInfoStatus fileStatus, String tableName) {
        Cursor cursor = null;
        try {
           cursor = db.query(tableName, null, "userId =? and filePath =?", new String[]{fileStatus.getUserId(), fileStatus.getFilePath()}, null, null, "id desc");

            if (cursor.getCount() <= 0) {
                return true;
            }
            if (cursor != null) {
                cursor.close();
            }
            return false;
        } catch (Exception e) {
            L.w(e);
            if (cursor != null) {
                cursor.close();
            }
            return false;
        }
    }

    /**
     * 查找数据
     */

    public FileInfoStatus findFileInfo(String userId, String fileStatus) {
        Cursor cursor=null;
        try {
            cursor = db.query("qk_file_upload", null, "userId =? and filePath =?", new String[]{userId, fileStatus}, null, null, "id desc");
            FileInfoStatus fileInfo = null;

            while (cursor.moveToNext()) {

                fileInfo = getFileInfo(cursor);

            }
            if (cursor != null) {
                cursor.close();
            }
            return fileInfo;
        } catch (Exception e) {
            L.w(e);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
    }


    /**
     * 增加数据
     */
    public boolean insert(FileInfoStatus fileStatus) {
        if (fileStatus == null) {
            return false;
        }
        if (!find(fileStatus, "qk_file_upload")) {
            L.w("db have filePath : %s", fileStatus.getFilePath());
            return false;
        }

        try {

            String sql = String.format("insert into qk_file_upload ('fileName', 'fileDisplayName','fileGroup', 'filePath','uploadName','fileType','userId','liveId','status','percent','fileLength','timeDate','timeLength','pause') values('%s','%s','%s','%s','%s','%s','%s','%d','%d','%.3f','%s','%s','%s','0')",
                    fileStatus.getFileName(),
                    fileStatus.getFileDisplayName(),
                    fileStatus.getFileGroup(),
                    fileStatus.getFilePath(),
                    fileStatus.getUploadName(),
                    fileStatus.getFileType(),
                    fileStatus.getUserId(),
                    fileStatus.getLiveId(),
                    fileStatus.getStatus(),
                    fileStatus.getPercent(),
                    fileStatus.getFileLength(),
                    fileStatus.getTimeDate(),
                    fileStatus.getTimeLength()
            );
            db.execSQL(sql);
            L.d("insert qk_file_upload ok" + sql);
            return true;
        } catch (Exception e) {
            L.w(e);
            return false;
        }
    }

    public boolean update(FileInfoStatus fileStatus) {
        try {
            String sql = String.format("update qk_file_upload set status='%d',percent='%.3f',uploadName='%s',fileLength='%s' where id='%s'",
                    fileStatus.getStatus(),
                    fileStatus.getPercent(),
                    fileStatus.getUploadName(),
                    fileStatus.getFileLength(),
                    fileStatus.getFileId());
//            L.d("update qk_file_upload =%s" , sql);
            db.execSQL(sql);
//            L.d("update qk_file_upload OK");
            return true;
        } catch (Exception e) {
            L.w(e);
            return false;
        }
    }

    public boolean updatePause(int pause, int fileId) {
        try {
            String sql = String.format("update qk_file_upload set pause='%d' where id='%s'",
                    pause,
                    fileId);

            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            L.w(e);
            return false;
        }
    }

    public int getFileInfoPause(FileInfoStatus fileStatus) {
        int pause = 0;
        Cursor cursor = null;
        try {
            cursor = db.query("qk_file_upload", null, "id =?", new String[]{fileStatus.getFileId() + ""}, null, null, "id desc");
            FileInfoStatus fileInfo = null;
            int indexPause = cursor.getColumnIndex("pause");

            while (cursor.moveToNext()) {

                pause = cursor.getInt(indexPause);

            }
            cursor.close();
            return pause;
        } catch (Exception e) {
            if (cursor!=null) {
                cursor.close();
            }
            L.w(e);
            return pause;
        }
    }

    public boolean delete(String filePath) {
        try {
            String sql = String.format("delete from qk_file_upload where filePath='%s'", filePath);
            db.execSQL(sql);
            L.d("delete %s from qk_file_upload ok", filePath);
            return true;
        } catch (Exception e) {
            L.w(e);
            return false;
        }
    }

    /**
     * 查询数据库,获取对应列表
     */
    public ArrayList<FileInfoStatus> selectAllList(String userId, String FileType) {
        ArrayList<FileInfoStatus> resultList = new ArrayList<>();

        // 查询数据库,获取未完成的记录
        Cursor cursor = null;
        if (db_alluploadedFile.equals(FileType)) {
            cursor = db.query("qk_file_upload", null, "userId =? and status > 1", new String[]{userId}, null, null, "id desc");

        } else if (db_newsFile.equals(FileType)) {
            cursor = db.query("qk_file_upload", null, "userId =? and fileType <> '" + db_takePicture+ "'"+"and"+" fileType <>'"+db_phoneFile+ "'", new String[]{userId}, null, null, "id desc", "0,20");

        } else if (db_needUploadFile.equals(FileType)) {
            cursor = db.query("qk_file_upload", null, "userId =? and status = '1'", new String[]{userId}, null, null, "id desc");
        } else {
            cursor = db.query("qk_file_upload", null, "userId =? and fileType =?", new String[]{userId, FileType}, null, null, "id desc");
        }

        if (cursor.getCount() <= 0) {
            return resultList;
        }
        L.d("Create or Check Table qk_file_upload ok");

        // 数据库的列索引
        int indexFileId = cursor.getColumnIndex("id");
        int indexFileName = cursor.getColumnIndex("fileName");
        int indexFileGroup = cursor.getColumnIndex("fileGroup");
        int indexFileDisplayName = cursor.getColumnIndex("fileDisplayName");
        int indexFilePath = cursor.getColumnIndex("filePath");
        int indexUploadName = cursor.getColumnIndex("uploadName");
        int indexFileType = cursor.getColumnIndex("fileType");
        int indexUserId = cursor.getColumnIndex("userId");
        int status = cursor.getColumnIndex("status");
        int Percent = cursor.getColumnIndex("percent");
        int indexFileLength = cursor.getColumnIndex("fileLength");
        int indexLiveId = cursor.getColumnIndex("liveId");
        int timeDate = cursor.getColumnIndex("timeDate");
        int timeLength = cursor.getColumnIndex("timeLength");


        while (cursor.moveToNext()) {
            String fileName = cursor.getString(indexFilePath);
            File file = new File(fileName);
            if (file.exists()) {
                FileInfoStatus fileInfo = new FileInfoStatus();
                fileInfo.setFileId(cursor.getInt(indexFileId));
                fileInfo.setFileName(cursor.getString(indexFileName));
                fileInfo.setFileGroup(cursor.getString(indexFileGroup));
                fileInfo.setFileDisplayName(cursor.getString(indexFileDisplayName));
                fileInfo.setFilePath(cursor.getString(indexFilePath));
                fileInfo.setUploadName(cursor.getString(indexUploadName));
                fileInfo.setFileType(cursor.getString(indexFileType));
                fileInfo.setUserId(cursor.getString(indexUserId));
                fileInfo.setStatus(cursor.getInt(status));
                L.d("cursor.getInt(Percent)=%s", cursor.getInt(Percent));
                fileInfo.setPercent(cursor.getInt(Percent));
                fileInfo.setFileLength(cursor.getString(indexFileLength));
                fileInfo.setLiveId(cursor.getLong(indexLiveId));
                fileInfo.setTimeDate(cursor.getString(timeDate));
                fileInfo.setTimeLength(cursor.getString(timeLength));


                resultList.add(fileInfo);
            } else {
                L.e("filename is not find:%s", fileName);
                delete(fileName);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return resultList;
    }


    /**
     * 查询数据库,获取对应列表
     */
    public ArrayList<FileInfoStatus> selectFileNeedMerger(String userId, String fileGroup) {
        ArrayList<FileInfoStatus> resultList = new ArrayList<>();

        // 查询数据库,获取未完成的记录
        Cursor cursor = db.query("qk_file_upload", null, "userId =? and fileGroup =?", new String[]{userId, fileGroup}, null, null, "id desc");


        if (cursor.getCount() <= 0) {
            return resultList;
        }
        L.d("Create or Check Table qk_file_upload ok");

        // 数据库的列索引
        int indexFileId = cursor.getColumnIndex("id");
        int indexFileName = cursor.getColumnIndex("fileName");
        int indexFileGroup = cursor.getColumnIndex("fileGroup");
        int indexFileDisplayName = cursor.getColumnIndex("fileDisplayName");
        int indexFilePath = cursor.getColumnIndex("filePath");
        int indexUploadName = cursor.getColumnIndex("uploadName");
        int indexFileType = cursor.getColumnIndex("fileType");
        int indexUserId = cursor.getColumnIndex("userId");
        int indexstatus = cursor.getColumnIndex("status");
        int Percent = cursor.getColumnIndex("percent");
        int indexFileLength = cursor.getColumnIndex("fileLength");
        int indexLiveId = cursor.getColumnIndex("liveId");
        int timeDate = cursor.getColumnIndex("timeDate");
        int timeLength = cursor.getColumnIndex("timeLength");

        while (cursor.moveToNext()) {
            String fileName = cursor.getString(indexFilePath);
            File file = new File(fileName);
            if (file.exists()) {
                int status = cursor.getInt(indexstatus);
            // 有可能原来的状态码是5已经合并完成了,所以这边判断要兼容各种状态
                if (status == 4||status==5) {
                    FileInfoStatus fileInfo = new FileInfoStatus();
                    fileInfo.setFileId(cursor.getInt(indexFileId));
                    fileInfo.setFileName(cursor.getString(indexFileName));
                    fileInfo.setFileGroup(cursor.getString(indexFileGroup));
                    fileInfo.setFileDisplayName(cursor.getString(indexFileDisplayName));
                    fileInfo.setFilePath(cursor.getString(indexFilePath));
                    fileInfo.setUploadName(cursor.getString(indexUploadName));
                    fileInfo.setFileType(cursor.getString(indexFileType));
                    fileInfo.setUserId(cursor.getString(indexUserId));
                    fileInfo.setStatus(cursor.getInt(indexstatus));
                    fileInfo.setPercent(cursor.getInt(Percent));
                    fileInfo.setFileLength(cursor.getString(indexFileLength));
                    fileInfo.setLiveId(cursor.getLong(indexLiveId));
                    fileInfo.setTimeDate(cursor.getString(timeDate));
                    fileInfo.setTimeLength(cursor.getString(timeLength));

                    resultList.add(fileInfo);
                } else {
                    return null;
                }

            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return resultList;
    }

    /**
     * 查询数据库,获取未完成的记录
     */
    public ArrayList<FileInfoStatus> selectNeedNoticeService() {
        ArrayList<FileInfoStatus> resultList = new ArrayList<>();

        // 查询数据库,获取未完成的记录
        Cursor cursor = db.query("qk_file_upload", null, "status = ? or status = ? ", new String[]{"3", "2"}, null, null, "id desc");
        if (cursor.getCount() <= 0) {
            return resultList;
        }
        L.d("Create or Check Table qk_file_upload ok");

        // 数据库的列索引
        int indexFileId = cursor.getColumnIndex("id");
        int indexFileName = cursor.getColumnIndex("fileName");
        int indexFileGroup = cursor.getColumnIndex("fileGroup");
        int indexFileDisplayName = cursor.getColumnIndex("fileDisplayName");

        int indexFilePath = cursor.getColumnIndex("filePath");
        int indexUploadName = cursor.getColumnIndex("uploadName");
        int indexFileType = cursor.getColumnIndex("fileType");
        int indexUserId = cursor.getColumnIndex("userId");
        int status = cursor.getColumnIndex("status");
        int Percent = cursor.getColumnIndex("percent");
        int indexFileLength = cursor.getColumnIndex("fileLength");
        int indexLiveId = cursor.getColumnIndex("liveId");
        int timeDate = cursor.getColumnIndex("timeDate");
        int timeLength = cursor.getColumnIndex("timeLength");
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(indexFilePath);
            File file = new File(fileName);
            if (file.exists()) {
                FileInfoStatus fileInfo = new FileInfoStatus();
                fileInfo.setFileId(cursor.getInt(indexFileId));
                fileInfo.setFileName(cursor.getString(indexFileName));
                fileInfo.setFileGroup(cursor.getString(indexFileGroup));
                fileInfo.setFileDisplayName(cursor.getString(indexFileDisplayName));
                fileInfo.setFilePath(cursor.getString(indexFilePath));
                fileInfo.setUploadName(cursor.getString(indexUploadName));
                fileInfo.setFileType(cursor.getString(indexFileType));
                fileInfo.setUserId(cursor.getString(indexUserId));
                fileInfo.setStatus(cursor.getInt(status));
                fileInfo.setPercent(cursor.getDouble(Percent));
                fileInfo.setFileLength(cursor.getString(indexFileLength));
                fileInfo.setLiveId(cursor.getLong(indexLiveId));
                fileInfo.setTimeDate(cursor.getString(timeDate));
                fileInfo.setTimeLength(cursor.getString(timeLength));
                resultList.add(fileInfo);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return resultList;
    }

    private FileInfoStatus getFileInfo(Cursor cursor) {
        // 数据库的列索引
        int indexFileId = cursor.getColumnIndex("id");
        int indexFileName = cursor.getColumnIndex("fileName");
        int indexFileGroup = cursor.getColumnIndex("fileGroup");
        int indexFileDisplayName = cursor.getColumnIndex("fileDisplayName");
        int indexFilePath = cursor.getColumnIndex("filePath");
        int indexUploadName = cursor.getColumnIndex("uploadName");
        int indexFileType = cursor.getColumnIndex("fileType");
        int indexUserId = cursor.getColumnIndex("userId");
        int status = cursor.getColumnIndex("status");
        int Percent = cursor.getColumnIndex("percent");
        int indexFileLength = cursor.getColumnIndex("fileLength");
        int indexLiveId = cursor.getColumnIndex("liveId");
        int timeDate = cursor.getColumnIndex("timeDate");
        int timeLength = cursor.getColumnIndex("timeLength");

        FileInfoStatus fileInfo = new FileInfoStatus();
        fileInfo.setFileId(cursor.getInt(indexFileId));
        fileInfo.setFileName(cursor.getString(indexFileName));
        fileInfo.setFileGroup(cursor.getString(indexFileGroup));
        fileInfo.setFileDisplayName(cursor.getString(indexFileDisplayName));
        fileInfo.setFilePath(cursor.getString(indexFilePath));
        fileInfo.setUploadName(cursor.getString(indexUploadName));
        fileInfo.setFileType(cursor.getString(indexFileType));
        fileInfo.setUserId(cursor.getString(indexUserId));
        fileInfo.setStatus(cursor.getInt(status));
        fileInfo.setPercent(cursor.getDouble(Percent));
        fileInfo.setFileLength(cursor.getString(indexFileLength));
        fileInfo.setLiveId(cursor.getLong(indexLiveId));
        fileInfo.setTimeDate(cursor.getString(timeDate));
        fileInfo.setTimeLength(cursor.getString(timeLength));
        return fileInfo;

    }


}
