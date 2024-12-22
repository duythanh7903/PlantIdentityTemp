package com.plantcare.ai.identifier.plantid.service.utils;

import android.Manifest;
import android.util.SparseBooleanArray;

import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity;

public final class AlarmUtils {

    private static final int REQUEST_ALARM = 1;
    private static final String[] PERMISSIONS_ALARM = {
            Manifest.permission.VIBRATE
    };

    private AlarmUtils() { throw new AssertionError(); }

    public static boolean isAlarmActive(AlarmEntity alarm) {

        final SparseBooleanArray days = alarm.getDays();

        boolean isActive = false;
        int count = 0;

        while (count < days.size() && !isActive) {
            isActive = days.valueAt(count);
            count++;
        }

        return isActive;

    }

}
