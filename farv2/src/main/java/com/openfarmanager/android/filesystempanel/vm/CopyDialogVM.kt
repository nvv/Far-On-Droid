package com.openfarmanager.android.filesystempanel.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import javax.inject.Inject

class CopyDialogVM @Inject constructor() : ViewModel() {

    val isTheSameDirectoryError = MutableLiveData<Boolean>()
    val isWrongDestinationError = MutableLiveData<Boolean>()
    val process = MutableLiveData<Boolean>()

    fun process(currentDirectory: String, destination: String) {

        if (currentDirectory == destination) {
            isTheSameDirectoryError.value = true
            return
        }

        if (!File(destination).isDirectory) {
            isWrongDestinationError.value = true
            return
        }

        process.value = true
    }

}