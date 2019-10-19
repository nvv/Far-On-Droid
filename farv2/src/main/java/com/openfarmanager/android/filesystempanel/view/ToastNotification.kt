package com.openfarmanager.android.filesystempanel.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.openfarmanager.android.R
import kotlinx.android.synthetic.main.toast_notification_view.view.*

class ToastNotification {

    fun makeText(context: Context, text: CharSequence, duration: Int): Toast {
        val view = LayoutInflater.from(context).inflate(R.layout.toast_notification_view, null)
        val toast = Toast(context)
        toast.duration = duration
        //toast.setDuration(Toast.LENGTH_LONG);
        view.text.text = text
        toast.view = view
        return toast
    }
}