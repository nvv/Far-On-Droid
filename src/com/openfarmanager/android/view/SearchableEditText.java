package com.openfarmanager.android.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.widget.EditText;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.LinesAdapter;
import com.openfarmanager.android.utils.FileUtilsExt;
import org.apache.commons.io.IOCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchableEditText extends EditText implements SearchableView {

    private String mText;

    public SearchableEditText(Context context, String text) {
        super(context);
        mText = text;
        setBackgroundResource(R.color.transparent);
        setTextColor(Color.CYAN);
        setPadding(0, 0, 0, 0);
        setTextSize(App.sInstance.getSettings().getViewerFontSize());
        setTypeface(App.sInstance.getSettings().getViewerFontType());
        //setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    @Override
    public void setText() {
        setText(mText);
    }

    @Override
    public void setupText(String newText) {
        mText = newText;
    }

    @Override
    public String getViewText() {
        return null;
    }

    @Override
    public void search(String pattern, IOCase caseSensitive, boolean wholeWords) {
        generalSearch(pattern, caseSensitive, wholeWords, false);
    }

    @Override
    public int getMode() {
        return LinesAdapter.MODE_EDIT;
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
