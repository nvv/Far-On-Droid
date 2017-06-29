package com.openfarmanager.android.filesystem.filter;

import com.openfarmanager.android.model.SelectParams;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

/**
 * @author Vlad Namashko
 */
public class FileNameFilter extends FileFilter {

    public FileNameFilter(SelectParams params) {
        super(params);
        mPredicate = file -> FilenameUtils.wildcardMatch(file.getName(), params.getSelectionString(), params.isCaseSensitive() ? IOCase.SENSITIVE : IOCase.INSENSITIVE);
    }

}
