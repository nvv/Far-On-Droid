package com.openfarmanager.android.adapters;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class LauncherAdapter extends FlatFileSystemAdapter {

    private Handler mHandler;

    private List<String> mSelectedPackages = new ArrayList<String>();

    public LauncherAdapter(Handler handler) {
        mHandler = handler;
        refresh();
    }

    public void refresh() {
        new AsyncTask<Void, Void, List<FileProxy>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSelectedPackages.clear();
                mHandler.sendEmptyMessage(GenericPanel.START_LOADING);
            }

            @Override
            protected List<FileProxy> doInBackground(Void... voids) {
                List<FileProxy> result = new LinkedList<FileProxy>();

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

                Collections.sort(result, new Comparator<FileProxy>() {
                    @Override
                    public int compare(FileProxy applicationInfo, FileProxy applicationInfo2) {
                        return applicationInfo.getName().compareToIgnoreCase(applicationInfo2.getName());
                    }
                });

                return result;
            }

            @Override
            protected void onPostExecute(List<FileProxy> result) {
                mFiles = result;
                notifyDataSetChanged();
                mHandler.sendEmptyMessage(GenericPanel.STOP_LOADING);
                populateApplicationSize();
            }
        }.execute();
    }

    private void populateApplicationSize() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
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

                        final FileProxy final_param = param;
                        getPackageSizeInfo.invoke(manager, ((ComponentProxy) param).mComponentName.getPackageName(),
                                new IPackageStatsObserver.Stub() {
                                    public void onGetStatsCompleted(PackageStats pStats, boolean succeedded)
                                            throws RemoteException {
                                        ((ComponentProxy) final_param).mSize = pStats.codeSize + pStats.dataSize;
                                        codeSizeSemaphore.release();
                                        publishProgress();
                                    }
                                });
                    }


                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                notifyDataSetChanged();
            }
        }.execute();
    }

    public void setSelectedFiles(List<FileProxy> selectedFiles) {
        super.setSelectedFiles(selectedFiles);

        mSelectedPackages.clear();
        for (FileProxy proxy : selectedFiles) {
            mSelectedPackages.add(proxy.getId());
        }
    }

    @Override
    public int getCount() {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(App.sInstance.getApplicationContext()).inflate(R.layout.panel_item, null);
        }

        FileProxy info = getItem(i);

        TextView name = (TextView) view.findViewById(R.id.item_name);
        TextView infoItem = (TextView) view.findViewById(R.id.item_info);

        name.setText(info.getName());
        if (info.getSize() > 0) {
            infoItem.setText(FileUtilsExt.byteCountToDisplaySize(info.getSize()));
        } else {
            infoItem.setText(null);
        }

        if (mSelectedPackages.contains(info.getId())) {
            name.setTextColor(App.sInstance.getSettings().getSelectedColor());
        } else {
            name.setTextColor(App.sInstance.getSettings().getInstallColor());
        }


        int size = App.sInstance.getSettings().getMainPanelFontSize();
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        infoItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, size); // to adjust item size

        Typeface typeface = App.sInstance.getSettings().getMainPanelFontType();
        name.setTypeface(typeface);
        infoItem.setTypeface(typeface);

        return view;
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
        public List<ComponentProxy> getChildren() {
            return null;
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
