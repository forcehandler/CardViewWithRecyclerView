package com.example.abhiraj.cardviewwithrecyclerview;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Abhiraj on 28-02-2017.
 */

public class FABScrollBehavior extends FloatingActionButton.Behavior {

    private static final String TAG = FABScrollBehavior.class.getSimpleName();
    public FABScrollBehavior(Context context, AttributeSet attributeSet){
        super();
    }
        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            if(dyConsumed > 0 && child.getVisibility() == View.VISIBLE){
                Log.d(TAG, "dy > 0 and child is visible");
                child.hide();
            } else if(dyConsumed < 0 && child.getVisibility() == View.GONE){
                Log.d(TAG, "dy < 0 and child is invisible");
                child.show();
            }
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
        }

}
