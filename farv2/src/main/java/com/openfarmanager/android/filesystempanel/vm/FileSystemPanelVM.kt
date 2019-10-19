package com.openfarmanager.android.filesystempanel.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openfarmanager.android.core.filesystem.FileSystemScanner
import com.openfarmanager.android.core.filesystem.exceptions.OpenDirectoryException
import com.openfarmanager.android.model.Entity
import com.openfarmanager.android.model.FileEntity
import com.openfarmanager.android.model.UpNavigator
import com.openfarmanager.android.core.filesystem.scanresult.ScanResult
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class FileSystemPanelVM @Inject constructor(val fileSystemScanner: FileSystemScanner) : ViewModel() {

    val scanResult = MutableLiveData<ScanResult>()
    val selectedFiles = MutableLiveData<Set<Entity>>()
    val selectedFilePosition = MutableLiveData<Pair<Int, Boolean>>()
    val openDirectoryError = MutableLiveData<Exception>()

    var isSelectionMode = false

    private val selectedFileList = mutableSetOf<Entity>()

    fun openDirectory(directory: Entity) {
        selectedFileList.clear()

        try {
            scanResult.value = ScanResult(directory, fileSystemScanner.openDirectory(directory))
        } catch (ex: OpenDirectoryException) {
            openDirectoryError.value = ex
        }
    }

    fun handleClick(position: Int, entity: Entity) {
        if (isSelectionMode) {
            val contains = selectedFileList.contains(entity)
            if (contains) {
                selectedFileList -= entity
            } else {
                selectedFileList += entity
            }
            selectedFiles.value = selectedFileList
            selectedFilePosition.value = Pair(position, !contains)
        } else {
            openDirectory(entity)
        }
    }

}