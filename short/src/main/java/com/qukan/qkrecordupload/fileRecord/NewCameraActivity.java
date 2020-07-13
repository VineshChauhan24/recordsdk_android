package com.qukan.qkrecordupload.fileRecord;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cgfay.cainfilter.camerarender.ParamsManager;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.fileRecord.record.ScreenRotateUtil;
import com.qukan.qkrecordupload.fileRecord.record.VedioFragment;

public class NewCameraActivity extends AppCompatActivity {

    private VedioFragment vedioFragment;

    private int currentIndex = 1;
    private long exitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParamsManager.closeAudio = false;

        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_camera);
        //保持屏幕常亮
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            //lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            window.setAttributes(lp);
        }
        showVedioFragment();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //保持屏幕常亮
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                window.setAttributes(lp);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", currentIndex);
    }


    private void showVedioFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (vedioFragment == null) {
            vedioFragment = new VedioFragment();
            transaction.add(R.id.fl_main, vedioFragment, "vedio");
        }
        hideFragment(transaction);

        transaction.show(vedioFragment);
        transaction.commit();
    }

    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (vedioFragment != null) {
            transaction.hide(vedioFragment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScreenRotateUtil.getInstance(this).start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreenRotateUtil.getInstance(this).stop();

    }

    public void startSensor() {
        ScreenRotateUtil.getInstance(this).start(this);
    }

    public void stopSensor() {
        ScreenRotateUtil.getInstance(this).stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vedioFragment = null;
        Log.d("NewCameraActivity", "onDestroy");
    }

}
