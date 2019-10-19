package com.openfarmanager.android.model

interface Entity {

    val name: String

    val path: String

    val size: Long

    val lastModifiedDate: Long

    val isDirectory: Boolean

    val isHidden: Boolean

    val canAccess: Boolean

}

fun Entity.extention(): String {
    return name.substringAfterLast(".", "")
}