package com.openfarmanager.android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.CancelableCommand;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.dialogs.CopyMoveFileDialog;
import com.openfarmanager.android.dialogs.CreateArchiveDialog;
import com.openfarmanager.android.dialogs.ExtractArchiveDialog;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.multi.network.CopyFromNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.multi.network.CopyToNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.network.CopyFromNetworkTask;
import com.openfarmanager.android.filesystem.actions.network.CopyToNetworkTask;
import com.openfarmanager.android.filesystem.commands.CopyCommand;
import com.openfarmanager.android.filesystem.commands.CopyFromNetworkCommand;
import com.openfarmanager.android.filesystem.commands.CopyToNetworkCommand;
import com.openfarmanager.android.filesystem.commands.CreateArchiveCommand;
import com.openfarmanager.android.filesystem.commands.ExtractArchiveCommand;
import com.openfarmanager.android.filesystem.commands.MoveCommand;
import com.openfarmanager.android.filesystem.commands.MoveFromNetworkCommand;
import com.openfarmanager.android.filesystem.commands.MoveToNetworkCommand;
import com.openfarmanager.android.filesystem.commands.RenameCommand;
import com.openfarmanager.android.filesystem.commands.RenameOnNetworkCommand;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.utils.SystemUtils;
import com.openfarmanager.android.view.OnSwipeTouchListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.openfarmanager.android.controllers.FileSystemController.ARG_EXPAND_LEFT_PANEL;
import static com.openfarmanager.android.controllers.FileSystemController.ARG_EXPAND_RIGHT_PANEL;
import static com.openfarmanager.android.controllers.FileSystemController.EXPAND_PANEL;
import static com.openfarmanager.android.controllers.FileSystemController.GAIN_FOCUS;

/**
 * @author Vlad Namashko
 */
@SuppressWarnings("ConstantConditions")
public abstract class BaseFileSystemPanel extends BasePanel {

    public static final String ARG_PANEL_LOCATION = "arg_panel_location";

    public static final int REQUEST_CODE_REQUEST_PERMISSION = 442;

    protected File mLastSelectedFile;

    // TODO: temp
    protected String mEncryptedArchivePassword;
    protected ExtractArchiveDialog.ExtractArchiveResult mExtractArchiveResult;

    protected Handler mHandler;
    protected boolean mIsActivePanel;

//    protected int mPanelLocation = -1;

    protected HashMap<String, Integer> mDirectorySelection = new HashMap<String, Integer>();

    public void setupGestures(RecyclerView view) {

        view.addOnItemTouchListener(new OnSwipeTouchListener(view.getContext()) {

            public void onTouch() {
                gainFocus();
            }

            public void onSwipeLeft() {
                if (App.sInstance.getSettings().isFlexiblePanelsMode()) {
                    mHandler.sendMessage(mHandler.obtainMessage(EXPAND_PANEL, ARG_EXPAND_RIGHT_PANEL, 0));
                }
            }

            public void onSwipeRight() {
                if (App.sInstance.getSettings().isFlexiblePanelsMode()) {
                    mHandler.sendMessage(mHandler.obtainMessage(EXPAND_PANEL, ARG_EXPAND_LEFT_PANEL, 0));
                }
            }
        });

    }

    public FragmentManager fragmentManager() throws Exception {
        Activity parent = getActivity();
        FragmentManager result = null;
        if (parent != null) {
            if (!parent.isFinishing()) {
                result = getActivity().getSupportFragmentManager();
            }
        } else {
            result = getFragmentManager();
        }
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    public void gainFocus() {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            message.what = GAIN_FOCUS;
            message.arg1 = getPanelLocation();
            mHandler.sendMessage(message);
        }
    }

    public int getPanelLocation() {
        return getArguments().getInt(ARG_PANEL_LOCATION);
    }

    /**
     * To be overriden in sub classes.
     *
     * @return null
     */
    protected String getArchivePassword() {
        return mEncryptedArchivePassword;
    }

    /**
     * To be overriden in sub classes.
     *
     * @return null
     */
    public ArchiveScanner.File getCurrentArchiveItem() {
        return null;
    }

