package com.openfarmanager.android.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.reactivex.Observable;


/**
 * @author Vlad Namashko.
 */
public class CommandLineUtils {

    public final static int BUF_LEN = 64 * 1024;

    public static Observable<String> executeReadCommand(final String command) {
        return Observable.create(emitter -> {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(command), BUF_LEN);
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    emitter.onNext(line);
                }

            } catch (IOException e) {
                emitter.onError(e);
            } finally {
                emitter.onComplete();
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public static Observable<CommandLineCommandOutput> excecuteCommand(final String... command) {
        return Observable.create(emitter -> {
            try {
                CommandLineCommandOutput commandOutput = new CommandLineCommandOutput();
                commandOutput.args = command;
                emitter.onNext(commandOutput);

                ProcessBuilder processBuilder = new ProcessBuilder(commandOutput.args);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                commandOutput.process = process;

                while ((line = reader.readLine()) != null) {
                    commandOutput.outputLine = line;
                    commandOutput.outputNum++;
                    emitter.onNext(commandOutput);
                }
            } catch (Exception e) {
                emitter.onError(e);
            }

        });
    }

    public static class CommandLineCommandOutput {
        public String[] args;
        public String outputLine;
        public int outputNum;
        public Process process;
    }

}
