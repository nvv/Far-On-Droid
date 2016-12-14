package com.openfarmanager.android.core.network.datasource;

import com.openfarmanager.android.filesystem.FileProxy;

/**
 * @author Vlad Namashko
 */
public abstract class RawPathDataSource extends DataSource {

    public FileProxy restoreFileParent(FileProxy file) {
        return null;
    }

}
