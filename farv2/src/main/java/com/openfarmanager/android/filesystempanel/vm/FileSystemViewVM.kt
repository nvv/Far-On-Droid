package com.openfarmanager.android.filesystempanel.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openfarmanager.android.model.Entity
import com.openfarmanager.android.model.FileEntity
import com.openfarmanager.android.model.UpNavigator
import java.io.File
import javax.inject.Inject

class FileSystemViewVM @Inject constructor() : ViewModel() {

    val data = MutableLiveData<List<Entity>>()

    fun openDirectory(path: String) {
        val file = File(path)

        val list = mutableListOf<Entity>()
        list.add(UpNavigator(file.parentFile))
        file.listFiles().forEach { item -> list.add(FileEntity(item)) }
        data.value = list
    }

}