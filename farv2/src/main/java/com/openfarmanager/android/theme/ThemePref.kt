package com.openfarmanager.android.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.openfarmanager.android.R
import java.util.HashSet

class ThemePref {

    companion object {
        private const val NAME = "themePref"

        private const val TEXT_COLOR = "text_color"
        private const val FOLDER_COLOR = "folder_color"
        private const val HIDDEN_COLOR = "hidden_color"
        private const val INSTALL_COLOR = "install_color"
        private const val SELECTED_COLOR = "selected_color"
        private const val ARCHIVE_COLOR = "archive_color"
    }

    private var cachedValues = HashSet<CachedValue<*>>()

    private lateinit var textColorValue: CachedValue<Int>
    private lateinit var folderColorValue: CachedValue<Int>
    private lateinit var hiddenColorValue: CachedValue<Int>
    private lateinit var installColorValue: CachedValue<Int>
    private lateinit var selectedColorValue: CachedValue<Int>
    private lateinit var archiveColorValue: CachedValue<Int>

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
        CachedValue.initialize(context.getSharedPreferences(NAME, Context.MODE_PRIVATE))

        textColorValue = CachedValue(TEXT_COLOR, R.color.cyan, Int::class.java)
                .also { cachedValues.add(it) }

        folderColorValue = CachedValue(FOLDER_COLOR, R.color.white, Int::class.java)
                .also { cachedValues.add(it) }

        hiddenColorValue = CachedValue(HIDDEN_COLOR, R.color.main_grey, Int::class.java)
                .also { cachedValues.add(it) }

        installColorValue = CachedValue(INSTALL_COLOR, R.color.green, Int::class.java)
                .also { cachedValues.add(it) }

        selectedColorValue = CachedValue(SELECTED_COLOR, R.color.yellow, Int::class.java)
                .also { cachedValues.add(it) }

        archiveColorValue = CachedValue(ARCHIVE_COLOR, R.color.magenta, Int::class.java)
                .also { cachedValues.add(it) }

    }

    var textColor: Int
        get() = resolveColor(textColorValue.value)
        set(value) {
            textColorValue.value = value
        }

    var folderColor: Int
        get() = resolveColor(folderColorValue.value)
        set(value) {
            folderColorValue.value = value
        }

    var hiddenColor: Int
        get() = resolveColor(hiddenColorValue.value)
        set(value) {
            hiddenColorValue.value = value
        }

    var installColor: Int
        get() = resolveColor(installColorValue.value)
        set(value) {
            installColorValue.value = value
        }

    var selectedColor: Int
        get() = resolveColor(selectedColorValue.value)
        set(value) {
            selectedColorValue.value = value
        }

    var archiveColor: Int
        get() = resolveColor(archiveColorValue.value)
        set(value) {
            archiveColorValue.value = value
        }

    private fun resolveColor(color: Int?): Int =
            ContextCompat.getColor(context, color ?: R.color.transparent)

    fun clear() {
        cachedValues.forEach { value -> value.delete() }
    }

}