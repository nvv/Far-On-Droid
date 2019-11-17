package com.openfarmanager.android.filesystempanel.fragments.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.openfarmanager.android.R
import com.openfarmanager.android.filesystempanel.vm.MainViewVM
import com.openfarmanager.android.model.actions.FileAction
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.dialog_file_action_menu.view.*
import javax.inject.Inject

class FileActionFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainVM: MainViewVM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_file_action_menu, container, false).apply {

            val adapter = object : ArrayAdapter<String>(
                    context, android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    actions()?.map { action -> getString(action.resId) }) {

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val item = super.getView(position, convertView, parent)
                    item.minimumWidth = actionList.width
                    (item.findViewById<View>(android.R.id.text1) as TextView).setTextColor(item.resources.getColor(R.color.white))
                    return item
                }
            }

            actionList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                actions()?.let {
                    dismiss()
                    mainVM.handleFileAction(it[position])
                }
            }

            actionList.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainVM = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewVM::class.java)
    }

    fun actions() = arguments?.getParcelableArray(ARG_ACTIONS) as? Array<FileAction>

    companion object {

        const val ARG_ACTIONS = "ARG_ACTIONS"

        fun newInstance(actions: List<FileAction>) : FileActionFragment {
            return FileActionFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray(ARG_ACTIONS, actions.toTypedArray())
                }
            }
        }

    }

}