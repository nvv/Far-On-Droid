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
import com.openfarmanager.android.core.utils.CustomFormatter
import com.openfarmanager.android.model.filesystem.Entity
import com.openfarmanager.android.model.filesystem.extension
import com.openfarmanager.android.theme.ThemePref
import javax.inject.Inject

class FileSystemAdapter : RecyclerView.Adapter<FileSystemAdapter.ViewHolder>() {

    private var files: List<AdapterEntity>? = null

    @Inject
    lateinit var themePref: ThemePref

    var clickListener: ((Int, Entity) -> Unit)? = null

    var longClickListener: ((Int, Entity) -> Unit)? = null

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
            holder.itemView.setOnLongClickListener {
                longClickListener?.invoke(position, entity.entity)
                true
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
            info.text = if (file.entity.isDirectory) info.context.getString(R.string.folder) else
                CustomFormatter.formatBytes(file.entity.size)

            when {
                file.isSelected -> setColor(themePref.selectedColor)
                !file.entity.canAccess -> setColor(themePref.hiddenColor)
                file.entity.isDirectory -> setColor(themePref.folderColor)
                file.entity.extension() == MimeTypes.MIME_APPLICATION_ANDROID_PACKAGE -> setColor(themePref.installColor)
                ArchiveUtils.isArchiveFile(file.entity) -> setColor(themePref.archiveColor)
                else -> setColor(themePref.textColor)
            }
        }


        private fun setColor(color: Int) {
            name.setTextColor(color)
            info.setTextColor(color)
        }
    }

    data class AdapterEntity(val entity: Entity,
                             var isSelected: Boolean = false)
}