    public AbstractCommand getCopyToCommand(MainPanel inactivePanel, File destination) {
        if (inactivePanel instanceof NetworkPanel) {
            return new CopyToNetworkCommand((MainPanel) this, (NetworkPanel) inactivePanel);
        }
        if (this instanceof NetworkPanel) {
            return new CopyFromNetworkCommand((NetworkPanel) this, inactivePanel);
        }

        return new CopyCommand((MainPanel) this, destination);
    }

    public AbstractCommand getMoveCommand(MainPanel inactivePanel, CopyMoveFileDialog.CopyMoveFileResult result) {
        if (result.isRename) {
            if (this instanceof NetworkPanel) {
                return new RenameOnNetworkCommand(this, result.destination, getLastSelectedFile());
            } else {
                return new MoveCommand((MainPanel) this, result.inactivePanel.getCurrentDir(), result.destination, result.isRename, mLastSelectedFile);
            }
        }

        if (inactivePanel instanceof NetworkPanel) {
            return new MoveToNetworkCommand((MainPanel) this, (NetworkPanel) inactivePanel);
        } else if (this instanceof NetworkPanel) {
            return new MoveFromNetworkCommand((NetworkPanel) this, inactivePanel);
        }

        return new MoveCommand((MainPanel) this, result.inactivePanel.getCurrentDir(), result.destination, result.isRename, mLastSelectedFile);
    }

    protected AbstractCommand getCreateArchiveCommand(CreateArchiveDialog.CreateArchiveResult createArchiveResult) {
        return new CreateArchiveCommand((MainPanel) this,
                FileUtilsExt.addSeparatorToTheEnd(getCurrentDir().getAbsolutePath()) + createArchiveResult.archiveName, createArchiveResult.archiveType, createArchiveResult.isCompressionEnabled, createArchiveResult.compression);
    }

    protected AbstractCommand getExtractArchiveCommand(ExtractArchiveDialog.ExtractArchiveResult result, String password) {
        return new ExtractArchiveCommand((MainPanel) this, mLastSelectedFile, new File(result.destination),
                result.isCompressed, password, getCurrentArchiveItem());
    }

    protected AbstractCommand mSelectFilesCommand = (AbstractCommand) args -> select((SelectParams) args[0]);

    protected CancelableCommand mRequestPasswordCommand = new CancelableCommand() {

        @Override
        public void execute(final Object... args) {
            mEncryptedArchivePassword = (String) args[0];
            getExtractArchiveCommand(mExtractArchiveResult, mEncryptedArchivePassword).execute();
            mEncryptedArchivePassword = null;
        }

        @Override
        public void cancel() {

        }
    };

    public void requestSdcardPermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                RequestPermissionFragment.newInstance(getString(R.string.sd_card_access_request), new YesNoDialog.YesNoDialogListener() {
                    @Override
                    public void yes() {
                        try {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            startActivityForResult(intent, REQUEST_CODE_REQUEST_PERMISSION);
                        } catch (Exception e) {
                            // unexpected case, just ignore it
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void no() {
                    }
                }).show(fragmentManager(), "errorDialog");
            } catch (Exception ignore) {}
        }
    }

    protected void adjustDialogSize(Dialog dialog) {
        adjustDialogSize(dialog, 0.8f);
    }

    /**
     * Adjust dialog size. Actuall for old android version only (due to absence of Holo themes).
     *
     * @param dialog dialog whose size should be adjusted.
     */
    protected void adjustDialogSize(Dialog dialog, float scaleFactor) {
        if (!SystemUtils.isHoneycombOrNever()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = (int) (metrics.widthPixels * scaleFactor);
            params.height = (int) (metrics.heightPixels * scaleFactor);

            dialog.getWindow().setAttributes(params);
        }
    }

    public abstract FileProxy getLastSelectedFile();

    public abstract List<FileProxy> getSelectedFileProxies();

    public abstract List<File> getSelectedFiles();

    public abstract File getCurrentDir();

    protected abstract String getCurrentPath();

    public abstract int select(SelectParams selectParams);

    public abstract void openDirectory(String path);

    protected abstract void invalidatePanels(MainPanel inactivePanel);

    protected abstract void invalidatePanels(MainPanel inactivePanel, boolean force);
}
