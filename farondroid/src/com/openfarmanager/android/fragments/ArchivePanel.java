package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.junrar.Archive;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.ArchiveEntryAdapter;
import com.openfarmanager.android.core.CancelableCommand;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.filesystem.ArchiveFile;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.model.exeptions.NoPasswordException;
import com.openfarmanager.android.view.ToastNotification;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static com.openfarmanager.android.controllers.FileSystemController.*;
import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;
import static com.openfarmanager.android.utils.Extensions.runAsynk;

public class ArchivePanel extends MainPanel {

    private ArchiveScanner.File mCurrentArchiveItem;
    protected boolean mIsArchiveCompressed;

    private OpenArchiveTask mOpenArchiveTask = new OpenArchiveTask();
    private boolean mEncryptedArchive;
    protected String mEncryptedArchivePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupHandler();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFileSystemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArchiveScanner.File item = (ArchiveScanner.File) adapterView.getItemAtPosition(i);

                if (item.isUpNavigator()) {
                    item = item.getParent();
                    if (item.isRoot()) { // exit from archive
                        exitFromArchive();
                        return;
                    } else {
                        item = item.getParent();
                        openArchiveDirectory(item);
                        return;
                    }
                } else if (!item.isDirectory()) {
                    onLongClick(adapterView, i);
                }

