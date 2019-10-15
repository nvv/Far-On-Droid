package com.openfarmanager.android.filesystempanel.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openfarmanager.android.filesystempanel.Panel
import javax.inject.Inject

class MainViewVM @Inject constructor() : ViewModel() {

    val activePanelChanged = MutableLiveData<Int>()

    fun requestFocus(@Panel.Companion.PanelPosition activePanel: Int) {
        activePanelChanged.value = activePanel
    }
}