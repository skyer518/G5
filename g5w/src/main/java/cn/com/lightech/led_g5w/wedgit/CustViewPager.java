package cn.com.lightech.led_g5w.wedgit;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 明 on 2016/3/21.
 * 禁止左右滑动，只能通过 tab 切换
 */
public class CustViewPager extends ViewPager {


    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    private boolean canScroll;

    public CustViewPager(Context context) {
        super(context);
    }

    public CustViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.canScroll && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.canScroll && super.onInterceptTouchEvent(event);
    }
}
