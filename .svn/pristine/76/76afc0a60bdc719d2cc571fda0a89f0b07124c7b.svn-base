package com.openfarmanager.android.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * author: Vlad Namashko
 */
public class ExpandPanelAnimation extends Animation {

    private View mView1;
    private View mView2;
    private LinearLayout.LayoutParams mLayoutParams1;
    private LinearLayout.LayoutParams mLayoutParams2;
    private float mWeight1;
    private float mWeight2;
    private float mTargetWeight1;
    private float mTargetWeight2;

    @SuppressWarnings("ConstantConditions")
    public ExpandPanelAnimation(View view1, View view2, float targetWeight1, float targetWeight2) {
        mView1 = view1;
        mView2 = view2;
        mLayoutParams1 = (LinearLayout.LayoutParams) view1.getLayoutParams();
        mLayoutParams2 = (LinearLayout.LayoutParams) view2.getLayoutParams();
        mWeight1 = mLayoutParams1.weight;
        mWeight2 = mLayoutParams2.weight;
        mTargetWeight1 = targetWeight1;
        mTargetWeight2 = targetWeight2;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {

        System.out.println(":::::::::  " + interpolatedTime);

        mLayoutParams1.weight = mWeight1 + interpolatedTime * (mTargetWeight1 - mWeight1);
        mLayoutParams2.weight = mWeight2 + interpolatedTime * (mTargetWeight2 - mWeight2);

        mView1.requestLayout();
        mView2.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
