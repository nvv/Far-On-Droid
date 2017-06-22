package com.openfarmanager.android.utils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


/**
 * @author Vlad Namashko.
 */
public class HardwareUtils {

    public static final String TAG = "HardwareUtils";

    public static final String NOMAC = "00:00:00:00:00:00";
    public final static String MAC_REGEX = "^0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
    public final static String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";

    public static HashMap<String, String> getHardwareAddresses(final HashMap<String, String> addresses) {
        final Pattern pattern = Pattern.compile(MAC_REGEX);

        CommandLineUtils.executeReadCommand("/proc/net/arp").subscribe(new DisposableObserver<String>() {

            @Override
            public void onNext(String line) {
                int ipLen = line.indexOf(' ');
                String ip = line.substring(0, ipLen);
                String mac = NOMAC;

                if (!ip.matches(NetworkCalculator.ipPattern)) {
                    return;
                }

                String macLine = line.substring(ipLen).trim();
                Matcher matcher = pattern.matcher(macLine);
                if (matcher.matches()) {
                    mac = matcher.group(1);
                }

                if (!mac.equals(NOMAC)) {
                    addresses.put(ip, mac);
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

        return addresses;
    }

    public static String getHardwareAddress(String ip) {
        String hw = NOMAC;
        BufferedReader bufferedReader = null;
        try {
            if (ip != null) {
                String ptrn = String.format(MAC_RE, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);
                bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), CommandLineUtils.BUF_LEN);
                String line;
                Matcher matcher;
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        hw = matcher.group(1);
                        break;
                    }
                }
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
            return hw;
        } finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hw;
    }

}
