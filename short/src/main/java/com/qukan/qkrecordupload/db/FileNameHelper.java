package com.qukan.qkrecordupload.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by Administrator on 2016-04-19.
 */
public class FileNameHelper {
    public static String findKeyEx(String fileName)
    {
        String temp ="";
        File file = new File(fileName);
        if (file.exists())
        {
            String Name = file.getName();
            temp =  Name.substring(0,Name.indexOf("_"));
        }

        return temp;
    }

    public static String findKey(String fileName)
    {
        String temp =  fileName.substring(0,fileName.indexOf("_"));
        return temp;
    }

    public static String getTimeAndRandom()
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String uploadName = fmt.format(new Date()) + random() +"_1";

        return uploadName;
    }

    private static String random() {
        int count = 5;
        char start = '0';
        char end = '9';

        Random rnd = new Random();

        char[] result = new char[count];
        int len = end - start + 1;

        while (count-- > 0) {
            result[count] = (char) (rnd.nextInt(len) + start);
        }

        return new String(result);
    }

    public static ArrayList<String> StringToList(String value)
    {
        ArrayList<String> list = new ArrayList<String>();

        if (value.length() > 2)
        {
            String tempValue = value.substring(1,value.length()-1);
            String [] temp = tempValue.split(",");
            for (String fileName : temp)
            {
                fileName = fileName.trim();
                list.add(fileName);
            }
        }

        return list;
    }
}
