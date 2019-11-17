package com.openfarmanager.android.core.command.executor

import com.openfarmanager.android.core.command.model.Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import kotlin.collections.LinkedHashMap

class CommandExecutor {

    private val commands = LinkedHashMap<String, Command>()

    fun enqueue(command: Command) {
        commands[command.id] = command
    }

    suspend fun execute(id: String): Result {
        return GlobalScope.async {
            commands[id]?.let {

                Success
            }

            Error
        }.await()

    }

}

sealed class Result

object Success : Result()
object Error : Result()