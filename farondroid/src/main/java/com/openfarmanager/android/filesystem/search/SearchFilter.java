package com.openfarmanager.android.filesystem.search;

import android.text.TextUtils;

import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemFile;
import com.openfarmanager.android.filesystem.filter.DateFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Vlad Namashko
 */
public class SearchFilter {

    protected SearchOptions mSearchOptions;

    public SearchFilter(SearchOptions searchOptions) {
        mSearchOptions = searchOptions;
    }

    public Observable<FileProxy> search(String currentDirectory) {
        return Observable.create(e -> {
            File directory = new File(currentDirectory);
            searchDirectory(e, directory);
            e.onComplete();
        });
    }

    public Observable<FileProxy> searchAsync(String currentDirectory) {
        return search(currentDirectory).subscribeOn(Schedulers.computation());
    }

    private void searchDirectory(ObservableEmitter<FileProxy> e, File directory) {
        File[] files = directory.listFiles(file -> file.isDirectory() ? mSearchOptions.includeFolders : mSearchOptions.includeFiles);
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            if (e.isDisposed()) {
                break;
            }

            if (FilenameUtils.wildcardMatch(file.getName(), mSearchOptions.fileMask, isCaseSensitive())) {
                boolean searchKeyword = !TextUtils.isEmpty(mSearchOptions.keyword);

                FileSystemFile proxy = new FileSystemFile(file);
                if (searchKeyword && file.isFile()) {
                    if (searchFile(file) && DateFilter.fileFilter(proxy, mSearchOptions.dateAfter, mSearchOptions.dateBefore) && filterBySize(proxy)) {
                        e.onNext(proxy);
                    }
                } else if ((file.isDirectory() && !searchKeyword) || (file.isFile() && DateFilter.fileFilter(proxy, mSearchOptions.dateAfter, mSearchOptions.dateBefore) && filterBySize(proxy))) {
                    e.onNext(new FileSystemFile(file));
                }
            }

            if (file.isDirectory() && file.canRead()) {
                searchDirectory(e, file);
            }
        }
    }

    protected boolean filterBySize(FileProxy file) {
        return file.isDirectory() || (mSearchOptions.biggerThenSizeBytes == -1 || file.getSize() > mSearchOptions.biggerThenSizeBytes) &&
                (mSearchOptions.smallerThenSizeBytes == -1 || file.getSize() < mSearchOptions.smallerThenSizeBytes);
    }

    private boolean searchFile(File file) {
        if (!file.canRead()) {
            return false;
        }
        BufferedReader is = null;
        try {
            String keyword = mSearchOptions.keyword;
            Pattern pattern = Pattern.compile(mSearchOptions.wholeWords ? String.format("\\b%s\\b", Pattern.quote(keyword)) : Pattern.quote(keyword),
                    isCaseSensitive() == IOCase.INSENSITIVE ? Pattern.CASE_INSENSITIVE : 0);
            is = new BufferedReader(new FileReader(file));
            String line;
            while ((line = is.readLine()) != null) {
                if (pattern.matcher(line).find()) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private IOCase isCaseSensitive() {
        return mSearchOptions.caseSensitive ? IOCase.SENSITIVE : IOCase.INSENSITIVE;
    }

}
