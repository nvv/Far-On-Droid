package com.openfarmanager.android.core.archive

import com.openfarmanager.android.core.archive.MimeTypes.MIME_APPLICATION_7Z
import com.openfarmanager.android.core.archive.MimeTypes.MIME_APPLICATION_JAVA_ARCHIVE
import com.openfarmanager.android.core.archive.MimeTypes.MIME_APPLICATION_X_AR
import com.openfarmanager.android.core.archive.MimeTypes.MIME_APPLICATION_X_CPIO
import com.openfarmanager.android.core.archive.MimeTypes.MIME_APPLICATION_X_RAR_COMPRESSED
import com.openfarmanager.android.core.archive.MimeTypes.MIME_APPLICATION_X_TAR
import com.openfarmanager.android.core.archive.MimeTypes.MIME_APPLICATION_ZIP
import com.openfarmanager.android.model.filesystem.Entity
import com.openfarmanager.android.model.filesystem.FileEntity

object ArchiveUtils {

    enum class ArchiveType {
        zip, tar, ar, jar, cpio, rar, x7z;


        companion object {

            fun getType(mime: String): ArchiveType? {
                return when {
                    MIME_APPLICATION_ZIP == mime -> zip
                    MIME_APPLICATION_X_TAR == mime -> tar
                    MIME_APPLICATION_X_AR == mime -> ar
                    MIME_APPLICATION_JAVA_ARCHIVE == mime -> jar
                    MIME_APPLICATION_X_CPIO == mime -> cpio
                    MIME_APPLICATION_X_RAR_COMPRESSED == mime -> rar
                    MIME_APPLICATION_7Z == mime -> x7z
                    else -> null
                }

            }
        }

    }

//    enum class CompressionEnum {
//        gzip, bzip2, xz, pack200;
//
//
//        companion object {
//
//            fun getCompression(mime: String): CompressionEnum? {
//
//                if (mime == MimeTypes.MIME_APPLICATION_X_GZIP || mime == MimeTypes.MIME_APPLICATION_TGZ) {
//                    return gzip
//                } else if (mime == MimeTypes.MIME_APPLICATION_X_XZ) {
//                    return xz
//                } else if (mime == MimeTypes.MIME_APPLICATION_X_BZIP2) {
//                    return bzip2
//                } else if (mime == MimeTypes.MIME_APPLICATION_X_PACK200) {
//                    return pack200
//                }
//
//                return null
//            }
//
//            fun toString(type: CompressionEnum): String {
//                when (type) {
//                    gzip -> return CompressorStreamFactory.GZIP
//                    xz -> return CompressorStreamFactory.XZ
//                    bzip2 -> return CompressorStreamFactory.BZIP2
//                    pack200 -> return CompressorStreamFactory.PACK200
//                    else -> return ""
//                }
//            }
//        }
//
//    }


    fun isArchiveFile(file: Entity): Boolean {
        return isArchiveSupported(file) || isCompressionSupported(file)
    }

    fun isArchiveSupported(file: Entity): Boolean {
        return file is FileEntity && ArchiveType.getType(file.mimeType) != null
    }

    fun isCompressionSupported(file: Entity): Boolean {
//        return CompressionEnum.getCompression(mime) != null
        return false
    }
}