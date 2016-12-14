package com.openfarmanager.android.model;

import java.io.File;

public interface OpenDirectoryActionListener {

    void onDirectoryOpened(File directory, Integer selection);

    void onError();
}
