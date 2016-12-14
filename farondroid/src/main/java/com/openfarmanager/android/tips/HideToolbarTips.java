package com.openfarmanager.android.tips;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.Settings;

/**
 * @author Vlad Namashko.
 */
public class HideToolbarTips {

    private ShowcaseView mShowCaseView;

    public HideToolbarTips(Activity activity, int offset) {

        Settings settings = App.sInstance.getSettings();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !settings.isShowToolbarTips()) {
            return;
        }

        settings.getSharedPreferences().edit().putBoolean(Settings.SHOW_TOOLBAR_TIPS, false).commit();

        Point point = new Point();
        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        point.set(offset, metrics.heightPixels - offset);

        mShowCaseView = new ShowcaseView.Builder(activity)
                .setTarget(new PointTarget(point))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mShowCaseView.hide();
                    }
                })
                .build();

        mShowCaseView.setStyle(R.style.CustomShowcaseTheme);
        mShowCaseView.setContentTitle(activity.getString(R.string.full_screen_icon));
        mShowCaseView.setContentText(activity.getString(R.string.full_screen_icon_summary));
        mShowCaseView.setButtonText(activity.getString(R.string.btn_ok));
    }
}
