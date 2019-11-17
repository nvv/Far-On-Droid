package com.openfarmanager.android.filesystempanel.fragments.dialogs

import android.content.Context
import com.openfarmanager.android.core.command.executor.CommandExecutor
import com.openfarmanager.android.core.command.model.Command
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class CommandFragment : ProgressFragment() {

    @Inject
    lateinit var commandExecutor: CommandExecutor

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    companion object {
        fun newInstance(command: Command) = CommandFragment()
    }
}