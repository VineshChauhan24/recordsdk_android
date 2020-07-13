package com.qukan.qkrecordupload.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 包含listview的viewpager
 *
 * @author fyxjsj
 */
public class ListViewViewPager extends ViewPager
{

    private float x;
    private float y;

    public ListViewViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ListViewViewPager(Context context)
    {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x = ev.getX();
                y = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - x) > Math.abs(ev.getY() - y))
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                else
                {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
