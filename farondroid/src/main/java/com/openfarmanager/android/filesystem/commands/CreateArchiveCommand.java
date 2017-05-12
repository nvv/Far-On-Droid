package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.filesystem.actions.CreateArchiveTask;
import com.openfarmanager.android.fragments.MainPanel;

/**
 * @author Vlad Namashko
 */
public class CreateArchiveCommand implements AbstractCommand {

    private MainPanel mPanel;
    private String mArchiveName;
    private ArchiveUtils.ArchiveType mArchiveType;
    private boolean mCompressionEnabled;
    private ArchiveUtils.CompressionEnum mCompression;

    public CreateArchiveCommand(MainPanel panel, String archiveName, ArchiveUtils.ArchiveType archiveType,
                                boolean compressionEnabled, ArchiveUtils.CompressionEnum compression) {
        mPanel = panel;
        mArchiveName = archiveName;
        mArchiveType = archiveType;
        mCompressionEnabled = compressionEnabled;
        mCompression = compression;
    }

    @Override
    public void execute(Object... args) {
        try {
            new CreateArchiveTask(mPanel.getContext(), mPanel.getPanelLocation(), mPanel.getSelectedFiles(),
                    mArchiveName, mArchiveType, mCompressionEnabled, mCompression).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
