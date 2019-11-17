package com.openfarmanager.android.filesystempanel.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.*
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import com.openfarmanager.android.App
import com.openfarmanager.android.R
import com.openfarmanager.android.di.ViewModelAccessor
import com.openfarmanager.android.di.ViewModelInjector
import com.openfarmanager.android.filesystempanel.vm.BottomBarVM
import com.openfarmanager.android.theme.ThemePref
import kotlinx.android.synthetic.main.dialog_file_action_menu.*
import javax.inject.Inject

class BottomBar(context: Context,
                attrs: AttributeSet?,
                defStyleAttr: Int
    ) : LinearLayout(context, attrs, defStyleAttr),
        ViewModelAccessor<BottomBarVM> by ViewModelInjector(context) {

    @Inject
    lateinit var themePref: ThemePref

    private lateinit var viewModel: BottomBarVM

    private var density: Float = 0F
    private var minWidth: Int = 0
    private var menu: MenuBuilder? = null
    private var itemsCount: Int = 0

    constructor(context: Context) : this(context, null, -1) {
        initMenu(context)
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, -1) {
        initMenu(context)
    }

    private fun initMenu(context: Context) {
        viewModel = buildViewModel(BottomBarVM::class.java)

        App.uiComponent.inject(this)

        density = resources.displayMetrics.density
        minWidth = (80 * density).toInt()

        menu = MenuBuilder(context)
        MenuInflater(context).inflate(R.menu.main, menu)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw) {
            buildMenu(false)
        }
    }

    fun buildMenu(forceRedraw: Boolean) {
        val count = measuredWidth / minWidth
        if (count == itemsCount && !forceRedraw) {
            return
        }

        removeAllViews()

        itemsCount = count

        val views = SparseArray<TextView>()

        var expanded = 0
        menu?.let { menu ->
            var used = menu.size()

            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                val view = getTextView(item)
                views.put(i * 100, view)

//            when (item.getItemId()) {
//                R.id.action_alt -> mAltView = view
//                R.id.action_applauncher -> mApplicationsView = view
//                R.id.action_quckview -> mQuickView = view
//                R.id.menu_more -> mMoreView = view
//                R.id.action_select -> mSelectView = view
//            }

            }

            while (true) {
                if (expanded == menu.size() || used > itemsCount) {
                    break
                }

                val item = menu.getItem(expanded)
                expanded++
                if (!item.hasSubMenu()) {
                    continue
                }
                if (used + item.subMenu.size() > itemsCount) {
                    continue
                }

                used += item.subMenu.size() - 1

                var index = 0
                for (i in 0 until views.size()) {
                    if (views.valueAt(i).tag == item) {
                        index = views.keyAt(i)
                        views.remove(index)
                    }
                }

                for (i in 0 until item.subMenu.size()) {
                    views.put(index + i, getTextView(item.subMenu.getItem(i)))
                }


            }
        }

        for (i in 0 until views.size()) {
            addView(views.valueAt(i))
        }

        post { this.requestLayout() }
    }

    private fun getTextView(item: MenuItem): TextView {
        val threedip = (3 * density).toInt()

        val size = themePref.bottomPanelFontSize
        val view = TextView(context)
        view.typeface = themePref.mainPanelFontValue
        view.text = item.title
        view.gravity = Gravity.CENTER
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)
        layoutParams.setMargins(threedip, 0, threedip, 0)
        view.layoutParams = layoutParams
        view.tag = item
        if (item.itemId == R.id.action_alt) {
            view.setOnTouchListener(altListener)
        } else {
            view.setOnClickListener(clickListener)
        }
        view.setBackgroundColor(themePref.secondaryColor)
        view.setTextColor(resources.getColor(R.color.black))
        view.setSingleLine()
        view.setPadding(threedip, threedip, threedip, threedip)
        view.height = ((6 + 2 * size) * density).toInt()
        view.minWidth = (80 * density).toInt()
        return view
    }

    private fun showSubMenu(item: MenuItem) {
        val dialog = SubMenuDialog(context, item, object : SubMenuDialog.OnActionSelectedListener {
            override fun onActionSelected(item: MenuItem) {

            }
        })
        dialog.show()
    }

    private val clickListener = OnClickListener { view ->
        val item = view.tag as MenuItem
        if (item.hasSubMenu()) {
            showSubMenu(item)
        } else {
//            sendMessage(item)
        }
    }

    private val altListener = OnTouchListener { view, motionEvent ->

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {

            }
        }

        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (themePref.holdAltByClick) {
                view.isSelected = !view.isSelected

                view.setBackgroundColor(if (view.isSelected)
                    ContextCompat.getColor(context, R.color.grey_button)
                else
                    themePref.selectedColor)
            } else {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_button))
            }

            viewModel.selectionMode.value = true
        } else if (motionEvent.action == MotionEvent.ACTION_UP) {
            if (!themePref.holdAltByClick) {
                viewModel.selectionMode.value = false
                view.setBackgroundColor(themePref.secondaryColor)
            }
        }
        true
    }

    /**
     * Used to show select dialog for non-expanded groups
     */
    class SubMenuDialog(context: Context,
                        private val menu: MenuItem,
                        private val listener: OnActionSelectedListener) :
            Dialog(context, R.style.Action_Dialog) {

        public override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            val view = View.inflate(context, R.layout.dialog_file_action_menu, null)

            val items = mutableListOf<String>()
            for (i in 0 until menu.subMenu.size()) {
                items.add(menu.subMenu.getItem(i).title as String)
            }

            val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, items) {

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val item = super.getView(position, convertView, parent)
                    item.minimumWidth = actionList.width
                    return item
                }
            }

            actionList.adapter = adapter

            actionList.setOnItemClickListener { _, _, position, _ ->
                dismiss()
                listener.onActionSelected(menu.subMenu.getItem(position))
            }

            setContentView(view)
        }

        interface OnActionSelectedListener {
            fun onActionSelected(item: MenuItem)
        }

    }
}