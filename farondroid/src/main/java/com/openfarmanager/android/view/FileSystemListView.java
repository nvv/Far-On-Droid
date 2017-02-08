package com.openfarmanager.android.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.AdapterView;

import com.openfarmanager.android.adapters.FileSystemAdapter;
import com.openfarmanager.android.model.OpenDirectoryActionListener;
import com.openfarmanager.android.view.decoration.HorizontalDividerItemDecoration;

import java.io.File;

/**
 * @author Vlad Namashko
 */
public class FileSystemListView extends RecyclerView {

    private FileSystemAdapter mAdapter;
    private FileSystemAdapter.OnItemClickListener mOnItemClickListener;

    public FileSystemListView(Context context) {
        super(context);
        init();
    }

    public FileSystemListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FileSystemListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new HorizontalDividerItemDecoration(getContext()));
    }

    public void initAdapter(FileSystemAdapter adapter) {
        mAdapter = adapter;
        setAdapter(mAdapter);

        if (mOnItemClickListener != null) {
            mAdapter.setOnItemClickListener(mOnItemClickListener);
        }
    }

    public void setOnItemClickListener(FileSystemAdapter.OnItemClickListener onItemClickListener) {
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(onItemClickListener);
        }
        mOnItemClickListener = onItemClickListener;
    }


}
