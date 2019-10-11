package com.openfarmanager.android.filesystempanel.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.openfarmanager.android.R
import com.openfarmanager.android.filesystempanel.adapter.FileSystemAdapter
import com.openfarmanager.android.model.Entity
import kotlinx.android.synthetic.main.file_system_view.view.*
import java.io.File

class FileSystemView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    var fileSystemAdapter: FileSystemAdapter = FileSystemAdapter()

    init {
        LayoutInflater.from(getContext()).inflate(R.layout.file_system_view, this)
        fileSystemListView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(getContext())
            adapter = fileSystemAdapter

//            addItemDecoration(HorizontalDividerItemDecoration(getContext()));
        }
    }

    fun setClickListener(clickListener: ((Entity) -> Unit)) {
        fileSystemAdapter.clickListener = clickListener
    }

    fun showFiles(files: List<Entity>) {
        fileSystemAdapter.setItems(files)
    }

}