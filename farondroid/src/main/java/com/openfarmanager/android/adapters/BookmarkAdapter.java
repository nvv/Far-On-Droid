package com.openfarmanager.android.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.model.Bookmark;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class BookmarkAdapter extends BaseAdapter {

    private List<Bookmark> mBookmarks = new ArrayList<Bookmark>();
    private OnClickListener mOnClickListener;

    public BookmarkAdapter(List<Bookmark> bookmarks, OnClickListener listener) {
        mBookmarks = bookmarks;
        mOnClickListener = listener;
    }

    @Override
    public int getCount() {
        return mBookmarks.size();
    }

    @Override
    public Object getItem(int i) {
        return mBookmarks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = View.inflate(App.sInstance.getApplicationContext(), R.layout.network_account_item, null);
        }

        Bookmark bookmark = mBookmarks.get(i);

        convertView.setTag(bookmark);
        ((TextView) convertView.findViewById(R.id.text)).setText(bookmark.getBookmarkLabel());

        View delete = convertView.findViewById(R.id.delete);
        delete.setTag(bookmark);

        delete.setOnClickListener(mOnDeleteClickListener);
        convertView.setOnClickListener(mOnViewClickListener);

        return convertView;
    }

    private View.OnClickListener mOnViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mOnClickListener.onBookmarkClicked((Bookmark) view.getTag());
        }
    };

    private View.OnClickListener mOnDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mOnClickListener.onDeleteClicked((Bookmark) view.getTag());
        }
    };

    public static interface OnClickListener {

        void onBookmarkClicked(Bookmark bookmark);

        void onDeleteClicked(Bookmark bookmark);

    }
}
