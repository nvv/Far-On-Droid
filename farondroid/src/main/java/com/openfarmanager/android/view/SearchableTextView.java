package com.openfarmanager.android.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.adapters.LinesAdapter;
import com.openfarmanager.android.utils.FileUtilsExt;
import org.apache.commons.io.IOCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: vnamashko
 */
public class SearchableTextView extends TextView implements SearchableView {

    public static final int SELECTION_MODE_BACKGROUND = 0;

    private int selectionMode = SELECTION_MODE_BACKGROUND;
    private String mText;

    public SearchableTextView(Context context, String text) {
        super(context);
        setTextColor(Color.CYAN);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, App.sInstance.getSettings().getViewerFontSize());
        setTypeface(App.sInstance.getSettings().getViewerFontType());
        setupText(text);
    }

    public void setText() {
        setText(mText);
    }

    @Override
    public void setupText(String newText) {
        mText = newText;

        // TODO : in some reason TextView with empty content ignored in list view and empty line is hidden, so give non empty string.
        if (mText.equals("")) {
            mText = " ";
        }
    }

    public String getViewText() {
        return mText;
    }

    public void search(String pattern, IOCase caseSensitive, boolean wholeWords) {
        generalSearch(pattern, caseSensitive, wholeWords, false);
    }

    @Override
    public int getMode() {
        return LinesAdapter.MODE_VIEW;
    }

    private void generalSearch(String pattern, IOCase caseSensitive, boolean wholeWords, boolean replace) {
        if (pattern == null) {
            setText(mText);
            return;
        }

        Pattern patternMatch = FileUtilsExt.createWordSearchPattern(pattern, wholeWords, caseSensitive);

        Matcher matcher = patternMatch.matcher(mText);
        if (matcher.find()) {
            SpannableString string = new SpannableString(mText);

            int firstOccurrence;
            do {
                firstOccurrence = matcher.start();
                setSelection(pattern, string, firstOccurrence);
            } while (matcher.find());
            setText(string);
        } else {
            setText(mText);
        }
    }

    private void setSelection(String pattern, SpannableString string, int firstOccurrence) {
        string.setSpan(new BackgroundColorSpan( Color.YELLOW), firstOccurrence,
                firstOccurrence + pattern.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }
}
