package com.onecivilization.Optimize.CustomView;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by CGZ on 2016/7/12.
 */
public class HideWhileScrollingBehavior extends CoordinatorLayout.Behavior<View> {

    public HideWhileScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyConsumed != 0) {
            child.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        if (child.getVisibility() == View.INVISIBLE) {
            child.setVisibility(View.VISIBLE);
        }
    }
}
