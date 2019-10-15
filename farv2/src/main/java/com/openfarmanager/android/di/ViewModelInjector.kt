package com.openfarmanager.android.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerAppCompatActivity

interface ViewModelAccessor<T : ViewModel> {
    fun buildViewModel(clazz: Class<T>): T
    val activity: DaggerAppCompatActivity
}

class ViewModelInjector<T : ViewModel>(val context: Context) : ViewModelAccessor<T> {

    override val activity: DaggerAppCompatActivity by lazy {
        try {
            context as DaggerAppCompatActivity
        } catch (exception: ClassCastException) {
            throw ClassCastException("Please ensure that the provided Context is a valid DaggerAppCompatActivity")
        }
    }

    override fun buildViewModel(clazz: Class<T>) = ViewModelProviders.of(activity).get(clazz)

}