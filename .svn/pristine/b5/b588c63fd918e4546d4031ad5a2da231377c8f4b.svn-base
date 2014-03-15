package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.microsoft.live.OverwriteOption;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.core.network.ftp.FtpAPI;
import com.openfarmanager.android.core.network.skydrive.SkyDriveAPI;
import com.openfarmanager.android.core.network.smb.SmbAPI;
import com.openfarmanager.android.core.network.yandexdisk.YandexDiskApi;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.yandex.disk.client.exceptions.WebdavException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import static com.openfarmanager.android.model.TaskStatusEnum.CANCELED;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_COPY;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_FILE_NOT_EXISTS;
import static com.openfarmanager.android.model.TaskStatusEnum.createNetworkError;
import static com.openfarmanager.android.utils.Extensions.runAsynk;

/**
 * @author Vlad Namashko
 */
public class CopyToNetworkTask extends NetworkActionTask {

    private final static byte[] BUFFER = new byte[256 * 1024];

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

}
