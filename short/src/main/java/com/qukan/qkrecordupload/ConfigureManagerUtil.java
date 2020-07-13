package com.qukan.qkrecordupload;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.RawRes;


/**
 * 缓存配置
 *
 * @author fyxjsj
 */
public class ConfigureManagerUtil {

    /**
     * ********************************* 直播配置相关 ********************************
     */


    // 直播页面横屏拍摄提示
    private static final String LIVE_SCREEN_ORIENTATION = "LIVE_SCREEN_ORIENTATION";
    private static final String ISREMIND = "ISREMIND";
    private static final String DEVICE_STANDAR_PREFERENCE = "DEVICE_STANDAR_PREFERENCE";

    // 视频的流类型
    private static final String VIDEO_STREAM_TYPE = "VIDEO_STREAM_TYPE.150718";

    private static final String UUID_RECORDING_PREFERENCE = "UUID_RECORDING_PREFERENCE";
    private static final String UUID_RECORDING = "UUID_RECORDING";

    private static final String UUID_SD_PATH = "UUID_SD_PATH";
    private static final String UUID_SD_CARD = "UUID_SD_CARD";

    private static final String UUID_SD_FREESIZE = "UUID_SD_FREESIZE";

    private static final String ORIENTATION_SWITCH = "ORIENTATION_SWITCH";

    private static final String CAMERA_SWITCH = "CAMERA_SWITCH";

    private static final String AUDIO_SEEK = "AUDIO_SEEK";

    private static final String LOGO_SWITCH = "LOGO_SWITCH";

    private static final String VIDEO_SWITCH = "VIDEO_SWITCH";
    private static final String RECORD_CAMERA_SWITCH = "RECORD_CAMERA_SWITCH";

    public static String getLoginAccount(Context context) {
        // 获取持久化存储
        SharedPreferences spf = context.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        return spf.getString("account", "");
    }

    public static String getLoginPwd(Context context) {
        // 获取持久化存储
        SharedPreferences spf = context.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        return spf.getString("pwd", "");
    }

    public static boolean putLiveCodeUser(Context context, String account) {
        // 获取持久化存储
        SharedPreferences spf = context.getSharedPreferences("live_code_info", Activity.MODE_PRIVATE);
        Editor edit = spf.edit();
        return edit.putString("account", account).commit();
    }

    public static String getLiveCodeUser(Context context) {
        // 获取持久化存储
        SharedPreferences spf = context.getSharedPreferences("live_code_info", Activity.MODE_PRIVATE);
        return spf.getString("account", "");
    }

    public static boolean saveLoginInfo(Context context, String account, String pwd) {
        // 获取持久化存储
        SharedPreferences spf = context.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        Editor edit = spf.edit();
        return edit.putString("account", account).putString("pwd", pwd).commit();
    }

    public static String getLoginAppKey(Context context) {
        // 获取持久化存储
        SharedPreferences spf = context.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        return spf.getString("appkey", "");
    }

    public static boolean saveLoginAppKey(Context context, String appkey) {

        SharedPreferences spf = context.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        Editor edit = spf.edit();
        return edit.putString("appkey", appkey).commit();
    }

//    在这里存储选择的路线

    public static boolean saveCDNinfo(Context context, String selected) {

        SharedPreferences spf = context.getSharedPreferences("cdn_info", Activity.MODE_PRIVATE);
        Editor edit = spf.edit();
        return edit.putString("selectedLine", selected).commit();
    }

    public static String getCDNinfo(Context context) {

        SharedPreferences spf = context.getSharedPreferences("cdn_info", Activity.MODE_PRIVATE);
        return spf.getString("selectedLine", "自动选择");
    }

    // 保存是否已经弹出过gps提示对话框了
    public static void saveShowGpsTip(Context context, boolean isShow) {
        SharedPreferences spf = context.getSharedPreferences("GpsTip", Activity.MODE_PRIVATE);
        Editor edit = spf.edit();
        edit.putBoolean("ShowGpsTip", isShow).apply();
    }

    public static boolean getShowGpsTip(Context context) {
        SharedPreferences spf = context.getSharedPreferences("GpsTip", Activity.MODE_PRIVATE);
        return spf.getBoolean("ShowGpsTip", true);
    }


