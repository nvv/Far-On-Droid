package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;
import com.mediafire.sdk.uploader.MediaFireUpload;
import com.mediafire.sdk.uploader.MediaFireUploadHandler;
import com.microsoft.live.OverwriteOption;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.core.network.ftp.FtpAPI;
import com.openfarmanager.android.core.network.ftp.SftpAPI;
import com.openfarmanager.android.core.network.googledrive.GoogleDriveApi;
import com.openfarmanager.android.core.network.mediafire.MediaFireApi;
import com.openfarmanager.android.core.network.skydrive.SkyDriveAPI;
import com.openfarmanager.android.core.network.smb.SmbAPI;
import com.openfarmanager.android.core.network.yandexdisk.YandexDiskApi;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.googledrive.api.GoogleDriveWebApi;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import static com.openfarmanager.android.model.TaskStatusEnum.CANCELED;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_COPY;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_FILE_NOT_EXISTS;
import static com.openfarmanager.android.model.TaskStatusEnum.createNetworkError;

/**
 * @author Vlad Namashko
 */
public class CopyToNetworkTask extends NetworkActionTask {

    private final static byte[] BUFFER = new byte[512 * 1024];

    protected String mDestination;

    public CopyToNetworkTask(NetworkEnum networkType, FragmentManager fragmentManager, OnActionListener listener, List<File> items, String destination) {
        mItems = items;
        mFragmentManager = fragmentManager;
        mListener = listener;

        mNetworkType = networkType;
        mDestination = destination;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        return doCopy();
    }

