package com.mediafire.sdk.api.responses.data_models;

import java.util.LinkedList;
import java.util.List;

/**
* Created by Chris on 5/14/2015.
*/
public class FolderContents {
    public String chunk_size;
    public String content_type;
    public String chunk_number;
    public List<Folder> folders;
    public List<File> files;

    public List<Folder> getFolders() {
        if (this.folders == null) {
            this.folders = new LinkedList<Folder>();
        }
        return this.folders;
    }

    public List<File> getFiles() {
        if (this.files == null) {
            this.files = new LinkedList<File>();
        }
        return this.files;
    }

    public int getChunkSize() {
        if (this.chunk_size == null) {
            this.chunk_size = "0";
        }
        return Integer.valueOf(chunk_size);
    }

    public String getContentType() {
        if (this.content_type == null) {
            this.content_type = "";
        }
        return content_type;
    }

    public int getChunkNumber() {
        if (this.chunk_number == null) {
            this.chunk_number = "0";
        }
        return Integer.valueOf(this.chunk_number);
    }
}
