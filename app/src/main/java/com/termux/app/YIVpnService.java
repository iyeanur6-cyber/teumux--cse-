package com.termux.app;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class YIVpnService extends VpnService implements Runnable {

    private Thread vpnThread;
    private ParcelFileDescriptor vpnInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (vpnThread != null) {
            vpnThread.interrupt();
        }
        vpnThread = new Thread(this, "YIVpnThread");
        vpnThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (vpnThread != null) {
            vpnThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public void run() {
        try {
            // ১. ভিপিএন ইন্টারফেস কনফিগার করা
            setupVpn();

            // ২. প্যাকেট ক্যাপচারিং লুপ শুরু (Network Analysis এর জন্য)
            capturePackets();

        } catch (Exception e) {
            Log.e("YIVpn", "Error: " + e.getMessage());
        } finally {
            closeInterface();
        }
    }

    private void setupVpn() throws Exception {
        Builder builder = new Builder();
        
        // ভার্চুয়াল আইপি সেটআপ (লোকাল লুপব্যাক)
        builder.setSession("YI Hacking Lab VPN")
               .addAddress("10.0.0.2", 24)
               .addDnsServer("8.8.8.8")
               .addRoute("0.0.0.0", 0); // সব ট্রাফিক ভিপিএন দিয়ে যাবে

        vpnInterface = builder.establish();
        Log.i("YIVpn", "VPN Interface established.");
    }

    private void capturePackets() throws Exception {
        FileInputStream in = new FileInputStream(vpnInterface.getFileDescriptor());
        FileOutputStream out = new FileOutputStream(vpnInterface.getFileDescriptor());
        ByteBuffer buffer = ByteBuffer.allocate(32767);

        while (!Thread.interrupted()) {
    int length = in.read(buffer.array());
    if (length > 0) {
        
        // নেটওয়ার্ক অ্যানালাইজার ক্লাস কল করা হলো
        YINetworkAnalyzer.analyzePacket(buffer, length);
        
        out.write(buffer.array(), 0, length);
        buffer.clear();
    }
    Thread.sleep(10);
        }
        
    }

    private void closeInterface() {
        try {
            if (vpnInterface != null) {
                vpnInterface.close();
                vpnInterface = null;
            }
        } catch (Exception e) {
            Log.e("YIVpn", "Close failed: " + e.getMessage());
        }
    }
}
