package com.openfarmanager.android.filesystempanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openfarmanager.android.App
import com.openfarmanager.android.R
import com.openfarmanager.android.model.Entity
import com.openfarmanager.android.theme.ThemePref
import java.io.File
import javax.inject.Inject

class FileSystemAdapter : RecyclerView.Adapter<FileSystemAdapter.ViewHolder>() {

    private var files: List<Entity>? = null

    @Inject
    lateinit var themePref: ThemePref

    var clickListener: ((Entity) -> Unit)? = null

    init {
        App.uiComponent.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.panel_item, parent, false))
    }

    override fun getItemCount() = files?.count() ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        files?.get(position)?.let {entity ->
            holder.bind(entity)
            holder.itemView.setOnClickListener {
                clickListener?.invoke(entity)
            }
        }
    }

    fun setItems(items: List<Entity>) {
        files = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name = view.findViewById(R.id.item_name) as TextView
        val info = view.findViewById(R.id.item_info) as TextView

        fun bind(file: Entity) {
            name.text = file.name()
            info.text = info.length().toString()

            when {
                file.isDirectory() -> setColor(themePref.folderColor)
                else -> setColor(themePref.textColor)
            }

            /*
            if (mSelectedFiles.contains(item)) {
                setColor(holder.name, holder.info, settings.getSelectedColor());
            } else if ((!fileItem.canRead() || fileItem.isHidden()) && !item.isVirtualDirectory()) {
                setColor(holder.name, holder.info, settings.getHiddenColor());
            } else if (item.isDirectory()) {
                setColor(holder.name, holder.info, settings.getFolderColor());
            } else if (ArchiveUtils.getMimeType(fileItem).equals(MimeTypes.MIME_APPLICATION_ANDROID_PACKAGE)) {
                setColor(holder.name, holder.info, settings.getInstallColor());
            } else if (ArchiveUtils.isArchiveFile(fileItem)) {
                setColor(holder.name, holder.info, settings.getArchiveColor());
            } else {
                setColor(holder.name, holder.info, settings.getTextColor());
            }
             */
        }


        private fun setColor(color: Int) {
            name.setTextColor(color)
            info.setTextColor(color)
        }
    }

}