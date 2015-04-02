package com.openfarmanager.android.model;

import java.util.ArrayList;

/**
 * Holder for any type of <code>Viewer's</code> text.
 */
public interface TextBuffer {

    /**
     * Get current line.
     *
     * @param lineNumber number of line
     * @return line with number <code>lineNumber</code>
     */
    String getLine(int lineNumber);

    /**
     * Get all text lines (including changes).
     *
     * @return all text.
     */
    ArrayList<String> getTextLines();

    /**
     * Total buffer size (count of all lines).
     *
     * @return total count of all lines.
     */
    int size();

    /**
     * Set <code>text</code> on <code>lineNumber</code> position.
     *
     * @param lineNumber position for text.
     * @param text new text value for <code>lineNumber</code> position.
     */
    void setLine(int lineNumber, String text);

    /**
     * Append empty line to the end of file. Essential for empty files.
     */
    void appendEmptyLine();

    /**
     * Setup new data for buffer.
     *
     * @param strings new data.
     */
    void swapData(ArrayList<String> strings);
}
