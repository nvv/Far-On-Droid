package com.openfarmanager.android.filesystempanel.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openfarmanager.android.model.Entity
import com.openfarmanager.android.model.FileEntity
import com.openfarmanager.android.model.UpNavigator
import com.openfarmanager.android.core.filesystem.scanresult.ScanResult
import java.io.File
import javax.inject.Inject

class FileSystemPanelVM @Inject constructor() : ViewModel() {

    val scanResult = MutableLiveData<ScanResult>()
    val selectedFiles = MutableLiveData<Set<Entity>>()
    val selectedFilePosition = MutableLiveData<Pair<Int, Boolean>>()

    var isSelectionMode = false

    private val selectedFileList = mutableSetOf<Entity>()

    fun openDirectory(path: String) {
        selectedFileList.clear()

        val file = File(path)

        val list = mutableListOf<Entity>()
        list.add(UpNavigator(file.parentFile))
        file.listFiles().forEach { item -> list.add(FileEntity(item)) }
        scanResult.value = ScanResult(path, list)
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
            openDirectory(entity.path())
        }
    }

}