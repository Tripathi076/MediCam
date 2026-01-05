package com.example.medicam;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class MedicationReminderReceiver extends BroadcastReceiver {
    
    private static final String CHANNEL_ID = "medication_reminder_channel";
    private static final String CHANNEL_NAME = "Medication Reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicationName = intent.getStringExtra("medication_name");
        String medicationDosage = intent.getStringExtra("medication_dosage");
        String medicationId = intent.getStringExtra("medication_id");

        if (medicationName == null) medicationName = "Medication";
        if (medicationDosage == null) medicationDosage = "";

        createNotificationChannel(context);
        showNotification(context, medicationName, medicationDosage, medicationId);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for taking medication");
            channel.enableVibration(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(Context context, String medicationName, String dosage, String medicationId) {
        Intent intent = new Intent(context, MedicationReminderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_medication)
                .setContentTitle("💊 Time for " + medicationName)
                .setContentText("Take " + dosage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_check_circle, "Taken", pendingIntent)
                .addAction(R.drawable.ic_close, "Skip", pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            int notificationId = medicationId != null ? medicationId.hashCode() : (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
