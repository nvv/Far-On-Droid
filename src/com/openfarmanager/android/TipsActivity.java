package com.openfarmanager.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.openfarmanager.android.adapters.TipsPagerAdapter;
import com.openfarmanager.android.view.DotPager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class TipsActivity extends FragmentActivity {

    private DotPager mPager;
    private ViewPager mViewPager;
    private Button mButtonNext;
    private Button mButtonBack;
    private Button mButtonFinish;
    private int mTipsCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_tips);

        mPager = (DotPager) findViewById(R.id.pager);

        mViewPager = (ViewPager) findViewById(R.id.tips_pager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                mPager.setCurrentItem(i);

                mButtonNext.setVisibility(i == (mTipsCount - 1) ? View.GONE : View.VISIBLE);
                mButtonFinish.setVisibility(i == (mTipsCount - 1) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mButtonNext = (Button) findViewById(R.id.btn_next);
        mButtonBack = (Button) findViewById(R.id.btn_back);
        mButtonFinish = (Button) findViewById(R.id.btn_finish);

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            }
        });

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mViewPager.getCurrentItem();
                if (position > 0) {
                    mViewPager.setCurrentItem(position - 1, true);
                }
            }
        });

        mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LayoutInflater inflater = (LayoutInflater) App.sInstance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup tipsList = (ViewGroup) inflater.inflate(R.layout.tips, null);
        List<View> tips = new ArrayList<View>(tipsList.getChildCount());
        for (int i = 0; i < tipsList.getChildCount(); i++) {
            tips.add(tipsList.getChildAt(i));
            mTipsCount++;
        }
        tipsList.removeAllViews();

        mViewPager.setAdapter(new TipsPagerAdapter(tips));
        mPager.initView(tips.size(), 0);
    }

}
