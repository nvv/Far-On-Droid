package com.openfarmanager.android.filesystem.actions;

import android.support.v4.app.FragmentManager;

import com.github.junrar.Archive;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.model.TaskStatusEnum;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class ExtractArchiveTask extends FileActionTask {

    private File mArchiveFile;
    private File mDestinationFolder;
    private ArchiveScanner.File mExtractTree;

    private boolean mIsCompressed;
    private String mEncryptedArchivePassword;

    public ExtractArchiveTask(FragmentManager fragmentManager, OnActionListener listener,
                              File archiveFile, File destination, boolean compressed, String encryptedArchivePassword,
                              ArchiveScanner.File extractTree) {
        super(fragmentManager, listener, new ArrayList<File>());
        mArchiveFile = archiveFile;
        mDestinationFolder = destination;
        mExtractTree = extractTree;
        mIsCompressed = compressed;
        mEncryptedArchivePassword = encryptedArchivePassword;
    }

    public ExtractArchiveTask(FragmentManager fragmentManager, OnActionListener listener,
                              File archiveFile, File destination, boolean compressed,
                              ArchiveScanner.File extractTree) {
        this(fragmentManager, listener, archiveFile, destination, compressed, null, extractTree);
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        if (!mDestinationFolder.exists() && !mDestinationFolder.mkdirs()) {
            return TaskStatusEnum.ERROR_CAN_T_CREATE_DIRECTORY;
        }

        if (mExtractTree == null) {
            try {
                ArchiveScanner.sInstance.clearArchive();

                if (mEncryptedArchivePassword != null) { // another api for special case :(
                    ZipFile zipFile = new ZipFile(mArchiveFile);
                    zipFile.setPassword(mEncryptedArchivePassword);

                    // Get the list of file headers from the zip file
                    List fileHeaderList = zipFile.getFileHeaders();

                    // Loop through the file headers
                    for (Object aFileHeaderList : fileHeaderList) {
                        FileHeader fileHeader = (FileHeader) aFileHeaderList;
                        // Extract the file to the specified destination
                        ArchiveScanner.sInstance.root().processFile(fileHeader.getFileName(), fileHeader.getUncompressedSize());
                    }
                } else if (ArchiveUtils.isRarArchive(mArchiveFile)) { // one more special case :(
                    Archive rarArchive = new Archive(mArchiveFile);
                    List<com.github.junrar.rarfile.FileHeader> fileHeaders = rarArchive.getFileHeaders();
                    for (final com.github.junrar.rarfile.FileHeader header : fileHeaders) {
                        if (header.isDirectory()) {
                            continue;
                        }
                        ArchiveScanner.sInstance.root().processFile(header.getFileNameString(), header.getFullUnpackSize());
                    }
                } else {
                    ArchiveInputStream inputStream = ArchiveUtils.createInputStream(
                            mIsCompressed ?
                                    new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(new FileInputStream(mArchiveFile))) :
                                    new FileInputStream(mArchiveFile)

                    );
                    ArchiveEntry entry;
                    while ((entry = inputStream.getNextEntry()) != null) {
                        ArchiveScanner.sInstance.root().processFile(entry.getName(), entry.getSize());
                    }
                }
            } catch (UnsupportedZipFeatureException e) {
                return TaskStatusEnum.ERROR_EXTRACTING_ARCHIVE_FILES_ENCRYPTION_PASSWORD_REQUIRED;
            } catch (Exception e) {
                return TaskStatusEnum.ERROR_CREATING_ARCHIVE_FILES_TREE;
            }
            mExtractTree = ArchiveScanner.sInstance.root();
        }

        try {
            ArchiveUtils.extractArchive(mArchiveFile, mDestinationFolder, mExtractTree, mIsCompressed, mEncryptedArchivePassword, mListener);
        } catch (Exception e) {
            return TaskStatusEnum.ERROR_EXTRACTING_ARCHIVE_FILES;
        }

        return TaskStatusEnum.OK;
    }

    private ArchiveUtils.ExtractArchiveListener mListener = new ArchiveUtils.ExtractArchiveListener() {
        @Override
        public void beforeExtractStarted(int filesToExtract) {
            totalSize = filesToExtract;
        }

        @Override
        public void onFileExtracted(ArchiveScanner.File extractedFile) {
            doneSize++;
            updateProgress();
        }
    };
}
