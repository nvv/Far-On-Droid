package com.openfarmanager.android.adapters;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.GenericPanel;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.view.ToastNotification;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LauncherAdapter extends FileSystemAdapter {

    private Handler mHandler;

    private List<String> mSelectedPackages = new ArrayList<String>();
    private CompositeDisposable mSubscription;

    public LauncherAdapter(Handler handler, CompositeDisposable subscription) {
        mHandler = handler;
        mSubscription = subscription;
        refresh();
    }

    public void refresh() {
        mSelectedPackages.clear();
        mHandler.sendEmptyMessage(GenericPanel.START_LOADING);
        Disposable disposable = Single.create((SingleOnSubscribe<List<FileProxy>>) e -> {
                    List<FileProxy> result = new LinkedList<>();

                    final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                    final PackageManager manager = App.sInstance.getPackageManager();
                    final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);

                    if (apps != null) {
                        for (ResolveInfo info : apps) {
                            ComponentName componentName = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
                            ComponentProxy applicationInfo = new ComponentProxy();
                            applicationInfo.mComponentName = componentName;
                            applicationInfo.mName = String.valueOf(info.loadLabel(manager));
                            applicationInfo.mPackagePath = info.activityInfo.applicationInfo.sourceDir;
                            if (TextUtils.isEmpty(applicationInfo.mName)) {
                                applicationInfo.mName = info.activityInfo.name;
                            }
                            result.add(applicationInfo);
                        }
                    }

                    Collections.sort(result, (info1, info2) -> info1.getName().compareToIgnoreCase(info2.getName()));
                    e.onSuccess(result);
                }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                result -> {
                    mFiles = result;
                    notifyDataSetChanged();
                    mHandler.sendEmptyMessage(GenericPanel.STOP_LOADING);
                    populateApplicationSize();
                }, Throwable::printStackTrace
            );

        mSubscription.add(disposable);
    }

    private void populateApplicationSize() {

        Disposable disposable = Observable.create(emitter -> {
            try {
                final PackageManager manager = App.sInstance.getPackageManager();
                Method getPackageSizeInfo = manager.getClass().getMethod("getPackageSizeInfo",
                        String.class,
                        IPackageStatsObserver.class);

                final Semaphore codeSizeSemaphore = new Semaphore(1, true);

                for (FileProxy param : mFiles) {
                    try {
                        codeSizeSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    }

                    final FileProxy fileProxy = param;
                    getPackageSizeInfo.invoke(manager, ((ComponentProxy) param).mComponentName.getPackageName(),
                            new IPackageStatsObserver.Stub() {
                                public void onGetStatsCompleted(PackageStats pStats, boolean succeedded)
                                        throws RemoteException {
                                    ((ComponentProxy) fileProxy).mSize = pStats.codeSize + pStats.dataSize;
                                    codeSizeSemaphore.release();
                                    if (!emitter.isDisposed()) {
                                        emitter.onNext(fileProxy);
                                    }
                                }
                            });
                }


            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(fileProxy -> notifyDataSetChanged(), Throwable::printStackTrace);

        mSubscription.add(disposable);
    }

    public void setSelectedFiles(List<FileProxy> selectedFiles) {
        super.setSelectedFiles(selectedFiles);

        mSelectedPackages.clear();
        for (FileProxy proxy : selectedFiles) {
            mSelectedPackages.add(proxy.getId());
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    @Override
    public FileProxy getItem(int i) {
        return mFiles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    protected void bindView(ViewHolder holder, final int position) {

        FileProxy info = getItem(position);

        holder.name.setText(info.getName());
        holder.info.setText(info.getSize() > 0 ? FileUtilsExt.byteCountToDisplaySize(info.getSize()) : null);

        holder.configureCell(App.sInstance.getSettings());

        holder.name.setTextColor(mSelectedPackages.contains(info.getId()) ?
                App.sInstance.getSettings().getSelectedColor() : App.sInstance.getSettings().getInstallColor());

//        int size = App.sInstance.getSettings().getMainPanelFontSize();
//        holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
//        holder.info.setTextSize(TypedValue.COMPLEX_UNIT_SP, size); // to adjust item size
//
//        Typeface typeface = App.sInstance.getSettings().getMainPanelFontType();
//        holder.name.setTypeface(typeface);
//        holder.info.setTypeface(typeface);
    }

    public void onItemClick(int i) {
        try {
            final PackageManager manager = App.sInstance.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(getItem(i).getFullPath());
            App.sInstance.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastNotification.makeText(App.sInstance.getApplicationContext(),
                    App.sInstance.getString(R.string.error_can_t_open_app), Toast.LENGTH_LONG).show();
        }
    }

    public FileActionEnum[] getAvailableActions() {
        if (mSelectedFiles.size() > 1) {
            return new FileActionEnum[]{
                    FileActionEnum.DELETE, FileActionEnum.SHARE
            };
        } else {
            return new FileActionEnum[]{
                    FileActionEnum.OPEN, FileActionEnum.INFO, FileActionEnum.DELETE, FileActionEnum.SHARE
            };
        }
    }

    public void executeAction(final FileActionEnum action, MainPanel inactivePanel) {
        if (mSelectedFiles.isEmpty()) {
            return;
        }

        Intent intent;
        String fullPath = mSelectedFiles.get(0).getFullPath();
        switch (action) {
            case OPEN:
                final PackageManager manager = App.sInstance.getPackageManager();
                App.sInstance.startActivity(manager.getLaunchIntentForPackage(fullPath));
                break;

            case INFO:
                try {
                    intent = new Intent()
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", fullPath, null));
                    App.sInstance.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastNotification.makeText(App.sInstance, App.sInstance.getString(R.string.error_not_supported), Toast.LENGTH_LONG).show();
                }
                break;

            case DELETE:
                for (FileProxy file : mSelectedFiles) {
                    doDelete(file.getFullPath());
                }
                break;

            case SHARE:
                intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ArrayList<Uri> files = new ArrayList<Uri>();

                for (FileProxy file : mSelectedFiles) {
                    File f = new File(file.getParentPath());
                    Uri uri = Uri.fromFile(f);
                    files.add(uri);
                }

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

                App.sInstance.startActivity(intent);
                break;

        }
    }

    private void doDelete(String fullPath) {
        try {
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", fullPath, null)).
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.sInstance.startActivity(uninstallIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastNotification.makeText(App.sInstance, App.sInstance.getString(R.string.error_not_supported), Toast.LENGTH_LONG).show();
        }
    }

    public class ComponentProxy implements FileProxy<ComponentProxy> {
        private ComponentName mComponentName;
        private String mName;
        private String mPackagePath;
        private long mSize;

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
            return false;
        }

        @Override
        public long getSize() {
            return mSize;
        }

        @Override
        public long lastModifiedDate() {
            return 0;
        }

        @Override
        public List getChildren() {
            return new ArrayList();
        }

        @Override
        public String getFullPath() {
            return mComponentName.getPackageName();
        }

        @Override
        public String getFullPathRaw() {
            return mComponentName.getPackageName();
        }

        @Override
        public String getParentPath() {
            return mPackagePath;
        }

        @Override
        public boolean isUpNavigator() {
            return false;
        }

        @Override
        public boolean isRoot() {
            return false;
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

}
