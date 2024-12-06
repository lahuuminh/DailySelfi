package com.example.dailyselfi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        boolean isPhotoTaken = prefs.getBoolean("photoTaken_" + today, false);

        if (!isPhotoTaken) {
            Toast.makeText(context, "Đã đến giờ nhắc nhở! Hãy chụp ảnh!", Toast.LENGTH_LONG).show();
        }
    }
}