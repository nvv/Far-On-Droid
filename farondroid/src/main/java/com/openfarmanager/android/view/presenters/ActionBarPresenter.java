package com.openfarmanager.android.view.presenters;

/**
 * @author Vlad Namashko
 */
public interface ActionBarPresenter {

    void changePath();

    void addBookmark();

    void openNetwork();

    void gotoHome();

    void openDirectory(String fullPath);
}
