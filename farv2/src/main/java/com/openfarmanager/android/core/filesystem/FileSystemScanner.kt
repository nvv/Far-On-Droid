package com.openfarmanager.android.core.filesystem

import android.os.Build
import com.openfarmanager.android.core.filesystem.exceptions.OpenDirectoryException
import com.openfarmanager.android.core.filesystem.exceptions.OpenDirectoryNotAllowed
import com.openfarmanager.android.model.Entity
import com.openfarmanager.android.model.FileEntity
import com.openfarmanager.android.model.UpNavigator
import com.openfarmanager.android.theme.ThemePref
import java.io.File
import java.util.*
import javax.inject.Inject

class FileSystemScanner @Inject constructor(
        val themePref: ThemePref,
        val storageUtils: StorageUtils) {

    private val sorters = LinkedList<Sorter>().apply {
        if (themePref.folderFirst) {
            add(SorterFactory.createDirectoryUpFilter())
        }
        add(SorterFactory.createPreferredFilter(themePref))
    }

    private val comparator = Comparator<Entity> { entity1, entity2 ->
        var result = 0
        sorters.forEach {sorter ->
            result = sorter.doSort(entity1, entity2)
            if (result != 0) {
                return@Comparator result
            }
        }
        result
    }

    fun openDirectory(directory: Entity, filter: String = ""): List<Entity> {
        if (directory.isDirectory) {

            if (Build.VERSION.SDK_INT >= 24
                    && storageUtils.sdPath.startsWith(directory.path)
                    && storageUtils.sdPath != directory.path) {

                // don't allow to open subfolder of sd card root on SDK >= 24
                throw OpenDirectoryNotAllowed("Not allowed to open subfolder of sd card root on SDK >= 24")
            }

            val file = File(directory.path)

            if (file.canRead()) {
                val list = mutableListOf<Entity>().apply {
                    add(UpNavigator(file.parentFile))

                    addAll(file.listFiles()
                            .map { item -> FileEntity(item) }
                            .filter { item -> !themePref.hideSystemFiles || !item.isHidden }
                            .toList()
                    )
                }
                sort(list)
                return list
            } else {
                throw OpenDirectoryNotAllowed("Can't read directory")
            }

        } else throw OpenDirectoryException("openDirectory can't scan file")
    }

    fun sort(filesToSort: List<Entity>) {
        Collections.sort(filesToSort, comparator)
    }
}