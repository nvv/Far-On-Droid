package com.openfarmanager.android.core.filesystem

import com.openfarmanager.android.model.filesystem.Entity

interface Sorter {
    fun doSort(file1: Entity, file2: Entity): Int
}