package com.openfarmanager.android.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.openfarmanager.android.App;
import com.openfarmanager.android.model.TextBuffer;
import com.openfarmanager.android.view.SearchableEditText;
import com.openfarmanager.android.view.SearchableTextView;
import com.openfarmanager.android.view.SearchableView;
import org.apache.commons.io.IOCase;

import java.util.ArrayList;

public class LinesAdapter extends BaseAdapter {

    public static final int MODE_VIEW = 0;
    public static final int MODE_EDIT = 1;

    private TextBuffer mText;

    private String mSearchPattern;
    private IOCase mCaseSensitive;
    private boolean mWholeWords;
    private boolean mRegularExpression;

    private boolean mDoSearch;

    private int mAdapterMode;

    public LinesAdapter(TextBuffer text) {
        mText = text;
        mAdapterMode = MODE_VIEW;
    }

    public void search(String pattern, boolean caseSensitive, boolean wholeWords, boolean regularExpression) {
        initSearchParams(pattern, caseSensitive, wholeWords, regularExpression);
        mDoSearch = true;
        notifyDataSetChanged();
    }

    private void initSearchParams(String pattern, boolean caseSensitive, boolean wholeWords, boolean regularExpression) {
        mSearchPattern = pattern;
        mCaseSensitive = caseSensitive ? IOCase.SENSITIVE : IOCase.INSENSITIVE;
        mWholeWords = wholeWords;
        mRegularExpression = regularExpression;
    }

    public void setMode(int mode) {
        mAdapterMode = mode;
        if (mAdapterMode == MODE_EDIT && mText.size() == 0) {
            mText.appendEmptyLine();
        }
    }

    public int getMode() {
        return mAdapterMode;
    }

    @Override
    public int getCount() {
        return mText.size();
    }

    @Override
    public Object getItem(int i) {
        return mText.getLine(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int lineNumber, View view, ViewGroup viewGroup) {
        String text = (String) getItem(lineNumber);

        if (view == null || ((SearchableView) view).getMode() != mAdapterMode) {
            view = newInstance(text, lineNumber);
        } else {
            ((SearchableView) view).setupText(text);
        }

        SearchableView textView = (SearchableView) view;

        if (mDoSearch) {
            textView.search(mSearchPattern, mCaseSensitive, mWholeWords);
        } else {
            textView.setText();
        }

        return (View) textView;
    }

    public void swapData(ArrayList<String> strings) {
        mText.swapData(strings);
        notifyDataSetChanged();
    }

    private View newInstance(final String initText, final int lineNumber) {
        if (mAdapterMode == MODE_EDIT) {
            final SearchableEditText editText = new SearchableEditText(App.sInstance.getApplicationContext(), initText);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    mText.setLine(lineNumber, editText.getText().toString());
                }
            });
            editText.setTag(lineNumber);
            return editText;
        }

        return new SearchableTextView(App.sInstance.getApplicationContext(), initText);
    }

    /**
     * Save value from current edit line to buffer.
     *
     * @param focusedView current focued view
     */
    public void saveCurrentEditLine(View focusedView) {
        if (focusedView instanceof SearchableEditText) {
            SearchableEditText view = (SearchableEditText) focusedView;
            mText.setLine((Integer) view.getTag(), view.getText().toString());
        }
    }
}