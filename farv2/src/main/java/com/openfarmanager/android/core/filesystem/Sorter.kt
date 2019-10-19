package com.openfarmanager.android.core.filesystem

import com.openfarmanager.android.model.Entity

interface Sorter {
    fun doSort(file1: Entity, file2: Entity): Int
}