package com.openfarmanager.android.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.FileSystemAdapter;
import com.openfarmanager.android.model.OpenDirectoryActionListener;
import com.openfarmanager.android.view.decoration.HorizontalDividerItemDecoration;

import java.io.File;

/**
 * @author Vlad Namashko
 */
public class FileSystemListView extends FrameLayout {

    private FileSystemAdapter mAdapter;
    private FileSystemAdapter.OnItemClickListener mOnItemClickListener;

    private RecyclerView mRecyclerView;

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
        LayoutInflater.from(getContext()).inflate(R.layout.file_system_list_view, this);

        mRecyclerView = (RecyclerView) findViewById(R.id.file_list_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration(getContext()));
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void initAdapter(FileSystemAdapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);

        FastScroller fastScroller = (FastScroller) findViewById(R.id.fastscroll);
        fastScroller.setRecyclerView(mRecyclerView);
        fastScroller.setViewProvider(new CustomScrollerViewProvider());

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


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
