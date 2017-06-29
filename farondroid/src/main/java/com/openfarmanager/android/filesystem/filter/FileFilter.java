package com.openfarmanager.android.filesystem.filter;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.SelectParams;

import java.util.List;

import io.reactivex.Single;

/**
 * @author Vlad Namashko
 */
public class FileFilter {

    private boolean mIsIncludeFiles;
    private boolean mIsIncludeFolders;

    private boolean mIsInverseSelection;

    protected Predicate<FileProxy> mPredicate;

    public FileFilter(SelectParams params) {
        mIsIncludeFiles = params.isIncludeFiles();
        mIsIncludeFolders = params.isIncludeFolders();
        mIsInverseSelection = params.isInverseSelection();
    }

    public FileFilter setFilterPredicate(Predicate<FileProxy> predicate) {
        mPredicate = predicate;
        return this;
    }

    public FileFilter setInverseSelection(boolean inverseSelection) {
        mIsInverseSelection = inverseSelection;
        return this;
    }

    private boolean accept(FileProxy file) {
        return (mIsInverseSelection != ((mPredicate == null || mPredicate.test(file))) && (file.isDirectory() ? mIsIncludeFolders : mIsIncludeFiles));
    }

    public Single<List<FileProxy>> filter(List<FileProxy> files) {
        return Single.create(e -> e.onSuccess(Stream.of(files).filter(this::accept).toList()));
    }
}
