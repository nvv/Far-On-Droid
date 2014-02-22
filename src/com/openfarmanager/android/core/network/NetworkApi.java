package com.openfarmanager.android.core.network;

import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkAccount;

import java.util.List;

public interface NetworkApi {

    int getAuthorizedAccountsCount();

    List<NetworkAccount> getAuthorizedAccounts();

    NetworkAccount newAccount();

    NetworkAccount getCurrentNetworkAccount();

    void delete(FileProxy file) throws Exception;

    boolean createDirectory(String path) throws Exception;

    List<FileProxy> search(String path, String query);

    boolean rename(String fullPath, String s) throws Exception;
}
