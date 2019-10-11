package com.openfarmanager.android.model

import java.io.File

open class FileEntity(private val file: File) : Entity {

    override fun name() = file.name

    override fun path() = file.absolutePath

    override fun size() = file.length()

    override fun isDirectory() = file.isDirectory

    override fun canAccess() = file.canRead() && !file.isHidden

}

class UpNavigator(private val file: File) : FileEntity(file) {

    override fun name() = ".."

    override fun path() = file.absolutePath

}