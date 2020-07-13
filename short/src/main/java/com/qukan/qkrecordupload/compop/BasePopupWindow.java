package com.qukan.qkrecordupload.compop;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.qukan.qkrecordupload.R;

import org.droidparts.Injector;
import org.droidparts.bus.EventBus;

/**
 * Created by Administrator on 2015/7/9 0009.
 */
public abstract class BasePopupWindow extends PopupWindow implements View.OnClickListener
{

    private Context context;


    public BasePopupWindow(Context context)
    {

        this.context = context;
        onInit();
    }

    public Context getContext()
    {
        return this.context;
    }

    private void onInit()
    {

        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        View inflate = setPopview();
        setContentView(inflate);
        setBackgroundDrawable(new BitmapDrawable());
        Injector.inject(getContentView(), this);
        EventBus.unregisterAnnotatedReceiver(this);
        EventBus.registerAnnotatedReceiver(this);


        setAnimationStyle(R.style.PopupAnimation2);
    }

    protected BasePopupWindow()
    {

    }

    public abstract View setPopview();


}
