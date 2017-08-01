package com.openfarmanager.android.core.network;

import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkAccount;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public interface NetworkApi {

    int getAuthorizedAccountsCount();

    List<NetworkAccount> getAuthorizedAccounts();

    NetworkAccount newAccount();

    NetworkAccount getCurrentNetworkAccount();

    void delete(FileProxy file) throws Exception;

    String createDirectory(String baseDirectory, String newDirectoryName) throws Exception;

    Observable<FileProxy> search(String path, String query);

    boolean rename(FileProxy srcFile, String s) throws Exception;
}
