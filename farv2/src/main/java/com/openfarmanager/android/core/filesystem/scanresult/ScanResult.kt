package com.openfarmanager.android.core.filesystem.scanresult

import com.openfarmanager.android.model.Entity

data class ScanResult(val path: String, val files: List<Entity>)