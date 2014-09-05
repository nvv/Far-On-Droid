package com.openfarmanager.android.core;

/**
 * Abstract command holder with additional callback for 'cancel' action.
 */
public interface CancelableCommand extends AbstractCommand {

    /**
     * cancel clicked.
     */
    void cancel();
}
