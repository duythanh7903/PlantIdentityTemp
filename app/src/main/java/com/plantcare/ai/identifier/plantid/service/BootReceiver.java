package com.plantcare.ai.identifier.plantid.service;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static com.plantcare.ai.identifier.plantid.service.AlarmReceiver.setReminderAlarms;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.plantcare.ai.identifier.plantid.data.database.AppDatabase;
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Re-schedules all stored alarms. This is necessary as {@link AlarmManager} does not persist alarms
 * between reboots.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Executors.newSingleThreadExecutor().execute(() -> {
                final List<AlarmEntity> entities = AppDatabase.Companion.getInstance(context).alarmDao().getAllRecordAlarmTypeList();
                setReminderAlarms(context, entities);
            });
        }
    }

}
