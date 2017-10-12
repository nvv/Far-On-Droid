package com.openfarmanager.android.view;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.support.annotation.AnimatorRes;
import android.support.annotation.Nullable;
import android.view.View;

import com.futuremind.recyclerviewfastscroll.viewprovider.ViewBehavior;
import com.futuremind.recyclerviewfastscroll.viewprovider.VisibilityAnimationManager;

/**
 * @author Vlad Namashko
 */
public class CustomHandleBehavior implements ViewBehavior {

    private final VisibilityAnimationManager mVisibilityManager;
    private final HandleAnimationManager mGrabManager;

    private boolean mIsGrabbed;

    public CustomHandleBehavior(VisibilityAnimationManager visibilityManager, HandleAnimationManager grabManager) {
        mVisibilityManager = visibilityManager;
        mGrabManager = grabManager;
    }

    @Override
    public void onHandleGrabbed() {
        mIsGrabbed = true;
        mVisibilityManager.show();
        mGrabManager.onGrab();
    }

    @Override
    public void onHandleReleased() {
        mIsGrabbed = false;
        mVisibilityManager.hide();
        mGrabManager.onRelease();
    }

    @Override
    public void onScrollStarted() {
        mVisibilityManager.show();
    }

    @Override
    public void onScrollFinished() {
        if(!mIsGrabbed) mVisibilityManager.hide();
    }

    static class HandleAnimationManager {

        @Nullable
        private AnimatorSet mGrabAnimator;
        @Nullable
        private AnimatorSet mReleaseAnimator;

        protected HandleAnimationManager(View handle, @AnimatorRes int grabAnimator, @AnimatorRes int releaseAnimator) {
            if (grabAnimator != -1) {
                mGrabAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(handle.getContext(), grabAnimator);
                mGrabAnimator.setTarget(handle);
            }
            if (releaseAnimator != -1) {
                mReleaseAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(handle.getContext(), releaseAnimator);
                mReleaseAnimator.setTarget(handle);
            }
        }

        public void onGrab() {
            if (mReleaseAnimator != null) {
                mReleaseAnimator.cancel();
            }
            if (mGrabAnimator != null) {
                mGrabAnimator.start();
            }
        }

        public void onRelease() {
            if (mGrabAnimator != null) {
                mGrabAnimator.cancel();
            }
            if (mReleaseAnimator != null) {
                mReleaseAnimator.start();
            }
        }

        public static class Builder {
            private View handle;
            private int grabAnimator;
            private int releaseAnimator;

            public Builder(View handle) {
                this.handle = handle;
            }

            public Builder withGrabAnimator(@AnimatorRes int grabAnimator) {
                this.grabAnimator = grabAnimator;
                return this;
            }

            public Builder withReleaseAnimator(@AnimatorRes int releaseAnimator) {
                this.releaseAnimator = releaseAnimator;
                return this;
            }

            public HandleAnimationManager build() {
                return new HandleAnimationManager(handle, grabAnimator, releaseAnimator);
            }
        }
    }

}
