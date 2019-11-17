package com.openfarmanager.android.model.filesystem

import android.os.Parcelable


interface Entity : Parcelable {

    val name: String

    val path: String

    val size: Long

    val lastModifiedDate: Long

    val isDirectory: Boolean

    val isHidden: Boolean

    val canAccess: Boolean

}

fun Entity.extension(): String {
    return name.substringAfterLast(".", "")
}