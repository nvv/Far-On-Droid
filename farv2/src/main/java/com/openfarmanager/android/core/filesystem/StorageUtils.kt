package com.openfarmanager.android.core.filesystem

import android.os.Environment
import java.io.File

class StorageUtils {

    val sdCard: File? = Environment.getExternalStorageDirectory()
    val sdPath: String = if (sdCard != null) sdCard?.path ?: "/" else "/"

}