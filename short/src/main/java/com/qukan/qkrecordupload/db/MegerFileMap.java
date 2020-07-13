package com.qukan.qkrecordupload.db;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-19.
 */
public class MegerFileMap
    //这个的fileName都是上传文件名
{
    private Map<String,FileMergeInfo> filesMap = new HashMap<String,FileMergeInfo>();

    private static MegerFileMap _instance = new MegerFileMap();

    // 返回单例
    public static MegerFileMap instance()
    {
        return _instance;
    }

    public void updateFileMergeInfo()
    {
        synchronized (filesMap)
        {
            filesMap = DBHelper.instance().selectUnfinishedMap();
        }
    }

    public void addFileMergeInfo(ArrayList<String> fileList)
    {
        if (fileList.isEmpty())
        {
            return;
        }

        String fileName = fileList.get(0);
        String key = FileNameHelper.findKey(fileName);

        synchronized (filesMap)
        {
            if(filesMap.containsKey(key))
            {
                FileMergeInfo info = filesMap.get(key);
                if (!info.getMerge())
                {
                    info.getFileList().addAll(fileList);
                    //写入数据库
                    DBHelper.instance().insertMap(info);

                    return;
                }
                else
                //如果是已经合并的则删除
                {
                    filesMap.remove(key);
                }
            }

            //新加合并列表
            {
                FileMergeInfo info = new FileMergeInfo();
                info.setKey(key);
                info.setMerge(false);
                info.setSendMerge(false);
                info.setFileList(fileList);

                filesMap.put(key, info);

                //写入数据库
                DBHelper.instance().insertMap(info);
            }
        }
    }

    public ArrayList<String> checkFileMergeInfo(String fileName)
    {
        String key = FileNameHelper.findKey(fileName);

        ArrayList<String> result = new ArrayList<String>();;

        synchronized (filesMap)
        {
            if(!filesMap.containsKey(key))
            {

                result.add(fileName);
                return result;
            }

            FileMergeInfo info = filesMap.get(key);

            if (!info.getKey().equals(key))
                //一般不太可能会出现
            {
                //QLog.w("filesMap the key is error : %s , %s",info.getKey(),key);
                return result;
            }

            if (info.getMerge())
            {
                //QLog.w("fileName is merger : %s ",key);
                return result;
            }

            ArrayList<String> fileList = info.getFileList();
            ArrayList<String> mergeList = info.getUploadList();
            for (String file : fileList)
            {
                if (file.equals(fileName))
                {
                    fileList.remove(file);
                    mergeList.add(file);
                    break;
                }
            }

            //QLog.i("fileName : %s",fileName);
            //QLog.i("FileMergeInfo :%s",info.toString());
            if (fileList.isEmpty() && !info.getSendMerge())
            {
                result.addAll(mergeList);
                //QLog.d("1 -- result : %s", result.toString());
                Collections.sort(result);
                //QLog.d("2 -- result : %s", result.toString());
                info.setSendMerge(true);
            }

            DBHelper.instance().updateMap(info);

            return result;
        }
    }

    public void setMergeType(String fileName)
    {
        String key = FileNameHelper.findKey(fileName);

        synchronized (filesMap)
        {
            if(!filesMap.containsKey(key))
            {
                //QLog.w("filesMap not find the key : %s", key);
                return ;
            }

            FileMergeInfo info = filesMap.get(key);

            if (!info.getKey().equals(key))
            //一般不太可能会出现
            {
                //QLog.w("filesMap the key is error : %s , %s",info.getKey(),key);
                return ;
            }

            if (info.getFileList().isEmpty())
            {
                info.setMerge(true);
                info.setSendMerge(false);

                DBHelper.instance().deleteMap(info);
            }
        }
    }
}
