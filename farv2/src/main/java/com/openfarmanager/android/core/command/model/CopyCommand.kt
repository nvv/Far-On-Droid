package com.openfarmanager.android.core.command.model

import com.openfarmanager.android.model.filesystem.Entity
import java.util.*

class CopyCommand(private val src: Entity,
                  private val dst: Entity,
                  private val files: Set<Entity>) : Command() {

    override val id = UUID.randomUUID().toString()

    override fun cancel() {

    }

    override fun execute() {

    }
}