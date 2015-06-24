package com.mediafire.sdk.log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chris on 5/26/2015.
 */
public class DefaultApiTransactionStore implements MFLogStore<ApiTransaction> {
    public final LinkedList<ApiTransaction> apiTransactions = new LinkedList<ApiTransaction>();
    private final Object lock = new Object();

    @Override
    public long deleteAll() {
        synchronized (lock) {
            int size = apiTransactions.size();
            apiTransactions.clear();
            return size;
        }
    }

    @Override
    public long getCount() {
        synchronized (lock) {
            return apiTransactions.size();
        }
    }

    @Override
    public long addLog(ApiTransaction apiTransaction) {
        synchronized (lock) {
            boolean added = apiTransactions.add(apiTransaction);

            if (added) {
                return apiTransactions.size();
            } else {
                return -1;
            }
        }
    }

    public void addLogs(List<ApiTransaction> apiTransactions) {
        synchronized (lock) {
            apiTransactions.addAll(apiTransactions);
        }
    }

    public LinkedList<ApiTransaction> getAll() {
        synchronized (lock) {
            return apiTransactions;
        }
    }
}
