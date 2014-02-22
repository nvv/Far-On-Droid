package com.openfarmanager.android.filesystem.actions;

import com.openfarmanager.android.filesystem.FileSystemFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.openfarmanager.android.utils.Extensions.runAsynk;
import static com.openfarmanager.android.utils.FileUtilsExt.findFileByName;

public class DiffDirectoriesTask {

    protected Set<File> mActivePanelFiles;
    protected Set<File> mInactivePanelFiles;

    private DiffDirectoriesTask.OnActionListener mListener;

    public DiffDirectoriesTask(DiffDirectoriesTask.OnActionListener listener,
                               Set<File> activePanelFiles, Set<File> inactivePanelFiles) {
        mActivePanelFiles = activePanelFiles;
        mInactivePanelFiles = inactivePanelFiles;
        mListener = listener;
    }

    public void execute() {
        runAsynk(new Runnable() {
            @Override
            public void run() {
                LinkedHashSet<File> activePanelDiffFiles = new LinkedHashSet<File>();
                LinkedHashSet<File> inactivePanelDiffFiles = new LinkedHashSet<File>();

                for (File file : mActivePanelFiles) {
                    if (file.isDirectory()) continue;
                    File theFile = findFileByName(mInactivePanelFiles, file);
                    if (theFile == null || !compareFiles(theFile, file)) {
                        activePanelDiffFiles.add(file);
                    }
                }

                for (File file : mInactivePanelFiles) {
                    if (file.isDirectory()) continue;
                    File theFile = findFileByName(mActivePanelFiles, file);
                    if (theFile == null || !compareFiles(theFile, file)) {
                        inactivePanelDiffFiles.add(file);
                    }
                }

                mListener.onActionFinish(activePanelDiffFiles, inactivePanelDiffFiles);
            }
        });
    }

    private boolean compareFiles(File file1, File file2) {
        try {
            return FileUtils.contentEquals(file1, file2);
        } catch (IOException e) {
            return false;
        }
    }

    public static interface OnActionListener {
        void onActionFinish(LinkedHashSet<File> activePanelDiffFiles, LinkedHashSet<File> inactivePanelDiffFiles);
    }
}
