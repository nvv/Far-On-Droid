package com.openfarmanager.android.core.filesystem

import com.openfarmanager.android.model.filesystem.Entity
import com.openfarmanager.android.theme.ThemePref
import java.text.Collator

object SorterFactory {

    private var sCollator: Collator = Collator.getInstance().apply {
        strength = Collator.SECONDARY
    }

    fun createDirectoryUpFilter() = object : Sorter {
        override fun doSort(file1: Entity, file2: Entity) =
                -java.lang.Boolean.valueOf(file1.isDirectory).compareTo(file2.isDirectory)
    }

    fun createAlphabeticFilter() = object : Sorter {
        override fun doSort(file1: Entity, file2: Entity) = sCollator.compare(file1.name, file2.name)
    }

    fun createSizeFilter() = object : Sorter {
        override fun doSort(file1: Entity, file2: Entity) = file1.size.compareTo(file2.size)
    }

    fun createExtensionFilter() = object : Sorter {
        override fun doSort(file1: Entity, file2: Entity): Int {
            var name1 = file1.name
            var name2 = file2.name

            val name1Dot = name1.lastIndexOf('.')
            val name2Dot = name2.lastIndexOf('.')

            return when {
                name1Dot == -1 == (name2Dot == -1) -> { // both or neither
                    name1 = name1.substring(name1Dot + 1)
                    name2 = name2.substring(name2Dot + 1)
                    name1.compareTo(name2)
                }
                name1Dot == -1 -> // only name2 has an extension, so name1 goes first
                    -1
                else -> // only name1 has an extension, so name1 goes second
                    1
            }
        }
    }

    fun createModifiedDateFilter() = object : Sorter {
        override fun doSort(file1: Entity, file2: Entity) = file1.lastModifiedDate.compareTo(file2.lastModifiedDate)
    }

    fun createPreferredFilter(themePref: ThemePref): Sorter {
        return when (themePref.filesSort.toInt()) {
            0 -> createAlphabeticFilter()
            1 -> createSizeFilter()
            2 -> createModifiedDateFilter()
            3 -> createExtensionFilter()
            else -> createAlphabeticFilter()
        }


    }

}