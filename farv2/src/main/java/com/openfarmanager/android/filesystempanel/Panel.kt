package com.openfarmanager.android.filesystempanel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.openfarmanager.android.R
import com.openfarmanager.android.filesystempanel.vm.BottomBarVM
import com.openfarmanager.android.filesystempanel.vm.FileSystemPanelVM
import com.openfarmanager.android.filesystempanel.vm.MainViewVM
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.main_panel.*
import javax.inject.Inject

class Panel : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var panelVM: FileSystemPanelVM

    private lateinit var mainVM: MainViewVM

    private lateinit var bottomBarVM: BottomBarVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_panel, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        panelVM = ViewModelProviders.of(this, viewModelFactory).get(FileSystemPanelVM::class.java)

        val pathView = currentPath
        val fileSystemView = fileSystemView

        activity?.let {
            mainVM = ViewModelProviders.of(it, viewModelFactory).get(MainViewVM::class.java)

            mainVM.activePanelChanged.observe(this, Observer { location ->
                pathView.setBackgroundResource(if (location == arguments?.getInt(ARG_POSITION)) R.color.colorPrimary else R.color.main_blue)
            })

            bottomBarVM = ViewModelProviders.of(it, viewModelFactory).get(BottomBarVM::class.java)

            bottomBarVM.selectionMode.observe(this, Observer {
                panelVM.isSelectionMode = it
            })

        }

        panelVM.scanResult.observe(this, Observer { result ->
            pathView.text = result.path
            fileSystemView.showFiles(result.files)
        })

        panelVM.selectedFilePosition.observe(this, Observer { result ->
            fileSystemView.selectFile(result.first, result.second)
        })

        fileSystemView.setClickListener { position, entity ->
            panelVM.handleClick(position, entity)
        }

        panelVM.openDirectory("/storage/emulated/0")
    }

    companion object {

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(POSITION_LEFT, POSITION_RIGHT)
        annotation class PanelPosition

        const val POSITION_LEFT = 0
        const val POSITION_RIGHT = 1

        private val ARG_POSITION = "ARG_POSITION"

        fun newInstance(@PanelPosition position: Int): Panel {
            return Panel().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
        }

    }
}