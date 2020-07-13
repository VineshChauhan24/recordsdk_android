package com.qukan.qkrecordupload.fileCut.musicChild;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.qukan.qkrecordupload.bean.MusicRes;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * 音乐工具类,
 */
public class MusicUtils {
    /**
     * 扫描系统里面的音频文件，返回一个list集合
     * 获取本地数据库音频文件
     */
    public static List<MusicRes> getMusicData(Context context) {
        List<MusicRes> list = new ArrayList<>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MusicRes song = new MusicRes();
                String musicName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                if (musicName == null || !musicName.endsWith(".mp3")) {
                    continue;
                }
                song.setName(musicName);
                song.setMusicAuthor(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (song.size > 1000 * 800) {
                    list.add(song);
                }
            }
            // 释放资源
            cursor.close();
        }

        List<MusicRes> dbList = DataSupport.findAll(MusicRes.class);
        for (MusicRes musicRes : dbList) {
            for (MusicRes data : list) {
                if (musicRes.getUrl().equals(data.getUrl())) {
                    data.setPcmPath(musicRes.getPcmPath());
                    break;
                }
            }
        }
        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
}
