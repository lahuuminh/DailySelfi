package com.example.dailyselfi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarmForTomorrow(context);
        SharedPreferences prefs = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        boolean isPhotoTaken = prefs.getBoolean("photoTaken_" + today, false);

        if (!isPhotoTaken) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Cấu hình thông báo
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ALARM_CHANNEL")
                    .setSmallIcon(android.R.drawable.ic_dialog_info) // Biểu tượng của thông báo
                    .setContentTitle("Thông báo nhắc nhở")
                    .setContentText("Đã đến giờ nhắc nhở! Hãy chụp ảnh!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true); // Tự động xóa khi người dùng nhấn vào thông báo

            // Kiểm tra nếu phiên bản Android >= Oreo (API 26)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Alarm Notifications";
                String description = "Nhóm thông báo cho các cảnh báo";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("ALARM_CHANNEL", name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }

            // Hiển thị thông báo
            notificationManager.notify(0, builder.build());
        }
    }
    private void setAlarmForTomorrow(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();

        // Thêm 1 ngày vào thời gian hiện tại
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
}