    protected TaskStatusEnum doCopy() {
        for (File file : mItems) {
            if (isCancelled()) {
                return CANCELED;
            }
            try {
                switch (mNetworkType) {
                    case SkyDrive:
                        copyToSkyDrive(file, mDestination);
                        break;
                    case GoogleDrive:
                        copyToGoogleDrive(file, mDestination);
                        break;
                    case Dropbox: default:
                        copyToDropbox(file, mDestination);
                        break;
                    case FTP:
                        copyToFtp(file, mDestination);
                        break;
                    case SFTP:
                        copyToSftp(file, mDestination);
                        break;
                    case SMB:
                        copyToSmb(file, mDestination);
                        break;
                    case YandexDisk:
                        copyToYandexDisk(file, mDestination);
                        break;
                    case MediaFire:
                        copyToMediaFire(file, mDestination);
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
            } catch (FTPDataTransferException e) {
                return ERROR_COPY;
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR_COPY;
            }
        }
        return TaskStatusEnum.OK;
    }

    private void copyToDropbox(File source, String destination) throws DropboxException, IOException {
        DropboxAPI api = App.sInstance.getDropboxApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }
        if (source.isDirectory()) {
            String[] files = source.list();
            for (String file : files) {
                copyToDropbox(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            mCurrentFile = source.getName();
            long tempSize = doneSize;
            api.putFileOverwrite(destination + "/" + source.getName(), new FileInputStream(source), source.length(), new ProgressListener() {
                long mPrevProgress = 0;

                @Override
                public void onProgress(long l, long l2) {
                    doneSize += (l - mPrevProgress);
                    mPrevProgress = l;
                    updateProgress();
                }
            });
            doneSize = tempSize + source.length();
        }
    }

    private void copyToGoogleDrive(File source, String destination) throws Exception {
        GoogleDriveApi api = App.sInstance.getGoogleDriveApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        if (api.findInPathAliases(destination) == null) {
            api.createDirectory(destination);
        }

        if (source.isDirectory()) {
            String[] files = source.list();
            for (String file : files) {
                copyToGoogleDrive(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            mCurrentFile = source.getName();
            updateProgress();

            api.upload(api.findPathId(destination), source.getName(), source, new GoogleDriveWebApi.UploadListener() {
                @Override
                public void onProgress(int uploaded, int transferedPortion, int total) {
                    doneSize += transferedPortion;
                    updateProgress();
                }
            });

//            doneSize += source.length();
        }
    }

    private void copyToSkyDrive(File source, String destination) throws Exception {
        SkyDriveAPI api = App.sInstance.getSkyDriveApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        if (api.findInPathAliases(destination) == null) {
            api.createDirectory(destination);
        }

        if (source.isDirectory()) {
            String[] files = source.list();
            for (String file : files) {
                copyToSkyDrive(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            mCurrentFile = source.getName();
            updateProgress();
            api.getConnectClient().upload(api.findPathId(destination), source.getName(), source, OverwriteOption.Overwrite);
            doneSize += source.length();
            updateProgress();
        }
    }

    private void copyToFtp(File source, String destination) throws Exception {
        FtpAPI api = App.sInstance.getFtpApi();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        if (source.isDirectory()) {
            api.createDirectory(destination + "/" + source.getName());

            String[] files = source.list();
            for (String file : files) {
                copyToFtp(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            mCurrentFile = source.getName();
            api.client().changeDirectory(destination);
            api.client().upload(source, new FTPDataTransferListener() {
                @Override
                public void started() {
                }

                @Override
                public void transferred(int i) {
                    doneSize += i;
                    updateProgress();
                }

                @Override
                public void completed() {
                }

                @Override
                public void aborted() {
                }

                @Override
                public void failed() {
                }
            });
        }
    }

    private void copyToSftp(File source, String destination) throws Exception {
        SftpAPI api = App.sInstance.getSftpApi();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        api.changeDirectory(destination);
        if (source.isDirectory()) {
            api.createDirectory(destination + "/" + source.getName());

            String[] files = source.list();
            for (String file : files) {
                copyToSftp(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            OutputStream out = api.getUploadStream(destination + "/" + source.getName());
            copyStreamRoutine(source, out);
        }
    }

    private void copyToSmb(File source, String destination) throws Exception {
        SmbAPI api = App.sInstance.getSmbAPI();

        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        if (source.isDirectory()) {
            api.createDirectory(destination + "/" + source.getName());

            String[] files = source.list();
            for (String file : files) {
                copyToSmb(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            SmbFile destinationFile = api.createSmbFile(destination + "/" + source.getName());
            SmbFileOutputStream out = new SmbFileOutputStream(destinationFile, true);
            copyStreamRoutine(source, out);
        }
    }

    private void copyToYandexDisk(File source, String destination) throws Exception {
        YandexDiskApi api = App.sInstance.getYandexDiskApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }
        if (source.isDirectory()) {
            api.createDirectory(destination + "/" + source.getName());

            String[] files = source.list();
            for (String file : files) {
                copyToYandexDisk(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            mCurrentFile = source.getName();
            long tempSize = doneSize;
            api.client().uploadFile(source.getAbsolutePath(), destination + (destination.endsWith("/") ? "" : "/"),
                    new com.yandex.disk.client.ProgressListener() {
                        long mPrevProgress = 0;

                        @Override
                        public void updateProgress(long loaded, long total) {
                            doneSize += (loaded - mPrevProgress);
                            mPrevProgress = loaded;
                            CopyToNetworkTask.this.updateProgress();
                        }

                        @Override
                        public boolean hasCancelled() {
                            return false;
                        }
                    });
            doneSize = tempSize + source.length();
        }
    }

    private void copyToMediaFire(final File source, String destination) throws Exception {
        MediaFireApi api = App.sInstance.getMediaFireApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }

        if (source.isDirectory()) {
            api.createDirectory(destination + "/" + source.getName());

            String[] files = source.list();
            for (String file : files) {
                copyToMediaFire(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            mCurrentFile = source.getName();
            MediaFireUpload upload = new MediaFireUpload(api.getMediaFire(), 200, source, source.getName(), destination, MediaFireUpload.ActionOnInAccount.UPLOAD_ALWAYS,
                    new MediaFireUploadHandler() {
                        @Override
                        public void uploadFailed(long id, MFException e) {
                        }

                        @Override
                        public void uploadFailed(long id, MFApiException e) {
                        }

                        @Override
                        public void uploadFailed(long id, MFSessionNotStartedException e) {
                        }

                        @Override
                        public void uploadFailed(long id, IOException e) {
                        }

                        @Override
                        public void uploadFailed(long id, InterruptedException e) {
                        }

                        @Override
                        public void uploadProgress(long id, double percentFinished) {
                        }

                        @Override
                        public void uploadFinished(long id, String quickKey, String fileName) {
                            doneSize += source.length();
                            updateProgress();
                        }

                        @Override
                        public void uploadPolling(long id, int statusCode, String description) {
                            updateProgress();
                        }
                    }, 1);
            upload.run();

        }
    }

    private void copyStreamRoutine(File source, OutputStream out) throws IOException {
        mCurrentFile = source.getName();
        FileInputStream in = new FileInputStream(source);
        int len;
        while ((len = in.read(BUFFER)) > 0) {
            out.write(BUFFER, 0, len);
            doneSize += len;
            updateProgress();
        }

        out.close();
        in.close();
    }
}
