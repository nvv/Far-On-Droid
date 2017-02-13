package com.openfarmanager.android.view.panels;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.toolbar.MenuBuilder;
import com.openfarmanager.android.toolbar.MenuItemImpl;
import com.openfarmanager.android.utils.SystemUtils;

import java.util.ArrayList;

import static com.openfarmanager.android.controllers.FileSystemController.ALT_DOWN;
import static com.openfarmanager.android.controllers.FileSystemController.ALT_UP;
import static com.openfarmanager.android.controllers.FileSystemController.APPLAUNCHER;
import static com.openfarmanager.android.controllers.FileSystemController.BOOKMARKS;
import static com.openfarmanager.android.controllers.FileSystemController.DIFF;
import static com.openfarmanager.android.controllers.FileSystemController.EXIT;
import static com.openfarmanager.android.controllers.FileSystemController.HELP;
import static com.openfarmanager.android.controllers.FileSystemController.MENU;
import static com.openfarmanager.android.controllers.FileSystemController.NETWORK;
import static com.openfarmanager.android.controllers.FileSystemController.NEW;
import static com.openfarmanager.android.controllers.FileSystemController.QUICKVIEW;
import static com.openfarmanager.android.controllers.FileSystemController.SEARCH;
import static com.openfarmanager.android.controllers.FileSystemController.SELECT;
import static com.openfarmanager.android.controllers.FileSystemController.SETTINGS;

public class MainToolbar extends LinearLayout {

    public static final SparseIntArray sActions = new SparseIntArray();

    static {
        sActions.put(R.id.action_select, SELECT);
        sActions.put(R.id.action_new, NEW);
        sActions.put(R.id.menu_action, MENU);
        sActions.put(R.id.action_quckview, QUICKVIEW);
        sActions.put(R.id.action_exit, EXIT);
        sActions.put(R.id.action_diff, DIFF);
        sActions.put(R.id.action_find, SEARCH);
        sActions.put(R.id.action_help, HELP);
        sActions.put(R.id.action_settings, SETTINGS);
        sActions.put(R.id.action_network, NETWORK);
        sActions.put(R.id.action_applauncher, APPLAUNCHER);
        sActions.put(R.id.action_bookmarks, BOOKMARKS);
    }

    private float mDensity;
    private int mMinWidth;

    private Handler mHandler;
    private MenuBuilder mMenu;
    private int mItemsCount;

    private View mAltView;
    private View mApplicationsView;
    private View mQuickView;
    private View mMoreView;
    private View mSelectView;

    private OnClickListener mClickListener = view -> {
        MenuItem item = (MenuItem) view.getTag();
        if (item.hasSubMenu()) {
            showSubMenu(item);
        } else {
            sendMessage(item);
        }
    };

    public MainToolbar(Context context) {
        super(context);
        initMenu(context);
    }

