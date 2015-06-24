package com.mediafire.sdk.api.responses.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class ChangedItems {
    private File[] files;
    private Folder[] folders;

    public File[] getFiles() {
        return files;
    }

    public Folder[] getFolders() {
        return folders;
    }
}
