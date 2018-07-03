package com.folio.dooley1001.folio.coinDetails;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/*Layout manager that allows the user to flip left and right through pages of data.
 *https://developer.android.com/reference/android/support/v4/view/ViewPager
 */
public class CustomView extends ViewPager {

    private boolean enabled;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enabled && super.onTouchEvent(event);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return enabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
