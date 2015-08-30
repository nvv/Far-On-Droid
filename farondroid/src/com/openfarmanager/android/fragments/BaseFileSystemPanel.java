package com.openfarmanager.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
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
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.*;
import com.openfarmanager.android.filesystem.actions.network.*;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.model.TaskStatusEnum;
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

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void handleNetworkActionResult(TaskStatusEnum status, Object[] args) {
        if (status != TaskStatusEnum.OK) {
            String error;
            error = status == TaskStatusEnum.ERROR_CREATE_DIRECTORY ?
                    getSafeString(R.string.error_cannot_create_file, (String) args[1]) : TaskStatusEnum.getErrorString(status);
            if (status == TaskStatusEnum.ERROR_NETWORK && status.getNetworkErrorException() != null) {
                error = status.getNetworkErrorException().getLocalizedError();
            }

            try {
                ErrorDialog.newInstance(error).show(fragmentManager(), "errorDialog");
            } catch (Exception ignore) {}
        }
        invalidatePanels((MainPanel) args[0]);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    protected void handleNetworkCopyActionResult(TaskStatusEnum status, Object[] args) {
        try {
            if (status != TaskStatusEnum.OK) {
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
                task = new RenameOnNetworkTask(((NetworkPanel) this).getNetworkType(), fragmentManager(),
                        new FileActionTask.OnActionListener() {
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
                        new FileActionTask.OnActionListener() {
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
            FileActionTask task = null;
            try {
                task = new CopyToNetworkTask(((NetworkPanel) args[0]).getNetworkType(), fragmentManager(),
                        new FileActionTask.OnActionListener() {
                            @Override
                            public void onActionFinish(TaskStatusEnum status) {
                                handleNetworkCopyActionResult(status, args);
                            }
                        }, getSelectedFiles(), (String) args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.execute();
        }
    };

    protected AbstractCommand mCopyFromNetworkCommand = new AbstractCommand() {

        @Override
        public void execute(final Object ... args) {
            FileActionTask task = null;
            try {
                task = new CopyFromNetworkTask(((NetworkPanel) BaseFileSystemPanel.this).getNetworkType(), fragmentManager(),
                        new FileActionTask.OnActionListener() {
                            @Override
                            public void onActionFinish(TaskStatusEnum status) {
                                handleNetworkCopyActionResult(status, args);
                            }
                        }, ((NetworkPanel) BaseFileSystemPanel.this).getFiles(), (String) args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.execute();
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
                        new FileActionTask.OnActionListener() {
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
                            new FileActionTask.OnActionListener() {
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
                FileActionTask task = null;
                try {
                    task = new MoveToNetworkTask(((NetworkPanel) args[0]).getNetworkType(), fragmentManager(),
                            new FileActionTask.OnActionListener() {
                                @Override
                                public void onActionFinish(TaskStatusEnum status) {
                                    invalidatePanels((MainPanel) args[0]);
                                }
                            }, getSelectedFiles(), ((MainPanel) args[0]).getCurrentPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                task.execute();
            }
        }
    };

    protected AbstractCommand mMoveFromNetworkCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            if ((Boolean) args[3]) {
                doRename(args, true);
            } else {
                FileActionTask task = null;
                try {
                    task = new MoveFromNetworkTask(((NetworkPanel) BaseFileSystemPanel.this).getNetworkType(), fragmentManager(),
                            new FileActionTask.OnActionListener() {
                                @Override
                                public void onActionFinish(TaskStatusEnum status) {
                                    invalidatePanels((MainPanel) args[0]);
                                }
                            }, getSelectedFileProxies(), ((MainPanel) args[0]).getCurrentPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                task.execute();
            }
        }
    };

    protected AbstractCommand mCreateBookmarkCommand = new AbstractCommand() {
        @Override
        public void execute(final Object... args) {
            TaskStatusEnum status = App.sInstance.getBookmarkManager().createBookmark((String) args[1], (String) args[3]);

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
                        new FileActionTask.OnActionListener() {
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

    public void requestSdcardPermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_REQUEST_PERMISSION);
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
}
