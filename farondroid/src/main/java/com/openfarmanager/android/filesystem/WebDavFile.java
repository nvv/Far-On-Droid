package com.openfarmanager.android.filesystem;

import android.text.Html;

import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.FileUtilsExt;

import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertySet;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;

/**
 * @author Vlad Namashko
 */
public class WebDavFile implements FileProxy {

    private static SimpleDateFormat sFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");

    private String mName;
    private boolean mIsDirectory;
    private long mSize;
    private long mLastModified;
    private String mFullPath;
    private String mParentPath;

    public WebDavFile(MultiStatusResponse response, String currentPath) throws SmbException {
        DavPropertySet set = response.getProperties(200);

        String href = response.getHref();
        try {
            href = FileUtilsExt.removeLastSeparator(URLDecoder.decode(href, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            href = response.getHref();
        }

        mName = FileUtilsExt.removeFirstSeparator(href.substring(href.indexOf(currentPath) + currentPath.length()));

        mIsDirectory = set.get("getcontenttype").getValue().equals("httpd/unix-directory");
        if (!mIsDirectory) {
            mSize = Integer.parseInt((String) set.get("getcontentlength").getValue());

            try {
                mLastModified = sFormat.parse((String) set.get("getlastmodified").getValue()).getTime();
            } catch (ParseException e) {
            }
        }

        mFullPath = href;
        mParentPath = currentPath;
    }

    public WebDavFile(String path) {
        path = FileUtilsExt.removeLastSeparator(path);
        mName = FileUtilsExt.getFileName(path);
        mIsDirectory = true;
        mSize = 0;
        mLastModified = System.currentTimeMillis();
        mFullPath = path;
        mParentPath = FileUtilsExt.getParentPath(path);
    }

    @Override
    public String getId() {
        return getFullPath();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isDirectory() {
        return mIsDirectory;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public long lastModifiedDate() {
        return mLastModified;
    }

    @Override
    public List getChildren() {
        return new ArrayList();
    }

    @Override
    public String getFullPath() {
        return mFullPath;
    }

    @Override
    public String getFullPathRaw() {
        return mFullPath;
    }

    @Override
    public String getParentPath() {
        return mParentPath;
    }

    @Override
    public boolean isUpNavigator() {
        return false;
    }

    @Override
    public boolean isRoot() {
        return getName().equals("..") && Extensions.isNullOrEmpty(mParentPath);
    }

    @Override
    public boolean isVirtualDirectory() {
        return false;
    }

    @Override
    public boolean isBookmark() {
        return false;
    }

    @Override
    public Bookmark getBookmark() {
        return null;
    }

    @Override
    public String getMimeType() {
        return null;
    }
}
