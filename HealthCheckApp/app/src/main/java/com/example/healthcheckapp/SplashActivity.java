package com.example.healthcheckapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "health_check_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Create fade-in animation for the logo
        ImageView logo = findViewById(R.id.logo);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.startAnimation(fadeIn);

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                showNotification(); // If permission is granted, show notification
            }
        } else {
            // No need to request permission for Android versions below 13
            showNotification();
        }

        // After 3 seconds, navigate to the main page (TopScoresActivity)
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, TopScoresActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 3000); // Delay of 3 seconds
    }

    private void showNotification() {
        // Get the device's default language
        String language = Locale.getDefault().getLanguage();
        String title;
        String message;

        // Set the title and message based on the language
        if (language.equals("fa")) {
            title = "خوش آمدید!"; // Persian message
            message = "از اینکه از اپلیکیشن ما استفاده می‌کنید، متشکریم.";
        } else {
            title = "Welcome!"; // English message
            message = "Thank you for using our app.";
        }

        // Create a notification channel for Android 8 (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Health Check Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notification for Health Check App");

            // Register the notification channel with the system
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Create the notification with a small icon, title, and message
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Icon for the notification
                .setContentTitle(title) // Title of the notification
                .setContentText(message) // Message of the notification
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for the notification
                .setAutoCancel(true); // Automatically remove the notification when clicked

        // Display the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        } else {
            // Show a Toast if notification fails to send
            Toast.makeText(this, "خطا در ارسال نوتیفیکیشن", Toast.LENGTH_SHORT).show();
        }
    }
}
