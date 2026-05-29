package com.termux.app;

import android.util.Log;
import java.nio.ByteBuffer;

public class YINetworkAnalyzer {

    private static final String TAG = "YINetworkAnalyzer";

    public static void analyzePacket(ByteBuffer buffer, int length) {
        if (length < 20) return; // মিনিমাম আইপি হেডার সাইজ ২০ বাইট হতে হবে

        int bufferOffset = buffer.position();

        // ১. আইপি ভার্সন চেক করা (IPv4)
        byte versionAndIHL = buffer.get(bufferOffset);
        int version = (versionAndIHL >> 4) & 0x0F;

        if (version == 4) {
            // ২. প্রোটোকল টাইপ বের করা (TCP = 6, UDP = 17, ICMP = 1)
            int protocol = buffer.get(bufferOffset + 9) & 0xFF;

            // ৩. সোর্স এবং ডেস্টিনেশন আইপি অ্যাড্রেস বের করা
            String sourceIP = getIPAddress(buffer, bufferOffset + 12);
            String destIP = getIPAddress(buffer, bufferOffset + 16);

            String protocolName = "UNKNOWN";
            if (protocol == 6) protocolName = "TCP";
            else if (protocol == 17) protocolName = "UDP";
            else if (protocol == 1) protocolName = "ICMP";

            // ৪. টার্মিনাল বা লগে ডেটা পাঠানো
            String logMessage = String.format("[YI-LAB] %s | SRC: %s -> DST: %s | Size: %d bytes", 
                    protocolName, sourceIP, destIP, length);
            
            Log.d(TAG, logMessage);
            
            // এখানে তুমি টার্মাক্স টার্মিনাল বা UI-তে ডেটা পুশ করার মেথড কল করতে পারো
            // sendToTerminal(logMessage);
        }
    }

    private static String getIPAddress(ByteBuffer buffer, int offset) {
        return (buffer.get(offset) & 0xFF) + "." +
               (buffer.get(offset + 1) & 0xFF) + "." +
               (buffer.get(offset + 2) & 0xFF) + "." +
               (buffer.get(offset + 3) & 0xFF);
    }
}
