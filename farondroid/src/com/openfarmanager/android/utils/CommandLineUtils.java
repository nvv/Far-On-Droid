package com.openfarmanager.android.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Vlad Namashko.
 */
public class CommandLineUtils {

    public final static int BUF_LEN = 64 * 1024;

    public static Observable<String> excecuteReadCommand(final String command) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(command), BUF_LEN);
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        subscriber.onNext(line);
                    }

                } catch (IOException e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                    try {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    public static Observable<CommandLineCommandOutput> excecuteCommand(final String ... command) {
        return Observable.create(new Observable.OnSubscribe<CommandLineCommandOutput>() {
            @Override
            public void call(Subscriber<? super CommandLineCommandOutput> subscriber) {
                try {
                    CommandLineCommandOutput commandOutput = new CommandLineCommandOutput();
                    commandOutput.args = command;
                    subscriber.onNext(commandOutput);

                    ProcessBuilder processBuilder = new ProcessBuilder(commandOutput.args);
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    commandOutput.process = process;

                    while ((line = reader.readLine()) != null) {
                        commandOutput.outputLine = line;
                        commandOutput.outputNum++;
                        subscriber.onNext(commandOutput);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
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
