package com.openfarmanager.android.filesystempanel.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openfarmanager.android.core.command.executor.CommandExecutor
import com.openfarmanager.android.core.command.model.Command
import com.openfarmanager.android.filesystempanel.fragments.PanelFragment
import com.openfarmanager.android.model.actions.CopyActionArguments
import com.openfarmanager.android.model.actions.FileAction
import com.openfarmanager.android.utils.doubleLet
import javax.inject.Inject

class MainViewVM @Inject constructor(val commandExecutor: CommandExecutor) : ViewModel() {

    val activePanelChanged = MutableLiveData<Int>()
    val copyAction = MutableLiveData<CopyActionArguments>()

    val executeCommand = MutableLiveData<Command>()

    private val panelViewModels = mutableSetOf<FileSystemPanelVM>()

    fun registerPanelVM(model: FileSystemPanelVM) {
        panelViewModels += model
    }

    fun getActivePanel() = panelViewModels.findLast { model -> model.isActive }

    fun getInactivePanel() = panelViewModels.findLast { model -> !model.isActive }

    fun requestFocus(@PanelFragment.Companion.PanelPosition activePanel: Int) {
        activePanelChanged.value = activePanel
    }

    fun handleFileAction(action: FileAction) {
        val activePanelVM = getActivePanel()
        val inactivePanelVM = getInactivePanel()

        doubleLet(activePanelVM, inactivePanelVM) { active, inactive ->
            when (action) {
                FileAction.COPY -> copyAction.value = CopyActionArguments(
                        active.currentDirectory, inactive.currentDirectory, active.selectedFileList)
            }
        }
    }

    fun executeCommand(command: Command) {
        commandExecutor.enqueue(command)

        executeCommand.value = command
    }
}