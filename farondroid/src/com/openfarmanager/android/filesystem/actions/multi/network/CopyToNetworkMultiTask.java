package com.openfarmanager.android.filesystem.actions.multi.network;

import android.content.Context;

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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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
public class CopyToNetworkMultiTask extends NetworkActionMultiTask {

    protected String mDestination;

    public CopyToNetworkMultiTask(Context context, NetworkEnum networkType, OnActionListener listener, List<File> items, String destination) {
        super(context, listener, items, networkType);
        mDestination = destination;
    }

    @Override
    public TaskStatusEnum doAction() {
        for (File file : mItems) {
            if (isCancelled()) {
                onTaskDone(CANCELED);
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
                NetworkException ex = NetworkException.handleNetworkException(e);
                return ex.getErrorCause() == NetworkException.ErrorCause.Unknown_Error ?
                        ERROR_COPY : createNetworkError(ex);
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

        mCurrentFile = getProgressText();
        updateProgress();

        return TaskStatusEnum.OK;
    }

    @Override
    public TaskStatusEnum handleSubTaskException(Exception e) {
        if (e instanceof NullPointerException) {
            return ERROR_FILE_NOT_EXISTS;
        } else if (e instanceof InterruptedIOException) {
            return CANCELED;
        } else if (e instanceof IllegalArgumentException || e instanceof FTPDataTransferException) {
            return ERROR_COPY;
        } else {
            e.printStackTrace();
            NetworkException ex = NetworkException.handleNetworkException(e);
            return ex.getErrorCause() == NetworkException.ErrorCause.Unknown_Error ?
                    ERROR_COPY : createNetworkError(ex);
        }
    }

    @Override
    public void onSubTaskDone(Future future) {
        super.onSubTaskDone(future);
        mCurrentFile = getProgressText();
        updateProgress();
    }

    private void copyToGoogleDrive(final File source, final String destination) throws Exception {
        final GoogleDriveApi api = App.sInstance.getGoogleDriveApi();
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
            runSubTaskAsynk(new Callable() {
                @Override
                public Object call() throws Exception {
                    api.upload(api.findPathId(destination), source.getName(), source, new GoogleDriveWebApi.UploadListener() {
                        @Override
                        public void onProgress(int uploaded, int transferedPortion, int total) {
                            mDoneSize += transferedPortion;
                            updateProgress();
                        }
                    });
                    return null;
                }
            }, source);
        }
    }

    private void copyToDropbox(final File source, final String destination) throws DropboxException, IOException {
        final DropboxAPI api = App.sInstance.getDropboxApi();
        if (isCancelled()) {
            throw new InterruptedIOException();
        }
        if (source.isDirectory()) {
            String[] files = source.list();
            for (String file : files) {
                copyToDropbox(new File(source, file), destination + "/" + source.getName());
            }
        } else {
            runSubTaskAsynk(new Callable() {
                @Override
                public Object call() throws Exception {
                    api.putFileOverwrite(destination + "/" + source.getName(), new FileInputStream(source), source.length(), new ProgressListener() {
                        long mPrevProgress = 0;

                        @Override
                        public void onProgress(long l, long l2) {
                            mDoneSize += (l - mPrevProgress);
                            mPrevProgress = l;
                            updateProgress();
                        }
                    });
                    return null;
                }
            }, source);
        }
    }

    private void copyToSkyDrive(final File source, final String destination) throws Exception {
        final SkyDriveAPI api = App.sInstance.getSkyDriveApi();
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
            runSubTaskAsynk(new Callable() {
                @Override
                public Object call() throws Exception {
                    api.getConnectClient().upload(api.findPathId(destination), source.getName(), source, OverwriteOption.Overwrite);
                    mDoneSize += source.length();
                    updateProgress();
                    return null;
                }
            }, source);
        }
    }

    private void copyToFtp(final File source, final String destination) throws Exception {
        final FtpAPI api = App.sInstance.getFtpApi();

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
            runSubTaskAsynk(new Callable() {
                @Override
                public Object call() throws Exception {
                    api.client().changeDirectory(destination);
                    api.client().upload(source, new FTPDataTransferListener() {
                        @Override
                        public void started() {
                        }

                        @Override
                        public void transferred(int i) {
                            mDoneSize += i;
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
                    return null;
                }
            }, source);
        }
    }

    private void copyToSmb(final File source, final String destination) throws Exception {
        final SmbAPI api = App.sInstance.getSmbAPI();

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
            runSubTaskAsynk(new Callable() {
                @Override
                public Object call() throws Exception {
                    SmbFile destinationFile = api.createSmbFile(destination + "/" + source.getName());
                    SmbFileOutputStream out = new SmbFileOutputStream(destinationFile, true);
                    FileInputStream in = new FileInputStream(source);
                    int len;
                    byte[] buf = new byte[512 * 1024];
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                        mDoneSize += len;
                        updateProgress();
                    }

                    out.close();
                    in.close();

                    return null;
                }
            }, source);
        }
    }

    private void copyToYandexDisk(final File source, final String destination) throws Exception {
        final YandexDiskApi api = App.sInstance.getYandexDiskApi();
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
            runSubTaskAsynk(new Callable() {
                @Override
                public Object call() throws Exception {
                    api.client().uploadFile(source.getAbsolutePath(), destination + (destination.endsWith("/") ? "" : "/"),
                            new com.yandex.disk.client.ProgressListener() {
                                long mPrevProgress = 0;

                                @Override
                                public void updateProgress(long loaded, long total) {
                                    mDoneSize += (loaded - mPrevProgress);
                                    mPrevProgress = loaded;
                                    CopyToNetworkMultiTask.this.updateProgress();
                                }

                                @Override
                                public boolean hasCancelled() {
                                    return false;
                                }
                            });

                    return null;
                }
            }, source);
        }
    }

    private void copyToMediaFire(final File source, final String destination) throws Exception {
        final MediaFireApi api = App.sInstance.getMediaFireApi();
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
            runSubTaskAsynk(new Callable() {
                @Override
                public Object call() throws Exception {
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
                                    mDoneSize += source.length();
                                    updateProgress();
                                }

                                @Override
                                public void uploadPolling(long id, int statusCode, String description) {
                                    updateProgress();
                                }
                            }, 1);
                    upload.run();
                    return null;
                }
            }, source);
        }
    }

}
