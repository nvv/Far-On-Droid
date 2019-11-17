package com.openfarmanager.android.filesystempanel.fragments.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.openfarmanager.android.R
import com.openfarmanager.android.core.command.model.CopyCommand
import com.openfarmanager.android.filesystempanel.vm.CopyDialogVM
import com.openfarmanager.android.filesystempanel.vm.MainViewVM
import com.openfarmanager.android.model.actions.CopyActionArguments
import com.openfarmanager.android.model.filesystem.DestinationPath
import com.openfarmanager.android.model.filesystem.Entity
import com.openfarmanager.android.model.filesystem.FileEntity
import com.openfarmanager.android.utils.tripleLet
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.dialog_copy_confirm.*
import kotlinx.android.synthetic.main.dialog_copy_confirm.view.*
import kotlinx.android.synthetic.main.dialog_copy_confirm.view.destination
import java.util.ArrayList
import javax.inject.Inject

class CopyDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CopyDialogVM

    private lateinit var mainVM: MainViewVM

    private var sourceDirectory: Entity? = null
    private var destinationDirectory: Entity? = null
    private var selectedFiles: List<Entity>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_copy_confirm, container, false).apply {
            sourceDirectory = arguments?.getParcelable(ARG_SOURCE_DIRECTORY)
            destinationDirectory = arguments?.getParcelable(ARG_DESTINATION_DIRECTORY)
            selectedFiles = arguments?.getParcelableArrayList(ARG_SELECTED_FILES)

            this.destination.setText(destinationDirectory?.path)

            this.cancel.setOnClickListener {
                dismiss()
            }

            this.ok.setOnClickListener {
                viewModel.process(sourceDirectory?.path ?: "", destination.text.toString())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CopyDialogVM::class.java)
        mainVM = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewVM::class.java)

        viewModel.isTheSameDirectoryError.observe(this, Observer {
            activity?.supportFragmentManager?.let {
                ErrorDialogFragment.newInstance(
                        requireActivity().getString(R.string.error_cannot_copy_files_to_the_same_directory))
                        .show(it, "error")
            }
        })

        viewModel.isWrongDestinationError.observe(this, Observer {
            activity?.supportFragmentManager?.let {
                ErrorDialogFragment.newInstance(
                        requireActivity().getString(R.string.error_not_valid_directory, destination.text))
                        .show(it, "error")
            }
        })

        viewModel.process.observe(this, Observer {
            dismiss()
            tripleLet(sourceDirectory, destinationDirectory, selectedFiles, { src, dest, list ->
                mainVM.executeCommand(CopyCommand(src,
                        DestinationPath(dest as FileEntity, destination.text.toString()),
                        list.toSet()))
            })
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.dialog)
    }

    companion object {
        const val ARG_SOURCE_DIRECTORY = "ARG_SOURCE_DIRECTORY"
        const val ARG_DESTINATION_DIRECTORY = "ARG_DESTINATION_DIRECTORY"
        const val ARG_SELECTED_FILES = "ARG_SELECTED_FILES"

        fun newInstance(copyActionArguments: CopyActionArguments) = CopyDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_SOURCE_DIRECTORY, copyActionArguments.currentDirectory)
                putParcelable(ARG_DESTINATION_DIRECTORY, copyActionArguments.destination)
                putParcelableArrayList(ARG_SELECTED_FILES, ArrayList(copyActionArguments.selectedFiles))
            }
        }
    }
}