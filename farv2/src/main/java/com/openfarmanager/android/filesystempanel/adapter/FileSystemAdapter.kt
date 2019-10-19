package com.openfarmanager.android.filesystempanel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openfarmanager.android.App
import com.openfarmanager.android.R
import com.openfarmanager.android.core.archive.ArchiveUtils
import com.openfarmanager.android.core.archive.MimeTypes
import com.openfarmanager.android.model.Entity
import com.openfarmanager.android.model.extention
import com.openfarmanager.android.theme.ThemePref
import java.io.File
import javax.inject.Inject

class FileSystemAdapter : RecyclerView.Adapter<FileSystemAdapter.ViewHolder>() {

    private var files: List<AdapterEntity>? = null

    @Inject
    lateinit var themePref: ThemePref

    var clickListener: ((Int, Entity) -> Unit)? = null

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
                clickListener?.invoke(position, entity.entity)
            }
        }
    }

    fun selectItems(selectedItems: Set<Entity>) {
        val changes = mutableListOf<Int>()
        files?.forEachIndexed { index, item ->
            if (item.isSelected) {
                if (!selectedItems.contains(item.entity)) {
                    item.isSelected = false
                    changes += index
                }
            } else {
                if (selectedItems.contains(item.entity)) {
                    item.isSelected = true
                    changes += index
                }
            }
        }

        changes.forEach {
            notifyItemChanged(it)
        }
    }

    fun selectItem(position: Int, isSelected: Boolean) {
        files?.get(position)?.isSelected = isSelected
        notifyItemChanged(position)
    }

    fun setItems(items: List<Entity>) {
        files = items.map { item -> AdapterEntity(item) }.toList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name = view.findViewById(R.id.item_name) as TextView
        val info = view.findViewById(R.id.item_info) as TextView

        fun bind(file: AdapterEntity) {
            name.text = file.entity.name
            info.text = info.length().toString()

            when {
                file.isSelected -> setColor(themePref.selectedColor)
                !file.entity.canAccess -> setColor(themePref.hiddenColor)
                file.entity.isDirectory -> setColor(themePref.folderColor)
                file.entity.extention() == MimeTypes.MIME_APPLICATION_ANDROID_PACKAGE -> setColor(themePref.installColor)
                ArchiveUtils.isArchiveFile(file.entity) -> setColor(themePref.archiveColor)
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

    data class AdapterEntity(val entity: Entity,
                             var isSelected: Boolean = false)
}