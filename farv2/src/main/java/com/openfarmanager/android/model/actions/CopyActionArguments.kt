package com.openfarmanager.android.model.actions

import com.openfarmanager.android.model.filesystem.Entity

data class CopyActionArguments(val currentDirectory: Entity,
                               val destination: Entity,
                               val selectedFiles: Set<Entity>)