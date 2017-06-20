package com.openfarmanager.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.BookmarkAdapter;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.model.Bookmark;

import java.util.List;

/**
 * author: Vlad Namashko
 */
public class BookmarksListDialog extends Dialog {

    private Handler mHandler;
    private View mDialogView;
    private TextView mLabel;
    private ListView mBookmarks;

    public BookmarksListDialog(Context context, Handler handler) {
        super(context, R.style.Action_Dialog);
        mHandler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_bookmarks_list, null);
        mLabel = (TextView) mDialogView.findViewById(R.id.text);
        mBookmarks = (ListView) mDialogView.findViewById(R.id.bookmarks_list);

        initView();
        setContentView(mDialogView);
    }

    private void initView() {
        List<Bookmark> bookmarkList = App.sInstance.getBookmarkManager().getBookmarks();

        boolean hasBookmarks = bookmarkList.size() > 0;
        mBookmarks.setVisibility(hasBookmarks ? View.VISIBLE : View.GONE);
        mLabel.setVisibility(hasBookmarks ? View.GONE : View.VISIBLE);

        if (hasBookmarks) {
            BookmarkAdapter adapter = new BookmarkAdapter(bookmarkList, mOnClickListener);
            mBookmarks.setAdapter(adapter);
        }
    }

    private BookmarkAdapter.OnClickListener mOnClickListener = new BookmarkAdapter.OnClickListener() {
        @Override
        public void onBookmarkClicked(Bookmark bookmark) {
            if (bookmark.isNetworkLink()) {
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.OPEN_NETWORK, bookmark));
            } else {
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.OPEN_PATH, bookmark.getBookmarkPath()));
            }

            dismiss();
        }

        @Override
        public void onDeleteClicked(Bookmark bookmark) {
            App.sInstance.getBookmarkManager().deleteBookmark(bookmark);
            initView();
        }
    };

}
