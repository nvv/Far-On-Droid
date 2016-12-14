package com.openfarmanager.android.utils;

import java.io.IOException;


/**
 * @author Vlad Namashko.
 */
public class NetworkUtils {

    public static boolean ping(String host) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "1", host);
        Process proc = processBuilder.start();

        int returnVal = proc.waitFor();
        return returnVal == 0;
    }


}
