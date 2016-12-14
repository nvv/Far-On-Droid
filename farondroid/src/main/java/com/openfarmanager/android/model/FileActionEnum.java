package com.openfarmanager.android.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemFile;
import com.openfarmanager.android.filesystem.GoogleDriveFile;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public enum FileActionEnum implements Parcelable, Serializable {

    INFO, SEND, COPY, EDIT, DELETE, MOVE, NEW, SELECT, RENAME, FILE_OPEN_WITH, ARCHIVE_EXTRACT,
    CREATE_BOOKMARK, CREATE_ARCHIVE, OPEN, SET_AS_HOME, EXPORT_AS, OPEN_WEB, SHARE, COPY_PATH, ADD_STAR, REMOVE_STAR;

    private static FileActionEnum[] sActionsForMultipleSelectedItems = new FileActionEnum[] {COPY, SET_AS_HOME, MOVE, DELETE, CREATE_ARCHIVE};
    private static FileActionEnum[] sActionsForDirectory = new FileActionEnum[] {INFO, SET_AS_HOME, COPY, MOVE, DELETE, RENAME, COPY_PATH, CREATE_ARCHIVE};
    private static FileActionEnum[] sActionsForFile = new FileActionEnum[] {INFO, SET_AS_HOME, SEND, COPY, MOVE, DELETE, EDIT, RENAME, COPY_PATH, FILE_OPEN_WITH, CREATE_ARCHIVE};

    private static FileActionEnum[] sActionsForNetwork = new FileActionEnum[] {COPY, MOVE, DELETE, RENAME};

    // google drive
    private static FileActionEnum[] sActionsForNetworkWithExport = new FileActionEnum[] {COPY, MOVE, EXPORT_AS, DELETE, RENAME};
    private static FileActionEnum[] sActionsForNetworkWithOpenWith = new FileActionEnum[] {COPY, MOVE, OPEN_WEB, DELETE, RENAME};
    private static FileActionEnum[] sActionsForNetworkWithExportAndOpenWith = new FileActionEnum[] {COPY, MOVE, EXPORT_AS, OPEN_WEB, DELETE, RENAME};

    public static FileActionEnum[] getAvailableActionsForNetwork(NetworkEnum networkType, List<FileProxy> selectedFiles) {
        FileActionEnum[] actionEnums = selectActionsForNetwork(networkType, selectedFiles);

        if (selectedFiles.size() == 1) {
            FileActionEnum[] actions = new FileActionEnum[actionEnums.length + 1];
            System.arraycopy(actionEnums, 0, actions, 0, actionEnums.length);
            actions[actionEnums.length] = COPY_PATH;
            return actions;
        }

        return actionEnums;
    }

    private static FileActionEnum[] selectActionsForNetwork(NetworkEnum networkType, List<FileProxy> selectedFiles) {
        boolean isSingleFileSelected = selectedFiles != null && selectedFiles.size() == 1;
        if (networkType == NetworkEnum.GoogleDrive && isSingleFileSelected) {
            GoogleDriveFile file = (GoogleDriveFile) selectedFiles.get(0);

            boolean provideExport = file.getExportLinks() != null && file.getExportLinks().size() > 0;
            boolean provideOpenWith = file.hasOpenWithLink();

            FileActionEnum[] actions = null;
            if (provideExport && provideOpenWith) {
                actions = sActionsForNetworkWithExportAndOpenWith;
            } else if (provideExport) {
                actions = sActionsForNetworkWithExport;
            } else if (provideOpenWith) {
                actions = sActionsForNetworkWithOpenWith;
            } else {
                actions = sActionsForNetwork;
            }

            if (actions != null) {
                FileActionEnum[] allActions = new FileActionEnum[actions.length + 1];
                System.arraycopy(actions, 0, allActions, 0, 2);
                allActions[2] = file.isStarred() ? REMOVE_STAR : ADD_STAR;
                System.arraycopy(actions, 2, allActions, 3, actions.length - 2);
                return allActions;
            }
        } else if (networkType == NetworkEnum.Dropbox && isSingleFileSelected) {
            FileActionEnum[] allActions = new FileActionEnum[sActionsForNetwork.length + 1];
            allActions[0] = SHARE;
            System.arraycopy(sActionsForNetwork, 0, allActions, 1, sActionsForNetwork.length);
            return allActions;
        }

        return sActionsForNetwork;
    }

    public static FileActionEnum[] getAvailableActions(List<File> selectedFiles, File lastSelectedFile) {
        boolean oneFileSelected = selectedFiles.size() == 1 && (lastSelectedFile != null && lastSelectedFile.isFile());

        FileActionEnum[] actions;

        if (selectedFiles.size() == 1 && lastSelectedFile instanceof FileSystemFile) {
            FileSystemFile file = (FileSystemFile) lastSelectedFile;
            if (file.isBookmark()) {
                actions = new FileActionEnum[1];
                //actions[0] = RENAME;
                actions[0] = DELETE;
                return actions;
            }
        }

        if (oneFileSelected) {
            boolean extractSupport = ArchiveUtils.isArchiveSupported(lastSelectedFile) ||
                    ArchiveUtils.isCompressionSupported(lastSelectedFile);
            if (extractSupport) {
                actions = new FileActionEnum[sActionsForFile.length + 1];
                System.arraycopy(sActionsForFile, 0, actions, 0, sActionsForFile.length);
                actions[sActionsForFile.length] = FileActionEnum.ARCHIVE_EXTRACT;
            } else {
                actions = sActionsForFile;
            }
        } else {
            actions = selectedFiles.size() == 1 ? sActionsForDirectory : sActionsForMultipleSelectedItems;
        }
        return actions;
    }

    public String getName() {

        Resources res = App.sInstance.getResources();
        switch (this) {
            case INFO:
                return res.getString(R.string.action_info);
            case SET_AS_HOME:
                return res.getString(R.string.action_set_as_home);
            case SEND:
                return res.getString(R.string.action_send);
            case COPY:
                return res.getString(R.string.action_copy);
            case EDIT:
                return res.getString(R.string.action_edit);
            case DELETE:
                return res.getString(R.string.action_delete);
            case MOVE:
                return res.getString(R.string.action_move);
            case RENAME:
                return res.getString(R.string.action_rename);
            case FILE_OPEN_WITH:
                return res.getString(R.string.action_open_with);
            case ARCHIVE_EXTRACT:
                return res.getString(R.string.action_archive_extract);
            case CREATE_ARCHIVE:
                return res.getString(R.string.action_create_archive);
            case OPEN:
                return res.getString(R.string.action_open);
            case EXPORT_AS:
                return res.getString(R.string.export_as);
            case OPEN_WEB:
                return res.getString(R.string.open_with);
            case SHARE:
                return res.getString(R.string.action_share);
            case ADD_STAR:
                return res.getString(R.string.add_star);
            case REMOVE_STAR:
                return res.getString(R.string.remove_star);
            case COPY_PATH:
                return res.getString(R.string.action_copy_path);
        }

        return "";
    }

    public static String[] names(FileActionEnum[] actions) {

        String[] names = new String[actions.length];
        int i = 0;
        for (FileActionEnum action : actions) {
            names[i++] = action.getName();
        }
        
        return names;
    }

    public static final Parcelable.Creator<FileActionEnum> CREATOR = new Parcelable.Creator<FileActionEnum>() {
        public FileActionEnum createFromParcel(Parcel source) {
            return FileActionEnum.values()[source.readInt()];
        }

        public FileActionEnum[] newArray(int size) {
            throw new UnsupportedOperationException();
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ordinal());
    }
}
