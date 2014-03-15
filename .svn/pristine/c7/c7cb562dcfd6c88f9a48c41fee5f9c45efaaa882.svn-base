package com.openfarmanager.android.view;

import android.app.Dialog;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.NetworkUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: Vlad Namashko
 */
public class NetworkScanDialog extends Dialog {

    private Handler mHandler;
    private View mDialogView;
    private EditText mIpAddress;
    private EditText mMask;
    private TextView mError;
    private Button mScanButton;
    private ProgressBar mProgressBar;
    private GridView mAvailableHosts;

    private String mMyIpAddress;
    private NetworkScanTask mNetworkScanTask;

    private ExecutorService mThreadPoolExecutor;
    private final LinkedList<String> mReachableHosts = new LinkedList<String>();

    private boolean mIsScanning;

    public NetworkScanDialog(Context context, Handler handler) {
        super(context, R.style.Action_Dialog);
        mHandler = handler;
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if (mNetworkScanTask != null) {
            mNetworkScanTask.cancel(true);
            mNetworkScanTask = null;
        }
        if (mThreadPoolExecutor != null) {
            mThreadPoolExecutor.shutdownNow();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_scan_local_network, null);
        mIpAddress = (EditText) mDialogView.findViewById(R.id.ip_address);
        mMask = (EditText) mDialogView.findViewById(R.id.ip_mask);
        mError = (TextView) mDialogView.findViewById(R.id.error);
        mScanButton = (Button) mDialogView.findViewById(R.id.scan);
        mProgressBar = (ProgressBar) mDialogView.findViewById(R.id.progress);
        mAvailableHosts = (GridView) mDialogView.findViewById(R.id.hosts);

        setContentView(mDialogView);

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mHandler.sendEmptyMessage(FileSystemController.SMB_SCAN_CANCELED);
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsScanning) {
                    mThreadPoolExecutor.shutdownNow();
                    stopScanning(0);
                } else {
                    mError.setVisibility(View.GONE);

                    String stringIp = mIpAddress.getText().toString();
                    String stringMask = mMask.getText().toString();

                    if (!NetworkUtil.isValidIp(stringIp) || !NetworkUtil.isCorrectMask(stringMask)) {
                        mError.setText(R.string.error_subnet_addresses_not_correct);
                        mError.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (mNetworkScanTask != null) {
                        mNetworkScanTask.cancel(true);
                    }

                    mNetworkScanTask = new NetworkScanTask();
                    mNetworkScanTask.execute(stringIp, stringMask);
                }
            }
        });

        mAvailableHosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                stopScanning(0);
                dismiss();

                String selectedIp = ((TextView) view).getText().toString();
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.SMB_IP_SELECTED, selectedIp));
            }
        });

        WifiManager manager = (WifiManager) App.sInstance.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = manager.getDhcpInfo();

        try {
            String stringIp = mMyIpAddress = NetworkUtil.ipIntToStringRevert(dhcpInfo.ipAddress);
            String stringMask = NetworkUtil.ipIntToStringRevert(dhcpInfo.netmask);

            mIpAddress.setText(stringIp);
            mMask.setText(stringMask);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

    }

    private void stopScanning(int errorCode) {
        mIsScanning = false;
        mIpAddress.setEnabled(true);
        mMask.setEnabled(true);
        mScanButton.setText(R.string.btn_scan);
        mProgressBar.setVisibility(View.GONE);
        if (errorCode != 0) {
            mError.setText(R.string.error_subnet_addresses_not_correct);
            mError.setVisibility(View.VISIBLE);
        }
    }

    private class NetworkScanTask extends AsyncTask<String, Void, Void> {

        private int mErrorCode;
        private int mIpsInNetwork;
        private AtomicInteger mScannedIps = new AtomicInteger();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIpAddress.setEnabled(false);
            mMask.setEnabled(false);

            mScannedIps.set(0);
            mProgressBar.setVisibility(View.VISIBLE);
            mScanButton.setText(R.string.btn_stop);

            mReachableHosts.clear();
            mIsScanning = true;
        }

        @Override
        protected Void doInBackground(String ... strings) {

            String stringIp = strings[0];
            String stringMask = strings[1];
            String[] ips;
            try {
                ips = NetworkUtil.allStringAddressesInSubnet(stringIp, stringMask);
                mIpsInNetwork = ips.length;
            } catch (Exception e) {
                mErrorCode = R.string.error_subnet_addresses_not_correct;
                e.printStackTrace();
                return null;
            }

            mThreadPoolExecutor = Executors.newFixedThreadPool(4);
            for (final String ip : ips) {
                if (ip.equals(mMyIpAddress)) {
                    continue;
                }

                mThreadPoolExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InetAddress address = InetAddress.getByName(ip);
                                if (address.isReachable(1500)) {
                                    synchronized (mReachableHosts) {
                                        mReachableHosts.add(ip);
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mAvailableHosts.getVisibility() == View.GONE) {
                                                    mAvailableHosts.setVisibility(View.VISIBLE);
                                                }

                                                mAvailableHosts.setAdapter(new ArrayAdapter<String>(App.sInstance.getApplicationContext(),
                                                        android.R.layout.simple_list_item_1, mReachableHosts.toArray(new String[mReachableHosts.size()])));
                                            }
                                        });
                                    }
                                }
                                mScannedIps.incrementAndGet();
                                NetworkScanTask.this.publishProgress();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });


            }

            mThreadPoolExecutor.shutdown();
            try {
                mThreadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopScanning(mErrorCode);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(100 * mScannedIps.get() / mIpsInNetwork);
        }
    }


}
