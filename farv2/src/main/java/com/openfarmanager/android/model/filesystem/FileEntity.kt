package com.openfarmanager.android.model.filesystem

import android.net.Uri
import android.os.Parcelable
import android.webkit.MimeTypeMap
import com.openfarmanager.android.core.archive.MimeTypes
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
open class FileEntity(internal val file: File) : Entity {

    override val name by lazy { file.name }

    override val path by lazy {  file.absolutePath }

    override val size by lazy { file.length() }

    override val lastModifiedDate by lazy { file.lastModified() }

    override val isDirectory by lazy { file.isDirectory }

    override val isHidden by lazy { file.isHidden }

    override val canAccess by lazy { file.canRead() && !file.isHidden }

    val mimeType = MimeTypes.getMimeType(MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString()))

}

class DestinationPath(entity: FileEntity, path: String): FileEntity(entity.file) {

    override val name = path.substringAfterLast("/", "")

    override val path = path
}

class UpNavigator(file: File) : FileEntity(file) {

    override val name = ".."

    override val path = file.absolutePath

}