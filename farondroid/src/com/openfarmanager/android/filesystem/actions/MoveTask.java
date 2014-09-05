package com.openfarmanager.android.filesystem.actions;

import android.support.v4.app.FragmentManager;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.utils.FileUtilsExt;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * author: vnamashko
 */
public class MoveTask extends FileActionTask {

    protected File destinationFolder;
    protected String destinationFileName;
    
    public MoveTask(FragmentManager fragmentManager, OnActionListener listener, List<File> items, File destination, 
                    String destinationFileName) {
        super(fragmentManager, listener, items);
        this.destinationFolder = destination;
        this.destinationFileName = destinationFileName;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {

        if (mItems.size() < 1) {
            return OK;
        }

        File srcFile = mItems.get(0); // first file from src folder
        boolean theSameFolders = FileUtilsExt.isTheSameFolders(mItems, destinationFolder);
        if (mItems.size() == 1 && theSameFolders) {
            // move inside the same folder, perform rename

            if (destinationFileName == null || destinationFileName.trim().equals("")) {
                return ERROR_WRONG_DESTINATION_FILE_NAME;
            }

            return !srcFile.renameTo(new File(srcFile.getParent() + File.separator + destinationFileName)) ?
                    ERROR_RENAME_FILE : OK;
        } else if (theSameFolders) {
            return OK;
        }

        // TODO: temporary
        List<File> items = new ArrayList<File>(mItems);
        for (File file : items) {
            try {
                doneSize += FileUtils.sizeOf(file);
                if (!destinationFolder.canWrite() || !file.getParentFile().canWrite()) {
                    if (!RootTask.move(file, destinationFolder)) {
                        throw new IOException("Cannot move file to " + destinationFolder.getAbsolutePath());
                    }
                } else {
                    FileUtils.moveToDirectory(file, destinationFolder, false);
                }
                updateProgress();
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (FileExistsException e) {
                return ERROR_FILE_EXISTS;
            } catch (IllegalArgumentException e) {
                return ERROR_MOVE_FILE;
            } catch (IOException e) {
                return ERROR_MOVE_FILE;
            }
        }

        return OK;
    }

}

