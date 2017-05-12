package com.openfarmanager.android.filesystem.actions.network;

import android.net.Uri;

import com.dropbox.client2.exception.DropboxException;
import com.jcraft.jsch.SftpException;
import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;
import com.mediafire.sdk.api.FileApi;
import com.mediafire.sdk.api.responses.FileGetLinksResponse;
import com.mediafire.sdk.api.responses.data_models.Link;
import com.microsoft.live.LiveOperationException;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.core.network.ftp.FtpAPI;
import com.openfarmanager.android.core.network.ftp.SftpAPI;
import com.openfarmanager.android.core.network.googledrive.GoogleDriveApi;
import com.openfarmanager.android.core.network.mediafire.MediaFireApi;
import com.openfarmanager.android.core.network.skydrive.SkyDriveAPI;
import com.openfarmanager.android.core.network.smb.SmbAPI;
import com.openfarmanager.android.core.network.webdav.WebDavApi;
import com.openfarmanager.android.core.network.yandexdisk.YandexDiskApi;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;
import com.openfarmanager.android.utils.SystemUtils;
import com.yandex.disk.client.ProgressListener;
import com.yandex.disk.client.exceptions.WebdavException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import jcifs.smb.SmbFileInputStream;

import static com.openfarmanager.android.model.TaskStatusEnum.*;
import static com.openfarmanager.android.utils.StorageUtils.checkForPermissionAndGetBaseUri;
import static com.openfarmanager.android.utils.StorageUtils.checkUseStorageApi;
import static com.openfarmanager.android.utils.StorageUtils.getStorageOutputFileStream;

/**
 * @author Vlad Namashko
 */
public class CopyFromNetworkTask extends NetworkActionTask {

    private final static byte[] BUFFER = new byte[512 * 1024];

    protected String mDestination;
    protected List<FileProxy> mItems;

    public CopyFromNetworkTask(BaseFileSystemPanel panel, List<FileProxy> items, String destination) {
        super(panel, new ArrayList<File>());
        mDestination = destination;
        mItems = items;
        mNoProgress = true;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        // TODO: hack
        mDoneSize = 1;

        try {
            mSdCardPath = SystemUtils.getExternalStorage(mDestination);
            if (checkUseStorageApi(mSdCardPath)) {
                mUseStorageApi = true;
                mBaseUri = checkForPermissionAndGetBaseUri();
            }
        } catch (SdcardPermissionException e) {
            return ERROR_STORAGE_PERMISSION_REQUIRED;
        }

        return doCopy();
    }

