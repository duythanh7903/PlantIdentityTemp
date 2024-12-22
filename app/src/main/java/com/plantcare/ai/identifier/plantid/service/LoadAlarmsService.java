package com.plantcare.ai.identifier.plantid.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.plantcare.ai.identifier.plantid.data.database.AppDatabase;
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity;

import java.util.ArrayList;
import java.util.List;

public final class LoadAlarmsService extends IntentService {

    private static final String TAG = LoadAlarmsService.class.getSimpleName();
    public static final String ACTION_COMPLETE = TAG + ".ACTION_COMPLETE";
    public static final String ALARMS_EXTRA = "alarms_extra";

    @SuppressWarnings("unused")
    public LoadAlarmsService() {
        this(TAG);
    }

    public LoadAlarmsService(String name){
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final List<AlarmEntity> entities = AppDatabase.Companion.getInstance(this).alarmDao().getAllRecordAlarmTypeList();
        final Intent i = new Intent(ACTION_COMPLETE);
        i.putParcelableArrayListExtra(ALARMS_EXTRA, new ArrayList<>(entities));
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

    }

    public static void launchLoadAlarmsService(Context context) {
        final Intent launchLoadAlarmsServiceIntent = new Intent(context, LoadAlarmsService.class);
        context.startService(launchLoadAlarmsServiceIntent);
    }

}
