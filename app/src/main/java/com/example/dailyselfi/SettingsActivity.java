package com.example.dailyselfi;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private Button btnSaveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        timePicker = findViewById(R.id.timePicker);
        btnSaveTime = findViewById(R.id.btnSaveTime);

        btnSaveTime.setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            saveReminderTime(hour, minute);
            setDailyReminder(hour, minute);

            Toast.makeText(this, "Nhắc nhở đã được lưu!", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveReminderTime(int hour, int minute) {
        SharedPreferences prefs = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("hour", hour);
        editor.putInt("minute", minute);
        editor.apply();
    }


    private void setDailyReminder(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        // Nếu thiết bị chạy Android 6.0 (API 23) trở lên, sử dụng setExactAndAllowWhileIdle để alarm chính xác

        try {
            // Kiểm tra quyền trên Android 12+ (API 31)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    // Hiển thị thông báo nếu không có quyền
                    Toast.makeText(this, "Ứng dụng không có quyền đặt báo thức chính xác.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Đối với Android < 12
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        } catch (SecurityException e) {
            Log.e("AlarmError", "Không thể đặt báo thức chính xác: " + e.getMessage());
        }

//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}