                if (item.isDirectory()) {
                    openArchiveDirectory(item);
                }
            }
        });

        // archive panel always starts from 'loading...' state
        setIsLoading(true);

        postInitialization();

        return view;
    }

    public void exitFromArchive() {
//        ToastNotification.makeText(App.sInstance.getApplicationContext(), getString(R.string.exit_from_archive), Toast.LENGTH_SHORT).show();
        mEncryptedArchivePassword = null;
        ArchiveScanner.sInstance.clearArchive();
        mHandler.sendEmptyMessage(EXIT_FROM_ARCHIVE);
    }

    protected boolean onLongClick(AdapterView<?> adapterView, int i) {
        ArchiveFile file = (ArchiveFile) adapterView.getItemAtPosition(i);
        if (!file.isUpNavigator()) {
            mCurrentArchiveItem = file;
            mHandler.sendMessage(mHandler.obtainMessage(EXTRACT_ARCHIVE));
        }
        return true;
    }

    public void extractArchive(final MainPanel inactivePanel) {

        if (inactivePanel == null || !inactivePanel.isFileSystemPanel()) {
            return;
        }

        String defaultPath = inactivePanel.getCurrentDir().getAbsolutePath();

        try {
            ConfirmActionDialog.newInstance(FileActionEnum.ARCHIVE_EXTRACT, mExtractArchiveCommand,
                    mIsArchiveCompressed, defaultPath, false).show(fragmentManager(), "mConfirmDialog");
        } catch (Exception e) {
        }
    }

    public void openCompressedArchive(final File item) {
        if (!mIsInitialized) {
            addToPendingList(new Runnable() {
                @Override
                public void run() {
                    openCompressedArchive(item);
                }
            });
            return;
        }

        try {
            mLastSelectedFile = item;
            openArchive(item, new CompressorStreamFactory().createCompressorInputStream(
                    new BufferedInputStream(new FileInputStream(item))));
            setCurrentPath(null);
            mIsArchiveCompressed = true;
        } catch (IOException e) {
            openArchiveFailed();
        } catch (CompressorException e) {
            openArchiveFailed();
        } catch (Exception e) {
            openArchiveFailed();
        }
    }

    public void openArchive(final File item) {
        if (!mIsInitialized) {
            addToPendingList(new Runnable() {
                @Override
                public void run() {
                    openArchive(item);
                }
            });
            return;
        }

        try {
            mLastSelectedFile = item;
            openArchive(item, new FileInputStream(item));
        } catch (IOException e) {
            openArchiveFailed();
        } catch (Exception e) {
            openArchiveFailed();
        }
    }

    private void openArchive(final File archiveFile, final InputStream stream) {
        setIsLoading(true);
        mOpenArchiveTask.init(archiveFile, stream, mEncryptedArchivePassword);
        runAsynk(mOpenArchiveTask);
    }

    private void openArchiveDirectory(ArchiveScanner.File selectedDirectory) {
        mCurrentArchiveItem = selectedDirectory;
        setCurrentPath(selectedDirectory);
        ((ArchiveEntryAdapter) mFileSystemList.getAdapter()).setItems(selectedDirectory);
    }

    private void setCurrentPath(ArchiveScanner.File file) {
        String prefix = mLastSelectedFile.getName();
        if (file != null && !file.isRoot()) {
            prefix += " : " + file.getFullPath();
        }

        mCurrentPathView.setText(prefix);
    }

    public String getCurrentPath() {
        return "/" + mCurrentArchiveItem.getFullPath();
    }

    protected void onNavigationItemSelected(int pos, List<String> items) {
        openArchiveDirectory(mCurrentArchiveItem.findInTree(TextUtils.join("/", items.subList(0, pos + 1))));
    }

    public boolean isRootDirectory() {
        // definetely something going wrong so it's better to tell that this is root and exit from archive to avoid crash.
        return mCurrentArchiveItem == null || mCurrentArchiveItem.isRoot();
    }

    public void navigateParent() {
        if (mCurrentArchiveItem != null && !mCurrentArchiveItem.isRoot()) {
            openArchiveDirectory(mCurrentArchiveItem.getParent());
        } else {
            exitFromArchive();
        }
    }

    private void openArchiveFailed() {
        postIfAttached(mOpenArchiveFailed);
    }

    public ArchiveScanner.File getCurrentArchiveItem() {
        return mCurrentArchiveItem;
    }

    protected boolean isCopyFolderSupported() {
        return false;
    }

    public boolean isFileSystemPanel() {
        return false;
    }

    public boolean isSearchSupported() {
        return false;
    }

    public String getPanelType() {
        return getSafeString(R.string.archive);
    }

    private Runnable mOpenArchiveFailed = new Runnable() {
        @Override
        public void run() {
            ToastNotification.makeText(App.sInstance.getApplicationContext(), getSafeString(R.string.error_open_archive), Toast.LENGTH_LONG).show();
            exitFromArchive();
        }
    };

    private Runnable mSetupArchiveView = new Runnable() {
        @Override
        public void run() {
            if (!mIsInitialized) {
                addToPendingList(new Runnable() {
                    @Override
                    public void run() {
                        setupArchiveView();
                    }
                });
                return;
            }

            setupArchiveView();
        }

        private void setupArchiveView() {
            if (ArchiveScanner.sInstance.root().getSortedChildren() != null) {
                mFileSystemList.setAdapter(new ArchiveEntryAdapter(ArchiveScanner.sInstance.root()));
            }
            setIsLoading(false);
            setCurrentPath(null);
        }
    };

    private Runnable mRequestPassword = new Runnable() {
        @Override
        public void run() {
            try {
                RequestPasswordDialog.newInstance(mRequestPasswordCommand).show(fragmentManager(), "confirmDialog");
            } catch (Exception e) {
            }
        }
    };

    private class OpenArchiveTask implements Runnable {

        private File mArchiveFile;
        private InputStream mStream;
        private String mPassword;

        public void init(File archiveFile, InputStream stream, String password) {
            mArchiveFile = archiveFile;
            mStream = stream;
            mPassword = password;
        }

        @Override
        public void run() {
            mEncryptedArchive = false;
            ArchiveInputStream inputStream = null;

            // special cases
            Archive rarArchive = null;
            ZipFile zipFile = null;
            SevenZFile sevenZFile = null;

            try {
                ArchiveScanner.sInstance.clearArchive();

                if (ArchiveUtils.isRarArchive(mArchiveFile)) {
                    rarArchive = new Archive(mArchiveFile);
                } else if (ArchiveUtils.is7zArchive(mArchiveFile)) {
                    sevenZFile = new SevenZFile(mArchiveFile, mPassword == null ? null : mPassword.getBytes());
                } else {
                    inputStream = ArchiveUtils.createInputStream(mStream);
                }

                if (inputStream instanceof ZipArchiveInputStream) {
                    zipFile = new ZipFile(mLastSelectedFile);
                    mEncryptedArchive = zipFile.isEncrypted();
                }

                LinkedList<ArchiveEntry> entries = new LinkedList<>();

                if (mEncryptedArchive) {

                    if (isNullOrEmpty(mPassword)) {
                        throw new NoPasswordException();
                    }

                    //noinspection ConstantConditions
                    zipFile.setPassword(mPassword);
                    //noinspection unchecked
                    List<FileHeader> fileHeaders = zipFile.getFileHeaders();

                    for (final FileHeader header : fileHeaders) {

                        ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(header.getFileName());
                        zipArchiveEntry.setSize(header.getUncompressedSize());
                        entries.add(zipArchiveEntry);
                    }
                } else if (rarArchive != null) { // special case: completely different api

                    if (rarArchive.isEncrypted()) {
                        if (isNullOrEmpty(mPassword)) {
                            throw new NoPasswordException();
                        }
                    }

                    List<com.github.junrar.rarfile.FileHeader> fileHeaders = rarArchive.getFileHeaders();
                    for (final com.github.junrar.rarfile.FileHeader header : fileHeaders) {
                        if (header.isDirectory()) {
                            continue;
                        }

                        ZipArchiveEntry entry = new ZipArchiveEntry(header.getFileNameString());
                        entry.setSize(header.getFullUnpackSize());
                        entries.add(entry);
                    }

                } else if (sevenZFile != null) { // special case: completely different api

                    SevenZArchiveEntry entry = sevenZFile.getNextEntry();
                    while (entry != null) {
                        if (!entry.isDirectory()) {
                            entries.add(entry);
                        }

                        byte[] content = new byte[(int) entry.getSize()];
                        sevenZFile.read(content, 0, content.length);
                        entry = sevenZFile.getNextEntry();
                    }
                    sevenZFile.close();

                } else {
                    // read all entries from archive first.
                    // it's essential since for 'DEFLATED' zip items some data is available only after entry actually been read.
                    ArchiveEntry entry = inputStream.getNextEntry();

                    do {
                        entries.add(entry);
                    } while ((entry = inputStream.getNextEntry()) != null);
                }

                for (ArchiveEntry theEntry : entries) {
                    ArchiveScanner.sInstance.root().processFile(theEntry.getName(), theEntry.getSize());
                }
                entries.clear();

            } catch (NoPasswordException | UnsupportedZipFeatureException e) {
                postIfAttached(mRequestPassword);
                return;
            } catch (IOException e) {
                if (e.getMessage().equals("Cannot read encrypted files without a password")) {
                    postIfAttached(mRequestPassword);
                } else {
                    e.printStackTrace();
                    openArchiveFailed();
                }
            } catch (Exception e) {
                e.printStackTrace();
                openArchiveFailed();
            } finally {
                try {
                    //noinspection ConstantConditions
                    inputStream.close();
                    mStream.close();
                } catch (Exception ignore) {}
            }

            mCurrentArchiveItem = ArchiveScanner.sInstance.root();
            postIfAttached(mSetupArchiveView);
        }
    }

    protected CancelableCommand mRequestPasswordCommand = new CancelableCommand() {

        @Override
        public void execute(final Object... args) {
            mEncryptedArchivePassword = (String) args[0];
            openArchive(mLastSelectedFile);
        }

        @Override
        public void cancel() {
            exitFromArchive();
        }
    };

    protected String getArchivePassword() {
        return mEncryptedArchivePassword;
    }
}