    /**
     * 清除密码
     *
     * @param context
     * @return
     */
    public static boolean clearPwd(Context context) {
        // 发送登出日志

        // 获取持久化存储
        SharedPreferences spf = context.getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        Editor edit = spf.edit();
        return edit.putString("pwd", "").commit();
    }

    public static boolean putLiveScreenOrientation(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LIVE_SCREEN_ORIENTATION, 0);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(ISREMIND, value);
        return editor.commit();
    }

    // 直播视频的分辨率
    public static boolean putVideoCameraSize(Context context, int iVideoCameraSize) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("video_camera_size", iVideoCameraSize);
        return editor.commit();
    }



    // 录播视频的分辨率
    public static boolean putRecordCameraSize(Context context, int iVideoCameraSize) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("record_camera_size", iVideoCameraSize);
        return editor.commit();
    }




    // 直播视频的码率
    public static boolean putVideoBitRate(Context ctx, int videoBitRate) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("video_bitrate", videoBitRate);
        return editor.commit();
    }

    public static int getVideoBitRate(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);
        return sharedPreferences.getInt("video_bitrate", 700);
    }

    // 录播视频的码率
    public static boolean putRecordBitRate(Context ctx, int videoBitRate) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("record_bitrate", videoBitRate);
        return editor.commit();
    }

    public static int getRecordBitRate(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);
        return sharedPreferences.getInt("record_bitrate", 500);
    }


    public static int getVideoFrameRate(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);

        // 默认帧率10
        return sharedPreferences.getInt("video_framerate", 10);
    }

    // 直播视频的帧率
    public static boolean putVideoFrameRate(Context ctx, int videoFrameRate) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("video_framerate", videoFrameRate);
        return editor.commit();
    }


    // 录播视频的帧率
    public static boolean putRecordFrameRate(Context ctx, int videoFrameRate) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("record_frameRate", videoFrameRate);
        return editor.commit();
    }

    public static int getRecordFrameRate(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);

        // 默认帧率10
        return sharedPreferences.getInt("record_frameRate", 10);
    }

    public static boolean putRecordDuration(Context ctx, int durationTime) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("RecordDuration", durationTime);
        return editor.commit();
    }

    public static int getRecordDuration(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", 0);
        return sharedPreferences.getInt("RecordDuration", 600);
    }


    // 提醒使用横屏
    public static boolean getLiveScreenOrientation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LIVE_SCREEN_ORIENTATION, 0);
        return sharedPreferences.getBoolean(ISREMIND, true);
    }

    // 录像开关
    public static boolean putRecordingFlag(Context context, boolean bRecordingFlag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UUID_RECORDING_PREFERENCE, 0);
        Editor editor = sharedPreferences.edit();

        // 设置录像开关
        editor.putBoolean(UUID_RECORDING, bRecordingFlag);

        return editor.commit();
    }

    public static boolean getRecordingFlag(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UUID_RECORDING_PREFERENCE, 0);
        return sharedPreferences.getBoolean(UUID_RECORDING, false);
    }

    // wifi下自动上传
    public static boolean putWifiUploadFlag(Context context, boolean bWifiUploadFlag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wifi upload", 0);
        Editor editor = sharedPreferences.edit();

        editor.putBoolean("is wifi upload", bWifiUploadFlag);

        return editor.commit();
    }

    public static boolean getWifiUploadFlag(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wifi upload", 0);
        return sharedPreferences.getBoolean("is wifi upload", true);
    }

    // 直播截图即时同步,默认打开
    public static boolean putSyncCaptureFlag(Context context, boolean SyncCaptureFlag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("capture picture", 0);
        Editor editor = sharedPreferences.edit();

        editor.putBoolean("picture upload sync", SyncCaptureFlag);

        return editor.commit();
    }

    public static boolean getSyncCaptureFlag(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("capture picture", 0);
        return sharedPreferences.getBoolean("picture upload sync", true);
    }

    // 直播录像自动同步
    public static boolean putSyncLiveVideoFlag(Context context, boolean liveVideoUploadFlag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("video live1", 0);
        Editor editor = sharedPreferences.edit();

        editor.putBoolean("live1 video upload sync", liveVideoUploadFlag);

        return editor.commit();
    }

    public static boolean getSyncLiveVideoFlag(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("video live1", 0);
        return sharedPreferences.getBoolean("live1 video upload sync", false);
    }


    // 设置选择的SD卡路径
    public static boolean putSDCardPath(Context context, String sSdPath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UUID_SD_PATH, 0);
        Editor editor = sharedPreferences.edit();
        editor.putString(UUID_SD_CARD, sSdPath);
        return editor.commit();
    }

    public static String getSdCardPath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UUID_SD_PATH, 0);
        return sharedPreferences.getString(UUID_SD_CARD, "");
    }

    public static boolean putSDFreeSize(Context context, String freeSize) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UUID_SD_PATH, 0);
        Editor editor = sharedPreferences.edit();
        editor.putString(UUID_SD_FREESIZE, freeSize);
        return editor.commit();
    }

    public static String getSDFreeSize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UUID_SD_PATH, 0);
        return sharedPreferences.getString(UUID_SD_FREESIZE, "");
    }

    public static boolean removeRecordKey(Context context, String preference) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
        Editor editor = sharedPreferences.edit();
        editor.remove(preference);
        return editor.commit();
    }

    public static boolean putOrientation(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ORIENTATION_SWITCH, 0);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("orientation", value);
        return editor.commit();
    }

    // true为横屏 false为竖屏
    public static boolean getOrientation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ORIENTATION_SWITCH, 0);
        return sharedPreferences.getBoolean("orientation", true);
    }

    public static boolean putCamera(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CAMERA_SWITCH, 0);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("camera", value);
        return editor.commit();
    }

    public static boolean getCamera(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CAMERA_SWITCH, 0);
        return sharedPreferences.getBoolean("camera", true);
    }

    public static boolean putRecordCamera(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(RECORD_CAMERA_SWITCH, 0);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("RecordCamera", value);
        return editor.commit();
    }

    public static boolean getRecordCamera(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(RECORD_CAMERA_SWITCH, 0);
        return sharedPreferences.getBoolean("RecordCamera", true);
    }

    public static boolean putAudio(Context context, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AUDIO_SEEK, 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("audio", value);
        return editor.commit();
    }

    public static int getAudio(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AUDIO_SEEK, 0);
        return sharedPreferences.getInt("audio", 100);
    }

    public static boolean putLogo(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGO_SWITCH, 0);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("logo", value);
        return editor.commit();
    }

    public static boolean getLogo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGO_SWITCH, 0);
        return sharedPreferences.getBoolean("logo", true);
    }

    public static boolean putRecordLogo(Context context, @DrawableRes int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGO_SWITCH, 0);
        Editor editor = sharedPreferences.edit();
        editor.putInt("recordLogo", value);
        return editor.commit();
    }

    public static int getRecordLogo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGO_SWITCH, 0);
        return sharedPreferences.getInt("recordLogo", 0);
    }

    // 保存设置界面视频转推图片的路径
    public static boolean putPortraitPic(Context context, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PortraitPic", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("portraitPic", path);
        return editor.commit();
    }

    public static String getPortraitPic(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PortraitPic", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("portraitPic", "");
    }

    public static boolean putLandscapePic(Context context, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LandscapePic", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("landscapePic", path);
        return editor.commit();
    }

    public static String getLandscapePic(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LandscapePic", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("landscapePic", "");
    }

    //保存session_token,原先采用全局变量保存,容易被gc清空
    public static boolean putSession_token(Context context, String Session_token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Session", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Session_token", Session_token);
        return editor.commit();
    }

    public static String getSession_token(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Session", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("Session_token", "");
    }


    public static boolean putVideoStabilization(Context context, boolean isVideoStabilization) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("VideoStabilization", isVideoStabilization);
        return editor.commit();
    }

    public static boolean getVideoStabilization(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("VideoStabilization", false);
    }

    // 闪屏页跳转链接
    public static boolean putSplashLink(Context context, String splashLink) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Session", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("splashLink", splashLink);
        return editor.commit();
    }

    public static String getSplashLink(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Session", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("splashLink", "");
    }

    // 闪屏图片网址
    public static boolean putSplashPath(Context context, String splashPath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Session", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("splashPath", splashPath);
        return editor.commit();
    }

    public static String getSplashPath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Session", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("splashPath", "");
    }

    // 闪屏页链接的打开方式
    public static boolean putSplashOpenType(Context context, boolean isOPenInApp) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("isOPenInApp", isOPenInApp);
        return editor.commit();
    }

    public static boolean getSplashOpenType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isOPenInApp", true);
    }

    public static boolean saveTTLoginInfo(Context context, String userName, String accessToken, String clientKey, long openId) {
        // 获取持久化存储
        SharedPreferences spf = context.getSharedPreferences("tou_tiao_login_info", Activity.MODE_PRIVATE);
        Editor edit = spf.edit();
        return edit.putString("userName", userName).putString("accessToken", accessToken).putString("clientKey", clientKey).putLong("openId", openId).commit();
    }

    public static String getTTuserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("tou_tiao_login_info", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("userName", "");
    }

    public static String getTTaccessToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("tou_tiao_login_info", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }

    public static String getTTclientKey(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("tou_tiao_login_info", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("clientKey", "");
    }

    public static long getTTopenId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("tou_tiao_login_info", Activity.MODE_PRIVATE);
        return sharedPreferences.getLong("openId", 0);
    }

    // 显示录制时间水印
    public static boolean getShowRecordTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("ShowRecordTime", false);
    }

    public static boolean putShowRecordTime(Context context, boolean showRecordTime) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("ShowRecordTime", showRecordTime);
        return editor.commit();
    }

    // 显示录制位置水印
    public static boolean getShowRecordLocation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("ShowRecordLocation", false);
    }

    public static boolean putShowRecordLocation(Context context, boolean showRecordLocation) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("ShowRecordLocation", showRecordLocation);
        return editor.commit();
    }

    // 显示直播时间水印
    public static boolean getShowLiveTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("ShowLiveTime", false);
    }

    public static boolean putShowLiveTime(Context context, boolean showLiveTime) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("ShowLiveTime", showLiveTime);
        return editor.commit();
    }

    // 显示直播位置水印
    public static boolean getShowLiveLocation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("ShowLiveLocation", false);
    }

    public static boolean putShowLiveLocation(Context context, boolean showLiveLocation) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("ShowLiveLocation", showLiveLocation);
        return editor.commit();
    }

    // 是否打开片尾开关
    public static boolean getVideoEnd(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("VideoEditEnd", false);
    }

    public static boolean putVideoEnd(Context context, boolean videoEnd) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("VideoEditEnd", videoEnd);
        return editor.commit();
    }


    public static boolean putPublishCompany(Context context, String publishCompany) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("PublishCompany", publishCompany);
        return editor.commit();
    }
    // 编辑人员

    public static String getVideoEditor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("VideoEditor", "徐小龙");
    }

    public static boolean putVideoEditor(Context context, String videoEditor) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("VideoEditor", videoEditor);
        return editor.commit();
    }



    public static boolean putCopyright(Context context, String copyright) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Copyright", copyright);
        return editor.commit();
    }



    public static boolean putTrackingReport(Context context, String trackingReport) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("TrackingReport", trackingReport);
        return editor.commit();
    }

    // 判断app是否是从后台切换回来的
    public static boolean putAppState(Context ctx, boolean inBack)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("AppState", inBack);
        return editor.commit();
    }

    public static boolean getAppState(Context ctx)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("AppState", true);
    }

    // 每次都+1
    public static long getVideoFlag(Context ctx)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        long videoFlag = sharedPreferences.getLong("VideoFlag", 0);
        Editor editor = sharedPreferences.edit();
        editor.putLong("VideoFlag", videoFlag + 1);
        editor.commit();
        return videoFlag;
    }

    // 权限是否获取保存
    public static boolean saveFilePermission(Context ctx, boolean isFileGranted)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("filePermission", isFileGranted);
        return editor.commit();
    }

    public static boolean saveLocationPermission(Context ctx, boolean isLocationGranted)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("locationPermission", isLocationGranted);
        return editor.commit();
    }

    public static boolean getFilePermission(Context ctx)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        boolean filePermission = sharedPreferences.getBoolean("filePermission", false);
        return filePermission;
    }

    public static boolean getLocationPermission(Context ctx)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        boolean locationPermission = sharedPreferences.getBoolean("locationPermission", false);
        return locationPermission;
    }

}
