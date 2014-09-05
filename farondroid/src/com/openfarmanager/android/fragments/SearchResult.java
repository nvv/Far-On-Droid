package com.openfarmanager.android.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemFile;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.ParcelableWrapper;
import com.openfarmanager.android.view.ToastNotification;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Performs search and shows result
 */
public class SearchResult extends DialogFragment {

    private FileProxy mSelected;
    private ListView mList;

    private final List<FileProxy> mData = Collections.synchronizedList(new LinkedList<FileProxy>());

    private FolderScanner mScanner;
    private NetworkScanner mNetworkScanner;
    private ProgressBar mProgressBar;

    public static SearchResult newInstance(boolean onlyFileSearch, NetworkEnum networkType, String currentDir, String fileMask, String keyword, boolean caseSensitive, boolean wholeWords, SearchResultListener listener) {
        SearchResult searchResult = new SearchResult();
        Bundle args = new Bundle();

        args.putBoolean("file_search", onlyFileSearch);
        args.putInt("network", networkType != null ? networkType.ordinal() : -1);
        args.putString("currentDir", currentDir);
        args.putString("fileMask", fileMask);
        if (!TextUtils.isEmpty(keyword)) {
            args.putString("keyword", keyword.trim());
        }
        args.putBoolean("caseSensitive", caseSensitive);
        args.putBoolean("wholeWords", wholeWords);
        args.putParcelable("listener", new ParcelableWrapper<SearchResultListener>(listener));

        searchResult.setArguments(args);
        return searchResult;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_result, container);
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.go_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkFileSelected()) {
                    return;
                }

                SearchResultListener listener = getListener();
                if (listener != null) {
                    listener.onGotoFile(mSelected);
                }
                dismiss();
            }
        });

        view.findViewById(R.id.new_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchResultListener listener = getListener();
                if (listener != null) {
                    listener.onResetSearch();
                }
                dismiss();
            }
        });

        view.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkFileSelected()) {
                    return;
                }

                SearchResultListener listener = getListener();
                if (listener != null) {
                    listener.onViewFile(mSelected);
                }
            }
        });

        view.findViewById(R.id.view).setVisibility(getArguments().getBoolean("file_search") ? View.GONE : View.VISIBLE);

        mProgressBar = (ProgressBar) view.findViewById(android.R.id.progress);

        mList = (ListView) view.findViewById(android.R.id.list);

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

                if (!checkFileSelected()) {
                    return;
                }

                SearchResultListener listener = getListener();
                if (listener != null) {
                    if (getArguments().getInt("network") == -1) {
                        listener.onViewFile(mSelected);
                    } else {
                        listener.onGotoFile(mSelected);
                        dismiss();
                    }
                }
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelected = (FileProxy) view.getTag();

                if (!checkFileSelected()) {
                    return true;
                }

                SearchResultListener listener = getListener();
                if (listener != null) {
                    listener.onGotoFile(mSelected);
                }
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
                    textView = new TextView(getActivity());
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(18);
                } else {
                    textView = (TextView) view;
                }
                FileProxy f = (FileProxy) getItem(i);
                textView.setText(f.getName());
                textView.setTag(f);
                return textView;
            }

            @Override
            public void notifyDataSetChanged() {
                synchronized (SearchResult.this.mData) {
                    mData = new LinkedList<FileProxy>(SearchResult.this.mData);
                }
                super.notifyDataSetChanged();
            }
        });

        return view;
    }

    private SearchResultListener getListener() {
        //noinspection unchecked
        try {
            return ((ParcelableWrapper<SearchResultListener>) getArguments().getParcelable("listener")).value;
        } catch (Exception e) {
            return null;
        }
    }

    private IOCase getCaseSensitive() {
        return getArguments().getBoolean("caseSensitive") ? IOCase.SENSITIVE : IOCase.INSENSITIVE;
    }

    private String getFileMask() {
        return getArguments().getString("fileMask");
    }

    private boolean checkFileSelected() {
        if (mSelected == null) {
            ToastNotification.makeText(getActivity(), App.sInstance.getString(R.string.error_no_selected_file), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        String path = getArguments().getString("currentDir");
        if (path != null) {
            NetworkEnum networkEnum = NetworkEnum.fromOrdinal(getArguments().getInt("network"));
            if (networkEnum != null) {
                if (mNetworkScanner == null) {
                    mNetworkScanner = new NetworkScanner(networkEnum);
                    mNetworkScanner.execute(path);
                }
            } else {
                if (mScanner == null) {
                    mScanner = new FolderScanner();
                    mScanner.execute(new File(path));
                }
            }
        }
        super.onStart();
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

                if (FilenameUtils.wildcardMatch(f.getName(), getFileMask(), getCaseSensitive())) {
                    if (getArguments().getString("keyword") != null && f.isFile()) {
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
                String keyword = getArguments().getString("keyword");
                Pattern pattern = Pattern.compile(getArguments().getBoolean("wholeWords") ? String.format("\\b%s\\b", Pattern.quote(keyword)) : Pattern.quote(keyword),
                        getCaseSensitive() == IOCase.INSENSITIVE ? Pattern.CASE_INSENSITIVE : 0);
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
            return getNetworkApi().search(path[0], getFileMask());
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
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mScanner != null) {
            mScanner.cancel(true);
        }
        super.onDismiss(dialog);
    }

    public interface SearchResultListener extends Serializable {
        public void onGotoFile(FileProxy file);

        public void onViewFile(FileProxy file);

        public void onResetSearch();
    }

}
