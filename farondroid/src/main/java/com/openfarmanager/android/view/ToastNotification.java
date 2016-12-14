package com.openfarmanager.android.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

public class ToastNotification {

    private static View sToastView;

    static {
        sToastView = LayoutInflater.from(App.sInstance.getApplicationContext()).inflate(R.layout.toast_notification_view, null);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        //toast.setDuration(Toast.LENGTH_LONG);

        ((TextView) sToastView.findViewById(R.id.text)).setText(text);

        toast.setView(sToastView);
        return toast;
    }

}
