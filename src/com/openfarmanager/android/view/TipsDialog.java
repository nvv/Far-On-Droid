package com.openfarmanager.android.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.TipsPagerAdapter;
import com.openfarmanager.android.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Vlad Namashko
 */
public class TipsDialog extends Dialog {

    private DotPager mPager;
    private ViewPager mViewPager;

    public TipsDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_tips);

        mPager = (DotPager) findViewById(R.id.pager);

        mViewPager = (ViewPager) findViewById(R.id.tips_pager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                mPager.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        LayoutInflater inflater = (LayoutInflater) App.sInstance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup tipsList = (ViewGroup) inflater.inflate(R.layout.tips, null);
        List<View> tips = new ArrayList<View>(tipsList.getChildCount());
        for (int i = 0; i < tipsList.getChildCount(); i++) {
            tips.add(tipsList.getChildAt(i));
        }
        tipsList.removeAllViews();

        mViewPager.setAdapter(new TipsPagerAdapter(tips));
        mPager.initView(tips.size(), 0);

        adjustDialogSize();
    }

    private void adjustDialogSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) App.sInstance.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        int size = Math.min(metrics.widthPixels, metrics.heightPixels);
        size -= 40;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(getWindow().getAttributes());
        params.width = size;
        params.height = size;

        getWindow().setAttributes(params);
    }

}
