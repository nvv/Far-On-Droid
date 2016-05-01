package com.openfarmanager.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemFile;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.view.ToastNotification;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Performs search and shows result.
 *
 * @author Vlad Namashko.
 */
public class SearchResultDialog extends Dialog {

    private FileProxy mSelected;
    private ListView mList;
    private View mDialogView;

    private int mSelectedFilePosition = -1;

    private final List<FileProxy> mData = Collections.synchronizedList(new LinkedList<FileProxy>());

    private FolderScanner mScanner;
    private NetworkScanner mNetworkScanner;
    private ProgressBar mProgressBar;

    private NetworkEnum mNetworkType;
    private SearchActionDialog.SearchActionResult mSearchOptions;
    private SearchResultListener mListener;
    private String mCurrentDir;

    public SearchResultDialog(Context context, NetworkEnum networkType, String currentDir,
                              SearchActionDialog.SearchActionResult searchOption, SearchResultListener listener) {
        super(context);
        mNetworkType = networkType;
        mCurrentDir = currentDir;
        mSearchOptions = searchOption;
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.search_result, null);
        setContentView(mDialogView);

        mDialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mDialogView.findViewById(R.id.go_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkFileSelected()) {
                    return;
                }
                mListener.onGotoFile(mSelected);
                dismiss();
            }
        });

        mDialogView.findViewById(R.id.new_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResetSearch();
                dismiss();
            }
        });

        mDialogView.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkFileSelected()) {
                    return;
                }
                mListener.onViewFile(mSelected);
            }
        });

        mDialogView.findViewById(R.id.view).setVisibility(mSearchOptions.isNetworkPanel ? View.GONE : View.VISIBLE);

        mProgressBar = (ProgressBar) mDialogView.findViewById(android.R.id.progress);

        mList = (ListView) mDialogView.findViewById(android.R.id.list);

        mList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelected = (FileProxy) view.getTag();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelected = (FileProxy) view.getTag();
                mSelectedFilePosition = i;
                ((BaseAdapter) mList.getAdapter()).notifyDataSetChanged();
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelected = (FileProxy) view.getTag();

                if (!checkFileSelected()) {
                    return true;
                }
                mListener.onGotoFile(mSelected);
                dismiss();
                return true;
            }
        });

        mList.setAdapter(new BaseAdapter() {
            private List<FileProxy> mData;

            @Override
            public int getCount() {
                return mData == null ? 0 : mData.size();
            }

            @Override
            public Object getItem(int i) {
                return mData.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                TextView textView;
                if (view == null) {
                    textView = new TextView(getContext());
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(18);
                } else {
                    textView = (TextView) view;
                }
                FileProxy f = (FileProxy) getItem(i);
                textView.setText(f.getName());
                textView.setTag(f);
                textView.setBackgroundResource(mSelectedFilePosition == i ? R.color.selected_item : R.color.main_grey);
                return textView;
            }

            @Override
            public void notifyDataSetChanged() {
                synchronized (SearchResultDialog.this.mData) {
                    mData = new LinkedList<>(SearchResultDialog.this.mData);
                }
                super.notifyDataSetChanged();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mScanner != null) {
                    mScanner.cancel(true);
                }
            }
        });

        if (mCurrentDir != null) {
            if (mNetworkType != null) {
                if (mNetworkScanner == null) {
                    mNetworkScanner = new NetworkScanner(mNetworkType);
                    mNetworkScanner.execute(mCurrentDir);
                }
            } else {
                if (mScanner == null) {
                    mScanner = new FolderScanner();
                    mScanner.execute(new File(mCurrentDir));
                }
            }
        }
    }

    private boolean checkFileSelected() {
        if (mSelected == null) {
            ToastNotification.makeText(getContext(), App.sInstance.getString(R.string.error_no_selected_file), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private IOCase isCaseSensitive() {
        return mSearchOptions.caseSensitive ? IOCase.SENSITIVE : IOCase.INSENSITIVE;
    }

    /**
     * Finds files and folder by Wildcard
     */
    private class FolderScanner extends AsyncTask<File, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(File... files) {
            for (File f : files) {
                searchDirectory(f);
            }
            return null;
        }

        private void searchDirectory(File mDir) {
            if (mDir == null) {
                return;
            }
            File[] files = mDir.listFiles();

            if (files == null) {
                return;
            }

            for (File f : files) {
                if (isCancelled()) {
                    return;
                }

                if (FilenameUtils.wildcardMatch(f.getName(), mSearchOptions.fileMask, isCaseSensitive())) {
                    if (mSearchOptions.keyword != null && f.isFile()) {
                        if (searchFile(f)) {
                            mData.add(new FileSystemFile(f.getAbsolutePath()));
                            publishProgress();
                        }
                    } else {
                        mData.add(new FileSystemFile(f.getAbsolutePath()));
                        publishProgress();
                    }
                }
                if (isCancelled()) {
                    return;
                }
                if (f.isDirectory() && f.canRead()) {
                    searchDirectory(f);
                }
            }
        }

        private boolean searchFile(File f) {
            if (!f.canRead()) {
                return false;
            }
            BufferedReader is = null;
            try {
                String keyword = mSearchOptions.keyword;
                Pattern pattern = Pattern.compile(mSearchOptions.wholeWords ? String.format("\\b%s\\b", Pattern.quote(keyword)) : Pattern.quote(keyword),
                        isCaseSensitive() == IOCase.INSENSITIVE ? Pattern.CASE_INSENSITIVE : 0);
                is = new BufferedReader(new FileReader(f));
                String line;
                while ((line = is.readLine()) != null) {
                    if (pattern.matcher(line).find()) {
                        return true;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (PatternSyntaxException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
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

        @Override
        protected void onProgressUpdate(Void... values) {
            ((BaseAdapter) mList.getAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * Finds files in network storage
     */
    private class NetworkScanner extends AsyncTask<String, Void, List<FileProxy>> {

        private NetworkEnum mNetworkType;

        public NetworkScanner(NetworkEnum networkType) {
            mNetworkType = networkType;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FileProxy> doInBackground(String ... path) {
            return getNetworkApi().search(path[0], mSearchOptions.fileMask);
        }

        @Override
        protected void onPostExecute(List<FileProxy> searchResult) {
            mData.clear();
            mData.addAll(searchResult);
            ((BaseAdapter) mList.getAdapter()).notifyDataSetChanged();
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        private NetworkApi getNetworkApi() {
            switch (mNetworkType) {
                case Dropbox: default:
                    return App.sInstance.getDropboxApi();
                case SkyDrive:
                    return App.sInstance.getSkyDriveApi();
                case GoogleDrive:
                    return App.sInstance.getGoogleDriveApi();
                case MediaFire:
                    return App.sInstance.getMediaFireApi();
                case WebDav:
                    return App.sInstance.getWebDavApi();
            }
        }
    }

    public interface SearchResultListener extends Serializable {
        void onGotoFile(FileProxy file);
        void onViewFile(FileProxy file);
        void onResetSearch();
    }
}