    protected TaskStatusEnum doCopy() {

        // avoid concurrent modification
        ArrayList<FileProxy> items = new ArrayList<FileProxy>(mItems);
        for (FileProxy file : items) {
            if (isCancelled()) {
                return CANCELED;
            }
            try {
                switch (mNetworkType) {
                    case SkyDrive:
                        copyFromSkyDrive(file, mDestination);
                        break;
                    case Dropbox:
                        copyFromDropbox(file, mDestination);
                        break;
                    case GoogleDrive:
                        copyFromGoogleDrive(file, mDestination);
                        break;
                    case FTP:
                        copyFromFTP(file, mDestination);
                        break;
                    case SFTP:
                        copyFromSFTP(file, mDestination);
                        break;
                    case SMB:
                        copyFromSmb(file, mDestination);
                        break;
                    case YandexDisk:
                        copyFromYandexDisk(file, mDestination);
                        break;
                    case MediaFire:
                        copyFromMediafire(file, mDestination);
                        break;
                    case WebDav:
                        copyFromWebDav(file, mDestination);
                        break;
                }
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (InterruptedIOException e) {
                return CANCELED;
            } catch (IOException e) {
                return ERROR_COPY;
            } catch (IllegalArgumentException e) {
                return ERROR_COPY;
            } catch (DropboxException e) {
                return createNetworkError(NetworkException.handleNetworkException(e));
            } catch (LiveOperationException e) {
                return ERROR_COPY;
            } catch (Exception e) {
                return ERROR_COPY;
            }
        }

        return TaskStatusEnum.OK;
    }

    @Override
    protected Object getExtra() {
        return mDestination;
    }

    private void copyFromGoogleDrive(FileProxy source, String destination) throws IOException {
        GoogleDriveApi api = App.sInstance.getGoogleDriveApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            createDirectoryIfNotExists(destination);

            List<FileProxy> list = api.getDirectoryFiles(source.getFullPath());

            if (list.size() == 0) {
                createDirectoryIfNotExists(fullSourceFilePath);
            } else {
                for (FileProxy file : list) {
                    copyFromGoogleDrive(file, fullSourceFilePath);
                }
            }
        } else {
            createDirectoryIfNotExists(destination);

            File destinationFile = createFileIfNotExists(fullSourceFilePath);

            setCurrentFile(source);
            api.download(source, getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile));
        }
    }

    private void copyFromSkyDrive(FileProxy source, String destination) throws LiveOperationException, IOException, JSONException {
        SkyDriveAPI api = App.sInstance.getSkyDriveApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            createDirectoryIfNotExists(destination);

            List<FileProxy> list = api.getDirectoryFiles(source.getFullPath());

            if (list.size() == 0) {
                createDirectoryIfNotExists(fullSourceFilePath);
            } else {
                for (FileProxy file : list) {
                    copyFromSkyDrive(file, fullSourceFilePath);
                }
            }
        } else {
            createDirectoryIfNotExists(destination);

            File destinationFile = createFileIfNotExists(fullSourceFilePath);

            setCurrentFile(source);
            api.download(source, getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile));
        }

    }

    private void copyFromDropbox(FileProxy source, String destination) throws DropboxException, IOException {
        DropboxAPI api = App.sInstance.getDropboxApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            try {
                createDirectoryIfNotExists(destination);
                com.dropbox.client2.DropboxAPI.Entry currentNode = api.metadata(source.getFullPath(), -1, null, true, null);

                if (currentNode.contents.size() == 0) {
                    createDirectoryIfNotExists(fullSourceFilePath);
                } else {
                    for (com.dropbox.client2.DropboxAPI.Entry entry : currentNode.contents) {
                        copyFromDropbox(new DropboxFile(entry), fullSourceFilePath);
                    }
                }
            } catch (Exception e) {
                throw NetworkException.handleNetworkException(e);
            }
        } else {
            createDirectoryIfNotExists(destination);

            File destinationFile = createFileIfNotExists(fullSourceFilePath);

            setCurrentFile(source);
            api.getFile(source.getFullPath(), null, getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile), null);
        }
    }

    private void copyFromFTP(FileProxy source, String destination) throws IOException {
        FtpAPI api = App.sInstance.getFtpApi();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            try {
                createDirectoryIfNotExists(destination);
                List<FileProxy> files = api.getDirectoryFiles(source.getFullPath());

                if (files.size() == 0) {
                    createDirectoryIfNotExists(fullSourceFilePath);
                } else {
                    for (FileProxy file : files) {
                        copyFromFTP(file, fullSourceFilePath);
                    }
                }
            } catch (Exception e) {
                throw NetworkException.handleNetworkException(e);
            }
        } else {
            createDirectoryIfNotExists(destination);

            File destinationFile = createFileIfNotExists(fullSourceFilePath);

            setCurrentFile(source);
            api.client().retrieveFile(source.getFullPath(), getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile));
        }
    }

    private void copyFromSFTP(FileProxy source, String destination) throws IOException, SftpException {
        SftpAPI api = App.sInstance.getSftpApi();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            try {
                createDirectoryIfNotExists(destination);
                List<FileProxy> files = api.getDirectoryFiles(source.getFullPath());

                if (files.size() == 0) {
                    createDirectoryIfNotExists(fullSourceFilePath);
                } else {
                    for (FileProxy file : files) {
                        copyFromSFTP(file, fullSourceFilePath);
                    }
                }
            } catch (Exception e) {
                throw NetworkException.handleNetworkException(e);
            }
        } else {
            createDirectoryIfNotExists(destination);
            File destinationFile = createFileIfNotExists(fullSourceFilePath);
            setCurrentFile(source);
            api.writeFileToStream(source.getFullPath(), getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile));
        }
    }

    private void copyFromSmb(FileProxy source, String destination) throws IOException {
        SmbAPI api = App.sInstance.getSmbAPI();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            try {
                createDirectoryIfNotExists(destination);
                List<FileProxy> files = api.getDirectoryFiles(source.getFullPath());

                if (files.size() == 0) {
                    createDirectoryIfNotExists(fullSourceFilePath);
                } else {
                    for (FileProxy file : files) {
                        copyFromSmb(file, fullSourceFilePath);
                    }
                }
            } catch (Exception e) {
                throw NetworkException.handleNetworkException(e);
            }
        } else {
            createDirectoryIfNotExists(destination);

            File destinationFile = createFileIfNotExists(fullSourceFilePath);

            setCurrentFile(source);
            SmbFileInputStream in = new SmbFileInputStream(api.createSmbFile(source.getFullPath()));
            OutputStream out = getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile);

            int len;
            while ((len = in.read(BUFFER)) > 0) {
                out.write(BUFFER, 0, len);
            }

            out.close();
            in.close();
        }
    }

    private void copyFromYandexDisk(FileProxy source, String destination) throws IOException, WebdavException {
        YandexDiskApi api = App.sInstance.getYandexDiskApi();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            try {
                createDirectoryIfNotExists(destination);
                List<FileProxy> files = api.getDirectoryFiles(source.getFullPath());

                if (files.size() == 0) {
                    createDirectoryIfNotExists(fullSourceFilePath);
                } else {
                    for (FileProxy file : files) {
                        copyFromYandexDisk(file, fullSourceFilePath);
                    }
                }
            } catch (Exception e) {
                throw NetworkException.handleNetworkException(e);
            }
        } else {
            createDirectoryIfNotExists(destination);

            File destinationFile = createFileIfNotExists(fullSourceFilePath);

            setCurrentFile(source);
            api.client().downloadFile(source.getFullPath(), getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile), new ProgressListener() {
                @Override
                public void updateProgress(long loaded, long total) {
                }

                @Override
                public boolean hasCancelled() {
                    return false;
                }
            });
        }
    }

    private void copyFromMediafire(FileProxy source, String destination) throws IOException, MFApiException, MFSessionNotStartedException, MFException {
        MediaFireApi api = App.sInstance.getMediaFireApi();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            try {
                createDirectoryIfNotExists(destination);
                List<FileProxy> files = api.openDirectory(source.getFullPath());

                if (files.size() == 0) {
                    createDirectoryIfNotExists(fullSourceFilePath);
                } else {
                    for (FileProxy file : files) {
                        copyFromMediafire(file, fullSourceFilePath);
                    }
                }
            } catch (Exception e) {
                throw NetworkException.handleNetworkException(e);
            }
        } else {
            createDirectoryIfNotExists(destination);
            File destinationFile = createFileIfNotExists(fullSourceFilePath);
            setCurrentFile(source);
            LinkedHashMap<String, Object> query = new LinkedHashMap<>();
            query.put("quick_key", source.getId());
            query.put("link_type", "direct_download");
            FileGetLinksResponse response = FileApi.getLinks(api.getMediaFire(), query, "1.4", FileGetLinksResponse.class);

            Link link = response.getLinks()[new Random().nextInt(response.getLinks().length)];

            HttpGet httpGet = new HttpGet(link.getDirectDownloadLink());
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() > 200) throw new RuntimeException();
            InputStream in = httpResponse.getEntity().getContent();
            OutputStream out = getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile);
            int len;
            while ((len = in.read(BUFFER)) > 0) {
                out.write(BUFFER, 0, len);
            }

            updateProgress();
            out.close();
            in.close();
        }
    }

    private void copyFromWebDav(FileProxy source, String destination) throws Exception {
        WebDavApi api = App.sInstance.getWebDavApi();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        String fullSourceFilePath = destination + "/" + source.getName();
        if (source.isDirectory()) {
            try {
                createDirectoryIfNotExists(destination);
                List<FileProxy> files = api.getDirectoryFiles(source.getFullPath());

                if (files.size() == 0) {
                    createDirectoryIfNotExists(fullSourceFilePath);
                } else {
                    for (FileProxy file : files) {
                        copyFromWebDav(file, fullSourceFilePath);
                    }
                }
            } catch (Exception e) {
                throw NetworkException.handleNetworkException(e);
            }
        } else {
            createDirectoryIfNotExists(destination);

            File destinationFile = createFileIfNotExists(fullSourceFilePath);

            setCurrentFile(source);
            InputStream in = api.getFromWebDav(source.getFullPath());
            OutputStream out = getOutputStream(mSdCardPath, mUseStorageApi, mBaseUri, destinationFile);

            int len;
            while ((len = in.read(BUFFER)) > 0) {
                out.write(BUFFER, 0, len);
            }

            out.close();
            in.close();
        }
    }

    private File createFileIfNotExists(String fullSourceFilePath) throws IOException {
        File destinationFile = new File(fullSourceFilePath);
        if (!mUseStorageApi && !destinationFile.exists() && !destinationFile.createNewFile()) {
            throw new IOException();
        }
        return destinationFile;
    }

    private void setCurrentFile(FileProxy source) {
        mCurrentFile = source.getName();
        updateProgress();
    }

    private static OutputStream getOutputStream(String sdCardPath, boolean useStorageApi, Uri baseUri, File outputFile) throws FileNotFoundException {
        return useStorageApi ? getStorageOutputFileStream(outputFile, baseUri, sdCardPath) :
                new FileOutputStream(outputFile);
    }
}
