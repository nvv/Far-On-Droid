package com.openfarmanager.android.fragments;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.LinesAdapter;
import com.openfarmanager.android.filesystem.actions.RootTask;
import com.openfarmanager.android.model.ViewerBigFileTextViewer;
import com.openfarmanager.android.model.ViewerTextBuffer;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.utils.ReversedIterator;
import com.openfarmanager.android.view.QuickPopupDialog;
import com.openfarmanager.android.view.SearchableTextView;
import com.openfarmanager.android.view.SelectEncodingDialog;
import com.openfarmanager.android.view.ToastNotification;
import org.apache.commons.io.IOCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static com.openfarmanager.android.controllers.EditViewController.MSG_BIG_FILE;
import static com.openfarmanager.android.controllers.EditViewController.MSG_TEXT_CHANGED;
import static com.openfarmanager.android.utils.Extensions.getThreadPool;
import static com.openfarmanager.android.utils.Extensions.runAsynk;

/**
 * File viewer
 */
public class Viewer extends Fragment {

    private static final String TAG = "::: Viewer :::";

    private static final int MB = 1048576;
    public static final int MAX_FILE_SIZE = 3 * MB;
    public static final int LINES_COUNT_FRAGMENT = 2000;

    private File mFile;
    private ListView mList;
    private LinesAdapter mAdapter;
    private ProgressBar mProgress;
    private Handler mHandler;

    private ViewerTextBuffer mText;
    private ViewerBigFileTextViewer mBigText;

    private boolean mStopLoading;
    private boolean mBigFile;

    private LoadFileTask mLoadFileTask;
    private LoadTextFragmentTask mLoadTextFragmentTask;

    private Charset mSelectedCharset = Charset.forName("UTF-8");
    private Dialog mCharsetSelectDialog;

    protected QuickPopupDialog mSearchResultsPopup;

    private int mCalculatedRowHeight = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EasyTracker.getInstance(App.sInstance).send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Viewer").build());

