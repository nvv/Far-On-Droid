package com.openfarmanager.android.model

import android.net.Uri
import android.webkit.MimeTypeMap
import com.openfarmanager.android.core.archive.MimeTypes
import java.io.File

open class FileEntity(private val file: File) : Entity {

    override val name by lazy { file.name }

    override val path by lazy {  file.absolutePath }

    override val size by lazy { file.length() }

    override val lastModifiedDate by lazy { file.lastModified() }

    override val isDirectory by lazy { file.isDirectory }

    override val isHidden by lazy { file.isHidden }

    override val canAccess by lazy { file.canRead() && !file.isHidden }

    val mimeType = MimeTypes.getMimeType(MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString()))

}

class UpNavigator(file: File) : FileEntity(file) {

    override val name = ".."

    override val path = file.absolutePath

}