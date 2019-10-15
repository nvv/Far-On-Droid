package com.openfarmanager.android.filesystempanel.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class BottomBarVM @Inject constructor() : ViewModel() {

    val selectionMode = MutableLiveData<Boolean>()

}