package com.openfarmanager.android.model;

import com.openfarmanager.android.fragments.Viewer;

import java.util.ArrayList;

public class ViewerBigFileTextViewer implements TextBuffer {

    private ArrayList<String> mLines;

    private int mCurrentFragment;

    private int mSize = 0;

    private boolean mHasUpperFragment;
    private boolean mHasBottomFragment;

    @Override
    public String getLine(int lineNumber) {

        if (lineNumber == 0 && mHasUpperFragment) {
            return "...";
        } else if (lineNumber == mSize - 1 && mHasBottomFragment) {
            return "...";
        }

        if (mHasUpperFragment) {
            lineNumber--;
        }

        return mLines.get(lineNumber);
    }

    @Override
    public ArrayList<String> getTextLines() {
        return mLines;
    }

    @Override
    public int size() {
        return mSize;
    }

    @Override
    public void setLine(int lineNumber, String text) {
    }

    @Override
    public void appendEmptyLine() {
    }

    @Override
    public void swapData(ArrayList<String> strings) {
        // swap data is called as initial step, so upper lines will zero-fragment, current fragment is first.
        mCurrentFragment = 0;
        setLines(strings);
    }

    public void setLines(ArrayList<String> lines) {
        mLines = lines;
        mSize = mLines.size();

        mHasUpperFragment = mCurrentFragment > 0;
        mHasBottomFragment = mLines.size() == Viewer.LINES_COUNT_FRAGMENT;

        if (mHasUpperFragment) {
            mSize++;
        }

        if (mHasBottomFragment) {
            mSize++;
        }
    }

    /**
     * Get next fragment and increment internal state of current fragment.
     *
     * @return incremented fragment.
     */
    public int nextFragment() {
        return ++mCurrentFragment;
    }

    /**
     * Get previous fragment and decrement internal state of current fragment.
     *
     * @return decremented fragment
     */
    public int previousFragment() {
        return --mCurrentFragment;
    }

    /**
     * Check if current fragment has upper parent, i.e. current fragment is not first in file.
     *
     * @return <code>true</code> if current fragment has upper fragment, <code>false</code> otherwise.
     */
    public boolean hasUpperFragment() {
        return mHasUpperFragment;
    }

    /**
     * Check if current fragment has bottom fragmnet, i.e. current fragment is not last in file.
     *
     * @return <code>true</code> if current fragment has bottom fragment, <code>false</code> otherwise.
     */
    public boolean hasBottomFragment() {
        return mHasBottomFragment;
    }

}
