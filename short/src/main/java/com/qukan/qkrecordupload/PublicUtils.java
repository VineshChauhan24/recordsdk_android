package com.qukan.qkrecordupload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.droidparts.util.L;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PublicUtils {
    public static boolean isEmptyString(String str) {
        return (null == str || str.isEmpty());
    }

    private static Context context_ = QkApplication.getContext();

    /**
     * 高精度的获取当前时间戳的方法
     */
    public static long currentTimeMillis() {
        long currNano = System.nanoTime();
        return currNano / 1000000L;
    }
    public static boolean isVideo(String path) {
        if (path.endsWith("mp4") || path.endsWith("MP4")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isImage(String path) {
        if (path.endsWith("jpg") || path.endsWith("JPG") || path.endsWith("png") || path.endsWith("PNG")|| path.endsWith("jpeg") || path.endsWith("JPEG")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isImagePath(String path) {
        if (path.endsWith("yuv") || path.endsWith("YUV")) {
            return true;
        } else {
            return false;
        }
    }

    public static int getVideoWidth(String path) {
        MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        mmr.setDataSource(path);
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        return Integer.valueOf(width);
    }

    public static int getVideoHeight(String path) {
        MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        mmr.setDataSource(path);
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        return Integer.valueOf(height);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static int px2dip(float pxValue) {
        final float scale = context_.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String formatTime(long dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date(dateTime);
        return sdf.format(date);
    }

    public static String formatTime2(long dateTime) {
        //yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(dateTime);
        return sdf.format(date);
    }

    public static String getLocationTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    public static String random() {
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

    /**
     * 获取屏幕的宽度
     *
     * @return
     */
    public static DisplayMetrics getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取不包含虚拟键的屏幕尺寸
     *
     * @return
     */
    public static Pair<Integer, Integer> getNoVirtualSize(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return new Pair<Integer, Integer>(display.getWidth(), display.getHeight());
    }

    public static void hideSoftKeyBoard(Context context, EditText edt) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    /**
     * 设置全屏和非全屏
     *
     * @param activity
     * @param enable
     */
    public static void full(Activity activity, boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attr);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 格式化时间
     *
     * @param nTime
     * @param replace
     * @return
     */
    public static String getTimerStr(int nTime, String replace) {
        String returnValue = "00" + replace + "00" + replace + "00";
        int nMin = nTime / 60;
        int nSec = nTime % 60;
        if (nMin >= 60) {
            int nHour = nMin / 60;
            nMin = nMin % 60;
            returnValue = String.format("%02d", nHour) + replace + String.format("%02d", nMin) + replace
                    + String.format("%02d", nSec);
        } else {
            returnValue = String.format("%02d", 0) + replace + String.format("%02d", nMin) + replace
                    + String.format("%02d", nSec);
        }
        return returnValue;
    }

    public static String msToHms(long milliSecondTime) {

        int hour = (int) milliSecondTime / (60 * 60 * 1000);
        int minute = (int) (milliSecondTime - hour * 60 * 60 * 1000) / (60 * 1000);
        int seconds = (int) (milliSecondTime - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;

        if (seconds >= 60) {
            seconds = seconds % 60;
            minute += seconds / 60;
        }
        if (minute >= 60) {
            minute = minute % 60;
            hour += minute / 60;
        }

        String sh = "";
        String sm = "";
        String ss = "";
        if (hour < 10) {
            sh = "0" + String.valueOf(hour);
        } else {
            sh = String.valueOf(hour);
        }
        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (seconds < 10) {
            ss = "0" + String.valueOf(seconds);
        } else {
            ss = String.valueOf(seconds);
        }

        return sh + ":" + sm + ":" + ss;
    }

    /**
     * 休息毫秒数
     *
     * @param milliSecond
     */
    public static void sleep(int milliSecond) {
        try {
            Thread.sleep(milliSecond);
        } catch (InterruptedException e) {
            L.d(e);
        }
    }

    public static long getSDFreeSize(String path) {
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();

        return (freeBlocks * blockSize) / 1024 / 1024; // ��λMB
    }

    public static long getSDAllSize(String path) {
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long allBlocks = sf.getBlockCount();

        return (allBlocks * blockSize) / 1024 / 1024; // ��λMB
    }

    public static String[] getVolumePaths(Context activity) {
        Class<?>[] paramTypes = {};
        Object[] params = {};
        String[] paths = {};
        StorageManager sm = (StorageManager) activity.getSystemService(Context.STORAGE_SERVICE);
        try {
            paths = (String[]) sm.getClass().getMethod("getVolumePaths", paramTypes).invoke(sm, params);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * 获取趣看tool的版本类型 true: market版本, false dev版本
     *
     * @param context
     * @return
     */
    public static boolean getQukanToolVersionFlag(Context context) {
        Bundle metaData = null;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                return metaData.getBoolean("qukantool_version_market");
            }
        } catch (NameNotFoundException e) {
            L.e(e);
        }
        return false;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static int dip2px(float dpValue) {
        if (context_ == null) {
            L.d("context_ == nullcontext_ == nullcontext_ == nullcontext_ == null");
        }
        final float scale = context_.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static String convertTime(float milliseconds, int time_format) {
        String format;
        if (time_format == 0) {
            format = "HH:mm:ss.SSS";
        } else {
            format = "HH:mm:ss";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return sdf.format(milliseconds);
    }

    public static String convertTime2(float milliseconds) {
        String format;
        format = "mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return sdf.format(milliseconds);
    }

    public static boolean isVideoValid(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (TextUtil.isEmpty(duration)) {
            return false;
        } else {
            return true;
        }
    }

    public static String getVideoTime(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (TextUtil.isEmpty(duration)) {
            return null;
        }
        int time = Integer.valueOf(duration);
        return PublicUtils.convertTime(time, 1);
    }

    public static long getVideoLenght(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (TextUtil.isEmpty(duration)) {
            return 0;
        }
        return Long.valueOf(duration);
    }

    public static int getVideoDuration(String path) {
        Log.d("getVideoDuration","path:" + path);
        if(PublicUtils.isImage(path)){
            return 20000;
        }
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (TextUtil.isEmpty(duration)) {
            return 0;
        }
        int time = Integer.valueOf(duration);
        return time;
    }

    // 是否是纯数字
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean checkPhoneNub(String nub) {
        if (TextUtil.isEmpty(nub)) {
            return false;
        }
        if (!nub.startsWith("1")) {
            return false;
        } else if (nub.length() != 11) {
            return false;
        } else {
            return true;
        }
    }

    public static String getIp(Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(context, "未开启wifi", Toast.LENGTH_LONG).show();
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        Log.d("getIp--ip:", ip);
        return ip;
    }

    public static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    // 通知系统有文件更新，媒体数据库与文件管理器保持数据统一，不然会出现刚录制的视频文件不会出现在文件管理器中
    public static void updateFile(File file, Context context) {
        if (file != null && file.exists()) {
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(file));
            context.sendBroadcast(scanIntent);
        }
    }

}
