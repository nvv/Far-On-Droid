package com.openfarmanager.android.model.exeptions;

/**
 * @author Vlad Namashko
 */
public class FileIsNotDirectoryException extends RuntimeException {

    public FileIsNotDirectoryException(String detailMessage) {
        super(detailMessage);
    }

}
