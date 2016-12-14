package com.openfarmanager.android.fragments;

import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.openfarmanager.android.App;
import com.openfarmanager.android.utils.SystemUtils;

/**
 * Base Dialog Fragment.
 *
 * @author Vlad Namashko
 */
public class BaseDialog extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        adjustDialogSize();
    }

    /**
     * Adjust dialog size. Actuall for old android version only (due to absence of Holo themes).
     */
    private void adjustDialogSize() {
        if (!SystemUtils.isHoneycombOrNever() && getDialog()!=null && getDialog().getWindow() != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(getDialog().getWindow().getAttributes());
            params.width = (int) (metrics.widthPixels * 0.8f);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            getDialog().getWindow().setAttributes(params);
        }
    }

    /**
     * getstring using Application instance instead of Activity, which throw exception.
     *
     * @param resId Resource id for the string
     */
    public final String getSafeString(int resId) {
        return App.sInstance.getString(resId);
    }

    /**
     * getstring using Application instance instead of Activity, which throw exception.
     *
     * @param resId Resource id for the string
     */
    public final String getSafeString(int resId, Object... formatArgs) {
        return App.sInstance.getString(resId, formatArgs);
    }
}
