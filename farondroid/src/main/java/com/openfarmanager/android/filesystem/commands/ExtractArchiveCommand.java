package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.filesystem.actions.ExtractArchiveTask;
import com.openfarmanager.android.fragments.MainPanel;

import java.io.File;

/**
 * @author Vlad Namashko
 */
public class ExtractArchiveCommand implements AbstractCommand {

    private MainPanel mMainPanel;
    private File mArchiveFile;
    private File mDestination;
    private boolean mCompressed;
    private String mEncryptedArchivePassword;
    private ArchiveScanner.File mExtractTree;

    public ExtractArchiveCommand(MainPanel mainPanel, File archiveFile, File destination, boolean compressed,
                                 String encryptedArchivePassword, ArchiveScanner.File extractTree) {
        mMainPanel = mainPanel;
        mArchiveFile = archiveFile;
        mDestination = destination;
        mCompressed = compressed;
        mEncryptedArchivePassword = encryptedArchivePassword;
        mExtractTree = extractTree;
    }

    @Override
    public void execute(Object... args) {
        new ExtractArchiveTask(mMainPanel.getContext(), mMainPanel.getPanelLocation(), mArchiveFile,
                mDestination, mCompressed, mEncryptedArchivePassword, mExtractTree).execute();
    }


}
