package com.qukan.qkrecordupload;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * Created by Administrator on 2015/7/30 0030.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentTransaction mFragmentTransaction;//fragment事务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppManager.getInstance().addActivity(this);
        Log.d(this.getClass().getName(), "onCreate()");
        // 创建后需要做的事情
        onPostCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public View getRootView() {
        return findViewById(android.R.id.content).getRootView();
    }


    private final int COUNT_START = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, COUNT_START, 2, "exit");
        menu.add(0, COUNT_START + 1, 2, "about");
        return super.onCreateOptionsMenu(menu);
    }

    protected abstract void onPostCreate();

    // 关闭activity
    public void close() {
        AppManager.getInstance().finishActivity(this);
    }

    public void close(Class cls) {
        AppManager.getInstance().finishSingleActivityByClass(cls);
    }


    public void closeInput(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setFragmentVisible(boolean isVisible, Fragment... fragments) {
        //开启事务
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!isVisible) {
            for (Fragment fragment : fragments){
                mFragmentTransaction.hide(fragment);
            }
        } else {
            for (Fragment fragment : fragments){
                mFragmentTransaction.show(fragment);
            }
        }
        //提交事务
        mFragmentTransaction.commit();
    }

}
