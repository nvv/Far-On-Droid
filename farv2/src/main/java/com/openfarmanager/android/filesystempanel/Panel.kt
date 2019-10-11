package com.openfarmanager.android.filesystempanel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.openfarmanager.android.R
import com.openfarmanager.android.filesystempanel.vm.FileSystemViewVM
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.main_panel.*
import javax.inject.Inject

class Panel : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: FileSystemViewVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_panel, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            viewModel = ViewModelProviders.of(it, viewModelFactory).get(FileSystemViewVM::class.java)

            viewModel.data.observe(this, Observer { items ->
                fileSystemView.showFiles(items)
            })

            fileSystemView.setClickListener { entity ->
                viewModel.openDirectory(entity.path())
            }

            viewModel.openDirectory("/storage/emulated/0")
        }
    }
}