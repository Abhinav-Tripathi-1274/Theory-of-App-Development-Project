

package com.ap.sutra;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.ap.sutra.databinding.ActivityMainBinding;
import com.ap.sutra.ml.StudentBrain;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private StatusViewModel viewModel;
    private static final String CHANNEL_ID = "STUDY_ALERTS";

    private Handler automationHandler = new Handler();
    private Runnable automationRunnable;

    // --- TIME TRACKING ---
    private long instaStartTime = 0; // Remembers when you opened Instagram

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(StatusViewModel.class);
        createNotificationChannel();

        if (!hasUsageStatsPermission()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        startAutoCheck();
    }

    private void startAutoCheck() {
        automationRunnable = new Runnable() {
            @Override
            public void run() {
                // Check usage every 3 seconds for better precision
                checkUsageEvents();
                automationHandler.postDelayed(this, 3000);
            }
        };
        automationHandler.post(automationRunnable);
    }

    private void checkUsageEvents() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();

        // We look at events from the last 30 seconds
        UsageEvents events = usm.queryEvents(currentTime - 30000, currentTime);
        UsageEvents.Event event = new UsageEvents.Event();

        boolean isInstagramForeground = false;

        while (events.hasNextEvent()) {
            events.getNextEvent(event);
            if (event.getPackageName().equals("com.instagram.android")) {
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    isInstagramForeground = true;
                    // Start timer if not already running
                    if (instaStartTime == 0) instaStartTime = event.getTimeStamp();
                } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    isInstagramForeground = false;
                    instaStartTime = 0; // Reset timer immediately if app is closed
                }
            }
        }

        if (isInstagramForeground && instaStartTime != 0) {
            long durationSpent = currentTime - instaStartTime;
            long seconds = durationSpent / 1000;

            // Only trigger AI/Notification after 20 seconds
            if (seconds >= 20) {
                runAiAnalysis(60, seconds);
            } else {
                viewModel.setStatus("Status: MONITORING (" + seconds + "s)");
            }
        } else {
            instaStartTime = 0;
            viewModel.setStatus("Status: FOCUSED ✅");
        }
    }

    private void runAiAnalysis(long minutes, long seconds) {
        try {
            StudentBrain model = StudentBrain.newInstance(this);
            float[] input = new float[]{(float) minutes, 4.0f, 2500.0f, 60.0f, 400.0f, 21.0f, 1.0f, 1.0f, 1.0f};
            TensorBuffer buffer = TensorBuffer.createFixedSize(new int[]{1, 9}, DataType.FLOAT32);
            buffer.loadArray(input);

            StudentBrain.Outputs outputs = model.process(buffer);
            float[] results = outputs.getOutputFeature0AsTensorBuffer().getFloatArray();

            int prediction = 0;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > results[prediction]) prediction = i;
            }

            if (prediction >= 4) {
                viewModel.setStatus("Status: DISTRACTED ❌ (" + seconds + "s)");
                sendProgressiveNotification("GET OFF INSTAGRAM!", "You've been scrolling for " + seconds + " seconds!", seconds);
            }
            model.close();
        } catch (IOException e) {
            Log.e("Sutra_AI", "Error: " + e.getMessage());
        }
    }

    private void sendProgressiveNotification(String title, String text, long seconds) {
        long[] vibrationPattern;

        // PROGRESSIVE VIBRATION: Patterns get longer/annoying over time
        if (seconds < 40) {
            vibrationPattern = new long[]{0, 200, 100, 200}; // Short nudge
        } else if (seconds < 60) {
            vibrationPattern = new long[]{0, 500, 200, 500, 200, 500}; // Medium pulses
        } else {
            vibrationPattern = new long[]{0, 1000, 500, 1000, 500, 1000}; // Long heavy pulses
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(vibrationPattern) // Set custom pattern
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (automationHandler != null) automationHandler.removeCallbacks(automationRunnable);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Study Alerts", NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}