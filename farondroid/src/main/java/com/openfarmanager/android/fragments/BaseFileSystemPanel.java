package com.openfarmanager.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.Toast;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.CancelableCommand;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.dialogs.YesNoDontAskAgainDialog;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.*;
import com.openfarmanager.android.filesystem.actions.multi.network.CopyFromNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.multi.network.CopyToNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.multi.network.MoveFromNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.multi.network.MoveToNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.network.*;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.utils.SystemUtils;
import com.openfarmanager.android.view.OnSwipeTouchListener;
import com.openfarmanager.android.view.ToastNotification;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.openfarmanager.android.controllers.FileSystemController.*;

/**
 * @author Vlad Namashko
 */
@SuppressWarnings("ConstantConditions")
public abstract class BaseFileSystemPanel extends BasePanel {

    public static final int REQUEST_CODE_REQUEST_PERMISSION = 442;

    protected File mLastSelectedFile;

    protected String mEncryptedArchivePassword;

    protected Handler mHandler;
    protected int mPanelLocation;

    protected HashMap<String, Integer> mDirectorySelection = new HashMap<String, Integer>();

    public void setupGestures(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener() {
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

    protected boolean openNavigationPathPopup(View view) {
        final List<String> items = new ArrayList<String>(Arrays.asList(getCurrentPath().split("/")));
        if (items.size() == 0) {
            return false;
        }
        items.set(0, "/");
        items.remove(items.size() - 1);

        if (Build.VERSION.SDK_INT > 10) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item, items);
            final ListPopupWindow select = new ListPopupWindow(getActivity());
            select.setBackgroundDrawable(getResources().getDrawable(R.drawable.panel_path_background));
            select.setAnchorView(view);
            select.setAdapter(adapter);
            select.setModal(true);
            select.setWidth(400);
            select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    select.dismiss();
                    onNavigationItemSelected(pos, items);
                }
            });
            select.show();
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice, items);
            new AlertDialog.Builder(getActivity())
                    .setSingleChoiceItems(adapter, 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onNavigationItemSelected(which, items);
                                }
                            })

                    .show();
        }

        return true;
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
            message.arg1 = mPanelLocation;
            mHandler.sendMessage(message);
        }
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

    public AbstractCommand getCopyToCommand(MainPanel inactivePanel) {
        if (inactivePanel instanceof NetworkPanel) {
            return mCopyToNetworkCommand;
        }
        if (this instanceof NetworkPanel) {
            return mCopyFromNetworkCommand;
        }

        return mCopyCommand;
    }

    public AbstractCommand getMoveCommand(MainPanel inactivePanel) {
        if (inactivePanel instanceof NetworkPanel) {
            return mMoveToNetworkCommand;
        } else if (this instanceof NetworkPanel) {
            return mMoveFromNetworkCommand;
        }

        return mMoveCommand;
    }

    private void doRename(Object[] args) {
        doRename(args, false);
    }

    public void handleNetworkActionResult(TaskStatusEnum status, Object[] args) {
        handleNetworkActionResult(status, true, args);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void handleNetworkActionResult(TaskStatusEnum status, boolean forceInvalidate, Object[] args) {
        if (status != TaskStatusEnum.OK) {

            if (status == TaskStatusEnum.ERROR_FTP_DELETE_DIRECTORY) { // special case for s/ftp
                if (!App.sInstance.getSettings().isFtpAllowRecursiveDelete() && App.sInstance.getSettings().allowedToAskRecursiveDelete()) {
                    new YesNoDontAskAgainDialog(getActivity()).show();
                    return;
                }
            }

            String error;
            error = status == TaskStatusEnum.ERROR_CREATE_DIRECTORY ?
                    getSafeString(R.string.error_cannot_create_file, (String) args[1]) : TaskStatusEnum.getErrorString(status, (String) args[1]);
            if (status == TaskStatusEnum.ERROR_NETWORK && status.getNetworkErrorException() != null) {
                error = status.getNetworkErrorException().getLocalizedError();
            }

            try {
                ErrorDialog.newInstance(error).show(fragmentManager(), "errorDialog");
            } catch (Exception ignore) {}
        }
        invalidatePanels((MainPanel) args[0], forceInvalidate);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    protected void handleNetworkCopyActionResult(TaskStatusEnum status, Object[] args) {
        try {
            if (status != TaskStatusEnum.OK) {
                if (checkIfPermissionRequired(status)) return;

                String error = status == TaskStatusEnum.ERROR_COPY || status == TaskStatusEnum.ERROR_FILE_NOT_EXISTS ?
                        App.sInstance.getString(R.string.error_cannot_copy_files, args[1]):
                        TaskStatusEnum.getErrorString(status);
                if (status == TaskStatusEnum.ERROR_NETWORK && status.getNetworkErrorException() != null) {
                    error = status.getNetworkErrorException().getLocalizedError();
                }
                ErrorDialog.newInstance(error).show(fragmentManager(), "errorDialog");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        invalidatePanels(((MainPanel) args[0]));
    }

    private void doRename(final Object[] args, boolean networkPanel) {
        if (networkPanel) {
            RenameOnNetworkTask task = null;
            try {
                task = new RenameOnNetworkTask(BaseFileSystemPanel.this,
                        new OnActionListener() {
                            @Override
                            public void onActionFinish(TaskStatusEnum status) {
                                handleNetworkActionResult(status, args);
                            }
                        }, getLastSelectedFile(), (String) args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (task != null) {
                task.execute();
            }
        } else {
            TaskStatusEnum status = new RenameTask(mLastSelectedFile, (String) args[1]).execute();
            if (status != TaskStatusEnum.OK) {
                if (checkIfPermissionRequired(status)) return;

                try {
                    ErrorDialog.newInstance(TaskStatusEnum.getErrorString(status)).show(fragmentManager(), "errorDialog");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            invalidatePanels((MainPanel) args[0]);
        }
    }

    protected AbstractCommand mCopyCommand = new AbstractCommand() {

        @Override
        public void execute(final Object ... args) {
            FileActionTask task = null;
            try {
                task = new CopyTask(fragmentManager(),
                        new OnActionListener() {
                            @Override
                            public void onActionFinish(TaskStatusEnum status) {
                                try {
                                    if (!status.equals(TaskStatusEnum.OK)) {
                                        if (checkIfPermissionRequired(status)) return;

                                        ErrorDialog.newInstance(
                                                status.equals(TaskStatusEnum.ERROR_COPY) ?
                                                        App.sInstance.getString(R.string.error_cannot_copy_files, args[1]):
                                                        String.format(TaskStatusEnum.getErrorString(status), args[1])).
                                                show(fragmentManager(), "errorDialog");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                invalidatePanels(((MainPanel) args[0]));
                            }
                        }, getSelectedFiles(), new File((String) args[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.execute();
        }
    };

    private boolean checkIfPermissionRequired(TaskStatusEnum status) {
        if (status == TaskStatusEnum.ERROR_STORAGE_PERMISSION_REQUIRED) {
            BaseFileSystemPanel.this.requestSdcardPermission();
            return true;
        }
        return false;
    }

    protected AbstractCommand mCopyToNetworkCommand = new AbstractCommand() {

        @Override
        public void execute(final Object ... args) {
            try {
                NetworkPanel networkPanel = (NetworkPanel) args[0];
                NetworkEnum type = networkPanel.getNetworkType();
                List<File> files = getSelectedFiles();
                if (App.sInstance.getSettings().isMultiThreadTasksEnabled(type)) {
                    new CopyToNetworkMultiTask(networkPanel,
                            createListener(args), files, (String) args[1]).execute();
                } else {
                    new CopyToNetworkTask(networkPanel,
                            createListener(args), files, (String) args[1]).execute();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected AbstractCommand mCopyFromNetworkCommand = new AbstractCommand() {

        @Override
        public void execute(final Object ... args) {

            NetworkPanel panel = (NetworkPanel) BaseFileSystemPanel.this;
            NetworkEnum type = panel.getNetworkType();
            String destination = (String) args[1];
            try {
                if (App.sInstance.getSettings().isMultiThreadTasksEnabled(type)) {
                    new CopyFromNetworkMultiTask(panel, createListener(args),
                            panel.getFiles(), destination).execute();
                } else {
                    new CopyFromNetworkTask(panel, createListener(args),
                            panel.getFiles(), destination).execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected AbstractCommand mSelectFilesCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            select((SelectParams) args[0]);
        }
    };

    protected AbstractCommand mExtractArchiveCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            FileActionTask task = null;
            try {
                task = new ExtractArchiveTask(fragmentManager(),
                        new OnActionListener() {
                            @Override
                            public void onActionFinish(TaskStatusEnum status) {
                                if (status == TaskStatusEnum.OK) {
                                    // all is ok, ignore
                                } else if (status == TaskStatusEnum.ERROR_EXTRACTING_ARCHIVE_FILES_ENCRYPTION_PASSWORD_REQUIRED) {
                                    try {
                                        RequestPasswordDialog.newInstance(mRequestPasswordCommand, args).show(fragmentManager(), "confirmDialog");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    if (checkIfPermissionRequired(status)) return;
                                    try {
                                        ErrorDialog.newInstance(TaskStatusEnum.getErrorString(status)).show(fragmentManager(), "error");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                invalidatePanels((MainPanel) args[0]);
                            }
                        }, mLastSelectedFile, new File((String) args[1]), (Boolean) args[3], getArchivePassword(), getCurrentArchiveItem());
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.execute();
        }
    };

    protected AbstractCommand mMoveCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            if ((Boolean) args[3]) {
                doRename(args);
            } else {
                FileActionTask task = null;
                try {
                    task = new MoveTask(fragmentManager(),
                            new OnActionListener() {
                                @Override
                                public void onActionFinish(TaskStatusEnum status) {
                                    if (!status.equals(TaskStatusEnum.OK)) {
                                        if (checkIfPermissionRequired(status)) return;
                                        try {
                                            ErrorDialog.newInstance(TaskStatusEnum.getErrorString(status)).show(fragmentManager(), "error");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    invalidatePanels((MainPanel) args[0]);
                                }
                            }, getSelectedFiles(), ((MainPanel) args[0]).getCurrentDir(), (String) args[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                task.execute();
            }
        }
    };

    protected AbstractCommand mMoveToNetworkCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            if ((Boolean) args[3]) {
                doRename(args, true);
            } else {
                try {
                    NetworkPanel panel = (NetworkPanel) args[0];
                    NetworkEnum type = panel.getNetworkType();
                    if (App.sInstance.getSettings().isMultiThreadTasksEnabled(type)) {
                        new MoveToNetworkMultiTask(panel, createListener(args),
                                getSelectedFiles(), panel.getCurrentPath()).execute();
                    } else {
                        new MoveToNetworkTask(panel, createListener(args),
                                getSelectedFiles(), panel.getCurrentPath()).execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    protected AbstractCommand mMoveFromNetworkCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            if ((Boolean) args[3]) {
                doRename(args, true);
            } else {
                try {
                    NetworkEnum type = ((NetworkPanel) BaseFileSystemPanel.this).getNetworkType();
                    String destination = ((MainPanel) args[0]).getCurrentPath();
                    if (App.sInstance.getSettings().isMultiThreadTasksEnabled(type)) {
                        new MoveFromNetworkMultiTask(BaseFileSystemPanel.this, createListener(args),
                                getSelectedFileProxies(), destination).execute();
                    } else {
                        new MoveFromNetworkTask(BaseFileSystemPanel.this, createListener(args),
                                getSelectedFileProxies(), destination).execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    protected AbstractCommand mCreateBookmarkCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            TaskStatusEnum status = App.sInstance.getBookmarkManager().createBookmark((String) args[1],
                    (String) args[3], (NetworkAccount) args[4]);

            if (status != TaskStatusEnum.OK) {
                try {
                    ErrorDialog.newInstance(TaskStatusEnum.getErrorString(status)).show(fragmentManager(), "errorDialog");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        getSafeString(R.string.bookmark_created), Toast.LENGTH_SHORT).show();
            }

            invalidatePanels((MainPanel) args[0]);
        }
    };

    protected AbstractCommand mCreateArchiveCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            String currentPath = getCurrentDir().getAbsolutePath();
            if (!currentPath.endsWith(File.separator)) {
                currentPath += File.separator;
            }

            FileActionTask task = null;
            try {
                task = new CreateArchiveTask(fragmentManager(),
                        new OnActionListener() {
                            @Override
                            public void onActionFinish(TaskStatusEnum status) {
                                if (!status.equals(TaskStatusEnum.OK)) {
                                    if (checkIfPermissionRequired(status)) return;
                                    try {
                                        ErrorDialog.newInstance(TaskStatusEnum.getErrorString(status)).show(fragmentManager(), "error");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                invalidatePanels((MainPanel) args[0]);
                            }
                        }, getSelectedFiles(), currentPath + args[1],
                        (ArchiveUtils.ArchiveType) args[2],
                        (Boolean) args[3], (ArchiveUtils.CompressionEnum) args[4]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.execute();
        }
    };

    protected CancelableCommand mRequestPasswordCommand = new CancelableCommand() {

        @Override
        public void execute(final Object... args) {
            mEncryptedArchivePassword = (String) args[0];
            mExtractArchiveCommand.execute((args.length > 1) ? (Object[]) args[1] : null);
            mEncryptedArchivePassword = null;
        }

        @Override
        public void cancel() {

        }
    };

    private OnActionListener createListener(final Object[] args) {
        return new OnActionListener() {
            @Override
            public void onActionFinish(TaskStatusEnum status) {
                handleNetworkActionResult(status, args);
            }
        };
    }

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

    protected abstract void onNavigationItemSelected(int pos, List<String> items);

    protected abstract void invalidatePanels(MainPanel inactivePanel);

    protected abstract void invalidatePanels(MainPanel inactivePanel, boolean force);
}
