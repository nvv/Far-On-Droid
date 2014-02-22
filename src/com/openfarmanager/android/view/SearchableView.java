package com.openfarmanager.android.view;

import org.apache.commons.io.IOCase;

public interface SearchableView {

    void setText();

    void setupText(String newText);

    String getViewText();

    void search(String pattern, IOCase caseSensitive, boolean wholeWords);

    int getMode();
}
