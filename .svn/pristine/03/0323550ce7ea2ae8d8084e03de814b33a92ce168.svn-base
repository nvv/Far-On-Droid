package com.openfarmanager.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.openfarmanager.android.R;

import java.util.ArrayList;

/**
 * author: Vlad Namashko
 */
public class DotPager extends LinearLayout {

    private ArrayList<ImageView> mDots;

    public DotPager(Context context) {
        super(context);
    }

    public DotPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initView(int count, int current) {
        removeAllViews();
        mDots = new ArrayList<ImageView>();

        if (count == 0) {
            return;
        }
        LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.width = LayoutParams.WRAP_CONTENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.leftMargin = 2;
        lp.rightMargin = 2;
        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(i == current ? R.drawable.dot_selected : R.drawable.dot_unselected);
            iv.setLayoutParams(lp);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            mDots.add(iv);
            addView(iv);
        }
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.HORIZONTAL);

        requestLayout();
    }

    public void setCurrentItem(int current) {
        if (mDots == null || mDots.size() == 0 || current >= mDots.size()) {
            return;
        }
        int dotsCount = mDots.size();

        int childCount = getChildCount();
        if (childCount == 0 || childCount < current) {
            return;
        }

        for (int i = 0; i < dotsCount; i++) {
            ImageView iv = mDots.get(i);
            iv.setImageResource(i == current ? R.drawable.dot_selected : R.drawable.dot_unselected);
        }
    }

}