    public MainToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMenu(context);
    }

    public MainToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMenu(context);
    }

    public View getAltView() {
        return mAltView;
    }

    public View getApplicationsView() {
        return mApplicationsView;
    }

    public View getQuickView() {
        return mQuickView;
    }

    public View getMoreView() {
        return mMoreView;
    }

    public View getSelectView() {
        return mSelectView;
    }

    private void sendMessage(MenuItem item) {
        sendMessage(sActions.get(item.getItemId()));
    }

    View.OnTouchListener mAltListener = (view, motionEvent) -> {
        Settings settings = App.sInstance.getSettings();
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (!SystemUtils.isHoneycombOrNever() || settings.isHoldAltOnTouch()) {
                view.setSelected(!view.isSelected());
                //noinspection ResourceAsColor
                view.setBackgroundColor(view.isSelected() ?
                        Color.parseColor(App.sInstance.getString(R.color.grey_button)) : settings.getSecondaryColor());
            } else {
                view.setBackgroundColor(Color.parseColor(App.sInstance.getString(R.color.grey_button)));
            }
            sendMessage(ALT_DOWN);

        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (SystemUtils.isHoneycombOrNever() && !settings.isHoldAltOnTouch()) {
                sendMessage(ALT_UP);
                view.setBackgroundColor(settings.getSecondaryColor());
            }
        }
        return true;
    };

    private void showSubMenu(MenuItem item) {
        final Dialog dialog = SubMenuDialog.newInstance(getContext(), item, this::sendMessage);
        dialog.show();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw) {
            buildMenu(false);
        }
    }

    public void buildMenu(boolean forceRedraw) {
        int itemsCount = getMeasuredWidth() / mMinWidth;
        if (itemsCount == mItemsCount && !forceRedraw) {
            return;
        }

        removeAllViews();

        mItemsCount = itemsCount;
        int used = mMenu.size();

        SparseArray<TextView> mViews = new SparseArray<TextView>();

        for (int i = 0; i < mMenu.size(); i++) {
            MenuItem item = mMenu.getItem(i);
            TextView view = getTextView(item);
            mViews.put(i * 100, view);

            switch (item.getItemId()) {
                case R.id.action_alt:
                    mAltView = view;
                    break;
                case R.id.action_applauncher:
                    mApplicationsView = view;
                    break;
                case R.id.action_quckview:
                    mQuickView = view;
                    break;
                case R.id.menu_more:
                    mMoreView = view;
                    break;
                case R.id.action_select:
                    mSelectView = view;
                    break;
            }

        }

        int expanded = 0;
        while (true) {
            if (expanded == mMenu.size() || used > mItemsCount) {
                break;
            }

            MenuItem item = mMenu.getItem(expanded);
            expanded++;
            if (!item.hasSubMenu()) {
                continue;
            }
            if (used + item.getSubMenu().size() > mItemsCount) {
                continue;
            }

            used += item.getSubMenu().size() - 1;

            int index = 0;
            for (int i = 0; i < mViews.size(); i++) {
                if (mViews.valueAt(i).getTag().equals(item)) {
                    index = mViews.keyAt(i);
                    mViews.remove(index);
                }
            }

            for (int i = 0; i < item.getSubMenu().size(); i++) {
                MenuItem sub = item.getSubMenu().getItem(i);
                sub.getOrder();
                mViews.put(index + i, getTextView(sub));
            }
        }

        for (int i = 0; i < mViews.size(); i++) {
            addView(mViews.valueAt(i));
        }

        post(this::requestLayout);
    }

    private TextView getTextView(MenuItem item) {
        int threedip = (int) (3 * mDensity);
        Settings settings = App.sInstance.getSettings();
        int size = settings.getBottomPanelFontSize();
        TextView view = new TextView(getContext());
        view.setTypeface(settings.getMainPanelFontType());
        view.setText(item.getTitle());
        view.setGravity(Gravity.CENTER);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
        layoutParams.setMargins(threedip, 0, threedip, 0);
        view.setLayoutParams(layoutParams);
        view.setTag(item);
        if (item.getItemId() == R.id.action_alt) {
            view.setOnTouchListener(mAltListener);
        } else {
            view.setOnClickListener(mClickListener);
        }
        view.setBackgroundColor(settings.getSecondaryColor());
        view.setTextColor(getResources().getColor(R.color.black));
        view.setSingleLine();
        view.setPadding(threedip, threedip, threedip, threedip);
        view.setHeight((int) ((6 + 2 * size) * mDensity));
        view.setMinWidth((int) (80 * mDensity));
        return view;
    }

    private void initMenu(Context context) {
        mDensity = getResources().getDisplayMetrics().density;
        mMinWidth = (int) (80 * mDensity);

        mMenu = new MenuBuilder(context);
        int res = context.getResources().getIdentifier("main", "menu", context.getPackageName());
        new MenuInflater(context).inflate(res, mMenu);
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void sendMessage(int what) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(what);
        }
    }

    /**
     * Used to show select dialog for non-expanded groups
     */
    public static class SubMenuDialog extends Dialog {

        private MenuItem mMenu;
        private ArrayList<MenuItemImpl> mMenuItems = new ArrayList<>();
        private SubMenuDialog.OnActionSelectedListener mListener;

        public SubMenuDialog(Context context) {
            super(context, R.style.Action_Dialog);
        }

        public static SubMenuDialog newInstance(Context context, ArrayList<MenuItemImpl> items, OnActionSelectedListener listener) {
            SubMenuDialog dialog = new SubMenuDialog(context);
            dialog.mMenuItems.addAll(Stream.of(items).filter(item -> item.getItemId() != R.id.action_alt).collect(Collectors.toList()));
            dialog.mListener = listener;
            return dialog;
        }

        public static SubMenuDialog newInstance(Context context, MenuItem menu, OnActionSelectedListener listener) {
            SubMenuDialog dialog = new SubMenuDialog(context);
            dialog.mMenu = menu;
            dialog.mListener = listener;
            return dialog;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            View view = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_file_action_menu, null);

            final ListView actionsList = (ListView) view.findViewById(R.id.action_list);

            String[] items;
            if (mMenu != null) {
                items = new String[mMenu.getSubMenu().size()];
                for (int i = 0; i < mMenu.getSubMenu().size(); i++) {
                    MenuItem sub = mMenu.getSubMenu().getItem(i);
                    items[i] = (String) sub.getTitle();
                }
            } else {
                items = new String[mMenuItems.size()];
                int i = 0;
                for (MenuItemImpl menuItem : mMenuItems) {
                    items[i++] = (String) menuItem.getTitle();
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    App.sInstance.getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, items) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View item = super.getView(position, convertView, parent);
                    item.setMinimumWidth(actionsList.getWidth());
                    return item;
                }
            };

            actionsList.setAdapter(adapter);

            actionsList.setOnItemClickListener((adapterView, view1, i, l) -> {
                dismiss();
                mListener.onActionSelected(mMenu != null ? mMenu.getSubMenu().getItem(i) : mMenuItems.get(i));
            });

            setContentView(view);
        }

        public void setMenu(MenuItem menu) {
            mMenu = menu;
        }

        public void setListener(SubMenuDialog.OnActionSelectedListener listener) {
            mListener = listener;
        }

        public interface OnActionSelectedListener {
            void onActionSelected(MenuItem item);
        }
    }
}
