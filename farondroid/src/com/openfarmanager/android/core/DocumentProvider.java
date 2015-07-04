package com.openfarmanager.android.core;

import android.database.Cursor;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsProvider;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Vlad Namashko
 */
public class DocumentProvider extends DocumentsProvider {

    private static String TAG = "::::: >>>";
    private File mBaseDir;
    private static final String ROOT = "root";

    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        return null;
    }

    @Override
    public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
        return null;
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
        return null;
    }

    @Override
    public ParcelFileDescriptor openDocument(String documentId, String mode, CancellationSignal signal) throws FileNotFoundException {
        return null;
    }

    @Override
    public boolean onCreate() {
        Log.v(TAG , "on create");
        mBaseDir = getContext().getFilesDir();
        return false;
    }

    @Override
    public String createDocument(String documentId, String mimeType, String displayName)
            throws FileNotFoundException {
        Log.v(TAG, "createDocument");

        File parent = getFileForDocId(documentId);
        File file = new File(parent.getPath(), displayName);
        try {
            file.createNewFile();
            file.setWritable(true);
            file.setReadable(true);
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to create document with name " +
                    displayName +" and documentId " + documentId);
        }
        return getDocIdForFile(file);
    }


    /**
     * Translate your custom URI scheme into a File object.
     *
     * @param docId the document ID representing the desired file
     * @return a File represented by the given document ID
     * @throws java.io.FileNotFoundException
     */
    private File getFileForDocId(String docId) throws FileNotFoundException {
        File target = mBaseDir;
        if (docId.equals(ROOT)) {
            return target;
        }
        final int splitIndex = docId.indexOf(':', 1);
        if (splitIndex < 0) {
            throw new FileNotFoundException("Missing root for " + docId);
        } else {
            final String path = docId.substring(splitIndex + 1);
            target = new File(target, path);
            if (!target.exists()) {
                throw new FileNotFoundException("Missing file for " + docId + " at " + target);
            }
            return target;
        }
    }


    /**
     * Get the document ID given a File.  The document id must be consistent across time.  Other
     * applications may save the ID and use it to reference documents later.
     * <p/>
     * This implementation is specific to this demo.  It assumes only one root and is built
     * directly from the file structure.  However, it is possible for a document to be a child of
     * multiple directories (for example "android" and "images"), in which case the file must have
     * the same consistent, unique document ID in both cases.
     *
     * @param file the File whose document ID you want
     * @return the corresponding document ID
     */
    private String getDocIdForFile(File file) {
        String path = file.getAbsolutePath();

        // Start at first char of path under root
        final String rootPath = mBaseDir.getPath();
        if (rootPath.equals(path)) {
            path = "";
        } else if (rootPath.endsWith("/")) {
            path = path.substring(rootPath.length());
        } else {
            path = path.substring(rootPath.length() + 1);
        }

        return "root" + ':' + path;
    }
}
