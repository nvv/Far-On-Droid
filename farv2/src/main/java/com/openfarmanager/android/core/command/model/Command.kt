package com.openfarmanager.android.core.command.model

abstract class Command {

    abstract val id: String

    abstract fun cancel(): Unit

    abstract fun execute(): Unit

}