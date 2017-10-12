package com.openfarmanager.android.view;


import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.viewprovider.DefaultBubbleBehavior;
import com.futuremind.recyclerviewfastscroll.viewprovider.ScrollerViewProvider;
import com.futuremind.recyclerviewfastscroll.Utils;
import com.futuremind.recyclerviewfastscroll.viewprovider.ViewBehavior;
import com.futuremind.recyclerviewfastscroll.viewprovider.VisibilityAnimationManager;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

/**
 * @author Vlad Namashko
 */
public class CustomScrollerViewProvider extends ScrollerViewProvider {

    private TextView mBubble;
    private View mHandle;

    @Override
    public View provideHandleView(ViewGroup container) {
        mHandle = new View(getContext());
        int dimen = getContext().getResources().getDimensionPixelSize(R.dimen.fast_navigation_handle_size);
        mHandle.setLayoutParams(new ViewGroup.LayoutParams(dimen, dimen));
        Utils.setBackground(mHandle, drawCircle(dimen, dimen, App.sInstance.getSettings().getSecondaryColor()));
        mHandle.setVisibility(View.INVISIBLE);
        return mHandle;
    }

    @Override
    public View provideBubbleView(ViewGroup container) {
        mBubble = new TextView(getContext());
        int dimen = getContext().getResources().getDimensionPixelSize(R.dimen.fast_navigation_bubble_size);
        mBubble.setLayoutParams(new ViewGroup.LayoutParams(dimen, dimen));
        Utils.setBackground(mBubble, drawCircle(dimen, dimen, App.sInstance.getSettings().getSecondaryColor()));
        mBubble.setVisibility(View.INVISIBLE);
        mBubble.setGravity(Gravity.CENTER);
        mBubble.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        return mBubble;
    }

    @Override
    public TextView provideBubbleTextView() {
        return mBubble;
    }

    @Override
    public int getBubbleOffset() {
        return (int) (getScroller().isVertical() ? (float) mHandle.getHeight() / 2f - (float) mBubble.getHeight() / 2f : (float) mHandle.getWidth() / 2f - (float) mBubble.getWidth() / 2);
    }

    @Override
    protected ViewBehavior provideHandleBehavior() {
        return new CustomHandleBehavior(
                new VisibilityAnimationManager.Builder(mHandle)
                        .withHideDelay(2000)
                        .build(),
                new CustomHandleBehavior.HandleAnimationManager.Builder(mHandle)
                        .withGrabAnimator(R.animator.custom_grab)
                        .withReleaseAnimator(R.animator.custom_release)
                        .build()
        );
    }

    @Override
    protected ViewBehavior provideBubbleBehavior() {
        return new DefaultBubbleBehavior(new VisibilityAnimationManager.Builder(mBubble).withHideDelay(0).build());
    }

    private static ShapeDrawable drawCircle(int width, int height, int color) {
        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setIntrinsicHeight(height);
        oval.setIntrinsicWidth(width);
        oval.getPaint().setColor(color);
        return oval;
    }

}
