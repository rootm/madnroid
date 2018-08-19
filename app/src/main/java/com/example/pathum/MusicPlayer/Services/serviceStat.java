package com.example.pathum.MusicPlayer.Services;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.Toast;

import com.example.pathum.MusicPlayer.MusicService;

/**
 * Created by Muvindu on 1/1/2017.
 */

public class serviceStat {
    public boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                Toast.makeText(context.getApplicationContext(), "Service running", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        Toast.makeText(context.getApplicationContext(), "Service not", Toast.LENGTH_LONG).show();
        return false;
    }
}
