package com.openfarmanager.android.theme

import android.content.Context
import android.graphics.Typeface
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
        private const val SECONDARY_COLOR = "secondary_color"

        private const val HOLD_ALT_BY_CLICK = "hold_alt_by_click"

        private const val BOTTOM_PANEL_FONT_SIZE = "bottom_panel_font_size"
        private const val MAIN_PANEL_FONT_NAME = "main_panel_font"
    }

    private var cachedValues = HashSet<CachedValue<*>>()

    // colors
    private lateinit var textColorValue: CachedValue<Int>
    private lateinit var folderColorValue: CachedValue<Int>
    private lateinit var hiddenColorValue: CachedValue<Int>
    private lateinit var installColorValue: CachedValue<Int>
    private lateinit var selectedColorValue: CachedValue<Int>
    private lateinit var archiveColorValue: CachedValue<Int>
    private lateinit var secondaryColorValue: CachedValue<Int>

    // behaviour
    private lateinit var holdAltByClickValue: CachedValue<Boolean>

    // bottom panel
    private lateinit var bottomPanelFontSizeValue: CachedValue<Int>

    // main panel
    private lateinit var mainPanelFontPathValue: CachedValue<String>
    var mainPanelFontValue = Typeface.DEFAULT

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

        secondaryColorValue = CachedValue(SECONDARY_COLOR, R.color.selected_item, Int::class.java)
                .also { cachedValues.add(it) }


        holdAltByClickValue = CachedValue(HOLD_ALT_BY_CLICK, false, Boolean::class.java)
                .also { cachedValues.add(it) }


        bottomPanelFontSizeValue = CachedValue(BOTTOM_PANEL_FONT_SIZE, 14, Int::class.java)
                .also { cachedValues.add(it) }


        mainPanelFontPathValue = CachedValue(MAIN_PANEL_FONT_NAME, "", String::class.java)
                .also { cachedValues.add(it) }
        if (!mainPanelFontPathValue.safeValue().isNullOrEmpty()) {
            mainPanelFontValue = Typeface.createFromFile(mainPanelFontPathValue.safeValue())
        }
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

    var secondaryColor: Int
        get() = resolveColor(secondaryColorValue.value)
        set(value) {
            secondaryColorValue.value = value
        }


    var holdAltByClick: Boolean
        get() = holdAltByClickValue.safeValue()
        set(value) {
            holdAltByClickValue.value = value
        }


    var bottomPanelFontSize: Int
        get() = bottomPanelFontSizeValue.safeValue()
        set(value) {
            bottomPanelFontSizeValue.value = value
        }

    var mainPanelFontPath: String
        get() = mainPanelFontPathValue.safeValue()
        set(value) {
            mainPanelFontPathValue.value = value
            mainPanelFontValue = Typeface.createFromFile(mainPanelFontPathValue.safeValue())
        }

    private fun resolveColor(color: Int?): Int =
            ContextCompat.getColor(context, color ?: R.color.transparent)

    fun clear() {
        cachedValues.forEach { value -> value.delete() }
    }

}