        View view = inflater.inflate(R.layout.viewer, container);
        mList = (ListView) view.findViewById(android.R.id.list);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);

        mText = new ViewerTextBuffer();
        mBigText = new ViewerBigFileTextViewer();

        view.findViewById(R.id.root_view).setBackgroundColor(App.sInstance.getSettings().getViewerColor());

        mSearchResultsPopup = new QuickPopupDialog(view, R.layout.search_results_popup);
        mSearchResultsPopup.setPosition(Gravity.RIGHT | Gravity.TOP, (int) (50 * getResources().getDisplayMetrics().density));
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mCharsetSelectDialog != null && mCharsetSelectDialog.isShowing()) {
            adjustDialogSize(mCharsetSelectDialog);
        }
    }

    @Override
    public void onDestroy() {
        if (mLoadFileTask != null) {
            mStopLoading = true;
            mLoadFileTask.cancel(true);
            mLoadFileTask = null;
        }

        mSearchResultsPopup.dismiss();
        super.onDestroy();
    }

    public void changeMode() {
        mAdapter.setMode(mAdapter.getMode() == LinesAdapter.MODE_VIEW ? LinesAdapter.MODE_EDIT : LinesAdapter.MODE_VIEW);
        EasyTracker.getInstance(App.sInstance).send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Viewer").set("MODE", mAdapter.getMode() == LinesAdapter.MODE_VIEW ? "view" : "edit").build());
        updateAdapter();
    }

    public int getMode() {
        return mAdapter.getMode();
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void setEncoding(Charset charset) {
        mSelectedCharset = charset;
        openSelectedFile();
    }

    public void gotoLine(final int lineNumber, int type) {
        int totalLinesNumber = mAdapter.getCount();
        int position = type == EditViewGotoDialog.GOTO_LINE_POSITION ?
                lineNumber : (int) (totalLinesNumber / 100.0 * lineNumber);

        if (position > totalLinesNumber) {
            position = totalLinesNumber;
        }

        mList.setSelection(position);
    }

    public void openFile(final File file) {
        mFile = file;

        String charset = App.sInstance.getSettings().getDefaultCharset();

        if (charset == null) {
            showSelectEncodingDialog();
        } else {
            setEncoding(Charset.forName(charset));
        }
    }

    private void openSelectedFile() {
        mProgress.setVisibility(View.VISIBLE);

        if (mFile.length() > MAX_FILE_SIZE) {
            mBigFile = true;
            mHandler.sendMessage(Message.obtain(mHandler, MSG_BIG_FILE));
            Log.d(TAG, "file to large to be opened in edit mode");
            ToastNotification.makeText(Viewer.this.getActivity(),
                    App.sInstance.getString(R.string.error_file_is_too_big), Toast.LENGTH_SHORT).show();

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == mAdapter.getCount() - 1 && mBigText.hasBottomFragment()) {
                        loadBifFileFragment(mBigText.nextFragment());
                    } else if (i == 0 && mBigText.hasUpperFragment()) {
                        loadBifFileFragment(mBigText.previousFragment());
                    }
                }
            });
        }

        mAdapter = new LinesAdapter(mBigFile ? mBigText : mText);
        mList.setAdapter(mAdapter);

        mStopLoading = false;
        mLoadFileTask = new LoadFileTask();
        //noinspection unchecked
        mLoadFileTask.execute();
    }

    private void loadBifFileFragment(int fragmentNumber) {
        if (mLoadTextFragmentTask != null) {
            mLoadTextFragmentTask.cancel(true);
        }

        mLoadTextFragmentTask = new LoadTextFragmentTask(fragmentNumber);
        //noinspection unchecked
        mLoadTextFragmentTask.execute();
    }

    public void search(String pattern, boolean caseSensitive, boolean wholeWords, boolean regularExpression) {
        mAdapter.search(pattern, caseSensitive, wholeWords, regularExpression);

        doSearch(pattern, caseSensitive, wholeWords, regularExpression);
    }

    public void doSearch(final String pattern, final boolean caseSensitive,
                         final boolean wholeWords, final boolean regularExpression) {

        if (!mSearchResultsPopup.isShowing()) {
            mSearchResultsPopup.show();
        }

        final List<Integer> searchLines = Collections.synchronizedList(new ArrayList<Integer>());
        final Map<Integer, int[]> wordsOnLine = Collections.synchronizedMap(new HashMap<Integer, int[]>());

        View view = mSearchResultsPopup.getContentView();
        final View progress = view.findViewById(R.id.search_progress);
        final TextView matches = (TextView) view.findViewById(R.id.search_found);
        final View next = view.findViewById(R.id.search_next);
        final View prev = view.findViewById(R.id.search_prev);
        final View close = view.findViewById(R.id.search_close);

        progress.setVisibility(View.VISIBLE);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mList.getFirstVisiblePosition();
                for (Integer pos : new ReversedIterator<>(searchLines)) {
                    TextView textView = (TextView) getChildView(pos);

                    if (pos < position) {
                        gotoLine(pos - 1, EditViewGotoDialog.GOTO_LINE_POSITION);
                        return;
                    } else if (textView != null && textView.getLineCount() > 1 && wordsOnLine.get(pos).length > 1) {
                        Layout layout = textView.getLayout();
                        calculateRowHeight(textView, layout);
                        int firstVisibleRow = calculateFirstVisibleRow(textView);
                        int firstSearchIndex = layout.getLineEnd(firstVisibleRow - 1);

                        int prevPosition = wordsOnLine.get(pos)[0];
                        for (int wordPosition : wordsOnLine.get(pos)) {
                            if (wordPosition > firstSearchIndex) {
                                for (int j = firstVisibleRow; j >= 0; j--) {
                                    if (isSearchWordOnLine(layout, prevPosition, j)) {
                                        scrollToLine(textView, firstVisibleRow, j);
                                        return;
                                    }
                                }
                            } else {
                                prevPosition = wordPosition;
                            }
                        }
                    }
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mList.getFirstVisiblePosition() + 2;
                for (Integer pos : searchLines) {
                    TextView textView = (TextView) getChildView(pos);

                    if (pos > position) {
                        gotoLine(pos - 1, EditViewGotoDialog.GOTO_LINE_POSITION);
                        return;
                    } else if (textView != null && textView.getLineCount() > 1 && wordsOnLine.get(pos).length > 1) {
                        Layout layout = textView.getLayout();
                        calculateRowHeight(textView, layout);
                        int firstVisibleRow = calculateFirstVisibleRow(textView);
                        int firstSearchIndex = layout.getLineEnd(firstVisibleRow);

                        for (int wordPosition : wordsOnLine.get(pos)) {
                            if (wordPosition > firstSearchIndex) {
                                for (int j = firstVisibleRow + 1; j < layout.getLineCount(); j++) {
                                    if (isSearchWordOnLine(layout, wordPosition, j)) {
                                        scrollToLine(textView, firstVisibleRow, j);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        matches.setText("0");
        final Subscription subscription = Observable.create(new Observable.OnSubscribe<SearchResult>() {
            @Override
            public void call(Subscriber<? super SearchResult> subscriber) {
                ArrayList<String> lines = mAdapter.getText();
                SearchResult searchResult = new SearchResult();
                for (int i = 0; i < lines.size(); i++) {
                    searchResult.reset();
                    String line = lines.get(i);
                    doSearchInText(line, pattern, caseSensitive, wholeWords, regularExpression, searchResult);

                    if (searchResult.count > 0) {
                        searchResult.lineNumber = i;
                        subscriber.onNext(searchResult);
                    }
                }

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.from(getThreadPool())).subscribe(new Subscriber<SearchResult>() {

            private int mTotalOccurrence;

            @Override
            public void onCompleted() {
                updateUi(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(SearchResult searchResult) {
                //System.out.println("::::  " + searchResult.first + " " + searchResult.second);

                mTotalOccurrence += searchResult.count;
                searchLines.add(searchResult.lineNumber);

                int[] wordPositions = new int[searchResult.positions.size()];
                int i = 0;
                for (Integer position : searchResult.positions) {
                    wordPositions[i++] = position;
                }

                wordsOnLine.put(searchResult.lineNumber, wordPositions);

                updateUi(mUpdateOccurrences);
            }

            private Runnable mUpdateOccurrences = new Runnable() {
                @Override
                public void run() {
                    matches.setText(String.valueOf(mTotalOccurrence));
                }
            };
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscription.unsubscribe();
                mAdapter.stopSearch();
                mSearchResultsPopup.dismiss();
            }
        });
    }

    private int calculateFirstVisibleRow(TextView textView) {
        return Math.abs(Math.round(textView.getY() / mCalculatedRowHeight));
    }

    private boolean isSearchWordOnLine(Layout layout, int prevPosition, int j) {
        return prevPosition >= layout.getLineStart(j) && prevPosition <= layout.getLineEnd(j);
    }

    private void scrollToLine(TextView textView, int firstVisibleRow, int j) {
        int scrollBy = mCalculatedRowHeight * (j - firstVisibleRow);
        mList.smoothScrollBy(scrollBy + (int) (textView.getY() + firstVisibleRow * mCalculatedRowHeight), 0);
    }

    private void calculateRowHeight(TextView textView, Layout layout) {
        if (mCalculatedRowHeight == -1) {
            mCalculatedRowHeight = layout.getHeight() / textView.getLineCount();
        }
    }

    private View getChildView(int pos) {
        int firstPosition = mList.getFirstVisiblePosition() - mList.getHeaderViewsCount();
        int wantedChild = pos - firstPosition;
        return mList.getChildAt(wantedChild);
    }

    private void doSearchInText(String string, String pattern, boolean caseSensitive, boolean wholeWords, boolean regularExpression, SearchResult result) {
        Pattern patternMatch = FileUtilsExt.createWordSearchPattern(pattern, wholeWords, caseSensitive ? IOCase.SENSITIVE : IOCase.INSENSITIVE);
        Matcher matcher = patternMatch.matcher(string);

        while (matcher.find()) {
            result.positions.add(matcher.start());
            result.count++;
        }
    }

    private class SearchResult {
        int lineNumber;
        int count;
        LinkedList<Integer> positions = new LinkedList<>();

        public void reset() {
            count = 0;
            positions.clear();
        }
    }

    public void save() {
        mAdapter.saveCurrentEditLine(getActivity().getCurrentFocus());
        mSaveObservable.subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                mHandler.sendMessage(Message.obtain(mHandler, MSG_TEXT_CHANGED, mText.isTextChanged()));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Boolean aBoolean) {

            }
        });
    }

    public void replace(final String pattern, final String replaceTo, final boolean caseSensitive,
                        final boolean wholeWords, final boolean regularExpression) {

        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                mText.replace(pattern, replaceTo, caseSensitive ? IOCase.SENSITIVE : IOCase.INSENSITIVE,
                        wholeWords, regularExpression);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.from(getThreadPool())).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                updateUi(onReplaceCompleted);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Boolean aBoolean) {

            }

            private Runnable onReplaceCompleted = new Runnable() {
                @Override
                public void run() {
                    updateAdapter();
                    mSearchResultsPopup.dismiss();
                    mHandler.sendMessage(Message.obtain(mHandler, MSG_TEXT_CHANGED, mText.isTextChanged()));
                }
            };
        });
    }

    // TODO: we need to avoid this говнокод!!!
    private class LoadTextFragmentTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private int mFragment;
        private int mStartPosition;

        private LoadTextFragmentTask(int fragment) {
            mFragment = fragment;

            mStartPosition = fragment * LINES_COUNT_FRAGMENT;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            BufferedReader is = null;
            ArrayList<String> lines = new ArrayList<String>();
            try {
                is = new BufferedReader(new InputStreamReader(new FileInputStream(mFile), mSelectedCharset.name()));
                String line;

                int count = 0;
                while (count < mStartPosition && (is.readLine()) != null) {
                    count++;
                }

                count = 0;
                while (count < LINES_COUNT_FRAGMENT && (line = is.readLine()) != null) {
                    lines.add(line);
                    count++;
                }

            } catch (IOException e) {
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

            return lines;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            mBigText.setLines(result);
            mProgress.setVisibility(View.GONE);
            mList.post(new Runnable() {
                public void run() {
                    mList.setSelection(0);
                    mList.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            mAdapter.notifyDataSetChanged();
                            mList.getViewTreeObserver().removeOnScrollChangedListener(this);
                        }
                    });
                }
            });

        }

    }

    private class LoadFileTask extends AsyncTask<Void, Void, Void> {

        private ArrayList<String> mLines;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mLines = new ArrayList<String>();

            if (mBigFile) {
                loadFragmentsFileStrings();
            } else {
                loadFileStrings();
            }

            return null;
        }

        private void loadFragmentsFileStrings() {
            BufferedReader is = null;
            try {
                is = new BufferedReader(new InputStreamReader(new FileInputStream(mFile), mSelectedCharset.name()));
                String line;

                int count = 0;
                while (!mStopLoading && count < LINES_COUNT_FRAGMENT && (line = is.readLine()) != null) {
                    mLines.add(line);
                    count++;
                }

            } catch (Exception e) {
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
        }

        private void loadFileStrings() {
            BufferedReader is = null;
            try {
                if(mFile.canRead()){
                    is = new BufferedReader(new InputStreamReader(new FileInputStream(mFile), mSelectedCharset.name()));
                }else{
                    is = new BufferedReader(RootTask.readFile(mFile));
                }
                String line;
                while (!mStopLoading && (line = is.readLine()) != null) {
                    mLines.add(line);
                }

            } catch (IOException e) {
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
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mProgress.setVisibility(View.GONE);
            mAdapter.swapData(mLines);
        }
    }

    private void updateAdapter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void showSelectEncodingDialog() {
        mCharsetSelectDialog = new SelectEncodingDialog(getActivity(), mHandler, mFile);
        mCharsetSelectDialog.show();
        adjustDialogSize(mCharsetSelectDialog);
    }

    /**
     * Adjust dialog size. Actuall for old android version only (due to absence of Holo themes).
     *
     * @param dialog dialog whose size should be adjusted.
     */
    private void adjustDialogSize(Dialog dialog) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = (int) (metrics.widthPixels * 0.65);
        params.height = (int) (metrics.heightPixels * 0.55);

        dialog.getWindow().setAttributes(params);
    }

    private void updateUi(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

    private Observable<Boolean> mSaveObservable = Observable.create(new Observable.OnSubscribe<Boolean>() {
        @Override
        public void call(Subscriber<? super Boolean> subscriber) {
            try {
                mText.save(mFile);
            } catch (Exception e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        }
    }).subscribeOn(Schedulers.from(getThreadPool()));

}
