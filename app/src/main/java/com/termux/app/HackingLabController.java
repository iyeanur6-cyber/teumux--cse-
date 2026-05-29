package com.termux.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import java.io.File;

public class HackingLabController {

    private final Context context;
    private final WindowManager windowManager;
    private View popupView;

    public HackingLabController(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showFloatingWindow() {
        if (popupView != null) return;

        int layoutId = context.getResources().getIdentifier("hacking_lab_popup", "layout", context.getPackageName());
        if (layoutId == 0) return;

        popupView = LayoutInflater.from(context).inflate(layoutId, null);

        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.END;
        params.x = 10;
        params.y = 100;

        windowManager.addView(popupView, params);
        setupComponents();
    }

    private void setupComponents() {
        String resPackage = context.getPackageName();
        int vpnId = context.getResources().getIdentifier("switch_vpn", "id", resPackage);
        int torId = context.getResources().getIdentifier("switch_tor", "id", resPackage);
        int obfs4Id = context.getResources().getIdentifier("switch_obfs4", "id", resPackage);
        int killWipeId = context.getResources().getIdentifier("btn_kill_wipe", "id", resPackage);

        Switch switchVpn = popupView.findViewById(vpnId);
        Switch switchTor = popupView.findViewById(torId);
        Switch switchObfs4 = popupView.findViewById(obfs4Id);
        Button btnKillWipe = popupView.findViewById(killWipeId);

        if (switchVpn != null) {
            switchVpn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Intent vpnIntent = new Intent(context, YIVpnService.class);
                    if (isChecked) {
                        context.startService(vpnIntent);
                        Toast.makeText(context, "YI VPN & Capture Engine Active", Toast.LENGTH_SHORT).show();
                    } else {
                        context.stopService(vpnIntent);
                        Toast.makeText(context, "VPN Engine Stopped", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (switchTor != null) {
            switchTor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        executeTermuxCommand("start-tor");
                        Toast.makeText(context, "Routing Traffic via Tor Network", Toast.LENGTH_SHORT).show();
                    } else {
                        executeTermuxCommand("pkill -f tor");
                        Toast.makeText(context, "Tor Disconnected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (switchObfs4 != null) {
            switchObfs4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(context, "Obfs4 Tunnelling Enabled", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Obfs4 Disabled", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (btnKillWipe != null) {
            btnKillWipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeKillAndWipe();
                }
            });
        }
    }

    private void executeTermuxCommand(String command) {
        try {
            Runtime.getRuntime().exec(new String[]{"/data/data/com.termux/files/usr/bin/bash", "-c", command});
        } catch (Exception e) {
            Log.e("HackingLabCtrl", "Command Execution Failed: " + e.getMessage());
        }
    }

    private void executeKillAndWipe() {
        Toast.makeText(context, "EMERGENCY ACTIVATED: Wiping Lab...", Toast.LENGTH_LONG).show();
        try {
            String scriptPath = "/data/data/com.termux/files/home/kill_and_wipe.sh";
            File scriptFile = new File(scriptPath);
            if (scriptFile.exists()) {
                Process process = Runtime.getRuntime().exec(new String[]{"/data/data/com.termux/files/usr/bin/bash", scriptPath});
                process.waitFor();
            } else {
                File homeDir = new File("/data/data/com.termux/files/home");
                deleteDirectory(homeDir);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            Toast.makeText(context, "Emergency Wipe Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            path.delete();
        }
    }

    public void removeFloatingWindow() {
        if (popupView != null) {
            windowManager.removeView(popupView);
            popupView = null;
        }
    }
}
