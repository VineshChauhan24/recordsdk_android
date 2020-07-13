package com.qukan.qkrecordupload;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//toast重新封装
public class RnToast {
    private static Toast mToast;
    private static TextView tvText;
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context mContext, int resId) {
        showToast(mContext, mContext.getString(resId));
    }

    public static void showToast(Context mContext, String text) {
        showToast(mContext, text, Toast.LENGTH_SHORT);
    }

    public static void showToastLong(Context mContext, String text) {
        showToast(mContext, text, Toast.LENGTH_LONG);
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getString(resId), duration);
    }


    public static void showToast(Context mContext, String text, int duration) {
        showToast(mContext, text, duration, false);
    }


    public static void showToast(Context mContext, int resId, boolean center) {
        showToast(mContext, mContext.getString(resId), center);
    }

    public static void showToast(Context mContext, String text, boolean center) {
        showToast(mContext, text, Toast.LENGTH_SHORT, center);
    }

    public static void showToast(Context mContext, int resId, int duration, boolean center) {
        showToast(mContext, mContext.getString(resId), duration, center);
    }

    public static void showToast(Context mContext, String text, int duration, boolean center) {
        if (mToast != null && tvText != null) {
            tvText.setText(text);
        } else {
            mToast = new Toast(mContext);
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_toast, null);
            tvText = view.findViewById(R.id.tv_text);
            tvText.setText(text);
            mToast.setView(view);
            mToast.setDuration(duration);
            if (center) {
                mToast.setGravity(Gravity.CENTER, 0, 0);
            }
        }

        mToast.show();

    }

    public static void toastUiThread(final Activity activity, final String msg) {
        if (activity != null) {
            ((Activity) activity).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showToast(activity, msg);

                }
            });
        }
    }
}
