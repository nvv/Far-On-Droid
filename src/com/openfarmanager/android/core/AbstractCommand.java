package com.openfarmanager.android.core;

import java.io.Serializable;

/**
 * Abstract single command holder.
 */
public interface AbstractCommand extends Serializable {

    /**
     * Command code with list of arbitrary arguments.
     *
     * @param args command arguments.
     */
    void execute(Object ... args);

}
