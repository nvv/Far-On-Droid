package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class ChangedItemsModel {
    private FileModel[] files;
    private FolderModel[] folders;

    public FileModel[] getFiles() {
        return files;
    }

    public FolderModel[] getFolders() {
        return folders;
    }
}
