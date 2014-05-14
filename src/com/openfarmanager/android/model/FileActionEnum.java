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
    CREATE_BOOKMARK, CREATE_ARCHIVE, OPEN, SET_AS_HOME, EXPORT_AS;

    private static FileActionEnum[] sActionsForMultipleSelectedItems = new FileActionEnum[] {COPY, SET_AS_HOME, MOVE, DELETE, CREATE_ARCHIVE};
    private static FileActionEnum[] sActionsForDirectory = new FileActionEnum[] {INFO, SET_AS_HOME, COPY, MOVE, DELETE, RENAME, CREATE_ARCHIVE};
    private static FileActionEnum[] sActionsForFile = new FileActionEnum[] {INFO, SET_AS_HOME, SEND, COPY, MOVE, DELETE, EDIT, RENAME, FILE_OPEN_WITH, CREATE_ARCHIVE};
    private static FileActionEnum[] sActionsForNetwork = new FileActionEnum[] {COPY, MOVE, DELETE, RENAME};
    private static FileActionEnum[] sActionsForNetworkWithExport = new FileActionEnum[] {COPY, MOVE, EXPORT_AS, DELETE, RENAME};

    public static FileActionEnum[] getAvailableActionsForNetwork(NetworkEnum networkType, List<FileProxy> selectedFiles) {
        if (networkType == NetworkEnum.GoogleDrive && selectedFiles != null && selectedFiles.size() == 1) {
            GoogleDriveFile file = (GoogleDriveFile) selectedFiles.get(0);

            if (file.getExportLinks() != null && file.getExportLinks().size() > 0) {
                return sActionsForNetworkWithExport;
            }
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
