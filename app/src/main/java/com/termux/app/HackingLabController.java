package com.termux.app;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
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

        popupView = LayoutInflater.from(context).inflate(R.layout.hacking_lab_popup, null);

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
        Switch switchVpn = popupView.findViewById(R.id.switch_vpn);
        Switch switchTor = popupView.findViewById(R.id.switch_tor);
        Switch switchObfs4 = popupView.findViewById(R.id.switch_obfs4);
        Button btnKillWipe = popupView.findViewById(R.id.btn_kill_wipe);

        switchVpn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, "Custom VPN Engine Starting...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "VPN Stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchTor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, "Routing via Tor SOCKS5", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Tor Disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchObfs4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, "Obfs4 Tunnelling Active", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Obfs4 Deactivated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnKillWipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeKillAndWipe();
            }
        });
    }

    private void executeKillAndWipe() {
        Toast.makeText(context, "EMERGENCY: Wiping Termux Lab...", Toast.LENGTH_LONG).show();
        
        try {
            String termuxFilesPath = "/data/data/com.termux/files";
            File homeDir = new File(termuxFilesPath + "/home");
            File usrDir = new File(termuxFilesPath + "/usr");

            deleteDirectory(homeDir);
            
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        } catch (Exception e) {
            Toast.makeText(context, "Wipe Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
